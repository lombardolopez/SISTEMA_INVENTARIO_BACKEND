package com.inventario.backend.service.impl;

import com.inventario.backend.dto.request.CreateMovementRequest;
import com.inventario.backend.dto.response.MovementResponse;
import com.inventario.backend.dto.response.PagedResponse;
import com.inventario.backend.exception.BusinessException;
import com.inventario.backend.exception.ResourceNotFoundException;
import com.inventario.backend.model.Movement;
import com.inventario.backend.model.MovementType;
import com.inventario.backend.model.Product;
import com.inventario.backend.model.User;
import com.inventario.backend.repository.MovementRepository;
import com.inventario.backend.repository.ProductRepository;
import com.inventario.backend.repository.UserRepository;
import com.inventario.backend.service.MovementService;
import com.inventario.backend.service.StockAlertService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovementServiceImpl implements MovementService {

  private final MovementRepository movementRepository;
  private final ProductRepository productRepository;
  private final UserRepository userRepository;
  private final StockAlertService stockAlertService;
  private final MongoTemplate mongoTemplate;

  @Override
  public PagedResponse<MovementResponse> findAll(
      MovementType type, LocalDateTime from, LocalDateTime to, Pageable pageable) {

    Page<Movement> page;
    boolean hasType = type != null;
    boolean hasRange = from != null && to != null;

    if (hasType && hasRange) {
      page = movementRepository.findByTypeAndCreatedAtBetween(type, from, to, pageable);
    } else if (hasType) {
      page = movementRepository.findByType(type, pageable);
    } else if (hasRange) {
      page = movementRepository.findByCreatedAtBetween(from, to, pageable);
    } else {
      page = movementRepository.findAll(pageable);
    }

    return PagedResponse.of(page.map(MovementResponse::from));
  }

  @Override
  public MovementResponse findById(String id) {
    Movement movement =
        movementRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Movimiento", id));
    return MovementResponse.from(movement);
  }

  @Override
  public PagedResponse<MovementResponse> findByProduct(String productId, Pageable pageable) {
    if (!productRepository.existsById(productId)) {
      throw new ResourceNotFoundException("Producto", productId);
    }
    Page<Movement> page = movementRepository.findByProductId(productId, pageable);
    return PagedResponse.of(page.map(MovementResponse::from));
  }

  @Override
  public MovementResponse create(CreateMovementRequest request, String performedByEmail) {

    // 1. Obtener producto
    Product product =
        productRepository
            .findById(request.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException("Producto", request.getProductId()));

    // 2. Validar stock suficiente para salidas
    if (request.getType() == MovementType.EXIT) {
      if (product.getCurrentStock() < request.getQuantity()) {
        throw new BusinessException(
            String.format(
                "Stock insuficiente. Stock actual: %d, cantidad solicitada: %d",
                product.getCurrentStock(), request.getQuantity()));
      }
    }

    // 3. Obtener usuario que realiza el movimiento (denormalización)
    User user =
        userRepository
            .findByEmail(performedByEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario", performedByEmail));

    // 4. Guardar el movimiento
    Movement movement =
        Movement.builder()
            .productId(product.getId())
            .productName(product.getName())
            .type(request.getType())
            .quantity(request.getQuantity())
            .reason(request.getReason())
            .notes(request.getNotes())
            .performedBy(user.getId())
            .performedByName(user.getName())
            .build();

    movement = movementRepository.save(movement);

    // 5. Actualizar stock atómicamente con $inc — evita condiciones de carrera
    int delta =
        request.getType() == MovementType.ENTRY ? request.getQuantity() : -request.getQuantity();

    Update stockUpdate =
        new Update().inc("currentStock", delta).set("updatedAt", LocalDateTime.now());

    mongoTemplate.updateFirst(
        Query.query(Criteria.where("_id").is(product.getId())), stockUpdate, Product.class);

    // 6. Actualizar alerta de stock para este producto
    int updatedStock = product.getCurrentStock() + delta;
    stockAlertService.updateAlertForProduct(
        product.getId(), product.getName(), updatedStock, product.getMinimumStock());

    return MovementResponse.from(movement);
  }
}
