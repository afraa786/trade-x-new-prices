package com.alpharedge.dto.coingecko;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoinGeckoDetailResponse {
    private String id;
    private String symbol;
    private String name;
    private MarketData marketData;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MarketData {
        private Price currentPrice;
        private Price allTimeHigh;
        private Price allTimeLow;
        private MarketCap marketCap;
        private Volume vol24h;
        private PriceChange priceChange24h;
        private PriceChange priceChangePercentage7d;
        private PriceChange priceChangePercentage30d;
        private Supply circulatingSupply;
        private Supply totalSupply;
        private Integer marketCapRank;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Price {
            private BigDecimal usd;
            private BigDecimal inr;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class MarketCap {
            private BigDecimal usd;
            private BigDecimal inr;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Volume {
            private BigDecimal usd;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class PriceChange {
            private BigDecimal usd;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Supply {
            private BigDecimal value;
        }
    }
}
