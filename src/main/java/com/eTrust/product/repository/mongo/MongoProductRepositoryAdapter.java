package com.eTrust.product.repository.mongo;

import com.eTrust.product.mapper.ProductDocumentMapper;
import com.eTrust.product.enums.InventoryStatus;
import com.eTrust.product.model.Product;
import com.eTrust.product.repository.ProductRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter pattern — bridges domain ProductRepository with Spring Data MongoDB.
 * Active only when profile = "mongo".
 */
@Repository
@Profile("mongo")
public class MongoProductRepositoryAdapter implements ProductRepository {

    private final SpringMongoProductRepository mongo;
    private final ProductDocumentMapper mapper;

    public MongoProductRepositoryAdapter(SpringMongoProductRepository mongo,
                                          ProductDocumentMapper mapper) {
        this.mongo = mongo;
        this.mapper = mapper;
    }

    @Override
    public Product save(Product product) {
        return mapper.toDomain(mongo.save(mapper.toDocument(product)));
    }

    @Override
    public Optional<Product> findById(Long id) {
        return mongo.findById(String.valueOf(id)).map(mapper::toDomain);
    }

    @Override
    public Optional<Product> findByCode(String code) {
        return mongo.findByCode(code).map(mapper::toDomain);
    }

    @Override
    public boolean existsById(Long id) {
        return mongo.existsById(String.valueOf(id));
    }

    @Override
    public boolean existsByCode(String code) {
        return mongo.existsByCode(code);
    }

    @Override
    public List<Product> findAll() {
        return mongo.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Product> findByFilters(InventoryStatus status, String category) {
        return mongo.findByFilters(status, category).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        mongo.deleteById(String.valueOf(id));
    }
}
