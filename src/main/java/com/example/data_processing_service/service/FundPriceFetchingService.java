package com.example.data_processing_service.service;

import com.example.data_processing_service.dto.response.PriceResponse;
import com.example.data_processing_service.exception.PriceFetchingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class FundPriceFetchingService {
    private final WebClient webClient;
    private static final String YAHOO_API = "https://query1.finance.yahoo.com/v8/finance/chart/";

    @Cacheable(value = "fundPrices", key = "#symbol")
    public PriceResponse getPrice(String symbol) {
        try {
            // Add .OL suffix for Norwegian funds if not present
            String adjustedSymbol = adjustSymbol(symbol);

            var response = webClient.get()
                    .uri(YAHOO_API + adjustedSymbol + "?interval=1d&range=1d")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("chart")) {
                Map<String, Object> chart = (Map<String, Object>) response.get("chart");
                List<Map<String, Object>> results = (List<Map<String, Object>>) chart.get("result");

                if (results != null && !results.isEmpty()) {
                    Map<String, Object> meta = (Map<String, Object>) results.get(0).get("meta");
                    if (meta != null && meta.containsKey("regularMarketPrice")) {
                        BigDecimal price = BigDecimal.valueOf((Double) meta.get("regularMarketPrice"));
                        String currency = (String) meta.get("currency");

                        return PriceResponse.builder()
                                .symbol(symbol)
                                .assetType("FUNDS")
                                .currentPrice(price)
                                .currency(currency)
                                .lastUpdated(LocalDateTime.now())
                                .source("Yahoo Finance")
                                .build();
                    }
                }
            }
            throw new PriceFetchingException("Price not found for fund: " + symbol);
        } catch (Exception e) {
            log.error("Error fetching fund price for symbol: {}. Exception: {}", symbol, e.getMessage(), e);
            throw new PriceFetchingException("Failed to fetch fund price: " + e.getMessage());
        }
    }

    private String adjustSymbol(String symbol) {
        // For Norwegian mutual funds
        if (isNorwegianFund(symbol) && !symbol.endsWith(".OL")) {
            return symbol + ".OL";
        }
        return symbol;
    }

    private boolean isNorwegianFund(String symbol) {
        // Add logic to identify Norwegian funds
        Set<String> norwegianFundPrefixes = Set.of("ODIN", "DNB", "KLP"); // Example prefixes
        return norwegianFundPrefixes.stream().anyMatch(prefix -> symbol.startsWith(prefix));
    }
}
