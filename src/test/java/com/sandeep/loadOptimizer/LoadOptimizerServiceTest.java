package com.sandeep.loadOptimizer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sandeep.loadOptimizer.dto.TruckLoadResponse;
import com.sandeep.loadOptimizer.dto.TruckOrderRequest;
import com.sandeep.loadOptimizer.service.LoadOptimizerService;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import static org.junit.jupiter.api.Assertions.*;

class LoadOptimizerServiceTest {

    private final LoadOptimizerService optimizer = new LoadOptimizerService();
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void test_optimize_one() throws Exception {
        // Load the file exactly as you provided it
        InputStream is = new ClassPathResource("truck-order-request-one.json").getInputStream();
        TruckOrderRequest request = mapper.readValue(is, TruckOrderRequest.class);

        // Execute
        TruckLoadResponse response = optimizer.optimize(request.truck(), request.orders());

        // Assertions
        assertEquals("truck-123", response.truckId());
        assertEquals(430000, response.totalPayoutCents());
        assertEquals(30000, response.totalWeightLbs());
        assertEquals(2, response.selectedOrderIds().size());
        assertEquals(68.18, response.utilizationWeightPercent());
        assertEquals(70.0, response.utilizationVolumePercent());
    }

    @Test
    void test_optimize_two() throws Exception {
        InputStream is = new ClassPathResource("truck-order-request-two.json").getInputStream();
        TruckOrderRequest request = mapper.readValue(is, TruckOrderRequest.class);

        TruckLoadResponse response = optimizer.optimize(request.truck(), request.orders());

        // Assertions
        assertEquals(5, response.selectedOrderIds().size());
        assertEquals(38500, response.totalWeightLbs());
        assertEquals(3000, response.totalVolumeCuft());
        assertEquals(87.5, response.utilizationWeightPercent());
        assertEquals(100.0, response.utilizationVolumePercent());
    }

}