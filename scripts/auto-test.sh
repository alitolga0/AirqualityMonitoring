#!/bin/bash

API_URL="http://localhost:8080/api/airquality/autoSaveAirQuality"

DURATION=50
RATE=1
ANOMALY_CHANCE=20

for ARG in "$@"; do
  case $ARG in
    --duration=*)
      DURATION="${ARG#*=}"
      ;;
    --rate=*)
      RATE="${ARG#*=}"
      ;;
    --anomaly-chance=*)
      ANOMALY_CHANCE="${ARG#*=}"
      ;;
    *)
      echo "Bilinmeyen argüman: $ARG"
      exit 1
      ;;
  esac
done

TOTAL_REQUESTS=$((DURATION * RATE))

echo "Toplam istek: $TOTAL_REQUESTS"
echo "Anomali oranı: %$ANOMALY_CHANCE"

PM25_THRESHOLD=25.0
PM10_THRESHOLD=50.0
NO2_THRESHOLD=40.0
SO2_THRESHOLD=20.0
O3_THRESHOLD=100.0

# random float
random_float() {
  local min="$1"
  local max="$2"
  awk -v min="$min" -v max="$max" 'BEGIN { srand(); print min + rand() * (max - min) }'
}

# random int
random_int() {
  local min="$1"
  local max="$2"
  echo $(( RANDOM % (max - min + 1) + min ))
}

# önceki 5 koordinat
declare -a LAST_COORDS=()

# koordinat benzerliği kontrolü (±0.0001 hassasiyet)
is_similar_coord() {
  local lat1="$1"
  local lon1="$2"

  for coord in "${LAST_COORDS[@]}"; do
    IFS=',' read -r lat2 lon2 <<< "$coord"

    diff_lat=$(awk -v a="$lat1" -v b="$lat2" 'BEGIN { print (a - b) < 0 ? (b - a) : (a - b) }')
    diff_lon=$(awk -v a="$lat1" -v b="$lon2" 'BEGIN { print (a - b) < 0 ? (b - a) : (a - b) }')

    if (( $(awk "BEGIN { print ($diff_lat < 0.0001) && ($diff_lon < 0.0001) }") )); then
      return 0
    fi
  done
  return 1
}

# yeni koordinatları sakla (en fazla 5)
add_coord_to_history() {
  LAST_COORDS+=("$1,$2")
  if [ "${#LAST_COORDS[@]}" -gt 5 ]; then
    LAST_COORDS=("${LAST_COORDS[@]:1}")
  fi
}

REQUEST_COUNT=0
ANOMALY_SENT_COUNT=0 # Gönderilen anomali sayısı

generate_random_data() {
  while true; do
    LATITUDE=$(random_float -90 90)
    LONGITUDE=$(random_float -180 180)

    if ! is_similar_coord "$LATITUDE" "$LONGITUDE"; then
      break
    fi
  done

  add_coord_to_history "$LATITUDE" "$LONGITUDE"

  PM25=$(random_int 0 24)
  PM10=$(random_int 0 49)
  NO2=$(random_int 0 39)
  SO2=$(random_int 0 19)
  O3=$(random_int 0 99)

  SHOULD_ANOMALY=0
  # Rastgele bir sayı üretip, bunun anomaly chance'ten küçük olup olmadığını kontrol et
  if (( $(random_int 1 100) <= $ANOMALY_CHANCE )); then
    SHOULD_ANOMALY=1
    ANOMALY_SENT_COUNT=$((ANOMALY_SENT_COUNT + 1))
  fi

  # Eğer anomali üretilmesi gerekiyorsa
  if [ $SHOULD_ANOMALY -eq 1 ]; then
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

  RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$API_URL" -H "Content-Type: application/json" -d "$JSON_DATA")

  if [ "$RESPONSE" -eq 200 ] || [ "$RESPONSE" -eq 201 ]; then
    echo "✅ [$REQUEST_COUNT] Veri gönderildi (Anomali: $SHOULD_ANOMALY): $JSON_DATA"
  else
    echo "❌ [$REQUEST_COUNT] Hata (HTTP $RESPONSE) (Anomali: $SHOULD_ANOMALY): $JSON_DATA"
  fi

  REQUEST_COUNT=$((REQUEST_COUNT + 1))
}

END_TIME=$(($(date +%s) + DURATION))
while [ $REQUEST_COUNT -lt $TOTAL_REQUESTS ]; do
  generate_random_data
  sleep 1  # Her saniyede veri üretilecek
done

echo "Toplam gönderilen istek sayısı: $REQUEST_COUNT"
echo "Toplam gönderilen anomali sayısı: $ANOMALY_SENT_COUNT"