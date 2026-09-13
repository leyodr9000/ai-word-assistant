<template>
  <div class="h-screen w-full p-4 md:p-8 relative font-sans overflow-y-auto">

    <!-- Dynamic Background Image -->
    <div 
      class="fixed inset-0 bg-cover bg-center transition-all duration-1000 ease-in-out z-0"
      :style="{ backgroundImage: `url(${backgroundImage})` }"
    ></div>
    <!-- Soft Light Overlay for readability -->
    <div class="fixed inset-0 bg-white/30 backdrop-blur-sm z-0"></div>

    <div class="max-w-6xl mx-auto relative z-10 flex flex-col gap-8 pb-20">
      
      <!-- Header -->
      <header class="flex flex-wrap justify-between items-center gap-4 bg-white/50 backdrop-blur-xl border border-white/40 rounded-3xl p-4 md:p-6 shadow-xl">
        <div>
          <h1 class="text-3xl font-black tracking-tight text-slate-800 drop-shadow-sm">词书大厅</h1>
          <p class="text-slate-600 mt-2 font-medium">导入专属词汇表，定制您的学习计划</p>
        </div>
        <button @click="router.push('/')" class="bg-slate-800/90 hover:bg-slate-900 text-white font-bold px-6 py-2.5 rounded-xl transition-all active:scale-95 shadow-md flex items-center gap-2 backdrop-blur-md border border-slate-700">
          返回学习 <i class="fas fa-arrow-right"></i>
        </button>
      </header>

      <!-- Import Section -->
      <section class="bg-white/50 backdrop-blur-xl border border-white/40 rounded-3xl p-8 shadow-xl">
        <div class="flex flex-col md:flex-row gap-8">
          <div class="flex-1 space-y-4">
            <h2 class="text-xl font-black flex items-center gap-2 text-slate-800 drop-shadow-sm">
              <div class="w-8 h-8 rounded-full bg-blue-100/80 text-blue-600 flex items-center justify-center border border-blue-200">
                <i class="fas fa-file-import text-sm"></i>
              </div>
              导入自定义词书
            </h2>
            <div class="bg-blue-50/60 backdrop-blur-md p-5 rounded-2xl border border-blue-200/50 text-sm text-slate-700 shadow-sm leading-relaxed">
              <p class="font-black text-blue-800 mb-3 text-base drop-shadow-sm"><i class="fas fa-lightbulb text-amber-500 mr-2"></i> 导入提示：</p>
              <p class="mb-3">您可以截图任何单词书，发送给 <strong class="text-blue-700">豆包、Kimi 或 ChatGPT</strong> 等 AI，让它为您生成以下格式的表格，然后保存为 Excel 导入！</p>
              <ul class="list-disc list-inside space-y-2 ml-1 text-slate-600 font-medium">
                <li>推荐包含表头：<strong class="bg-white/60 px-2 py-0.5 rounded text-blue-800">单元 | 单词 | 音标 | 释义</strong></li>
                <li>支持 <code class="bg-white/60 px-1.5 py-0.5 rounded text-blue-800">.xlsx</code> 或 <code class="bg-white/60 px-1.5 py-0.5 rounded text-blue-800">.xls</code> 格式</li>
                <li>系统会自动在所有工作表中寻找包含单词的数据</li>
              </ul>
            </div>
          </div>
          
          <div class="flex-1 flex flex-col items-center justify-center border-2 border-dashed border-white/80 bg-white/30 rounded-3xl p-8 hover:border-blue-400 hover:bg-white/50 transition-all group backdrop-blur-sm cursor-pointer active:scale-[0.98]">
            <input type="file" id="excel-upload" accept=".xlsx, .xls" class="hidden" @change="handleFileUpload">
            <label for="excel-upload" class="cursor-pointer flex flex-col items-center text-center w-full h-full">
              <div class="w-20 h-20 rounded-full bg-blue-600/90 text-white flex items-center justify-center mb-6 group-hover:scale-110 group-hover:bg-blue-500 transition-all shadow-lg border border-blue-400/50">
                <i class="fas fa-cloud-upload-alt text-3xl"></i>
              </div>
              <span class="font-black text-slate-800 text-xl drop-shadow-sm">点击选择 Excel 文件</span>
              <span class="text-sm text-slate-600 mt-2 font-medium">请上传按照上述格式排版的文件</span>
            </label>
          </div>
        </div>
      </section>

      <!-- Books Grid -->
      <section>
        <h2 class="text-2xl font-black mb-6 flex items-center gap-2 text-slate-800 drop-shadow-sm px-2">
          <i class="fas fa-layer-group text-amber-500"></i> 我的词书
        </h2>
        
        <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4 md:gap-6">
          
          <!-- Built-in Book -->
          <div class="bg-white/60 backdrop-blur-xl border border-white/50 rounded-3xl p-6 transition-all cursor-pointer shadow-lg relative overflow-hidden group active:scale-95"
               :class="currentBookId === 'builtin' ? 'ring-4 ring-blue-500/50 bg-white/80' : 'hover:-translate-y-2 hover:shadow-xl hover:bg-white/70'"
               @click="selectBook('builtin')">
            <div class="absolute top-0 right-0 bg-blue-600/90 backdrop-blur-md text-white text-xs font-bold px-4 py-1.5 rounded-bl-xl shadow-sm z-10" v-if="currentBookId === 'builtin'">当前选择</div>
            <div class="w-14 h-14 rounded-2xl bg-amber-100/80 border border-amber-200 text-amber-600 flex items-center justify-center mb-5 shadow-sm">
              <i class="fas fa-star text-2xl"></i>
            </div>
            <h3 class="font-black text-xl text-slate-800 mb-2 drop-shadow-sm">大学核心词汇 (内置)</h3>
            <p class="text-sm text-slate-600 mb-6 font-medium leading-relaxed">包含 Unit 1 - Unit 4 的精选核心词汇。</p>
            <div class="flex justify-between items-center text-xs text-slate-500 font-bold">
              <span>共 {{ builtinCount }} 词</span>
              <span class="bg-slate-800/10 backdrop-blur-md px-3 py-1 rounded-lg">系统默认</span>
            </div>
          </div>

          <!-- Imported Books -->
          <div v-for="book in sortedImportedBooks" :key="book.id"
               class="bg-white/60 backdrop-blur-xl border border-white/50 rounded-3xl p-6 transition-all cursor-pointer shadow-lg relative group active:scale-95"
               :class="currentBookId === book.id ? 'ring-4 ring-blue-500/50 bg-white/80' : 'hover:-translate-y-2 hover:shadow-xl hover:bg-white/70'"
               @click="selectBook(book.id)">
            
            <div class="absolute top-0 right-0 bg-blue-600/90 backdrop-blur-md text-white text-xs font-bold px-4 py-1.5 rounded-bl-xl rounded-tr-3xl shadow-sm z-10" v-if="currentBookId === book.id">当前选择</div>
            <div class="absolute top-0 left-0 bg-amber-500/90 backdrop-blur-md text-white text-[10px] font-bold px-3 py-1 rounded-br-xl rounded-tl-3xl shadow-sm z-10" v-if="book.isPinned"><i class="fas fa-thumbtack"></i> 已置顶</div>
            
            <!-- Options Menu Trigger -->
            <div class="absolute top-4 right-4 z-20">
              <button @click.stop="toggleMenu(book.id, $event)" class="w-8 h-8 rounded-full bg-white/50 border border-white/50 text-slate-400 hover:text-slate-700 hover:bg-slate-200/50 flex items-center justify-center shadow-sm transition-all" :class="{'opacity-100 bg-slate-200/50 text-slate-700': openMenuId === book.id, 'opacity-0 group-hover:opacity-100': openMenuId !== book.id}">
                <i class="fas fa-ellipsis-v"></i>
              </button>
              
              <!-- Dropdown Menu -->
              <div v-if="openMenuId === book.id" class="absolute top-full right-0 mt-2 w-32 bg-white/90 backdrop-blur-xl border border-white rounded-xl shadow-2xl py-2 overflow-hidden animate-fade-in-down transform origin-top-right">
                <button @click.stop="togglePin(book.id); closeMenu()" class="w-full text-left px-4 py-2 text-sm font-bold text-slate-700 hover:bg-blue-50 hover:text-blue-600 transition-colors flex items-center gap-2">
                  <i class="fas fa-thumbtack text-xs w-4"></i> {{ book.isPinned ? '取消置顶' : '置顶' }}
                </button>
                <button @click.stop="renameBook(book.id); closeMenu()" class="w-full text-left px-4 py-2 text-sm font-bold text-slate-700 hover:bg-blue-50 hover:text-blue-600 transition-colors flex items-center gap-2">
                  <i class="fas fa-edit text-xs w-4"></i> 重命名
                </button>
                <div class="h-px bg-slate-200 my-1 mx-2"></div>
                <button @click.stop="deleteBook(book.id); closeMenu()" class="w-full text-left px-4 py-2 text-sm font-bold text-red-600 hover:bg-red-50 transition-colors flex items-center gap-2">
                  <i class="fas fa-trash-alt text-xs w-4"></i> 删除
                </button>
              </div>
            </div>

            <div class="w-14 h-14 rounded-2xl bg-emerald-100/80 border border-emerald-200 text-emerald-600 flex items-center justify-center mb-5 shadow-sm mt-2">
              <i class="fas fa-file-excel text-2xl"></i>
            </div>
            <h3 class="font-black text-xl text-slate-800 mb-2 truncate drop-shadow-sm pr-6" :title="book.name">{{ book.name }}</h3>
            <p class="text-sm text-slate-600 mb-6 font-medium leading-relaxed">用户自定义导入词书。</p>
            <div class="flex justify-between items-center text-xs text-slate-500 font-bold">
              <span>共 {{ book.words ? book.words.length : book.wordCount }} 词</span>
              <span class="bg-slate-800/10 backdrop-blur-md px-3 py-1 rounded-lg">{{ new Date(book.importedAt).toLocaleDateString() }}</span>
            </div>
          </div>

        </div>
      </section>

    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { VOCABULARY_DATA } from '../data/words.js'
import * as XLSX from 'xlsx'

const router = useRouter()
const builtinCount = ref(VOCABULARY_DATA.length)
const importedBooks = ref([])
const currentBookId = ref(localStorage.getItem('current_book_id') || 'builtin')

// 登录用户词书存数据库(多端同步), 游客存 localStorage
const currentUser = ref(JSON.parse(localStorage.getItem('current_user') || 'null'))
const isCloud = computed(() => !!currentUser.value)

// Background Image Logic
const defaultBg = 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?q=80&w=2073&auto=format&fit=crop'
const backgroundImage = ref(localStorage.getItem('custom_bg') || defaultBg)

const loadImportedBooks = async () => {
  if (isCloud.value) {
    try {
      const res = await fetch('/api/books')
      if (res.ok) {
        const list = await res.json()
        importedBooks.value = list.map(b => ({
          id: b.id,
          name: b.name,
          isPinned: !!b.isPinned,
          importedAt: b.createdAt ? Date.parse(b.createdAt) : Date.now(),
          wordCount: b.wordCount || 0,
          words: null // 云端词书单词明细按需从 /api/books/{id} 加载
        }))
        return
      }
    } catch (err) {
      console.error('Failed to load cloud books:', err)
    }
    importedBooks.value = []
  } else {
    const books = localStorage.getItem('imported_vocab_books')
    importedBooks.value = books ? JSON.parse(books) : []
  }
}

const sortedImportedBooks = computed(() => {
  return [...importedBooks.value].sort((a, b) => {
    if (a.isPinned && !b.isPinned) return -1
    if (!a.isPinned && b.isPinned) return 1
    return b.importedAt - a.importedAt // newest first
  })
})

const selectBook = (id) => {
  currentBookId.value = id
  localStorage.setItem('current_book_id', id)
  router.push('/home')
}

// Menu state
const openMenuId = ref(null)

const toggleMenu = (id, event) => {
  if (openMenuId.value === id) {
    openMenuId.value = null
  } else {
    openMenuId.value = id
  }
}

const closeMenu = () => {
  openMenuId.value = null
}

onMounted(() => {
  document.addEventListener('click', closeMenu)
})
onUnmounted(() => {
  document.removeEventListener('click', closeMenu)
})

const deleteBook = async (id) => {
  if (confirm('确定要删除这本词书吗？您的学习进度可能会受到影响。')) {
    if (isCloud.value) {
      try {
        const res = await fetch(`/api/books/${id}`, { method: 'DELETE' })
        if (res.ok) {
          if (currentBookId.value === id) {
            selectBook('builtin')
          } else {
            await loadImportedBooks()
          }
        } else {
          alert('删除失败')
        }
      } catch (err) {
        console.error(err)
        alert('删除失败, 请检查后端服务')
      }
      return
    }
    importedBooks.value = importedBooks.value.filter(b => b.id !== id)
    localStorage.setItem('imported_vocab_books', JSON.stringify(importedBooks.value))
    if (currentBookId.value === id) {
       selectBook('builtin')
    }
  }
}

const renameBook = async (id) => {
  const book = importedBooks.value.find(b => b.id === id)
  if (!book) return
  const newName = prompt('请输入新的词书名称：', book.name)
  if (newName && newName.trim()) {
    if (isCloud.value) {
      try {
        const res = await fetch(`/api/books/${id}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ name: newName.trim() })
        })
        if (res.ok) {
          await loadImportedBooks()
        } else {
          const data = await res.json().catch(() => ({}))
          alert(data.message || '重命名失败')
        }
      } catch (err) {
        console.error(err)
        alert('重命名失败, 请检查后端服务')
      }
      return
    }
    book.name = newName.trim()
    localStorage.setItem('imported_vocab_books', JSON.stringify(importedBooks.value))
  }
}

const togglePin = async (id) => {
  const book = importedBooks.value.find(b => b.id === id)
  if (!book) return
  if (isCloud.value) {
    try {
      const res = await fetch(`/api/books/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ isPinned: !book.isPinned })
      })
      if (res.ok) {
        await loadImportedBooks()
      }
    } catch (err) {
      console.error(err)
    }
    return
  }
  book.isPinned = !book.isPinned
  localStorage.setItem('imported_vocab_books', JSON.stringify(importedBooks.value))
}

const handleFileUpload = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  const reader = new FileReader()
  reader.onload = async (e) => {
    try {
      const data = new Uint8Array(e.target.result)
      const workbook = XLSX.read(data, { type: 'array' })
      
      let parsedWords = []
      
      // Look through all sheets to find the one with the most words
      for (const sheetName of workbook.SheetNames) {
        const worksheet = workbook.Sheets[sheetName]
        const json = XLSX.utils.sheet_to_json(worksheet, { header: 1 })
        
        if (json.length <= 1) continue
        
        // Find header row
        let headerRowIdx = -1
        let colWord = -1, colPhonetic = -1, colDef = -1, colUnit = -1
        
        for (let i = 0; i < Math.min(10, json.length); i++) {
          const row = json[i]
          if (!row) continue
          const rowStr = row.map(c => String(c).toLowerCase()).join(' ')
          if (rowStr.includes('单词') || rowStr.includes('word') || rowStr.includes('英文')) {
            headerRowIdx = i
            row.forEach((cell, idx) => {
              const c = String(cell).toLowerCase()
              if (c.includes('单词') || c.includes('英文') || c.includes('word')) colWord = idx
              else if (c.includes('音标') || c.includes('phonetic')) colPhonetic = idx
              else if (c.includes('释义') || c.includes('翻译') || c.includes('def')) colDef = idx
              else if (c.includes('单元') || c.includes('分类') || c.includes('unit')) colUnit = idx
            })
            break
          }
        }
        
        // Fallback if no clear header is found: try to guess based on the format: Unit | Word | Phonetic | Def
        if (colWord === -1) {
            colUnit = 0; colWord = 1; colPhonetic = 2; colDef = 3;
            headerRowIdx = 0; // Assume first row is header
        }

        const currentSheetWords = []
        for (let i = headerRowIdx + 1; i < json.length; i++) {
          const row = json[i]
          if (!row || row.length === 0 || !row[colWord]) continue

          currentSheetWords.push({
            word: String(row[colWord]).trim(),
            phonetic: colPhonetic !== -1 && row[colPhonetic] ? String(row[colPhonetic]).trim() : '',
            definition: colDef !== -1 && row[colDef] ? String(row[colDef]).trim() : '暂无释义',
            unit: colUnit !== -1 && row[colUnit] ? String(row[colUnit]).trim() : '默认分类'
          })
        }
        
        if (currentSheetWords.length > parsedWords.length) {
            parsedWords = currentSheetWords
        }
      }

      if (parsedWords.length > 0) {
        if (isCloud.value) {
          // 登录用户: 词书存入数据库
          try {
            const res = await fetch('/api/books', {
              method: 'POST',
              headers: { 'Content-Type': 'application/json' },
              body: JSON.stringify({
                name: file.name.replace(/\.[^/.]+$/, ""),
                words: parsedWords
              })
            })
            if (res.ok) {
              const data = await res.json()
              alert(`成功导入 ${data.wordCount ?? parsedWords.length} 个单词（已保存到云端）！`)
              await loadImportedBooks()
              selectBook(data.id)
            } else {
              const errData = await res.json().catch(() => ({}))
              alert(errData.message || '保存词书失败')
            }
          } catch (err) {
            console.error(err)
            alert('保存词书失败，请检查后端服务。')
          }
          event.target.value = ''
          return
        }
        const newBook = {
          id: 'book_' + Date.now(),
          name: file.name.replace(/\.[^/.]+$/, ""),
          importedAt: Date.now(),
          isPinned: false,
          words: parsedWords
        }
        importedBooks.value.push(newBook)
        localStorage.setItem('imported_vocab_books', JSON.stringify(importedBooks.value))
        alert(`成功导入 ${parsedWords.length} 个单词！`)
        selectBook(newBook.id)
      } else {
        alert('未能解析出有效的单词数据，请确保表格中包含“单词”、“释义”等列。')
      }
    } catch (err) {
      console.error(err)
      alert('解析 Excel 文件时发生错误。')
    }
    event.target.value = ''
  }
  reader.readAsArrayBuffer(file)
}

onMounted(() => {
  loadImportedBooks()
})
</script>

<style scoped>
/* Dropdown Animation */
@keyframes fadeInDown {
  from { opacity: 0; transform: translateY(-10px) scale(0.95); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}
.animate-fade-in-down {
  animation: fadeInDown 0.15s ease-out forwards;
}
</style>
