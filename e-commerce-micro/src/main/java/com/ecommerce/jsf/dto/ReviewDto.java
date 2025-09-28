package com.ecommerce.jsf.dto;

import java.time.OffsetDateTime;

public record ReviewDto(
    Long reviewId,
    Long productId,
    Long customerId,
    int rating,
    String comment,
    OffsetDateTime reviewDate) {}
