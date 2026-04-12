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
public class CoinGeckoMarketChartResponse {
    private List<Object[]> prices;
    private List<Object[]> marketCaps;
    private List<Object[]> volumes;
}
