package com.unomi_service.dto;

public class ProductViewRequest {
    private String sessionId;
    private String scope;
    private String sourceId;     // ví dụ product-detail-page
    private String sourceType;   // page
    private String productId;
    private String productName;
    private String category;
    private Double price;
}