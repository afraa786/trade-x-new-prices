package com.alpharedge.controller;

import com.alpharedge.dto.response.GlobalMarketDTO;
import com.alpharedge.dto.response.PriceSnapshotDTO;
import com.alpharedge.dto.response.TrendingDTO;
import com.alpharedge.service.MarketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/market")
@Tag(name = "Market", description = "Global market data and trending coins endpoints")
public class MarketController {

    private final MarketService marketService;

    @Autowired
    public MarketController(MarketService marketService) {
        this.marketService = marketService;
    }

    @GetMapping("/summary")
    @Operation(summary = "Get global market summary", description = "Get global cryptocurrency market data")
    public ResponseEntity<GlobalMarketDTO> getGlobalSummary() {
        log.debug("Get global market summary request");
        GlobalMarketDTO summary = marketService.getGlobalSummary();
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/trending")
    @Operation(summary = "Get trending coins", description = "Get list of trending cryptocurrencies")
    public ResponseEntity<List<TrendingDTO>> getTrending() {
        log.debug("Get trending coins request");
        List<TrendingDTO> trending = marketService.getTrending();
        return ResponseEntity.ok(trending);
    }

    @GetMapping("/rankings")
    @Operation(summary = "Get ranked coins", description = "Get paginated list of cryptocurrencies with ranking and sorting")
    public ResponseEntity<Page<PriceSnapshotDTO>> getRankings(
            @Parameter(description = "Sort by: marketCap, price, priceChange24h, priceChange7d, volume24h")
            @RequestParam(defaultValue = "marketCap") String sortBy,
            @Parameter(description = "Sort order: asc or desc")
            @RequestParam(defaultValue = "asc") String order,
            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size) {
        log.debug("Get rankings request: sortBy={}, order={}, page={}, size={}", sortBy, order, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<PriceSnapshotDTO> rankings = marketService.getRankings(sortBy, order, pageable);
        return ResponseEntity.ok(rankings);
    }
}
