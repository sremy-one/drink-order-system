<script setup>
import { ref, onMounted, computed } from 'vue';
import { useStore } from '../store';
import { Gift, Plus, Edit, Trash2, Search, ArrowLeft, X, Package, Upload } from 'lucide-vue-next';
import api from '../api/axios.js';
import CustomModal from '../components/CustomModal.vue';
import Toast from '../components/Toast.vue';

const store = useStore();
const API_BASE_URL = 'http://localhost:3001';

const isLoading = ref(true);
const products = ref([]);
const searchQuery = ref('');
const showEditModal = ref(false);
const editingProduct = ref(null);
const formData = ref({
  name: '',
  description: '',
  pointsRequired: '',
  stock: '',
  image: null
});
const formErrors = ref({});

const showConfirmModal = ref(false);
const confirmMessage = ref('');
const showAlertModal = ref(false);
const alertMessage = ref('');
const alertType = ref('info');
const showToast = ref(false);
const toastMessage = ref('');
const toastType = ref('success');

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

const validateForm = () => {
  formErrors.value = {};
  let isValid = true;

  if (!formData.value.name || formData.value.name.trim() === '') {
    formErrors.value.name = '请输入商品名称';
    isValid = false;
  }

  if (!formData.value.description || formData.value.description.trim() === '') {
    formErrors.value.description = '请输入商品描述';
    isValid = false;
  }

  if (!formData.value.pointsRequired || formData.value.pointsRequired === '') {
    formErrors.value.pointsRequired = '请输入所需积分';
    isValid = false;
  } else if (parseInt(formData.value.pointsRequired) <= 0) {
    formErrors.value.pointsRequired = '所需积分必须是正整数';
    isValid = false;
  }

  if (!formData.value.stock || formData.value.stock === '') {
    formErrors.value.stock = '请输入库存数量';
    isValid = false;
  } else if (parseInt(formData.value.stock) < 0) {
    formErrors.value.stock = '库存数量不能为负数';
    isValid = false;
  }

  if (!formData.value.image) {
    formErrors.value.image = '请上传图片';
    isValid = false;
  }

  return isValid;
};

const fetchProducts = async () => {
  isLoading.value = true;
  try {
    await store.fetchPointsProducts();
    products.value = store.pointsProducts || [];
  } catch (error) {
    console.error('获取积分商品列表失败:', error);
  } finally {
    isLoading.value = false;
  }
};

const openAddModal = () => {
  editingProduct.value = null;
  formErrors.value = {};
  formData.value = { name: '', description: '', pointsRequired: '', stock: '', image: null };
  showEditModal.value = true;
};

const openEditModal = (product) => {
  editingProduct.value = product;
  formErrors.value = {};
  formData.value = {
    name: product.name,
    description: product.description,
    pointsRequired: product.pointsRequired,
    stock: product.stock,
    image: null
  };
  showEditModal.value = true;
};

const closeModal = () => {
  showEditModal.value = false;
  editingProduct.value = null;
  formErrors.value = {};
  formData.value = { name: '', description: '', pointsRequired: '', stock: '', image: null };
};

const handleConfirmSave = () => {
  showConfirmModal.value = false;
  saveProduct();
};

const saveProduct = async () => {
  try {
    const formDataObj = new FormData();
    formDataObj.append('name', formData.value.name);
    formDataObj.append('description', formData.value.description);
    formDataObj.append('pointsRequired', String(formData.value.pointsRequired));
    formDataObj.append('stock', String(formData.value.stock));
    if (formData.value.image) {
      formDataObj.append('image', formData.value.image);
    }

    const url = editingProduct.value 
      ? `${API_BASE_URL}/api/merchant/points-products/${editingProduct.value.id}`
      : `${API_BASE_URL}/api/merchant/points-products`;
    const method = editingProduct.value ? 'PUT' : 'POST';

    const res = await fetch(url, {
      method,
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: formDataObj
    });

    if (res.ok) {
      await fetchProducts();
      closeModal();
      showToastMessage(editingProduct.value ? '商品更新成功！' : '商品添加成功！');
    } else {
      showAlert('保存失败，请稍后重试', 'error');
    }
  } catch (error) {
    console.error('保存积分商品失败:', error);
    showAlert('保存失败，请稍后重试', 'error');
  }
};

const deleteProduct = async (productId) => {
  try {
    await api.delete(`/api/merchant/points-products/${productId}`);
    await fetchProducts();
    showToastMessage('商品删除成功！');
  } catch (error) {
    console.error('删除积分商品失败:', error);
    showAlert('删除失败，请稍后重试', 'error');
  }
};

const filteredProducts = computed(() => {
  if (!searchQuery.value) return products.value;
  return products.value.filter(p => 
    p.name.toLowerCase().includes(searchQuery.value.toLowerCase())
  );
});

onMounted(() => {
  fetchProducts();
});
</script>

<template>
  <div class="px-6 py-8 space-y-6 pb-32">
    <header class="flex items-center justify-between">
      <div class="flex items-center gap-4">
        <button @click="$router.back()" class="p-2 hover:bg-slate-100 rounded-xl transition-colors">
          <ArrowLeft :size="24" class="text-slate-600" />
        </button>
        <h1 class="text-2xl font-black text-slate-800">积分商城</h1>
      </div>
      <button
        @click="openAddModal"
        class="flex items-center gap-2 px-4 py-2 bg-emerald-600 text-white rounded-xl font-bold hover:bg-emerald-700 transition-colors"
      >
        <Plus :size="20" />
        添加商品
      </button>
    </header>

    <div class="relative">
      <Search :size="20" class="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400" />
      <input
        v-model="searchQuery"
        type="text"
        placeholder="搜索积分商品..."
        class="w-full pl-12 pr-4 py-3 bg-white border border-slate-200 rounded-xl focus:outline-none focus:border-emerald-500"
      />
    </div>

    <div v-if="isLoading" class="text-center py-12">
      <div class="inline-block animate-spin rounded-full h-8 w-8 border-4 border-slate-200 border-t-slate-600"></div>
    </div>

    <div v-else-if="filteredProducts.length === 0" class="text-center py-12">
      <Gift :size="64" class="mx-auto text-slate-300 mb-4" />
      <p class="text-slate-400">{{ searchQuery ? '未找到匹配的商品' : '暂无积分商品' }}</p>
    </div>

    <div v-else class="grid grid-cols-2 gap-4">
      <div
        v-for="product in filteredProducts"
        :key="product.id"
        class="bg-white rounded-2xl shadow-sm border border-slate-100 overflow-hidden flex flex-col"
      >
        <div class="w-full aspect-square bg-slate-100 flex items-center justify-center overflow-hidden">
          <img :src="API_BASE_URL + product.image" class="max-w-full max-h-full object-contain" />
        </div>
        <div class="p-4 flex-1 flex flex-col justify-between">
          <div>
            <h3 class="font-bold text-slate-800 mb-1 truncate">{{ product.name }}</h3>
            <p class="text-xs text-slate-500 mb-2 line-clamp-2">{{ product.description }}</p>
          </div>
          <div>
            <div class="flex items-center justify-between mb-2">
              <span class="text-lg font-black text-emerald-600">{{ product.pointsRequired }}积分</span>
              <div class="flex items-center gap-1 text-xs text-slate-500">
                <Package :size="14" />
                <span>剩余: {{ product.stock }}</span>
              </div>
            </div>
            <div class="flex gap-2">
              <button
                @click="openEditModal(product)"
                class="flex-1 p-2 bg-emerald-50 text-emerald-600 rounded-lg hover:bg-emerald-100 transition-colors"
              >
                <Edit :size="16" class="mx-auto" />
              </button>
              <button
                @click="deleteProduct(product.id)"
                class="flex-1 p-2 bg-red-50 text-red-600 rounded-lg hover:bg-red-100 transition-colors"
              >
                <Trash2 :size="16" class="mx-auto" />
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div
      v-if="showEditModal"
      class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4"
      @click.self="closeModal"
    >
      <div class="bg-white w-full max-w-lg rounded-2xl p-6 space-y-4 max-h-[90vh] overflow-y-auto">
        <div class="flex items-center justify-between">
          <h2 class="text-xl font-bold text-slate-800">{{ editingProduct ? '编辑积分商品' : '添加积分商品' }}</h2>
          <button @click="closeModal" class="p-2 hover:bg-slate-100 rounded-xl">
            <X :size="20" class="text-slate-500" />
          </button>
        </div>

        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1">商品名称</label>
            <input
              v-model="formData.name"
              type="text"
              class="w-full px-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:border-emerald-500"
              :class="{ 'border-red-500': formErrors.name }"
            />
            <p v-if="formErrors.name" class="text-red-500 text-sm mt-1">{{ formErrors.name }}</p>
          </div>

          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1">商品描述</label>
            <textarea
              v-model="formData.description"
              rows="3"
              class="w-full px-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:border-emerald-500 resize-none"
              :class="{ 'border-red-500': formErrors.description }"
            ></textarea>
            <p v-if="formErrors.description" class="text-red-500 text-sm mt-1">{{ formErrors.description }}</p>
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-slate-700 mb-1">所需积分</label>
              <input
                v-model="formData.pointsRequired"
                type="number"
                min="1"
                class="w-full px-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:border-emerald-500"
                :class="{ 'border-red-500': formErrors.pointsRequired }"
              />
              <p v-if="formErrors.pointsRequired" class="text-red-500 text-sm mt-1">{{ formErrors.pointsRequired }}</p>
            </div>

            <div>
              <label class="block text-sm font-medium text-slate-700 mb-1">库存数量</label>
              <input
                v-model="formData.stock"
                type="number"
                min="0"
                class="w-full px-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:border-emerald-500"
                :class="{ 'border-red-500': formErrors.stock }"
              />
              <p v-if="formErrors.stock" class="text-red-500 text-sm mt-1">{{ formErrors.stock }}</p>
            </div>
          </div>

          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1">商品图片</label>
            <div class="border-2 border-dashed border-slate-200 rounded-xl p-4 text-center hover:border-emerald-300 transition-colors cursor-pointer" @click="$refs.imageInput.click()">
              <input 
                ref="imageInput" 
                type="file" 
                accept="image/*" 
                class="hidden" 
                @change="(e) => formData.image = e.target.files[0]"
              />
              <Upload :size="32" class="mx-auto text-slate-400 mb-2" />
              <p class="text-sm text-slate-500">点击上传图片</p>
              <p v-if="formData.image" class="text-xs text-slate-400 mt-2">{{ formData.image.name }}</p>
            </div>
            <p v-if="formErrors.image" class="text-red-500 text-sm mt-1">{{ formErrors.image }}</p>
          </div>
        </div>

        <div class="flex gap-3">
          <button
            @click="closeModal"
            class="flex-1 py-3 bg-slate-100 text-slate-600 rounded-xl font-bold hover:bg-slate-200 transition-colors"
          >
            取消
          </button>
          <button
            @click="() => { if (validateForm()) { confirmMessage = editingProduct ? '确定要修改这个积分商品吗？' : '确定要添加这个积分商品吗？'; showConfirmModal = true; } }"
            class="flex-1 py-3 bg-emerald-600 text-white rounded-xl font-bold hover:bg-emerald-700 transition-colors"
          >
            {{ editingProduct ? '保存修改' : '添加商品' }}
          </button>
        </div>
      </div>
    </div>

    <CustomModal
      :show="showConfirmModal"
      :title="'确认操作'"
      :message="confirmMessage"
      :type="'info'"
      :showCancel="true"
      :confirmText="'确定'"
      :cancelText="'取消'"
      @confirm="handleConfirmSave"
      @cancel="showConfirmModal = false"
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