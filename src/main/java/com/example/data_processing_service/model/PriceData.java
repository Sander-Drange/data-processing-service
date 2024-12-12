package com.example.data_processing_service.model;

import com.example.data_processing_service.enums.AssetType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "price_data",
        indexes = @Index(name = "idx_symbol_asset_type", columnList = "symbol, assetType")
)
public class PriceData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String symbol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetType assetType;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private String currency;

    private String source; // What API provided the data

    @Column(nullable = false)
    private LocalDateTime fetchTime;

    private boolean isLatest; // Flag to mark the most recent price

    @Version
    private Long version;

    @PrePersist
    private void prePersist() {
        this.fetchTime = LocalDateTime.now();
    }
}
