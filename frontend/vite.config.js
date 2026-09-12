import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      // 开发环境: 前端请求同源 /api, 由 Vite 转发到 Spring Boot,
      // 生产(绿色版单 JAR)本身同源, 无需代理
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
      },
    },
  },
})
