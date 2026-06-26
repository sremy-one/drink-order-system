<script setup>
import { onMounted, computed, ref } from 'vue';
import { useStore } from '../store';
import { Wallet, Ticket, Gift, Star, Zap, Coffee, ShoppingCart, Gift as GiftIcon, Flame, TrendingUp, MessageSquare, Package, Edit, Plus, Store } from 'lucide-vue-next';

const store = useStore();
const isMerchant = computed(() => store.user?.role === 'merchant');
const newCouponsCount = ref(0);

onMounted(() => {
  if (!store.user) {
    store.fetchUser();
  }
  fetchNewCouponsCount();
});

const fetchNewCouponsCount = async () => {
  try {
    const token = localStorage.getItem('token');
    const res = await fetch('http://localhost:3001/api/coupons/available', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    if (res.ok) {
      const data = await res.json();
      newCouponsCount.value = data.length;
    }
  } catch (error) {
    console.error('获取优惠券数量失败:', error);
  }
};
</script>

<template>
  <div class="px-6 py-8 space-y-8 pb-32">
    <!-- User Header -->
    <header v-if="store.user" class="flex items-center justify-between">
      <div class="flex items-center gap-4">
        <img :src="store.user?.avatar ? 'http://localhost:3001' + store.user.avatar : ''" class="w-16 h-16 rounded-full border-4 border-white shadow-sm" />
        <div>
          <h1 class="text-xl font-black text-slate-800">Hi, {{ store.user.name }}!</h1>
          <p class="text-sm text-slate-400">{{ isMerchant ? '开启美好经营的一天 🏪' : '开启元气满满的一天 🥤' }}</p>
        </div>
      </div>
      <div :class="isMerchant ? 'bg-blue-100 text-blue-600' : 'bg-orange-100 text-orange-600'" class="p-3 rounded-2xl">
        <Zap :size="24" :class="isMerchant ? 'fill-blue-500' : 'fill-orange-500'" />
      </div>
    </header>

    <!-- Stats Card -->
    <div :class="isMerchant ? 'merchant-gradient' : 'joy-gradient'" class="p-6 rounded-[2.5rem] text-white flex justify-between items-center shadow-xl shadow-orange-100">
      <router-link :to="isMerchant ? '/today-income' : '/balance'" class="text-center px-4 border-r border-white/20 flex-1 group">
        <div class="flex items-center justify-center gap-1 opacity-80 mb-1 group-hover:scale-110 transition-transform">
          <TrendingUp v-if="isMerchant" :size="14" />
          <Wallet v-else :size="14" />
          <span class="text-xs font-bold uppercase tracking-widest">{{ isMerchant ? '今日收入' : '余额' }}</span>
        </div>
        <div class="text-2xl font-black tabular-nums">{{ isMerchant ? '￥' : '' }}{{ store.user?.balance || 0 }}</div>
      </router-link>
      <router-link :to="isMerchant ? '/reviews' : '/coupons'" class="text-center px-4 flex-1 group">
        <div class="flex items-center justify-center gap-1 opacity-80 mb-1 group-hover:scale-110 transition-transform">
          <MessageSquare v-if="isMerchant" :size="14" />
          <Ticket v-else :size="14" />
          <span class="text-xs font-bold uppercase tracking-widest">{{ isMerchant ? '收到评价' : '优惠券' }}</span>
        </div>
        <div class="text-2xl font-black tabular-nums">{{ store.user?.coupons || 0 }}<span class="text-sm font-normal ml-0.5">{{ isMerchant ? '条' : '张' }}</span></div>
      </router-link>
    </div>

    <!-- Quick Actions -->
    <div class="grid grid-cols-2 gap-4">
      <router-link :to="isMerchant ? '/product-management' : '/merchant-select'" class="joy-card flex flex-col items-center justify-center py-8 gap-3 bg-white">
        <div :class="isMerchant ? 'bg-blue-50 text-blue-500' : 'bg-orange-50 text-orange-500'" class="w-16 h-16 rounded-full flex items-center justify-center">
          <Package v-if="isMerchant" :size="32" />
          <ShoppingCart v-else :size="32" />
        </div>
        <div class="text-lg font-black">{{ isMerchant ? '商品管理' : '门店自取' }}</div>
        <p class="text-xs text-slate-400">{{ isMerchant ? '管理您的商品' : '快速下单不排队' }}</p>
      </router-link>
      <router-link :to="isMerchant ? '/branch-management' : '/merchant-select'" class="joy-card flex flex-col items-center justify-center py-8 gap-3 bg-white">
        <div :class="isMerchant ? 'bg-green-50 text-green-500' : 'bg-blue-50 text-blue-500'" class="w-16 h-16 rounded-full flex items-center justify-center">
          <Store v-if="isMerchant" :size="32" />
          <Zap v-else :size="32" class="fill-blue-500" />
        </div>
        <div class="text-lg font-black">{{ isMerchant ? '门店管理' : '外卖配送' }}</div>
        <p class="text-xs text-slate-400">{{ isMerchant ? '管理您的门店' : '新鲜极速送到家' }}</p>
      </router-link>
    </div>

    <!-- Features -->
    <div class="grid grid-cols-3 gap-4">
      <router-link :to="isMerchant ? '/coupon-management' : '/coupons'" class="flex flex-col items-center gap-2 group relative">
        <div :class="isMerchant ? 'bg-purple-50 text-purple-500' : 'bg-pink-50 text-pink-500'" class="w-12 h-12 rounded-2xl flex items-center justify-center group-hover:scale-110 transition-transform">
          <Ticket :size="24" />
        </div>
        <span class="text-xs font-bold text-slate-600">{{ isMerchant ? '发优惠券' : '领优惠券' }}</span>
        <span v-if="!isMerchant && newCouponsCount > 0" class="absolute -top-1 -right-1 bg-red-500 text-white text-xs font-bold rounded-full w-5 h-5 flex items-center justify-center">
          {{ newCouponsCount }}
        </span>
      </router-link>
      <router-link :to="isMerchant ? '/points-mall' : '/exchange'" class="flex flex-col items-center gap-2 group">
        <div :class="isMerchant ? 'bg-emerald-50 text-emerald-500' : 'bg-indigo-50 text-indigo-500'" class="w-12 h-12 rounded-2xl flex items-center justify-center group-hover:scale-110 transition-transform">
          <GiftIcon :size="24" />
        </div>
        <span class="text-xs font-bold text-slate-600">{{ isMerchant ? '积分商城' : '积分兑换' }}</span>
      </router-link>
      <router-link to="/activities" class="flex flex-col items-center gap-2 group">
        <div class="w-12 h-12 rounded-2xl bg-amber-50 flex items-center justify-center text-amber-500 group-hover:scale-110 transition-transform">
          <Flame :size="24" />
        </div>
        <span class="text-xs font-bold text-slate-600">热门活动</span>
      </router-link>
    </div>

    <!-- Promotion banner -->
    <div class="relative rounded-[2rem] overflow-hidden group shadow-lg">
      <img src="../img/春日桃桃系列宣传图.png" class="w-full h-48 object-cover group-hover:scale-110 transition-transform duration-700" />
      <div class="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent flex flex-col justify-end p-6">
        <div class="text-white">
          <h3 class="text-xl font-black">春日限定：桃桃系列</h3>
          <p class="text-white/70 text-sm">新品上市，满30减10</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.joy-gradient {
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%);
}

.merchant-gradient {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.joy-card {
  border-radius: 1.5rem;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
}

.joy-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}
</style>