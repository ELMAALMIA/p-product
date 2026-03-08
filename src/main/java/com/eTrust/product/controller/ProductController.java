package com.eTrust.product.controller;

import com.eTrust.product.dto.request.ProductRequest;
import com.eTrust.product.dto.response.ProductResponse;
import com.eTrust.product.entity.InventoryStatus;
import com.eTrust.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.media.Content;


import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Products", description = "Product management endpoints")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Create a product (image as Base64 in JSON)")
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(request));
    }

    @Operation(summary = "Get all products : optional filters: inventoryStatus, category")
    @GetMapping
    public ResponseEntity<List<ProductResponse>> findAll(
            @RequestParam(required = false) InventoryStatus inventoryStatus,
            @RequestParam(required = false) String category) {
        return ResponseEntity.ok(productService.findAll(inventoryStatus, category));
    }

    @Operation(summary = "Get product by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @Operation(summary = "Update product")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    @Operation(summary = "Delete product")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Create product with multipart image upload",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            encoding = {
                                    @Encoding(name = "product", contentType = "application/json"),
                                    @Encoding(name = "image", contentType = "image/*")
                            }
                    )
            )
    )
    @PostMapping(value = "/v2", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> createWithFile(
            @RequestPart("product") ProductRequest request,
            @RequestPart("image") MultipartFile image
    ) throws IOException {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createWithFile(request, image));
    }
}
