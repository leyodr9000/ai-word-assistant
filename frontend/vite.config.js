import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// base 可用环境变量覆盖:
//   VITE_BASE=./            → 本地 file:// / Capacitor 打包
//   VITE_BASE=/ai-word-assistant/ → GitHub Pages 子路径部署
const base = process.env.VITE_BASE || '/'

export default defineConfig({
  base,
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
