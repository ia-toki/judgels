import react from '@vitejs/plugin-react';
import { defineConfig } from 'vitest/config';

export default defineConfig({
  plugins: [react()],
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: ['./src/setupTests.js'],
    include: ['src/**/__tests__/**/*.{js,jsx}', 'src/**/*.{spec,test}.{js,jsx}'],
    css: true,
    mockReset: true,
  },
});
