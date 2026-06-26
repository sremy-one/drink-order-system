<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useStore } from '../store';
import { ChevronLeft, Wallet, CreditCard, Apple, CheckCircle2 } from 'lucide-vue-next';
import CustomModal from '../components/CustomModal.vue';
import api from '../api/axios.js';

const router = useRouter();
const store = useStore();
const rechargeAmount = ref(50);
const amounts = [10, 20, 50, 100, 200, 500];
const isLoading = ref(false);
const errorMessage = ref('');

// 弹窗状态
const showConfirmModal = ref(false);
const showSuccessModal = ref(false);
const showErrorModal = ref(false);

onMounted(() => {
  store.fetchUser();
});

const handleRecharge = () => {
  showConfirmModal.value = true;
};

const confirmRecharge = async () => {
  if (store.user) {
    isLoading.value = true;
    try {
      // 发送充值请求
      const response = await api.post('/api/user/recharge', {
        amount: rechargeAmount.value
      });
      
      // 更新本地用户信息
      store.user = response.data;
      showConfirmModal.value = false;
      showSuccessModal.value = true;
    } catch (error) {
      console.error('充值失败:', error);
      errorMessage.value = '充值失败，请稍后重试';
      showConfirmModal.value = false;
      showErrorModal.value = true;
    } finally {
      isLoading.value = false;
    }
  }
};

const closeSuccessModal = () => {
  showSuccessModal.value = false;
};

const closeErrorModal = () => {
  showErrorModal.value = false;
};
</script>

<template>
  <div class="px-6 py-8 space-y-8 min-h-screen bg-slate-50">
    <header class="flex items-center gap-4">
      <button @click="router.back()" class="p-2 hover:bg-slate-100 rounded-full transition-colors">
        <ChevronLeft :size="24" />
      </button>
      <h1 class="text-2xl font-black text-slate-800">我的余额 💰</h1>
    </header>

    <!-- Balance Card -->
    <div class="joy-gradient p-8 rounded-[3rem] text-white shadow-xl shadow-orange-100 flex flex-col items-center gap-2">
      <div class="opacity-80 text-xs font-bold uppercase tracking-widest">当前可用余额 (元)</div>
      <div class="text-5xl font-black tabular-nums">￥{{ store.user?.balance?.toFixed(2) || '0.00' }}</div>
    </div>

    <!-- Recharge Selection -->
    <div class="space-y-6">
      <h3 class="px-2 text-xs font-black text-slate-400 uppercase tracking-widest">充值金额</h3>
      <div class="grid grid-cols-3 gap-3">
        <button 
          v-for="amt in amounts" 
          :key="amt"
          @click="rechargeAmount = amt"
          class="joy-card flex flex-col items-center justify-center p-4 border-2 transition-all"
          :class="rechargeAmount === amt ? 'border-orange-500 bg-orange-50/30' : 'border-slate-50 bg-white'"
        >
          <span class="text-lg font-black tabular-nums" :class="rechargeAmount === amt ? 'text-orange-500' : 'text-slate-800'">￥{{ amt }}</span>
          <span class="text-[9px] text-slate-400 font-bold uppercase tracking-widest mt-1">充{{ amt }}得{{ amt }}</span>
        </button>
      </div>
    </div>

    <!-- Payment Methods -->
    <div class="space-y-4">
      <h3 class="px-2 text-xs font-black text-slate-400 uppercase tracking-widest">支付方式</h3>
      <div class="joy-card divide-y divide-slate-50 !p-0 overflow-hidden">
        <div class="p-4 flex items-center justify-between">
          <div class="flex items-center gap-4">
            <div class="bg-blue-50 p-2 rounded-xl text-blue-500"><CreditCard :size="20" /></div>
            <span class="font-bold text-slate-700">微信支付</span>
          </div>
          <CheckCircle2 :size="20" class="text-orange-500" />
        </div>
        <div class="p-4 flex items-center justify-between opacity-50">
          <div class="flex items-center gap-4">
            <div class="bg-slate-100 p-2 rounded-xl text-slate-800"><Apple :size="20" /></div>
            <span class="font-bold text-slate-700">Apple Pay</span>
          </div>
          <div class="w-5 h-5 rounded-full border-2 border-slate-200"></div>
        </div>
      </div>
    </div>

    <!-- Recharge Button -->
    <button @click="handleRecharge" class="w-full joy-btn-primary py-5 rounded-[2rem] text-lg" :disabled="isLoading">
      {{ isLoading ? '充值中...' : '立即开启元气之旅' }}
    </button>

    <p class="text-center text-[10px] text-slate-300 px-8 uppercase tracking-widest font-bold leading-relaxed">
      充值即代表同意《BubbleJoy 充值服务协议》及《隐私政策》
    </p>

    <!-- 确认充值弹窗 -->
    <CustomModal
      :show="showConfirmModal"
      :title="'确认充值'"
      :message="`确认充值 ￥${rechargeAmount} 吗？`"
      :type="'info'"
      :showCancel="true"
      :confirmText="'确认'"
      :cancelText="'取消'"
      @confirm="confirmRecharge"
      @cancel="showConfirmModal = false"
    />

    <!-- 充值成功弹窗 -->
    <CustomModal
      :show="showSuccessModal"
      :title="'充值成功'"
      :message="'充值成功！您的账户已更新。'"
      :type="'success'"
      :showCancel="false"
      :confirmText="'确定'"
      @confirm="closeSuccessModal"
      @close="showSuccessModal = false"
    />

    <!-- 充值失败弹窗 -->
    <CustomModal
      :show="showErrorModal"
      :title="'充值失败'"
      :message="errorMessage"
      :type="'error'"
      :showCancel="false"
      :confirmText="'确定'"
      @confirm="closeErrorModal"
      @close="showErrorModal = false"
    />
  </div>
</template>
