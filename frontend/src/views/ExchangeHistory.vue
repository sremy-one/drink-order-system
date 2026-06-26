<script setup>
import { useRouter } from 'vue-router';
import { ChevronLeft, Calendar } from 'lucide-vue-next';

const router = useRouter();
const logs = [
  { id: 1, item: '5元现金券', points: 500, time: '2024-03-15 10:30', status: '已发放到卡券', imageUrl: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=coupon%20cash%20voucher%20gift%20card&image_size=square' },
  { id: 2, item: '珍珠奶茶系列买一赠一', points: 800, time: '2024-02-28 16:45', status: '已使用', imageUrl: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pearl%20milk%20tea%20drink%20cartoon&image_size=square' },
  { id: 3, item: 'BubbleJoy 帆布袋', points: 2500, time: '2024-01-20 09:15', status: '待自提', imageUrl: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=canvas%20tote%20bag%20shopping%20bag&image_size=square' },
];
</script>

<template>
  <div class="px-6 py-8 space-y-6 min-h-screen bg-slate-50">
    <header class="flex items-center gap-4">
      <button @click="router.back()" class="p-2 hover:bg-slate-100 rounded-full transition-colors">
        <ChevronLeft :size="24" />
      </button>
      <h1 class="text-2xl font-black text-slate-800">兑换记录 📜</h1>
    </header>

    <div v-if="logs.length" class="space-y-4">
      <div 
        v-for="log in logs" 
        :key="log.id"
        class="joy-card flex items-center gap-4 bg-white"
      >
        <div class="w-14 h-14 rounded-2xl bg-indigo-50 flex items-center justify-center shrink-0 overflow-hidden">
          <img 
            :src="log.imageUrl" 
            alt="兑换商品" 
            class="w-full h-full object-contain" 
          />
        </div>
        
        <div class="flex-1 min-w-0 space-y-1">
          <div class="flex justify-between items-start">
            <h3 class="font-black text-slate-800 truncate">{{ log.item }}</h3>
            <span class="text-xs font-black text-indigo-600 tabular-nums shrink-0">{{ log.points }}积分</span>
          </div>
          
          <div class="flex items-center gap-1 text-[10px] text-slate-400 font-mono">
            <Calendar :size="10" />
            {{ log.time }}
          </div>
          
          <div class="flex items-center gap-2 mt-2">
            <span class="text-[10px] px-2 py-0.5 rounded-full border border-slate-100 text-slate-500 font-bold uppercase tracking-widest">
              {{ log.status }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="text-center py-40 space-y-4">
      <div class="w-20 h-20 bg-slate-100 rounded-full flex items-center justify-center mx-auto text-slate-300">
        <img 
          src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=empty%20history%20icon%20minimal&image_size=square" 
          alt="无记录" 
          class="w-16 h-16 object-contain" 
        />
      </div>
      <p class="text-slate-400 text-sm font-bold">还没有兑换记录哦</p>
    </div>
  </div>
</template>