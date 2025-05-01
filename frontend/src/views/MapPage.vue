<template>
  <div id="map"></div>
</template>

<script>
import L from "leaflet";
import "leaflet.heat";
import axios from "axios";

// Marker görünmeme sorununu çözen importlar:
import markerIcon from "leaflet/dist/images/marker-icon.png";
import markerShadow from "leaflet/dist/images/marker-shadow.png";

delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconUrl: markerIcon,
  shadowUrl: markerShadow,
});

// WebSocket için gerekli
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

export default {
  name: "MapPage",
  data() {
    return {
      map: null,
      heatLayer: null,
      anomalies: [],
      anomalyMarkers: [],
      anomalyTTL: 3600000,
    };
  },

  mounted() {
    this.initMap();
    this.fetchAnomalies();
    this.connectWebSocket();
    this.loadStoredMarkers();
  },

  methods: {
    initMap() {
      this.map = L.map("map", {
        zoomControl: false,
      }).setView([20, 0], 2);

      L.control.zoom({ position: "topright" }).addTo(this.map);

      L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
        attribution: "© OpenStreetMap contributors",
      }).addTo(this.map);

      this.heatLayer = L.heatLayer([], {
        radius: 25,
        blur: 15,
        maxZoom: 10,
        gradient: {
          0.1: "blue",
          0.3: "lime",
          0.5: "yellow",
          0.7: "orange",
          1.0: "red",
        },
      }).addTo(this.map);

      window.addEventListener("resize", () => {
        if (this.map) {
          this.map.invalidateSize();
        }
      });
    },

    async fetchAnomalies() {
      const now = new Date();
      const endDate = new Date(now);
      endDate.setDate(now.getDate() + 1); // yarın

      const startDate = new Date(now);
      startDate.setDate(now.getDate() - 10); // 10 gün öncesi

      const toISOStringWithoutMs = (date) => date.toISOString().split(".")[0]; // yyyy-MM-ddTHH:mm:ss

      const startTime = toISOStringWithoutMs(startDate);
      const endTime = toISOStringWithoutMs(endDate);

      const apiUrl = `/api/airquality/anomalies?startTime=${startTime}&endTime=${endTime}`;

      try {
        const response = await axios.get(apiUrl);
        console.log("Anomali verileri alındı:", response.data);
        this.anomalies = response.data;
        this.updateHeatmap();
      } catch (error) {
        console.error("Anomali verilerini alma hatası:", error);
      }
    },

    updateHeatmap() {
      const heatMapPoints = this.anomalies.map((data) => {
        const intensity = data.pm25 ? Math.min(1, data.pm25 / 300) : 0.5;
        return [data.latitude, data.longitude, intensity];
      });

      this.heatLayer.setLatLngs(heatMapPoints);
    },

    connectWebSocket() {
      const socket = new SockJS("http://localhost:8080/ws");
      const stompClient = new Client({
        webSocketFactory: () => socket,
        reconnectDelay: 5000,
        onConnect: () => {
          console.log("Harita WebSocket bağlantısı kuruldu.");
          stompClient.subscribe("/topic/anomalies", (message) => {
            if (message.body) {
              const anomaly = JSON.parse(message.body);
              this.addAnomalyMarker(anomaly);
            }
          });
        },
        onStompError: (frame) => {
          console.error("Harita STOMP hatası:", frame.headers["message"]);
        },
      });

      stompClient.activate();
    },

    addAnomalyMarker(anomaly) {
      const { latitude, longitude, parameter, value, messageId } = anomaly;

      const savedAnomalies =
        JSON.parse(localStorage.getItem("anomalies")) || [];
      const alreadyExists = savedAnomalies.some(
        (item) => item.messageId === messageId
      );
      if (alreadyExists) {
        console.log(`Zaten eklenmiş mesaj: ${messageId}`);
        return;
      }

      const marker = L.marker([latitude, longitude])
        .bindPopup(`<b>Anomali!</b><br>${parameter} = ${value}`)
        .addTo(this.map);
      this.anomalyMarkers.push(marker);

      savedAnomalies.push({
        ...anomaly,
        timestamp: new Date().getTime(),
      });
      localStorage.setItem("anomalies", JSON.stringify(savedAnomalies));

      // 💡 Heatmap güncelle
      this.anomalies.push(anomaly);
      this.updateHeatmap();

      setTimeout(() => {
        this.removeAnomalyMarker(marker, messageId);
      }, this.anomalyTTL);
    },

    addStoredAnomalyMarker(anomaly, remainingTime) {
      const { latitude, longitude, parameter, value, messageId } = anomaly;

      const marker = L.marker([latitude, longitude])
        .bindPopup(`<b>Kaydedilmiş Anomali</b><br>${parameter} = ${value}`)
        .addTo(this.map);
      this.anomalyMarkers.push(marker);

      setTimeout(() => {
        this.removeAnomalyMarker(marker, messageId);
      }, remainingTime);
    },

    removeAnomalyMarker(marker, messageId) {
      this.map.removeLayer(marker);
      this.anomalyMarkers = this.anomalyMarkers.filter((m) => m !== marker);

      const savedAnomalies =
        JSON.parse(localStorage.getItem("anomalies")) || [];
      const updatedAnomalies = savedAnomalies.filter(
        (a) => a.messageId !== messageId
      );
      localStorage.setItem("anomalies", JSON.stringify(updatedAnomalies));
    },

    loadStoredMarkers() {
      const savedAnomalies =
        JSON.parse(localStorage.getItem("anomalies")) || [];
      const currentTime = new Date().getTime();
      const validAnomalies = [];

      savedAnomalies.forEach((anomaly) => {
        const elapsedTime = currentTime - anomaly.timestamp;

        if (elapsedTime <= this.anomalyTTL) {
          const remainingTime = this.anomalyTTL - elapsedTime;
          this.addStoredAnomalyMarker(anomaly, remainingTime);
          validAnomalies.push(anomaly);
        }
      });

      localStorage.setItem("anomalies", JSON.stringify(validAnomalies));
    },
  },
};
</script>

<style scoped>
#map {
  width: 100%;
  height: 100dvh;
  z-index: 0;
}
</style>
