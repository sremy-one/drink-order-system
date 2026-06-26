<script setup>
import { useRouter } from 'vue-router';
import { ChevronLeft, Coffee, Flame, Heart } from 'lucide-vue-next';

const router = useRouter();
const history = [
  { id: 1, name: '芝士莓莓', date: '2024-03-21', price: 28, image: 'https://images.unsplash.com/photo-1546173159-315724a9369b?q=80&w=200&fit=crop' },
  { id: 2, name: '生椰拿铁', date: '2024-03-20', price: 25, image: 'https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?q=80&w=200&fit=crop' },
  { id: 3, name: '招牌丝袜奶茶', date: '2024-03-18', price: 18, image: 'https://images.unsplash.com/photo-1576092768241-dec231879fc3?q=80&w=200&fit=crop' },
  { id: 4, name: '冰摇桃桃', date: '2024-03-15', price: 22, image: 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?q=80&w=200&fit=crop' },
];
</script>

<template>
  <div class="px-6 py-8 space-y-6 min-h-screen bg-slate-50">
    <header class="flex items-center gap-4">
      <button @click="router.back()" class="p-2 hover:bg-slate-100 rounded-full transition-colors">
        <ChevronLeft :size="24" />
      </button>
      <h1 class="text-2xl font-black text-slate-800">饮品足迹 🥤</h1>
    </header>

    <div class="joy-card bg-orange-500 p-8 text-white flex justify-around items-center mb-8 shadow-xl shadow-orange-100">
      <div class="text-center">
        <div class="text-3xl font-black tabular-nums">24</div>
        <div class="text-[10px] font-bold uppercase tracking-[0.2em] opacity-80">总饮用杯数</div>
      </div>
      <div class="w-px h-10 bg-white/20"></div>
      <div class="text-center">
        <div class="text-3xl font-black tabular-nums">8</div>
        <div class="text-[10px] font-bold uppercase tracking-[0.2em] opacity-80">最爱品类</div>
      </div>
    </div>

    <div class="space-y-4 pb-32">
      <h3 class="px-2 text-xs font-black text-slate-400 uppercase tracking-widest">消费明细</h3>
      <div 
        v-for="item in history" 
        :key="item.id"
        class="joy-card flex items-center gap-4 bg-white group cursor-pointer"
      >
        <div class="relative w-16 h-16 rounded-2xl overflow-hidden shrink-0 shadow-sm">
          <img :src="item.image" class="w-full h-full object-cover" />
          <div class="absolute inset-0 bg-black/10 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
            <Heart :size="20" class="text-white fill-white" />
          </div>
        </div>
        
        <div class="flex-1 min-w-0">
          <h4 class="font-black text-slate-800 truncate">{{ item.name }}</h4>
          <div class="flex items-center gap-1 text-[10px] text-slate-400 font-mono mt-1">
            <Flame :size="10" />
            热度 +1 | {{ item.date }}
          </div>
        </div>

        <div class="text-right">
          <div class="text-sm font-black text-orange-500 tabular-nums">￥{{ item.price }}</div>
          <div class="text-[9px] text-slate-300 font-bold uppercase">已完成</div>
        </div>
      </div>
    </div>
  </div>
</template>
