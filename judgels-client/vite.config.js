import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import { viteStaticCopy } from 'vite-plugin-static-copy';

export default defineConfig({
  plugins: [
    react(),
    viteStaticCopy({
      targets: [
        {
          src: 'node_modules/tinymce/*',
          dest: 'tinymce'
        }
      ]
    })
  ],

  define: {
    global: 'globalThis',
  },

  server: {
    port: 3000,
    host: '0.0.0.0',
    open: false,
  },

  build: {
    outDir: 'build',
  },
});
