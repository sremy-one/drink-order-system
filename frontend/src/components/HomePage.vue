<script setup>
import { ref, onMounted, computed } from 'vue';
import { 
  Search, 
  BookOpen, 
  Zap, 
  Compass, 
  ArrowRight,
  Calculator,
  Atom,
  FlaskConical,
  Cpu
} from 'lucide-vue-next';

const icons = { Calculator, Atom, FlaskConical, Cpu };

const subjects = ref([]);
const searchQuery = ref('');
const loading = ref(true);

const emit = defineEmits(['selectSubject']);

onMounted(async () => {
  try {
    const res = await fetch('/api/subjects');
    subjects.value = await res.json();
  } catch (err) {
    console.error('Failed to fetch subjects:', err);
  } finally {
    loading.value = false;
  }
});

const filteredSubjects = computed(() => {
  if (!searchQuery.value) return subjects.value;
  return subjects.value.filter(s => 
    s.name.includes(searchQuery.value) || 
    s.id.includes(searchQuery.value.toLowerCase())
  );
});
</script>

<template>
  <div class="max-w-7xl mx-auto px-6 py-12 relative z-10">
    <!-- Hero Section -->
    <header class="text-center mb-16 space-y-6">
      <div class="inline-flex items-center gap-2 px-4 py-1.5 rounded-full bg-sky-500/10 border border-sky-500/20 text-sky-400 text-xs font-semibold tracking-widest uppercase">
        <Zap class="w-3 h-3" />
        下一代智能教学系统
      </div>
      <h1 class="text-6xl md:text-8xl font-bold tracking-tight bg-gradient-to-b from-white to-slate-500 bg-clip-text text-transparent">
        Nova EdTech
      </h1>
      <p class="text-slate-400 max-w-2xl mx-auto text-lg leading-relaxed">
        跨越星辰与微观世界，在沉浸式的交互中感受知识的脉动。选择一个学科，开启你的科创之旅。
      </p>

      <!-- Search Bar -->
      <div class="relative max-w-xl mx-auto mt-12 group">
        <div class="absolute -inset-1 bg-gradient-to-r from-sky-500 to-indigo-500 rounded-2xl blur opacity-25 group-focus-within:opacity-50 transition duration-500"></div>
        <div class="relative flex items-center glass-card px-6 py-4">
          <Search class="w-5 h-5 text-slate-400" />
          <input 
            v-model="searchQuery"
            type="text" 
            placeholder="搜索学科或知识点 (如：数学、力学...)" 
            class="w-full bg-transparent border-none focus:ring-0 text-white placeholder-slate-500 px-4"
          />
        </div>
      </div>
    </header>

    <!-- Subjects Grid -->
    <div v-if="!loading" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <div 
        v-for="subject in filteredSubjects" 
        :key="subject.id"
        @click="emit('selectSubject', subject)"
        class="glass-card p-8 group cursor-pointer hover:border-white/20 transition-all duration-500 hover:-translate-y-2 relative overflow-hidden"
      >
        <!-- Subject Accent -->
        <div 
          class="absolute top-0 right-0 w-32 h-32 blur-3xl opacity-0 group-hover:opacity-20 transition-opacity duration-500"
          :style="{ backgroundColor: subject.color }"
        ></div>

        <div class="space-y-6 relative">
          <div 
            class="w-14 h-14 rounded-xl flex items-center justify-center transition-transform duration-500 group-hover:rotate-12"
            :style="{ backgroundColor: `${subject.color}20`, color: subject.color }"
          >
            <component :is="icons[subject.icon]" class="w-7 h-7" />
          </div>
          
          <div class="space-y-2">
            <h3 class="text-2xl font-bold text-white group-hover:text-sky-400 transition-colors">
              {{ subject.name }}
            </h3>
            <p class="text-slate-400 text-sm leading-relaxed">
              {{ subject.description }}
            </p>
          </div>

          <div class="pt-4 flex items-center gap-2 text-xs font-bold uppercase tracking-wider opacity-0 group-hover:opacity-100 transition-all -translate-x-4 group-hover:translate-x-0" :style="{ color: subject.color }">
            进入实验室 <ArrowRight class="w-3 h-3" />
          </div>
        </div>
      </div>
    </div>
    
    <div v-else class="text-center py-20">
      <div class="inline-block w-8 h-8 border-2 border-sky-500/30 border-t-sky-500 rounded-full animate-spin"></div>
    </div>

    <!-- Empty State -->
    <div v-if="!loading && filteredSubjects.length === 0" class="text-center py-20 space-y-4">
      <Compass class="w-12 h-12 text-slate-700 mx-auto" />
      <p class="text-slate-500">未找到相关内容，尝试其他关键词。</p>
    </div>
  </div>
</template>
