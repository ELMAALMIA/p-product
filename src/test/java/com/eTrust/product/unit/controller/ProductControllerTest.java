package com.eTrust.product.unit.controller;

import com.eTrust.product.controller.ProductController;
import com.eTrust.product.dto.request.ProductRequest;
import com.eTrust.product.dto.response.ProductResponse;
import com.eTrust.product.entity.InventoryStatus;
import com.eTrust.product.security.JwtAuthFilter;
import com.eTrust.product.security.JwtService;
import com.eTrust.product.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = ProductController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthFilter.class
        )
)
class ProductControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean ProductService productService;
    @MockBean JwtService jwtService;

    private ProductRequest validRequest;
    private ProductResponse sampleResponse;

    @BeforeEach
    void setUp() {
        validRequest = new ProductRequest(
                "PROD001", "iPhone 15 Pro", "Latest Apple smartphone",
                "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==",
                "Electronics", 999.99, 10, 1L, InventoryStatus.INSTOCK, 4.5
        );

        sampleResponse = new ProductResponse(
                1L, "PROD001", "iPhone 15 Pro", "Latest Apple smartphone",
                "data:image/png;base64,abc", "Electronics",
                999.99, 10, "REF-ABC12345", 1L,
                InventoryStatus.INSTOCK, 4.5,
                Instant.now(), Instant.now()
        );
    }


    @Test
    @WithMockUser
    @DisplayName("POST /products — 201 Created")
    void create_returns201() throws Exception {
        when(productService.create(any())).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/v1/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("PROD001"))
                .andExpect(jsonPath("$.name").value("iPhone 15 Pro"))
                .andExpect(jsonPath("$.internalReference").value("REF-ABC12345"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /products — 400 when code is blank")
    void create_invalidCode_returns400() throws Exception {
        ProductRequest bad = new ProductRequest(
                "", "Name", null, "data:image/png;base64,abc",
                "Electronics", 10.0, 5, null, InventoryStatus.INSTOCK, 0.0
        );

        mockMvc.perform(post("/api/v1/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.code").exists());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /products — 400 when quantity exceeds 40")
    void create_quantityTooHigh_returns400() throws Exception {
        ProductRequest bad = new ProductRequest(
                "PROD001", "Name", null, "data:image/png;base64,abc",
                "Electronics", 10.0, 99, null, InventoryStatus.INSTOCK, 0.0
        );

        mockMvc.perform(post("/api/v1/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.quantity").exists());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /products — 400 when price is zero")
    void create_priceZero_returns400() throws Exception {
        ProductRequest bad = new ProductRequest(
                "PROD001", "Name", null, "data:image/png;base64,abc",
                "Electronics", 0.0, 5, null, InventoryStatus.INSTOCK, 0.0
        );

        mockMvc.perform(post("/api/v1/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.price").exists());
    }


    @Test
    @WithMockUser
    @DisplayName("GET /products — 200 with list")
    void findAll_returns200() throws Exception {
        when(productService.findAll(null, null)).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("PROD001"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /products?inventoryStatus=INSTOCK — 200 filtered")
    void findAll_withFilter_returns200() throws Exception {
        when(productService.findAll(InventoryStatus.INSTOCK, null))
                .thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/v1/products")
                        .param("inventoryStatus", "INSTOCK"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].inventoryStatus").value("INSTOCK"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /products/1 — 200 when found")
    void findById_returns200() throws Exception {
        when(productService.findById(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }


    @Test
    @WithMockUser
    @DisplayName("PUT /products/1 — 200 updated")
    void update_returns200() throws Exception {
        when(productService.update(eq(1L), any())).thenReturn(sampleResponse);

        mockMvc.perform(put("/api/v1/products/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("PROD001"));
    }


    @Test
    @WithMockUser
    @DisplayName("DELETE /products/1 — 204 No Content")
    void delete_returns204() throws Exception {
        doNothing().when(productService).delete(1L);

        mockMvc.perform(delete("/api/v1/products/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(productService).delete(1L);
    }


    @Test
    @DisplayName("GET /products :  401 when no token")
    void findAll_noAuth_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isUnauthorized());
    }
}