<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useStore } from '../store';
import { ChevronLeft, Flame, Timer, ArrowRight, Edit, Trash2 } from 'lucide-vue-next';
import api from '../api/axios.js';

const router = useRouter();
const store = useStore();
const API_BASE_URL = 'http://localhost:3001';
const isMerchant = computed(() => store.user?.role === 'merchant');
const activities = ref([]);
const isLoading = ref(true);

const fetchActivities = async () => {
  isLoading.value = true;
  try {
    const response = await api.get('/api/activities');
    activities.value = response.data || [];
  } catch (error) {
    console.error('获取活动列表失败:', error);
    activities.value = [];
  } finally {
    isLoading.value = false;
  }
};

const goToActivityManagement = () => {
  router.push('/activity-management');
};

const deleteActivity = async (activityId) => {
  if (!confirm('确定要删除这个活动吗？')) return;

  try {
    const token = localStorage.getItem('token');
    await api.delete(`/api/merchant/activities/${activityId}`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    await fetchActivities();
  } catch (error) {
    console.error('删除活动失败:', error);
  }
};

onMounted(() => {
  fetchActivities();
});
</script>

<template>
  <div class="px-6 py-8 space-y-6 min-h-screen bg-slate-50">
    <header class="flex items-center justify-between">
      <div class="flex items-center gap-4">
        <button @click="router.back()" class="p-2 hover:bg-slate-100 rounded-full transition-colors">
          <ChevronLeft :size="24" />
        </button>
        <h1 class="text-2xl font-black text-slate-800">热门活动 🔥</h1>
      </div>
      <button
        v-if="isMerchant"
        @click="goToActivityManagement"
        class="flex items-center gap-2 px-4 py-2 bg-orange-600 text-white rounded-xl font-bold text-sm hover:bg-orange-700 transition-colors"
      >
        管理活动
      </button>
    </header>

    <div v-if="isLoading" class="text-center py-20">
      <div class="inline-block animate-spin rounded-full h-8 w-8 border-4 border-slate-200 border-t-orange-600"></div>
    </div>

    <div v-else-if="activities.length === 0" class="text-center py-20">
      <Flame :size="64" class="mx-auto text-slate-300 mb-4" />
      <p class="text-slate-400">暂无活动</p>
    </div>

    <div v-else class="grid grid-cols-2 gap-6">
      <div
        v-for="activity in activities"
        :key="activity.id"
        class="relative rounded-[2.5rem] overflow-hidden group shadow-lg aspect-[4/3]"
      >
        <img :src="API_BASE_URL + (activity.image || '/default-image.png')" class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-700" />

        <div class="absolute top-4 left-4 flex gap-2 flex-wrap">
          <span class="bg-orange-500 text-white px-3 py-1 rounded-full text-[10px] font-black uppercase tracking-widest flex items-center gap-1 shadow-lg shadow-orange-500/20">
            <Flame :size="10" class="fill-white" />
            {{ activity.tag }}
          </span>
          <span class="bg-white/80 backdrop-blur-md text-slate-800 px-3 py-1 rounded-full text-[10px] font-black uppercase tracking-widest shadow-lg">
            {{ activity.badge }}
          </span>
          <span
            v-if="!activity.available"
            class="bg-gray-500 text-white px-3 py-1 rounded-full text-[10px] font-black uppercase tracking-widest shadow-lg"
          >
            已下架
          </span>
        </div>

        <div class="absolute inset-0 bg-gradient-to-t from-black/80 via-black/20 to-transparent flex flex-col justify-end p-6 gap-2">
          <h3 class="text-xl font-black text-white">{{ activity.title }}</h3>
          <p class="text-white/70 text-xs italic">{{ activity.description }}</p>

          <div class="mt-2 flex items-center justify-between text-white/50">
            <div class="flex items-center gap-1 text-[10px] font-mono tracking-widest uppercase">
              <Timer :size="12" />
              {{ activity.available ? '进行中' : '已下架' }}
            </div>
            <div class="flex items-center gap-1">
              <button
                v-if="isMerchant"
                @click="goToActivityManagement"
                class="bg-white/80 text-orange-600 p-1.5 rounded-xl hover:bg-white active:scale-90 transition-transform"
              >
                <Edit :size="14" />
              </button>
              <button
                v-if="isMerchant"
                @click="deleteActivity(activity.id)"
                class="bg-white/80 text-red-600 p-1.5 rounded-xl hover:bg-white active:scale-90 transition-transform"
              >
                <Trash2 :size="14" />
              </button>
              <button class="bg-white text-orange-600 p-2 rounded-2xl active:scale-90 transition-transform">
                <ArrowRight :size="16" />
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>