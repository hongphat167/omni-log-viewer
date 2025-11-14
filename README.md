# 🚀 Omni Log Viewer

Real-time log viewer tool similar to Grafana for monitoring logs from `https://10.22.17.219:10443/omni-digital/` microservices.

## 📸 Features

- ✅ **Real-time log streaming** via WebSocket
- ✅ **Multi-service support** (api-service, auth-service, bank-service, etc.)
- ✅ **Advanced filtering** (date range, log level, requestId, username, path)
- ✅ **Full-text search** with regex support
- ✅ **Syntax highlighting** for JSON logs
- ✅ **Color-coded log levels** (INFO, WARN, ERROR, DEBUG)
- ✅ **Export logs** (JSON, TXT formats)
- ✅ **Dark/Light theme**
- ✅ **Virtual scrolling** for performance
- ✅ **Responsive design**

## 🏗️ Architecture

```
Browser (React) ←→ Spring Boot API ←→ https://10.22.17.219:10443/omni-digital/
                       ↓
                   WebSocket (STOMP)
```

## 🛠️ Tech Stack

**Backend:**
- Java 17
- Spring Boot 3.2.0
- Spring WebFlux (WebClient)
- Spring WebSocket (STOMP)
- Lombok

**Frontend:**
- React 18 + TypeScript
- Vite
- TailwindCSS
- SockJS + STOMP
- React Virtuoso (virtual scrolling)
- Lucide React (icons)

## 🚀 Quick Start

### Using Docker (Recommended)

```bash
# Clone repository
git clone https://github.com/hongphat167/omni-log-viewer.git
cd omni-log-viewer

# Start with Docker Compose
docker-compose up -d

# Access the application
# Frontend: http://localhost:3000
# Backend API: http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui.html
```

### Local Development

**Prerequisites:**
- Java 17+
- Node.js 18+
- Maven 3.8+

**Backend:**
```bash
cd log-viewer-backend
./mvnw spring-boot:run
```

**Frontend:**
```bash
cd log-viewer-frontend
npm install
npm run dev
```

## 📖 API Documentation

### REST Endpoints

```
GET    /api/services                          - Get all available services
GET    /api/services/{serviceName}/logs       - Get logs with filters
POST   /api/logs/search                       - Advanced search
GET    /api/services/{serviceName}/logs/download - Download logs
```

### WebSocket

```
CONNECT    /ws-logs
SUBSCRIBE  /topic/logs/{serviceName}
SEND       /app/stream/start
SEND       /app/stream/stop
```

## 🎨 Log Format

The tool parses logs in this format:

```
[VERSION][TIMESTAMP] - [REQUEST_ID] [SESSION_ID] [USERNAME] [PATH] JSON/MESSAGE
```

Example:
```
[1.0.12-1311202515][2025-11-14 08:06:55.337] - [c715faf2-6858-9ae8-b1c8-94aee478de1d] [0969964430] [POST:/api/v1/app/auth/login] {"client_ip":"103.199.56.52",...}
```

## ⚙️ Configuration

### Backend (application.yml)

```yaml
log-source:
  base-url: https://10.22.17.219:10443/omni-digital/
  ssl-verify: false
  poll-interval: 3000
```

### Frontend (.env)

```env
VITE_API_URL=http://localhost:8080
VITE_WS_URL=ws://localhost:8080/ws-logs
```

## 📝 Usage

1. **Select Service**: Choose a service from the dropdown
2. **Apply Filters**: Filter by date, log level, requestId, username
3. **Search**: Use full-text or regex search
4. **Stream**: Click Play to start real-time streaming
5. **Export**: Download filtered logs in JSON or TXT format

## 🐛 Troubleshooting

**Backend can't connect to log source:**
- Check network connectivity to `10.22.17.219:10443`
- Verify SSL certificate handling

**WebSocket connection fails:**
- Check CORS configuration
- Verify firewall rules for WebSocket port

**High memory usage:**
- Reduce poll interval
- Limit log buffer size
- Clear logs periodically

## 📄 License

MIT License

## 👥 Contributors

- Tạ Hoàng Hồng Phát (@hongphat167)