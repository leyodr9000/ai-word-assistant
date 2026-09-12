<template>
  <div class="min-h-screen w-full flex items-center justify-center relative overflow-hidden bg-slate-900">
    
    <!-- Dynamic Background Image -->
    <div 
      class="absolute inset-0 bg-cover bg-center transition-all duration-1000 ease-in-out z-0"
      :style="{ backgroundImage: `url(${backgroundImage})` }"
    ></div>

    <!-- Dark Overlay to ensure readability -->
    <div class="absolute inset-0 bg-black/30 z-10"></div>

    <!-- Settings Button (Top Right) -->
    <div class="absolute top-6 right-6 z-30">
      <input type="file" id="bg-upload" accept="image/*" class="hidden" @change="handleBgUpload">
      <label for="bg-upload" class="flex items-center gap-2 px-4 py-2 bg-white/20 hover:bg-white/30 backdrop-blur-md rounded-full text-white/90 text-sm cursor-pointer transition-all border border-white/20 shadow-lg group">
        <i class="fas fa-image group-hover:scale-110 transition-transform"></i> 
        <span>自定义背景</span>
      </label>
    </div>

    <!-- Login/Register Card -->
    <div class="relative z-20 w-full max-w-md p-8 md:p-10 mx-4 rounded-[2rem] bg-white/10 backdrop-blur-xl border border-white/20 shadow-2xl overflow-hidden">
      
      <!-- Subtle internal glow -->
      <div class="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-blue-400/50 via-purple-400/50 to-emerald-400/50"></div>
      
      <div class="text-center mb-8">
        <div class="w-16 h-16 rounded-2xl bg-white/20 flex items-center justify-center mx-auto mb-4 border border-white/30 shadow-inner">
          <i class="fas fa-book-reader text-3xl text-white"></i>
        </div>
        <h1 class="text-2xl font-bold text-white tracking-wide">AI 单词伴学</h1>
        <p class="text-white/60 text-sm mt-2">每天背一点，记得牢一点</p>
      </div>

      <!-- Mode Switcher -->
      <div class="flex p-1 bg-black/20 rounded-xl mb-8 border border-white/10 backdrop-blur-md">
        <button 
          @click="isLogin = true" 
          :class="['flex-1 py-2 rounded-lg text-sm font-medium transition-all active:scale-95', isLogin ? 'bg-white/20 text-white shadow-sm backdrop-blur-sm' : 'text-white/50 hover:text-white/80']"
        >
          登录
        </button>
        <button 
          @click="isLogin = false" 
          :class="['flex-1 py-2 rounded-lg text-sm font-medium transition-all active:scale-95', !isLogin ? 'bg-white/20 text-white shadow-sm backdrop-blur-sm' : 'text-white/50 hover:text-white/80']"
        >
          注册
        </button>
      </div>

      <!-- Form -->
      <form @submit.prevent="handleSubmit" class="space-y-5">
        <div>
          <label class="block text-white/70 text-xs font-medium mb-1.5 ml-1 uppercase tracking-wider">用户名</label>
          <div class="relative">
            <i class="fas fa-user absolute left-4 top-1/2 -translate-y-1/2 text-white/40"></i>
            <input 
              v-model="form.username" 
              type="text" 
              required
              class="w-full bg-black/20 backdrop-blur-sm border border-white/10 rounded-xl pl-11 pr-4 py-3 outline-none focus:border-white/40 focus:bg-black/30 text-white placeholder-white/30 transition-all shadow-inner"
              placeholder="输入您的账号"
            >
          </div>
        </div>
        
        <div>
          <label class="block text-white/70 text-xs font-medium mb-1.5 ml-1 uppercase tracking-wider">密码</label>
          <div class="relative">
            <i class="fas fa-lock absolute left-4 top-1/2 -translate-y-1/2 text-white/40"></i>
            <input 
              v-model="form.password" 
              type="password" 
              required
              class="w-full bg-black/20 backdrop-blur-sm border border-white/10 rounded-xl pl-11 pr-4 py-3 outline-none focus:border-white/40 focus:bg-black/30 text-white placeholder-white/30 transition-all shadow-inner"
              placeholder="输入密码"
            >
          </div>
        </div>

        <button 
          type="submit" 
          :disabled="isLoading"
          class="w-full mt-6 bg-gradient-to-r from-blue-500/90 to-indigo-600/90 hover:from-blue-600 hover:to-indigo-700 backdrop-blur-md text-white font-bold py-3.5 rounded-xl shadow-lg hover:shadow-xl transition-all transform active:scale-95 disabled:opacity-70 disabled:cursor-not-allowed border border-blue-400/30"
        >
          <span v-if="isLoading"><i class="fas fa-spinner fa-spin mr-2"></i> 请稍候...</span>
          <span v-else>{{ isLogin ? '进入学习' : '立即注册' }}</span>
        </button>
      </form>

      <!-- Divider -->
      <div class="relative flex items-center py-6">
        <div class="flex-grow border-t border-white/10"></div>
        <span class="flex-shrink-0 mx-4 text-white/40 text-xs font-medium tracking-widest">或者</span>
        <div class="flex-grow border-t border-white/10"></div>
      </div>

      <!-- Guest Login -->
      <button
        @click="handleGuestLogin"
        class="w-full bg-white/10 hover:bg-white/20 backdrop-blur-md border border-white/20 text-white/90 font-medium py-3 rounded-xl transition-all flex items-center justify-center gap-2 group active:scale-95 shadow-sm"
      >
        <i class="fas fa-user-secret group-hover:text-amber-400 transition-colors"></i> 游客免登录体验
      </button>

      <p class="text-center text-white/40 text-xs mt-4 leading-relaxed">
        系统内置管理员账号 <span class="text-white/70 font-bold">admin / admin123</span><br>
        登录后可在右上角 ⚙ 设置中修改密码
      </p>

    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const isLogin = ref(true)
const isLoading = ref(false)

// Background Image Logic
const defaultBg = 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?q=80&w=2073&auto=format&fit=crop'
const backgroundImage = ref(localStorage.getItem('custom_bg') || defaultBg)

const handleBgUpload = (e) => {
  const file = e.target.files[0]
  if (!file) return
  
  const reader = new FileReader()
  reader.onload = (event) => {
    const base64 = event.target.result
    backgroundImage.value = base64
    // Attempt to save to localStorage. May fail if image is too large (>5MB)
    try {
      localStorage.setItem('custom_bg', base64)
    } catch (err) {
      alert('图片过大，无法本地持久化保存，但当前会话已生效。建议裁剪后再上传。')
    }
  }
  reader.readAsDataURL(file)
}

const form = reactive({
  username: '',
  password: ''
})

const handleSubmit = async () => {
  isLoading.value = true
  try {
    const endpoint = isLogin.value ? '/api/auth/login' : '/api/auth/register'
    const res = await fetch(endpoint, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(form)
    })
    
    const data = await res.json()
    if (!res.ok) throw new Error(data.message || '请求失败')

    alert(data.message)
    
    if (isLogin.value) {
      // Save user info and redirect
      localStorage.setItem('current_user', JSON.stringify(data.user))
      if (data.user.role === 'ADMIN') {
        router.push('/admin')
      } else {
        router.push('/home')
      }
    } else {
      // Registration successful: Switch to login mode
      isLogin.value = true
      form.password = '' // Clear password for security
    }
    
  } catch (err) {
    alert(err.message)
  } finally {
    isLoading.value = false
  }
}

const handleGuestLogin = () => {
  // Clear any existing user
  localStorage.removeItem('current_user')
  router.push('/home')
}

// Redirect if already logged in
onMounted(() => {
  const userJson = localStorage.getItem('current_user')
  if (userJson) {
    try {
      const user = JSON.parse(userJson)
      if (user.role === 'ADMIN') {
        router.push('/admin')
      } else {
        router.push('/home')
      }
    } catch (e) {}
  }
})
</script>
