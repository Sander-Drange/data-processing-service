package com.example.data_processing_service.service;

import com.example.data_processing_service.dto.response.PriceResponse;
import com.example.data_processing_service.exception.PriceFetchingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class MetalsPriceFetchingService {
    private final WebClient webClient;

    @Value("${api.metals.key}")
    private String apiKey;

    private static final String METALS_API = "https://metals-api.com/api/latest";

    @Cacheable(value = "metalPrices", key = "#symbol")
    public PriceResponse getPrice(String symbol) {
        try {
            String metalSymbol = getMetalSymbol(symbol);

            // Build the URI using UriComponentsBuilder
            String uri = UriComponentsBuilder.fromUriString(METALS_API)
                    .queryParam("access_key", apiKey)
                    .queryParam("base", "USD")
                    .queryParam("symbols", metalSymbol)
                    .toUriString();

            // Fetch the response
            var response = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            // Parse the response
            if (response != null && response.containsKey("rates")) {
                Map<String, Double> rates = (Map<String, Double>) response.get("rates");
                if (rates.containsKey(metalSymbol)) {
                    return PriceResponse.builder()
                            .symbol(symbol)
                            .assetType("PRECIOUS_METALS")
                            .currentPrice(BigDecimal.valueOf(rates.get(metalSymbol)))
                            .currency("USD")
                            .lastUpdated(LocalDateTime.now())
                            .source("Metals-API")
                            .build();
                }
            }
            throw new PriceFetchingException("Price not found for metal: " + symbol);
        } catch (Exception e) {
            log.error("Error fetching metal price for symbol: {}. Exception: {}", symbol, e.getMessage(), e);
            throw new PriceFetchingException("Failed to fetch metal price: " + e.getMessage());
        }
    }

    private String getMetalSymbol(String metal) {
        return switch (metal.toUpperCase()) {
            case "GOLD" -> "XAU";
            case "SILVER" -> "XAG";
            case "PLATINUM" -> "XPT";
            case "PALLADIUM" -> "XPD";
            default -> throw new IllegalArgumentException("Unsupported metal type: " + metal);
        };
    }
}
