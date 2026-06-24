package com.unomi_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductTrackingRequest {
    @NotBlank
    private String sessionId;

    @NotBlank
    private String productId;

    private String productName;

    private String category;

    private BigDecimal price;
}
