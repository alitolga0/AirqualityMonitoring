# Kullanılacak base image
FROM gradle:7.6-jdk17-alpine AS builder

# Çalışma dizinini belirleyelim
WORKDIR /app

# Gradle Wrapper ve gerekli dosyaları kopyalayalım
COPY --chown=gradle:gradle . .

# Projeyi build edelim
RUN gradle build --no-daemon

# Uygulama image'ını oluşturma
FROM amazoncorretto:17-alpine-jdk

# Çalışma dizinini belirleyelim
WORKDIR /app

# Gradle build sonucunda oluşan JAR dosyasını kopyalayalım
COPY --from=builder /app/build/libs/airquality-monitoring-0.0.1-SNAPSHOT.jar /app/

# Uygulamayı çalıştıracak komut
EXPOSE 8080
CMD ["java", "-jar", "airquality-monitoring-0.0.1-SNAPSHOT.jar"]
