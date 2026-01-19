package com.sandeep.loadOptimizer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Truck(
        String id,

        @JsonProperty("max_weight_lbs")
        int maxWeightLbs,

        @JsonProperty("max_volume_cuft")
        int maxVolumeCuft
) {}
