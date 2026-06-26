<script setup>
import { ref, onMounted } from 'vue';
import { useStore } from '../store';
import { useRouter } from 'vue-router';
import { Plus, Minus, Trash2, ChevronRight, X, MapPin, Home, ArrowLeft } from 'lucide-vue-next';
import CustomModal from '../components/CustomModal.vue';
import Toast from '../components/Toast.vue';

const store = useStore();
const router = useRouter();
const showAddModal = ref(false);
const showEditModal = ref(false);
const editingAddress = ref(null);

// 表单数据
const formData = ref({
  name: '',
  phone: '',
  address: '',
  isDefault: false
});

const editFormData = ref({
  name: '',
  phone: '',
  address: '',
  isDefault: false
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

const fetchAddresses = async () => {
  try {
    const token = localStorage.getItem('token');
    const res = await fetch(`${API_BASE_URL}/api/addresses`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    if (res.ok) {
      const data = await res.json();
      store.addresses = data;
    }
  } catch (error) {
    console.error('获取地址失败:', error);
  }
};

const openAddModal = () => {
  formData.value = {
    name: '',
    phone: '',
    address: '',
    isDefault: false
  };
  showAddModal.value = true;
};

const openEditModal = (address) => {
  editingAddress.value = address;
  editFormData.value = {
    name: address.name,
    phone: address.phone,
    address: address.address,
    isDefault: address.isDefault
  };
  showEditModal.value = true;
};

const saveAddress = async () => {
  if (!formData.value.name || !formData.value.phone || !formData.value.address) {
    showAlert('请填写完整信息！', 'warning');
    return;
  }

  try {
    const url = editingAddress.value 
      ? `${API_BASE_URL}/api/addresses/${editingAddress.value.id}`
      : `${API_BASE_URL}/api/addresses`;
    const method = editingAddress.value ? 'PUT' : 'POST';
    const data = editingAddress.value ? editFormData.value : formData.value;

    const res = await fetch(url, {
      method,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: JSON.stringify(data)
    });

    if (res.ok) {
      await fetchAddresses();
      if (editingAddress.value) {
        showEditModal.value = false;
        showToastMessage('地址更新成功');
      } else {
        showAddModal.value = false;
        showToastMessage('地址添加完成！');
      }
    } else {
      showAlert('保存地址失败，请稍后重试', 'error');
    }
  } catch (error) {
    console.error('保存地址失败:', error);
    showAlert('保存地址失败，请稍后重试', 'error');
  }
};

const deleteAddress = (addressId) => {
  showConfirm('确定要删除该地址吗？', async () => {
    try {
      const res = await fetch(`${API_BASE_URL}/api/addresses/${addressId}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      });

      if (res.ok) {
        await fetchAddresses();
        showToastMessage('地址删除成功');
      } else {
        showAlert('删除地址失败，请稍后重试', 'error');
      }
    } catch (error) {
      console.error('删除地址失败:', error);
      showAlert('删除地址失败，请稍后重试', 'error');
    }
  });
};

onMounted(async () => {
  await fetchAddresses();
});
</script>

<template>
  <div class="px-6 py-8 space-y-6 min-h-screen bg-slate-50">
    <header class="flex items-center justify-between">
      <div class="flex items-center gap-4">
        <button
          @click="router.back()"
          class="p-2 hover:bg-slate-200 rounded-xl transition-colors"
        >
          <ArrowLeft :size="24" class="text-slate-600" />
        </button>
        <h1 class="text-2xl font-black text-slate-800">收货地址</h1>
      </div>
      <button
        @click="openAddModal"
        class="flex items-center gap-2 px-4 py-2 bg-orange-500 text-white rounded-xl font-bold hover:bg-orange-600 transition-colors"
      >
        <Plus :size="18" />
        添加地址
      </button>
    </header>

    <div class="space-y-4">
      <div
        v-for="address in store.addresses"
        :key="address.id"
        class="bg-white rounded-2xl p-4 shadow-sm border border-slate-100 hover:shadow-md transition-shadow"
      >
        <div class="flex justify-between items-start">
          <div class="flex-1 min-w-0">
            <div class="flex items-center gap-2">
              <h3 class="font-bold text-slate-800">{{ address.name }}</h3>
              <span class="text-sm text-slate-500">{{ address.phone }}</span>
              <span v-if="address.isDefault" class="px-2 py-0.5 bg-orange-100 text-orange-600 rounded-full text-xs font-bold">
                默认
              </span>
            </div>
            <p class="text-sm text-slate-600 mt-2">{{ address.address }}</p>
          </div>
        </div>
        <div class="flex gap-2 mt-4">
          <button
            @click="openEditModal(address)"
            class="flex-1 py-2 bg-blue-50 text-blue-600 rounded-xl text-sm font-bold hover:bg-blue-100 transition-colors"
          >
            编辑
          </button>
          <button
            @click="deleteAddress(address.id)"
            class="flex-1 py-2 bg-red-50 text-red-600 rounded-xl text-sm font-bold hover:bg-red-100 transition-colors"
          >
            <Trash2 :size="14" class="inline mr-1" />
            删除
          </button>
        </div>
      </div>
    </div>

    <!-- 添加地址弹窗 -->
    <div
      v-if="showAddModal"
      class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4"
      @click.self="showAddModal = false"
    >
      <div class="bg-white w-full max-w-lg rounded-2xl p-6 space-y-4 max-h-[90vh] overflow-y-auto">
        <div class="flex items-center justify-between">
          <h2 class="text-xl font-bold text-slate-800">添加地址</h2>
          <button @click="showAddModal = false" class="p-2 hover:bg-slate-100 rounded-xl">
            <X :size="20" class="text-slate-500" />
          </button>
        </div>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1">收货人</label>
            <input
              v-model="formData.name"
              type="text"
              class="w-full px-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:border-blue-500"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1">手机号</label>
            <input
              v-model="formData.phone"
              type="tel"
              class="w-full px-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:border-blue-500"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1">详细地址</label>
            <textarea
              v-model="formData.address"
              rows="3"
              class="w-full px-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:border-blue-500 resize-none"
            ></textarea>
          </div>
          <div class="flex items-center gap-2">
            <input
              v-model="formData.isDefault"
              type="checkbox"
              id="default"
              class="w-4 h-4 text-blue-600 border-slate-300 rounded focus:ring-blue-500"
            />
            <label for="default" class="text-sm font-medium text-slate-700">设为默认地址</label>
          </div>
        </div>
        <button
          @click="saveAddress"
          class="w-full py-3 bg-orange-500 text-white rounded-xl font-bold hover:bg-orange-600 transition-colors"
        >
          添加地址
        </button>
      </div>
    </div>

    <!-- 编辑地址弹窗 -->
    <div
      v-if="showEditModal"
      class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4"
      @click.self="showEditModal = false"
    >
      <div class="bg-white w-full max-w-lg rounded-2xl p-6 space-y-4 max-h-[90vh] overflow-y-auto">
        <div class="flex items-center justify-between">
          <h2 class="text-xl font-bold text-slate-800">编辑地址</h2>
          <button @click="showEditModal = false" class="p-2 hover:bg-slate-100 rounded-xl">
            <X :size="20" class="text-slate-500" />
          </button>
        </div>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1">收货人</label>
            <input
              v-model="editFormData.name"
              type="text"
              class="w-full px-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:border-blue-500"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1">手机号</label>
            <input
              v-model="editFormData.phone"
              type="tel"
              class="w-full px-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:border-blue-500"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1">详细地址</label>
            <textarea
              v-model="editFormData.address"
              rows="3"
              class="w-full px-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:border-blue-500 resize-none"
            ></textarea>
          </div>
          <div class="flex items-center gap-2">
            <input
              v-model="editFormData.isDefault"
              type="checkbox"
              id="edit-default"
              class="w-4 h-4 text-blue-600 border-slate-300 rounded focus:ring-blue-500"
            />
            <label for="edit-default" class="text-sm font-medium text-slate-700">设为默认地址</label>
          </div>
        </div>
        <button
          @click="saveAddress"
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