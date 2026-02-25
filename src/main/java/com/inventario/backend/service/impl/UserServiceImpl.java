package com.inventario.backend.service.impl;

import com.inventario.backend.dto.request.CreateUserRequest;
import com.inventario.backend.dto.request.UpdateUserRequest;
import com.inventario.backend.dto.response.UserResponse;
import com.inventario.backend.exception.BusinessException;
import com.inventario.backend.exception.ResourceNotFoundException;
import com.inventario.backend.model.User;
import com.inventario.backend.repository.UserRepository;
import com.inventario.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public Page<UserResponse> findAll(Pageable pageable) {
    return userRepository.findAll(pageable).map(UserResponse::from);
  }

  @Override
  public UserResponse findById(String id) {
    return UserResponse.from(findUserById(id));
  }

  @Override
  public UserResponse create(CreateUserRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new BusinessException(409, "Ya existe un usuario con ese email");
    }

    User user =
        User.builder()
            .name(request.getName())
            .email(request.getEmail())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .role(request.getRole())
            .isActive(true)
            .build();

    return UserResponse.from(userRepository.save(user));
  }

  @Override
  public UserResponse update(String id, UpdateUserRequest request) {
    User user = findUserById(id);

    if (!user.getEmail().equals(request.getEmail())
        && userRepository.existsByEmail(request.getEmail())) {
      throw new BusinessException(409, "Ya existe un usuario con ese email");
    }

    user.setName(request.getName());
    user.setEmail(request.getEmail());
    user.setRole(request.getRole());

    if (request.getPassword() != null && !request.getPassword().isBlank()) {
      user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
    }

    return UserResponse.from(userRepository.save(user));
  }

  @Override
  public UserResponse toggleActive(String id) {
    User user = findUserById(id);
    user.setIsActive(!Boolean.TRUE.equals(user.getIsActive()));
    return UserResponse.from(userRepository.save(user));
  }

  @Override
  public void delete(String id, String currentUserEmail) {
    User user = findUserById(id);

    if (user.getEmail().equals(currentUserEmail)) {
      throw new BusinessException("No puedes eliminar tu propio usuario");
    }

    userRepository.delete(user);
  }

  private User findUserById(String id) {
    return userRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
  }
}
