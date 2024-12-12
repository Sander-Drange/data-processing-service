package com.example.data_processing_service.dto.request;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceRequest {
    private String symbol;
    private String assetType;
}