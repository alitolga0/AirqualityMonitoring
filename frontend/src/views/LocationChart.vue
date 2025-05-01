<template>
  <div>
    <!-- Latitude, Longitude Input Fields -->
    <div class="input-container">
      <input type="text" v-model="latitude" placeholder="Latitude" />
      <input type="text" v-model="longitude" placeholder="Longitude" />
      <button @click="fetchData">Analizi Başlat</button>
    </div>

    <!-- Map -->
    <div id="map"></div>

    <!-- Chart -->
    <div class="graphs-container">
      <div class="chart-container">
        <Bar v-if="chartData.labels.length" :data="chartData" :options="chartOptions" />
      </div>
    </div>
  </div>
</template>

<script>
import L from 'leaflet';
import 'leaflet.heat';
import axios from 'axios';
import { Bar } from 'vue-chartjs';
import { Chart as ChartJS, BarElement, CategoryScale, LinearScale, Tooltip, Legend, Title } from 'chart.js';

ChartJS.register(BarElement, CategoryScale, LinearScale, Tooltip, Legend, Title);

export default {
  name: 'MapPage',
  components: {
    Bar,
  },
  data() {
    return {
      map: null,
      heatLayer: null,
      anomalies: [],
      latitude: '',
      longitude: '',
      chartData: {
        labels: [],
        datasets: []
      },
      chartOptions: {
        responsive: true,
        plugins: {
          title: {
            display: true,
            text: 'Air Pollution Data',
          },
        },
      },
    };
  },
  mounted() {
    this.initMap();
  },
  methods: {
    initMap() {
      try {
        this.map = L.map('map', {
          zoomControl: false,
        }).setView([20, 0], 2);

        L.control.zoom({ position: 'topright' }).addTo(this.map);

        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
          attribution: '© OpenStreetMap contributors',
        }).addTo(this.map);

        this.heatLayer = L.heatLayer([], {
          radius: 25,
          blur: 15,
          maxZoom: 10,
          gradient: {
            0.1: 'blue',
            0.3: 'lime',
            0.5: 'yellow',
            0.7: 'orange',
            1.0: 'red',
          },
        }).addTo(this.map);
      } catch (error) {
        console.error('Map initialization error:', error);
      }
    },

    async fetchData() {
      const lat = parseFloat(this.latitude);
      const lng = parseFloat(this.longitude);
      
      if (isNaN(lat) || isNaN(lng) || lat < -90 || lat > 90 || lng < -180 || lng > 180) {
        alert('Please enter valid latitude (-90 to 90) and longitude (-180 to 180) values');
        return;
      }

      const apiUrl = `/api/airquality/findNearbyRecords?latitude=${lat}&longitude=${lng}`;

      try {
        const response = await axios.get(apiUrl);
        
        if (!response.data || !Array.isArray(response.data)) {
          throw new Error('Invalid data format received from API');
        }

        this.anomalies = response.data;
        this.updateHeatmap();
        this.centerMapOnLocation();
        this.updateChartData();

      } catch (error) {
        console.error('Data fetching error:', error);
        alert('Error fetching data: ' + error.message);
      }
    },

    updateHeatmap() {
      try {
        const heatMapPoints = this.anomalies.map((data) => {
          const intensity = data.pm25 ? Math.min(1, data.pm25 / 300) : 0.5;
          return [data.latitude, data.longitude, intensity];
        });
        this.heatLayer.setLatLngs(heatMapPoints);
      } catch (error) {
        console.error('Heatmap update error:', error);
      }
    },

    centerMapOnLocation() {
      try {
        this.map.setView(
          [parseFloat(this.latitude), parseFloat(this.longitude)], 
          10
        );
      } catch (error) {
        console.error('Map centering error:', error);
      }
    },

    updateChartData() {
  try {
    if (!this.anomalies || this.anomalies.length === 0) {
      this.chartData = { labels: [], datasets: [] };
      return;
    }

    // Grafik etiketleri (başlıklar)
    const chartLabels = ['PM25 Anomalı', 'PM10 Anomalı', 'NO2 Anomalı', 'SO2 Anomalı', 'O3 Anomalı'];
    
    // Tek veri seti oluşturuyoruz
    const chartDataset = {
      label: 'Anomali Değerleri',
      backgroundColor: chartLabels.map(label => this.getColorForParameter(label.split(' ')[0])),
      data: []
    };

    // Her parametre için ortalama değer hesapla
    chartLabels.forEach(label => {
      const param = label.split(' ')[0].toLowerCase();
      const values = this.anomalies.map(item => item[param] || 0);
      const avg = values.reduce((sum, val) => sum + val, 0) / values.length;
      chartDataset.data.push(avg);
    });

    this.chartData = {
      labels: chartLabels,
      datasets: [chartDataset]
    };

    // Grafik seçeneklerini güncelle
    this.chartOptions = {
      responsive: true,
      plugins: {
        title: {
          display: true,
          text: 'Hava Kirliliği Anomali Değerleri',
        },
        legend: {
          display: false // Tek bir veri seti olduğu için legend gerek yok
        }
      },
      scales: {
        y: {
          beginAtZero: true
        }
      }
    };

  } catch (error) {
    console.error('Grafik verisi güncelleme hatası:', error);
    this.chartData = { labels: [], datasets: [] };
  }
},

    getColorForParameter(parameter) {
      const colors = {
        'PM25': '#e53935',
        'PM10': '#1e88e5',
        'NO2': '#66bb6a',
        'SO2': '#ffb300',
        'O3': '#ff7043',
      };
      return colors[parameter] || '#000000';
    },
  },
};
</script>
<style scoped>
body {
  margin: 0;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background-color: #f0f4f8;
  color: #333;
}

#map {
  width: 100%;
  height: 50vh;
  min-height: 300px;
  border-bottom: 4px solid #42b983;
}

/* Input Alanı Kutusu */
.input-container {
  margin: 60px auto 30px;
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  align-items: center;
  background-color: #fff;
  padding: 25px 30px;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  max-width: 600px;
  width: 90%;
  box-sizing: border-box;
}

.input-container input {
  padding: 10px 12px;
  margin: 8px;
  border: 2px solid #42b983;
  border-radius: 8px;
  font-size: 16px;
  width: 140px; /* Genişlik düşürüldü */
  flex-shrink: 0;
  transition: border-color 0.3s ease;
}

.input-container input:focus {
  border-color: #2c8b6e;
  outline: none;
}

.input-container button {
  padding: 10px 16px;
  margin: 8px;
  background-color: #42b983;
  color: white;
  font-weight: bold;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.input-container button:hover {
  background-color: #2c8b6e;
}

.graphs-container {
  margin-top: 30px;
  display: flex;
  justify-content: center;
  padding: 0 20px;
  box-sizing: border-box;
}

.chart-container {
  width: 100%;
  max-width: 800px;
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  padding: 20px;
  box-sizing: border-box;
}

/* Mobil Görünüm */
@media (max-width: 768px) {
  .input-container {
    flex-direction: column;
    align-items: center;
    padding: 20px;
    margin: 40px 16px 20px;
  }

  .input-container input,
  .input-container button {
    width: 90%;
    max-width: 280px;
    margin: 6px 0;
  }

  .chart-container {
    padding: 16px;
  }

  canvas {
    width: 100% !important;
    height: auto !important;
  }
}



</style>