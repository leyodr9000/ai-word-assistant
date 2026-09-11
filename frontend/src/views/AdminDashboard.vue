<template>
  <div class="flex h-screen w-full bg-slate-50 text-slate-800 font-sans">
    <!-- Sidebar Navigation -->
    <aside class="w-64 bg-white border-r border-slate-200 flex flex-col">
      <div class="p-6 border-b border-slate-100 flex items-center gap-3">
        <div class="w-8 h-8 rounded-lg bg-blue-600 text-white flex items-center justify-center">
          <i class="fas fa-shield-alt"></i>
        </div>
        <h1 class="font-bold text-lg tracking-tight">Admin Center</h1>
      </div>
      
      <nav class="flex-1 p-4 space-y-2">
        <a @click="currentTab = 'dashboard'" :class="['flex items-center gap-3 px-4 py-3 rounded-xl cursor-pointer transition-colors', currentTab === 'dashboard' ? 'bg-blue-50 text-blue-600 font-medium' : 'hover:bg-slate-50 text-slate-600']">
          <i class="fas fa-chart-pie w-5"></i> 控制面板
        </a>
        <a @click="currentTab = 'users'" :class="['flex items-center gap-3 px-4 py-3 rounded-xl cursor-pointer transition-colors', currentTab === 'users' ? 'bg-blue-50 text-blue-600 font-medium' : 'hover:bg-slate-50 text-slate-600']">
          <i class="fas fa-users w-5"></i> 用户管理
        </a>
        <a @click="currentTab = 'ai-config'" :class="['flex items-center gap-3 px-4 py-3 rounded-xl cursor-pointer transition-colors', currentTab === 'ai-config' ? 'bg-blue-50 text-blue-600 font-medium' : 'hover:bg-slate-50 text-slate-600']">
          <i class="fas fa-robot w-5"></i> AI 模型配置
        </a>
      </nav>

      <div class="p-4 border-t border-slate-100">
        <router-link to="/" class="flex items-center gap-3 px-4 py-3 rounded-xl hover:bg-slate-50 text-slate-600 transition-colors">
          <i class="fas fa-sign-out-alt w-5"></i> 返回前台
        </router-link>
      </div>
    </aside>

    <!-- Main Content -->
    <main class="flex-1 overflow-y-auto p-8">
      
      <!-- Tab: Dashboard -->
      <div v-if="currentTab === 'dashboard'" class="max-w-5xl mx-auto space-y-6">
        <div>
          <h2 class="text-2xl font-bold text-slate-800 mb-2">控制面板</h2>
          <p class="text-slate-500">查看系统当前的运行状态和访问数据。</p>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mt-6">
          <div class="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm flex flex-col">
            <div class="flex items-center gap-3 text-slate-500 mb-4">
              <i class="fas fa-eye text-indigo-500 bg-indigo-50 p-2 rounded-lg"></i>
              <span class="font-medium">总访问次数</span>
            </div>
            <div class="text-4xl font-bold text-slate-800">{{ stats.visitCount || 0 }}</div>
            <div class="text-sm text-emerald-500 mt-2 font-medium"><i class="fas fa-arrow-up"></i> +12% 较昨日</div>
          </div>
          
          <div class="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm flex flex-col">
            <div class="flex items-center gap-3 text-slate-500 mb-4">
              <i class="fas fa-microchip text-rose-500 bg-rose-50 p-2 rounded-lg"></i>
              <span class="font-medium">CPU 负载 (模拟)</span>
            </div>
            <div class="text-4xl font-bold text-slate-800">{{ stats.cpuLoad || '2.4' }}%</div>
            <div class="w-full bg-slate-100 rounded-full h-1.5 mt-4">
              <div class="bg-rose-500 h-1.5 rounded-full" :style="{ width: `${stats.cpuLoad || 2.4}%` }"></div>
            </div>
          </div>

          <div class="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm flex flex-col">
            <div class="flex items-center gap-3 text-slate-500 mb-4">
              <i class="fas fa-memory text-amber-500 bg-amber-50 p-2 rounded-lg"></i>
              <span class="font-medium">内存占用 (模拟)</span>
            </div>
            <div class="text-4xl font-bold text-slate-800">{{ stats.memoryUsage || '45' }}%</div>
            <div class="w-full bg-slate-100 rounded-full h-1.5 mt-4">
              <div class="bg-amber-500 h-1.5 rounded-full" :style="{ width: `${stats.memoryUsage || 45}%` }"></div>
            </div>
          </div>
        </div>
        
        <div class="bg-white border border-slate-200 rounded-2xl p-6 shadow-sm mt-6">
          <h3 class="font-bold text-lg mb-4">近期活动</h3>
          <div class="flex flex-col gap-4 text-sm text-slate-600">
            <div class="flex justify-between items-center py-2 border-b border-slate-50">
              <span class="flex items-center gap-2"><i class="fas fa-circle text-[8px] text-emerald-500"></i> 系统正常启动</span>
              <span class="text-slate-400">10分钟前</span>
            </div>
            <div class="flex justify-between items-center py-2 border-b border-slate-50">
              <span class="flex items-center gap-2"><i class="fas fa-circle text-[8px] text-blue-500"></i> 用户配置了新模型: DeepSeek</span>
              <span class="text-slate-400">1小时前</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Tab: Users -->
      <div v-if="currentTab === 'users'" class="max-w-5xl mx-auto space-y-6">
        <div class="flex justify-between items-center">
          <div>
            <h2 class="text-2xl font-bold text-slate-800 mb-2">用户管理</h2>
            <p class="text-slate-500">查看和管理系统中注册的所有用户数据。</p>
          </div>
          <button @click="loadUsers" class="px-4 py-2 bg-white border border-slate-200 text-slate-600 rounded-xl hover:bg-slate-50 transition-colors shadow-sm flex items-center gap-2 font-medium">
            <i class="fas fa-sync-alt" :class="{'fa-spin': isLoadingUsers}"></i> 刷新数据
          </button>
        </div>

        <div class="bg-white border border-slate-200 rounded-2xl shadow-sm overflow-hidden mt-6">
          <div class="overflow-x-auto">
            <table class="w-full text-left text-sm text-slate-600">
              <thead class="bg-slate-50 text-slate-700 font-bold border-b border-slate-200 uppercase text-xs">
                <tr>
                  <th scope="col" class="px-6 py-4">ID</th>
                  <th scope="col" class="px-6 py-4">用户名</th>
                  <th scope="col" class="px-6 py-4">角色</th>
                  <th scope="col" class="px-6 py-4">注册时间</th>
                  <th scope="col" class="px-6 py-4 text-right">操作</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-slate-100">
                <tr v-if="users.length === 0" class="hover:bg-slate-50/50">
                  <td colspan="5" class="px-6 py-8 text-center text-slate-400">暂无用户数据</td>
                </tr>
                <tr v-for="user in users" :key="user.id" class="hover:bg-slate-50 transition-colors">
                  <td class="px-6 py-4 font-mono">{{ user.id }}</td>
                  <td class="px-6 py-4 font-medium text-slate-800 flex items-center gap-2">
                    <div class="w-6 h-6 rounded-full bg-slate-100 flex items-center justify-center text-slate-400 text-xs">
                      <i class="fas fa-user"></i>
                    </div>
                    {{ user.username }}
                  </td>
                  <td class="px-6 py-4">
                    <span :class="['px-2.5 py-1 rounded-full text-xs font-bold border', user.role === 'ADMIN' ? 'bg-blue-50 text-blue-600 border-blue-200' : 'bg-slate-50 text-slate-600 border-slate-200']">
                      {{ user.role || 'USER' }}
                    </span>
                  </td>
                  <td class="px-6 py-4 text-slate-500">{{ user.createdAt ? new Date(user.createdAt).toLocaleString() : '-' }}</td>
                  <td class="px-6 py-4 text-right">
                    <button v-if="user.role !== 'ADMIN'" @click="deleteUser(user.id, user.username)" class="text-red-500 hover:text-white border border-red-200 hover:bg-red-500 px-3 py-1.5 rounded-lg transition-all text-xs font-bold shadow-sm">
                      <i class="fas fa-trash-alt mr-1"></i> 删除
                    </button>
                    <span v-else class="text-slate-400 text-xs italic">不可删除</span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- Tab: AI Config -->
      <div v-if="currentTab === 'ai-config'" class="max-w-3xl mx-auto space-y-6">
        <div>
          <h2 class="text-2xl font-bold text-slate-800 mb-2">AI 模型配置</h2>
          <p class="text-slate-500">配置前台单词助手所使用的底层大模型服务接口，支持 OpenAI 兼容格式的各类厂商接口。</p>
        </div>

        <div class="bg-white border border-slate-200 shadow-sm rounded-2xl overflow-hidden">
          <div class="p-6 space-y-6">
            
            <!-- Provider Selection -->
            <div>
              <label class="block text-sm font-bold text-slate-700 mb-2">AI 服务提供商 (自动应用默认地址)</label>
              <div class="grid grid-cols-3 gap-4">
                <div @click="setProvider('DeepSeek', 'https://api.deepseek.com', 'deepseek-chat')" 
                     :class="['border rounded-xl p-4 flex flex-col items-center justify-center cursor-pointer transition-all', config.activeModel === 'DeepSeek' ? 'border-blue-500 bg-blue-50 shadow-sm ring-2 ring-blue-500/20' : 'border-slate-200 hover:bg-slate-50 hover:border-slate-300']">
                  <div class="w-10 h-10 bg-blue-600 rounded-full flex items-center justify-center text-white mb-2 shadow-sm font-black text-xl">D</div>
                  <span class="text-sm font-bold text-slate-700">DeepSeek</span>
                </div>
                
                <div @click="setProvider('Doubao', 'https://ark.cn-beijing.volces.com/api/v3', 'ep-xxx')" 
                     :class="['border rounded-xl p-4 flex flex-col items-center justify-center cursor-pointer transition-all', config.activeModel === 'Doubao' ? 'border-indigo-500 bg-indigo-50 shadow-sm ring-2 ring-indigo-500/20' : 'border-slate-200 hover:bg-slate-50 hover:border-slate-300']">
                  <div class="w-10 h-10 bg-indigo-600 rounded-full flex items-center justify-center text-white mb-2 shadow-sm font-black text-xl">豆</div>
                  <span class="text-sm font-bold text-slate-700">字节豆包</span>
                </div>

                <div @click="setProvider('Custom', '', '')" 
                     :class="['border rounded-xl p-4 flex flex-col items-center justify-center cursor-pointer transition-all', config.activeModel === 'Custom' ? 'border-slate-700 bg-slate-100 shadow-sm ring-2 ring-slate-500/20' : 'border-slate-200 hover:bg-slate-50 hover:border-slate-300']">
                  <div class="w-10 h-10 bg-slate-700 rounded-full flex items-center justify-center text-white mb-2 shadow-sm font-black text-xl"><i class="fas fa-cube"></i></div>
                  <span class="text-sm font-bold text-slate-700">自定义模型</span>
                </div>
              </div>
            </div>

            <div class="h-px bg-slate-100 my-4"></div>

            <!-- Base URL -->
            <div>
              <label class="block text-sm font-bold text-slate-700 mb-2">接口地址 (Base URL)</label>
              <div class="relative">
                <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-400">
                  <i class="fas fa-link"></i>
                </div>
                <input v-model="config.baseUrl" type="text" placeholder="https://api.deepseek.com" class="w-full bg-slate-50 border border-slate-300 text-slate-700 py-3 pl-10 pr-4 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 font-mono text-sm shadow-sm inset-shadow-sm">
              </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <!-- API Key -->
              <div>
                <label class="block text-sm font-bold text-slate-700 mb-2">API 密钥 (API Key)</label>
                <div class="relative">
                  <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-400">
                    <i class="fas fa-key"></i>
                  </div>
                  <input v-model="config.apiKey" type="password" placeholder="sk-..." class="w-full bg-slate-50 border border-slate-300 text-slate-700 py-3 pl-10 pr-4 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 font-mono text-sm shadow-sm inset-shadow-sm">
                </div>
              </div>
              
              <!-- Model Name -->
              <div>
                <label class="block text-sm font-bold text-slate-700 mb-2">模型标识名称 (Model Name)</label>
                <div class="relative">
                  <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-400">
                    <i class="fas fa-tag"></i>
                  </div>
                  <input v-model="config.modelName" type="text" placeholder="例如: deepseek-chat" class="w-full bg-slate-50 border border-slate-300 text-slate-700 py-3 pl-10 pr-4 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 font-mono text-sm shadow-sm inset-shadow-sm">
                </div>
              </div>
            </div>
            
            <!-- Temperature -->
            <div>
              <label class="flex justify-between text-sm font-bold text-slate-700 mb-2">
                <span>思维发散度 (Temperature)</span>
                <span class="text-blue-600 bg-blue-50 px-2 rounded-md">{{ config.temperature }}</span>
              </label>
              <input v-model="config.temperature" type="range" min="0" max="2" step="0.1" class="w-full accent-blue-600">
              <div class="flex justify-between text-xs text-slate-400 mt-1">
                <span>0.0 严谨准确</span>
                <span>2.0 天马行空</span>
              </div>
            </div>

          </div>
          
          <!-- Actions & Test -->
          <div class="bg-slate-50 px-6 py-4 border-t border-slate-200 flex flex-col md:flex-row justify-between items-center gap-4">
            
            <div class="flex items-center gap-2">
              <button @click="testConnection" :disabled="isTesting" class="px-4 py-2 rounded-xl bg-white border border-slate-300 text-slate-700 font-bold hover:bg-slate-100 transition-colors shadow-sm flex items-center gap-2 disabled:opacity-50">
                <i :class="['fas fa-wifi', isTesting ? 'animate-pulse text-amber-500' : 'text-slate-500']"></i> {{ isTesting ? '测试中...' : '测试连通性' }}
              </button>
              <span v-if="testResult" :class="['text-sm font-bold px-3 py-1 rounded-full', testResult.success ? 'bg-emerald-100 text-emerald-700' : 'bg-red-100 text-red-700']">
                <i :class="['fas', testResult.success ? 'fa-check-circle' : 'fa-times-circle']"></i> {{ testResult.message }}
              </span>
            </div>

            <div class="flex items-center gap-3 w-full md:w-auto">
              <button @click="loadConfig" class="px-5 py-2 rounded-xl border border-slate-300 bg-white text-slate-700 font-bold hover:bg-slate-100 transition-colors shadow-sm flex-1 md:flex-none text-center">取消</button>
              <button @click="saveConfig" class="px-5 py-2 rounded-xl bg-blue-600 text-white font-bold hover:bg-blue-700 transition-colors shadow-sm flex items-center justify-center gap-2 flex-1 md:flex-none">
                <i class="fas fa-save"></i> 保存设置
              </button>
            </div>
            
          </div>
        </div>
      </div>

    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const currentTab = ref('dashboard')

const stats = ref({
  visitCount: 0,
  cpuLoad: (Math.random() * 5 + 1).toFixed(1),
  memoryUsage: Math.floor(Math.random() * 30 + 30)
})

const config = ref({
  activeModel: 'DeepSeek',
  apiKey: '',
  baseUrl: 'https://api.deepseek.com',
  modelName: 'deepseek-chat',
  temperature: 0.7
})

const isTesting = ref(false)
const testResult = ref(null)

const users = ref([])
const isLoadingUsers = ref(false)

const loadUsers = async () => {
  isLoadingUsers.value = true
  try {
    const res = await fetch('http://localhost:8081/api/admin/users')
    const data = await res.json()
    users.value = data
  } catch (error) {
    console.error('Failed to load users:', error)
  } finally {
    isLoadingUsers.value = false
  }
}

const deleteUser = async (id, username) => {
  if (!confirm(`警告：确定要永久删除用户 "${username}" 及其所有学习进度吗？此操作不可逆！`)) {
    return
  }
  
  try {
    const res = await fetch(`http://localhost:8081/api/admin/users/${id}`, {
      method: 'DELETE'
    })
    
    if (res.ok) {
      alert('删除成功')
      loadUsers() // Reload list
    } else {
      alert('删除失败，请稍后重试')
    }
  } catch (error) {
    console.error('Failed to delete user:', error)
    alert('请求出错')
  }
}

const setProvider = (provider, baseUrl, defaultModel) => {
  config.value.activeModel = provider
  if (baseUrl) config.value.baseUrl = baseUrl
  if (defaultModel) config.value.modelName = defaultModel
}

const testConnection = async () => {
  if (!config.value.apiKey) {
    testResult.value = { success: false, message: '请先填写 API Key' }
    return
  }
  if (!config.value.baseUrl || !config.value.modelName) {
    testResult.value = { success: false, message: 'Base URL 和 模型名称不能为空' }
    return
  }
  
  isTesting.value = true
  testResult.value = null
  
  try {
    // 构造标准的 OpenAI completions 请求格式
    const url = config.value.baseUrl.endsWith('/') ? `${config.value.baseUrl}chat/completions` : `${config.value.baseUrl}/chat/completions`
    
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${config.value.apiKey}`
      },
      body: JSON.stringify({
        model: config.value.modelName,
        messages: [{ role: 'user', content: '测试连通性，请回复OK' }],
        max_tokens: 10
      })
    })

    if (response.ok) {
      testResult.value = { success: true, message: '连接成功！模型响应正常。' }
    } else {
      const errorData = await response.json().catch(() => ({}))
      testResult.value = { success: false, message: `连接失败: HTTP ${response.status} ${errorData.error?.message || ''}` }
    }
  } catch (error) {
    testResult.value = { success: false, message: `请求出错: ${error.message}` }
  } finally {
    isTesting.value = false
    // 5秒后清除提示
    setTimeout(() => {
      if (testResult.value) testResult.value = null
    }, 5000)
  }
}

const loadConfig = async () => {
  try {
    const res = await fetch('http://localhost:8081/api/admin/config')
    const data = await res.json()
    if (data && data.activeModel) {
      config.value = data
    }
  } catch (error) {
    console.error('Failed to load config:', error)
  }
}

const loadStats = async () => {
  try {
    const res = await fetch('http://localhost:8081/api/admin/stats')
    const data = await res.json()
    if (data) {
      stats.value.visitCount = data.visitCount || stats.value.visitCount
    }
  } catch (error) {
    console.error('Failed to load stats:', error)
  }
}

const saveConfig = async () => {
  try {
    await fetch('http://localhost:8081/api/admin/config', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(config.value)
    })
    alert('配置保存成功！')
  } catch (error) {
    alert('配置保存失败，请检查后端服务。')
  }
}

onMounted(() => {
  loadConfig()
  loadStats()
  loadUsers()
})
</script>
