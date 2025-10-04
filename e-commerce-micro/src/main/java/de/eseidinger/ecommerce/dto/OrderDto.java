package de.eseidinger.ecommerce.dto;

import java.time.OffsetDateTime;

public record OrderDto(
    Long orderId, OffsetDateTime orderDate, Long customerId, Double totalAmount) {}
