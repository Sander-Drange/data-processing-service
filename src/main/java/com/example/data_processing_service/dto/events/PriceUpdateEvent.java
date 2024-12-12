package com.example.data_processing_service.dto.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceUpdateEvent {
    private String symbol;
    private String assetType;
    private BigDecimal price;
    private String currency;
    private LocalDateTime timestamp;
}