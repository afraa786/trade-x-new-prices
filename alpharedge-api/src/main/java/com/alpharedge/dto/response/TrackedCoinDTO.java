package com.alpharedge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackedCoinDTO {
    private String id;
    private String coinId;
    private String symbol;
    private String name;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
