<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import { useStore } from './store';
import { Home, ShoppingBag, ClipboardList, User, Package, Edit } from 'lucide-vue-next';

const route = useRoute();
const store = useStore();
const showNav = computed(() => {
  return route.path !== '/login' && route.path !== '/register';
});
const isMerchant = computed(() => store.user?.role === 'merchant');
</script>

<template>
  <div :class="['min-h-screen', showNav ? 'bg-slate-50 pb-24' : 'bg-[#121921]']">
    <router-view v-slot="{ Component }">
      <transition
        enter-active-class="transition duration-300 ease-out"
        enter-from-class="opacity-0 translate-y-4"
        enter-to-class="opacity-100 translate-y-0"
        mode="out-in"
      >
        <component :is="Component" />
      </transition>
    </router-view>

    <nav v-if="showNav" class="fixed bottom-6 left-6 right-6 h-16 bg-white/80 backdrop-blur-xl border border-slate-100 rounded-3xl shadow-xl flex items-center justify-around px-2 z-50">
      <router-link to="/" class="tab-bar-item flex-1">
        <Home :size="24" />
        <span class="text-[10px]">首页</span>
      </router-link>
      <router-link :to="isMerchant ? '/product-management' : '/order'" class="tab-bar-item flex-1">
        <Package v-if="isMerchant" :size="24" />
        <ShoppingBag v-else :size="24" />
        <span class="text-[10px]">{{ isMerchant ? '商品' : '点单' }}</span>
      </router-link>
      <router-link to="/orders" class="tab-bar-item flex-1">
        <ClipboardList :size="24" />
        <span class="text-[10px]">订单</span>
      </router-link>
      <router-link to="/profile" class="tab-bar-item flex-1">
        <User :size="24" />
        <span class="text-[10px]">我的</span>
      </router-link>
    </nav>
  </div>
</template>