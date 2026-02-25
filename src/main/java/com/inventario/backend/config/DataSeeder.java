package com.inventario.backend.config;

import com.inventario.backend.model.Category;
import com.inventario.backend.model.Product;
import com.inventario.backend.model.Role;
import com.inventario.backend.model.Unit;
import com.inventario.backend.model.User;
import com.inventario.backend.repository.CategoryRepository;
import com.inventario.backend.repository.ProductRepository;
import com.inventario.backend.repository.UserRepository;
import com.inventario.backend.service.StockAlertService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

  private final UserRepository userRepository;
  private final CategoryRepository categoryRepository;
  private final ProductRepository productRepository;
  private final StockAlertService stockAlertService;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(ApplicationArguments args) {
    if (userRepository.count() > 0) {
      log.info("DataSeeder: la base de datos ya tiene datos, omitiendo seed.");
      return;
    }

    log.info("DataSeeder: insertando datos iniciales...");

    // ── Usuarios ────────────────────────────────────────────────────────
    User admin =
        userRepository.save(
            User.builder()
                .name("Admin Principal")
                .email("admin@carpentry.com")
                .passwordHash(passwordEncoder.encode("Admin1234!"))
                .role(Role.ADMIN)
                .isActive(true)
                .build());

    userRepository.save(
        User.builder()
            .name("Carlos Mendez")
            .email("carlos@carpentry.com")
            .passwordHash(passwordEncoder.encode("Warehouse1234!"))
            .role(Role.WAREHOUSE_MANAGER)
            .isActive(true)
            .build());

    userRepository.save(
        User.builder()
            .name("Ana Viewer")
            .email("ana@carpentry.com")
            .passwordHash(passwordEncoder.encode("Viewer1234!"))
            .role(Role.VIEWER)
            .isActive(true)
            .build());

    log.info("DataSeeder: {} usuarios creados.", userRepository.count());

    // ── Categorías ──────────────────────────────────────────────────────
    Category lumber =
        categoryRepository.save(
            Category.builder()
                .name("Lumber & Boards")
                .description("Raw wood boards, plywood, MDF panels")
                .color("#8B5E3C")
                .build());

    Category hardware =
        categoryRepository.save(
            Category.builder()
                .name("Hardware & Fasteners")
                .description("Screws, nails, bolts, hinges and other hardware")
                .color("#6B7280")
                .build());

    Category finishes =
        categoryRepository.save(
            Category.builder()
                .name("Finishes & Coatings")
                .description("Paints, varnishes, stains and sealants")
                .color("#D97706")
                .build());

    Category tools =
        categoryRepository.save(
            Category.builder()
                .name("Tools & Equipment")
                .description("Hand tools, power tools and accessories")
                .color("#1D4ED8")
                .build());

    Category safety =
        categoryRepository.save(
            Category.builder()
                .name("Safety & PPE")
                .description("Personal protective equipment and safety gear")
                .color("#DC2626")
                .build());

    log.info("DataSeeder: {} categorías creadas.", categoryRepository.count());

    // ── Productos ───────────────────────────────────────────────────────
    List<Product> products =
        List.of(
            Product.builder()
                .name("Pine Board 2x4x8")
                .description("Standard pine lumber board, 8 feet length")
                .categoryId(lumber.getId())
                .sku("LUM-PIN-248")
                .unit(Unit.PIECE)
                .currentStock(120)
                .minimumStock(30)
                .unitPrice(5.50)
                .location("Aisle A-1")
                .build(),
            Product.builder()
                .name("Plywood Sheet 4x8 (3/4\")")
                .description("Standard plywood sheet, 3/4 inch thickness")
                .categoryId(lumber.getId())
                .sku("LUM-PLY-34")
                .unit(Unit.SHEET)
                .currentStock(8)
                .minimumStock(15)
                .unitPrice(42.00)
                .location("Aisle A-2")
                .build(),
            Product.builder()
                .name("Maple Hardwood 1x6x8")
                .description("Premium maple hardwood board")
                .categoryId(lumber.getId())
                .sku("LUM-MAP-168")
                .unit(Unit.PIECE)
                .currentStock(3)
                .minimumStock(12)
                .unitPrice(18.75)
                .location("Aisle A-3")
                .build(),
            Product.builder()
                .name("Wood Screws #8 x 2\" (Box 100)")
                .description("Coarse thread wood screws, zinc plated")
                .categoryId(hardware.getId())
                .sku("HRD-SCR-8X2")
                .unit(Unit.BOX)
                .currentStock(45)
                .minimumStock(10)
                .unitPrice(8.99)
                .location("Aisle B-1")
                .build(),
            Product.builder()
                .name("Concealed Hinge 35mm")
                .description("European style cabinet hinge, soft-close")
                .categoryId(hardware.getId())
                .sku("HRD-HNG-35SC")
                .unit(Unit.PIECE)
                .currentStock(200)
                .minimumStock(50)
                .unitPrice(2.50)
                .location("Aisle B-2")
                .build(),
            Product.builder()
                .name("Water-Based Varnish 1L")
                .description("Clear water-based wood varnish, satin finish")
                .categoryId(finishes.getId())
                .sku("FIN-VAR-1L")
                .unit(Unit.LITER)
                .currentStock(0)
                .minimumStock(10)
                .unitPrice(14.99)
                .location("Aisle C-1")
                .build(),
            Product.builder()
                .name("Wood Stain Dark Walnut 1L")
                .description("Oil-based dark walnut wood stain")
                .categoryId(finishes.getId())
                .sku("FIN-STN-DW1L")
                .unit(Unit.LITER)
                .currentStock(22)
                .minimumStock(8)
                .unitPrice(12.50)
                .location("Aisle C-2")
                .build(),
            Product.builder()
                .name("Safety Goggles (Clear Lens)")
                .description("Anti-fog safety goggles with clear polycarbonate lens")
                .categoryId(safety.getId())
                .sku("SAF-GOG-CLR")
                .unit(Unit.PIECE)
                .currentStock(15)
                .minimumStock(20)
                .unitPrice(6.75)
                .location("Aisle E-1")
                .build(),
            Product.builder()
                .name("Dust Mask N95 (Box 20)")
                .description("N95 particulate respirator masks")
                .categoryId(safety.getId())
                .sku("SAF-MSK-N95")
                .unit(Unit.BOX)
                .currentStock(4)
                .minimumStock(5)
                .unitPrice(19.99)
                .location("Aisle E-2")
                .build(),
            Product.builder()
                .name("Measuring Tape 25ft")
                .description("Heavy-duty measuring tape with magnetic tip")
                .categoryId(tools.getId())
                .sku("TLS-TAPE-25")
                .unit(Unit.PIECE)
                .currentStock(30)
                .minimumStock(10)
                .unitPrice(11.99)
                .location("Aisle D-1")
                .build());

    productRepository.saveAll(products);
    log.info("DataSeeder: {} productos creados.", productRepository.count());

    // ── Generar alertas iniciales ────────────────────────────────────────
    stockAlertService.generateAlerts();
    log.info("DataSeeder: alertas de stock iniciales generadas.");
    log.info(
        "DataSeeder: ✓ Seed completado. Credenciales admin → admin@carpentry.com / Admin1234!");
  }
}
