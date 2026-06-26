<script setup>
import { computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useStore } from '../store';
import { ChevronLeft, TrendingUp, Coffee, Star, Award, Crown } from 'lucide-vue-next';

const router = useRouter();
const store = useStore();

const memberLevel = computed(() => store.user?.memberLevel || '初级');

const memberLevelClass = computed(() => {
  const level = memberLevel.value;
  if (level === '至尊') return 'bg-gradient-to-r from-purple-500 via-pink-500 to-red-500 animate-gradient';
  if (level === '超级') return 'bg-gradient-to-r from-amber-400 via-orange-500 to-red-500 animate-gradient';
  if (level === '白金') return 'bg-gradient-to-r from-slate-300 to-slate-500';
  if (level === '黄金') return 'bg-gradient-to-r from-amber-300 to-amber-500';
  if (level === '白银') return 'bg-gradient-to-r from-slate-200 to-slate-400';
  return 'bg-gradient-to-r from-orange-300 to-orange-500';
});

const isSpecialMember = computed(() => {
  return memberLevel.value === '至尊' || memberLevel.value === '超级';
});

const totalConsumption = computed(() => {
  return store.user?.totalConsumption || 0;
});

const totalDrinks = computed(() => {
  return store.user?.totalDrinks || 0;
});

const favoriteCategory = computed(() => {
  return store.user?.favoriteCategory || '暂无';
});

onMounted(async () => {
  if (!store.user) {
    await store.fetchUser();
  }
});
</script>

<template>
  <div class="px-6 py-8 space-y-6 min-h-screen bg-slate-50">
    <header class="flex items-center justify-between">
      <div class="flex items-center gap-4">
        <button @click="router.back()" class="p-2 hover:bg-slate-100 rounded-full transition-colors">
          <ChevronLeft :size="24" />
        </button>
        <h1 class="text-2xl font-black text-slate-800">消费统计</h1>
      </div>
    </header>

    <!-- Member Level Card -->
    <div class="relative bg-orange-500 rounded-[3rem] p-8 text-white shadow-xl shadow-orange-100 overflow-hidden">
      <div class="absolute top-0 right-0 w-32 h-32 bg-white/10 blur-3xl rounded-full translate-x-10 -translate-y-10"></div>
      
      <div class="flex items-center justify-between relative">
        <div class="flex items-center gap-6">
          <div class="relative">
            <img :src="store.user?.avatar ? 'http://localhost:3001' + store.user.avatar : ''" class="w-24 h-24 rounded-full border-4 border-white/20" />
            <div class="absolute -bottom-2 -right-2 bg-amber-400 p-2 rounded-full border-2 border-orange-500">
              <Crown :size="16" class="text-white" />
            </div>
          </div>
          <div class="space-y-1">
            <h2 class="text-2xl font-black">{{ store.user?.name || '用户名' }}</h2>
            <div :class="['inline-flex items-center gap-1 px-3 py-1 rounded-full text-xs font-bold uppercase tracking-widest', memberLevelClass]">
              <Award :size="10" />
              <span :class="{ 'animate-text-shimmer': isSpecialMember }">{{ memberLevel }}会员</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Consumption Stats -->
    <div class="space-y-4">
      <h3 class="px-2 text-xs font-black text-slate-400 uppercase tracking-widest">消费数据</h3>

      <div class="grid grid-cols-1 gap-4">
        <div class="bg-white rounded-2xl p-6 shadow-sm border border-slate-100">
          <div class="flex items-center justify-between mb-4">
            <div class="flex items-center gap-3">
              <div class="bg-orange-50 p-3 rounded-2xl text-orange-500">
                <TrendingUp :size="24" />
              </div>
              <div>
                <div class="text-sm text-slate-500">累计消费</div>
                <div class="text-3xl font-black text-orange-500">￥{{ totalConsumption.toFixed(1) }}</div>
              </div>
            </div>
          </div>
          
          <div class="flex items-center justify-between mb-4">
            <div class="flex items-center gap-3">
              <div class="bg-indigo-50 p-3 rounded-2xl text-indigo-500">
                <Coffee :size="24" />
              </div>
              <div>
                <div class="text-sm text-slate-500">总饮用杯数</div>
                <div class="text-3xl font-black text-indigo-500">{{ totalDrinks }}</div>
              </div>
            </div>
          </div>
          
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-3">
              <div class="bg-amber-50 p-3 rounded-2xl text-amber-500">
                <Star :size="24" />
              </div>
              <div>
                <div class="text-sm text-slate-500">最爱品类</div>
                <div class="text-2xl font-black text-amber-500">{{ favoriteCategory }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Member Benefits -->
    <div class="space-y-4">
      <h3 class="px-2 text-xs font-black text-slate-400 uppercase tracking-widest">会员权益</h3>
      
      <div class="bg-white rounded-2xl p-6 shadow-sm border border-slate-100 space-y-4">
        <div class="flex items-start gap-4 p-4 bg-orange-50 rounded-xl">
          <div class="bg-orange-500 p-2 rounded-full text-white">
            <Crown :size="16" />
          </div>
          <div class="flex-1">
            <h4 class="font-bold text-slate-800">专属优惠</h4>
            <p class="text-sm text-slate-500">享受会员专享优惠券和折扣活动</p>
          </div>
        </div>
        
        <div class="flex items-start gap-4 p-4 bg-indigo-50 rounded-xl">
          <div class="bg-indigo-500 p-2 rounded-full text-white">
            <Coffee :size="16" />
          </div>
          <div class="flex-1">
            <h4 class="font-bold text-slate-800">积分翻倍</h4>
            <p class="text-sm text-slate-500">消费时获得更多积分奖励</p>
          </div>
        </div>
        
        <div class="flex items-start gap-4 p-4 bg-amber-50 rounded-xl">
          <div class="bg-amber-500 p-2 rounded-full text-white">
            <Award :size="16" />
          </div>
          <div class="flex-1">
            <h4 class="font-bold text-slate-800">优先服务</h4>
            <p class="text-sm text-slate-500">享受优先制作和配送服务</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>