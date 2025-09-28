package com.ecommerce.jsf.dto;

import java.time.LocalDate;

public record ReviewDto(
    Long reviewId,
    Long productId,
    Long customerId,
    int rating,
    String comment,
    LocalDate reviewDate) {}
