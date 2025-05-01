import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';

export default defineConfig({
  plugins: [vue()],
  define: {
    'global': 'window'
  },
  server: {
    proxy: {
      // HTTP API istekleri
      '/api': {
        target: 'http://app:8080',
        changeOrigin: true,
      },
      // WebSocket istekleri
      '/ws': {
        target: 'http://app:8080',
        ws: true,
        changeOrigin: true,
      },
    }
  }
});
