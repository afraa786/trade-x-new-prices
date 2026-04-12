package com.alpharedge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrendingDTO {
    private String coinId;
    private String name;
    private String symbol;
    private String thumb;
    private Integer marketCapRank;
}
