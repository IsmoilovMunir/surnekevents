package com.surnekev.ticketing.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CreateSeatCategoryRequest(
        @NotBlank(message = "Category name is required")
        String name,
        @NotNull(message = "Price is required")
        @Min(value = 0, message = "Price must be greater or equal to zero")
        Integer priceCents,
        String description,
        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be in #RRGGBB format")
        String colorHex
) {
}


