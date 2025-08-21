# TaskFlow - Deployment Guide

## Overview

This document provides instructions for deploying the TaskFlow application in various environments including development, staging, and production.

## Prerequisites

Before deploying TaskFlow, ensure you have:

1. **Docker** (v20.10 or higher) installed on all deployment targets
2. **Kubernetes** (v1.20 or higher) cluster for production deployments
3. **kubectl** configured to communicate with your Kubernetes cluster
4. **Helm** (v3.0 or higher) for managing Kubernetes charts
5. **Domain name** configured for production deployments
6. **SSL certificates** for HTTPS (Let's Encrypt recommended)
7. **Cloud storage** account for file uploads (AWS S3, Google Cloud Storage, etc.)

## Architecture Overview

TaskFlow follows a microservices architecture with the following components:

1. **Frontend Service** (Next.js application)
2. **Auth Service** (User authentication and JWT management)
3. **User Service** (User profiles and team management)
4. **Project Service** (Project management)
5. **Task Service** (Task management)
6. **Notification Service** (Real-time notifications)
7. **Database** (PostgreSQL)
8. **Cache** (Redis)
9. **Message Broker** (RabbitMQ or Apache Kafka)

## Development Deployment

### Local Development with Docker Compose

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd taskflow
   ```

2. Create a `.env` file in the root directory with the following variables:
   ```env
   # Database
   POSTGRES_USER=taskflow
   POSTGRES_PASSWORD=development_password
   POSTGRES_DB=taskflow_dev
   
   # JWT
   JWT_SECRET=development_jwt_secret
   JWT_EXPIRES_IN=900
   REFRESH_TOKEN_SECRET=development_refresh_token_secret
   REFRESH_TOKEN_EXPIRES_IN=604800
   
   # Redis
   REDIS_URL=redis://redis:6379
   
   # Services
   AUTH_SERVICE_URL=http://auth-service:3001
   USER_SERVICE_URL=http://user-service:3002
   PROJECT_SERVICE_URL=http://project-service:3003
   TASK_SERVICE_URL=http://task-service:3004
   ```

3. Start the services:
   ```bash
   docker-compose up -d
   ```

4. Access the application at `http://localhost:3000`

### Stopping Development Environment
```bash
docker-compose down
```

To remove all data (including database):
```bash
docker-compose down -v
```

## Staging Deployment

### Kubernetes Deployment with Helm

1. Ensure you have Helm installed and configured:
   ```bash
   helm version
   ```

2. Navigate to the `helm` directory:
   ```bash
   cd deployment/helm
   ```

3. Create a `staging-values.yaml` file with staging-specific configurations:
   ```yaml
   # staging-values.yaml
   replicaCount: 2
   
   image:
     tag: staging-latest
   
   ingress:
     enabled: true
     hosts:
       - host: staging.taskflow.example.com
         paths:
           - path: /
             pathType: Prefix
     tls:
       - secretName: staging-tls
         hosts:
           - staging.taskflow.example.com
   
   postgresql:
     auth:
       username: taskflow
       password: staging_password
       database: taskflow_staging
   
   redis:
     auth:
       enabled: true
       password: staging_redis_password
   ```

4. Deploy to staging:
   ```bash
   helm install taskflow-staging . -f staging-values.yaml
   ```

5. Check deployment status:
   ```bash
   kubectl get pods
   kubectl get services
   kubectl get ingress
   ```

## Production Deployment

### Kubernetes Deployment with Helm

1. Create a `production-values.yaml` file with production-specific configurations:
   ```yaml
   # production-values.yaml
   replicaCount: 3
   
   image:
     tag: production-latest
   
   ingress:
     enabled: true
     hosts:
       - host: taskflow.example.com
         paths:
           - path: /
             pathType: Prefix
     tls:
       - secretName: production-tls
         hosts:
           - taskflow.example.com
   
   postgresql:
     auth:
       username: taskflow
       password: production_password  # Use Kubernetes secret in production
       database: taskflow_production
   
   redis:
     auth:
       enabled: true
       password: production_redis_password  # Use Kubernetes secret in production
   
   autoscaling:
     enabled: true
     minReplicas: 3
     maxReplicas: 10
     targetCPUUtilizationPercentage: 80
   ```

2. Deploy to production:
   ```bash
   helm install taskflow-production . -f production-values.yaml
   ```

3. Check deployment status:
   ```bash
   kubectl get pods
   kubectl get services
   kubectl get ingress
   ```

## Environment Variables

### Required Environment Variables

Each service requires specific environment variables. Here's a comprehensive list:

#### Frontend Service
```env
NEXT_PUBLIC_API_URL=https://api.taskflow.example.com
NEXT_PUBLIC_WS_URL=wss://api.taskflow.example.com
```

#### Auth Service
```env
PORT=3001
DATABASE_URL=postgresql://user:password@host:port/database
REDIS_URL=redis://host:port
JWT_SECRET=your_jwt_secret
JWT_EXPIRES_IN=900
REFRESH_TOKEN_SECRET=your_refresh_token_secret
REFRESH_TOKEN_EXPIRES_IN=604800
```

#### User Service
```env
PORT=3002
DATABASE_URL=postgresql://user:password@host:port/database
REDIS_URL=redis://host:port
JWT_SECRET=your_jwt_secret
```

#### Project Service
```env
PORT=3003
DATABASE_URL=postgresql://user:password@host:port/database
REDIS_URL=redis://host:port
JWT_SECRET=your_jwt_secret
```

#### Task Service
```env
PORT=3004
DATABASE_URL=postgresql://user:password@host:port/database
REDIS_URL=redis://host:port
JWT_SECRET=your_jwt_secret
NOTIFICATION_SERVICE_URL=http://notification-service:3005
```

## Database Migrations

### Running Migrations

1. For development:
   ```bash
   docker-compose exec auth-service npm run migrate
   docker-compose exec user-service npm run migrate
   docker-compose exec project-service npm run migrate
   docker-compose exec task-service npm run migrate
   ```

2. For Kubernetes deployments:
   ```bash
   kubectl exec -it <auth-service-pod> -- npm run migrate
   kubectl exec -it <user-service-pod> -- npm run migrate
   kubectl exec -it <project-service-pod> -- npm run migrate
   kubectl exec -it <task-service-pod> -- npm run migrate
   ```

### Rolling Back Migrations
```bash
npm run migrate:rollback
```

## Monitoring and Logging

### Monitoring Setup

1. **Prometheus** is used for metrics collection
2. **Grafana** is used for visualization
3. **Health checks** are implemented for each service

### Logging

1. All services output structured JSON logs
2. Logs are collected by Fluentd
3. Logs are stored in Elasticsearch
4. Logs can be viewed in Kibana

### Health Checks

Each service exposes a health check endpoint:
```
GET /health
```

Response:
```json
{
  "status": "healthy",
  "timestamp": "ISO_timestamp",
  "service": "service-name"
}
```

## Backup and Recovery

### Database Backup

1. **Daily full backups** are scheduled
2. **Hourly incremental backups** are created
3. Backups are stored in cloud storage with encryption

### Recovery Process

1. Identify the backup to restore from
2. Stop the affected services
3. Restore the database from backup
4. Restart the services
5. Validate the restored data

## Scaling

### Horizontal Scaling

1. Services can be scaled independently:
   ```bash
   kubectl scale deployment auth-service --replicas=5
   ```

2. Auto-scaling is configured based on CPU utilization

### Database Scaling

1. PostgreSQL read replicas can be added for read-heavy workloads
2. Connection pooling is implemented to optimize database connections

## Security Considerations

### Network Security

1. Services communicate over internal network only
2. External access is controlled through ingress controllers
3. Rate limiting is implemented at the API gateway level

### Data Security

1. All data is encrypted at rest
2. All communication is encrypted with TLS
3. Secrets are managed with Kubernetes secrets

### Access Control

1. Role-based access control is implemented
2. JWT tokens are used for authentication
3. Regular security audits are performed

## Troubleshooting

### Common Issues

1. **Service won't start**: Check logs with `kubectl logs <pod-name>`
2. **Database connection failed**: Verify DATABASE_URL environment variable
3. **Authentication issues**: Check JWT_SECRET and token expiration settings
4. **Performance issues**: Check resource utilization and consider scaling

### Useful Commands

1. View pod logs:
   ```bash
   kubectl logs <pod-name>
   ```

2. View pod status:
   ```bash
   kubectl get pods
   ```

3. Describe pod details:
   ```bash
   kubectl describe pod <pod-name>
   ```

4. Access pod shell:
   ```bash
   kubectl exec -it <pod-name> -- sh
   ```

This deployment guide provides a comprehensive overview of deploying TaskFlow in various environments. For specific issues or custom deployment requirements, please consult with the DevOps team.