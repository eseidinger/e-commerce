package de.eseidinger.ecommerce.dto;

import java.time.OffsetDateTime;

public record ReviewDto(
    Long reviewId,
    Long productId,
    Long customerId,
    int rating,
    String comment,
    OffsetDateTime reviewDate) {}
