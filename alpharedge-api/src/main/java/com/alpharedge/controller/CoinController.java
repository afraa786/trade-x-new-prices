package com.alpharedge.controller;

import com.alpharedge.dto.response.CoinDetailDTO;
import com.alpharedge.dto.response.CoinSignalDTO;
import com.alpharedge.dto.response.CompareDTO;
import com.alpharedge.dto.response.PriceSnapshotDTO;
import com.alpharedge.dto.response.TrackedCoinDTO;
import com.alpharedge.service.CoinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/coins")
@Tag(name = "Coins", description = "Cryptocurrency tracking and data endpoints")
public class CoinController {

    private final CoinService coinService;

    @Autowired
    public CoinController(CoinService coinService) {
        this.coinService = coinService;
    }

    @PostMapping("/track")
    @Operation(summary = "Track a new cryptocurrency", description = "Start tracking a cryptocurrency by its CoinGecko ID")
    public ResponseEntity<TrackedCoinDTO> trackCoin(
            @Parameter(description = "CoinGecko coin ID (e.g., 'bitcoin')")
            @RequestParam String coinId) {
        log.info("Track coin request: {}", coinId);
        TrackedCoinDTO tracked = coinService.trackCoin(coinId);
        return ResponseEntity.status(HttpStatus.CREATED).body(tracked);
    }

    @GetMapping
    @Operation(summary = "Get all tracked coins", description = "Retrieve list of all active tracked cryptocurrencies")
    public ResponseEntity<List<TrackedCoinDTO>> getAllCoins() {
        log.debug("Get all coins request");
        List<TrackedCoinDTO> coins = coinService.getAllCoins();
        return ResponseEntity.ok(coins);
    }

    @GetMapping("/{coinId}")
    @Operation(summary = "Get coin details", description = "Get detailed information about a specific tracked coin")
    public ResponseEntity<CoinDetailDTO> getCoinDetail(
            @Parameter(description = "CoinGecko coin ID")
            @PathVariable String coinId) {
        log.debug("Get coin detail request: {}", coinId);
        CoinDetailDTO details = coinService.getCoinDetail(coinId);
        return ResponseEntity.ok(details);
    }

    @GetMapping("/{coinId}/history")
    @Operation(summary = "Get coin price history", description = "Get historical price data for a coin over specified days")
    public ResponseEntity<List<PriceSnapshotDTO>> getCoinHistory(
            @Parameter(description = "CoinGecko coin ID")
            @PathVariable String coinId,
            @Parameter(description = "Number of days to retrieve")
            @RequestParam(defaultValue = "30") int days) {
        log.debug("Get coin history request: coinId={}, days={}", coinId, days);
        List<PriceSnapshotDTO> history = coinService.getCoinHistory(coinId, days);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/{coinId}/signal")
    @Operation(summary = "Get technical analysis signal", description = "Get the latest technical analysis signal for a coin")
    public ResponseEntity<CoinSignalDTO> getCoinSignal(
            @Parameter(description = "CoinGecko coin ID")
            @PathVariable String coinId) {
        log.debug("Get coin signal request: {}", coinId);
        CoinSignalDTO signal = coinService.getCoinSignal(coinId);
        return ResponseEntity.ok(signal);
    }

    @GetMapping("/{coinId}/price")
    @Operation(summary = "Get live price", description = "Get the latest live price data for a coin")
    public ResponseEntity<PriceSnapshotDTO> getLivePrice(
            @Parameter(description = "CoinGecko coin ID")
            @PathVariable String coinId) {
        log.debug("Get live price request: {}", coinId);
        PriceSnapshotDTO price = coinService.getLivePrice(coinId);
        return ResponseEntity.ok(price);
    }

    @GetMapping("/compare")
    @Operation(summary = "Compare coins", description = "Compare multiple coins by their signals and metrics")
    public ResponseEntity<List<CompareDTO>> compareCoins(
            @Parameter(description = "Comma-separated list of CoinGecko coin IDs")
            @RequestParam String ids) {
        log.debug("Compare coins request: {}", ids);
        List<String> coinIds = List.of(ids.split(","));
        List<CompareDTO> comparison = coinService.compareCoins(coinIds);
        return ResponseEntity.ok(comparison);
    }
}
