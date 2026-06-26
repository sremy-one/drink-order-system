<script setup>
import { ref, onMounted } from 'vue';
import { Plus, Trash2, X, Upload, Image, ChevronRight, Edit, Eye, EyeOff } from 'lucide-vue-next';
import { useStore } from '../store';
import CustomModal from '../components/CustomModal.vue';
import Toast from '../components/Toast.vue';

const store = useStore();
const showAddModal = ref(false);
const showEditModal = ref(false);
const editingDrink = ref(null);

// 表单数据
const formData = ref({
  name: '',
  description: '',
  price: '',
  stock: '',
  category: '奶茶',
  available: true,
  image: null
});

const editFormData = ref({
  name: '',
  description: '',
  price: '',
  stock: '',
  category: '奶茶',
  available: true,
  image: null
});

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

const categories = ['奶茶', '果茶', '咖啡', '沙冰', '其他'];

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

const openAddModal = () => {
  formData.value = {
    name: '',
    description: '',
    price: '',
    stock: '',
    category: '奶茶',
    available: true,
    image: null
  };
  showAddModal.value = true;
};

const openEditModal = (drink) => {
  editingDrink.value = drink;
  editFormData.value = {
    name: drink.name,
    description: drink.description,
    price: drink.price,
    stock: drink.stock,
    category: drink.category,
    available: drink.available,
    image: null
  };
  showEditModal.value = true;
};

const addProduct = async () => {
  if (!formData.value.name || !formData.value.price || !formData.value.stock) {
    showAlert('请填写完整的商品信息', 'warning');
    return;
  }

  try {
    const formDataObj = new FormData();
    formDataObj.append('name', formData.value.name);
    formDataObj.append('description', formData.value.description);
    formDataObj.append('price', formData.value.price);
    formDataObj.append('stock', formData.value.stock);
    formDataObj.append('category', formData.value.category);
    formDataObj.append('available', formData.value.available);
    if (formData.value.image) {
      formDataObj.append('image', formData.value.image);
    }

    const res = await fetch(`${API_BASE_URL}/api/merchant/products`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: formDataObj
    });

    if (res.ok) {
      await store.fetchDrinks();
      showAddModal.value = false;
      showToastMessage('商品添加成功');
    } else {
      showAlert('商品添加失败，请稍后重试', 'error');
    }
  } catch (error) {
    console.error('添加商品失败:', error);
    showAlert('商品添加失败，请稍后重试', 'error');
  }
};

const updateProduct = async () => {
  if (!editFormData.value.name || !editFormData.value.price || !editFormData.value.stock) {
    showAlert('请填写完整的商品信息', 'warning');
    return;
  }

  try {
    const formDataObj = new FormData();
    formDataObj.append('name', editFormData.value.name);
    formDataObj.append('description', editFormData.value.description);
    formDataObj.append('price', editFormData.value.price);
    formDataObj.append('stock', editFormData.value.stock);
    formDataObj.append('category', editFormData.value.category);
    formDataObj.append('available', editFormData.value.available);
    if (editFormData.value.image) {
      formDataObj.append('image', editFormData.value.image);
    }

    const res = await fetch(`${API_BASE_URL}/api/merchant/products/${editingDrink.value.id}`, {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: formDataObj
    });

    if (res.ok) {
      await store.fetchDrinks();
      showEditModal.value = false;
      showToastMessage('商品更新成功');
    } else {
      showAlert('商品更新失败，请稍后重试', 'error');
    }
  } catch (error) {
    console.error('更新商品失败:', error);
    showAlert('商品更新失败，请稍后重试', 'error');
  }
};

const deleteProduct = (productId) => {
  showConfirm('确定要删除这个商品吗？', async () => {
    try {
      const res = await fetch(`${API_BASE_URL}/api/merchant/products/${productId}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      });

      if (res.ok) {
        await store.fetchDrinks();
        showToastMessage('商品删除成功');
      } else {
        showAlert('商品删除失败，请稍后重试', 'error');
      }
    } catch (error) {
      console.error('删除商品失败:', error);
      showAlert('商品删除失败，请稍后重试', 'error');
    }
  });
};

const toggleProductAvailability = async (drink) => {
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
    } else {
      showAlert('操作失败，请稍后重试', 'error');
    }
  } catch (error) {
    console.error('切换商品状态失败:', error);
    showAlert('操作失败，请稍后重试', 'error');
  }
};

onMounted(async () => {
  await store.fetchDrinks();
});
</script>

<template>
  <div class="px-6 py-8 space-y-6 min-h-screen bg-slate-50">
    <header class="flex items-center justify-between">
      <h1 class="text-2xl font-black text-slate-800">商品管理</h1>
      <button
        @click="openAddModal"
        class="flex items-center gap-2 px-4 py-2 bg-orange-500 text-white rounded-xl font-bold hover:bg-orange-600 transition-colors"
      >
        <Plus :size="18" />
        添加商品
      </button>
    </header>

    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
      <div
        v-for="drink in store.drinks"
        :key="drink.id"
        class="bg-white rounded-2xl p-4 shadow-sm border border-slate-100 hover:shadow-md transition-shadow"
      >
        <div class="flex justify-between items-start">
          <div class="flex items-center gap-4">
            <img :src="API_BASE_URL + drink.image" class="w-16 h-16 object-cover rounded-xl" />
            <div class="flex-1 min-w-0">
              <h3 class="font-bold text-slate-800 line-clamp-1">{{ drink.name }}</h3>
              <p class="text-xs text-slate-500 mt-1 line-clamp-1">{{ drink.description }}</p>
              <div class="flex items-center gap-4 mt-2">
                <span class="text-sm font-black text-orange-600">￥{{ drink.price }}</span>
                <span class="text-xs text-slate-500">库存: {{ drink.stock }}</span>
                <span class="text-xs text-slate-500">分类: {{ drink.category }}</span>
              </div>
            </div>
          </div>
          <div class="flex flex-col gap-2">
            <button
              @click="toggleProductAvailability(drink)"
              :class="[
                'px-2 py-1 rounded text-xs font-bold transition-colors',
                drink.available
                  ? 'bg-green-50 text-green-600 hover:bg-green-100'
                  : 'bg-red-50 text-red-600 hover:bg-red-100'
              ]"
            >
              {{ drink.available ? '上架' : '下架' }}
            </button>
          </div>
        </div>
        <div class="flex gap-2 mt-4">
          <button
            @click="openEditModal(drink)"
            class="flex-1 py-2 bg-blue-50 text-blue-600 rounded-xl text-sm font-bold hover:bg-blue-100 transition-colors"
          >
            <Edit :size="14" class="inline mr-1" />
            编辑
          </button>
          <button
            @click="deleteProduct(drink.id)"
            class="flex-1 py-2 bg-red-50 text-red-600 rounded-xl text-sm font-bold hover:bg-red-100 transition-colors"
          >
            <Trash2 :size="14" class="inline mr-1" />
            删除
          </button>
        </div>
      </div>
    </div>

    <!-- 添加商品弹窗 -->
    <div
      v-if="showAddModal"
      class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4"
      @click.self="showAddModal = false"
    >
      <div class="bg-white w-full max-w-lg rounded-2xl p-6 space-y-4 max-h-[90vh] overflow-y-auto">
        <div class="flex items-center justify-between">
          <h2 class="text-xl font-bold text-slate-800">添加商品</h2>
          <button @click="showAddModal = false" class="p-2 hover:bg-slate-100 rounded-xl">
            <X :size="20" class="text-slate-500" />
          </button>
        </div>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1">商品图片</label>
            <div class="border-2 border-dashed border-slate-200 rounded-xl p-4 text-center hover:border-blue-300 transition-colors cursor-pointer" @click="$refs.imageInput.click()">
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
          </div>
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1">商品名称</label>
            <input
              v-model="formData.name"
              type="text"
              class="w-full px-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:border-blue-500"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1">商品描述</label>
            <textarea
              v-model="formData.description"
              rows="3"
              class="w-full px-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:border-blue-500 resize-none"
            ></textarea>
          </div>
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-slate-700 mb-1">价格</label>
              <div class="relative">
                <span class="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 font-bold">￥</span>
                <input
                  v-model="formData.price"
                  type="number"
                  step="0.01"
                  class="w-full pl-10 pr-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:border-blue-500"
                />
              </div>
            </div>
            <div>
              <label class="block text-sm font-medium text-slate-700 mb-1">库存</label>
              <input
                v-model="formData.stock"
                type="number"
                class="w-full px-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:border-blue-500"
              />
            </div>
          </div>
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1">分类</label>
            <select
              v-model="formData.category"
              class="w-full px-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:border-blue-500"
            >
              <option v-for="category in categories" :key="category" :value="category">
                {{ category }}
              </option>
            </select>
          </div>
          <div class="flex items-center gap-2">
            <input
              v-model="formData.available"
              type="checkbox"
              id="available"
              class="w-4 h-4 text-blue-600 border-slate-300 rounded focus:ring-blue-500"
            />
            <label for="available" class="text-sm font-medium text-slate-700">商品上架</label>
          </div>
        </div>
        <button
          @click="addProduct"
          class="w-full py-3 bg-orange-500 text-white rounded-xl font-bold hover:bg-orange-600 transition-colors"
        >
          添加商品
        </button>
      </div>
    </div>

    <!-- 编辑商品弹窗 -->
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
            <label class="block text-sm font-medium text-slate-700 mb-1">商品图片</label>
            <div class="border-2 border-dashed border-slate-200 rounded-xl p-4 text-center hover:border-blue-300 transition-colors cursor-pointer" @click="$refs.editImageInput.click()">
              <input 
                ref="editImageInput" 
                type="file" 
                accept="image/*" 
                class="hidden" 
                @change="(e) => editFormData.image = e.target.files[0]"
              />
              <Upload :size="32" class="mx-auto text-slate-400 mb-2" />
              <p class="text-sm text-slate-500">点击上传图片</p>
              <p v-if="editFormData.image" class="text-xs text-slate-400 mt-2">{{ editFormData.image.name }}</p>
            </div>
          </div>
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
                <span class="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 font-bold">￥</span>
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
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1">分类</label>
            <select
              v-model="editFormData.category"
              class="w-full px-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:border-blue-500"
            >
              <option v-for="category in categories" :key="category" :value="category">
                {{ category }}
              </option>
            </select>
          </div>
          <div class="flex items-center gap-2">
            <input
              v-model="editFormData.available"
              type="checkbox"
              id="edit-available"
              class="w-4 h-4 text-blue-600 border-slate-300 rounded focus:ring-blue-500"
            />
            <label for="edit-available" class="text-sm font-medium text-slate-700">商品上架</label>
          </div>
        </div>
        <button
          @click="updateProduct"
          class="w-full py-3 bg-blue-600 text-white rounded-xl font-bold hover:bg-blue-700 transition-colors"
        >
          保存修改
        </button>
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