package com.sandeep.loadoptimizer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record Order(
        @NotBlank
        String id,

        @Min(1)
        @JsonProperty("payout_cents")
        int payoutCents,

        @Min(1)
        @JsonProperty("weight_lbs")
        int weightLbs,

        @Min(1)
        @JsonProperty("volume_cuft")
        int volumeCuft,

        @NotBlank
        String origin,

        @NotBlank
        String destination,

        @NotBlank
        @JsonProperty("pickup_date")
        String pickupDate,

        @NotBlank
        @JsonProperty("delivery_date")
        String deliveryDate,

        @JsonProperty("is_hazmat")
        boolean isHazmat
) {}
