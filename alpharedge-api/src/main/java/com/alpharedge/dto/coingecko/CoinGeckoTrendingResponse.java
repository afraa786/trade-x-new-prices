package com.alpharedge.dto.coingecko;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoinGeckoTrendingResponse {
    private List<TrendingCoin> coins;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TrendingCoin {
        private Item item;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Item {
            private Integer id;
            private String coinId;
            private String name;
            private String symbol;
            private String thumb;
            private Integer marketCapRank;
        }
    }
}
