import react from '@vitejs/plugin-react';
import { defineConfig } from 'vite';
import { viteStaticCopy } from 'vite-plugin-static-copy';

export default defineConfig({
  plugins: [
    react(),
    viteStaticCopy({
      targets: [
        {
          src: 'node_modules/tinymce/*',
          dest: 'tinymce',
        },
      ],
    }),
  ],

  define: {
    global: 'globalThis',
  },

  server: {
    port: 3000,
    host: '0.0.0.0',
    open: false,
    proxy: {
      '/api/v2': {
        target: 'http://localhost:9101',
        changeOrigin: true,
      },
    },
  },

  build: {
    outDir: 'build',
  },
});
