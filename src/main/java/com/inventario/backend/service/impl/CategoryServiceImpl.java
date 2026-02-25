package com.inventario.backend.service.impl;

import com.inventario.backend.dto.request.CreateCategoryRequest;
import com.inventario.backend.dto.request.UpdateCategoryRequest;
import com.inventario.backend.dto.response.CategoryResponse;
import com.inventario.backend.exception.BusinessException;
import com.inventario.backend.exception.ResourceNotFoundException;
import com.inventario.backend.model.Category;
import com.inventario.backend.repository.CategoryRepository;
import com.inventario.backend.repository.ProductRepository;
import com.inventario.backend.service.CategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

  private final CategoryRepository categoryRepository;
  private final ProductRepository productRepository;

  @Override
  public List<CategoryResponse> findAll() {
    return categoryRepository.findAll().stream()
        .map(cat -> CategoryResponse.from(cat, productRepository.countByCategoryId(cat.getId())))
        .toList();
  }

  @Override
  public CategoryResponse findById(String id) {
    Category category = findCategoryById(id);
    return CategoryResponse.from(category, productRepository.countByCategoryId(id));
  }

  @Override
  public CategoryResponse create(CreateCategoryRequest request) {
    if (categoryRepository.existsByName(request.getName())) {
      throw new BusinessException(409, "Ya existe una categoría con ese nombre");
    }

    Category category =
        Category.builder()
            .name(request.getName())
            .description(request.getDescription())
            .color(request.getColor())
            .build();

    return CategoryResponse.from(categoryRepository.save(category), 0);
  }

  @Override
  public CategoryResponse update(String id, UpdateCategoryRequest request) {
    Category category = findCategoryById(id);

    if (!category.getName().equals(request.getName())
        && categoryRepository.existsByName(request.getName())) {
      throw new BusinessException(409, "Ya existe una categoría con ese nombre");
    }

    category.setName(request.getName());
    category.setDescription(request.getDescription());
    category.setColor(request.getColor());

    long count = productRepository.countByCategoryId(id);
    return CategoryResponse.from(categoryRepository.save(category), count);
  }

  @Override
  public void delete(String id) {
    findCategoryById(id);

    if (productRepository.countByCategoryId(id) > 0) {
      throw new BusinessException(
          "No se puede eliminar la categoría porque tiene productos asociados");
    }

    categoryRepository.deleteById(id);
  }

  private Category findCategoryById(String id) {
    return categoryRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Categoría", id));
  }
}
