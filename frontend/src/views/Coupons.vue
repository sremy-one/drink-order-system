<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useStore } from '../store';
import { ChevronLeft, Ticket, CheckCircle2, ShoppingBag, RefreshCw } from 'lucide-vue-next';
import CustomModal from '../components/CustomModal.vue';
import Toast from '../components/Toast.vue';

const router = useRouter();
const store = useStore();

const coupons = ref([]);
const isLoading = ref(true);
const showSuccessModal = ref(false);
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

const fetchAvailableCoupons = async () => {
  isLoading.value = true;
  try {
    await store.fetchAvailableCoupons();
    coupons.value = store.availableCoupons || [];
  } catch (error) {
    console.error('获取可用优惠券失败:', error);
    showAlert('获取优惠券失败，请稍后重试', 'error');
  } finally {
    isLoading.value = false;
  }
};

const claimCoupon = async (couponId) => {
  try {
    await store.claimCoupon(couponId);
    showSuccessModal.value = true;
    await fetchAvailableCoupons();
  } catch (error) {
    console.error('领取优惠券失败:', error);
    showAlert('领取优惠券失败，请稍后重试', 'error');
  }
};

const goToOrder = () => {
  showSuccessModal.value = false;
  router.push('/order');
};

const goToMyCoupons = () => {
  showSuccessModal.value = false;
  router.push('/my-coupons');
};

const getCouponType = (coupon) => {
  if (coupon.targetMemberLevel) {
    return '专享';
  }
  if (coupon.minConsumption && coupon.minConsumption > 0) {
    return '条件';
  }
  return '通用';
};

const getCouponBgClass = (type) => {
  return type === '通用' ? 'bg-orange-500' : 'bg-indigo-500';
};

onMounted(async () => {
  if (!store.user) {
    await store.fetchUser();
  }
  await fetchAvailableCoupons();
});
</script>

<template>
  <div class="px-6 py-8 space-y-6 min-h-screen bg-slate-50">
    <header class="flex items-center justify-between">
      <div class="flex items-center gap-4">
        <button @click="router.back()" class="p-2 hover:bg-slate-100 rounded-full transition-colors">
          <ChevronLeft :size="24" />
        </button>
        <h1 class="text-2xl font-black text-slate-800">领券中心 🧧</h1>
      </div>
      <button
        @click="fetchAvailableCoupons"
        class="p-2 hover:bg-slate-100 rounded-full transition-colors"
      >
        <RefreshCw :size="20" class="text-slate-500" />
      </button>
    </header>

    <div v-if="isLoading" class="text-center py-12">
      <div class="inline-block animate-spin rounded-full h-8 w-8 border-4 border-slate-200 border-t-slate-600"></div>
    </div>

    <div v-else-if="coupons.length === 0" class="text-center py-12">
      <Ticket :size="64" class="mx-auto text-slate-300 mb-4" />
      <p class="text-slate-400">暂无可领取的优惠券</p>
      <p class="text-xs text-slate-400 mt-2">尽情期待更多优惠活动</p>
    </div>

    <div v-else class="space-y-4">
      <div
        v-for="coupon in coupons"
        :key="coupon.id"
        class="relative overflow-hidden joy-card flex items-center p-0 border-none shadow-lg"
      >
        <div
          class="w-32 py-8 flex flex-col items-center justify-center text-white"
          :class="getCouponBgClass(getCouponType(coupon))"
        >
          <span class="text-xs font-bold opacity-80 mb-1">{{ getCouponType(coupon) }}</span>
          <Ticket :size="32" />
        </div>

        <div class="flex-1 p-6 relative">
          <div class="flex flex-col gap-1">
            <h3 class="text-lg font-black text-slate-800">{{ coupon.code }}</h3>
            <p v-if="coupon.description" class="text-xs text-slate-500">{{ coupon.description }}</p>
            <p class="text-xs text-slate-400">
              折扣: {{ coupon.discount }}折 | 最低消费: ￥{{ coupon.minimumSpend }}
            </p>
            <p class="text-[10px] text-slate-300 font-mono mt-2">有效期至: {{ coupon.expireDate }}</p>
            <p v-if="coupon.targetMemberLevel" class="text-xs text-orange-500 mt-1">
              仅限{{ coupon.targetMemberLevel }}会员
            </p>
            <p v-else-if="coupon.minConsumption && coupon.minConsumption > 0" class="text-xs text-orange-500 mt-1">
              累计消费满￥{{ coupon.minConsumption }}可领取
            </p>
          </div>

          <button
            @click="claimCoupon(coupon.id)"
            class="absolute right-6 top-1/2 -translate-y-1/2 px-4 py-2 rounded-full text-xs font-black transition-all"
            :class="getCouponType(coupon) === '通用' ? 'bg-orange-100 text-orange-600' : 'bg-indigo-100 text-indigo-600'"
          >
            立即领取
          </button>
        </div>

        <div class="absolute left-32 top-0 bottom-0 w-px border-l-2 border-dashed border-slate-100"></div>
      </div>
    </div>

    <div class="mt-6">
      <button
        @click="goToMyCoupons"
        class="w-full py-3 bg-slate-100 text-slate-600 rounded-xl font-bold hover:bg-slate-200 transition-colors"
      >
        查看我的优惠券
      </button>
    </div>

    <CustomModal
      :show="showSuccessModal"
      :title="'领取成功'"
      :message="'恭喜你获得一张优惠券，快去查看吧！'"
      :type="'success'"
      :showCancel="true"
      :confirmText="'查看我的优惠券'"
      :cancelText="'取消'"
      @confirm="goToMyCoupons"
      @cancel="showSuccessModal = false"
      @close="showSuccessModal = false"
    />

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
