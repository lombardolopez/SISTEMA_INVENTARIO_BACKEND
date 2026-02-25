package com.inventario.backend.controller;

import com.inventario.backend.dto.request.CreateUserRequest;
import com.inventario.backend.dto.request.UpdateUserRequest;
import com.inventario.backend.dto.response.ApiResponse;
import com.inventario.backend.dto.response.UserResponse;
import com.inventario.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping
  public ResponseEntity<ApiResponse<Page<UserResponse>>> findAll(
      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable) {
    return ResponseEntity.ok(ApiResponse.ok(userService.findAll(pageable)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<UserResponse>> findById(@PathVariable String id) {
    return ResponseEntity.ok(ApiResponse.ok(userService.findById(id)));
  }

  @PostMapping
  public ResponseEntity<ApiResponse<UserResponse>> create(
      @Valid @RequestBody CreateUserRequest request) {
    UserResponse created = userService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.ok("Usuario creado exitosamente", created));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<UserResponse>> update(
      @PathVariable String id, @Valid @RequestBody UpdateUserRequest request) {
    return ResponseEntity.ok(
        ApiResponse.ok("Usuario actualizado exitosamente", userService.update(id, request)));
  }

  @PatchMapping("/{id}/toggle-active")
  public ResponseEntity<ApiResponse<UserResponse>> toggleActive(@PathVariable String id) {
    return ResponseEntity.ok(
        ApiResponse.ok("Estado del usuario actualizado", userService.toggleActive(id)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(
      @PathVariable String id, Authentication authentication) {
    userService.delete(id, authentication.getName());
    return ResponseEntity.ok(ApiResponse.ok("Usuario eliminado exitosamente", null));
  }
}
