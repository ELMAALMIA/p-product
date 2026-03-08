package com.eTrust.product.unit.service;

import com.eTrust.product.dto.request.ProductRequest;
import com.eTrust.product.dto.response.ProductResponse;
import com.eTrust.product.entity.InventoryStatus;
import com.eTrust.product.entity.ProductEntity;
import com.eTrust.product.exception.ResourceNotFoundException;
import com.eTrust.product.mapper.ProductMapper;
import com.eTrust.product.repository.ProductRepository;
import com.eTrust.product.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock private ProductRepository repository;
    @Mock private ProductMapper mapper;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ProductServiceImpl service;

    private ProductEntity entity;
    private ProductRequest request;
    private ProductResponse response;

    @BeforeEach
    void setUp() {
        entity = new ProductEntity();
        entity.setId(1L);
        entity.setCode("PROD001");
        entity.setName("iPhone 15 Pro");
        entity.setDescription("Latest Apple smartphone");
        entity.setImage("data:image/png;base64,abc");
        entity.setCategory("Electronics");
        entity.setPrice(999.99);
        entity.setQuantity(10);
        entity.setInternalReference("REF-ABC12345");
        entity.setShellId(1L);
        entity.setInventoryStatus(InventoryStatus.INSTOCK);
        entity.setRating(4.5);

        request = new ProductRequest(
                "PROD001", "iPhone 15 Pro", "Latest Apple smartphone",
                "data:image/png;base64,abc", "Electronics",
                999.99, 10, 1L, InventoryStatus.INSTOCK, 4.5
        );

        response = new ProductResponse(
                1L, "PROD001", "iPhone 15 Pro", "Latest Apple smartphone",
                "data:image/png;base64,abc", "Electronics",
                999.99, 10, "REF-ABC12345", 1L,
                InventoryStatus.INSTOCK, 4.5,
                Instant.now().toEpochMilli(), Instant.now().toEpochMilli()
        );
    }

    @Test
    @DisplayName("create() : should save product and return response")
    void create_success() {
        when(repository.findByCode("PROD001")).thenReturn(Optional.empty());
        when(mapper.toEntity(request)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        ProductResponse result = service.create(request);

        assertThat(result).isNotNull();
        assertThat(result.code()).isEqualTo("PROD001");
        verify(repository).save(entity);
        verify(eventPublisher).publishEvent(any());
    }

    @Test
    @DisplayName("create() : should throw when code already exists")
    void create_duplicateCode_throws() {
        when(repository.findByCode("PROD001")).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("create() : should auto-generate internalReference")
    void create_generatesInternalReference() {
        when(repository.findByCode(anyString())).thenReturn(Optional.empty());
        when(mapper.toEntity(request)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        service.create(request);

        assertThat(entity.getInternalReference()).matches("REF-[A-Z0-9]{8}");
    }


    @Test
    @DisplayName("findById() : should return product when found")
    void findById_success() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        ProductResponse result = service.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("findById() : should throw when not found")
    void findById_notFound_throws() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("findAll() : should return list filtered by status")
    void findAll_withFilters() {
        when(repository.findByFilters(InventoryStatus.INSTOCK, null))
                .thenReturn(List.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        List<ProductResponse> result = service.findAll(InventoryStatus.INSTOCK, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).inventoryStatus()).isEqualTo(InventoryStatus.INSTOCK);
    }

    @Test
    @DisplayName("findAll() : should return empty list when no products")
    void findAll_empty() {
        when(repository.findByFilters(null, null)).thenReturn(List.of());

        List<ProductResponse> result = service.findAll(null, null);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("update() : should update and return response")
    void update_success() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        ProductResponse result = service.update(1L, request);

        assertThat(result).isNotNull();
        verify(mapper).updateEntityFromRequest(request, entity);
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("update() : should throw when product not found")
    void update_notFound_throws() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99L, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("update() : should throw when new code conflicts with another product")
    void update_codeConflict_throws() {
        ProductEntity other = new ProductEntity();
        other.setId(2L);
        other.setCode("PROD999");

        ProductRequest newRequest = new ProductRequest(
                "PROD999", "New Name", null,
                "data:image/png;base64,abc", "Electronics",
                99.0, 5, null, InventoryStatus.INSTOCK, 0.0
        );

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(repository.findByCode("PROD999")).thenReturn(Optional.of(other));

        assertThatThrownBy(() -> service.update(1L, newRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }


    @Test
    @DisplayName("delete() : should delete existing product")
    void delete_success() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).deleteById(1L);
        verify(eventPublisher).publishEvent(any());
    }

    @Test
    @DisplayName("delete() : should throw when product not found")
    void delete_notFound_throws() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(repository, never()).deleteById(any());
    }
}