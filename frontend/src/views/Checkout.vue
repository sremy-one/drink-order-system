<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useStore } from '../store';
import { Home, Map, CreditCard, CircleDollarSign, CheckCircle2, X, ArrowRight } from 'lucide-vue-next';
import CustomModal from '../components/CustomModal.vue';
import Toast from '../components/Toast.vue';

const router = useRouter();
const store = useStore();
const serviceType = ref('自取');
const deliveryFee = 5;
const paymentMethod = ref('balance');
const API_BASE_URL = 'http://localhost:3001';

// 弹窗状态
const showAlertModal = ref(false);
const alertMessage = ref('');
const alertType = ref('info');
const showSuccessModal = ref(false);
const pickupCode = ref('');
const currentOrderId = ref('');

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

const finalCheckout = async () => {
  if (store.cart.length === 0) {
    showAlert('请先添加商品', 'warning');
    return;
  }

  if (!store.user) {
    showAlert('请先登录', 'warning');
    return;
  }

  // 检查余额是否足够
  if (paymentMethod.value === 'balance') {
    const finalTotal = serviceType.value === '外卖' ? store.cartTotal + deliveryFee : store.cartTotal;
    if (store.user.balance < finalTotal) {
      showAlert('余额不足，请选择其他支付方式或充值', 'warning');
      return;
    }
  }

  try {
    const finalTotal = serviceType.value === '外卖' ? store.cartTotal + deliveryFee : store.cartTotal;
    // 获取选择的分店ID
    const selectedBranchId = localStorage.getItem('selectedBranchId');
    
    const orderData = {
      type: serviceType.value,
      items: store.cart.map(i => ({
          drinkId: i.id,
          name: i.name,
          quantity: i.count,
          price: i.price
        })),
      total: finalTotal,
      customerId: store.user.id,
      branchId: selectedBranchId,
      paymentMethod: paymentMethod.value
    };

    const token = localStorage.getItem('token');
    if (!token) {
      showAlert('请先登录', 'warning');
      return;
    }
    
    const res = await fetch(`${API_BASE_URL}/api/orders`, {
      method: 'POST',
      headers: { 
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(orderData)
    });

    if (res.ok) {
      const data = await res.json();
      pickupCode.value = data.pickupCode;
      currentOrderId.value = data.id || data.orderId;
      store.clearCart();
      await store.fetchUser(); // 先更新用户信息
      await store.fetchOrders(); // 再获取订单
      showSuccessModal.value = true;
    } else {
      const errorData = await res.json();
      showAlert(errorData.message || '订单提交失败，请稍后重试', 'error');
    }
  } catch (err) {
    console.error('Order failed:', err);
    showAlert('订单提交失败，请稍后重试', 'error');
  }
};

const goToOrderDetail = () => {
  showSuccessModal.value = false;
  if (currentOrderId.value) {
    router.push(`/order/${currentOrderId.value}`);
  } else {
    router.push('/orders');
  }
};

const goBack = () => {
  router.back();
};

onMounted(async () => {
  await store.fetchUser();
});
</script>

<template>
  <div class="px-6 py-8 space-y-6 min-h-screen bg-slate-50">
    <header class="flex items-center gap-4">
      <button @click="goBack" class="p-2 hover:bg-slate-100 rounded-full transition-colors">
        <X :size="24" />
      </button>
      <h1 class="text-2xl font-black text-slate-800">结算</h1>
    </header>

    <!-- 取餐方式 -->
    <div class="bg-white rounded-2xl p-4 shadow-sm border border-slate-100">
      <h3 class="text-sm font-bold text-slate-800 mb-3">取餐方式</h3>
      <div class="flex gap-2">
        <button
          @click="serviceType = '自取'"
          class="flex-1 py-3 rounded-xl text-sm transition-colors"
          :class="serviceType === '自取' ? 'bg-orange-500 text-white font-bold' : 'bg-slate-100 text-slate-600'"
        >
          <Home :size="16" class="inline mr-2" />
          自取
        </button>
        <button
          @click="serviceType = '外卖'"
          class="flex-1 py-3 rounded-xl text-sm transition-colors"
          :class="serviceType === '外卖' ? 'bg-orange-500 text-white font-bold' : 'bg-slate-100 text-slate-600'"
        >
          <Map :size="16" class="inline mr-2" />
          外卖 +￥5
        </button>
      </div>
    </div>

    <!-- 订单商品 -->
    <div class="bg-white rounded-2xl p-4 shadow-sm border border-slate-100">
      <h3 class="text-sm font-bold text-slate-800 mb-3">订单商品</h3>
      <div class="space-y-3">
        <div v-for="item in store.cart" :key="item.id" class="flex justify-between items-center py-2 border-b border-slate-50">
          <span class="text-sm">{{ item.name }} × {{ item.count }}</span>
          <span class="text-sm font-bold">￥{{ (item.price * item.count).toFixed(2) }}</span>
        </div>
        <div v-if="serviceType === '外卖'" class="flex justify-between text-sm border-t border-slate-100 pt-2">
          <span>配送费</span>
          <span>￥{{ deliveryFee }}</span>
        </div>
        <div class="flex justify-between font-bold pt-2 border-t border-slate-200">
          <span>总计</span>
          <span class="text-lg font-black text-orange-600">￥{{ (serviceType === '外卖' ? store.cartTotal + deliveryFee : store.cartTotal).toFixed(2) }}</span>
        </div>
      </div>
    </div>

    <!-- 支付方式 -->
    <div class="bg-white rounded-2xl p-4 shadow-sm border border-slate-100">
      <h3 class="text-sm font-bold text-slate-800 mb-3">支付方式</h3>
      <div class="space-y-3">
        <div 
          @click="paymentMethod = 'balance'"
          class="flex items-center justify-between p-3 rounded-xl transition-colors"
          :class="paymentMethod === 'balance' ? 'bg-orange-50' : 'bg-slate-50'"
        >
          <div class="flex items-center gap-3">
            <div class="bg-green-50 p-2 rounded-xl text-green-500"><CreditCard :size="20" /></div>
            <div>
              <p class="font-bold text-slate-800">账户余额</p>
              <p class="text-xs text-slate-500">可用余额：￥{{ store.user?.balance?.toFixed(2) || '0.00' }}</p>
            </div>
          </div>
          <CheckCircle2 
            :size="20" 
            :class="paymentMethod === 'balance' ? 'text-orange-500' : 'text-slate-300'"
          />
        </div>
        <div 
          @click="paymentMethod = 'wechat'"
          class="flex items-center justify-between p-3 rounded-xl transition-colors"
          :class="paymentMethod === 'wechat' ? 'bg-orange-50' : 'bg-slate-50'"
        >
          <div class="flex items-center gap-3">
            <div class="bg-green-50 p-2 rounded-xl text-green-500"><CircleDollarSign :size="20" /></div>
            <p class="font-bold text-slate-800">微信支付</p>
          </div>
          <CheckCircle2 
            :size="20" 
            :class="paymentMethod === 'wechat' ? 'text-orange-500' : 'text-slate-300'"
          />
        </div>
        <div 
          @click="paymentMethod = 'alipay'"
          class="flex items-center justify-between p-3 rounded-xl transition-colors"
          :class="paymentMethod === 'alipay' ? 'bg-orange-50' : 'bg-slate-50'"
        >
          <div class="flex items-center gap-3">
            <div class="bg-blue-50 p-2 rounded-xl text-blue-500"><CircleDollarSign :size="20" /></div>
            <p class="font-bold text-slate-800">支付宝支付</p>
          </div>
          <CheckCircle2 
            :size="20" 
            :class="paymentMethod === 'alipay' ? 'text-orange-500' : 'text-slate-300'"
          />
        </div>
      </div>
    </div>

    <!-- 祝福文本 -->
    <div class="bg-orange-50 rounded-2xl p-4 text-center">
      <p class="text-sm font-bold text-orange-600">祝您用餐愉快！😊</p>
      <p class="text-xs text-orange-500 mt-1">感谢您选择我们的饮品，期待您的再次光临！</p>
    </div>

    <!-- 提交订单按钮 -->
    <button
      @click="finalCheckout"
      class="w-full py-4 bg-orange-500 text-white rounded-xl font-bold hover:bg-orange-600 transition-colors"
    >
      提交订单
    </button>

    <!-- 确认弹窗 -->
    <CustomModal
      :show="showAlertModal"
      :title="alertType === 'error' ? '错误' : alertType === 'warning' ? '警告' : '提示'"
      :message="alertMessage"
      :type="alertType"
      @confirm="showAlertModal = false"
      @close="showAlertModal = false"
    />

    <!-- 支付成功弹窗 -->
    <CustomModal
      :show="showSuccessModal"
      :title="'支付成功'"
      :message="'订单提交成功！\n您的取餐码：' + pickupCode"
      :type="'success'"
      :showCancel="false"
      :confirmText="'查看订单'"
      @confirm="goToOrderDetail"
      @close="showSuccessModal = false"
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