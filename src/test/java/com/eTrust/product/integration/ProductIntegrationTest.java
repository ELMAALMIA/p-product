package com.eTrust.product.integration;

import com.eTrust.product.dto.request.ProductRequest;
import com.eTrust.product.entity.InventoryStatus;
import com.eTrust.product.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@WithMockUser(username = "admin")
class ProductIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired ProductRepository productRepository;

    @BeforeEach
    void cleanDb() {
        productRepository.deleteAll();
    }


    @Test
    @Order(1)
    @DisplayName("POST /products — full flow create")
    void create_success() throws Exception {
        ProductRequest req = buildRequest("PROD001");

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.code").value("PROD001"))
                .andExpect(jsonPath("$.internalReference").value(matchesPattern("REF-[A-Z0-9]{8}")))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    @Order(2)
    @DisplayName("POST /products — 400 duplicate code")
    void create_duplicateCode_returns400() throws Exception {
        ProductRequest req = buildRequest("PROD001");

        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));

        // Duplicate
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(3)
    @DisplayName("GET /products — returns all products")
    void findAll_success() throws Exception {

        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildRequest("PROD001"))));

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @Order(4)
    @DisplayName("GET /products?inventoryStatus=INSTOCK — filtered")
    void findAll_filtered() throws Exception {
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildRequest("PROD001"))));

        mockMvc.perform(get("/api/v1/products")
                        .param("inventoryStatus", "INSTOCK"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].inventoryStatus").value("INSTOCK"));
    }

    @Test
    @Order(5)
    @DisplayName("GET /products/{id} — returns product")
    void findById_success() throws Exception {
        String body = mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest("PROD001"))))
                .andReturn().getResponse().getContentAsString();

        long id = objectMapper.readTree(body).get("id").asLong();

        mockMvc.perform(get("/api/v1/products/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("PROD001"));
    }

    @Test
    @Order(6)
    @DisplayName("GET /products/9999 — 404 not found")
    void findById_notFound() throws Exception {
        mockMvc.perform(get("/api/v1/products/9999"))
                .andExpect(status().isNotFound());
    }


    @Test
    @Order(7)
    @DisplayName("PUT /products/{id} — updates product")
    void update_success() throws Exception {
        String body = mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest("PROD001"))))
                .andReturn().getResponse().getContentAsString();

        long id = objectMapper.readTree(body).get("id").asLong();

        ProductRequest updated = new ProductRequest(
                "PROD001", "Updated Name", "Updated desc",
                "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==",
                "Electronics", 1299.99, 5, 1L, InventoryStatus.LOWSTOCK, 4.0
        );

        mockMvc.perform(put("/api/v1/products/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.price").value(1299.99))
                .andExpect(jsonPath("$.inventoryStatus").value("LOWSTOCK"));
    }


    @Test
    @Order(8)
    @DisplayName("DELETE /products/{id} — 204 then 404")
    void delete_success() throws Exception {
        String body = mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest("PROD001"))))
                .andReturn().getResponse().getContentAsString();

        long id = objectMapper.readTree(body).get("id").asLong();

        // Delete
        mockMvc.perform(delete("/api/v1/products/" + id))
                .andExpect(status().isNoContent());

        // Confirm gone
        mockMvc.perform(get("/api/v1/products/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(9)
    @DisplayName("DELETE /products/9999 — 404")
    void delete_notFound() throws Exception {
        mockMvc.perform(delete("/api/v1/products/9999"))
                .andExpect(status().isNotFound());
    }


    @Test
    @Order(10)
    @WithAnonymousUser
    @DisplayName("GET /products — 401 without token")
    void findAll_noAuth_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isUnauthorized());
    }

    private ProductRequest buildRequest(String code) {
        return new ProductRequest(
                code, "iPhone 15 Pro", "Latest Apple smartphone",
                "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==",
                "Electronics", 999.99, 10, 1L, InventoryStatus.INSTOCK, 4.5
        );
    }
}
