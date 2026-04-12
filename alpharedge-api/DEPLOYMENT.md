# Production Deployment Guide

## Pre-Deployment Checklist

- [ ] Code reviewed and tested
- [ ] All tests passing locally
- [ ] Environment variables configured
- [ ] MongoDB production database ready
- [ ] SMTP credentials configured (Gmail/SendGrid)
- [ ] SSL/TLS certificates obtained
- [ ] Firewall rules configured
- [ ] Monitoring/logging infrastructure set up
- [ ] Backup strategy in place
- [ ] Disaster recovery plan documented
- [ ] Load balancer configured (if needed)
- [ ] CDN configured (if needed)

## Environment Setup

### 1. Production Properties File

Create `src/main/resources/application-prod.properties`:

```properties
spring.application.name=alpharedge-api
server.port=8080
spring.profiles.active=prod

# MongoDB Production
spring.data.mongodb.uri=mongodb+srv://${MONGO_USER}:${MONGO_PASSWORD}@${MONGO_CLUSTER}.mongodb.net/${MONGO_DB}?retryWrites=true&w=majority
spring.data.mongodb.auto-index-creation=false

# Mail Configuration
spring.mail.host=${MAIL_HOST}
spring.mail.port=${MAIL_PORT}
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}
spring.mail.from=${MAIL_FROM}

# API Documentation (disabled)
springdoc.swagger-ui.enabled=false

# Logging
logging.level.root=WARN
logging.level.com.alpharedge=INFO
logging.file.name=/var/log/alpharedge/alpharedge-prod.log
logging.file.max-size=100MB
logging.file.max-history=30

# Server Config
server.error.include-message=never
server.error.include-stacktrace=never
server.servlet.session.timeout=30m
server.tomcat.threads.max=200
```

### 2. Environment Variables

```bash
export SPRING_PROFILES_ACTIVE=prod

# MongoDB Atlas
export MONGO_USER=alpharedge-prod-user
export MONGO_PASSWORD=<secure-password>
export MONGO_CLUSTER=cluster-prod
export MONGO_DB=alpharedge

# Email (SendGrid recommended for production)
export MAIL_HOST=smtp.sendgrid.net
export MAIL_PORT=587
export MAIL_USERNAME=apikey
export MAIL_PASSWORD=SG.xxxxxxxxxxxxxxxxxxxxxxxx
export MAIL_FROM=noreply@alpharedge.com

# Server
export SERVER_PORT=8080
export SERVER_SERVLET_CONTEXT_PATH=/api
```

## Deployment Options

### Option 1: Docker Compose (Recommended for Small Scale)

#### Prerequisites
- Docker & Docker Compose installed
- MongoDB Atlas account or external MongoDB running

#### Build & Push to Registry
```bash
# Build image
docker build -t alpharedge-api:1.0 .

# Tag for registry (e.g., Docker Hub)
docker tag alpharedge-api:1.0 myregistry/alpharedge-api:1.0

# Push to registry
docker push myregistry/alpharedge-api:1.0
```

#### Deploy
```bash
# Update docker-compose.yml with environment variables
cat > .env.prod <<EOF
SPRING_PROFILES_ACTIVE=prod
MONGO_USER=alpharedge-prod-user
MONGO_PASSWORD=<password>
MONGO_CLUSTER=cluster-prod
MONGO_DB=alpharedge
MAIL_HOST=smtp.sendgrid.net
MAIL_PORT=587
MAIL_USERNAME=apikey
MAIL_PASSWORD=SG.xxx
MAIL_FROM=noreply@alpharedge.com
EOF

# Start containers
docker-compose -f docker-compose.yml --env-file .env.prod up -d

# Check status
docker-compose ps
docker-compose logs -f alpharedge-api
```

#### Update Process
```bash
# Build new version
docker build -t myregistry/alpharedge-api:2.0 .
docker push myregistry/alpharedge-api:2.0

# Update deployment
docker-compose pull
docker-compose up -d

# Verify
docker-compose ps
```

---

### Option 2: AWS EC2/ECS/Fargate

#### Build Docker Image
```bash
docker build -t alpharedge-api:1.0 .
docker tag alpharedge-api:1.0 <account-id>.dkr.ecr.<region>.amazonaws.com/alpharedge-api:1.0
aws ecr get-login-password --region <region> | docker login --username AWS --password-stdin <account-id>.dkr.ecr.<region>.amazonaws.com
docker push <account-id>.dkr.ecr.<region>.amazonaws.com/alpharedge-api:1.0
```

#### Create ECS Task Definition
```json
{
  "family": "alpharedge-api",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "512",
  "memory": "1024",
  "containerDefinitions": [
    {
      "name": "alpharedge-api",
      "image": "<account-id>.dkr.ecr.<region>.amazonaws.com/alpharedge-api:1.0",
      "portMappings": [
        {
          "containerPort": 8080,
          "hostPort": 8080,
          "protocol": "tcp"
        }
      ],
      "environment": [
        {
          "name": "SPRING_PROFILES_ACTIVE",
          "value": "prod"
        }
      ],
      "secrets": [
        {
          "name": "MONGO_USER",
          "valueFrom": "arn:aws:secretsmanager:<region>:<account-id>:secret:alpharedge/mongo-user"
        },
        {
          "name": "MONGO_PASSWORD",
          "valueFrom": "arn:aws:secretsmanager:<region>:<account-id>:secret:alpharedge/mongo-password"
        },
        {
          "name": "MAIL_PASSWORD",
          "valueFrom": "arn:aws:secretsmanager:<region>:<account-id>:secret:alpharedge/mail-password"
        }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/alpharedge-api",
          "awslogs-region": "<region>",
          "awslogs-stream-prefix": "ecs"
        }
      }
    }
  ]
}
```

#### Create ECS Service
```bash
aws ecs create-service \
  --cluster alpharedge-prod \
  --service-name alpharedge-api \
  --task-definition alpharedge-api \
  --desired-count 2 \
  --launch-type FARGATE \
  --network-configuration "awsvpcConfiguration={subnets=[subnet-xxx],securityGroups=[sg-xxx],assignPublicIp=DISABLED}" \
  --load-balancers targetGroupArn=arn:aws:elasticloadbalancing:...,containerName=alpharedge-api,containerPort=8080
```

---

### Option 3: Kubernetes (Helm)

#### Create Helm Values
```yaml
# values.yaml
replicaCount: 3

image:
  repository: myregistry/alpharedge-api
  tag: "1.0"
  pullPolicy: IfNotPresent

resources:
  requests:
    cpu: 500m
    memory: 1Gi
  limits:
    cpu: 1000m
    memory: 2Gi

service:
  type: LoadBalancer
  port: 80
  targetPort: 8080

ingress:
  enabled: true
  className: nginx
  hosts:
    - host: api.alpharedge.com
      paths:
        - path: /
          pathType: Prefix

env:
  SPRING_PROFILES_ACTIVE: prod
  MONGO_CLUSTER: cluster-prod
  MONGO_DB: alpharedge

secrets:
  MONGO_USER: alpharedge-prod-user
  MONGO_PASSWORD: <from-secret>
  MAIL_PASSWORD: <from-secret>

mongodb:
  enabled: false  # Use external MongoDB Atlas
```

#### Deploy with Helm
```bash
# Install
helm install alpharedge ./alpharedge-helm -f values.yaml -n alpharedge --create-namespace

# Verify
kubectl get pods -n alpharedge
kubectl logs -n alpharedge -f <pod-name>

# Upgrade
helm upgrade alpharedge ./alpharedge-helm -f values.yaml -n alpharedge
```

---

## Post-Deployment Configuration

### 1. Nginx Reverse Proxy (Recommended)

```nginx
upstream alpharedge_backend {
    server localhost:8080;
    server localhost:8081;
    server localhost:8082;
    keepalive 64;
}

server {
    listen 443 ssl http2;
    server_name api.alpharedge.com;

    ssl_certificate /etc/ssl/certs/api.alpharedge.com.crt;
    ssl_certificate_key /etc/ssl/private/api.alpharedge.com.key;

    client_max_body_size 10M;
    client_body_timeout 30s;
    client_header_timeout 30s;

    gzip on;
    gzip_types application/json;
    gzip_min_length 1000;

    location / {
        proxy_pass http://alpharedge_backend;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;

        # Timeouts
        proxy_connect_timeout 10s;
        proxy_send_timeout 30s;
        proxy_read_timeout 30s;
    }

    # Hide version info
    server_tokens off;

    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Referrer-Policy "strict-origin-when-cross-origin" always;
}

# Redirect HTTP to HTTPS
server {
    listen 80;
    server_name api.alpharedge.com;
    return 301 https://$server_name$request_uri;
}
```

### 2. Firewall Rules (AWS Security Groups)

```bash
# Allow HTTPS inbound
aws ec2 authorize-security-group-ingress \
  --group-id sg-xxx \
  --protocol tcp --port 443 --cidr 0.0.0.0/0

# Allow HTTP inbound (redirect to HTTPS)
aws ec2 authorize-security-group-ingress \
  --group-id sg-xxx \
  --protocol tcp --port 80 --cidr 0.0.0.0/0

# Allow MongoDB outbound (if on separate instance)
aws ec2 authorize-security-group-egress \
  --group-id sg-xxx \
  --protocol tcp --port 27017 --cidr 10.0.0.0/8
```

### 3. SSL/TLS Certificate (Let's Encrypt)

```bash
# Install Certbot
sudo apt-get install certbot python3-certbot-nginx

# Obtain certificate
sudo certbot certonly --nginx -d api.alpharedge.com

# Auto-renewal
sudo systemctl enable certbot.timer
sudo systemctl start certbot.timer

# Verify renewal
sudo certbot renew --dry-run
```

---

## Monitoring & Logging

### 1. Application Logs

```bash
# View logs
tail -f /var/log/alpharedge/alpharedge-prod.log

# Search for errors
grep ERROR /var/log/alpharedge/alpharedge-prod.log

# Archive old logs
find /var/log/alpharedge -name "*.gz" -mtime +30 -delete
```

### 2. CloudWatch Monitoring (AWS)

```bash
# View metrics
aws cloudwatch get-metric-statistics \
  --namespace AWS/ECS \
  --metric-name CPUUtilization \
  --dimensions Name=ServiceName,Value=alpharedge-api \
  --start-time 2026-04-12T00:00:00Z \
  --end-time 2026-04-13T00:00:00Z \
  --period 3600 \
  --statistics Average

# Create alarm
aws cloudwatch put-metric-alarm \
  --alarm-name alpharedge-high-cpu \
  --alarm-description "Alert when CPU > 80%" \
  --metric-name CPUUtilization \
  --namespace AWS/ECS \
  --statistic Average \
  --period 300 \
  --threshold 80 \
  --comparison-operator GreaterThanThreshold
```

### 3. Health Checks

```bash
# Check API health
curl -f http://localhost:8080/actuator/health || exit 1

# Automated health check script
#!/bin/bash
while true; do
  status=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/v1/coins)
  if [ $status != 200 ]; then
    echo "API unhealthy: $status" | logger
    # Auto-restart or alert
  fi
  sleep 60
done
```

---

## Backup & Recovery

### 1. MongoDB Backup

```bash
# Backup
mongodump --uri "mongodb+srv://$MONGO_USER:$MONGO_PASSWORD@$MONGO_CLUSTER.mongodb.net/alpharedge" \
  --out /backups/alpharedge-$(date +%Y%m%d-%H%M%S)

# Restore
mongorestore --uri "mongodb+srv://$MONGO_USER:$MONGO_PASSWORD@$MONGO_CLUSTER.mongodb.net" \
  /backups/alpharedge-20260412-100000
```

### 2. Automated Backup Script

```bash
#!/bin/bash
BACKUP_DIR="/backups/alpharedge"
RETENTION_DAYS=30

# Create backup
mkdir -p $BACKUP_DIR
mongodump --uri "mongodb+srv://$MONGO_USER:$MONGO_PASSWORD@$MONGO_CLUSTER.mongodb.net/alpharedge" \
  --out $BACKUP_DIR/alpharedge-$(date +%Y%m%d-%H%M%S)

# Keep only recent backups
find $BACKUP_DIR -type d -mtime +$RETENTION_DAYS -exec rm -rf {} \;

echo "Backup completed successfully"
```

### 3. Scheduled Backups (Cron)

```bash
# Add to crontab
0 2 * * * /usr/local/bin/backup-alpharedge.sh >> /var/log/backups.log 2>&1
```

---

## Scaling

### 1. Horizontal Scaling

```bash
# Docker Compose - Add more instances
docker-compose --profile prod up -d --scale api=3

# Kubernetes - Scale replicas
kubectl scale deployment alpharedge-api --replicas=5 -n alpharedge

# Update load balancer target groups
aws elbv2 register-targets \
  --target-group-arn arn:aws:elasticloadbalancing:... \
  --targets Id=i-xxx Id=i-yyy
```

### 2. Database Scaling

```bash
# MongoDB Atlas - Increase tier
# Through MongoDB Atlas Console:
# 1. Select cluster
# 2. Cluster Configuration
# 3. Change instance tier to larger size
# 4. Confirm upgrade

# Monitor performance
db.stats()
db.currentOp()
```

### 3. Caching Layer (Redis)

Add Spring Data Redis dependency and configure:

```properties
spring.redis.host=redis.alpharedge.com
spring.redis.port=6379
spring.redis.password=${REDIS_PASSWORD}
spring.cache.type=redis
```

---

## Troubleshooting

### Application Won't Start

```bash
# Check logs
docker logs alpharedge-api

# Common issues:
# 1. Port already in use: Change SERVER_PORT
# 2. MongoDB connection failed: Verify MONGO_URI
# 3. Out of memory: Increase JVM heap size

# Set JVM options
export JAVA_OPTS="-Xms512m -Xmx2g"
```

### High CPU Usage

```bash
# Monitor processes
docker stats alpharedge-api

# Check slow queries
# MongoDB
db.setProfilingLevel(1, { slowms: 100 })
db.system.profile.find().sort({ ts: -1 }).limit(5).pretty()

# Scale horizontally
kubectl scale deployment alpharedge-api --replicas=5
```

### Database Connection Issues

```bash
# Test connection
mongosh "mongodb+srv://$MONGO_USER:$MONGO_PASSWORD@$MONGO_CLUSTER.mongodb.net"

# Check network
telnet mongodb.cluster.mongodb.net 27017

# Verify credentials
echo $MONGO_USER $MONGO_PASSWORD
```

---

## Security Hardening

### 1. Network Security

```bash
# Enable firewall
ufw enable
ufw allow 22/tcp
ufw allow 80/tcp
ufw allow 443/tcp

# Restrict access to MongoDB
ufw deny to mongodb.cluster from anywhere
```

### 2. Application Security

- [ ] Implement API authentication (JWT/OAuth2)
- [ ] Add rate limiting
- [ ] CORS configuration
- [ ] SQL injection prevention (already handled by Spring Data)
- [ ] CSRF protection
- [ ] Security headers

```properties
# Security headers in Nginx seen above:
# X-Frame-Options, X-Content-Type-Options, CSP, etc.
```

### 3. Secrets Management

```bash
# Use AWS Secrets Manager
aws secretsmanager create-secret \
  --name alpharedge/mongo-password \
  --secret-string $(openssl rand -base64 32)

# Reference in application
# Via @Value("${MONGO_PASSWORD}") or env variable
```

---

## Maintenance

### Regular Tasks

- Daily: Monitor logs and metrics
- Weekly: Review backup status
- Monthly: Update dependencies and patches
- Quarterly: Capacity planning and performance tuning
- Annually: Disaster recovery drill

### Update Procedure

```bash
# 1. Build new version
mvn clean package

# 2. Test locally
java -jar target/alpharedge-api-0.0.2.jar

# 3. Build Docker image
docker build -t alpharedge-api:0.0.2 .

# 4. Tag and push
docker tag alpharedge-api:0.0.2 registry/alpharedge-api:0.0.2
docker push registry/alpharedge-api:0.0.2

# 5. Update deployment (rolling update)
kubectl set image deployment/alpharedge-api \
  alpharedge-api=registry/alpharedge-api:0.0.2 \
  --record -n alpharedge

# 6. Monitor rollout
kubectl rollout status deployment/alpharedge-api -n alpharedge

# 7. Rollback if needed
kubectl rollout undo deployment/alpharedge-api -n alpharedge
```

---

## Performance Tuning

### JVM Tuning

```bash
export JAVA_OPTS="
  -Xms2g
  -Xmx2g
  -XX:+UseG1GC
  -XX:MaxGCPauseMillis=200
  -XX:+ParallelRefProcEnabled
  -XX:+AlwaysPreTouch
"
```

### MongoDB Optimization

```javascript
// Create indexes
db.tracked_coins.createIndex({ "coinId": 1 }, { unique: true })
db.price_snapshots.createIndex({ "coinId": 1, "fetchedAt": -1 })
db.portfolios.createIndex({ "userId": 1 })
db.price_alerts.createIndex({ "userId": 1, "isActive": 1 })

// Monitor indexes
db.collection.aggregate([{ $indexStats: {} }])
```

---

## Conclusion

Proper deployment and monitoring ensure AlphaEdge runs reliably in production. Adjust configuration based on specific infrastructure and requirements.
