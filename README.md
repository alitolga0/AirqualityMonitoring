
# 🌍 Air Quality Monitoring Platform

Gerçek zamanlı hava kirliliği verilerini toplayan, analiz eden ve görselleştiren **web tabanlı bir platform**. Bu sistem; farklı konumlardan alınan sensör verilerini kaydeder, analiz eder, anomalileri tespit eder ve kullanıcıları bilgilendirir. Kafka ile veri sürekliliği sağlanmıştır.

---

## 🛠️ Proje Bileşenleri

- **Backend:** Spring Boot (Java 17) kullanılarak geliştirildi.
- **Frontend:** Vue ve Vite kullanılarak geliştirildi.
- **Veritabanı:** PostgreSQL.
- **Mesaj Kuyruğu:** Apache Kafka.
- **Container Orkestrasyonu:** Docker Compose.
- **Veri Üretimi:** Gerçek zamanlı veri girişini simüle eden bash scriptleri.

---

## 📦 Docker Compose Yapısı

Tüm sistem aşağıdaki servislerden oluşmaktadır:

| Servis      | Açıklama                                 | Port         |
|-------------|-------------------------------------------|--------------|
| `app`       | Spring Boot backend uygulaması           | `8080`       |
| `frontend`  | Vue frontend uygulaması                | `3000`       |
| `db`        | PostgreSQL veritabanı                    | `5432`       |
| `zookeeper` | Kafka'nın bağımlılığı                    | `2181`       |
| `kafka`     | Apache Kafka mesaj kuyruğu               | `9092`, `29092` |

**PostgreSQL Ayarları:**
- Kullanıcı: `root`
- Şifre: `Password1`

**Kafka Topic Bilgisi:**
- Topic adı: `air-quality-events`
- Consumer group id: `airquality-group`

---

## 🚀 Uygulamayı Başlatma

1. ```https://github.com/alitolga0/AirqualityMonitoring.git```
2. Gerekli klasör yapısını oluşturduğunuzdan emin olun.
3. Proje kök dizininde terminali açarak aşağıdaki komutu çalıştırın:

**NOT: Bu işlem internet hızınıza göre yavaşlık gösterebilir ve kesintiler yaşanabilir.**

```bash
docker-compose up --build
```

4. Sistem aşağıdaki adreslerde aktif olacaktır:
   - **Swagger (API Dokümantasyonu):** [http://localhost:8080/swagger-ui/index.html#/](http://localhost:8080/swagger-ui/index.html#/)
   - **Frontend (React Uygulaması):** [http://localhost:3000](http://localhost:3000)

---

## 🔁 API Endpointleri

### 📤 Veri Kaydetme (Otomatik)

```http
POST /api/airquality/autoSaveAirQuality
```

#### Örnek Body:
```json
{
  "latitude": 39.92077,
  "longitude": 32.85411,
  "pm25": 5,
  "pm10": 12,
  "no2": 8,
  "so2": 3,
  "o3": 20
}
```

---

### 📌 Konuma Göre Veri Getir

```http
GET /api/airquality/location?latitude=39.92077&longitude=32.85411
```

---

### ⚠️ Anomali Tespiti (Zaman Aralığına Göre)

```http
GET /api/airquality/anomalies?startTime=2025-04-29T23:41:00&endTime=2025-05-02T11:48:00
```

---

### 📍 Yakın Alan Verileri (25km² içindeki anomaly verileri)

```http
GET /api/airquality/findNearbyRecords?latitude=39.92088&longitude=32.85411
```

> Bu endpoint özellikle **harita üzerindeki bölgesel analizlerde** kullanılır. Belirtilen koordinatın çevresindeki 25km² alanda bulunan **anomaly** değerlerini getirir.

---

## 🧪 Test Scriptleri

Verilerin sisteme otomatik veya manuel şekilde gönderilmesini sağlayan bash scriptleri:

### 1. Otomatik Test (Çoklu veri üretimi)

**Aşağıdaki komut ile scripti çalıştıracak olduğumuz dosya yoluna gitmek gerekir**

```cd {rootOfProject}\backend\scripts```

```bash
$ ./auto-test.sh --duration=50 --rate=5 --anomaly-chance=20
```

- `duration`: Scriptin çalışacağı süre (saniye cinsinden)
- `rate`: Saniyede üretilecek veri sayısı
- `anomaly-chance`: Verilerin yüzde kaçının anomalik olacağı

> Örnek olarak 50 saniyede 250 veri gönderilir, yaklaşık 50 tanesi anomalidir.

### 2. Manuel Veri Girişi

```bash
$ ./manual-input.sh 39.92077 32.85411 5 12 8 3 20
```

Parametreler sırasıyla:
- LATITUDE
- LONGITUDE
- PM2.5
- PM10
- NO2
- SO2
- O3

### 3. Alternatif: Postman Üzerinden Giriş

```http
POST /api/airquality
```

```json
{
  "latitude": 14.7129,
  "longitude": 56.258,
  "pm25": 120,
  "pm10": 150,
  "no2": 85,
  "so2": 70,
  "o3": 10
}
```

---

## 🧭 Kullanım Senaryoları

- Harita üzerinden belirli bir konumun hava kalitesini analiz etme
- Farklı zaman dilimlerinde meydana gelen anomalileri listeleme
- Bölgesel bazda (ör. 25km²) kirlilik haritası oluşturma
- Kafka üzerinden gelen verilerin gerçek zamanlı işlenmesi

---

## 📊 Gelişmiş Özellikler

- ✅ Kafka destekli gerçek zamanlı veri akışı
- ✅ Anomali tespiti
- ✅ Swagger UI üzerinden API test imkanı
- ✅ Docker Compose ile kolay kurulum
- ✅ Hava kirliliği analiz haritaları (Frontend tarafı ile entegre)
- ✅ Scriptler ile yüksek hacimli veri simülasyonu

---

## 📂 Dosya Yapısı (Özet)

```
airquality-monitoring/
├── backend/
│   ├── scripts/
│   │   ├── auto-test.sh
│   │   └── manual-input.sh
│   └── (Spring Boot Kaynakları)
├── frontend/
│   └── (Vue kaynakları)
├── docker-compose.yml
└── README.md
```

---

## 📚 Gereksinimler

- Docker & Docker Compose
- Java 17 (development için)
- Git Bash (Windows kullanıcıları için script çalıştırma)

---

## 🧯 Sorun Giderme Rehberi

Docker servisleri başlatılamıyorsa
docker ps -a komutu ile hangi servisin hata verdiğini kontrol edin.

Kafka başlatılamıyorsa port çakışması veya ağ hatası olabilir.

Sırasıyla aşağıdaki komutları tekrardan çalıştırırn.

```
- docker compose down --volume
- docker-compose up --build

```

---

## 🧠Önyüz Ekran Görüntüleri

![image](https://github.com/user-attachments/assets/0167aa4d-35dd-4ac2-8720-9897380ae4cd)

![image](https://github.com/user-attachments/assets/ee31156f-4b96-4208-ade3-4590041e47f6)

![image](https://github.com/user-attachments/assets/f47bc0b3-f0d0-4b39-8d42-6c62151e0927)


![image](https://github.com/user-attachments/assets/a03b5b4b-62e2-489c-bdb1-022140a4d946)

![image](https://github.com/user-attachments/assets/8945df2d-f875-461f-a31b-0188b97d1941)








---
## 🧠 Katkıda Bulunmak

Pull request’ler her zaman açıktır. Büyük değişiklikler için lütfen önce neyi değiştirmek istediğinizi tartışmak üzere bir issue açın.

---

## 📧 İletişim

Bu proje hakkında sorularınız varsa veya katkıda bulunmak istiyorsanız benimle iletişime geçebilirsiniz.

📨 E-posta: [cakiralitolga@gmail.com](mailto:cakiralitolga@gmail.com)

--- 

Hazırlayan: **Ali Tolga Çakir**

--- 
