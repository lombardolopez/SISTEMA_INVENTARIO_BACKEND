package com.inventario.backend.repository;

import com.inventario.backend.model.Role;
import com.inventario.backend.model.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);

  Page<User> findByRole(Role role, Pageable pageable);

  long countByRole(Role role);
}
