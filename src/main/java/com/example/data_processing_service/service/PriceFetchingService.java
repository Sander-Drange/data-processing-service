package com.example.data_processing_service.service;

import com.example.data_processing_service.config.RabbitMQConfig;
import com.example.data_processing_service.dto.PriceRequest;
import com.example.data_processing_service.dto.response.PriceResponse;
import org.springframework.cache.annotation.Cacheable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PriceFetchingService {
    private final CryptoPriceFetchingService cryptoPriceFetchingService;
    private final StockPriceFetchingService stockPriceFetchingService;
    private final MetalsPriceFetchingService metalsPriceFetchingService;
    private final FundPriceFetchingService fundPriceFetchingService;
    private final RabbitTemplate rabbitTemplate;

    @Cacheable(value = "prices", key = "#symbol + #assetType")
    public PriceResponse fetchPrice(String symbol, String assetType) {
        PriceResponse response = switch (assetType.toUpperCase()) {
            case "CRYPTOCURRENCY" -> cryptoPriceFetchingService.getPrice(symbol);
            case "STOCKS" -> stockPriceFetchingService.getPrice(symbol);
            case "PRECIOUS_METALS" -> metalsPriceFetchingService.getPrice(symbol);
            case "FUNDS" -> fundPriceFetchingService.getPrice(symbol);
            default -> throw new IllegalArgumentException("Unsupported asset type: " + assetType);
        };

        // Publish price update to RabbitMQ
        publishPriceUpdate(response);
        return response;
    }

    public List<PriceResponse> fetchBatchPrices(List<PriceRequest> requests) {
        return requests.stream()
                .map(req -> fetchPrice(req.getSymbol(), req.getAssetType()))
                .collect(Collectors.toList());
    }

    private void publishPriceUpdate(PriceResponse priceResponse) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.PRICE_UPDATE_EXCHANGE,
                    RabbitMQConfig.PRICE_UPDATE_ROUTING_KEY,
                    priceResponse
            );
        } catch (Exception e) {
            log.error("Failed to publish price update: {}", e.getMessage());
        }
    }
}
