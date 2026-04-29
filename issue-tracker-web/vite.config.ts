import { defineConfig } from "vite";
import react from "@vitejs/plugin-react-swc";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  define: {
    global: "globalThis",
  },
  build: {
    manifest: true,
    rollupOptions: {
      output: {
        entryFileNames: `assets/[name].js`,
        chunkFileNames: `assets/[name].js`,
        assetFileNames: `assets/[name].[ext]`,
      },
    },
  },
  server: {
    port: 5174,
    host: true,
    strictPort: true,
    hmr: {
      host: 'localhost',
      port: 5174,
    },

    proxy: {
      "/api": {
        target: "http://localhost:6969",
        changeOrigin: true,
        secure: false,
      },
      "/chat": {
        target: "http://localhost:5001",
        changeOrigin: true,
        secure: false,
      },
      "/chat/feedback": {
        target: "http://localhost:5001",
        changeOrigin: true,
        secure: false,
      },
    },
    headers: {
      "Service-Worker-Allowed": "/",
    },
  },
});