package com.eTrust.product.repository.mongo;

import com.eTrust.product.dao.ProductDocument;
import com.eTrust.product.enums.InventoryStatus;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

@Profile("mongo")
public interface SpringMongoProductRepository extends MongoRepository<ProductDocument, String> {

    Optional<ProductDocument> findByCode(String code);

    boolean existsByCode(String code);

    @Query("{ $and: [ { $or: [ { inventory_status: ?0 }, { ?0: null } ] }, { $or: [ { category: { $regex: ?1, $options: 'i' } }, { ?1: null } ] } ] }")
    List<ProductDocument> findByFilters(InventoryStatus status, String category);
}
