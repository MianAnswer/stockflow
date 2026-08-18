package com.miananswer.stockflow.controller;

import com.miananswer.stockflow.model.dto.ProductResponse;
import com.miananswer.stockflow.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    void createProduct_shouldReturn201WhenRequestIsValid() throws Exception {

        ProductResponse response = new ProductResponse(
                1L,
                "LAPTOP-001",
                "Laptop",
                "Business laptop",
                new BigDecimal("999.99"),
                10
        );

        when(productService.createProduct(any()))
                .thenReturn(response);

        String request = """
                {
                    "sku": "LAPTOP-001",
                    "name": "Laptop",
                    "description": "Business laptop",
                    "price": 999.99,
                    "quantity": 10
                }
                """;

        MockHttpServletRequestBuilder postRequest = post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request);

        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.sku").value("LAPTOP-001"))
                .andExpect(jsonPath("$.name").value("Laptop"));

        verify(productService).createProduct(any());
    }

    @Test
    void createProduct_shouldReturn400WhenRequestIsInvalid() throws Exception {

        String request = """
            {
                "sku": "",
                "name": "",
                "description": "Invalid product",
                "price": -10,
                "quantity": -5
            }
            """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.errors.sku").value("SKU must not be blank"))
                .andExpect(jsonPath("$.errors.name").value("Name must not be blank"))
                .andExpect(jsonPath("$.errors.price").value("Price must be greater than 0"))
                .andExpect(jsonPath("$.errors.quantity").value("Quantity cannot be negative"));

        verify(productService, never()).createProduct(any());
    }

    @Test
    void createProduct_shouldReturn400WhenJsonIsMalformed() throws Exception {

        String request = """
            {
                "sku": "LAPTOP-001",
                "name": "Laptop",
                "price": 999.99,
                "quantity": 
            }
            """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());

        verify(productService, never()).createProduct(any());
    }
}
