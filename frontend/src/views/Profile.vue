<script setup>
import { computed, onMounted } from 'vue';
import { useStore } from '../store';
import {
  Settings,
  ChevronRight,
  MapPin,
  Headphones,
  History,
  ShieldCheck,
  Award,
  Crown,
  Edit3,
  TrendingUp,
  Coffee,
  Star
} from 'lucide-vue-next';

const store = useStore();
const isMerchant = computed(() => store.user?.role === 'merchant');

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
  <div class="px-6 py-8 space-y-8 pb-32">
    <!-- Profile Header -->
    <header class="relative bg-orange-500 rounded-[3rem] p-8 text-white shadow-xl shadow-orange-100 overflow-hidden">
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
            <h1 class="text-2xl font-black">{{ store.user?.name || '用户名' }}</h1>
            <div :class="['inline-flex items-center gap-1 px-3 py-1 rounded-full text-xs font-bold uppercase tracking-widest', memberLevelClass]">
              <Award :size="10" />
              <span :class="{ 'animate-text-shimmer': isSpecialMember }">{{ memberLevel }}会员</span>
            </div>
          </div>
        </div>

        <router-link to="/edit-profile" class="bg-white/20 p-3 rounded-2xl hover:bg-white/30 transition-colors backdrop-blur-sm shadow-inner group">
          <Edit3 :size="20" class="text-white group-hover:rotate-12 transition-transform" />
        </router-link>
      </div>

      <div class="grid grid-cols-2 mt-8 gap-4 border-t border-white/20 pt-6">
        <router-link :to="isMerchant ? '/today-income' : '/exchange'" class="text-center group active:scale-95 transition-transform">
          <div class="flex items-center justify-center gap-1">
            <TrendingUp v-if="isMerchant" :size="18" class="opacity-80" />
            <div class="text-xl font-black tabular-nums group-hover:text-amber-200 transition-colors">{{ store.user?.balance || store.user?.points || 0 }}</div>
          </div>
          <div class="text-[10px] opacity-70 uppercase font-bold tracking-widest">{{ isMerchant ? '累计收入' : '累计积分' }}</div>
        </router-link>
        <router-link :to="isMerchant ? '/drink-ranking' : '/consumption-stats'" class="text-center border-l border-white/20 group active:scale-95 transition-transform">
          <div class="flex items-center justify-center gap-1">
            <Coffee v-if="isMerchant" :size="18" class="opacity-80" />
            <Star v-else :size="18" class="opacity-80" />
            <div class="text-xl font-black tabular-nums group-hover:text-amber-200 transition-colors">￥{{ totalConsumption.toFixed(1) }}</div>
          </div>
          <div class="text-[10px] opacity-70 uppercase font-bold tracking-widest">{{ isMerchant ? '饮品排行' : '累计消费' }}</div>
        </router-link>
      </div>
    </header>

    <!-- Menu Section -->
    <div class="space-y-4">
      <h3 class="px-2 text-xs font-black text-slate-400 uppercase tracking-widest">服务管理</h3>

      <div class="joy-card divide-y divide-slate-50 !p-0 overflow-hidden">
        <router-link to="/history" class="p-4 flex items-center justify-between hover:bg-slate-50 transition-colors cursor-pointer group">
          <div class="flex items-center gap-4">
            <div class="bg-indigo-50 p-3 rounded-2xl text-indigo-500"><History :size="20" /></div>
            <span class="font-bold text-slate-700">积分兑换记录</span>
          </div>
          <ChevronRight :size="20" class="text-slate-300 group-hover:translate-x-1 transition-transform" />
        </router-link>

        <router-link v-if="!isMerchant" to="/my-coupons" class="p-4 flex items-center justify-between hover:bg-slate-50 transition-colors cursor-pointer group">
          <div class="flex items-center gap-4">
            <div class="bg-orange-50 p-3 rounded-2xl text-orange-500"><Award :size="20" /></div>
            <span class="font-bold text-slate-700">我的优惠券</span>
          </div>
          <ChevronRight :size="20" class="text-slate-300 group-hover:translate-x-1 transition-transform" />
        </router-link>

        <router-link to="/support" class="p-4 flex items-center justify-between hover:bg-slate-50 transition-colors cursor-pointer group">
          <div class="flex items-center gap-4">
            <div class="bg-blue-50 p-3 rounded-2xl text-blue-500"><Headphones :size="20" /></div>
            <span class="font-bold text-slate-700">在线联系客服</span>
          </div>
          <ChevronRight :size="20" class="text-slate-300 group-hover:translate-x-1 transition-transform" />
        </router-link>

        <router-link to="/addresses" class="p-4 flex items-center justify-between hover:bg-slate-50 transition-colors cursor-pointer group">
          <div class="flex items-center gap-4">
            <div class="bg-amber-50 p-3 rounded-2xl text-amber-500"><MapPin :size="20" /></div>
            <span class="font-bold text-slate-700">收货地址管理</span>
          </div>
          <ChevronRight :size="20" class="text-slate-300 group-hover:translate-x-1 transition-transform" />
        </router-link>
      </div>
    </div>

    <!-- Security & Settings -->
    <div class="space-y-4">
      <h3 class="px-2 text-xs font-black text-slate-400 uppercase tracking-widest">账户设置</h3>
      <div class="joy-card divide-y divide-slate-50 !p-0">
        <router-link to="/privacy" class="p-4 flex items-center justify-between group cursor-pointer hover:bg-slate-50">
          <div class="flex items-center gap-4">
            <div class="bg-green-50 p-3 rounded-2xl text-green-500"><ShieldCheck :size="20" /></div>
            <span class="font-bold text-slate-700">隐私与权限</span>
          </div>
          <ChevronRight :size="20" class="text-slate-300" />
        </router-link>
        <router-link to="/settings" class="p-4 flex items-center justify-between group cursor-pointer hover:bg-slate-50">
          <div class="flex items-center gap-4">
            <div class="bg-slate-100 p-3 rounded-2xl text-slate-500"><Settings :size="20" /></div>
            <span class="font-bold text-slate-700">系统设置</span>
          </div>
          <ChevronRight :size="20" class="text-slate-300" />
        </router-link>
      </div>
    </div>

    <!-- App Version -->
    <div class="text-center py-6">
      <p class="text-[10px] font-mono text-slate-300 uppercase tracking-widest">BubbleJoy App v2.4.0</p>
    </div>
  </div>
</template>
