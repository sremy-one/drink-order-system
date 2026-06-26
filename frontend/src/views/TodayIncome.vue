<script setup>
import { ref, onMounted } from 'vue';
import { TrendingUp, Calendar, ArrowLeft, DollarSign, Package } from 'lucide-vue-next';
import api from '../api/axios.js';

const isLoading = ref(true);
const todayIncome = ref(0);
const orderCount = ref(0);
const incomeList = ref([]);
const selectedDate = ref(new Date().toISOString().split('T')[0]);

const fetchTodayIncome = async () => {
  isLoading.value = true;
  try {
    const response = await api.get(`/api/merchant/income?date=${selectedDate.value}`);
    todayIncome.value = response.data.income || 0;
    orderCount.value = response.data.orderCount || 0;
    incomeList.value = response.data.orders || [];
  } catch (error) {
    console.error('获取收入数据失败:', error);
  } finally {
    isLoading.value = false;
  }
};

onMounted(() => {
  fetchTodayIncome();
});
</script>

<template>
  <div class="px-6 py-8 space-y-6 pb-32">
    <header class="flex items-center gap-4">
      <button @click="$router.back()" class="p-2 hover:bg-slate-100 rounded-xl transition-colors">
        <ArrowLeft :size="24" class="text-slate-600" />
      </button>
      <h1 class="text-2xl font-black text-slate-800">今日收入</h1>
    </header>

    <div class="space-y-4">
      <div class="flex items-center gap-2 bg-slate-100 p-3 rounded-xl">
        <Calendar :size="20" class="text-slate-500" />
        <input
          v-model="selectedDate"
          type="date"
          @change="fetchTodayIncome"
          class="bg-transparent text-slate-700 font-medium focus:outline-none"
        />
      </div>

      <div class="merchant-gradient p-6 rounded-[2rem] text-white shadow-xl">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-white/80 text-sm font-medium mb-1">当日收入</p>
            <div class="flex items-baseline gap-1">
              <DollarSign :size="32" />
              <span class="text-4xl font-black tabular-nums">{{ todayIncome.toFixed(2) }}</span>
            </div>
          </div>
          <div class="text-right">
            <p class="text-white/80 text-sm font-medium mb-1">订单数量</p>
            <div class="flex items-center justify-end gap-1">
              <Package :size="24" />
              <span class="text-3xl font-black tabular-nums">{{ orderCount }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="isLoading" class="text-center py-12">
      <div class="inline-block animate-spin rounded-full h-8 w-8 border-4 border-slate-200 border-t-slate-600"></div>
    </div>

    <div v-else-if="incomeList.length === 0" class="text-center py-12">
      <TrendingUp :size="64" class="mx-auto text-slate-300 mb-4" />
      <p class="text-slate-400">暂无收入数据</p>
    </div>

    <div v-else class="space-y-3">
      <h2 class="text-lg font-bold text-slate-800">收入明细</h2>
      <div
        v-for="order in incomeList"
        :key="order.id"
        class="bg-white p-4 rounded-2xl shadow-sm border border-slate-100"
      >
        <div class="flex items-center justify-between mb-2">
          <span class="text-sm text-slate-500">订单 #{{ order.id }}</span>
          <span class="font-bold text-slate-800">￥{{ order.totalAmount.toFixed(2) }}</span>
        </div>
        <div class="text-sm text-slate-600">{{ order.items || '订单商品' }}</div>
        <div class="text-xs text-slate-400 mt-1">{{ order.createdAt }}</div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.merchant-gradient {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
</style>