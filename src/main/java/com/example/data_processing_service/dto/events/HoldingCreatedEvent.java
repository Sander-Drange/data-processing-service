package com.example.data_processing_service.dto.events;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HoldingCreatedEvent {
    private String userId;
    private String symbol;
    private String assetType;
    private BigDecimal quantity;
    private String currency;
}