package com.inventario.backend.service;

import com.inventario.backend.dto.request.CreateUserRequest;
import com.inventario.backend.dto.request.UpdateUserRequest;
import com.inventario.backend.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

  Page<UserResponse> findAll(Pageable pageable);

  UserResponse findById(String id);

  UserResponse create(CreateUserRequest request);

  UserResponse update(String id, UpdateUserRequest request);

  UserResponse toggleActive(String id);

  void delete(String id, String currentUserEmail);
}
