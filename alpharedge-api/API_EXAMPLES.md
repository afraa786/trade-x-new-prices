# API Examples & Usage Scenarios

## Complete Workflows

### Scenario 1: Building a Crypto Portfolio Tracker

**User Goal**: Create a portfolio, add holdings, and monitor performance

#### Step 1: Create Portfolio
```bash
curl -X POST http://localhost:8080/api/v1/portfolios \
  -H "Content-Type: application/json" \
  -H "X-User-Id: john-doe-123" \
  -d '{
    "name": "My Crypto Investments"
  }'
```

**Response** (201 Created):
```json
{
  "id": "65f8c2a1b9e4d7f8g9h2i3j4",
  "userId": "john-doe-123",
  "name": "My Crypto Investments",
  "holdings": [],
  "createdAt": "2026-04-12T10:15:00"
}
```

#### Step 2: Ensure Coins are Tracked
```bash
# Track Bitcoin
curl -X POST "http://localhost:8080/api/v1/coins/track?coinId=bitcoin"

# Track Ethereum
curl -X POST "http://localhost:8080/api/v1/coins/track?coinId=ethereum"

# Track Cardano
curl -X POST "http://localhost:8080/api/v1/coins/track?coinId=cardano"
```

#### Step 3: Add Holdings to Portfolio
```bash
# Add 0.5 BTC purchased at $40,000
curl -X POST "http://localhost:8080/api/v1/portfolios/65f8c2a1b9e4d7f8g9h2i3j4/holdings" \
  -H "Content-Type: application/json" \
  -H "X-User-Id: john-doe-123" \
  -d '{
    "coinId": "bitcoin",
    "quantity": 0.5,
    "buyPriceUsd": 40000,
    "buyDate": "2024-01-15",
    "notes": "Long-term hold"
  }'

# Add 5 ETH purchased at $2,000
curl -X POST "http://localhost:8080/api/v1/portfolios/65f8c2a1b9e4d7f8g9h2i3j4/holdings" \
  -H "Content-Type: application/json" \
  -H "X-User-Id: john-doe-123" \
  -d '{
    "coinId": "ethereum",
    "quantity": 5,
    "buyPriceUsd": 2000,
    "buyDate": "2024-02-20",
    "notes": "DeFi position"
  }'

# Add 100 ADA purchased at $0.50
curl -X POST "http://localhost:8080/api/v1/portfolios/65f8c2a1b9e4d7f8g9h2i3j4/holdings" \
  -H "Content-Type: application/json" \
  -H "X-User-Id: john-doe-123" \
  -d '{
    "coinId": "cardano",
    "quantity": 100,
    "buyPriceUsd": 0.50,
    "buyDate": "2024-03-10",
    "notes": "Stake for passive income"
  }'
```

#### Step 4: Get Portfolio Performance Summary
```bash
curl http://localhost:8080/api/v1/portfolios/65f8c2a1b9e4d7f8g9h2i3j4/summary \
  -H "X-User-Id: john-doe-123"
```

**Response** (200 OK):
```json
{
  "portfolioId": "65f8c2a1b9e4d7f8g9h2i3j4",
  "portfolioName": "My Crypto Investments",
  "totalValue": 50000,
  "totalCostBasis": 45000,
  "totalPnlUsd": 5000,
  "totalPnlPercent": 11.11,
  "bestPerformer": "bitcoin",
  "bestPerformerGain": 25.0,
  "worstPerformer": "cardano",
  "worstPerformerLoss": 5.0,
  "holdings": [
    {
      "holdingId": "uuid-1",
      "coinId": "bitcoin",
      "symbol": "btc",
      "quantity": 0.5,
      "currentPrice": 50000,
      "currentValue": 25000,
      "costBasis": 20000,
      "pnlUsd": 5000,
      "pnlPercent": 25.0
    },
    {
      "holdingId": "uuid-2",
      "coinId": "ethereum",
      "symbol": "eth",
      "quantity": 5,
      "currentPrice": 3000,
      "currentValue": 15000,
      "costBasis": 10000,
      "pnlUsd": 5000,
      "pnlPercent": 50.0
    },
    {
      "holdingId": "uuid-3",
      "coinId": "cardano",
      "symbol": "ada",
      "quantity": 100,
      "currentPrice": 0.475,
      "currentValue": 47.5,
      "costBasis": 50,
      "pnlUsd": -2.5,
      "pnlPercent": -5.0
    }
  ]
}
```

---

### Scenario 2: Setting Up Price Alert Notifications

**User Goal**: Get notified when BTC reaches $60,000 or ETH drops below $2,500

#### Step 1: Create Price-Above Alert for Bitcoin
```bash
curl -X POST http://localhost:8080/api/v1/alerts \
  -H "Content-Type: application/json" \
  -H "X-User-Id: john-doe-123" \
  -d '{
    "coinId": "bitcoin",
    "alertType": "PRICE_ABOVE",
    "targetPriceUsd": 60000,
    "notifyEmail": "john@example.com"
  }'
```

**Response**:
```json
{
  "id": "alert-btc-60k",
  "userId": "john-doe-123",
  "coinId": "bitcoin",
  "coinName": "Bitcoin",
  "symbol": "btc",
  "alertType": "PRICE_ABOVE",
  "targetPriceUsd": 60000,
  "isTriggered": false,
  "isActive": true,
  "triggeredAt": null,
  "notifyEmail": "john@example.com",
  "createdAt": "2026-04-12T11:00:00"
}
```

#### Step 2: Create Price-Below Alert for Ethereum
```bash
curl -X POST http://localhost:8080/api/v1/alerts \
  -H "Content-Type: application/json" \
  -H "X-User-Id: john-doe-123" \
  -d '{
    "coinId": "ethereum",
    "alertType": "PRICE_BELOW",
    "targetPriceUsd": 2500,
    "notifyEmail": "john@example.com"
  }'
```

#### Step 3: View All Active Alerts
```bash
curl http://localhost:8080/api/v1/alerts \
  -H "X-User-Id: john-doe-123"
```

**Response**:
```json
[
  {
    "id": "alert-btc-60k",
    "userId": "john-doe-123",
    "coinId": "bitcoin",
    "coinName": "Bitcoin",
    "symbol": "btc",
    "alertType": "PRICE_ABOVE",
    "targetPriceUsd": 60000,
    "isTriggered": false,
    "isActive": true,
    "notifyEmail": "john@example.com"
  },
  {
    "id": "alert-eth-2.5k",
    "userId": "john-doe-123",
    "coinId": "ethereum",
    "coinName": "Ethereum",
    "symbol": "eth",
    "alertType": "PRICE_BELOW",
    "targetPriceUsd": 2500,
    "isTriggered": false,
    "isActive": true,
    "notifyEmail": "john@example.com"
  }
]
```

#### Step 4: Alert Triggered Automatically
When price reaches target (via scheduler every 5 minutes):

**Email Sent**:
```
Subject: AlphaEdge Alert — Bitcoin (btc)

Your PRICE_ABOVE alert has been triggered.

Coin: Bitcoin (btc)
Target Price: $60000
Current Price: $61250
Triggered at: 2026-04-12T14:30:00

— AlphaEdge
```

#### Step 5: Deactivate an Alert
```bash
curl -X DELETE http://localhost:8080/api/v1/alerts/alert-btc-60k \
  -H "X-User-Id: john-doe-123"
```

**Response** (204 No Content)

---

### Scenario 3: Analyzing Market Trends & Finding Opportunities

**User Goal**: Identify trending coins and compare technical signals

#### Step 1: Get Global Market Summary
```bash
curl http://localhost:8080/api/v1/market/summary
```

**Response**:
```json
{
  "totalMarketCapUsd": 2500000000000,
  "totalMarketCapInr": 208000000000000,
  "totalVolumeUsd": 150000000000,
  "totalVolumeInr": 12500000000000,
  "btcMarketCapPercent": 45.2,
  "ethMarketCapPercent": 18.5,
  "activeCryptocurrencies": 15000
}
```

#### Step 2: Get Trending Coins
```bash
curl http://localhost:8080/api/v1/market/trending
```

**Response**:
```json
[
  {
    "coinId": "solana",
    "name": "Solana",
    "symbol": "sol",
    "thumb": "https://assets.coingecko.com/...",
    "marketCapRank": 5
  },
  {
    "coinId": "polygon",
    "name": "Polygon",
    "symbol": "matic",
    "thumb": "https://assets.coingecko.com/...",
    "marketCapRank": 11
  }
]
```

#### Step 3: Track a Trending Coin
```bash
curl -X POST "http://localhost:8080/api/v1/coins/track?coinId=solana"
```

#### Step 4: Get Technical Analysis Signal
```bash
curl http://localhost:8080/api/v1/coins/solana/signal
```

**Response**:
```json
{
  "id": "signal-solana",
  "coinId": "solana",
  "rsi": 65.3,
  "macd": 0.055,
  "macdSignal": 0.045,
  "macdHistogram": 0.010,
  "sma7": 138.50,
  "sma30": 135.00,
  "bollingerUpper": 145.00,
  "bollingerMiddle": 140.00,
  "bollingerLower": 135.00,
  "signal": "HOLD",
  "strength": "MODERATE",
  "volatilityScore": 62.5,
  "momentumScore": 35.2,
  "computedAt": "2026-04-12T12:00:00"
}
```

**Interpretation**:
- RSI = 65.3 (approaching overbought, 70+ is overbought)
- MACD > Signal Line (bullish momentum)
- Price above 7-day MA (uptrend)
- Signal = HOLD (moderate strength suggests waiting for confirmation)

#### Step 5: Compare Multiple Coins
```bash
curl "http://localhost:8080/api/v1/coins/compare?ids=bitcoin,ethereum,cardano,solana"
```

**Response**:
```json
[
  {
    "coinId": "ethereum",
    "symbol": "eth",
    "name": "Ethereum",
    "priceUsd": 3000,
    "priceChange24hPercent": 5.2,
    "marketCapUsd": 360000000000,
    "volume24hUsd": 20000000000,
    "rsi": 58.5,
    "signal": "BUY",
    "momentumScore": 45.0
  },
  {
    "coinId": "bitcoin",
    "symbol": "btc",
    "name": "Bitcoin",
    "priceUsd": 50000,
    "priceChange24hPercent": 2.1,
    "marketCapUsd": 1000000000000,
    "volume24hUsd": 35000000000,
    "rsi": 55.0,
    "signal": "HOLD",
    "momentumScore": 25.0
  }
]
```

**Sorted by Momentum**: Ethereum shows strongest momentum (45.0) vs Bitcoin (25.0)

---

### Scenario 4: Accessing Price History & Data

**User Goal**: Get historical price data for analysis

#### Step 1: Get 30-Day Price History
```bash
curl "http://localhost:8080/api/v1/coins/bitcoin/history?days=30"
```

**Response**:
```json
[
  {
    "id": "snap-1",
    "coinId": "bitcoin",
    "priceUsd": 52000,
    "priceInr": 4331000,
    "marketCapUsd": 1020000000000,
    "volume24hUsd": 32000000000,
    "priceChange24hPercent": 1.5,
    "priceChange7dPercent": 5.0,
    "priceChange30dPercent": 8.2,
    "allTimeHighUsd": 69000,
    "allTimeLowUsd": 100,
    "marketCapRank": 1,
    "fetchedAt": "2026-04-12T09:00:00"
  },
  {
    "id": "snap-2",
    "coinId": "bitcoin",
    "priceUsd": 51500,
    "priceInr": 4290000,
    "marketCapUsd": 1008000000000,
    "volume24hUsd": 31000000000,
    "priceChange24hPercent": -0.95,
    "priceChange7dPercent": 3.7,
    "priceChange30dPercent": 6.5,
    "allTimeHighUsd": 69000,
    "allTimeLowUsd": 100,
    "marketCapRank": 1,
    "fetchedAt": "2026-04-11T09:00:00"
  }
]
```

#### Step 2: Get Live Price Only
```bash
curl http://localhost:8080/api/v1/coins/bitcoin/price
```

**Response** (freshest price from API):
```json
{
  "coinId": "bitcoin",
  "priceUsd": 52150,
  "priceInr": 4343000,
  "marketCapUsd": 1023000000000,
  "volume24hUsd": 33000000000,
  "priceChange24hPercent": 2.0,
  "priceChange7dPercent": 5.5,
  "priceChange30dPercent": 8.8,
  "marketCapRank": 1,
  "fetchedAt": "2026-04-12T14:35:00"
}
```

#### Step 3: Get Ranked Coins with Sorting
```bash
# Top 10 by market cap
curl "http://localhost:8080/api/v1/market/rankings?sortBy=marketCap&order=desc&page=0&size=10"

# Top gainers (24h)
curl "http://localhost:8080/api/v1/market/rankings?sortBy=priceChange24h&order=desc&page=0&size=10"

# Sorted by volume
curl "http://localhost:8080/api/v1/market/rankings?sortBy=volume24h&order=desc&page=0&size=10"
```

---

## Error Handling Examples

### Coin Not Found (404)
```bash
curl http://localhost:8080/api/v1/coins/invalid-coin-id
```

**Response** (404 Not Found):
```json
{
  "type": "https://api.alpharedge.com/errors/coin-not-found",
  "title": "Coin Not Found",
  "status": 404,
  "detail": "Coin not found: invalid-coin-id",
  "timestamp": "2026-04-12T15:00:00Z"
}
```

### Validation Error (400)
```bash
curl -X POST http://localhost:8080/api/v1/portfolios \
  -H "Content-Type: application/json" \
  -H "X-User-Id: user-123" \
  -d '{
    "name": ""  # Empty name - invalid
  }'
```

**Response** (400 Bad Request):
```json
{
  "type": "https://api.alpharedge.com/errors/validation-error",
  "title": "Validation Error",
  "status": 400,
  "detail": "Validation failed",
  "timestamp": "2026-04-12T15:01:00Z",
  "name": "Portfolio name is required"
}
```

### Unauthorized (403)
```bash
# Trying to access portfolio of another user
curl http://localhost:8080/api/v1/portfolios/other-user-portfolio/summary \
  -H "X-User-Id: john-doe-123"
```

**Response** (403 Forbidden):
```json
{
  "type": "https://api.alpharedge.com/errors/unauthorized",
  "title": "Unauthorized Access",
  "status": 403,
  "detail": "Portfolio not found or access denied",
  "timestamp": "2026-04-12T15:02:00Z"
}
```

### Rate Limit (429)
```bash
# After many rapid requests to CoinGecko API
curl http://localhost:8080/api/v1/coins/bitcoin
```

**Response** (429 Too Many Requests):
```json
{
  "type": "https://api.alpharedge.com/errors/rate-limit",
  "title": "Rate Limit Exceeded",
  "status": 429,
  "detail": "CoinGecko API rate limit exceeded",
  "timestamp": "2026-04-12T15:03:00Z"
}
```

---

## Integration Examples

### JavaScript/Node.js
```javascript
const axios = require('axios');

async function getPortfolioSummary(portfolioId, userId) {
  const response = await axios.get(
    `http://localhost:8080/api/v1/portfolios/${portfolioId}/summary`,
    {
      headers: {
        'X-User-Id': userId
      }
    }
  );
  console.log('Portfolio Summary:', response.data);
  return response.data;
}

async function trackCoin(coinId) {
  const response = await axios.post(
    `http://localhost:8080/api/v1/coins/track?coinId=${coinId}`
  );
  console.log('Tracked Coin:', response.data);
  return response.data;
}
```

### Python
```python
import requests
import json

BASE_URL = "http://localhost:8080/api/v1"
USER_ID = "user-123"

def create_alert(coin_id, alert_type, target_price, email):
    headers = {"X-User-Id": USER_ID}
    data = {
        "coinId": coin_id,
        "alertType": alert_type,
        "targetPriceUsd": target_price,
        "notifyEmail": email
    }
    response = requests.post(
        f"{BASE_URL}/alerts",
        json=data,
        headers=headers
    )
    return response.json()

def get_coin_signal(coin_id):
    response = requests.get(f"{BASE_URL}/coins/{coin_id}/signal")
    return response.json()

# Usage
alert = create_alert("bitcoin", "PRICE_ABOVE", 60000, "user@example.com")
signal = get_coin_signal("bitcoin")
print(json.dumps(signal, indent=2))
```

---

## Batch Operations

### Import Historical Portfolio
```bash
#!/bin/bash
PORTFOLIO_ID="my-portfolio-id"
USER_ID="john-doe-123"

# Create portfolio
PORTFOLIO=$(curl -X POST http://localhost:8080/api/v1/portfolios \
  -H "Content-Type: application/json" \
  -H "X-User-Id: $USER_ID" \
  -d '{"name":"Imported Portfolio"}')

PORTFOLIO_ID=$(echo $PORTFOLIO | jq -r '.id')

# Add multiple holdings
holdings=(
  '{"coinId":"bitcoin","quantity":0.5,"buyPriceUsd":40000,"buyDate":"2024-01-15"}'
  '{"coinId":"ethereum","quantity":5,"buyPriceUsd":2000,"buyDate":"2024-02-20"}'
  '{"coinId":"cardano","quantity":100,"buyPriceUsd":0.5,"buyDate":"2024-03-10"}'
)

for holding in "${holdings[@]}"; do
  curl -X POST "http://localhost:8080/api/v1/portfolios/$PORTFOLIO_ID/holdings" \
    -H "Content-Type: application/json" \
    -H "X-User-Id: $USER_ID" \
    -d "$holding"
done

echo "Portfolio imported successfully"
```
