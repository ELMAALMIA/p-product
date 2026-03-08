package com.eTrust.product.unit.repository;

import com.eTrust.product.entity.InventoryStatus;
import com.eTrust.product.entity.ProductEntity;
import com.eTrust.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.eTrust.product.config.JpaAuditingConfig;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaAuditingConfig.class)
@ActiveProfiles("test")
class ProductRepositoryTest {


    @Autowired private ProductRepository repository;


    @BeforeEach
    void setUp() {
        repository.deleteAll();

        ProductEntity p1 = build("PROD001", "iPhone 15", "Electronics", InventoryStatus.INSTOCK);
        ProductEntity p2 = build("PROD002", "MacBook", "Electronics", InventoryStatus.LOWSTOCK);
        ProductEntity p3 = build("PROD003", "Nike Shoes", "Footwear", InventoryStatus.OUTOFSTOCK);

        repository.saveAll(List.of(p1, p2, p3));
    }

    @Test
    @DisplayName("findByCode() — should return product when code exists")
    void findByCode_exists() {
        Optional<ProductEntity> result = repository.findByCode("PROD001");
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("iPhone 15");
    }

    @Test
    @DisplayName("findByCode() — should return empty when code not found")
    void findByCode_notFound() {
        Optional<ProductEntity> result = repository.findByCode("UNKNOWN");
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("existsByCode() — should return true when code exists")
    void existsByCode_true() {
        assertThat(repository.existsByCode("PROD001")).isTrue();
    }

    @Test
    @DisplayName("existsByCode() — should return false when code not found")
    void existsByCode_false() {
        assertThat(repository.existsByCode("UNKNOWN")).isFalse();
    }

    @Test
    @DisplayName("findByFilters() — should return all when no filters")
    void findByFilters_noFilters() {
        List<ProductEntity> result = repository.findByFilters(null, null);
        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("findByFilters() — should filter by status")
    void findByFilters_byStatus() {
        List<ProductEntity> result = repository.findByFilters(InventoryStatus.INSTOCK, null);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("PROD001");
    }

    @Test
    @DisplayName("findByFilters() — should filter by category")
    void findByFilters_byCategory() {
        List<ProductEntity> result = repository.findByFilters(null, "Electronics");
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("findByFilters() — should filter by both status and category")
    void findByFilters_byStatusAndCategory() {
        List<ProductEntity> result = repository.findByFilters(InventoryStatus.LOWSTOCK, "Electronics");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("PROD002");
    }

    @Test
    @DisplayName("save() — should persist createdAt and updatedAt")
    void save_auditing() {
        ProductEntity p = build("PROD999", "Test", "Test", InventoryStatus.INSTOCK);
        ProductEntity saved = repository.save(p);

        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }


    private ProductEntity build(String code, String name, String category, InventoryStatus status) {
        ProductEntity e = new ProductEntity();
        e.setCode(code);
        e.setName(name);
        e.setImage("data:image/png;base64,abc");
        e.setCategory(category);
        e.setPrice(100.0);
        e.setQuantity(10);
        e.setInternalReference("REF-" + code);
        e.setInventoryStatus(status);
        e.setRating(0.0);
        return e;
    }
}