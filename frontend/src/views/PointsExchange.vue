<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useStore } from '../store';
import { Plus, Minus, Trash2, ChevronRight, X, Star, ChevronLeft } from 'lucide-vue-next';
import CustomModal from '../components/CustomModal.vue';
import Toast from '../components/Toast.vue';

const router = useRouter();
const store = useStore();
const API_BASE_URL = 'http://localhost:3001';

// 弹窗状态
const showConfirmModal = ref(false);
const confirmMessage = ref('');
const confirmAction = ref(null);
const showAlertModal = ref(false);
const alertMessage = ref('');
const alertType = ref('info');

// Toast状态
const showToast = ref(false);
const toastMessage = ref('');
const toastType = ref('info');

const showAlert = (message, type = 'info') => {
  alertMessage.value = message;
  alertType.value = type;
  showAlertModal.value = true;
};

const showToastMessage = (message, type = 'success') => {
  toastMessage.value = message;
  toastType.value = type;
  showToast.value = true;
  setTimeout(() => {
    showToast.value = false;
  }, 3000);
};

const showConfirm = (message, action) => {
  confirmMessage.value = message;
  confirmAction.value = action;
  showConfirmModal.value = true;
};

const handleConfirm = () => {
  if (confirmAction.value) {
    confirmAction.value();
  }
  showConfirmModal.value = false;
};

const fetchRewards = async () => {
  try {
    await store.fetchPointsProducts();
  } catch (error) {
    console.error('获取积分商品失败:', error);
  }
};

const exchangeReward = async (reward) => {
  if (store.user?.points < reward.pointsRequired) {
    showAlert('积分不足，快去点单赚取积分吧！', 'warning');
    return;
  }

  showConfirm(`确定要消耗 ${reward.pointsRequired} 积分兑换 ${reward.name} 吗？`, async () => {
    try {
      const token = localStorage.getItem('token');
      const res = await fetch(`${API_BASE_URL}/api/merchant/points-products/${reward.id}/exchange?userId=${store.user?.id}`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      if (res.ok) {
        await store.fetchUser();
        await store.fetchPointsProducts();
        showToastMessage('兑换成功！可在记录中查看。');
      }
    } catch (error) {
      console.error('兑换失败:', error);
      showAlert('兑换失败，请稍后重试', 'error');
    }
  });
};

onMounted(async () => {
  await fetchRewards();
  await store.fetchUser();
});
</script>

<template>
  <div class="px-6 py-8 space-y-6 min-h-screen bg-slate-50">
    <header class="flex items-center gap-4">
      <button @click="router.back()" class="p-2 hover:bg-slate-100 rounded-full transition-colors">
        <ChevronLeft :size="24" />
      </button>
      <h1 class="text-2xl font-black text-slate-800">积分兑换</h1>
    </header>

    <div class="bg-white rounded-2xl p-4 shadow-sm border border-slate-100 mb-6">
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-lg font-bold text-slate-800">我的积分</h2>
          <p class="text-3xl font-black text-orange-600">{{ store.user?.points || 0 }}</p>
        </div>
        <div class="bg-orange-50 px-4 py-2 rounded-xl">
          <p class="text-sm font-bold text-orange-600">每消费 1 元获得 1 积分</p>
        </div>
      </div>
    </div>

    <div class="space-y-4">
      <div
        v-for="reward in store.pointsProducts"
        :key="reward.id"
        class="bg-white rounded-2xl p-4 shadow-sm border border-slate-100 hover:shadow-md transition-shadow"
      >
        <div class="flex justify-between items-start">
          <div class="flex items-center gap-4">
            <div class="w-16 h-16 bg-orange-50 rounded-xl flex items-center justify-center overflow-hidden">
              <img 
                :src="API_BASE_URL + (reward.image || '/default-image.png')" 
                alt="商品图片" 
                class="w-full h-full object-contain" 
              />
            </div>
            <div class="flex-1 min-w-0">
              <h3 class="font-bold text-slate-800 line-clamp-1">{{ reward.name }}</h3>
              <p class="text-xs text-slate-500 mt-1 line-clamp-2">{{ reward.description }}</p>
              <div class="flex items-center gap-2 mt-2">
                <span class="px-2 py-0.5 bg-purple-100 text-purple-600 rounded-full text-xs font-bold">
                  {{ reward.pointsRequired }} 积分
                </span>
                <span class="text-xs text-slate-500">剩余: {{ reward.stock }}</span>
              </div>
            </div>
          </div>
          <button
            @click="exchangeReward(reward)"
            class="px-4 py-2 bg-orange-500 text-white rounded-xl font-bold hover:bg-orange-600 transition-colors"
            :disabled="reward.stock <= 0"
            :class="{ 'opacity-50 cursor-not-allowed': reward.stock <= 0 }"
          >
            兑换
          </button>
        </div>
      </div>
    </div>

    <!-- 确认弹窗 -->
    <CustomModal
      :show="showConfirmModal"
      :title="'确认兑换'"
      :message="confirmMessage"
      :type="'info'"
      :showCancel="true"
      :confirmText="'确认'"
      :cancelText="'取消'"
      @confirm="handleConfirm"
      @cancel="showConfirmModal = false"
    />

    <!-- 提示弹窗 -->
    <CustomModal
      :show="showAlertModal"
      :title="alertType === 'error' ? '错误' : alertType === 'warning' ? '警告' : '提示'"
      :message="alertMessage"
      :type="alertType"
      @confirm="showAlertModal = false"
      @close="showAlertModal = false"
    />

    <!-- Toast 提示 -->
    <Toast
      :show="showToast"
      :message="toastMessage"
      :type="toastType"
      @close="showToast = false"
    />
  </div>
</template>