<template>
  <div class="flex flex-col md:flex-row h-screen w-full bg-slate-50 text-slate-800 font-sans">
    <!-- Sidebar Navigation (窄屏时变为顶部横条) -->
    <aside class="w-full md:w-64 bg-white border-b md:border-b-0 md:border-r border-slate-200 flex flex-row md:flex-col items-center md:items-stretch shrink-0">
      <div class="p-4 md:p-6 border-b md:border-b-0 border-slate-100 flex items-center gap-3 w-full md:w-auto shrink-0">
        <div class="w-8 h-8 rounded-lg bg-blue-600 text-white flex items-center justify-center">
          <i class="fas fa-shield-alt"></i>
        </div>
        <h1 class="font-bold text-lg tracking-tight">Admin Center</h1>
      </div>

      <nav class="flex-1 p-2 md:p-4 flex md:block space-x-2 md:space-x-0 md:space-y-2 overflow-x-auto">
        <a @click="currentTab = 'dashboard'" :class="['flex items-center gap-3 px-4 py-3 rounded-xl cursor-pointer transition-colors whitespace-nowrap', currentTab === 'dashboard' ? 'bg-blue-50 text-blue-600 font-medium' : 'hover:bg-slate-50 text-slate-600']">
          <i class="fas fa-chart-pie w-5"></i> 控制面板
        </a>
        <a @click="currentTab = 'users'" :class="['flex items-center gap-3 px-4 py-3 rounded-xl cursor-pointer transition-colors whitespace-nowrap', currentTab === 'users' ? 'bg-blue-50 text-blue-600 font-medium' : 'hover:bg-slate-50 text-slate-600']">
          <i class="fas fa-users w-5"></i> 用户管理
        </a>
        <a @click="currentTab = 'ai-config'" :class="['flex items-center gap-3 px-4 py-3 rounded-xl cursor-pointer transition-colors whitespace-nowrap', currentTab === 'ai-config' ? 'bg-blue-50 text-blue-600 font-medium' : 'hover:bg-slate-50 text-slate-600']">
          <i class="fas fa-robot w-5"></i> AI 模型配置
        </a>
      </nav>

      <div class="p-2 md:p-4 border-t border-slate-100 shrink-0">
        <router-link to="/" class="flex items-center gap-3 px-4 py-3 rounded-xl hover:bg-slate-50 text-slate-600 transition-colors whitespace-nowrap">
          <i class="fas fa-sign-out-alt w-5"></i> 返回前台
        </router-link>
      </div>
    </aside>

    <!-- Main Content -->
    <main class="flex-1 overflow-y-auto p-4 md:p-8 min-w-0">
      
      <!-- Tab: Dashboard -->
      <div v-if="currentTab === 'dashboard'" class="max-w-5xl mx-auto space-y-6">
        <div>
          <h2 class="text-2xl font-bold text-slate-800 mb-2">控制面板</h2>
          <p class="text-slate-500">查看系统当前的运行状态和访问数据。</p>
        </div>

        <div class="grid grid-cols-2 md:grid-cols-3 gap-6 mt-6">
          <div class="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm flex flex-col">
            <div class="flex items-center gap-3 text-slate-500 mb-4">
              <i class="fas fa-users text-blue-500 bg-blue-50 p-2 rounded-lg"></i>
              <span class="font-medium">注册用户</span>
            </div>
            <div class="text-4xl font-bold text-slate-800">{{ stats.userCount ?? 0 }}</div>
          </div>

          <div class="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm flex flex-col">
            <div class="flex items-center gap-3 text-slate-500 mb-4">
              <i class="fas fa-layer-group text-indigo-500 bg-indigo-50 p-2 rounded-lg"></i>
              <span class="font-medium">云端词书</span>
            </div>
            <div class="text-4xl font-bold text-slate-800">{{ stats.bookCount ?? 0 }}</div>
          </div>

          <div class="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm flex flex-col">
            <div class="flex items-center gap-3 text-slate-500 mb-4">
              <i class="fas fa-book text-emerald-500 bg-emerald-50 p-2 rounded-lg"></i>
              <span class="font-medium">云端单词</span>
            </div>
            <div class="text-4xl font-bold text-slate-800">{{ stats.cloudWordCount ?? 0 }}</div>
          </div>

          <div class="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm flex flex-col">
            <div class="flex items-center gap-3 text-slate-500 mb-4">
              <i class="fas fa-tasks text-amber-500 bg-amber-50 p-2 rounded-lg"></i>
              <span class="font-medium">学习进度记录</span>
            </div>
            <div class="text-4xl font-bold text-slate-800">{{ stats.progressCount ?? 0 }}</div>
          </div>

          <div class="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm flex flex-col">
            <div class="flex items-center gap-3 text-slate-500 mb-4">
              <i class="fas fa-sign-in-alt text-rose-500 bg-rose-50 p-2 rounded-lg"></i>
              <span class="font-medium">累计登录</span>
            </div>
            <div class="text-4xl font-bold text-slate-800">{{ stats.visitCount ?? 0 }}</div>
          </div>

          <div @click="currentTab = 'ai-config'" class="bg-white p-6 rounded-2xl border shadow-sm flex flex-col cursor-pointer hover:border-blue-300 transition-colors" :class="stats.aiConfigured ? 'border-slate-200' : 'border-amber-300 bg-amber-50/50'">
            <div class="flex items-center gap-3 text-slate-500 mb-4">
              <i class="fas fa-robot text-indigo-500 bg-indigo-50 p-2 rounded-lg"></i>
              <span class="font-medium">AI 助教状态</span>
            </div>
            <div class="text-lg font-bold" :class="stats.aiConfigured ? 'text-emerald-600' : 'text-amber-600'">
              {{ stats.aiConfigured ? '已配置' : '未配置 API Key' }}
            </div>
            <div class="text-xs text-slate-400 mt-1">{{ stats.modelName || '-' }} · 点击前往配置</div>
          </div>
        </div>

        <div class="bg-white border border-slate-200 rounded-2xl p-6 shadow-sm mt-6">
          <div class="flex justify-between items-center mb-4">
            <h3 class="font-bold text-lg">最近登录记录</h3>
            <button @click="loadVisits" class="text-xs font-bold text-blue-600 hover:underline flex items-center gap-1"><i class="fas fa-sync-alt"></i> 刷新</button>
          </div>
          <div v-if="visits.length === 0" class="text-sm text-slate-400 py-4 text-center">暂无登录记录 (用户登录后自动记录)</div>
          <div v-else class="flex flex-col text-sm text-slate-600">
            <div v-for="(v, i) in visits" :key="v.id" class="flex justify-between items-center py-2 border-b border-slate-50 last:border-0">
              <span class="flex items-center gap-2">
                <i class="fas fa-circle text-[8px]" :class="i === 0 ? 'text-emerald-500' : 'text-slate-300'"></i>
                {{ i === 0 ? '最近一次登录' : '登录' }}
              </span>
              <span class="text-slate-400">{{ formatTime(v.visitTime) }}</span>
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
                  <th scope="col" class="px-6 py-4">学习进度</th>
                  <th scope="col" class="px-6 py-4">注册时间</th>
                  <th scope="col" class="px-6 py-4 text-right">操作</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-slate-100">
                <tr v-if="users.length === 0" class="hover:bg-slate-50/50">
                  <td colspan="6" class="px-6 py-8 text-center text-slate-400">暂无用户数据</td>
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
                  <td class="px-6 py-4">
                    <span class="bg-blue-50 text-blue-600 px-2.5 py-1 rounded-full text-xs font-bold">{{ user.progressCount ?? 0 }} 词</span>
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
                  <input v-model="config.apiKey" type="password" :placeholder="hasSavedKey ? `已保存 ${savedKeyMasked}，留空则不修改` : 'sk-...'" class="w-full bg-slate-50 border border-slate-300 text-slate-700 py-3 pl-10 pr-4 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 font-mono text-sm shadow-sm inset-shadow-sm">
                </div>
                <p v-if="hasSavedKey" class="text-xs text-slate-400 mt-1.5 ml-1">出于安全考虑，Key 只保存在服务器上，输入新值即可覆盖。</p>
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
  userCount: 0,
  bookCount: 0,
  cloudWordCount: 0,
  progressCount: 0,
  visitCount: 0,
  aiConfigured: false,
  modelName: ''
})

const visits = ref([])

const formatTime = (t) => {
  if (!t) return '-'
  const d = new Date(t)
  return isNaN(d.getTime()) ? t : d.toLocaleString('zh-CN', { hour12: false })
}

const loadVisits = async () => {
  try {
    const res = await fetch('/api/admin/visits')
    if (res.ok) visits.value = await res.json()
  } catch (error) {
    console.error('Failed to load visits:', error)
  }
}

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
    const res = await fetch('/api/admin/users')
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
    const res = await fetch(`/api/admin/users/${id}`, {
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
  if (!config.value.baseUrl || !config.value.modelName) {
    testResult.value = { success: false, message: 'Base URL 和 模型名称不能为空' }
    return
  }

  isTesting.value = true
  testResult.value = null

  try {
    // 测试请求由后端代理执行: apiKey 留空时使用数据库已保存的 Key, 浏览器接触不到真实 Key
    const response = await fetch('/api/admin/config/test', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        baseUrl: config.value.baseUrl,
        modelName: config.value.modelName,
        apiKey: config.value.apiKey
      })
    })
    const data = await response.json()
    testResult.value = {
      success: !!data.success,
      message: data.message || (data.success ? '连接成功！模型响应正常。' : '连接失败')
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

const savedKeyMasked = ref('')
const hasSavedKey = ref(false)

const loadConfig = async () => {
  try {
    const res = await fetch('/api/admin/config')
    const data = await res.json()
    if (data && data.activeModel) {
      savedKeyMasked.value = data.apiKeyMasked || ''
      hasSavedKey.value = !!data.hasApiKey
      config.value = {
        activeModel: data.activeModel,
        apiKey: '', // 真实 Key 不下发到浏览器, 此字段仅接收用户新输入
        baseUrl: data.baseUrl || '',
        modelName: data.modelName || '',
        temperature: data.temperature ?? 0.7
      }
    }
  } catch (error) {
    console.error('Failed to load config:', error)
  }
}

const loadStats = async () => {
  try {
    const res = await fetch('/api/admin/stats')
    const data = await res.json()
    if (data) {
      stats.value = { ...stats.value, ...data }
    }
  } catch (error) {
    console.error('Failed to load stats:', error)
  }
}

const saveConfig = async () => {
  try {
    await fetch('/api/admin/config', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(config.value)
    })
    alert('配置保存成功！')
    await loadConfig()
  } catch (error) {
    alert('配置保存失败，请检查后端服务。')
  }
}

onMounted(() => {
  loadConfig()
  loadStats()
  loadUsers()
  loadVisits()
})
</script>
