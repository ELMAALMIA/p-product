package com.eTrust.product.service.impl;

import com.eTrust.product.dto.request.ProductRequest;
import com.eTrust.product.dto.response.ProductResponse;
import com.eTrust.product.entity.ProductEntity;
import com.eTrust.product.event.ProductCreatedEvent;
import com.eTrust.product.event.ProductDeletedEvent;
import com.eTrust.product.exception.ResourceNotFoundException;
import com.eTrust.product.mapper.ProductMapper;
import com.eTrust.product.entity.InventoryStatus;
import com.eTrust.product.repository.ProductRepository;
import com.eTrust.product.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
@Primary
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository repository;
    private final ProductMapper mapper;
    private final ApplicationEventPublisher eventPublisher;

    public ProductServiceImpl(ProductRepository repository,
                              ProductMapper mapper,
                              ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.mapper = mapper;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public ProductResponse create(ProductRequest request) {
        ensureCodeUnique(request.code(), null);
        ProductEntity entity = mapper.toEntity(request);
        entity.setInternalReference(generateReference());
        ProductEntity saved = repository.save(entity);
        eventPublisher.publishEvent(new ProductCreatedEvent(this, saved));
        log.info("Product created id={}", saved.getId());
        return mapper.toResponse(saved);
    }

    @Override
    public ProductResponse findById(Long id) {
        return mapper.toResponse(getOrThrow(id));
    }

    @Override
    public List<ProductResponse> findAll(InventoryStatus status, String category) {
        return repository.findByFilters(status, category)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        ProductEntity existing = getOrThrow(id);
        if (!existing.getCode().equals(request.code())) {
            ensureCodeUnique(request.code(), id);
        }
        mapper.updateEntityFromRequest(request, existing);
        ProductEntity updated = repository.save(existing);
        log.info("Product updated id={}", id);
        return mapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) throw new ResourceNotFoundException("Product", id);
        repository.deleteById(id);
        eventPublisher.publishEvent(new ProductDeletedEvent(this, id));
        log.info("Product deleted id={}", id);
    }

    @Override
    @Transactional
    public ProductResponse createWithFile(ProductRequest request, MultipartFile image) throws IOException {
        validateImageFile(image);
        ensureCodeUnique(request.code(), null);
        String base64 = "data:" + image.getContentType() + ";base64,"
                + Base64.getEncoder().encodeToString(image.getBytes());
        ProductEntity entity = mapper.toEntity(request);
        entity.setImage(base64);
        entity.setInternalReference(generateReference());
        ProductEntity saved = repository.save(entity);
        eventPublisher.publishEvent(new ProductCreatedEvent(this, saved));
        return mapper.toResponse(saved);
    }

    private String generateReference() {
        return "REF-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }

    private void validateImageFile(MultipartFile image) {
        if (image == null || image.isEmpty())
            throw new IllegalArgumentException("Image file is required");
        if (image.getSize() > 100 * 1024L)
            throw new IllegalArgumentException("Image must be less than 100KB");
    }

    private ProductEntity getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    private void ensureCodeUnique(String code, Long excludeId) {
        repository.findByCode(code).ifPresent(existing -> {
            if (excludeId == null || !existing.getId().equals(excludeId)) {
                throw new IllegalArgumentException("Product code '" + code + "' already exists");
            }
        });
    }
}