<template>
  <div class="anomaly-page">
    <h1 class="page-title">Son 1 Haftadaki Anormal Hava Kalitesi Değerleri</h1>

    <div v-if="anomalies.length === 0" class="no-anomalies">
      <p>Son 1 haftada herhangi bir anomali bulunamadı.</p>
    </div>

    <div v-else class="anomaly-list">
      <div
        v-for="anomaly in anomalies"
        :key="anomaly.id"
        class="anomaly-item"
        :class="getAnomalyClass(anomaly.parameter)"
      >
        <div class="anomaly-header">
          <strong>{{ formatLocation(anomaly.latitude, anomaly.longitude) }}</strong>
          <span class="anomaly-date">{{ formatDate(anomaly.detectedAt) }}</span>
        </div>
        <div class="anomaly-details">
          <p>{{ anomaly.parameter }}: <strong>{{ anomaly.value }} µg/m³</strong></p>
          <p><em>Tip: {{ anomaly.type }}</em></p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "AnomalyDetectionPage",
  data() {
    return {
      anomalies: [],
    };
  },
  methods: {
    async fetchAnomalies() {
      const now = new Date();
      const oneWeekAgo = new Date(now.getTime() - 168 * 60 * 60 * 1000); // 168 saat önce (1 hafta)

      const startTime = this.formatDateTime(oneWeekAgo);
      const endTime = this.formatDateTime(now);
      const apiUrl = `/api/airquality/anomalies?startTime=${startTime}&endTime=${endTime}`;

      try {
        const response = await fetch(apiUrl);
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        this.anomalies = await response.json();
        console.log("Son 1 Haftadaki Anomaliler:", this.anomalies);
      } catch (error) {
        console.error("Anomali verilerini çekerken hata oluştu:", error);
      }
    },
    formatDateTime(date) {
      const pad = (n) => n.toString().padStart(2, '0');
      return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
    },
    formatDate(dateTimeString) {
      const date = new Date(dateTimeString);
      const options = { year: 'numeric', month: 'long', day: 'numeric', hour: 'numeric', minute: 'numeric' };
      return date.toLocaleDateString('tr-TR', options);
    },
    formatLocation(latitude, longitude) {
      return `Enlem: ${latitude.toFixed(4)}, Boylam: ${longitude.toFixed(4)}`;
    },
    getAnomalyClass(pollutant) {
      switch (pollutant) {
        case "PM2.5":
          return "pm25";
        case "NO2":
          return "no2";
        case "PM10":
          return "pm10";
        case "SO2":
          return "so2";
        case "O3":
          return "o3";
        default:
          return "";
      }
    },
  },
  mounted() {
    this.fetchAnomalies();
  },
};
</script>

<style scoped>
.anomaly-page {
  padding: 20px;
}

.page-title {
  font-size: 2rem;
  font-weight: bold;
  color: #333;
  text-align: center;
  margin-bottom: 20px;
}

.no-anomalies {
  text-align: center;
  color: #777;
  font-style: italic;
}

.anomaly-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.anomaly-item {
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
}

.anomaly-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
}

.anomaly-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.anomaly-header strong {
  font-size: 1.2rem;
  color: #333;
}

.anomaly-date {
  font-size: 0.9rem;
  color: #777;
}

.anomaly-details {
  margin-top: 10px;
}

.anomaly-details p {
  font-size: 1rem;
  color: #555;
}

.anomaly-details strong {
  color: #d9534f; /* Kırmızı renkte önemli değer */
}

.anomaly-item.pm25 {
  border-left: 5px solid #d9534f; /* Kırmızı */
}

.anomaly-item.no2 {
  border-left: 5px solid #f0ad4e; /* Sarı */
}

.anomaly-item.pm10 {
  border-left: 5px solid #5bc0de; /* Mavi */
}

.anomaly-item.so2 {
  border-left: 5px solid #5cb85c; /* Yeşil */
}

.anomaly-item.o3 {
  border-left: 5px solid #0275d8; /* Lacivert */
}
</style>
