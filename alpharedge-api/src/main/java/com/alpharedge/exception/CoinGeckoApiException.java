package com.alpharedge.exception;

public class CoinGeckoApiException extends RuntimeException {
    public CoinGeckoApiException(String message) {
        super(message);
    }

    public CoinGeckoApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
