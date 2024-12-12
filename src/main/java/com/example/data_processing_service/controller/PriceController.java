package com.example.data_processing_service.controller;

import com.example.data_processing_service.dto.PriceRequest;
import com.example.data_processing_service.dto.response.PriceResponse;
import com.example.data_processing_service.service.PriceFetchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prices")
@RequiredArgsConstructor
public class PriceController {
    private final PriceFetchingService priceFetchingService;

    @GetMapping("/{assetType}/{symbol}")
    public ResponseEntity<PriceResponse> getPrice(
            @PathVariable String assetType,
            @PathVariable String symbol
    ) {
        return ResponseEntity.ok(priceFetchingService.fetchPrice(symbol, assetType));
    }

    @PostMapping("/batch")
    public ResponseEntity<List<PriceResponse>> getBatchPrices(
            @RequestBody List<PriceRequest> requests
    ) {
        return ResponseEntity.ok(priceFetchingService.fetchBatchPrices(requests));
    }
}
