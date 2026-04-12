package com.alpharedge.controller;

import com.alpharedge.dto.request.CreateAlertRequest;
import com.alpharedge.dto.response.AlertDTO;
import com.alpharedge.service.AlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping("/api/v1/alerts")
@Tag(name = "Alerts", description = "Price alert management endpoints")
public class AlertController {

    private final AlertService alertService;

    @Autowired
    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @PostMapping
    @Operation(summary = "Create price alert", description = "Create a new price alert for a cryptocurrency")
    public ResponseEntity<AlertDTO> createAlert(
            @Parameter(description = "User ID from header")
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CreateAlertRequest request) {
        log.info("Create alert request for user: {}", userId);
        AlertDTO alert = alertService.createAlert(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(alert);
    }

    @GetMapping
    @Operation(summary = "Get user alerts", description = "Retrieve all active price alerts for the user")
    public ResponseEntity<List<AlertDTO>> getUserAlerts(
            @Parameter(description = "User ID from header")
            @RequestHeader("X-User-Id") String userId) {
        log.debug("Get alerts request for user: {}", userId);
        List<AlertDTO> alerts = alertService.getUserAlerts(userId);
        return ResponseEntity.ok(alerts);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate alert", description = "Deactivate a price alert")
    public ResponseEntity<Void> deactivateAlert(
            @Parameter(description = "Alert ID")
            @PathVariable String id,
            @Parameter(description = "User ID from header")
            @RequestHeader("X-User-Id") String userId) {
        log.info("Deactivate alert: {} for user: {}", id, userId);
        alertService.deactivateAlert(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate alert (patch)", description = "Deactivate a price alert via PATCH")
    public ResponseEntity<Void> deactivateAlertPatch(
            @Parameter(description = "Alert ID")
            @PathVariable String id,
            @Parameter(description = "User ID from header")
            @RequestHeader("X-User-Id") String userId) {
        log.info("Deactivate alert (PATCH): {} for user: {}", id, userId);
        alertService.deactivateAlert(id, userId);
        return ResponseEntity.noContent().build();
    }
}
