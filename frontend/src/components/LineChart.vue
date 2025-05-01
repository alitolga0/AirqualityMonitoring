<template>
  <div>
    <Line :data="chartData" :options="chartOptions" />
  </div>
</template>

<script>
import { Line } from 'vue-chartjs';
import { reactive, watch } from 'vue';


export default {
  name: 'LineChart',
  components: {
    Line
  },
  props: {
    data: Array
  },
  setup(props) {
    const chartData = reactive({
      labels: props.data.map(item => item.time),  
      datasets: [
        {
          label: 'Air Quality Levels',
          data: props.data.map(item => item.value),
          borderColor: 'rgba(75, 192, 192, 1)',
          backgroundColor: 'rgba(75, 192, 192, 0.2)',
          borderWidth: 1,
        }
      ]
    });

    const chartOptions = reactive({
      responsive: true,
      maintainAspectRatio: false,
      scales: {
        y: {
          beginAtZero: true,
        }
      }
    });

    // watch prop 'data' to update chartData when it changes
    watch(() => props.data, (newData) => {
      chartData.labels = newData.map(item => item.time);
      chartData.datasets[0].data = newData.map(item => item.value);
    });

    return { chartData, chartOptions };
  }
};
</script>

<style scoped>
.line-chart-container {
  width: 100%;
  height: 400px; 
  margin: 20px 0;
  background-color: #fff; 
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1); 
  padding: 10px;
  display: flex;
  justify-content: center;
  align-items: center;
}

canvas {
  max-width: 100%;
  max-height: 100%;
  display: block;
}
</style>