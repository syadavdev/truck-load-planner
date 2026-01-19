package com.sandeep.loadOptimizer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Order(
        String id,

        @JsonProperty("payout_cents")
        int payoutCents,

        @JsonProperty("weight_lbs")
        int weightLbs,

        @JsonProperty("volume_cuft")
        int volumeCuft,

        String origin,
        String destination,

        @JsonProperty("pickup_date")
        String pickupDate,

        @JsonProperty("delivery_date")
        String deliveryDate,

        @JsonProperty("is_hazmat")
        boolean isHazmat
) {}
