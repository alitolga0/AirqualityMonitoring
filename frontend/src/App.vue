<template>
  <div id="app">
    <div :class="['sidebar', { 'sidebar-open': sidebarOpen }]">
      <ul>
        <li @click="switchPage('map')">Harita</li>
        <li @click="switchPage('graphs')">Grafikler</li>
        <li @click="switchPage('anomaly-detection')">Anomali Tespiti</li>
        <li @click="switchPage('LocationChart')">Bölge Analizi</li>
        <li @click="switchPage('AnomalyAlerts')">Uyarılar</li>
      </ul>
    </div>

    <button class="sidebar-toggle" @click="toggleSidebar">
      <span v-if="!sidebarOpen">☰</span>
      <span v-else>×</span>
    </button>

    <div :class="['content', { 'content-shifted': sidebarOpen }]">
      <div v-if="currentPage === 'map'">
        <MapPage />
      </div>
      <div v-if="currentPage === 'graphs'">
        <GraphsPage />
      </div>
      <div v-if="currentPage === 'anomaly-detection'">
        <AnomalyDetectionPage />
      </div>
      <div v-if="currentPage === 'LocationChart'">
        <LocationChart />
      </div>
      <div v-if="currentPage === 'AnomalyAlerts'">
        <AnomalyAlert />
      </div>
    </div>
  </div>
</template>

<script>
import LocationChart from './views/LocationChart.vue';
import MapPage from './views/MapPage.vue';
import GraphsPage from './views/GraphsPage.vue';
import AnomalyDetectionPage from './views/AnomalyDetectionPage.vue';
import AnomalyAlert from './views/AnomalyAlerts.vue';

export default {
  name: 'App',
  components: {
    MapPage,
    GraphsPage,
    AnomalyDetectionPage,
    LocationChart,
    AnomalyAlert,
  },
  data() {
    return {
      currentPage: 'map',  
      sidebarOpen: false,  
    };
  },
  methods: {
    switchPage(page) {
      this.currentPage = page;
      if (this.sidebarOpen) {
        this.toggleSidebar(); 
      }
    },
    toggleSidebar() {
      this.sidebarOpen = !this.sidebarOpen;
    },
  },
};
</script>

<style scoped>
#app {
  display: flex;
  height: 100dvh; /* Dynamic viewport height kullan */
  transition: all 0.3s ease;
  padding-left: 250px; /* Sidebar boşluğu */
}

.sidebar {
  width: 250px;
  background-color: #2c3e50;
  color: white;
  padding: 20px 0;
  display: flex;
  flex-direction: column;
  position: fixed;
  left: 0;
  top: 0;
  height: 100%;
  transition: transform 0.3s ease, width 0.3s ease;
  transform: translateX(0);
  z-index: 1000;
}

.sidebar ul {
  list-style-type: none;
  padding: 0;
  margin: 0;
}

.sidebar li {
  padding: 15px 20px;
  font-size: 18px;
  cursor: pointer;
  border-bottom: 1px solid #444;
}

.sidebar li:hover {
  background-color: #34495e;
  border-left: 4px solid #1abc9c;
}

.content {
  flex-grow: 1;
  padding: 0;
  background-color: white;
  overflow-y: auto;
  transition: margin-left 0.3s ease, width 0.3s ease;
}

.content-shifted {
  margin-left: 0;
}

.sidebar-toggle {
  display: none;
  position: absolute;
  top: 20px;
  left: 20px;
  font-size: 30px;
  background-color: transparent;
  border: none;
  color: #333;
  z-index: 1100; 
  cursor: pointer;
}

@media (max-width: 768px) {
  .sidebar {
    width: 70%;
    transform: translateX(-100%);
  }

  .sidebar-open {
    transform: translateX(0);
  }

  .content {
    margin-left: 0;
    padding: 0; 
  }

  #app {
    padding-left: 0;
  }

  .sidebar-toggle {
    display: block;
  }
}
</style>
