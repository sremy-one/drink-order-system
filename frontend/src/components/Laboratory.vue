<script setup>
import { ref, onMounted } from 'vue';
import { ChevronLeft, Info, FileText, Play, Rocket } from 'lucide-vue-next';
import MathVisualizer from './MathVisualizer.vue';

const props = defineProps(['subject']);
const emit = defineEmits(['back']);

const content = ref(null);
const loading = ref(true);

onMounted(async () => {
  try {
    const res = await fetch(`/api/content/${props.subject.id}`);
    content.value = await res.json();
  } catch (err) {
    console.error('Failed to fetch subject content:', err);
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <div class="max-w-7xl mx-auto px-6 py-8 relative z-10">
    <!-- Back Header -->
    <button 
      @click="emit('back')"
      class="flex items-center gap-2 text-slate-400 hover:text-white transition-colors mb-8 group"
    >
      <ChevronLeft class="w-5 h-5 transition-transform group-hover:-translate-x-1" />
      <span class="text-xs font-bold uppercase tracking-widest">返回主控台</span>
    </button>

    <div v-if="!loading && content" class="grid grid-cols-1 lg:grid-cols-12 gap-12">
      <!-- Left: Theory & Info -->
      <div class="lg:col-span-4 space-y-8">
        <header class="space-y-4">
          <div 
            class="w-12 h-12 rounded-xl flex items-center justify-center"
            :style="{ backgroundColor: `${subject.color}20`, color: subject.color }"
          >
            <Rocket class="w-6 h-6" />
          </div>
          <h1 class="text-4xl font-bold text-white tracking-tight">
            {{ content.title }}
          </h1>
          <p class="text-slate-400 leading-relaxed italic">
            {{ content.theory }}
          </p>
        </header>

        <div class="space-y-6">
          <div class="glass-card p-6 space-y-4">
            <h4 class="flex items-center gap-2 text-xs font-bold uppercase tracking-widest text-slate-500">
              <FileText class="w-4 h-4 text-sky-400" />
              核心法则
            </h4>
            <div class="space-y-3">
              <div 
                v-for="(formula, idx) in content.formulas" 
                :key="idx"
                class="font-mono text-lg bg-white/5 p-4 rounded-xl border border-white/5 hover:border-sky-500/20 transition-colors"
                style="color: var(--on-color)"
              >
                {{ formula }}
              </div>
            </div>
          </div>

          <div class="glass-card p-6 bg-sky-500/5 border-sky-500/20">
            <p class="text-sm text-sky-400 flex items-start gap-2">
              <Info class="w-4 h-4 mt-0.5" />
              <span>你可以在右侧的交互区域实时修改参数，观察数据的反馈。这就是科学探索的乐趣所在。</span>
            </p>
          </div>
        </div>
      </div>

      <!-- Right: Visualizer -->
      <div class="lg:col-span-8 flex flex-col gap-6">
        <div class="flex items-center justify-between">
          <h2 class="text-xl font-bold text-white flex items-center gap-2">
            <Play class="w-5 h-5 text-sky-400" />
            可视化实验场
          </h2>
          <div class="text-[10px] font-mono text-slate-500 uppercase tracking-widest">
            Module: {{ subject.name }} Analysis 2.0
          </div>
        </div>

        <!-- Render visualizer based on subject -->
        <MathVisualizer v-if="subject.id === 'math'" :hue="subject.color" />
        
        <!-- Placeholder for others -->
        <div v-else class="glass-card flex-1 flex flex-col items-center justify-center p-20 text-center space-y-4">
          <Atom class="w-16 h-16 text-slate-700 animate-spin-slow" />
          <div class="space-y-2">
            <h3 class="text-xl font-bold text-slate-300">实验场正在构建中</h3>
            <p class="text-slate-500 max-w-sm">
              我们正在为 {{ subject.name }} 模块编写底层的物理仿真代码。下个版本即可开启探索之旅！
            </p>
          </div>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-else class="flex flex-col items-center justify-center py-40 space-y-6">
      <div class="w-16 h-16 border-4 border-sky-500/20 border-t-sky-500 rounded-full animate-spin"></div>
      <div class="text-sm font-mono text-sky-400 animate-pulse uppercase tracking-[0.3em]">正在链接神经元...</div>
    </div>
  </div>
</template>

<style scoped>
.animate-spin-slow {
  animation: spin 10s linear infinite;
}
@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
