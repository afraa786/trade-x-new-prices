package com.alpharedge.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CoinNotFoundException.class)
    public ProblemDetail handleCoinNotFoundException(CoinNotFoundException ex, WebRequest request) {
        log.error("Coin not found: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
        problemDetail.setTitle("Coin Not Found");
        problemDetail.setType(URI.create("https://api.alpharedge.com/errors/coin-not-found"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(PortfolioNotFoundException.class)
    public ProblemDetail handlePortfolioNotFoundException(PortfolioNotFoundException ex, WebRequest request) {
        log.error("Portfolio not found: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
        problemDetail.setTitle("Portfolio Not Found");
        problemDetail.setType(URI.create("https://api.alpharedge.com/errors/portfolio-not-found"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(AlertNotFoundException.class)
    public ProblemDetail handleAlertNotFoundException(AlertNotFoundException ex, WebRequest request) {
        log.error("Alert not found: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
        problemDetail.setTitle("Alert Not Found");
        problemDetail.setType(URI.create("https://api.alpharedge.com/errors/alert-not-found"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ProblemDetail handleUnauthorizedException(UnauthorizedException ex, WebRequest request) {
        log.error("Unauthorized access: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                ex.getMessage()
        );
        problemDetail.setTitle("Unauthorized Access");
        problemDetail.setType(URI.create("https://api.alpharedge.com/errors/unauthorized"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(RateLimitException.class)
    public ProblemDetail handleRateLimitException(RateLimitException ex, WebRequest request) {
        log.error("Rate limit exceeded: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.TOO_MANY_REQUESTS,
                ex.getMessage()
        );
        problemDetail.setTitle("Rate Limit Exceeded");
        problemDetail.setType(URI.create("https://api.alpharedge.com/errors/rate-limit"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(CoinGeckoApiException.class)
    public ProblemDetail handleCoinGeckoApiException(CoinGeckoApiException ex, WebRequest request) {
        log.error("CoinGecko API error: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_GATEWAY,
                "External API error: " + ex.getMessage()
        );
        problemDetail.setTitle("External API Error");
        problemDetail.setType(URI.create("https://api.alpharedge.com/errors/api-error"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        log.error("Validation error: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Validation failed"
        );
        problemDetail.setTitle("Validation Error");
        problemDetail.setType(URI.create("https://api.alpharedge.com/errors/validation-error"));
        problemDetail.setProperty("timestamp", Instant.now());
        ex.getBindingResult().getFieldErrors().forEach(error ->
            problemDetail.setProperty(error.getField(), error.getDefaultMessage())
        );
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex, WebRequest request) {
        log.error("Unexpected error occurred", ex);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred"
        );
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setType(URI.create("https://api.alpharedge.com/errors/internal-server-error"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}
