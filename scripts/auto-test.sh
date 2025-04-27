#!/bin/bash

# Varsayılan API URL
API_URL="http://localhost:8080/api/airquality/autoSaveAirQuality"

# Default değerler
DURATION=60
RATE=1
ANOMALY_CHANCE=10

# Parametreleri işle
for ARG in "$@"
do
  case $ARG in
    --duration=*)
      DURATION="${ARG#*=}"
      shift
      ;;
    --rate=*)
      RATE="${ARG#*=}"
      shift
      ;;
    --anomaly-chance=*)
      ANOMALY_CHANCE="${ARG#*=}"
      shift
      ;;
    *)
      echo "Bilinmeyen argüman: $ARG"
      exit 1
      ;;
  esac
done

# Total veri ve anomaly hesapla
TOTAL_REQUESTS=$((DURATION * RATE))
TOTAL_ANOMALIES=$((TOTAL_REQUESTS * ANOMALY_CHANCE / 100))
ANOMALY_COUNT=0
REQUEST_COUNT=0

echo "Toplam istek: $TOTAL_REQUESTS"
echo "Beklenen anomaly sayısı: $TOTAL_ANOMALIES"

# Threshold değerler
PM25_THRESHOLD=25.0
PM10_THRESHOLD=50.0
NO2_THRESHOLD=40.0
SO2_THRESHOLD=20.0
O3_THRESHOLD=100.0

# random float üretimi (bc kullanmadan)
random_float() {
  local min=$1
  local max=$2
  awk -v min="$min" -v max="$max" 'BEGIN { srand(); print min + rand() * (max - min) }'
}

# random int üretimi
random_int() {
  local min=$1
  local max=$2
  echo $(( RANDOM % (max - min + 1) + min ))
}

# Son gönderilen latitude ve longitude'u global tutacağız
LAST_LATITUDE=""
LAST_LONGITUDE=""

# Veri üretme fonksiyonu
generate_random_data() {
  # Her veri için farklı koordinatlar üret
  while true; do
    LATITUDE=$(random_float -90 90)
    LONGITUDE=$(random_float -180 180)

    # Eğer önceki ile aynıysa yeniden üret
    if [ "$LATITUDE" != "$LAST_LATITUDE" ] || [ "$LONGITUDE" != "$LAST_LONGITUDE" ]; then
      break
    fi
  done

  # Son gönderilen konumu güncelle
  LAST_LATITUDE=$LATITUDE
  LAST_LONGITUDE=$LONGITUDE

  # Diğer değerleri rastgele üret
  PM25=$(random_int 0 50)
  PM10=$(random_int 0 100)
  NO2=$(random_int 0 50)
  SO2=$(random_int 0 25)
  O3=$(random_int 0 120)

  SHOULD_ANOMALY=0

  if [ $ANOMALY_COUNT -lt $TOTAL_ANOMALIES ]; then
    if [ $((RANDOM % (TOTAL_REQUESTS - REQUEST_COUNT + 1))) -lt $((TOTAL_ANOMALIES - ANOMALY_COUNT)) ]; then
      SHOULD_ANOMALY=1
      ANOMALY_COUNT=$((ANOMALY_COUNT + 1))
    fi
  fi

  if [ $SHOULD_ANOMALY -eq 1 ]; then
    # Anomaly veriler threshold üstüne çıkar
    PM25=$(awk -v th="$PM25_THRESHOLD" 'BEGIN { srand(); print th + rand() * 300 }')
    PM10=$(awk -v th="$PM10_THRESHOLD" 'BEGIN { srand(); print th + rand() * 300 }')
    NO2=$(awk -v th="$NO2_THRESHOLD" 'BEGIN { srand(); print th + rand() * 100 }')
    SO2=$(awk -v th="$SO2_THRESHOLD" 'BEGIN { srand(); print th + rand() * 50 }')
    O3=$(awk -v th="$O3_THRESHOLD" 'BEGIN { srand(); print th + rand() * 100 }')
  fi

  JSON_DATA=$(cat <<EOF
{
  "latitude": $LATITUDE,
  "longitude": $LONGITUDE,
  "pm25": $PM25,
  "pm10": $PM10,
  "no2": $NO2,
  "so2": $SO2,
  "o3": $O3
}
EOF
)

  # API'ye POST yap
  RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$API_URL" -H "Content-Type: application/json" -d "$JSON_DATA")
  
  if [ "$RESPONSE" -eq 200 ] || [ "$RESPONSE" -eq 201 ]; then
    echo "✅ [$REQUEST_COUNT] Veri gönderildi: $JSON_DATA"
  else
    echo "❌ [$REQUEST_COUNT] Hata (HTTP $RESPONSE): $JSON_DATA"
  fi

  REQUEST_COUNT=$((REQUEST_COUNT + 1))
}

# Başlangıç ve bitiş zamanı
END_TIME=$(($(date +%s) + DURATION))

# Ana döngü
while [ $(date +%s) -lt $END_TIME ]; do
  for ((i=0; i<$RATE; i++)); do
    generate_random_data
  done
  sleep 1
done
