package com.alpharedge.dto.coingecko;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoinGeckoMarketResponse {
    private String id;
    private String symbol;
    private String name;

    @JsonProperty("current_price")
    private BigDecimal currentPrice;

    @JsonProperty("market_cap")
    private BigDecimal marketCap;

    @JsonProperty("market_cap_rank")
    private Integer marketCapRank;

    @JsonProperty("total_volume")
    private BigDecimal totalVolume;

    @JsonProperty("price_change_percentage_24h")
    private BigDecimal priceChange24h;

    @JsonProperty("price_change_percentage_7d_in_currency")
    private BigDecimal priceChange7d;

    @JsonProperty("price_change_percentage_30d_in_currency")
    private BigDecimal priceChange30d;

    @JsonProperty("circulating_supply")
    private BigDecimal circulatingSupply;

    @JsonProperty("total_supply")
    private BigDecimal totalSupply;

    @JsonProperty("ath")
    private BigDecimal allTimeHigh;

    @JsonProperty("atl")
    private BigDecimal allTimeLow;
}
