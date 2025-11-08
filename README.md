# EAD - Enterprise Automobile Distribution

A full-stack web application for automobile distribution management, built with Spring Boot, Next.js, PostgreSQL, and deployed with Docker and Kubernetes.

## 🚀 Features

- **Full-Stack Architecture**: Spring Boot backend with Next.js frontend
- **Database**: PostgreSQL for reliable data persistence
- **Image Management**: Cloudinary integration for image storage
- **Email Service**: SMTP email notifications
- **Containerization**: Docker and Docker Compose support
- **Orchestration**: Kubernetes deployment configurations
- **Admin Dashboard**: Secure admin interface for management

## 🛠️ Tech Stack

### Backend
- **Framework**: Spring Boot
- **Database**: PostgreSQL 17
- **ORM**: JPA/Hibernate
- **Security**: Spring Security
- **Email**: Spring Mail (SMTP)
- **Image Storage**: Cloudinary

### Frontend
- **Framework**: Next.js (React)
- **Language**: TypeScript
- **Styling**: Modern CSS

### DevOps
- **Containerization**: Docker
- **Orchestration**: Kubernetes
- **Database**: PostgreSQL

## 📋 Prerequisites

Before you begin, ensure you have the following installed:
- Docker and Docker Compose
- Node.js (v18 or higher)
- Java 17 or higher (for local development)
- kubectl (for Kubernetes deployment)

## 🚀 Quick Start

### Using Docker Compose (Recommended)

1. **Clone the repository**
   ```bash
   git clone https://github.com/dila72/EAD.git
   cd EAD
   ```

2. **Configure environment variables**
   ```bash
   cp .env.example .env
   ```
   Edit `.env` and fill in your configuration values:
   - Database credentials
   - Email settings (Gmail SMTP)
   - Cloudinary credentials
   - Admin credentials

3. **Start the application**
   
   On Linux/Mac:
   ```bash
   chmod +x docker-start.sh
   ./docker-start.sh
   ```
   
   On Windows:
   ```bash
   docker-start.bat
   ```

4. **Access the application**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080

### Manual Setup

#### Backend Setup
```bash
cd backend
# Configure backend/.env.example
./mvnw clean install
./mvnw spring-boot:run
```

#### Frontend Setup
```bash
cd frontend
npm install
npm run dev
```

## 🐳 Docker Deployment

The project includes a complete Docker Compose setup:

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

### Services
- **postgres**: PostgreSQL database (Port 5432)
- **backend**: Spring Boot API (Port 8080)
- **frontend**: Next.js application (Port 3000)

## ☸️ Kubernetes Deployment

For production deployment on Kubernetes, refer to [K8S_DEPLOYMENT.md](./K8S_DEPLOYMENT.md) for detailed instructions.

## 📁 Project Structure

```
EAD/
├── backend/              # Spring Boot application
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── frontend/             # Next.js application
│   ├── src/
│   ├── Dockerfile
│   └── package.json
├── kubernetes/           # Kubernetes manifests
├── docker-compose.yml    # Docker Compose configuration
├── .env.example          # Environment variables template
└── README.md
```

## 🔧 Configuration

### Environment Variables

Key environment variables to configure:

**Database**
- `POSTGRES_DB`: Database name
- `POSTGRES_USER`: Database username
- `POSTGRES_PASSWORD`: Database password

**Backend**
- `BACKEND_PORT`: Backend server port (default: 8080)
- `ADMIN_USERNAME`: Admin username
- `ADMIN_PASSWORD`: Admin password

**Frontend**
- `FRONTEND_PORT`: Frontend port (default: 3000)
- `NEXT_PUBLIC_API_URL`: Backend API URL

**Email (Gmail)**
- `MAIL_HOST`: SMTP host
- `MAIL_PORT`: SMTP port
- `MAIL_USERNAME`: Your email
- `MAIL_PASSWORD`: App password

**Cloudinary**
- `CLOUDINARY_CLOUD_NAME`: Your cloud name
- `CLOUDINARY_API_KEY`: Your API key
- `CLOUDINARY_API_SECRET`: Your API secret

## 🔒 Security

- Always use strong passwords for production
- Never commit `.env` files to version control
- Use app passwords for Gmail SMTP
- Keep Cloudinary credentials secure

## 📝 Development

### Backend Development
```bash
cd backend
./mvnw spring-boot:run
```

### Frontend Development
```bash
cd frontend
npm run dev
```

## 🧪 Testing

### Backend Tests
```bash
cd backend
./mvnw test
```

### Frontend Tests
```bash
cd frontend
npm test
```

## 📚 API Documentation

Once the backend is running, access the API documentation at:
- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/api-docs

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Authors

- **dila72** - *Initial work* - [GitHub](https://github.com/dila72)

## 🙏 Acknowledgments

- Spring Boot community
- Next.js team
- PostgreSQL team
- Docker and Kubernetes communities

## 📞 Support

For support, please open an issue in the GitHub repository.

---

**Built with ❤️ using Spring Boot, Next.js, and PostgreSQL**