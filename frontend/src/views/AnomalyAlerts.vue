<template>
  <div class="anomaly-alerts">
    <h2>Anomali Uyarı Paneli</h2>
    <div v-if="Object.keys(groupedAnomalies).length > 0" class="anomaly-cards">
      <div
        v-for="(anomaliesGroup, location) in groupedAnomalies"
        :key="location"
        class="anomaly-card"
      >
        <div class="anomaly-header">
          <span class="anomaly-location">📍 {{ location }}</span>
          <button @click="removeAnomalies(location)" class="remove-btn">Sil</button>
        </div>
        <div class="anomaly-body">
          <div v-for="(anomaly, index) in anomaliesGroup" :key="index" class="anomaly-item">
            <div><strong>Parameter:</strong> {{ anomaly.parameter }} = {{ anomaly.value }}</div>
            <div><strong>Type:</strong> {{ anomaly.type }}</div>
            <div><strong>Time:</strong> {{ formatDate(anomaly.detectedAt) }}</div>
          </div>
        </div>
      </div>
    </div>
    <p v-else>Henüz anomali verisi yok.</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

const anomalies = ref([]); // WebSocket'ten gelen verileri tutacak
const groupedAnomalies = ref({}); // Konum bazında gruplanmış anomaliler

onMounted(() => {
  const socket = new SockJS('http://localhost:8080/ws'); // Spring tarafında tanımladığın endpoint
  const stompClient = new Client({
    webSocketFactory: () => socket,
    reconnectDelay: 5000, // Bağlantı koparsa 5 sn sonra tekrar dener
    onConnect: () => {
      console.log('WebSocket bağlandı');
      stompClient.subscribe('/topic/anomalies', (message) => {
        if (message.body) {
          const anomaly = JSON.parse(message.body);
          anomaly.timestamp = Date.now(); // Anomalinin geldiği zamanı ekle
          anomalies.value.push(anomaly); // Veriyi ekle
          localStorage.setItem('anomalies', JSON.stringify(anomalies.value)); // LocalStorage'e kaydet
          groupAnomaliesByLocation(); // Anomalileri grupla
          removeExpiredAnomalies(); // Süresi dolmuş verileri sil
        }
      });
    },
    onStompError: (frame) => {
      console.error('STOMP Hatası:', frame.headers['message']);
    }
  });

  stompClient.activate();

  // LocalStorage'den var olan anomalileri al
  const savedAnomalies = JSON.parse(localStorage.getItem('anomalies'));
  if (savedAnomalies) {
    anomalies.value = savedAnomalies;
    groupAnomaliesByLocation();
  }
});

// 10 dakika sonra otomatik olarak silme
function removeExpiredAnomalies() {
  const currentTime = Date.now();
  anomalies.value = anomalies.value.filter(anomaly => currentTime - anomaly.timestamp < 10 * 60 * 1000);
  localStorage.setItem('anomalies', JSON.stringify(anomalies.value)); // Güncel veriyi localStorage'e kaydet
  groupAnomaliesByLocation(); // Anomaliler tekrar gruplanmalı
}

// Anomalileri konumlarına göre grupla
function groupAnomaliesByLocation() {
  const grouped = anomalies.value.reduce((acc, anomaly) => {
    const location = `[${anomaly.latitude}, ${anomaly.longitude}]`; // Konum bilgisi
    if (!acc[location]) {
      acc[location] = [];
    }
    acc[location].push(anomaly);
    return acc;
  }, {});

  groupedAnomalies.value = grouped;
}

// Anomaliyi manuel olarak silme
function removeAnomalies(location) {
  delete groupedAnomalies.value[location];

  // Silinen konumun verilerini anomalies listesinden de kaldır
  anomalies.value = anomalies.value.filter(anomaly => `[${anomaly.latitude}, ${anomaly.longitude}]` !== location);

  // LocalStorage'den de sil
  localStorage.setItem('anomalies', JSON.stringify(anomalies.value));
}

// Tarih formatı
function formatDate(dateStr) {
  const date = new Date(dateStr);
  return date.toLocaleString('tr-TR');
}
</script>

<style scoped>
.anomaly-alerts {
  padding: 1rem;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background-color: #f4f7fb;
  color: #333;
}

h2 {
  font-size: 2rem;
  color: #2c3e50;
  margin-bottom: 1rem;
  text-align: center;
}

.anomaly-cards {
  display: flex;
  flex-wrap: wrap;
  gap: 1.5rem;
  justify-content: center;
}

.anomaly-card {
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
  padding: 1.5rem;
  width: 100%;
  max-width: 700px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  display: flex;
  flex-direction: column;
}

.anomaly-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.2);
}

.anomaly-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  font-weight: bold;
  color: #2c3e50;
}

.anomaly-location {
  font-size: 1.1rem;
}

.remove-btn {
  background-color: #e53935;
  color: #fff;
  border: none;
  padding: 0.6rem 1rem;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.remove-btn:hover {
  background-color: #c62828;
}

.anomaly-body {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  font-size: 1rem;
  color: #7f8c8d;
}

.anomaly-item {
  display: flex;
  flex-direction: column;
  margin-bottom: 0.8rem;
}

.anomaly-item div {
  margin: 0.5rem 0;
}

.anomaly-body strong {
  color: #34495e;
}

hr {
  margin: 1rem 0;
  border: 0.5px solid #ccc;
}

p {
  text-align: center;
  color: #7f8c8d;
}
</style>
