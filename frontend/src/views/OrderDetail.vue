<script setup>
import { ref, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useStore } from "../store";
import {
  ChevronLeft,
  Clock,
  MapPin,
  CreditCard,
  CheckCircle2,
  XCircle,
  AlertCircle,
  ArrowRight,
  Check,
  Trash2,
  Headphones,
} from "lucide-vue-next";
import api from "../api/axios.js";

const router = useRouter();
const route = useRoute();
const store = useStore();
const API_BASE_URL = "http://localhost:3001";
const order = ref(null);
const isLoading = ref(true);
const showConfirmModal = ref(false);
const confirmMessage = ref("");
const confirmAction = ref(null);
const showToast = ref(false);
const toastMessage = ref("");

onMounted(async () => {
  try {
    // 获取订单ID
    const orderId = route.params.id;
    if (!orderId) {
      router.push("/orders");
      return;
    }

    // 直接从API获取订单数据
    const res = await api.get(`/api/orders/${orderId}`);
    if (res.data) {
      order.value = res.data;
    } else {
      console.error("订单不存在");
    }
  } catch (error) {
    console.error("获取订单详情失败:", error);
    // 如果API调用失败，尝试从store中获取
    try {
      if (store.orders.length === 0) {
        await store.fetchOrders();
      }
      const orderId = route.params.id;
      order.value = store.orders.find((o) => {
        const idValue = o.id || o.orderId;
        return String(idValue) === String(orderId);
      });
    } catch (e) {
      console.error("从store获取订单也失败:", e);
    }
  } finally {
    isLoading.value = false;
  }
});

const goBack = () => {
  router.push("/orders");
};

const showAlert = (message, action) => {
  confirmMessage.value = message;
  confirmAction.value = action;
  showConfirmModal.value = true;
};

const showToastMessage = (message) => {
  toastMessage.value = message;
  showToast.value = true;
  setTimeout(() => {
    showToast.value = false;
  }, 3000);
};

const handleConfirm = () => {
  if (confirmAction.value) {
    confirmAction.value();
  }
  showConfirmModal.value = false;
};

const updateOrderStatus = async (status) => {
  try {
    const orderId = order.value.id || order.value.orderId;
    const res = await fetch(
      `${API_BASE_URL}/api/merchant/orders/${orderId}/status`,
      {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
        body: JSON.stringify({ status }),
      }
    );
    if (res.ok) {
      // 更新本地订单状态
      order.value.status = status;
      showToastMessage("订单状态已更新");
    } else {
      showToastMessage("订单状态更新失败");
    }
  } catch (error) {
    console.error("更新订单状态失败:", error);
    showToastMessage("订单状态更新失败");
  }
};

const cancelOrder = async () => {
  try {
    const orderId = order.value.id || order.value.orderId;
    const res = await fetch(
      `${API_BASE_URL}/api/merchant/orders/${orderId}/cancel`,
      {
        method: "PATCH",
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      }
    );
    if (res.ok) {
      // 更新本地订单状态
      order.value.status = "已取消";
      showToastMessage("订单已取消");
    } else {
      showToastMessage("订单取消失败");
    }
  } catch (error) {
    console.error("取消订单失败:", error);
    showToastMessage("订单取消失败");
  }
};

// 根据订单状态获取对应的文本和图标
const getStatusInfo = (status) => {
  switch (status) {
    case "制作中":
      return { text: "制作中", icon: Clock, color: "text-yellow-500" };
    case "已出餐":
      return { text: "已出餐", icon: Check, color: "text-blue-500" };
    case "已完成":
      return { text: "已完成", icon: CheckCircle2, color: "text-green-500" };
    case "已取消":
      return { text: "已取消", icon: XCircle, color: "text-red-500" };
    default:
      return { text: status, icon: AlertCircle, color: "text-gray-500" };
  }
};
</script>

<template>
  <div class="px-6 py-8 min-h-screen bg-slate-50">
    <!-- 头部 -->
    <header class="flex items-center gap-4 mb-8">
      <button
        @click="goBack"
        class="p-2 hover:bg-slate-100 rounded-full transition-colors"
      >
        <ChevronLeft :size="24" />
      </button>
      <h1 class="text-2xl font-black text-slate-800">订单详情</h1>
    </header>

    <!-- 加载状态 -->
    <div v-if="isLoading" class="flex items-center justify-center h-64">
      <div
        class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-orange-500"
      ></div>
    </div>

    <!-- 订单不存在 -->
    <div
      v-else-if="!order"
      class="flex flex-col items-center justify-center h-64 space-y-4"
    >
      <AlertCircle :size="48" class="text-gray-400" />
      <p class="text-slate-600 font-medium">订单不存在</p>
      <button
        @click="goBack"
        class="px-6 py-2 bg-orange-500 text-white rounded-lg font-bold"
      >
        返回订单列表
      </button>
    </div>

    <!-- 订单详情 -->
    <div v-else class="space-y-6">
      <!-- 订单状态 -->
      <div class="joy-card p-6">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-3">
            <component
              :is="getStatusInfo(order.status).icon"
              :class="getStatusInfo(order.status).color"
              :size="24"
            />
            <span class="text-lg font-bold">{{
              getStatusInfo(order.status).text
            }}</span>
          </div>
          <span class="text-sm text-slate-500">{{ order.time }}</span>
        </div>
        <div class="mt-4 text-sm text-slate-600">
          <p>订单号: {{ order.orderId }}</p>
          <p v-if="order.pickupCode">取餐码: {{ order.pickupCode }}</p>
        </div>
      </div>

      <!-- 订单商品 -->
      <div class="joy-card p-6">
        <h3 class="text-lg font-bold mb-4">商品信息</h3>
        <div
          v-for="(item, index) in order.items"
          :key="index"
          class="flex items-center justify-between py-3 border-b border-slate-100"
        >
          <div class="flex items-center gap-3">
            <img
              :src="API_BASE_URL + (item.image || '/default-image.png')"
              class="w-16 h-16 object-cover rounded-lg"
            />
            <div>
              <p class="font-medium">{{ item.name }}</p>
              <p class="text-sm text-slate-500">
                x{{ item.quantity || item.count }}
              </p>
            </div>
          </div>
          <span class="font-bold">￥{{ (item.price || 0).toFixed(2) }}</span>
        </div>
        <div class="mt-4 flex items-center justify-between font-bold">
          <span>总计</span>
          <span>￥{{ order.total.toFixed(2) }}</span>
        </div>
      </div>

      <!-- 订单信息 -->
      <div class="joy-card p-6">
        <h3 class="text-lg font-bold mb-4">订单信息</h3>
        <div class="space-y-3 text-sm">
          <div class="flex justify-between">
            <span class="text-slate-600">订单类型</span>
            <span class="font-medium">{{
              order.type === "自取" ? "到店自取" : "外卖配送"
            }}</span>
          </div>
          <div class="flex justify-between">
            <span class="text-slate-600">支付方式</span>
            <span class="font-medium">微信支付</span>
          </div>
          <div class="flex justify-between">
            <span class="text-slate-600">下单时间</span>
            <span class="font-medium">{{ order.time }}</span>
          </div>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="flex gap-3 mt-8">
        <!-- 商家端操作按钮 -->
        <div v-if="store.user?.role === 'merchant'" class="flex gap-3 w-full">
          <button
            v-if="order.status === '制作中'"
            @click="updateOrderStatus('已出餐')"
            class="flex-1 py-4 bg-blue-50 text-blue-600 rounded-2xl font-bold hover:bg-blue-100 transition-colors flex items-center justify-center gap-2"
          >
            <Check :size="18" />
            已出餐
          </button>
          <button
            v-if="order.status === '制作中' || order.status === '已出餐'"
            @click="showAlert('确定要取消这个订单吗？', cancelOrder)"
            class="flex-1 py-4 bg-red-50 text-red-600 rounded-2xl font-bold hover:bg-red-100 transition-colors flex items-center justify-center gap-2"
          >
            <Trash2 :size="18" />
            取消订单
          </button>
        </div>

        <!-- 客户端操作按钮 -->
        <div v-else class="flex gap-3 w-full">
          <button
            @click="router.push('/')"
            class="flex-1 py-4 border-2 border-slate-200 rounded-2xl font-bold text-slate-700 transition-colors hover:border-slate-300 flex items-center justify-center gap-2"
          >
            <ArrowRight :size="18" />
            再次购买
          </button>
          <button
            @click="router.push('/support')"
            class="flex-1 py-4 joy-gradient rounded-2xl font-bold text-white transition-colors hover:opacity-90 flex items-center justify-center gap-2"
          >
            <Headphones :size="18" />
            联系客服
          </button>
        </div>
      </div>

      <!-- 确认弹窗 -->
      <div
        v-if="showConfirmModal"
        class="fixed inset-0 bg-black/50 flex items-center justify-center z-50"
      >
        <div class="bg-white rounded-2xl p-6 max-w-md w-full mx-4">
          <h3 class="text-lg font-bold mb-4">确认操作</h3>
          <p class="text-slate-600 mb-6">{{ confirmMessage }}</p>
          <div class="flex gap-3">
            <button
              @click="showConfirmModal = false"
              class="flex-1 py-2 border border-slate-200 rounded-xl font-bold text-slate-700"
            >
              取消
            </button>
            <button
              @click="handleConfirm"
              class="flex-1 py-2 bg-red-500 text-white rounded-xl font-bold"
            >
              确认
            </button>
          </div>
        </div>
      </div>

      <!-- Toast 提示 -->
      <div
        v-if="showToast"
        class="fixed top-4 right-4 bg-green-500 text-white px-4 py-2 rounded-lg shadow-lg z-50"
      >
        {{ toastMessage }}
      </div>
    </div>
  </div>
</template>