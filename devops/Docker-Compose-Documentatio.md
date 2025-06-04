# Docker Compose Setup - Documentation

Αυτό το αρχείο εξηγεί τη λειτουργία του `docker-compose.yaml` που χρησιμοποιείται για την εκκίνηση της Spring Boot εφαρμογής και της PostgreSQL βάσης δεδομένων.

---

## Services

### 1. `db` — PostgreSQL Database

- **Image**: `postgres:16`
- **Ports**:
  - Εσωτερική: 5432 (με `expose`)
  - Εξωτερική: 5432 (με `ports`)
- **Environment Variables**:
  - `POSTGRES_USER=myapp_user`
  - `POSTGRES_PASSWORD=myapp_password`
  - `POSTGRES_DB=colab`
- **Healthcheck**:
  Εκτελεί `pg_isready -U postgres` κάθε 30s.
- **Volume**:
  - Συνδέεται με το `dsprojectdb` για μόνιμη αποθήκευση δεδομένων.

>  **Σημείωση**: Η βάση `colab` δημιουργείται **μόνο την πρώτη φορά**, αν το volume είναι άδειο.

---

### 2. `spring` — Spring Boot Application

- **Build**:
  - Dockerfile: `devops/nonroot-multistage.Dockerfile`
  - Context: `/tmp/project`
- **Ports**: 8080 (host ↔ container)
- **Depends On**:
  Περιμένει το `db` να είναι **healthy** πριν ξεκινήσει.
- **Environment Variables**:
  - `SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/colab`
  - `SPRING_DATASOURCE_USERNAME=myapp_user`
  - `SPRING_DATASOURCE_PASSWORD=myapp_password`
- **Healthcheck**:
  Ελέγχει το endpoint `/actuator/health` κάθε 30s.

---

##  Volumes

```yaml
volumes:
  dsprojectdb:
```