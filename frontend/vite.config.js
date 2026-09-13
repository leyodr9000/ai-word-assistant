import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// base 策略:
//   --mode standalone  → './'   (单机版: 本地 file:// / Capacitor APK, 资源相对引用)
//   默认               → '/'    (服务端版: 部署在域名根路径)
export default defineConfig(({ mode }) => ({
  base: mode === 'standalone' ? './' : '/',
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
}))
