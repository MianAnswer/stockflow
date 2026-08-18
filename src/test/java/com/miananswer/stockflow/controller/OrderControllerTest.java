package com.miananswer.stockflow.controller;

import com.miananswer.stockflow.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    void createOrder_shouldReturn400WhenOrderHasNoItems() throws Exception {

        String request = """
                {
                    "items": []
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());

        verify(orderService, never()).createOrder(any());
    }

    @Test
    void createOrder_shouldReturn400WhenQuantityIsInvalid() throws Exception {

        String request = """
                {
                    "items": [
                        {
                            "productId": 1,
                            "quantity": 0
                        }
                    ]
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());

        verify(orderService, never()).createOrder(any());
    }
}
