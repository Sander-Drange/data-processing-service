package com.example.data_processing_service.service;

import com.example.data_processing_service.dto.response.PriceResponse;
import com.example.data_processing_service.exception.PriceFetchingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CryptoPriceFetchingService {
    private final WebClient webClient;
    private static final String COINGECKO_API = "https://api.coingecko.com/api/v3";

    public PriceResponse getPrice(String symbol) {
        try {
            var response = webClient.get()
                    .uri(COINGECKO_API + "/simple/price?ids=" + symbol + "&vs_currencies=usd")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey(symbol)) {
                Map<String, Object> priceData = (Map<String, Object>) response.get(symbol);
                Double usdPrice = (Double) priceData.get("usd");
                return PriceResponse.builder()
                        .symbol(symbol)
                        .assetType("CRYPTOCURRENCY")
                        .currentPrice(BigDecimal.valueOf(usdPrice))
                        .currency("USD")
                        .lastUpdated(LocalDateTime.now())
                        .source("CoinGecko")
                        .build();
            }
            throw new PriceFetchingException("Failed to fetch price for symbol: " + symbol + ". Reason: Price data is missing in API response.");
        } catch (Exception e) {
            log.error("Error Fetching Crypto Price For {}: {}", symbol, e.getMessage());
            throw new PriceFetchingException("Failed To Fetch Crypto Price: " + e.getMessage());
        }
    }
}
