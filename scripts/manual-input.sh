#!/bin/bash

# Varsayılan API URL
API_URL="http://localhost:8080/api/airquality/autoSaveAirQuality"

# Parametreleri al
LATITUDE=$1
LONGITUDE=$2
PM25=$3
PM10=$4
NO2=$5
SO2=$6
O3=$7

# JSON verisini oluştur
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

# Veriyi API'ye gönder
curl -X POST $API_URL -H "Content-Type: application/json" -d "$JSON_DATA"

echo "Veri gönderildi: $JSON_DATA"
