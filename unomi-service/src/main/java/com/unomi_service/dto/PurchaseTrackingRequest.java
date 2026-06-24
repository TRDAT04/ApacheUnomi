package com.unomi_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;

@Data
@Getter
public class PurchaseTrackingRequest {

    /** Session ID (bắt buộc) – dùng để liên kết profile trong Unomi */
    @NotBlank
    private String sessionId;

    /** Mã đơn hàng (bắt buộc) – dùng làm itemId của event */
    @NotBlank
    private String orderId;

    /** Tổng giá trị đơn hàng (VND) – VIP threshold là 30,000,000 */
    @Positive
    private BigDecimal amount;

    /** Tuỳ chọn: profileId nếu user đã login, để Unomi merge profile */
    private String profileId;
}
