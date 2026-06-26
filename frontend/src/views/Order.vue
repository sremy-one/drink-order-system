<script setup>
import { ref, onMounted, computed, nextTick } from 'vue';
import { useRouter } from 'vue-router';
import { Plus, Minus, Map, Home, ShoppingBag, ArrowRight, ArrowLeft, Edit, X, DollarSign } from 'lucide-vue-next';
import { useStore } from '../store';
import CustomModal from '../components/CustomModal.vue';
import Toast from '../components/Toast.vue';

const router = useRouter();
const store = useStore();
const serviceType = ref('自取');
const deliveryFee = 5;
const showCart = ref(false);
const editingDrink = ref(null);
const showEditModal = ref(false);
const editFormData = ref({ name: '', description: '', price: '', stock: '', available: true });
const API_BASE_URL = 'http://localhost:3001';

// 获取选择的分店
const selectedBranchId = ref(localStorage.getItem('selectedBranchId'));
const selectedBranchName = ref(localStorage.getItem('selectedBranchName'));

// 弹窗状态
const showAlertModal = ref(false);
const alertMessage = ref('');
const alertType = ref('info');

// Toast状态
const showToast = ref(false);
const toastMessage = ref('');
const toastType = ref('info');

const categories = [
  '奶茶', '果茶', '咖啡', '沙冰', '其他'
];

const activeCategory = ref('奶茶');

const categoryDrinks = computed(() => {
  return store.drinks.filter(drink => drink.category === activeCategory.value);
});

const toggleCart = () => {
  showCart.value = !showCart.value;
};

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

const proceedToCheckout = () => {
  if (store.cart.length === 0) {
    showAlert('请先添加商品', 'warning');
    return;
  }
  showCart.value = false;
  router.push('/checkout');
};

const openEditModal = (drink) => {
  editingDrink.value = drink;
  editFormData.value = {
    name: drink.name,
    description: drink.description,
    price: drink.price,
    stock: drink.stock,
    available: drink.available
  };
  showEditModal.value = true;
};

const saveDrinkChanges = async () => {
  try {
    const res = await fetch(`${API_BASE_URL}/api/merchant/products/${editingDrink.value.id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: JSON.stringify(editFormData.value)
    });
    if (res.ok) {
      await store.fetchDrinks();
      showEditModal.value = false;
      showToastMessage('商品更新成功');
    }
  } catch (error) {
    console.error('保存商品失败:', error);
    showAlert('保存商品失败，请稍后重试', 'error');
  }
};

const toggleDrinkAvailability = async (drink) => {
  try {
    const res = await fetch(`${API_BASE_URL}/api/merchant/products/${drink.id}/toggle`, {
      method: 'PATCH',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    });
    if (res.ok) {
      await store.fetchDrinks();
      showToastMessage(`商品已${drink.available ? '下架' : '上架'}`);
    }
  } catch (error) {
    console.error('切换商品状态失败:', error);
    showAlert('操作失败，请稍后重试', 'error');
  }
};

onMounted(async () => {
  // 检查是否选择了分店
  if (!selectedBranchId.value) {
    router.push('/merchant-select');
    return;
  }
  
  // 所有分店共用同一个商品列表，不需要传递分店ID
  await store.fetchDrinks();
  await store.fetchUser();
});
</script>

<template>
  <div class="px-6 py-8 space-y-6 min-h-screen bg-slate-50 pb-32">
    <header class="flex items-center justify-between">
      <div class="flex items-center gap-4">
        <button @click="router.push('/merchant-select')" class="p-2 hover:bg-slate-100 rounded-full transition-colors">
          <ArrowLeft :size="24" class="text-slate-600" />
        </button>
        <div>
          <h1 class="text-2xl font-black text-slate-800">饮品菜单</h1>
          <p v-if="selectedBranchName" class="text-sm text-slate-500">分店：{{ selectedBranchName }}</p>
        </div>
      </div>
      <button
        @click="toggleCart"
        class="relative p-2 hover:bg-slate-100 rounded-full transition-colors"
      >
        <ShoppingBag :size="24" class="text-slate-600" />
        <span
          v-if="store.cartCount > 0"
          class="absolute -top-1 -right-1 w-5 h-5 bg-orange-500 text-white rounded-full text-[10px] font-black flex items-center justify-center"
        >
          {{ store.cartCount }}
        </span>
      </button>
    </header>

    <div class="flex overflow-x-auto gap-2 pb-4">
      <button
        v-for="category in categories"
        :key="category"
        @click="activeCategory = category"
        class="px-4 py-2 rounded-full whitespace-nowrap transition-colors"
        :class="activeCategory === category ? 'bg-orange-500 text-white font-bold' : 'bg-slate-100 text-slate-600 hover:bg-slate-200'"
      >
        {{ category }}
      </button>
    </div>

    <div class="space-y-4">
      <div
        v-for="drink in categoryDrinks"
        :key="drink.id"
        class="flex gap-4 items-center animate-in fade-in slide-in-from-bottom-4 duration-500 relative"
        :class="{ 'opacity-50': !drink.available }"
      >
        <img :src="API_BASE_URL + (drink.image || '/default-image.png')" class="w-16 h-16 object-cover rounded-2xl" />
        <div class="flex-1 min-w-0">
          <div class="flex justify-between items-start">
            <h3 class="font-bold text-slate-800">{{ drink.name }}</h3>
            <span class="text-lg font-black text-orange-600 ml-4">￥{{ drink.price }}</span>
          </div>
          <p class="text-xs text-slate-500 mt-1 line-clamp-1">{{ drink.description }}</p>
          <div class="flex items-center gap-2 mt-2 text-xs">
            <span class="text-slate-500">库存: {{ drink.stock }}</span>
            <span v-if="!drink.available" class="ml-2 text-xs text-red-500 font-bold">已下架</span>
          </div>
        </div>
        <button
          v-if="!drink.available"
          disabled
          class="w-8 h-8 rounded-full bg-slate-300 flex items-center justify-center text-white cursor-not-allowed"
        >
          <Plus :size="16" />
        </button>
        <button
          v-else
          @click="store.addToCart(drink); showCart = true"
          class="w-8 h-8 rounded-full bg-orange-500 flex items-center justify-center text-white active:scale-90 transition-transform shadow-md"
        >
          <Plus :size="16" />
        </button>
        <div v-if="store.user?.role === 'merchant'" class="flex gap-2 ml-2">
          <button
            @click="openEditModal(drink)"
            class="p-1 bg-blue-50 text-blue-600 rounded hover:bg-blue-100"
          >
            <Edit :size="14" />
          </button>
          <button
            @click="toggleDrinkAvailability(drink)"
            :class="drink.available ? 'bg-green-50 text-green-600' : 'bg-red-50 text-red-600'"
            class="p-1 rounded hover:bg-slate-100"
          >
            <X :size="14" />
          </button>
        </div>
      </div>
    </div>

    <div v-if="showCart" class="fixed bottom-20 left-0 right-0 bg-white p-4 border-t border-slate-200 z-40">
      <div class="flex justify-between items-center mb-4">
        <h3 class="font-bold text-slate-800">购物车 ({{ store.cartCount }})</h3>
        <button @click="toggleCart" class="text-slate-400 hover:text-slate-600">
          <X :size="20" />
        </button>
      </div>
      <div class="space-y-2 max-h-60 overflow-y-auto mb-4">
        <div v-for="item in store.cart" :key="item.id" class="flex items-center justify-between py-2 border-b border-slate-50">
          <div class="flex items-center gap-3">
            <img :src="API_BASE_URL + (item.image || '/default-image.png')" class="w-12 h-12 object-cover rounded-xl" />
            <div>
              <h4 class="font-bold text-sm">{{ item.name }}</h4>
              <p class="text-xs text-orange-600">￥{{ item.price }}</p>
            </div>
          </div>
          <div class="flex items-center gap-2">
            <button
              @click="store.removeFromCart(item.id)"
              class="w-6 h-6 rounded-full border-2 border-slate-100 flex items-center justify-center text-slate-400 active:bg-slate-100"
            >
              <Minus :size="12" />
            </button>
            <span class="text-sm font-bold">{{ item.count }}</span>
            <button
              @click="store.addToCart(item)"
              class="w-6 h-6 rounded-full border-2 border-slate-100 flex items-center justify-center text-slate-400 active:bg-slate-100"
            >
              <Plus :size="12" />
            </button>
          </div>
        </div>
      </div>
      <div class="flex justify-between items-center py-2 border-t border-slate-50">
        <span class="font-bold">合计:</span>
        <span class="text-lg font-black text-orange-600">￥{{ store.cartTotal.toFixed(2) }}</span>
      </div>
      <button
        @click="proceedToCheckout"
        class="w-full mt-4 py-3 bg-orange-500 text-white rounded-xl font-bold hover:bg-orange-600 transition-colors"
      >
        去结算
      </button>
    </div>



    <div
      v-if="showEditModal"
      class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4"
      @click.self="showEditModal = false"
    >
      <div class="bg-white w-full max-w-lg rounded-2xl p-6 space-y-4 max-h-[90vh] overflow-y-auto">
        <div class="flex items-center justify-between">
          <h2 class="text-xl font-bold text-slate-800">编辑商品</h2>
          <button @click="showEditModal = false" class="p-2 hover:bg-slate-100 rounded-xl">
            <X :size="20" class="text-slate-500" />
          </button>
        </div>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1">商品名称</label>
            <input
              v-model="editFormData.name"
              type="text"
              class="w-full px-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:border-blue-500"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1">商品描述</label>
            <textarea
              v-model="editFormData.description"
              rows="3"
              class="w-full px-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:border-blue-500 resize-none"
            ></textarea>
          </div>
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-slate-700 mb-1">价格</label>
              <div class="relative">
                <DollarSign :size="18" class="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
                <input
                  v-model="editFormData.price"
                  type="number"
                  step="0.01"
                  class="w-full pl-10 pr-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:border-blue-500"
                />
              </div>
            </div>
            <div>
              <label class="block text-sm font-medium text-slate-700 mb-1">库存</label>
              <input
                v-model="editFormData.stock"
                type="number"
                class="w-full px-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:border-blue-500"
              />
            </div>
          </div>
          <div class="flex items-center gap-2">
            <input
              v-model="editFormData.available"
              type="checkbox"
              id="available"
              class="w-4 h-4 text-blue-600 border-slate-300 rounded focus:ring-blue-500"
            />
            <label for="available" class="text-sm font-medium text-slate-700">商品上架</label>
          </div>
        </div>
        <button
          @click="saveDrinkChanges"
          class="w-full py-3 bg-blue-600 text-white rounded-xl font-bold hover:bg-blue-700 transition-colors"
        >
          保存修改
        </button>
      </div>
    </div>

    <!-- 自定义弹窗 -->
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