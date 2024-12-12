package com.example.data_processing_service.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PriceResponse {
    private String symbol;
    private String assetType;
    private BigDecimal currentPrice;
    private String currency;
    private LocalDateTime lastUpdated;
    private String source;  // Which API provided the data
}
