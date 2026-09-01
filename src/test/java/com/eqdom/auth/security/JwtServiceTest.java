package com.eqdom.auth.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.eqdom.auth.entity.Role;
import com.eqdom.auth.entity.RoleName;
import com.eqdom.auth.entity.User;

class JwtServiceTest {

    private static final String SECRET = "test-only-secret-key-that-is-at-least-32-bytes-long";

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, 3_600_000L, 604_800_000L);
    }

    @Test
    void accessTokenCarriesUsernameUserIdAndRoles() {
        User user = User.builder()
                .id(4L)
                .username("client1")
                .roles(Set.of(Role.builder().name(RoleName.CLIENT).build()))
                .build();

        String token = jwtService.generateAccessToken(user);

        assertThat(jwtService.isValid(token)).isTrue();
        assertThat(jwtService.getUsername(token)).isEqualTo("client1");
        assertThat(jwtService.getRoles(token)).containsExactly("CLIENT");
        assertThat(jwtService.parseClaims(token).get("userId", Number.class).longValue()).isEqualTo(4L);
    }

    @Test
    void tokenSignedWithADifferentSecretIsRejected() {
        User user = User.builder().id(1L).username("admin").roles(Set.of()).build();
        String token = jwtService.generateAccessToken(user);

        JwtService otherService = new JwtService("a-completely-different-secret-key-of-32-bytes!!", 3_600_000L, 604_800_000L);

        assertThat(otherService.isValid(token)).isFalse();
    }

    @Test
    void refreshTokenHashingIsDeterministicAndOneWay() {
        String raw = jwtService.generateRawRefreshToken();
        String hash1 = jwtService.hash(raw);
        String hash2 = jwtService.hash(raw);

        assertThat(hash1).isEqualTo(hash2);
        assertThat(hash1).isNotEqualTo(raw);
    }
}
