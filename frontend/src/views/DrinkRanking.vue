<script setup>
import { ref, onMounted } from 'vue';
import { Coffee, TrendingUp, ArrowLeft, Award, Medal, Trophy } from 'lucide-vue-next';
import api from '../api/axios.js';

const isLoading = ref(true);
const drinkRankings = ref([]);

const fetchDrinkRankings = async () => {
  isLoading.value = true;
  try {
    const response = await api.get('/api/merchant/drink-rankings');
    drinkRankings.value = response.data || [];
  } catch (error) {
    console.error('获取饮品排行失败:', error);
  } finally {
    isLoading.value = false;
  }
};

const getRankIcon = (index) => {
  if (index === 0) return Trophy;
  if (index === 1) return Medal;
  if (index === 2) return Award;
  return null;
};

const getRankColor = (index) => {
  if (index === 0) return 'text-amber-500';
  if (index === 1) return 'text-slate-400';
  if (index === 2) return 'text-amber-700';
  return 'text-slate-300';
};

onMounted(() => {
  fetchDrinkRankings();
});
</script>

<template>
  <div class="px-6 py-8 space-y-6 pb-32">
    <header class="flex items-center gap-4">
      <button @click="$router.back()" class="p-2 hover:bg-slate-100 rounded-xl transition-colors">
        <ArrowLeft :size="24" class="text-slate-600" />
      </button>
      <h1 class="text-2xl font-black text-slate-800">饮品排行</h1>
    </header>

    <div v-if="isLoading" class="text-center py-12">
      <div class="inline-block animate-spin rounded-full h-8 w-8 border-4 border-slate-200 border-t-slate-600"></div>
    </div>

    <div v-else-if="drinkRankings.length === 0" class="text-center py-12">
      <Coffee :size="64" class="mx-auto text-slate-300 mb-4" />
      <p class="text-slate-400">暂无饮品数据</p>
    </div>

    <div v-else class="space-y-4">
      <div
        v-for="(drink, index) in drinkRankings"
        :key="drink.id"
        :class="['bg-white rounded-2xl shadow-sm border overflow-hidden', index < 3 ? 'border-amber-200' : 'border-slate-100']"
      >
        <div class="p-5">
          <div class="flex items-center gap-4">
            <div :class="['w-12 h-12 rounded-xl flex items-center justify-center font-black text-xl', index < 3 ? 'bg-gradient-to-br from-amber-100 to-amber-50' : 'bg-slate-100']">
              <component
                v-if="getRankIcon(index)"
                :is="getRankIcon(index)"
                :size="24"
                :class="getRankColor(index)"
              />
              <span v-else :class="getRankColor(index)">{{ index + 1 }}</span>
            </div>

            <div class="flex-1">
              <h3 class="font-bold text-slate-800 text-lg">{{ drink.name }}</h3>
              <p class="text-sm text-slate-500">{{ drink.category }}</p>
            </div>

            <div class="text-right">
              <div class="flex items-center gap-1 text-blue-600 font-bold">
                <TrendingUp :size="16" />
                <span class="text-2xl">{{ drink.orderCount }}</span>
              </div>
              <p class="text-xs text-slate-400">单</p>
            </div>
          </div>

          <div class="mt-4 pt-4 border-t border-slate-100 grid grid-cols-3 gap-4 text-center">
            <div>
              <p class="text-xs text-slate-400 mb-1">总销售额</p>
              <p class="font-bold text-slate-800">￥{{ drink.totalRevenue.toFixed(2) }}</p>
            </div>
            <div>
              <p class="text-xs text-slate-400 mb-1">平均评分</p>
              <p class="font-bold text-slate-800">{{ drink.averageRating.toFixed(1) }}⭐</p>
            </div>
            <div>
              <p class="text-xs text-slate-400 mb-1">好评率</p>
              <p class="font-bold text-green-600">{{ drink.positiveRate }}%</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="bg-blue-50 rounded-2xl p-5 border border-blue-100">
      <div class="flex items-start gap-3">
        <TrendingUp :size="20" class="text-blue-600 mt-0.5" />
        <div>
          <h4 class="font-bold text-blue-800 mb-1">经营建议</h4>
          <p class="text-sm text-blue-700">
            根据当前数据，建议您增加{{ drinkRankings[0]?.name || '热销饮品'}}的库存，并考虑推出相关促销活动以提升销量。
          </p>
        </div>
      </div>
    </div>
  </div>
</template>