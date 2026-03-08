package com.eTrust.product.service.impl;

import com.eTrust.product.dto.request.ProductRequest;
import com.eTrust.product.dto.response.ProductResponse;
import com.eTrust.product.event.ProductCreatedEvent;
import com.eTrust.product.event.ProductDeletedEvent;
import com.eTrust.product.exception.ResourceNotFoundException;
import com.eTrust.product.factory.ProductFactory;
import com.eTrust.product.mapper.ProductMapper;
import com.eTrust.product.model.InventoryStatus;
import com.eTrust.product.model.Product;
import com.eTrust.product.repository.ProductRepository;
import com.eTrust.product.service.ProductService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Core business logic — orchestrates factory → repository → mapper → events.
 * Does NOT handle validation (delegated to ValidatedProductService decorator).
 */
@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository repository;
    private final ProductMapper mapper;
    private final ProductFactory factory;
    private final ApplicationEventPublisher eventPublisher;

    public ProductServiceImpl(ProductRepository repository,
                               ProductMapper mapper,
                               ProductFactory factory,
                               ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.mapper = mapper;
        this.factory = factory;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public ProductResponse create(ProductRequest request) {
        ensureCodeUnique(request.code(), null);
        Product saved = repository.save(factory.createFrom(request));
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
        Product existing = getOrThrow(id);
        if (!existing.getCode().equals(request.code())) {
            ensureCodeUnique(request.code(), id);
        }
        mapper.updateDomainFromRequest(request, existing);
        existing.setUpdatedAt(Instant.now().getEpochSecond());
        Product updated = repository.save(existing);
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
        String base64 = "data:" + image.getContentType() + ";base64,"
                + Base64.getEncoder().encodeToString(image.getBytes());
        Product saved = repository.save(factory.createFrom(request, base64));
        eventPublisher.publishEvent(new ProductCreatedEvent(this, saved));
        return mapper.toResponse(saved);
    }

    private Product getOrThrow(Long id) {
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
