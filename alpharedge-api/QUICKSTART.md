# Quick Start Guide

## Prerequisites

- Java 21 JDK
- Maven 3.6+
- Git
- IDE (IntelliJ IDEA recommended, or VS Code with Java Extensions)
- MongoDB (optional - embedded MongoDB included in dev)

## 5-Minute Setup

### 1. Clone & Build
```bash
cd alpharedge-api
mvn clean install
```

### 2. Set Environment Variables (optional, needed only for email alerts)
```bash
# Linux/Mac
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-app-password

# Windows (PowerShell)
$env:MAIL_USERNAME="your-email@gmail.com"
$env:MAIL_PASSWORD="your-app-password"
```

### 3. Start the Application
```bash
mvn spring-boot:run
```

### 4. Verify It's Running
- Open browser: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/api-docs

## Using Docker (Alternative)

```bash
# Build image
docker build -t alpharedge-api:1.0 .

# Run with docker-compose (includes MongoDB)
docker-compose up -d

# Check logs
docker-compose logs -f alpharedge-api
```

## API Usage Examples

### 1. Track a Coin
```bash
curl -X POST "http://localhost:8080/api/v1/coins/track?coinId=bitcoin"
```

### 2. Get Global Market Summary
```bash
curl http://localhost:8080/api/v1/market/summary
```

### 3. Get Trending Coins
```bash
curl http://localhost:8080/api/v1/market/trending
```

### 4. Create a Portfolio
```bash
curl -X POST http://localhost:8080/api/v1/portfolios \
  -H "Content-Type: application/json" \
  -H "X-User-Id: user-123" \
  -d '{"name":"My Portfolio"}'
```

### 5. Get Coin Details
```bash
curl http://localhost:8080/api/v1/coins/bitcoin
```

### 6. Get Coin Signal
```bash
curl http://localhost:8080/api/v1/coins/bitcoin/signal
```

### 7. Create Price Alert
```bash
curl -X POST http://localhost:8080/api/v1/alerts \
  -H "Content-Type: application/json" \
  -H "X-User-Id: user-123" \
  -d '{
    "coinId": "bitcoin",
    "alertType": "PRICE_ABOVE",
    "targetPriceUsd": 50000,
    "notifyEmail": "user@example.com"
  }'
```

### 8. Add Holding to Portfolio
```bash
curl -X POST http://localhost:8080/api/v1/portfolios/{id}/holdings \
  -H "Content-Type: application/json" \
  -H "X-User-Id: user-123" \
  -d '{
    "coinId": "bitcoin",
    "quantity": 0.5,
    "buyPriceUsd": 40000,
    "buyDate": "2024-01-15",
    "notes": "Long-term hold"
  }'
```

### 9. Get Portfolio Summary
```bash
curl http://localhost:8080/api/v1/portfolios/{id}/summary \
  -H "X-User-Id: user-123"
```

### 10. Compare Coins
```bash
curl "http://localhost:8080/api/v1/coins/compare?ids=bitcoin,ethereum,cardano"
```

## Development Workflow

### IDE Setup (IntelliJ IDEA)

1. **Import as Maven Project**
   - File → Open → Select `pom.xml`
   - IntelliJ auto-configures dependencies

2. **Enable Annotation Processing**
   - Settings → Build, Execution, Deployment → Compiler → Annotation Processors
   - Enable annotation processing: ✓
   - Auto-detect: ✓

3. **Configure Run Configuration**
   - Run → Edit Configurations → Add → Maven
   - Working directory: `$PROJECT_DIR$`
   - Command: `spring-boot:run`
   - Environment variables: Set MAIL_USERNAME, MAIL_PASSWORD

### IDE Setup (VS Code)

1. **Install Extensions**
   - Extension Pack for Java (Microsoft)
   - Spring Boot Extension Pack (Pivotal)
   - REST Client (optional, for testing)

2. **Open Folder**
   - File → Open Folder → Select `alpharedge-api`

3. **Debug Configuration** (in `.vscode/launch.json`)
   ```json
   {
     "version": "0.2.0",
     "configurations": [
       {
         "type": "java",
         "name": "AlphaEdgeApplication",
         "request": "launch",
         "mainClass": "com.alpharedge.AlphaEdgeApplication",
         "projectName": "alpharedge-api",
         "cwd": "${workspaceFolder}",
         "console": "integratedTerminal",
         "env": {
           "MAIL_USERNAME": "your-email@gmail.com",
           "MAIL_PASSWORD": "your-app-password"
         }
       }
     ]
   }
   ```

## Common Development Tasks

### Build the Project
```bash
mvn clean package
```

### Run Tests
```bash
mvn test
```

### Run Specific Test
```bash
mvn test -Dtest=CoinServiceTest
```

### Generate JavaDoc
```bash
mvn javadoc:javadoc
open target/site/apidocs/index.html
```

### Check for POM Issues
```bash
mvn dependency:analyze
mvn dependency:tree
```

### Format Code (optional - requires plugin)
```bash
mvn spotless:apply
```

### Run with Specific Profile
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

## MongoDB Management

### Using MongoDB CLI (if local MongoDB running)
```bash
# Show all databases
mongosh
> show dbs

# Use alpharedge
> use alpharedge
> show collections

# Query tracked coins
> db.tracked_coins.find({})

# Count price snapshots
> db.price_snapshots.countDocuments()
```

### Using MongoDB Compass (GUI)
- Download: https://www.mongodb.com/products/compass
- Connection: `mongodb://localhost:27017`
- Select `alpharedge` database

## Troubleshooting

### Port 8080 Already in Use
```bash
# Find process using port 8080
lsof -i :8080  # Mac/Linux
netstat -ano | findstr :8080  # Windows

# Kill process or use different port
export SERVER_PORT=8081
mvn spring-boot:run
```

### Maven Compilation Errors
```bash
# Clean and rebuild
mvn clean compile

# Force dependency update
mvn clean install -U

# Check for conflicts
mvn dependency:tree -Dverbose
```

### MongoDB Connection Issues
```bash
# Check if MongoDB is running
ps aux | grep mongod  # Mac/Linux

# Check connection in logs
tail -f logs/spring.log | grep -i mongodb
```

### Email Configuration Issues
- Ensure Gmail App Password (not regular password) is used
- Enable "Less secure app access" if using Gmail
- Check email logs in `logs/` directory

## Monitoring & Debugging

### View Application Logs
```bash
# Real-time logs
tail -f logs/spring.log

# Filter by level
grep ERROR logs/spring.log

# Search for specific component
grep "CoinService" logs/spring.log
```

### Enable Debug Logging
Update `application.properties`:
```properties
logging.level.com.alpharedge=TRACE
logging.level.org.springframework.web=DEBUG
```

### Health Check
```bash
curl http://localhost:8080/actuator/health
```

### View All Scheduled Jobs
The scheduler shows logs:
```
[scheduler] Starting price update job
[scheduler] Price update complete
[scheduler] Starting signal computation job
```

## Database Initialization

### First-Time Setup
1. Application starts → embedded MongoDB initializes
2. Collections created automatically on first write
3. Indexes created by Spring Data annotations

### Reset Database (Development Only)
```bash
# Delete MongoDB data
rm -rf /path/to/embedded-mongo-data

# Restart application
mvn spring-boot:run
```

## Testing with cURL or Postman

### Create Postman Collection
1. Import OpenAPI: `http://localhost:8080/api-docs`
2. Postman automatically creates collection
3. Set `X-User-Id` header in environment: `user-123`

### Example cURL Tests
```bash
# Track Bitcoin
curl -v -X POST "http://localhost:8080/api/v1/coins/track?coinId=bitcoin"

# Get all tracked coins
curl http://localhost:8080/api/v1/coins

# Get Bitcoin details with signal
curl http://localhost:8080/api/v1/coins/bitcoin

# Compare coins
curl "http://localhost:8080/api/v1/coins/compare?ids=bitcoin,ethereum"

# Get market summary
curl http://localhost:8080/api/v1/market/summary

# Get rankings (paginated)
curl "http://localhost:8080/api/v1/market/rankings?sortBy=marketCap&order=desc&page=0&size=10"

# Create portfolio
curl -X POST http://localhost:8080/api/v1/portfolios \
  -H "X-User-Id: user-123" \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Portfolio"}'
```

## Next Steps

1. **Read** [IMPLEMENTATION_GUIDE.md](IMPLEMENTATION_GUIDE.md) for architecture details
2. **Explore** [README.md](README.md) for full API documentation
3. **Review** source code in `src/main/java/com/alpharedge/`
4. **Test** each endpoint via Swagger UI or cURL
5. **Extend** functionality as needed

## Common Questions

**Q: How do I add a new cryptocurrency?**
A: Call `POST /api/v1/coins/track?coinId=ethereum` with any CoinGecko coin ID

**Q: Where are prices fetched from?**
A: CoinGecko free API (https://coingecko.com/en/api)

**Q: How often are prices updated?**
A: Every 5 minutes via CryptoScheduler.priceUpdateJob()

**Q: How often are signals computed?**
A: Hourly via CryptoScheduler.signalComputeJob()

**Q: Can I use external MongoDB instead of embedded?**
A: Yes, set `spring.data.mongodb.uri` in application.properties

**Q: How do I authenticate users in production?**
A: Implement JWT/OAuth in PortfolioController and AlertController (currently uses header)

**Q: Can I deploy to AWS Lambda?**
A: Requires custom setup; consider Fargate or EC2 instead

## Support

- Check logs: `tail -f logs/spring.log`
- Review docs: [README.md](README.md) | [IMPLEMENTATION_GUIDE.md](IMPLEMENTATION_GUIDE.md)
- Check CoinGecko API status: https://status.coingecko.com/
- File issues in Git with detailed logs and reproduction steps
