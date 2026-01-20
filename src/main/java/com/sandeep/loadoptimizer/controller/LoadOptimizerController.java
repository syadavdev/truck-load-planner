package com.sandeep.loadoptimizer.controller;

import com.sandeep.loadoptimizer.dto.TruckLoadResponse;
import com.sandeep.loadoptimizer.dto.TruckOrderRequest;
import com.sandeep.loadoptimizer.service.LoadOptimizerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/api/v1/load-optimizer")
@RequiredArgsConstructor
public class LoadOptimizerController {

    private final LoadOptimizerService optimizerService;

    @PostMapping("/optimize")
    public Mono<TruckLoadResponse> optimize(@Valid @RequestBody TruckOrderRequest request) {
        return Mono.fromCallable(() -> optimizerService.optimize(request.truck(), request.orders()))
                .subscribeOn(Schedulers.boundedElastic())
                .timeout(Duration.ofSeconds(2))
                .onErrorResume(ex -> {
                    if (ex instanceof TimeoutException) {
                        return Mono.error(new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Limit Exceeded"));
                    }
                    return Mono.error(ex);
                });
    }
}
