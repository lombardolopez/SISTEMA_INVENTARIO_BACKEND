package com.inventario.backend.repository;

import com.inventario.backend.model.Category;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {

  boolean existsByName(String name);

  boolean existsByNameAndIdNot(String name, String id);

  Optional<Category> findByName(String name);
}
