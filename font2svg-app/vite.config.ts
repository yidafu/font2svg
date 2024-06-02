import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react-swc';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/files': 'http://127.0.0.1:8888',
      '/tasks': 'http://127.0.0.1:8888',
      '/fonts': 'http://127.0.0.1:8888',
    },
  },
});
