import { createApp } from 'vue';
import App from './App.vue';
import 'leaflet/dist/leaflet.css'; 
import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend } from 'chart.js';


ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend);

createApp(App).mount('#app');
