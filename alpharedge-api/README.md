# AlphaEdge - Crypto Intelligence Platform REST API

## Project Overview

AlphaEdge is a production-grade Spring Boot 3.x REST API for cryptocurrency intelligence, tracking, and portfolio management. It integrates with the CoinGecko API to provide real-time market data, technical analysis signals, and portfolio performance tracking.

## Technology Stack

- **Framework**: Spring Boot 3.3.5
- **Java**: 21
- **Database**: MongoDB (with embedded MongoDB for development)
- **Build Tools**: Maven 3.x
- **Key Dependencies**:
  - Spring WebFlux (reactive HTTP client)
  - Spring Data MongoDB
  - MapStruct (object mapping)
  - Lombok (code generation)
  - SpringDoc OpenAPI (Swagger/OpenAPI 3.0)
  - Spring Mail (email notifications)
  - Spring Retry (resilient API calls)

## Project Structure

```
alpharedge-api/
├── pom.xml
├── README.md
├── src/
│   ├── main/
│   │   ├── java/com/alpharedge/
│   │   │   ├── AlphaEdgeApplication.java              # Main entry point
│   │   │   ├── client/
│   │   │   │   └── CoinGeckoClient.java               # CoinGecko API client with retry logic
│   │   │   ├── config/
│   │   │   │   ├── WebClientConfig.java               # WebClient beans for reactive HTTP
│   │   │   │   └── OpenApiConfig.java                 # Swagger/OpenAPI configuration
│   │   │   ├── controller/
│   │   │   │   ├── CoinController.java                # Coin tracking endpoints
│   │   │   │   ├── MarketController.java              # Global market & trending endpoints
│   │   │   │   ├── PortfolioController.java           # Portfolio management endpoints
│   │   │   │   └── AlertController.java               # Price alert endpoints
│   │   │   ├── document/                              # MongoDB document models
│   │   │   │   ├── TrackedCoin.java
│   │   │   │   ├── PriceSnapshot.java
│   │   │   │   ├── Portfolio.java
│   │   │   │   ├── Holding.java (embedded)
│   │   │   │   ├── PriceAlert.java
│   │   │   │   └── CoinSignal.java
│   │   │   ├── dto/                                   # Data Transfer Objects
│   │   │   │   ├── request/                           # Request DTOs
│   │   │   │   ├── response/                          # Response DTOs
│   │   │   │   └── coingecko/                         # CoinGecko API response DTOs
│   │   │   ├── engine/
│   │   │   │   └── TechnicalAnalysisService.java      # Technical analysis calculations
│   │   │   ├── exception/                             # Custom exception handlers
│   │   │   │   └── GlobalExceptionHandler.java        # RFC 7807 problem details handler
│   │   │   ├── mapper/                                # MapStruct entity mappers
│   │   │   ├── repository/                            # Spring Data MongoDB repositories
│   │   │   ├── scheduler/
│   │   │   │   └── CryptoScheduler.java               # Background scheduled tasks
│   │   │   └── service/                               # Business logic services
│   │   │       ├── CoinService.java
│   │   │       ├── MarketService.java
│   │   │       ├── PortfolioService.java
│   │   │       └── AlertService.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/alpharedge/
│           └── AlphaEdgeApplicationTests.java
```

## Key Features

### 1. Cryptocurrency Tracking
- Track any cryptocurrency available on CoinGecko
- Automatic snapshot collection of price and market data
- Historical price data retrieval

### 2. Technical Analysis
- RSI (Relative Strength Index)
- MACD (Moving Average Convergence Divergence)
- SMA (Simple Moving Average) - 7 and 30 period
- Bollinger Bands
- Volatility Score
- Momentum Score
- Automated BUY/HOLD/SELL signals with strength indicators

### 3. Portfolio Management
- Create and manage multiple portfolios
- Add/remove cryptocurrency holdings
- Track cost basis and buy dates
- Real-time P&L calculations in USD
- Portfolio performance summary with best/worst performers
- Holdings performance metrics

### 4. Price Alerts
- Set price triggers (above/below targets)
- Email notifications when alerts trigger
- Active/inactive alert management
- Per-user alert isolation

### 5. Market Intelligence
- Global market summary (total market cap, volume, dominance)
- Trending cryptocurrencies
- Ranked coin listings with sorting options
- Coin comparison tool

### 6. Scheduler & Background Jobs
- **Price Update Job** (every 5 minutes):
  - Fetches latest prices for all tracked coins
  - Checks and triggers price alerts
  
- **Signal Computation Job** (hourly):
  - Computes all technical indicators
  - Updates trading signals

## Getting Started

### Prerequisites
- Java 21 JDK
- Maven 3.6+
- MongoDB (optional for dev - embedded MongoDB is configured)
- SMTP credentials for email alerts (Gmail recommended)

### Installation & Setup

1. **Clone and navigate to project**:
   ```bash
   cd alpharedge-api
   ```

2. **Build the project**:
   ```bash
   mvn clean install
   ```

3. **Set environment variables** (for email functionality):
   ```bash
   export MAIL_USERNAME=your-email@gmail.com
   export MAIL_PASSWORD=your-app-specific-password
   ```

4. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```

5. **Access the API**:
   - API Base URL: `http://localhost:8080`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`
   - OpenAPI JSON: `http://localhost:8080/api-docs`

## API Endpoints

### Coins (`/api/v1/coins`)
- `POST /track?coinId=bitcoin` - Track new coin
- `GET /` - List all tracked coins
- `GET /{coinId}` - Get coin details with latest signal
- `GET /{coinId}/history?days=30` - Price history
- `GET /{coinId}/signal` - Latest technical signal
- `GET /{coinId}/price` - Live price snapshot
- `GET /compare?ids=bitcoin,ethereum` - Compare coins

### Market (`/api/v1/market`)
- `GET /summary` - Global market data
- `GET /trending` - Top trending coins
- `GET /rankings?sortBy=marketCap&order=desc&page=0&size=20` - Ranked coins

### Portfolios (`/api/v1/portfolios`)
- `POST /` - Create portfolio
- `GET /` - Get user portfolios
- `POST /{id}/holdings` - Add holding
- `DELETE /{id}/holdings/{holdingId}` - Remove holding
- `GET /{id}/summary` - Portfolio performance summary
- `GET /{id}/holdings` - List holdings

### Alerts (`/api/v1/alerts`)
- `POST /` - Create price alert
- `GET /` - Get user alerts
- `DELETE /{id}` - Deactivate alert
- `PATCH /{id}/deactivate` - Deactivate (alternate)

**Note**: Portfolio and Alert endpoints require `X-User-Id` header for user isolation.

## Configuration

Edit `src/main/resources/application.properties`:

```properties
spring.application.name=alpharedge-api
server.port=8080
spring.data.mongodb.database=alpharedge
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}
logging.level.com.alpharedge=DEBUG
```

## Technical Indicators Implementation

### RSI (Relative Strength Index)
- Period: 14 (configurable)
- Range: 0-100
- Buy Signal: RSI < 30
- Sell Signal: RSI > 70

### MACD
- Fast EMA: 12 period
- Slow EMA: 26 period
- Signal Line: 9 period EMA of MACD
- Buy Signal: MACD > Signal Line
- Sell Signal: MACD < Signal Line

### Bollinger Bands
- Period: 20
- Standard Deviations: 2
- Buy Signal: Price < Lower Band
- Sell Signal: Price > Upper Band

### Signal Generation Logic
- **Strong Signal**: All 3 conditions met (RSI + Price + MACD)
- **Moderate Signal**: 2 of 3 conditions met
- **Weak Signal**: 1 of 3 conditions met

## Error Handling

All errors follow RFC 7807 Problem Details specification:

```json
{
  "type": "https://api.alpharedge.com/errors/coin-not-found",
  "title": "Coin Not Found",
  "status": 404,
  "detail": "Coin not found: invalid-id",
  "timestamp": "2026-04-12T10:30:00Z"
}
```

## Database Models

### TrackedCoin
- Unique index on `coinId`
- Tracks which coins are actively monitored

### PriceSnapshot
- Time-series data of price and market metrics
- Indexed on `coinId` and `fetchedAt`

### Portfolio
- User-specific portfolio (isolated by userId)
- Contains embedded `Holding` documents

### PriceAlert
- User-specific price triggers
- Isolated by userId
- Tracks trigger state and email delivery

### CoinSignal
- Latest calculated technical analysis per coin
- Updated hourly by scheduler

## Building for Production

### Maven Build with JAR
```bash
mvn clean package
java -jar target/alpharedge-api-0.0.1-SNAPSHOT.jar
```

### Docker Build (if Dockerfile created)
```bash
docker build -t alpharedge-api:1.0 .
docker run -e MAIL_USERNAME=... -e MAIL_PASSWORD=... -p 8080:8080 alpharedge-api:1.0
```

### MongoDB Connection
For production, configure external MongoDB:
```properties
spring.data.mongodb.uri=mongodb://user:password@host:27017/alpharedge
```

## Scheduled Jobs

### Price Update Job
- **Frequency**: Every 5 minutes (300000 ms)
- **Tasks**:
  - Fetches latest price snapshots from CoinGecko
  - Triggers price alerts if thresholds met
  - Sends email notifications

### Signal Computation Job
- **Frequency**: Every hour (0 0 * * * *)
- **Tasks**:
  - Fetches 90-day market chart for each coin
  - Computes all technical indicators
  - Updates trading signals with strength

## Performance Considerations

- **BigDecimal Scale**: All financial calculations use scale=8 with HALF_UP rounding
- **Retry Logic**: 3 attempts with 2-second backoff for CoinGecko API calls
- **Rate Limiting**: Handles 429 responses with RateLimitException
- **Pagination**: Market rankings support efficient paging

## Security

- User isolation via `X-User-Id` header (implement authentication layer in production)
- Validated input with `@Valid` and `@Validated`
- Exception handlers prevent information leakage
- Email credentials via environment variables

## Development Notes

- **Annotation Processing**: MapStruct and Lombok via Maven compiler plugin
- **Reactive HTTP**: WebFlux WebClient for non-blocking API calls
- **Logging**: SLF4J with Logback configured for DEBUG level in dev
- **MongoDB**: Embedded MongoDB for local development

## Testing

Run tests with:
```bash
mvn test
```

## Future Enhancements

- WebSocket support for real-time price updates
- User authentication/authorization layer
- Advanced portfolio analytics and backtesting
- Additional technical indicators (Stochastic, ATR, etc.)
- User preferences and notification settings
- API rate limiting and throttling
- Caching layer for frequently accessed data
- Multi-currency support
- Historical signal accuracy tracking

## License

Proprietary - AlphaEdge

## Support

For issues and questions, please contact: support@alpharedge.com
