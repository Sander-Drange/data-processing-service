package com.example.data_processing_service.service;

import com.example.data_processing_service.config.RabbitMQConfig;
import com.example.data_processing_service.dto.events.HoldingCreatedEvent;
import com.example.data_processing_service.dto.events.PriceUpdateEvent;
import com.example.data_processing_service.dto.request.PriceRequest;
import com.example.data_processing_service.dto.response.PriceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PriceUpdateService {
    private final PriceFetchingService priceFetchingService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMQConfig.HOLDING_CREATED_QUEUE)
    public void handleNewHolding(HoldingCreatedEvent event) {
        try {
            PriceResponse response = priceFetchingService.fetchPrice(
                    event.getSymbol(),
                    event.getAssetType()
            );
            publishPriceUpdate(response);
        } catch (Exception e) {
            log.error("Error processing new holding: {}", e.getMessage());
        }
    }

    @RabbitListener(queues = RabbitMQConfig.PRICE_REQUEST_QUEUE)
    public void handlePriceRequest(PriceRequest request) {
        try {
            PriceResponse response = priceFetchingService.fetchPrice(
                    request.getSymbol(),
                    request.getAssetType()
            );
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.PRICE_RESPONSE_QUEUE,
                    response
            );
        } catch (Exception e) {
            log.error("Error processing price request: {}", e.getMessage());
        }
    }

    private void publishPriceUpdate(PriceResponse response) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PRICE_UPDATE_EXCHANGE,
                RabbitMQConfig.PRICE_UPDATE_ROUTING_KEY,
                response
        );
    }
}
