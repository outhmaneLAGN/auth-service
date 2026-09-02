package com.eqdom.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.eqdom.auth.dto.LoginRequest;
import com.eqdom.auth.entity.User;
import com.eqdom.auth.exception.InvalidCredentialsException;
import com.eqdom.auth.mapper.UserMapper;
import com.eqdom.auth.repository.RefreshTokenRepository;
import com.eqdom.auth.repository.RoleRepository;
import com.eqdom.auth.repository.UserRepository;
import com.eqdom.auth.security.JwtService;

class AuthServiceTest {

    private UserRepository userRepository;
    private AuthenticationManager authenticationManager;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        authenticationManager = mock(AuthenticationManager.class);
        JwtService jwtService = mock(JwtService.class);
        UserMapper userMapper = mock(UserMapper.class);

        authService = new AuthService(userRepository, roleRepository, refreshTokenRepository, passwordEncoder,
                authenticationManager, jwtService, userMapper);

        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void fifthFailedAttemptLocksTheAccount() {
        User user = User.builder().id(1L).username("demo").email("demo@eqdom.ma").password("hash")
                .enabled(true).failedLoginAttempts(4).build();
        when(userRepository.findByUsernameOrEmail("demo", "demo")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad"));

        LoginRequest request = new LoginRequest("demo", "wrong-password");

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertThat(saved.getFailedLoginAttempts()).isZero();
        assertThat(saved.getLockedUntil()).isNotNull();
        assertThat(saved.getLockedUntil()).isAfter(LocalDateTime.now());
    }

    @Test
    void lockedAccountIsRejectedEvenWithCorrectPassword() {
        User user = User.builder().id(1L).username("demo").email("demo@eqdom.ma").password("hash")
                .enabled(true).failedLoginAttempts(0).lockedUntil(LocalDateTime.now().plusMinutes(10)).build();
        when(userRepository.findByUsernameOrEmail("demo", "demo")).thenReturn(Optional.of(user));

        LoginRequest request = new LoginRequest("demo", "correct-password");

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("locked");

        verify(authenticationManager, never()).authenticate(any());
    }
}
