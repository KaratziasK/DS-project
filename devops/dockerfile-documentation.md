# Dockerfile Documentation for Spring Boot Multi-Stage Build

This Dockerfile uses a multi-stage build to create an optimized and secure image for a Spring Boot application.

---

## ✨ Overview
- **Language**: Java 21
- **Build Tool**: Maven
- **Base Images**:
  - Build Stage: `maven:3.9.6-eclipse-temurin-21`
  - Runtime Stage: `eclipse-temurin:21-alpine-3.21`

---

## ✅ Stage 1: Build the Spring Boot App
```dockerfile
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests
```

### 🔍 Τι κάνει:
- Κατεβάζει image με Maven + JDK 21.
- Ορίζει working directory `/build`.
- Αντιγράφει `pom.xml` και `src/` στον image.
- Κάνει build με `mvn clean package` και αγνοεί τα tests (`-DskipTests`).
- Το τελικό `.jar` θα βρίσκεται στο `/build/target/`.

---

## ✅ Stage 2: Create the Runtime Image
```dockerfile
FROM eclipse-temurin:21-alpine-3.21
LABEL maintainer="karatziask"
WORKDIR /app
```

### 🔍 Τι κάνει:
- Ξεκινά από ένα ελαφρύ Alpine-based image με JDK 21.
- Ορίζει working directory `/app` (**μέσα στο image**).

### 🌐 Ασφάλεια και Εργαλεία:
```dockerfile
RUN apk update && apk add curl
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
```
- Εγκαθιστά `curl` για debugging/healthchecks.
- Δημιουργεί μη προνομιούχο χρήστη `appuser` για λόγους ασφαλείας.

### 💾 Αντιγραφή του .jar:
```dockerfile
COPY --from=builder /build/target/dsproject-0.0.1-SNAPSHOT.jar ./application.jar
RUN chown appuser /app/application.jar
```
- Παίρνει το `.jar` από το build stage.
- Το βάζει στον φάκελο `/app/`.
- Αλλάζει ιδιοκτήτη σε `appuser`.

### ⚖️ Εκτέλεση της εφαρμογής:
```dockerfile
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "application.jar"]
```
- Εκτελεί την εφαρμογή με τον unprivileged user.
- Ανοίγει το port 8080.
- Εκκινεί το `.jar` με `java -jar`.

---

## 📊 Πλεονεκτήματα
- ✅ **Multi-stage build**: Χωρίζει build & runtime για μικρότερο και πιο καθαρό image.
- ✅ **Ασφαλές**: Δεν τρέχεις ως root.
- ✅ **Ελαφρύ**: Το τελικό image περιέχει μόνο ό,τι χρειάζεται για να τρέξει το app.

---

## 📖 Χρήση με Docker Compose
Αυτό το Dockerfile χρησιμοποιείται σε `docker-compose.yaml` με:
```yaml
spring:
  build:
    context: /tmp/project
    dockerfile: /tmp/project/devops/nonroot-multistage.Dockerfile
```