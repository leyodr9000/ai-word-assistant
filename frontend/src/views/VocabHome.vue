<template>
  <div class="h-screen w-full bg-gradient-to-br from-indigo-50/80 via-white to-blue-50/80 dark:from-slate-900/90 dark:via-slate-800/90 dark:to-slate-900/90 p-0 md:p-4 flex gap-4 relative font-sans overflow-hidden transition-all duration-500" :style="customBgStyle">

    <!-- Dynamic Background Image (Shared with Login) -->
    <div 
      class="absolute inset-0 bg-cover bg-center transition-all duration-1000 ease-in-out z-0"
      :style="{ backgroundImage: `url(${backgroundImage})` }"
    ></div>
    <!-- Soft Light Overlay for readability -->
    <div class="absolute inset-0 bg-white/20 backdrop-blur-sm z-0"></div>

    <!-- Main Content Panel -->
    <main class="flex-1 flex flex-col gap-4 min-w-0 relative z-10 h-full">
      
      <!-- Top Header / Dashboard -->
      <header class="p-6 grid grid-cols-1 lg:grid-cols-3 items-center gap-6 shrink-0 bg-white/50 dark:bg-slate-800/50 backdrop-blur-xl border border-white/40 dark:border-slate-600/30 rounded-3xl shadow-xl transition-all hover:bg-white/60 dark:hover:bg-slate-700/50 relative z-50">
        <!-- Left: Branding & Timer -->
        <div class="flex items-center gap-6 justify-self-start shrink-0">
          <!-- Circular Progress -->
          <div class="relative w-20 h-20 flex items-center justify-center">
            <svg class="w-full h-full transform -rotate-90 drop-shadow-md" viewBox="0 0 36 36">
              <path class="text-white/50" stroke-width="3" stroke="currentColor" fill="none"
                d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831" />
              <path class="text-emerald-500 transition-all duration-1000 ease-out" stroke-dasharray="100, 100" :stroke-dashoffset="100 - progressPercentage" stroke-width="3" stroke-linecap="round" stroke="currentColor" fill="none"
                d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831" />
            </svg>
            <div class="absolute inset-0 flex flex-col items-center justify-center">
              <span class="text-lg font-extrabold text-slate-800 dark:text-slate-100">{{ progressPercentage }}%</span>
            </div>
          </div>
          
          <div>
            <h1 class="text-2xl font-bold tracking-tight text-slate-800 dark:text-slate-100 flex items-center gap-4">
              {{ currentBookName }}
              <router-link to="/library" class="bg-blue-600/90 hover:bg-blue-700 text-white font-medium px-4 py-1.5 text-sm rounded-xl transition-all active:scale-95 shadow-md flex items-center gap-2 backdrop-blur-md whitespace-nowrap">
                <i class="fas fa-exchange-alt"></i> 切换词书
              </router-link>
            </h1>
            <p class="text-sm text-slate-600 dark:text-slate-300 mt-2 flex items-center gap-2 font-medium">
              <template v-if="isSyncing">
                <i class="fas fa-spinner fa-spin text-blue-500"></i> <span class="text-blue-500 tracking-wider">正在同步学习进度...</span>
              </template>
              <template v-else>
                <i class="fas fa-clock text-blue-500"></i> 今日学习: <span class="font-bold text-slate-800 dark:text-slate-100">{{ formattedStudyTime }}</span>
              </template>
            </p>
          </div>
        </div>

        <!-- Middle: User Greeting & Proverb -->
        <div class="hidden lg:flex flex-col items-center justify-center justify-self-center w-full max-w-md">
          <div class="flex flex-col items-center gap-3 w-full">
            <div class="flex items-center gap-3">
              <span class="text-sm font-black text-slate-700 dark:text-slate-200 bg-white/60 dark:bg-slate-800/60 px-5 py-1.5 rounded-full border border-white/80 dark:border-slate-600/60 shadow-sm backdrop-blur-md flex items-center gap-2 tracking-wide">
                <i class="fas fa-user-circle text-blue-500 text-lg"></i> 
                Hi, {{ currentUser?.username || '游客' }}
              </span>
              <button @click="handleLogout" class="text-xs font-bold text-red-500 bg-white/60 dark:bg-slate-800/60 hover:bg-red-500 hover:text-white px-3 py-1.5 rounded-full border border-red-200/50 shadow-sm backdrop-blur-md flex items-center gap-1.5 transition-all active:scale-95">
                <i class="fas fa-sign-out-alt"></i> 退出
              </button>
            </div>
            <div class="w-full text-center group relative cursor-pointer">
              <p class="text-sm text-slate-600 dark:text-slate-300 font-medium italic bg-white/40 dark:bg-slate-800/40 px-6 py-3 rounded-2xl border border-white/50 dark:border-slate-600/40 shadow-sm backdrop-blur-sm line-clamp-2 transition-all hover:bg-white/70 dark:hover:bg-slate-700/60 hover:shadow-md">
                "{{ currentProverb.en }}"
              </p>
              <!-- Tooltip for Chinese translation -->
              <div class="absolute top-full left-1/2 -translate-x-1/2 mt-3 opacity-0 group-hover:opacity-100 pointer-events-none transition-all duration-300 bg-slate-800/90 backdrop-blur-xl text-white text-xs px-4 py-2.5 rounded-xl whitespace-nowrap shadow-2xl z-50 scale-95 group-hover:scale-100 font-medium tracking-wider">
                {{ currentProverb.zh }}
                <!-- Small triangle -->
                <div class="absolute -top-1.5 left-1/2 -translate-x-1/2 border-4 border-transparent border-b-slate-800/90"></div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- Dashboard Stats -->
        <div class="flex gap-4 items-center justify-self-end">
          <div class="bg-emerald-100/60 dark:bg-emerald-900/30 backdrop-blur-md border border-emerald-200/50 dark:border-emerald-700/50 px-4 py-2 rounded-2xl flex flex-col items-center min-w-[80px] shadow-sm transition-all hover:scale-105">
            <span class="text-xs text-emerald-700 dark:text-emerald-400 font-bold mb-0.5">熟练</span>
            <span class="font-black text-emerald-600 dark:text-emerald-500 text-xl">{{ stats.mastered }}</span>
          </div>
          <div class="bg-orange-100/60 dark:bg-orange-900/30 backdrop-blur-md border border-orange-200/50 dark:border-orange-700/50 px-4 py-2 rounded-2xl flex flex-col items-center min-w-[80px] shadow-sm transition-all hover:scale-105">
            <span class="text-xs text-orange-700 dark:text-orange-400 font-bold mb-0.5">陌生</span>
            <span class="font-black text-orange-600 dark:text-orange-500 text-xl">{{ stats.unfamiliar }}</span>
          </div>
          <div class="bg-blue-100/60 dark:bg-blue-900/30 backdrop-blur-md border border-blue-200/50 dark:border-blue-700/50 px-4 py-2 rounded-2xl flex flex-col items-center min-w-[80px] shadow-sm transition-all hover:scale-105">
            <span class="text-xs text-blue-700 dark:text-blue-400 font-bold mb-0.5">未学</span>
            <span class="font-black text-blue-600 dark:text-blue-500 text-xl">{{ stats.new }}</span>
          </div>
          <div class="h-8 w-px bg-slate-300 dark:bg-slate-600 mx-2"></div>
          <router-link v-if="currentUser?.role === 'ADMIN'" to="/admin" class="w-10 h-10 rounded-full bg-slate-800/10 dark:bg-slate-800/50 hover:bg-slate-800/20 dark:hover:bg-slate-700/80 text-slate-600 dark:text-slate-200 flex items-center justify-center transition-all active:scale-95 shadow-sm backdrop-blur-md shrink-0" title="后台管理">
            <i class="fas fa-cog text-lg"></i>
          </router-link>
          <button @click="showSettings = true" class="w-10 h-10 rounded-full bg-slate-800/10 dark:bg-slate-800/50 hover:bg-slate-800/20 dark:hover:bg-slate-700/80 text-slate-600 dark:text-slate-200 flex items-center justify-center transition-all active:scale-95 shadow-sm backdrop-blur-md shrink-0" title="个性化设置">
            <i class="fas fa-palette text-lg"></i>
          </button>
        </div>
      </header>
      
      <!-- Controls -->
      <div class="relative z-20 p-4 flex flex-wrap gap-4 shrink-0 bg-white/50 dark:bg-slate-800/50 backdrop-blur-xl border border-white/40 dark:border-slate-600/30 rounded-2xl shadow-lg items-center transition-all hover:bg-white/60 dark:hover:bg-slate-700/50">
        <!-- Search -->
        <div class="flex-1 min-w-[200px] relative">
          <i class="fas fa-search absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 dark:text-slate-500"></i>
          <input 
            v-model="searchQuery" 
            type="text" 
            placeholder="搜索单词或释义..." 
            class="w-full bg-white/40 dark:bg-slate-800/40 backdrop-blur-md border border-white/50 dark:border-slate-600/40 rounded-xl pl-10 pr-4 py-2 outline-none focus:border-blue-400/80 focus:bg-white/80 dark:bg-slate-800/80 transition-all placeholder:text-slate-500 dark:text-slate-400 dark:text-slate-500 text-sm font-medium shadow-inner"
          >
        </div>

        <!-- Voice Selector Custom Dropdown -->
        <div class="relative hidden lg:block select-none z-50">
          <div @click="isVoiceMenuOpen = !isVoiceMenuOpen" class="flex items-center bg-white/40 dark:bg-slate-800/40 backdrop-blur-md border border-white/50 dark:border-slate-600/40 rounded-xl px-4 py-2 gap-3 text-sm text-slate-700 dark:text-slate-200 font-bold shadow-sm cursor-pointer hover:bg-white/60 dark:hover:bg-slate-700/50 transition-all active:scale-95">
            <i class="fas fa-headphones-alt text-blue-600"></i>
            <span class="min-w-[100px]">{{ availableVoices[selectedVoiceIndex]?.label || '加载中...' }}</span>
            <i :class="['fas text-[10px] text-slate-500 dark:text-slate-400 dark:text-slate-500 transition-transform duration-300', isVoiceMenuOpen ? 'fa-chevron-up' : 'fa-chevron-down']"></i>
          </div>
          
          <!-- Dropdown List -->
          <div v-if="isVoiceMenuOpen" class="absolute top-full mt-2 right-0 w-48 bg-white/80 dark:bg-slate-800/80 backdrop-blur-2xl border border-white/50 dark:border-slate-600/40 rounded-2xl shadow-2xl overflow-hidden animate-fade-in-down z-50 py-2">
            <div 
              v-for="(voice, idx) in availableVoices" 
              :key="idx" 
              @click="selectVoice(idx)" 
              class="px-4 py-3 text-sm font-bold cursor-pointer transition-all hover:bg-blue-500 hover:text-white flex items-center justify-between group"
              :class="selectedVoiceIndex === idx ? 'text-blue-600 bg-blue-50/50' : 'text-slate-700 dark:text-slate-200'"
            >
              <span>{{ voice.label }}</span>
              <i class="fas fa-check text-xs opacity-0 group-hover:opacity-100 transition-opacity" :class="selectedVoiceIndex === idx ? 'opacity-100' : ''"></i>
            </div>
          </div>
        </div>

        <!-- Definition Toggles -->
        <div class="flex items-center gap-2">
          <button @click="hideAllDefinitions = !hideAllDefinitions" :class="['px-3 py-2 rounded-xl text-sm font-bold transition-all border active:scale-95 shadow-sm backdrop-blur-md', hideAllDefinitions ? 'bg-slate-700/90 text-white border-slate-600' : 'bg-white/50 dark:bg-slate-800/50 text-slate-700 dark:text-slate-200 border-white/50 dark:border-slate-600/40 hover:bg-white/70 dark:hover:bg-slate-700/60']" title="隐藏所有汉语释义">
            <i :class="hideAllDefinitions ? 'fas fa-eye-slash' : 'fas fa-eye'" class="mr-1"></i> 全隐
          </button>
          <button @click="hideMasteredDefinitions = !hideMasteredDefinitions" :class="['px-3 py-2 rounded-xl text-sm font-bold transition-all border active:scale-95 shadow-sm backdrop-blur-md', hideMasteredDefinitions ? 'bg-emerald-600/90 text-white border-emerald-500' : 'bg-emerald-50/50 text-emerald-700 border-emerald-200/50 hover:bg-emerald-100/60']" title="仅隐藏已标熟练的释义">
            <i :class="hideMasteredDefinitions ? 'fas fa-eye-slash' : 'fas fa-eye'" class="mr-1"></i> 熟隐
          </button>
        </div>

        <!-- Shuffle / Restore -->
        <div class="flex bg-purple-50/50 backdrop-blur-md rounded-xl border border-purple-200/50 overflow-hidden shrink-0 shadow-sm">
          <button @click="shuffleWords" class="px-3 py-2 text-sm text-purple-700 hover:bg-purple-100/80 font-bold transition-all active:bg-purple-200/90 border-r border-purple-200/50" title="打乱顺序">
            <i class="fas fa-random lg:mr-1"></i><span class="hidden lg:inline">乱序</span>
          </button>
          <button @click="restoreOrder" class="px-3 py-2 text-sm text-purple-700 hover:bg-purple-100/80 transition-all active:bg-purple-200/90" title="恢复原本顺序">
            <i class="fas fa-undo"></i>
          </button>
        </div>
        
        <!-- View Toggle -->
        <div class="flex bg-slate-900/10 dark:bg-white/10 backdrop-blur-md p-1 rounded-xl border border-white/20 dark:border-white/10 shrink-0 shadow-inner">
          <button @click="viewMode = 'list'" :class="['px-3 lg:px-4 py-1.5 rounded-lg text-sm transition-all active:scale-95', viewMode === 'list' ? 'bg-white dark:bg-slate-800 shadow-md text-blue-600 font-bold' : 'hover:bg-white/40 dark:hover:bg-slate-700/40 text-slate-700 dark:text-slate-200 font-medium']">
            <i class="fas fa-list lg:mr-1"></i> <span class="hidden lg:inline">列表</span>
          </button>
          <button @click="viewMode = 'card'" :class="['px-3 lg:px-4 py-1.5 rounded-lg text-sm transition-all active:scale-95', viewMode === 'card' ? 'bg-white dark:bg-slate-800 shadow-md text-blue-600 font-bold' : 'hover:bg-white/40 dark:hover:bg-slate-700/40 text-slate-700 dark:text-slate-200 font-medium']">
            <i class="fas fa-clone lg:mr-1"></i> <span class="hidden lg:inline">卡片</span>
          </button>
        </div>
      </div>
      
      <!-- Content Area -->
      <div class="flex-1 overflow-hidden relative flex flex-col bg-white/50 dark:bg-slate-800/50 backdrop-blur-2xl border border-white/40 dark:border-slate-600/30 rounded-3xl shadow-xl">
        <!-- List View -->
        <div v-if="viewMode === 'list'" class="flex-1 overflow-y-auto custom-scrollbar">
          <table class="w-full text-left border-collapse">
            <thead class="sticky top-0 z-10 shadow-sm">
              <tr>
                <th class="py-3 px-6 font-extrabold tracking-wider text-slate-600 dark:text-slate-300 bg-white/60 dark:bg-slate-800/60 backdrop-blur-2xl border-b border-white/40 dark:border-slate-600/30">单词</th>
                <th class="py-3 px-6 font-extrabold tracking-wider text-slate-600 dark:text-slate-300 bg-white/60 dark:bg-slate-800/60 backdrop-blur-2xl border-b border-white/40 dark:border-slate-600/30">释义</th>
                <th class="py-3 px-6 font-extrabold tracking-wider text-right text-slate-600 dark:text-slate-300 bg-white/60 dark:bg-slate-800/60 backdrop-blur-2xl border-b border-white/40 dark:border-slate-600/30">状态控制</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="word in filteredWords" :key="word.word" class="border-b border-slate-200 dark:border-slate-700/50/40 dark:border-slate-700/40 hover:bg-white/60 dark:hover:bg-slate-700/50 transition-colors group">
                <td class="py-3 px-6 w-1/3">
                  <div class="flex items-center gap-3">
                    <button @click="speak(word.word)" class="w-8 h-8 rounded-full bg-blue-100/80 text-blue-600 flex items-center justify-center hover:bg-blue-600 hover:text-white transition-all transform active:scale-90 group-hover:shadow-md shrink-0 border border-blue-200">
                      <i class="fas fa-volume-up text-sm"></i>
                    </button>
                    <div>
                      <div class="font-extrabold text-xl text-slate-800 dark:text-slate-100 tracking-wide drop-shadow-sm">{{ word.word }}</div>
                      <div class="text-slate-500 dark:text-slate-300 text-xs font-medium tracking-wider">{{ word.phonetic }}</div>
                    </div>
                  </div>
                </td>
                <td class="py-3 px-6 w-1/3">
                  <div class="text-slate-700 dark:text-slate-200 font-medium text-base leading-relaxed cursor-pointer" 
                       :class="(hideAllDefinitions || (hideMasteredDefinitions && getStatus(word.word) === 'mastered')) ? 'blur-md hover:blur-none transition-all duration-300 select-none' : ''"
                       title="点击/悬停查看释义">
                    {{ word.definition }}
                  </div>
                </td>
                <td class="py-3 px-6 w-1/3">
                  <div class="flex gap-2 justify-end">
                    <button @click="updateStatus(word.word, 'new')" :class="['px-3 py-1.5 rounded-xl text-xs font-bold transition-all active:scale-90', getStatus(word.word) === 'new' ? 'bg-blue-500 text-white shadow-md shadow-blue-500/30 border-none' : 'bg-white/50 dark:bg-slate-800/50 text-slate-600 dark:text-slate-300 border border-white/50 dark:border-slate-600/40 hover:bg-white dark:bg-slate-800 shadow-sm']">未学</button>
                    <button @click="updateStatus(word.word, 'unfamiliar')" :class="['px-3 py-1.5 rounded-xl text-xs font-bold transition-all active:scale-90', getStatus(word.word) === 'unfamiliar' ? 'bg-orange-500 text-white shadow-md shadow-orange-500/30 border-none' : 'bg-white/50 dark:bg-slate-800/50 text-slate-600 dark:text-slate-300 border border-white/50 dark:border-slate-600/40 hover:bg-white dark:bg-slate-800 shadow-sm']">陌生</button>
                    <button @click="updateStatus(word.word, 'mastered')" :class="['px-3 py-1.5 rounded-xl text-xs font-bold transition-all active:scale-90', getStatus(word.word) === 'mastered' ? 'bg-emerald-500 text-white shadow-md shadow-emerald-500/30 border-none' : 'bg-white/50 dark:bg-slate-800/50 text-slate-600 dark:text-slate-300 border border-white/50 dark:border-slate-600/40 hover:bg-white dark:bg-slate-800 shadow-sm']">熟练</button>
                  </div>
                </td>
              </tr>
              <tr v-if="filteredWords.length === 0">
                <td colspan="3" class="text-center py-10 text-slate-500 dark:text-slate-400 dark:text-slate-500 font-bold">没有找到匹配的单词</td>
              </tr>
            </tbody>
          </table>
        </div>
        
        <!-- Card View -->
        <div v-else class="flex-1 flex flex-col items-center justify-center p-4 md:p-8 relative">
          <div v-if="filteredWords.length > 0" class="w-full max-w-lg relative h-full max-h-[500px] flex flex-col">
            <div class="text-center text-slate-500 dark:text-slate-400 dark:text-slate-500 mb-4 font-bold tracking-widest text-sm shrink-0 bg-white/40 dark:bg-slate-800/40 inline-block px-4 py-1 rounded-full mx-auto backdrop-blur-md shadow-sm">
              {{ currentIndex + 1 }} / {{ filteredWords.length }}
            </div>
            
            <!-- Prev/Next Navigation outside the card -->
            <button @click="prevCard" class="absolute top-1/2 -left-12 md:-left-16 -translate-y-1/2 w-10 h-10 md:w-12 md:h-12 rounded-full bg-white/80 dark:bg-slate-800/80 backdrop-blur-md border border-white text-slate-600 dark:text-slate-300 hover:text-blue-600 hover:bg-white dark:bg-slate-800 flex items-center justify-center shadow-lg transition-all active:scale-90 z-10">
              <i class="fas fa-chevron-left"></i>
            </button>
            <button @click="nextCard" class="absolute top-1/2 -right-12 md:-right-16 -translate-y-1/2 w-10 h-10 md:w-12 md:h-12 rounded-full bg-white/80 dark:bg-slate-800/80 backdrop-blur-md border border-white text-slate-600 dark:text-slate-300 hover:text-blue-600 hover:bg-white dark:bg-slate-800 flex items-center justify-center shadow-lg transition-all active:scale-90 z-10">
              <i class="fas fa-chevron-right"></i>
            </button>

            <Transition :name="slideTransitionName" mode="out-in">
              <div 
                :key="currentWord.word"
                @click="isFlipped = !isFlipped"
                class="relative w-full flex-1 perspective-1000 cursor-pointer group min-h-[250px]"
              >
                <div :class="['w-full h-full transition-transform duration-700 preserve-3d relative', isFlipped ? 'rotate-y-180' : '']">
                  <!-- Front (Word) -->
                  <div class="absolute inset-0 backface-hidden bg-white/80 dark:bg-slate-800/80 backdrop-blur-2xl border-2 border-white rounded-[2rem] flex flex-col items-center justify-center p-6 shadow-2xl transition-all">
                    <span class="absolute top-4 left-4 text-[10px] font-black bg-slate-200/50 text-slate-500 dark:text-slate-300 px-3 py-1.5 rounded-lg uppercase tracking-wider">{{ currentWord.unit }}</span>
                    <button @click.stop="speak(currentWord.word)" class="absolute top-4 right-4 w-12 h-12 rounded-full bg-blue-100/80 text-blue-600 flex items-center justify-center hover:bg-blue-600 hover:text-white transition-all active:scale-90 shadow-md text-lg">
                      <i class="fas fa-volume-up"></i>
                    </button>
                    <h2 class="text-4xl md:text-5xl font-black mb-4 tracking-tight text-slate-800 dark:text-slate-100 text-center drop-shadow-sm">{{ currentWord.word }}</h2>
                    <p class="text-xl md:text-2xl text-slate-500 dark:text-slate-300 font-medium tracking-widest">{{ currentWord.phonetic }}</p>
                    <div class="absolute bottom-6 flex items-center gap-2 text-xs text-slate-400 dark:text-slate-300 font-bold bg-slate-100 dark:bg-slate-800/50 backdrop-blur-md px-4 py-2 rounded-full">
                      <i class="fas fa-sync-alt"></i> 点击卡片查看释义
                    </div>
                  </div>
                  
                  <!-- Back (Definition) -->
                  <div class="absolute inset-0 backface-hidden rotate-y-180 bg-emerald-100/80 backdrop-blur-2xl border-2 border-emerald-200 rounded-[2rem] flex flex-col items-center justify-center p-8 shadow-2xl transition-all">
                    <h3 class="text-xs font-black text-emerald-600 uppercase tracking-widest mb-6 flex items-center gap-2 bg-white/50 dark:bg-slate-800/50 px-3 py-1.5 rounded-full"><i class="fas fa-book-open"></i> 释义</h3>
                    <p class="text-2xl md:text-3xl text-center leading-relaxed font-extrabold text-slate-800 dark:text-slate-100 drop-shadow-sm">{{ currentWord.definition }}</p>
                  </div>
                </div>
              </div>
            </Transition>
            
            <!-- Card Controls -->
            <div class="flex justify-between items-center mt-6 gap-4 shrink-0">
              <button @click="markCard('unfamiliar')" class="flex-1 py-4 rounded-2xl border border-white/50 dark:border-slate-600/40 bg-orange-100/80 backdrop-blur-md hover:bg-orange-500 hover:text-white hover:border-orange-500 text-orange-700 font-black transition-all active:scale-95 shadow-lg group text-sm md:text-base">
                <i class="fas fa-times mr-1 group-hover:rotate-90 transition-transform"></i> 仍陌生
              </button>
              <button @click="markCard('mastered')" class="flex-1 py-4 rounded-2xl border border-white/50 dark:border-slate-600/40 bg-emerald-100/80 backdrop-blur-md hover:bg-emerald-500 hover:text-white hover:border-emerald-500 text-emerald-700 font-black transition-all active:scale-95 shadow-lg group text-sm md:text-base">
                <i class="fas fa-check mr-1 group-hover:scale-125 transition-transform"></i> 已掌握
              </button>
            </div>
          </div>
          <div v-else class="text-slate-500 dark:text-slate-400 dark:text-slate-500 text-lg font-bold bg-white/50 dark:bg-slate-800/50 px-6 py-3 rounded-full backdrop-blur-md shadow-sm">没有找到对应的单词</div>
        </div>
      </div>
      
    </main>

    <!-- Right: AI Assistant Sidebar -->
    <aside class="w-64 lg:w-72 flex flex-col shrink-0 overflow-hidden relative z-10 bg-white/50 dark:bg-slate-800/50 backdrop-blur-xl border border-white/40 dark:border-slate-600/30 rounded-3xl shadow-xl">
      <!-- AI Header -->
      <header class="p-4 border-b border-white/40 dark:border-slate-600/30 shrink-0 z-10 bg-gradient-to-r from-blue-100/50 to-indigo-100/50 backdrop-blur-md">
        <h2 class="text-base font-black flex items-center gap-2 text-slate-800 dark:text-slate-100">
          <div class="w-8 h-8 rounded-full bg-blue-600 text-white flex items-center justify-center shadow-md">
            <i class="fas fa-robot text-xs"></i>
          </div>
          AI 智能伴学
        </h2>
        <p class="text-[11px] text-slate-600 dark:text-slate-300 mt-1 ml-10 font-medium">查词缀 / 造句 / 联想记忆</p>
      </header>

      <!-- Chat Messages -->
      <div class="flex-1 overflow-y-auto p-4 space-y-4 custom-scrollbar" ref="chatContainer">
        <div v-for="(msg, i) in messages" :key="i" :class="['flex w-full', msg.role === 'user' ? 'justify-end' : 'justify-start']">
          
          <div v-if="msg.role === 'assistant'" class="w-7 h-7 rounded-full bg-white/80 dark:bg-slate-800/80 border border-white backdrop-blur-sm text-blue-600 flex items-center justify-center shrink-0 mr-2 mt-1 shadow-sm">
            <i class="fas fa-robot text-[10px]"></i>
          </div>

          <div :class="['max-w-[80%] rounded-2xl px-4 py-2.5 text-[13px] leading-relaxed shadow-md backdrop-blur-md', 
            msg.role === 'user' ? 'bg-blue-600/90 text-white rounded-tr-sm border border-blue-500/50' : 'bg-white/80 dark:bg-slate-800/80 border border-white text-slate-800 dark:text-slate-100 font-medium rounded-tl-sm']">
            {{ msg.content }}
          </div>
        </div>
      </div>

      <!-- Chat Input -->
      <div class="p-3 bg-white/40 dark:bg-slate-800/40 backdrop-blur-xl border-t border-white/40 dark:border-slate-600/30 shrink-0">
        <div class="relative flex items-center bg-white/60 dark:bg-slate-800/60 border border-white/80 dark:border-slate-600/60 rounded-2xl p-1 focus-within:border-blue-400 focus-within:ring-4 focus-within:ring-blue-400/20 transition-all shadow-inner">
          <input 
            v-model="aiInput" @keydown.enter="sendAiMessage"
            type="text" placeholder="输入问题..." :disabled="isGenerating"
            class="flex-1 bg-transparent border-none outline-none px-3 py-2 text-sm text-slate-800 dark:text-slate-100 font-medium placeholder-slate-500"
          >
          <button 
            @click="sendAiMessage" :disabled="!aiInput.trim() || isGenerating"
            class="mr-0.5 w-9 h-9 rounded-xl bg-blue-600/90 text-white flex items-center justify-center transition-all disabled:opacity-50 disabled:bg-slate-400 hover:bg-blue-600 active:scale-90 shadow-md backdrop-blur-md"
          >
            <i class="fas fa-paper-plane text-[11px]"></i>
          </button>
        </div>
      </div>
    </aside>
    
    <!-- Settings Modal -->
    <div v-if="showSettings" class="fixed inset-0 z-[100] flex items-center justify-center bg-black/40 backdrop-blur-sm transition-all" @click.self="showSettings = false">
      <div class="bg-white/90 dark:bg-slate-800/90 backdrop-blur-2xl p-6 rounded-3xl w-full max-w-sm shadow-2xl border border-white/50 dark:border-slate-600/40 animate-fade-in-down">
        <div class="flex justify-between items-center mb-6">
          <h2 class="text-xl font-bold text-slate-800 dark:text-slate-100 flex items-center gap-2">
            <i class="fas fa-palette text-blue-500"></i> 个性化设置
          </h2>
          <button @click="showSettings = false" class="w-8 h-8 rounded-full bg-slate-100 dark:bg-slate-800/50 hover:bg-slate-200 dark:hover:bg-slate-700/50 text-slate-500 dark:text-slate-400 dark:text-slate-500 flex items-center justify-center transition-colors">
            <i class="fas fa-times"></i>
          </button>
        </div>

        <div class="space-y-6">
          <!-- Background URL & Upload -->
          <div>
            <label class="block text-sm font-bold text-slate-700 dark:text-slate-200 mb-2">自定义背景图片 (URL / 本地上传)</label>
            <div class="flex gap-2">
              <input v-model="customBgUrl" @change="saveSettings" type="text" placeholder="https://..." class="flex-1 bg-white/50 dark:bg-slate-800/50 border border-slate-200 dark:border-slate-700/50 px-3 py-2 rounded-xl focus:ring-2 focus:ring-blue-400 outline-none transition-all text-sm">
              <label class="bg-blue-50 text-blue-600 hover:bg-blue-100 px-3 py-2 rounded-xl cursor-pointer transition-colors flex items-center justify-center border border-blue-100" title="上传本地图片">
                <i class="fas fa-upload"></i>
                <input type="file" accept="image/*" class="hidden" @change="handleBgUpload">
              </label>
            </div>
            <div class="mt-3 flex gap-2">
              <button @click="setPresetBg('')" class="flex-1 py-1.5 text-xs bg-slate-100 dark:bg-slate-800/50 hover:bg-slate-200 dark:hover:bg-slate-700/50 rounded-lg transition-colors font-medium">默认极简</button>
              <button @click="setPresetBg('https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=1600&q=80')" class="flex-1 py-1.5 text-xs bg-blue-50 text-blue-600 hover:bg-blue-100 rounded-lg transition-colors font-medium">海滩度假</button>
              <button @click="setPresetBg('https://images.unsplash.com/photo-1534008897995-27a23e859048?w=1600&q=80')" class="flex-1 py-1.5 text-xs bg-indigo-50 text-indigo-600 hover:bg-indigo-100 rounded-lg transition-colors font-medium">抽象艺术</button>
            </div>
          </div>

          <!-- Night Mode Mask -->
          <div class="flex items-center justify-between p-3 bg-slate-50/50 dark:bg-slate-900/50 rounded-xl border border-slate-100 dark:border-slate-700/50">
            <div>
              <div class="text-sm font-bold text-slate-700 dark:text-slate-200 flex items-center gap-2">
                <i class="fas fa-moon text-indigo-500"></i> 深色模式 (Dark Mode)
              </div>
              <div class="text-xs text-slate-500 dark:text-slate-400 mt-0.5">体验原生 iOS 级暗色主题自适应</div>
            </div>
            <label class="relative inline-flex items-center cursor-pointer">
              <input type="checkbox" v-model="isNightMode" @change="saveSettings" class="sr-only peer">
              <div class="w-11 h-6 bg-slate-200 dark:bg-slate-700 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-slate-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-indigo-600 dark:peer-checked:bg-indigo-500"></div>
            </label>
          </div>
        </div>
      </div>
    </div>
    
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick, watch, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { VOCABULARY_DATA } from '../data/words.js'

const router = useRouter()

// UI State
const showSettings = ref(false)
const isNightMode = ref(localStorage.getItem('night_mode') === 'true')
const customBgUrl = ref(localStorage.getItem('custom_bg') || '')

const customBgStyle = computed(() => {
  return customBgUrl.value ? { backgroundImage: `url(${customBgUrl.value})`, backgroundSize: 'cover', backgroundPosition: 'center' } : {}
})

const saveSettings = () => {
  localStorage.setItem('night_mode', isNightMode.value)
  localStorage.setItem('custom_bg', customBgUrl.value)
}

const setPresetBg = (url) => {
  customBgUrl.value = url
  saveSettings()
}

const handleBgUpload = (e) => {
  const file = e.target.files[0]
  if (!file) return
  
  const reader = new FileReader()
  reader.onload = (event) => {
    const base64 = event.target.result
    customBgUrl.value = base64
    try {
      saveSettings()
    } catch (err) {
      alert('图片过大，无法本地持久化保存，但当前会话已生效。建议裁剪后再上传。')
    }
  }
  reader.readAsDataURL(file)
}

// Dark Mode logic
const applyNightMode = (val) => {
  if (val) {
    document.documentElement.classList.add('dark')
  } else {
    document.documentElement.classList.remove('dark')
  }
}

watch(isNightMode, (newVal) => {
  applyNightMode(newVal)
})

const handleLogout = () => {
  if (confirm('确定要退出登录吗？退出后将返回登录界面。')) {
    localStorage.removeItem('current_user')
    router.push('/')
  }
}

// Proverb
const proverbs = [
  { en: 'Where there is a will, there is a way.', zh: '有志者，事竟成。' },
  { en: 'Rome was not built in a day.', zh: '冰冻三尺，非一日之寒。' },
  { en: 'A journey of a thousand miles begins with a single step.', zh: '千里之行，始于足下。' },
  { en: 'Nothing is impossible to a willing heart.', zh: '心之所愿，无事不成。' },
  { en: 'Practice makes perfect.', zh: '熟能生巧。' },
  { en: 'Time is money.', zh: '一寸光阴一寸金。' }
]
const currentProverb = ref(proverbs[0])

// --- View Toggles ---
const hideAllDefinitions = ref(false)
const hideMasteredDefinitions = ref(false)

// Background Image Logic (Shared with Login)
const defaultBg = 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?q=80&w=2073&auto=format&fit=crop'
const backgroundImage = ref(localStorage.getItem('custom_bg') || defaultBg)

// --- Study Time Tracker ---
const studySeconds = ref(parseInt(localStorage.getItem('study_seconds') || '0'))
let timerInterval = null

const formattedStudyTime = computed(() => {
  const m = Math.floor(studySeconds.value / 60)
  const s = studySeconds.value % 60
  return `${m} 分 ${s} 秒`
})

onMounted(() => {
  applyNightMode(isNightMode.value)
  currentProverb.value = proverbs[Math.floor(Math.random() * proverbs.length)]

  timerInterval = setInterval(() => {
    studySeconds.value++
    if (studySeconds.value % 10 === 0) {
      localStorage.setItem('study_seconds', studySeconds.value)
    }
  }, 1000)
})

onUnmounted(() => {
  if (timerInterval) clearInterval(timerInterval)
})

// --- Book Management ---
const currentBookName = ref('默认词书')
const words = ref([])
const originalWords = ref([])
const progress = reactive(JSON.parse(localStorage.getItem('vocab_progress') || '{}'))
const currentUser = ref(JSON.parse(localStorage.getItem('current_user') || 'null'))
const isSyncing = ref(false)

const fetchProgressFromDB = async () => {
  if (!currentUser.value) return
  isSyncing.value = true
  try {
    const res = await fetch(`http://localhost:8081/api/progress/${currentUser.value.id}`)
    if (res.ok) {
      const data = await res.json()
      // We MUST clear local progress first to prevent previous user's progress carrying over to a new user
      for (const key in progress) {
        delete progress[key]
      }
      Object.assign(progress, data)
      localStorage.setItem('vocab_progress', JSON.stringify(progress))
    }
  } catch (err) {
    console.error('Failed to fetch progress from DB:', err)
  } finally {
    isSyncing.value = false
  }
}

const syncProgressToDB = async () => {
  if (!currentUser.value) return
  try {
    await fetch(`http://localhost:8081/api/progress/${currentUser.value.id}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(progress)
    })
  } catch (err) {
    console.error('Failed to sync progress to DB:', err)
  }
}

let syncTimeout = null
watch(progress, (newVal) => {
  localStorage.setItem('vocab_progress', JSON.stringify(newVal))
  if (currentUser.value) {
    clearTimeout(syncTimeout)
    syncTimeout = setTimeout(() => {
      syncProgressToDB()
    }, 2000)
  }
}, { deep: true })

const loadCurrentBook = async () => {
  const bookId = localStorage.getItem('current_book_id') || 'builtin'
  if (bookId === 'builtin') {
    words.value = [...VOCABULARY_DATA]
    originalWords.value = [...VOCABULARY_DATA]
    currentBookName.value = '大学核心词汇 (系统)'
  } else {
    const importedBooks = JSON.parse(localStorage.getItem('imported_vocab_books') || '[]')
    const book = importedBooks.find(b => b.id === bookId)
    if (book) {
      words.value = [...book.words]
      originalWords.value = [...book.words]
      currentBookName.value = book.name
    } else {
      words.value = [...VOCABULARY_DATA]
      originalWords.value = [...VOCABULARY_DATA]
      currentBookName.value = '大学核心词汇 (系统)'
    }
  }
  
  if (currentUser.value) {
    await fetchProgressFromDB()
  }
}

// --- Vocabulary Logic ---
const searchQuery = ref('')
const viewMode = ref('list')
const currentIndex = ref(0)
const isFlipped = ref(false)

const isVoiceMenuOpen = ref(false)
const selectVoice = (idx) => {
  selectedVoiceIndex.value = idx
  isVoiceMenuOpen.value = false
  // Close menu if clicked outside? We can keep it simple.
}

const slideTransitionName = ref('slide-left')

const getStatus = (wordText) => progress[wordText] || 'new'

const updateStatus = (wordText, status) => {
  if (status === 'new') {
    delete progress[wordText]
  } else {
    progress[wordText] = status
  }
}

const filteredWords = computed(() => {
  const query = searchQuery.value.toLowerCase()
  if (!query) return words.value
  return words.value.filter(w => 
    w.word.toLowerCase().includes(query) || 
    w.definition.toLowerCase().includes(query) ||
    w.phonetic.toLowerCase().includes(query)
  )
})

const currentWord = computed(() => {
  if (filteredWords.value.length === 0) return {}
  return filteredWords.value[currentIndex.value]
})

const stats = computed(() => {
  let m = 0, u = 0
  words.value.forEach(w => {
    const s = getStatus(w.word)
    if (s === 'mastered') m++
    else if (s === 'unfamiliar') u++
  })
  return {
    mastered: m,
    unfamiliar: u,
    new: words.value.length - m - u
  }
})

const progressPercentage = computed(() => {
  if (words.value.length === 0) return 0
  return Math.round((stats.value.mastered / words.value.length) * 100)
})

const shuffleWords = () => {
  const array = [...words.value]
  for (let i = array.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [array[i], array[j]] = [array[j], array[i]]
  }
  words.value = array
  slideTransitionName.value = 'slide-left'
  currentIndex.value = 0
  isFlipped.value = false
}

const restoreOrder = () => {
  words.value = [...originalWords.value]
  slideTransitionName.value = 'slide-left'
  currentIndex.value = 0
  isFlipped.value = false
}

const prevCard = () => {
  if (filteredWords.value.length === 0) return
  isFlipped.value = false
  slideTransitionName.value = 'slide-right'
  if (currentIndex.value > 0) {
    currentIndex.value--
  } else {
    currentIndex.value = filteredWords.value.length - 1
  }
}

const nextCard = () => {
  if (filteredWords.value.length === 0) return
  isFlipped.value = false
  slideTransitionName.value = 'slide-left'
  if (currentIndex.value < filteredWords.value.length - 1) {
    currentIndex.value++
  } else {
    currentIndex.value = 0
  }
}

const markCard = (status) => {
  if (filteredWords.value.length === 0) return
  const word = currentWord.value.word
  updateStatus(word, status)
  
  isFlipped.value = false
  setTimeout(() => {
    nextCard()
  }, 100) 
}

// --- Text to Speech with Voice Selection ---
const availableVoices = ref([])
const selectedVoiceIndex = ref(0)
let voicesLoaded = false

const initVoices = () => {
  const voices = window.speechSynthesis.getVoices()
  if (voices.length > 0 && !voicesLoaded) {
    const enVoices = voices.filter(v => v.lang.startsWith('en'))
    
    // Keywords for matching genders
    const femaleWords = ['zira', 'samantha', 'female', 'jenny', 'aria', 'hazel', 'serena', '女', 'xiaoxiao']
    const maleWords = ['david', 'alex', 'male', 'george', 'daniel', 'mark', 'guy', '男', 'yunxi']
    
    const isFemale = (v) => femaleWords.some(w => v.name.toLowerCase().includes(w))
    const isMale = (v) => maleWords.some(w => v.name.toLowerCase().includes(w))
    
    const usVoices = enVoices.filter(v => v.lang.includes('US'))
    const ukVoices = enVoices.filter(v => v.lang.includes('GB') || v.lang.includes('UK'))
    
    // Safely fallback if we don't find a perfect match
    const fUS = usVoices.find(v => isFemale(v)) || usVoices[0] || enVoices[0]
    const mUS = usVoices.find(v => isMale(v)) || usVoices.find(v => v !== fUS) || enVoices.find(v => v !== fUS)
    
    const fUK = ukVoices.find(v => isFemale(v)) || ukVoices[0] || enVoices.find(v => v !== fUS && v !== mUS)
    const mUK = ukVoices.find(v => isMale(v)) || ukVoices.find(v => v !== fUK) || enVoices.find(v => v !== fUS && v !== mUS && v !== fUK)

    const presets = []
    if (fUS) presets.push({ label: '美音女声', voice: fUS })
    if (mUS && mUS !== fUS) presets.push({ label: '美音男声', voice: mUS })
    if (fUK && fUK !== fUS && fUK !== mUS) presets.push({ label: '英音女声', voice: fUK })
    if (mUK && mUK !== fUS && mUK !== mUS && mUK !== fUK) presets.push({ label: '英音男声', voice: mUK })

    // If we still don't have 4, fill up to 4 with remaining distinct English voices
    for (const v of enVoices) {
      if (presets.length >= 4) break;
      if (!presets.find(p => p.voice === v)) {
        presets.push({ label: '备用发音 - ' + (v.name.split(' ')[0] || '默认'), voice: v })
      }
    }

    if(presets.length > 0) {
      availableVoices.value = presets
      selectedVoiceIndex.value = 0
    } else {
      availableVoices.value = [{ label: '系统默认发音', voice: voices[0] }]
    }
    voicesLoaded = true
  }
}

const speak = (text) => {
  if (!('speechSynthesis' in window)) return
  window.speechSynthesis.cancel()
  const utterance = new SpeechSynthesisUtterance(text)
  utterance.lang = 'en-US'
  utterance.rate = 0.9
  
  if (availableVoices.value.length > 0) {
    utterance.voice = availableVoices.value[selectedVoiceIndex.value].voice
  }
  
  window.speechSynthesis.speak(utterance)
}

onMounted(() => {
  loadCurrentBook()
  
  initVoices()
  if (window.speechSynthesis.onvoiceschanged !== undefined) {
    window.speechSynthesis.onvoiceschanged = initVoices
  }

  window.addEventListener('keydown', (e) => {
    if (viewMode.value !== 'card' || document.activeElement.tagName === 'INPUT') return
    if (e.code === 'Space') {
      e.preventDefault()
      isFlipped.value = !isFlipped.value
    } else if (e.code === 'ArrowLeft') {
      prevCard()
    } else if (e.code === 'ArrowRight') {
      nextCard()
    }
  })
})

// --- AI Assistant Logic ---
const messages = ref([
  { role: 'assistant', content: '您好！我是您的 AI 单词助教。今天也要加油哦！遇到难记的单词，随时发给我～' }
])
const aiInput = ref('')
const isGenerating = ref(false)
const chatContainer = ref(null)

const scrollToBottom = async () => {
  await nextTick()
  if (chatContainer.value) {
    chatContainer.value.scrollTop = chatContainer.value.scrollHeight
  }
}

const sendAiMessage = async () => {
  const text = aiInput.value.trim()
  if (!text || isGenerating.value) return

  messages.value.push({ role: 'user', content: text })
  aiInput.value = ''
  isGenerating.value = true
  scrollToBottom()

  const assistantIdx = messages.value.length
  messages.value.push({ role: 'assistant', content: '' })

  try {
    const response = await fetch('http://localhost:8081/api/chat/stream', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ message: text })
    })

    if (!response.body) throw new Error('No stream available')

    const reader = response.body.getReader()
    const decoder = new TextDecoder('utf-8')
    let buffer = ''

    while (true) {
      const { value, done } = await reader.read()
      if (done) break

      buffer += decoder.decode(value, { stream: true })
      
      const lines = buffer.split('\n')
      // The last element might be an incomplete line, so keep it in the buffer
      buffer = lines.pop() || ''

      for (const line of lines) {
        if (line.startsWith('data:')) {
          const data = line.slice(5).trim()
          if (data === '[DONE]') break
          // Handle multiline SSE or specific data formats if needed
          if (data) {
             try {
               const parsedData = JSON.parse(data)
               messages.value[assistantIdx].content += parsedData
             } catch(e) {
               messages.value[assistantIdx].content += data
             }
             scrollToBottom()
          }
        }
      }
    }
  } catch (error) {
    console.error('SSE Error:', error)
    messages.value[assistantIdx].content = '抱歉，无法连接到后端服务器。请确保 Spring Boot 已启动。'
  } finally {
    isGenerating.value = false
    scrollToBottom()
  }
}
</script>

<style scoped>
.preserve-3d {
  transform-style: preserve-3d;
}
.perspective-1000 {
  perspective: 1000px;
}
.backface-hidden {
  backface-visibility: hidden;
}
.rotate-y-180 {
  transform: rotateY(180deg);
}

/* Custom Webkit Scrollbar for Glassmorphism */
.custom-scrollbar::-webkit-scrollbar {
  width: 8px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 4px;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.1);
  border-radius: 4px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 0, 0, 0.2);
}

/* Slide Left Animation (Next Card) */
.slide-left-enter-active,
.slide-left-leave-active {
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
}
.slide-left-enter-from {
  opacity: 0;
  transform: translateX(100px) rotateY(15deg);
}
.slide-left-leave-to {
  opacity: 0;
  transform: translateX(-100px) rotateY(-15deg);
}

/* Slide Right Animation (Prev Card) */
.slide-right-enter-active,
.slide-right-leave-active {
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
}
.slide-right-enter-from {
  opacity: 0;
  transform: translateX(-100px) rotateY(-15deg);
}
.slide-right-leave-to {
  opacity: 0;
  transform: translateX(100px) rotateY(15deg);
}

/* Dropdown Animation */
@keyframes fadeInDown {
  from { opacity: 0; transform: translateY(-10px); }
  to { opacity: 1; transform: translateY(0); }
}
.animate-fade-in-down {
  animation: fadeInDown 0.2s ease-out forwards;
}
</style>
