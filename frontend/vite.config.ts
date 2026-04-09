import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, '.', '')
  const backendUrl = env.VITE_BACKEND_URL || env.VITE_API_URL || 'http://localhost:8080'

  return {
    plugins: [react()],
    cacheDir: '.vite',
    server: {
      proxy: {
        '/api': backendUrl,
        '/auth': backendUrl,
        '/public': backendUrl,
        '/oauth2': backendUrl,
        '/logout': backendUrl,
        '/me': backendUrl,
      },
    },
  }
})
