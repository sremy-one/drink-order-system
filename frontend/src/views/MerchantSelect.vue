<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { Store, MapPin, ArrowRight, ArrowLeft, Coffee, Star } from 'lucide-vue-next';
import { useStore } from '../store';
import Toast from '../components/Toast.vue';

const router = useRouter();
const store = useStore();
const API_BASE_URL = 'http://localhost:3001';

// 分店列表
const branches = ref([]);
const loading = ref(true);

// Toast状态
const showToast = ref(false);
const toastMessage = ref('');
const toastType = ref('info');

const showToastMessage = (message, type = 'success') => {
  toastMessage.value = message;
  toastType.value = type;
  showToast.value = true;
  setTimeout(() => {
    showToast.value = false;
  }, 3000);
};

// 选择分店
const selectBranch = (branch) => {
  // 存储选择的分店ID和名称到本地存储
  localStorage.setItem('selectedBranchId', branch.id);
  localStorage.setItem('selectedBranchName', branch.name);
  // 跳转到订单页面
  router.push('/order');
};

// 加载分店列表
const loadBranches = async () => {
  try {
    const res = await fetch(`${API_BASE_URL}/api/merchants/branches`, {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    });
    if (res.ok) {
      const data = await res.json();
      branches.value = data;
    } else {
      showToastMessage('加载分店列表失败', 'error');
    }
  } catch (error) {
    console.error('加载分店列表失败:', error);
    showToastMessage('加载分店列表失败', 'error');
  } finally {
    loading.value = false;
  }
};

onMounted(async () => {
  await loadBranches();
});
</script>

<template>
  <div class="px-6 py-8 space-y-6 min-h-screen bg-slate-50">
    <header class="flex items-center justify-between">
      <div class="flex items-center gap-4">
        <button @click="router.push('/')" class="p-2 hover:bg-slate-100 rounded-full transition-colors">
          <ArrowLeft :size="24" class="text-slate-600" />
        </button>
        <h1 class="text-2xl font-black text-slate-800">选择分店</h1>
      </div>
    </header>

    <div class="space-y-4">
      <h2 class="text-lg font-bold text-slate-700">附近分店</h2>
      
      <div v-if="loading" class="flex justify-center items-center py-12">
        <div class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-orange-500"></div>
      </div>
      
      <div v-else-if="branches.length === 0" class="text-center py-12">
        <Store :size="48" class="mx-auto text-slate-300 mb-4" />
        <p class="text-slate-500">暂无可用分店</p>
      </div>
      
      <div v-else class="space-y-4">
        <div 
          v-for="branch in branches" 
          :key="branch.id"
          @click="selectBranch(branch)"
          class="bg-white rounded-2xl shadow-sm p-4 flex items-center gap-4 cursor-pointer hover:shadow-md transition-shadow"
        >
          <div class="w-16 h-16 rounded-full bg-orange-100 flex items-center justify-center flex-shrink-0">
            <Coffee :size="28" class="text-orange-500" />
          </div>
          <div class="flex-1">
            <div class="flex items-center justify-between">
              <h3 class="font-bold text-slate-800">{{ branch.name }}</h3>
              <div class="flex items-center gap-1">
                <Star :size="16" class="text-yellow-400 fill-yellow-400" />
                <span class="text-sm font-medium text-slate-600">{{ branch.rating || 5.0 }}</span>
              </div>
            </div>
            <div class="flex items-center gap-2 text-sm text-slate-500 mt-1">
              <MapPin :size="14" />
              <span>{{ branch.address || '未知地址' }}</span>
            </div>
            <div class="mt-2 flex items-center gap-2 text-xs">
              <span class="px-2 py-1" :class="branch.open ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'">{{ branch.open ? '营业中' : '已关闭' }}</span>
              <span class="text-slate-500">{{ branch.distance || '0.5' }}km</span>
            </div>
          </div>
          <ArrowRight :size="20" class="text-slate-400" />
        </div>
      </div>
    </div>

    <!-- Toast 提示 -->
    <Toast
      :show="showToast"
      :message="toastMessage"
      :type="toastType"
      @close="showToast = false"
    />
  </div>
</template>