<script setup>
import { ref, onMounted } from 'vue';
import { Plus, Trash2, X, ChevronRight, Edit, ArrowUpDown, Upload, ArrowLeft } from 'lucide-vue-next';
import { useStore } from '../store';
import { useRouter } from 'vue-router';
import CustomModal from '../components/CustomModal.vue';
import Toast from '../components/Toast.vue';

const store = useStore();
const router = useRouter();
const showAddModal = ref(false);
const showEditModal = ref(false);
const editingActivity = ref(null);

// 表单数据
const formData = ref({
  title: '',
  badge: '',
  image: null,
  description: '',
  available: true
});

const editFormData = ref({
  title: '',
  badge: '',
  image: null,
  description: '',
  available: true
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

const fetchActivities = async () => {
  try {
    const token = localStorage.getItem('token');
    const res = await fetch(`${API_BASE_URL}/api/merchant/activities`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    if (res.ok) {
      const data = await res.json();
      store.activities = data;
    }
  } catch (error) {
    console.error('获取活动失败:', error);
  }
};

const openAddModal = () => {
  editingActivity.value = null;
  formData.value = {
    title: '',
    badge: '',
    image: null,
    description: '',
    available: true
  };
  showAddModal.value = true;
};

const openEditModal = (activity) => {
  editingActivity.value = activity;
  editFormData.value = {
    title: activity.title,
    badge: activity.badge,
    image: null,
    description: activity.description,
    available: activity.available
  };
  showEditModal.value = true;
};

const saveActivity = async () => {
  const data = editingActivity.value ? editFormData.value : formData.value;
  if (!data.title || !data.badge) {
    showAlert('请填写完整的活动信息', 'warning');
    return;
  }

  try {
    const url = editingActivity.value
      ? `${API_BASE_URL}/api/merchant/activities/${editingActivity.value.id}`
      : `${API_BASE_URL}/api/merchant/activities`;
    const method = editingActivity.value ? 'PUT' : 'POST';

    const formDataObj = new FormData();
    formDataObj.append('title', data.title);
    formDataObj.append('badge', data.badge);
    formDataObj.append('description', data.description);
    formDataObj.append('available', data.available);
    if (data.image) {
      formDataObj.append('image', data.image);
    }

    const res = await fetch(url, {
      method,
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: formDataObj
    });

    if (res.ok) {
      await fetchActivities();
      if (editingActivity.value) {
        showEditModal.value = false;
        showToastMessage('活动更新成功');
      } else {
        showAddModal.value = false;
        showToastMessage('活动添加成功');
      }
    } else {
      showAlert('保存活动失败，请稍后重试', 'error');
    }
  } catch (error) {
    console.error('保存活动失败:', error);
    showAlert('保存活动失败，请稍后重试', 'error');
  }
};

const deleteActivity = (activityId) => {
  showConfirm('确定要删除这个活动吗？', async () => {
    try {
      const res = await fetch(`${API_BASE_URL}/api/merchant/activities/${activityId}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      });

      if (res.ok) {
        await fetchActivities();
        showToastMessage('活动删除成功');
      } else {
        showAlert('删除活动失败，请稍后重试', 'error');
      }
    } catch (error) {
      console.error('删除活动失败:', error);
      showAlert('删除活动失败，请稍后重试', 'error');
    }
  });
};

const toggleActivityAvailability = async (activity) => {
  try {
    const res = await fetch(`${API_BASE_URL}/api/merchant/activities/${activity.id}/toggle`, {
      method: 'PATCH',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    });

    if (res.ok) {
      const updatedActivity = await res.json();
      await fetchActivities();
      showToastMessage(`活动已${updatedActivity.available ? '上架' : '下架'}`);
    } else {
      showAlert('操作失败，请稍后重试', 'error');
    }
  } catch (error) {
    console.error('切换活动状态失败:', error);
    showAlert('操作失败，请稍后重试', 'error');
  }
};

onMounted(async () => {
  await fetchActivities();
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
        <h1 class="text-2xl font-black text-slate-800">活动管理</h1>
      </div>
      <button
        @click="openAddModal"
        class="flex items-center gap-2 px-4 py-2 bg-orange-500 text-white rounded-xl font-bold hover:bg-orange-600 transition-colors"
      >
        <Plus :size="18" />
        添加活动
      </button>
    </header>

    <div class="space-y-4">
      <div
        v-for="activity in store.activities"
        :key="activity.id"
        class="bg-white rounded-2xl p-4 shadow-sm border border-slate-100 hover:shadow-md transition-shadow"
      >
        <div class="flex justify-between items-start">
          <div class="flex items-center gap-4">
            <img :src="API_BASE_URL + activity.image" class="w-16 h-16 object-cover rounded-xl" />
            <div class="flex-1 min-w-0">
              <div class="flex items-center gap-2">
                <h3 class="font-bold text-slate-800 line-clamp-1">{{ activity.title }}</h3>
                <span class="px-2 py-0.5 bg-orange-100 text-orange-600 rounded-full text-xs font-bold">
                  {{ activity.badge }}
                </span>
              </div>
              <p class="text-xs text-slate-500 mt-1 line-clamp-2">{{ activity.description }}</p>
            </div>
          </div>
          <div class="flex flex-col gap-2">
            <button
              @click="toggleActivityAvailability(activity)"
              :class="[
                'px-2 py-1 rounded text-xs font-bold transition-colors',
                activity.available
                  ? 'bg-green-50 text-green-600 hover:bg-green-100'
                  : 'bg-red-50 text-red-600 hover:bg-red-100'
              ]"
            >
              {{ activity.available ? '上架' : '下架' }}
            </button>
          </div>
        </div>
        <div class="flex gap-2 mt-4">
          <button
            @click="openEditModal(activity)"
            class="flex-1 py-2 bg-blue-50 text-blue-600 rounded-xl text-sm font-bold hover:bg-blue-100 transition-colors"
          >
            <Edit :size="14" class="inline mr-1" />
            编辑
          </button>
          <button
            @click="deleteActivity(activity.id)"
            class="flex-1 py-2 bg-red-50 text-red-600 rounded-xl text-sm font-bold hover:bg-red-100 transition-colors"
          >
            <Trash2 :size="14" class="inline mr-1" />
            删除
          </button>
        </div>
      </div>
    </div>

    <!-- 添加活动弹窗 -->
    <div
      v-if="showAddModal"
      class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4"
      @click.self="showAddModal = false"
    >
      <div class="bg-white w-full max-w-lg rounded-2xl p-6 space-y-4 max-h-[90vh] overflow-y-auto">
        <div class="flex items-center justify-between">
          <h2 class="text-xl font-bold text-slate-800">添加活动</h2>
          <button @click="showAddModal = false" class="p-2 hover:bg-slate-100 rounded-xl">
            <X :size="20" class="text-slate-500" />
          </button>
        </div>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1">活动标题</label>
            <input
              v-model="formData.title"
              type="text"
              class="w-full px-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:border-blue-500"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1">活动标签</label>
            <input
              v-model="formData.badge"
              type="text"
              class="w-full px-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:border-blue-500"
              placeholder="例如：限时特惠"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1">活动图片</label>
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
            <label class="block text-sm font-medium text-slate-700 mb-1">活动描述</label>
            <textarea
              v-model="formData.description"
              rows="3"
              class="w-full px-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:border-blue-500 resize-none"
            ></textarea>
          </div>
          <div class="flex items-center gap-2">
            <input
              v-model="formData.available"
              type="checkbox"
              id="available"
              class="w-4 h-4 text-blue-600 border-slate-300 rounded focus:ring-blue-500"
            />
            <label for="available" class="text-sm font-medium text-slate-700">活动上架</label>
          </div>
        </div>
        <button
          @click="saveActivity"
          class="w-full py-3 bg-orange-500 text-white rounded-xl font-bold hover:bg-orange-600 transition-colors"
        >
          添加活动
        </button>
      </div>
    </div>

    <!-- 编辑活动弹窗 -->
    <div
      v-if="showEditModal"
      class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4"
      @click.self="showEditModal = false"
    >
      <div class="bg-white w-full max-w-lg rounded-2xl p-6 space-y-4 max-h-[90vh] overflow-y-auto">
        <div class="flex items-center justify-between">
          <h2 class="text-xl font-bold text-slate-800">编辑活动</h2>
          <button @click="showEditModal = false" class="p-2 hover:bg-slate-100 rounded-xl">
            <X :size="20" class="text-slate-500" />
          </button>
        </div>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1">活动标题</label>
            <input
              v-model="editFormData.title"
              type="text"
              class="w-full px-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:border-blue-500"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1">活动标签</label>
            <input
              v-model="editFormData.badge"
              type="text"
              class="w-full px-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:border-blue-500"
              placeholder="例如：限时特惠"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1">活动图片</label>
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
            <label class="block text-sm font-medium text-slate-700 mb-1">活动描述</label>
            <textarea
              v-model="editFormData.description"
              rows="3"
              class="w-full px-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:border-blue-500 resize-none"
            ></textarea>
          </div>
          <div class="flex items-center gap-2">
            <input
              v-model="editFormData.available"
              type="checkbox"
              id="edit-available"
              class="w-4 h-4 text-blue-600 border-slate-300 rounded focus:ring-blue-500"
            />
            <label for="edit-available" class="text-sm font-medium text-slate-700">活动上架</label>
          </div>
        </div>
        <button
          @click="saveActivity"
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