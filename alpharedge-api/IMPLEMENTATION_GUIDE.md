# AlphaEdge Implementation Guide

## Architecture Overview

AlphaEdge follows a layered architecture pattern with clear separation of concerns:

```
Request Handlers (Controllers)
    ↓
Business Logic (Services)
    ↓
Data Access (Repositories)
    ↓
Database (MongoDB)

External APIs (CoinGecko)
    ↓
API Client with Retry Logic
    ↓
Services
```

## Core Components

### 1. Controllers (`controller/`)

**Purpose**: Handle HTTP requests and responses

- `CoinController`: Cryptocurrency tracking - tracking new coins, retrieving details, getting signals
- `MarketController`: Global market data and trending coins  
- `PortfolioController`: User portfolio management (create, add holdings, get summary)
- `AlertController`: Price alert creation and management

**Key Features**:
- `@Validated` for request validation
- `@RequestHeader("X-User-Id")` for user isolation
- `@Operation` & `@Tag` for OpenAPI documentation
- Proper HTTP status codes (201 for creation, 404 for not found, etc.)

### 2. Services (`service/`)

**Purpose**: Implement core business logic

#### CoinService
```
trackCoin()
├─ calls CoinGeckoClient.fetchCoinDetails()
├─ saves TrackedCoin document
├─ saves initial PriceSnapshot
├─ fetches 90-day chart
├─ computes technical indicators
└─ saves CoinSignal

getAllCoins()
└─ returns all active tracked coins

getCoinDetail()
├─ fetches TrackedCoin
├─ gets latest PriceSnapshot
├─ gets latest CoinSignal
└─ returns combined CoinDetailDTO

compareCoins()
├─ fetches snapshots and signals for each coin
├─ sorts by momentumScore descending
└─ returns comparison list

fetchAndSaveSnapshots()
└─ scheduled job - fetches fresh prices for all tracked coins

computeAndSaveSignals()
└─ scheduled job - computes all technical indicators hourly
```

#### MarketService
```
getGlobalSummary()
└─ calls CoinGeckoClient.fetchGlobal()

getTrending()
└─ calls CoinGeckoClient.fetchTrending()

getRankings()
├─ fetches latest snapshot per tracked coin
├─ sorts by requested criteria (marketCap, price, volatility, etc.)
└─ returns paginated results
```

#### PortfolioService
```
createPortfolio()
└─ creates new empty portfolio for user

addHolding()
├─ verifies user ownership
├─ validates coin exists
├─ creates Holding with auto-generated UUID
└─ saves to portfolio.holdings list

getPortfolioSummary()
├─ iterates all holdings
├─ fetches latest price snapshot for each
├─ calculates: currentValue, costBasis, P&L (USD & %)
├─ tracks bestPerformer and worstPerformer
└─ returns comprehensive portfolio summary
```

#### AlertService
```
createAlert()
├─ validates coin exists
├─ saves PriceAlert with isTriggered=false
└─ returns AlertDTO

checkAndTriggerAlerts()
├─ scheduled job - fetches all active untriggered alerts
├─ for each alert:
│   ├─ fetches latest price
│   ├─ checks if PRICE_ABOVE or PRICE_BELOW condition met
│   ├─ updates isTriggered=true, isActive=false
│   └─ sends email notification
└─ logs triggered alerts

sendAlertEmail()
├─ builds plain-text email body
├─ sends via JavaMailSender
└─ handles failures gracefully
```

### 3. Repositories (`repository/`)

**Purpose**: Data access layer for MongoDB

All extend `MongoRepository<T, String>`:

```java
TrackedCoinRepository
├─ findByCoinId(String coinId) → Optional
└─ findByIsActiveTrue() → List

PriceSnapshotRepository
├─ findTopByCoinIdOrderByFetchedAtDesc(coinId) → Optional (latest)
└─ findByCoinIdAndFetchedAtAfterOrderByFetchedAtDesc(coinId, datetime) → List

PortfolioRepository
├─ findByUserId(userId) → List
└─ findByIdAndUserId(id, userId) → Optional (verfies ownership)

PriceAlertRepository
├─ findByUserIdAndIsActiveTrue(userId) → List
└─ findByIsActiveTrueAndIsTriggeredFalse() → List (for scheduling)

CoinSignalRepository
└─ findTopByCoinIdOrderByComputedAtDesc(coinId) → Optional (latest)
```

### 4. Client (`client/CoinGeckoClient.java`)

**Purpose**: RESTful HTTP calls to CoinGecko with resilience

**Features**:
- `@Retryable(maxAttempts=3, backoff=@Backoff(delay=2000))` on all methods
- Reactive WebClient for non-blocking I/O
- Custom exception handling:
  - `429 → RateLimitException`
  - Other errors → `CoinGeckoApiException`

**Key Methods**:
```java
fetchCoinDetails(coinId)           // GET /coins/{id}
fetchMarketChart(coinId, days)     // GET /coins/{id}/market_chart
fetchTrending()                    // GET /trending
fetchGlobal()                      // GET /global
fetchTopCoins(limit)               // GET /coins/markets
```

### 5. Technical Analysis Engine (`engine/TechnicalAnalysisService.java`)

**Purpose**: Compute all technical indicators from price data

**Indicators**:
- `computeRSI()` - Momentum oscillator (0-100)
- `computeEMA()` - Exponential Moving Average
- `computeMACD()` - Trend-following momentum indicator
- `computeSMA()` - Simple Moving Average
- `computeBollingerBands()` - Volatility indicator
- `computeVolatilityScore()` - Standardized volatility (0-100)
- `computeMomentumScore()` - Weighted price change score
- `generateSignal()` - Multi-condition logic for BUY/HOLD/SELL
- `computeAll()` - Orchestrates all calculations

**Design**:
- All financial values use `BigDecimal` with scale=8
- `RoundingMode.HALF_UP` for rounding
- Error handling returns zero/default values instead of throwing
- Inner classes for complex return types (MACDResult, BollingerBandsResult)

### 6. Mappers (`mapper/`)

**Purpose**: Convert between entities and DTOs (MapStruct)

```java
CoinMapper
├─ TrackedCoin ↔ TrackedCoinDTO
├─ PriceSnapshot ↔ PriceSnapshotDTO
└─ CoinSignal ↔ CoinSignalDTO

PortfolioMapper
├─ Portfolio ↔ PortfolioDTO
└─ Holding ↔ HoldingSummaryDTO

AlertMapper
├─ PriceAlert ↔ AlertDTO
└─ CreateAlertRequest → PriceAlert
```

### 7. Documents (`document/`)

**MongoDB Collections**:

```javascript
// tracked_coins (indexed on coinId: unique)
{
  _id: ObjectId,
  coinId: "bitcoin",
  symbol: "btc",
  name: "Bitcoin",
  isActive: true,
  createdAt: ISODate("2026-04-12T...")
}

// price_snapshots (time-series on coinId)
{
  _id: ObjectId,
  coinId: "bitcoin",
  priceUsd: BigDecimal,
  priceInr: BigDecimal,
  marketCapUsd: BigDecimal,
  volume24hUsd: BigDecimal,
  priceChange24hPercent: BigDecimal,
  priceChange7dPercent: BigDecimal,
  priceChange30dPercent: BigDecimal,
  allTimeHighUsd: BigDecimal,
  allTimeLowUsd: BigDecimal,
  circulatingSupply: BigDecimal,
  totalSupply: BigDecimal,
  marketCapRank: 1,
  fetchedAt: ISODate
}

// portfolios (user-scoped)
{
  _id: ObjectId,
  userId: "user-123",
  name: "My Portfolio",
  holdings: [
    {
      holdingId: UUID,
      coinId: "bitcoin",
      coinName: "Bitcoin",
      symbol: "btc",
      quantity: BigDecimal,
      buyPriceUsd: BigDecimal,
      buyDate: LocalDate,
      notes: "string",
      createdAt: ISODate
    }
  ],
  createdAt: ISODate
}

// price_alerts (user-scoped)
{
  _id: ObjectId,
  userId: "user-123",
  coinId: "bitcoin",
  coinName: "Bitcoin",
  symbol: "btc",
  alertType: "PRICE_ABOVE" | "PRICE_BELOW",
  targetPriceUsd: BigDecimal,
  isTriggered: false,
  isActive: true,
  triggeredAt: null,
  notifyEmail: "user@example.com",
  createdAt: ISODate
}

// coin_signals
{
  _id: ObjectId,
  coinId: "bitcoin",
  rsi: BigDecimal,
  macd: BigDecimal,
  macdSignal: BigDecimal,
  macdHistogram: BigDecimal,
  sma7: BigDecimal,
  sma30: BigDecimal,
  bollingerUpper: BigDecimal,
  bollingerMiddle: BigDecimal,
  bollingerLower: BigDecimal,
  signal: "BUY" | "HOLD" | "SELL",
  strength: "WEAK" | "MODERATE" | "STRONG",
  volatilityScore: BigDecimal,
  momentumScore: BigDecimal,
  computedAt: ISODate
}
```

### 8. Exception Handler (`exception/GlobalExceptionHandler.java`)

**Purpose**: Centralized error handling with RFC 7807 Problem Details

**Handled Exceptions**:
- `CoinNotFoundException` (404)
- `PortfolioNotFoundException` (404)
- `AlertNotFoundException` (404)
- `UnauthorizedException` (403)
- `RateLimitException` (429)
- `CoinGeckoApiException` (502)
- `MethodArgumentNotValidException` (400 with field errors)
- Generic `Exception` (500)

**Response Format**:
```json
{
  "type": "https://api.alpharedge.com/errors/coin-not-found",
  "title": "Coin Not Found",
  "status": 404,
  "detail": "Coin not found: invalid-id",
  "timestamp": "2026-04-12T10:30:00Z"
}
```

### 9. Scheduler (`scheduler/CryptoScheduler.java`)

**Purpose**: Background periodic tasks

```
priceUpdateJob()
├─ Frequency: Every 5 minutes (300000 ms)
├─ Tasks:
│   ├─ CoinService.fetchAndSaveSnapshots()
│   └─ AlertService.checkAndTriggerAlerts()
└─ Logging: "Price update complete"

signalComputeJob()
├─ Frequency: Hourly (cron: "0 0 * * * *")
├─ Task: CoinService.computeAndSaveSignals()
└─ Logging: "Signal computation complete"
```

**Error Handling**: All exceptions caught and logged, jobs continue on failure

### 10. Configuration (`config/`)

#### WebClientConfig
- Creates `WebClient` bean with CoinGecko base URL
- Default `User-Agent: AlphaEdge/1.0` header
- Non-blocking Reactor Netty HTTP client

#### OpenApiConfig
- Configures Swagger UI and OpenAPI documentation
- Title: "AlphaEdge API"
- Serves at `/swagger-ui.html` and `/api-docs`

## Data Flow Examples

### Tracking a New Coin

```
POST /api/v1/coins/track?coinId=bitcoin
    ↓
CoinController.trackCoin()
    ↓
CoinService.trackCoin()
    ├─ Check if already tracked → return if exists
    ├─ CoinGeckoClient.fetchCoinDetails("bitcoin")
    ├─ Save TrackedCoin document
    ├─ Map response to PriceSnapshot
    ├─ Save PriceSnapshot
    ├─ CoinGeckoClient.fetchMarketChart("bitcoin", 90)
    ├─ TechnicalAnalysisService.computeAll(prices, changes...)
    ├─ Save CoinSignal
    └─ Return TrackedCoinDTO
```

### Portfolio Performance Summary

```
GET /api/v1/portfolios/:id/summary?X-User-Id=user-123
    ↓
PortfolioController.getPortfolioSummary()
    ↓
PortfolioService.getPortfolioSummary()
    ├─ Fetch Portfolio by ID+UserID (verify ownership)
    ├─ For each Holding:
    │   ├─ Fetch latest PriceSnapshot
    │   ├─ Calculate currentValue = quantity * currentPrice
    │   ├─ Calculate costBasis = quantity * buyPrice
    │   ├─ Calculate P&L = currentValue - costBasis
    │   ├─ Track best/worst performers
    │   └─ Add HoldingPerformanceDTO to list
    ├─ Sum totals (value, cost, P&L)
    └─ Return PortfolioSummaryDTO
```

### Alert Triggering (Scheduled)

```
CryptoScheduler.priceUpdateJob() [Every 5 minutes]
    ↓
AlertService.checkAndTriggerAlerts()
    ├─ Fetch all active, untriggered alerts
    ├─ For each alert:
    │   ├─ Fetch latest PriceSnapshot for coin
    │   ├─ Compare current price with target
    │   ├─ If condition met (ABOVE/BELOW):
    │   │   ├─ Set isTriggered=true, isActive=false
    │   │   ├─ Set triggeredAt=now()
    │   │   ├─ Save alert
    │   │   └─ AlertService.sendAlertEmail()
    │   │       ├─ Build email body
    │   │       └─ Use JavaMailSender.send()
    │   └─ Log "Alert triggered"
    └─ Log completion
```

## Key Design Patterns

### 1. **Service Layer Pattern**
- All business logic in services
- Controllers only handle HTTP concerns
- Services coordinate repositories and other services

### 2. **Repository Pattern**
- Data access abstraction via Spring Data MongoDB
- Repositories handle all database queries
- Type-safe query methods

### 3. **DTO Pattern**
- Request DTOs for incoming data with `@Valid` annotations
- Response DTOs for outgoing data
- Mappers handle entity ↔ DTO conversions

### 4. **Exception Translation**
- API exceptions caught and translated to app exceptions
- Global exception handler converts to RFC 7807 Problem Details
- Prevents information leakage

### 5. **Resilience Patterns**
- Retry logic with exponential backoff for API calls
- Graceful degradation (return empty/zero on errors)
- Comprehensive logging at each layer

### 6. **Composition Over Inheritance**
- Services composed together (CoinService uses CoinGeckoClient, mapper, repos)
- No deep inheritance hierarchies

## Performance Considerations

1. **BigDecimal for Financial Values**: Avoids floating-point precision errors
2. **Indexed Queries**: MongoDB indexes on frequently queried fields
3. **Paginated Results**: Market rankings support efficient paging
4. **Latest-Record Queries**: Uses `findTopByOrderBy` for single latest record
5. **Scheduled Refresh**: Background jobs prevent N+1 queries
6. **WebClient**: Non-blocking HTTP prevents thread exhaustion

## Security Considerations

1. **User Isolation**: Bearer token/user ID validation (implement in production)
2. **Input Validation**: `@Valid` on all DTOs
3. **Exception Handlers**: Don't expose stack traces
4. **Environment Variables**: Email credentials via env, not in code
5. **MongoDB Injection**: Spring Data prevents query injection
6. **HTTP Headers**: User-Agent on external API calls

## Testing Strategy

- Unit tests for services (mock repositories and clients)
- Integration tests for repositories
- Controller tests for HTTP behavior
- Technical analysis tests for calculation accuracy
- API contract tests via Swagger/OpenAPI

## Future Enhancements

- WebSocket for real-time price updates
- Redis caching for frequently accessed data
- API rate limiting and request throttling
- Advanced logging and metrics (Micrometer)
- Batch processing for large portfolio operations
- Event-driven architecture with message queues
