package com.sandeep.loadOptimizer.controller;

import com.sandeep.loadOptimizer.dto.Order;
import com.sandeep.loadOptimizer.dto.Truck;
import com.sandeep.loadOptimizer.dto.TruckLoadResponse;
import com.sandeep.loadOptimizer.dto.TruckOrderRequest;
import com.sandeep.loadOptimizer.service.LoadOptimizerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/load-optimizer")
@RequiredArgsConstructor
public class LoadOptimizerController {

    private final LoadOptimizerService optimizerService;

    @PostMapping("/optimize")
    public ResponseEntity<TruckLoadResponse> optimize(@RequestBody TruckOrderRequest request) {
        Truck truck = request.truck();
        List<Order> allOrders = request.orders();
        if (allOrders == null || allOrders.isEmpty()) {
            return ResponseEntity.ok(new TruckLoadResponse(truck.id(), List.of(), 0, 0, 0, 0, 0));
        }

        return ResponseEntity.ok(optimizerService.optimize(truck, allOrders));
    }
}
