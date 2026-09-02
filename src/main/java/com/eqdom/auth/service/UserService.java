package com.eqdom.auth.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eqdom.auth.dto.CreateUserRequest;
import com.eqdom.auth.dto.UserResponse;
import com.eqdom.auth.entity.Role;
import com.eqdom.auth.entity.RoleName;
import com.eqdom.auth.entity.User;
import com.eqdom.auth.exception.DuplicateResourceException;
import com.eqdom.auth.exception.InvalidRequestException;
import com.eqdom.auth.exception.ResourceNotFoundException;
import com.eqdom.auth.mapper.UserMapper;
import com.eqdom.auth.repository.RoleRepository;
import com.eqdom.auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already taken: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }
        if (request.getCin() != null && userRepository.existsByCin(request.getCin())) {
            throw new DuplicateResourceException("CIN already registered: " + request.getCin());
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .cin(request.getCin())
                .enabled(true)
                .roles(resolveRoles(request.getRoles()))
                .build();

        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> listUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Long id) {
        return userMapper.toResponse(findUserOrThrow(id));
    }

    @Transactional
    public UserResponse updateRoles(Long id, Set<RoleName> roleNames) {
        User user = findUserOrThrow(id);
        boolean isCurrentlyAdmin = user.isEnabled() && hasRole(user, RoleName.ADMIN);
        boolean willRemainAdmin = roleNames.contains(RoleName.ADMIN);
        if (isCurrentlyAdmin && !willRemainAdmin) {
            requireNotLastAdmin();
        }
        user.setRoles(resolveRoles(roleNames));
        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateStatus(Long id, boolean enabled) {
        User user = findUserOrThrow(id);
        boolean isCurrentlyEnabledAdmin = user.isEnabled() && hasRole(user, RoleName.ADMIN);
        if (!enabled && isCurrentlyEnabledAdmin) {
            requireNotLastAdmin();
        }
        user.setEnabled(enabled);
        return userMapper.toResponse(userRepository.save(user));
    }

    private void requireNotLastAdmin() {
        if (userRepository.countByEnabledTrueAndRoles_Name(RoleName.ADMIN) <= 1) {
            throw new InvalidRequestException("Cannot remove the last administrator account.");
        }
    }

    private boolean hasRole(User user, RoleName roleName) {
        return user.getRoles().stream().anyMatch(role -> role.getName() == roleName);
    }

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    private Set<Role> resolveRoles(Set<RoleName> roleNames) {
        return roleNames.stream()
                .map(name -> roleRepository.findByName(name)
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + name)))
                .collect(Collectors.toSet());
    }
}
