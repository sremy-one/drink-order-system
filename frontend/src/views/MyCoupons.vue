<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useStore } from '../store';
import { ChevronLeft, Ticket, CheckCircle2, XCircle, RefreshCw } from 'lucide-vue-next';
import CustomModal from '../components/CustomModal.vue';
import Toast from '../components/Toast.vue';

const router = useRouter();
const store = useStore();

const userCoupons = ref([]);
const isLoading = ref(true);
const showToast = ref(false);
const toastMessage = ref('');
const toastType = ref('success');
const showAlertModal = ref(false);
const alertMessage = ref('');
const alertType = ref('info');

const showToastMessage = (message, type = 'success') => {
  toastMessage.value = message;
  toastType.value = type;
  showToast.value = true;
  setTimeout(() => {
    showToast.value = false;
  }, 3000);
};

const showAlert = (message, type = 'info') => {
  alertMessage.value = message;
  alertType.value = type;
  showAlertModal.value = true;
};

const fetchMyCoupons = async () => {
  isLoading.value = true;
  try {
    await store.fetchUserCoupons();
    userCoupons.value = store.userCoupons || [];
  } catch (error) {
    console.error('获取我的优惠券失败:', error);
    showAlert('获取优惠券失败，请稍后重试', 'error');
  } finally {
    isLoading.value = false;
  }
};

const formatDate = (dateString) => {
  if (!dateString) return '无';
  try {
    const date = new Date(dateString);
    return date.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' });
  } catch {
    return dateString;
  }
};

const isExpired = (expireDate) => {
  if (!expireDate) return false;
  try {
    const expire = new Date(expireDate);
    return expire < new Date();
  } catch {
    return false;
  }
};

const goToCoupons = () => {
  router.push('/coupons');
};

onMounted(async () => {
  if (!store.user) {
    await store.fetchUser();
  }
  await fetchMyCoupons();
});
</script>

<template>
  <div class="px-6 py-8 space-y-6 min-h-screen bg-slate-50">
    <header class="flex items-center justify-between">
      <div class="flex items-center gap-4">
        <button @click="router.back()" class="p-2 hover:bg-slate-100 rounded-full transition-colors">
          <ChevronLeft :size="24" />
        </button>
        <h1 class="text-2xl font-black text-slate-800">我的优惠券</h1>
      </div>
      <button
        @click="fetchMyCoupons"
        class="p-2 hover:bg-slate-100 rounded-full transition-colors"
      >
        <RefreshCw :size="20" class="text-slate-500" />
      </button>
    </header>

    <div v-if="isLoading" class="text-center py-12">
      <div class="inline-block animate-spin rounded-full h-8 w-8 border-4 border-slate-200 border-t-slate-600"></div>
    </div>

    <div v-else-if="userCoupons.length === 0" class="text-center py-12">
      <Ticket :size="64" class="mx-auto text-slate-300 mb-4" />
      <p class="text-slate-400">暂无可用的优惠券</p>
      <button
        @click="goToCoupons"
        class="mt-4 px-6 py-3 bg-orange-500 text-white rounded-xl font-bold hover:bg-orange-600 transition-colors"
      >
        去领券中心
      </button>
    </div>

    <div v-else class="space-y-4">
      <div
        v-for="coupon in userCoupons"
        :key="coupon.id"
        class="relative overflow-hidden joy-card flex items-center p-0 border-none shadow-lg"
        :class="{ 'opacity-60 grayscale': coupon.isUsed || isExpired(coupon.expireDate) }"
      >
        <div
          class="w-32 py-8 flex flex-col items-center justify-center text-white"
          :class="coupon.isUsed ? 'bg-slate-400' : 'bg-green-500'"
        >
          <span class="text-xs font-bold opacity-80 mb-1">{{ coupon.isUsed ? '已使用' : '待使用' }}</span>
          <CheckCircle2 v-if="!coupon.isUsed && !isExpired(coupon.expireDate)" :size="32" />
          <XCircle v-else :size="32" />
        </div>

        <div class="flex-1 p-6 relative">
          <div class="flex flex-col gap-1">
            <h3 class="text-lg font-black text-slate-800">{{ coupon.code || '优惠券' }}</h3>
            <p v-if="coupon.description" class="text-xs text-slate-500">{{ coupon.description }}</p>
            <p class="text-xs text-slate-400">
              折扣: {{ coupon.discount }}折 | 最低消费: ￥{{ coupon.minimumSpend }}
            </p>
            <p class="text-[10px] text-slate-300 font-mono mt-2">
              {{ coupon.isUsed ? '已使用' : '有效期至: ' + formatDate(coupon.expireDate) }}
            </p>
            <p v-if="isExpired(coupon.expireDate) && !coupon.isUsed" class="text-xs text-red-500 mt-1">
              已过期
            </p>
          </div>
        </div>

        <div class="absolute left-32 top-0 bottom-0 w-px border-l-2 border-dashed border-slate-100"></div>
      </div>
    </div>

    <CustomModal
      :show="showAlertModal"
      :title="alertType === 'error' ? '错误' : alertType === 'warning' ? '警告' : '提示'"
      :message="alertMessage"
      :type="alertType"
      @confirm="showAlertModal = false"
      @close="showAlertModal = false"
    />

    <Toast
      :show="showToast"
      :message="toastMessage"
      :type="toastType"
      @close="showToast = false"
    />
  </div>
</template>