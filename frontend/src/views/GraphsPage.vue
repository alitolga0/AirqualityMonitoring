<template>
  <div class="graphs-container">
    <!-- PM2.5 Grafik -->
    <div class="chart-container">
      <Bar v-if="pm25Data" :data="pm25Data" :options="chartOptions" />
    </div>

    <!-- PM10 Grafik -->
    <div class="chart-container">
      <Bar v-if="pm10Data" :data="pm10Data" :options="chartOptions" />
    </div>

    <!-- NO2 Grafik -->
    <div class="chart-container">
      <Bar v-if="no2Data" :data="no2Data" :options="chartOptions" />
    </div>

    <!-- SO2 Grafik -->
    <div class="chart-container">
      <Bar v-if="so2Data" :data="so2Data" :options="chartOptions" />
    </div>

    <!-- O3 Grafik -->
    <div class="chart-container">
      <Bar v-if="o3Data" :data="o3Data" :options="chartOptions" />
    </div>
  </div>
</template>

<script>
import {
  Chart as ChartJS,
  BarElement,
  CategoryScale,
  LinearScale,
  Tooltip,
  Legend,
  Title,
} from 'chart.js';
import { Bar } from 'vue-chartjs';
import { ref, onMounted } from 'vue';
import axios from 'axios';

ChartJS.register(BarElement, CategoryScale, LinearScale, Tooltip, Legend, Title);

export default {
  name: 'GraphsPage',
  components: {
    Bar,
  },
  setup() {
    // Grafikleri için veri yapıları
    const pm25Data = ref(null);
    const pm10Data = ref(null);
    const no2Data = ref(null);
    const so2Data = ref(null);
    const o3Data = ref(null);

    // Bugünün tarihi
    const currentDate = new Date();
    // 3 gün öncesi
    const threeDaysAgo = new Date(currentDate);
    threeDaysAgo.setDate(currentDate.getDate() - 3);

    // Formatları: YYYY-MM-DDT00:00:00 şeklinde alıyoruz
    const startDate = `${threeDaysAgo.getFullYear()}-${String(threeDaysAgo.getMonth() + 1).padStart(2, '0')}-${String(threeDaysAgo.getDate()).padStart(2, '0')}T00:00:00`;
    const endDate = `${currentDate.getFullYear()}-${String(currentDate.getMonth() + 1).padStart(2, '0')}-${String(currentDate.getDate()).padStart(2, '0')}T23:59:59`;

    // Grafik başlığında kullanacağımız tarih
    const chartTitle = `${currentDate.toLocaleString('default', { month: 'long' })} ${currentDate.getFullYear()}`;

    const chartOptions = {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          position: 'top',
        },
        title: {
          display: true,
          text: chartTitle, // Başlık dinamik olarak ayarlanıyor
        },
      },
      scales: {
        y: {
          beginAtZero: true,
          title: {
            display: true,
            text: 'Anomali Sayısı',
          },
        },
        x: {
          title: {
            display: true,
            text: 'Bölgeler',
          },
        },
      },
    };

    onMounted(async () => {
      try {
        // API'ye veri isteği
        const response = await axios.get('/api/airquality/anomalies', {
          params: {
            startTime: startDate, // 3 gün öncesinden bugüne kadar
            endTime: endDate, // Bugün
          },
        });

        const data = response.data;
        console.log("API'den gelen veri:", data.slice(0, 5)); // Kontrol için

        // Parametrelere göre anomali sayısını hesapla
        const anomalyCountByParameter = data.reduce((acc, item) => {
          const param = item.parameter;  // 'PM2.5', 'O3' gibi değerler
          if (!acc[param]) {
            acc[param] = 0;
          }
          // 'value' belirli bir eşiği aşarsa anomali olarak sayabiliriz
          if (item.value > 0) {  // Geçici olarak bu satırın filtresini kaldırdık
            acc[param]++;
          }
          return acc;
        }, {});

        // Grafik verilerini ayrı ayrı oluşturuyoruz
        pm25Data.value = {
          labels: ['PM2.5'],
          datasets: [
            {
              label: 'PM2.5 Anomali Sayısı',
              backgroundColor: '#e53935',
              data: [anomalyCountByParameter['PM2.5'] || 0],
            },
          ],
        };

        pm10Data.value = {
          labels: ['PM10'],
          datasets: [
            {
              label: 'PM10 Anomali Sayısı',
              backgroundColor: '#1e88e5',
              data: [anomalyCountByParameter['PM10'] || 0],
            },
          ],
        };

        no2Data.value = {
          labels: ['NO2'],
          datasets: [
            {
              label: 'NO2 Anomali Sayısı',
              backgroundColor: '#66bb6a',
              data: [anomalyCountByParameter['NO2'] || 0],
            },
          ],
        };

        so2Data.value = {
          labels: ['SO2'],
          datasets: [
            {
              label: 'SO2 Anomali Sayısı',
              backgroundColor: '#ffb300',
              data: [anomalyCountByParameter['SO2'] || 0],
            },
          ],
        };

        o3Data.value = {
          labels: ['O3'],
          datasets: [
            {
              label: 'O3 Anomali Sayısı',
              backgroundColor: '#ff7043',
              data: [anomalyCountByParameter['O3'] || 0],
            },
          ],
        };

        console.log("Grafik verisi:", {
          pm25Data: pm25Data.value,
          pm10Data: pm10Data.value,
          no2Data: no2Data.value,
          so2Data: so2Data.value,
          o3Data: o3Data.value,
        });
      } catch (error) {
        console.error('Veri alınamadı:', error);
      }
    });

    return {
      pm25Data,
      pm10Data,
      no2Data,
      so2Data,
      o3Data,
      chartOptions,
    };
  },
};
</script>

<style scoped>
.graphs-container {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  flex-wrap: wrap;
}

.chart-container {
  width: 48%;
  height: 400px;
}

@media (max-width: 768px) {
  .chart-container {
    width: 100%;
  }
}
</style>
