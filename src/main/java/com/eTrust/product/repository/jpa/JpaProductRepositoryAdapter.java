package com.eTrust.product.repository.jpa;

import com.eTrust.product.mapper.ProductEntityMapper;
import com.eTrust.product.model.InventoryStatus;
import com.eTrust.product.model.Product;
import com.eTrust.product.repository.ProductRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter pattern — bridges domain ProductRepository with Spring Data JPA.
 */
@Repository
public class JpaProductRepositoryAdapter implements ProductRepository {

    private final SpringJpaProductRepository jpa;
    private final ProductEntityMapper mapper;

    public JpaProductRepositoryAdapter(SpringJpaProductRepository jpa,
                                       ProductEntityMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public Product save(Product product) {
        return mapper.toDomain(jpa.save(mapper.toEntity(product)));
    }

    @Override
    public Optional<Product> findById(Long id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Product> findByCode(String code) {
        return jpa.findByCode(code).map(mapper::toDomain);
    }

    @Override
    public boolean existsById(Long id) {
        return jpa.existsById(id);
    }

    @Override
    public boolean existsByCode(String code) {
        return jpa.existsByCode(code);
    }

    @Override
    public List<Product> findAll() {
        return jpa.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Product> findByFilters(InventoryStatus status, String category) {
        return jpa.findByFilters(status, category).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }
}
