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
public class StockPriceFetchingService {
    private final WebClient webClient;

    @Value("${api.yahoo.base-url}")
    private String baseUrl;

    @Value("${api.yahoo.key}")
    private String apiKey;

    @Value("${api.yahoo.host}")
    private String apiHost;

    @Cacheable(value = "stockPrices", key = "#symbol")
    public PriceResponse getPrice(String symbol) {
        try {
            String uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/stock/v2/get-summary")
                    .queryParam("symbol", symbol)
                    .queryParam("region", "US") // Adjust region if needed
                    .toUriString();

            var response = webClient.get()
                    .uri(uri)
                    .header("x-rapidapi-host", apiHost)
                    .header("x-rapidapi-key", apiKey)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("price")) {
                Map<String, Object> priceData = (Map<String, Object>) response.get("price");
                BigDecimal price = BigDecimal.valueOf((Double) priceData.get("regularMarketPrice"));
                String currency = (String) priceData.get("currency");

                return PriceResponse.builder()
                        .symbol(symbol)
                        .assetType("STOCKS")
                        .currentPrice(price)
                        .currency(currency)
                        .lastUpdated(LocalDateTime.now())
                        .source("Yahoo Finance")
                        .build();
            }
            throw new PriceFetchingException("Price not found for stock: " + symbol);
        } catch (Exception e) {
            log.error("Error fetching stock price for {}: {}", symbol, e.getMessage());
            throw new PriceFetchingException("Failed to fetch stock price: " + e.getMessage());
        }
    }
}
