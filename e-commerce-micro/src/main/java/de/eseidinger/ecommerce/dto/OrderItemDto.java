package de.eseidinger.ecommerce.dto;

public record OrderItemDto(
    Long orderItemId, Long orderId, Long productId, int quantity, Double price) {}
