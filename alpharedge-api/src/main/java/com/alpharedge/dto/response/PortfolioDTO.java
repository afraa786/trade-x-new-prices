package com.alpharedge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioDTO {
    private String id;
    private String userId;
    private String name;
    private List<HoldingSummaryDTO> holdings;
    private LocalDateTime createdAt;
}
