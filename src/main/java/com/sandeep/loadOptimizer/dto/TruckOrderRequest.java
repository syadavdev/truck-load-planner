package com.sandeep.loadOptimizer.dto;

import java.util.List;

public record TruckOrderRequest(
        Truck truck,
        List<Order> orders
) {
}
