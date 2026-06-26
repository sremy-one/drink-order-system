<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useStore } from "../store";
import { X, Check, Clock, Trash2, Star } from "lucide-vue-next";
import CustomModal from "../components/CustomModal.vue";
import Toast from "../components/Toast.vue";

const router = useRouter();
const store = useStore();
const API_BASE_URL = "http://localhost:3001";

// 订单类型切换
const orderType = ref("recent"); // 'recent' 或 'history'

// 弹窗状态
const showConfirmModal = ref(false);
const confirmMessage = ref("");
const confirmAction = ref(null);
const showAlertModal = ref(false);
const alertMessage = ref("");
const alertType = ref("info");

// Toast状态
const showToast = ref(false);
const toastMessage = ref("");
const toastType = ref("info");

// 计算属性：最近订单（非已完成和非已取消的订单）
const recentOrders = computed(() => {
  return store.orders.filter(
    (order) => order.status !== "已完成" && order.status !== "已取消"
  );
});

// 计算属性：历史订单（所有订单）
const historyOrders = computed(() => {
  return store.orders;
});

// 当前显示的订单
const displayOrders = computed(() => {
  return orderType.value === "recent"
    ? recentOrders.value
    : historyOrders.value;
});

const showAlert = (message, type = "info") => {
  alertMessage.value = message;
  alertType.value = type;
  showAlertModal.value = true;
};

const showToastMessage = (message, type = "success") => {
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

const updateOrderStatus = async (orderId, status) => {
  try {
    const res = await fetch(
      `${API_BASE_URL}/api/merchants/orders/${orderId}/status`,
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
      await store.fetchOrders();
      showToastMessage("订单状态已更新");
    } else {
      showAlert("订单状态更新失败", "error");
    }
  } catch (error) {
    console.error("更新订单状态失败:", error);
    showAlert("订单状态更新失败", "error");
  }
};

const cancelOrder = (orderId) => {
  showConfirm("确定要取消这个订单吗？", async () => {
    try {
      const res = await fetch(
        `${API_BASE_URL}/api/merchants/orders/${orderId}/cancel`,
        {
          method: "PATCH",
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      );
      if (res.ok) {
        await store.fetchOrders();
        showToastMessage("订单已取消");
      } else {
        showAlert("订单取消失败", "error");
      }
    } catch (error) {
      console.error("取消订单失败:", error);
      showAlert("订单取消失败", "error");
    }
  });
};

const submitReview = async (orderId, rating, comment) => {
  try {
    const res = await fetch(`${API_BASE_URL}/api/orders/${orderId}/review`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
      body: JSON.stringify({ rating, comment }),
    });
    if (res.ok) {
      await store.fetchOrders();
      showToastMessage(`感谢您的评价！订单号：${orderId}`);
    }
  } catch (error) {
    console.error("提交评价失败:", error);
  }
};

const goToOrderDetail = (order) => {
  const orderId = order.id || order.orderId;
  if (orderId) {
    router.push(`/order/${orderId}`);
  }
};

onMounted(async () => {
  await store.fetchUser();
  await store.fetchOrders();
});
</script>

<template>
  <div class="px-6 py-8 space-y-6 min-h-screen bg-slate-50">
    <header class="flex items-center justify-between gap-4">
      <h1 class="text-2xl font-black text-slate-800">我的订单</h1>
      <div class="flex gap-2">
        <button
          @click="orderType = 'recent'"
          :class="
            orderType === 'recent'
              ? 'bg-orange-500 text-white'
              : 'bg-slate-100 text-slate-600'
          "
          class="px-4 py-2 rounded-full text-sm font-bold transition-colors"
        >
          最近订单
        </button>
        <button
          @click="orderType = 'history'"
          :class="
            orderType === 'history'
              ? 'bg-orange-500 text-white'
              : 'bg-slate-100 text-slate-600'
          "
          class="px-4 py-2 rounded-full text-sm font-bold transition-colors"
        >
          历史订单
        </button>
      </div>
    </header>

    <div class="space-y-4">
      <div
        v-for="order in displayOrders"
        :key="order.id"
        class="bg-white rounded-2xl p-4 shadow-sm border border-slate-100 cursor-pointer hover:shadow-md transition-shadow"
        @click="goToOrderDetail(order)"
      >
        <div class="flex justify-between items-start">
          <div>
            <div class="flex items-center gap-2">
              <h3 class="font-bold text-slate-800">{{ order.orderId }}</h3>
              <span
                :class="[
                  'px-2 py-0.5 rounded-full text-xs font-bold',
                  order.status === '制作中'
                    ? 'bg-yellow-100 text-yellow-600'
                    : order.status === '已出餐'
                    ? 'bg-blue-100 text-blue-600'
                    : order.status === '已完成'
                    ? 'bg-green-100 text-green-600'
                    : 'bg-red-100 text-red-600',
                ]"
              >
                {{ order.status }}
              </span>
            </div>
            <p class="text-xs text-slate-500 mt-1">{{ order.time }}</p>
          </div>
          <div v-if="order.pickupCode" class="text-right">
            <p class="text-xs text-slate-500">取餐码</p>
            <p class="text-lg font-black text-orange-600">
              {{ order.pickupCode }}
            </p>
          </div>
        </div>

        <div class="mt-4 space-y-2">
          <div
            v-for="item in order.items"
            :key="item.id"
            class="flex justify-between items-center py-2 border-b border-slate-50"
          >
            <div class="flex items-center gap-3">
              <img
                :src="API_BASE_URL + (item.image || '/default-image.png')"
                class="w-12 h-12 object-cover rounded-lg"
              />
              <div>
                <span class="text-sm"
                  >{{ item.name }} × {{ item.quantity }}</span
                >
              </div>
            </div>
            <span class="text-sm font-bold">￥{{ item.price }}</span>
          </div>
          <div class="flex justify-between items-center pt-2">
            <span class="font-bold">总计</span>
            <span class="text-lg font-black text-orange-600"
              >￥{{ order.total }}</span
            >
          </div>
        </div>

        <div class="mt-4 flex gap-2">
          <button
            v-if="store.user?.role === 'merchant' && order.status === '制作中'"
            @click.stop="updateOrderStatus(order.id, '已出餐')"
            class="flex-1 py-2 bg-blue-50 text-blue-600 rounded-xl text-sm font-bold hover:bg-blue-100 transition-colors"
          >
            <Check :size="14" class="inline mr-1" />
            已出餐
          </button>
          <button
            v-if="store.user?.role === 'customer' && order.status === '已出餐'"
            @click.stop="updateOrderStatus(order.id, '已取餐')"
            class="flex-1 py-2 bg-green-50 text-green-600 rounded-xl text-sm font-bold hover:bg-green-100 transition-colors"
          >
            <Check :size="14" class="inline mr-1" />
            确认取餐
          </button>
          <button
            v-if="
              store.user?.role === 'merchant' &&
              (order.status === '制作中' || order.status === '已出餐')
            "
            @click.stop="cancelOrder(order.id)"
            class="flex-1 py-2 bg-red-50 text-red-600 rounded-xl text-sm font-bold hover:bg-red-100 transition-colors"
          >
            <Trash2 :size="14" class="inline mr-1" />
            取消订单
          </button>
        </div>
      </div>
    </div>

    <!-- 确认弹窗 -->
    <CustomModal
      :show="showConfirmModal"
      :title="'确认操作'"
      :message="confirmMessage"
      :type="'warning'"
      :showCancel="true"
      :confirmText="'确认'"
      :cancelText="'取消'"
      @confirm="handleConfirm"
      @cancel="showConfirmModal = false"
    />

    <!-- 提示弹窗 -->
    <CustomModal
      :show="showAlertModal"
      :title="
        alertType === 'error'
          ? '错误'
          : alertType === 'warning'
          ? '警告'
          : '提示'
      "
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