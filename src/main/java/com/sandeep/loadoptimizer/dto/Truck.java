package com.sandeep.loadoptimizer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record Truck(
        @NotBlank
        String id,

        @Min(1)
        @JsonProperty("max_weight_lbs")
        int maxWeightLbs,

        @Min(1)
        @JsonProperty("max_volume_cuft")
        int maxVolumeCuft
) {}
