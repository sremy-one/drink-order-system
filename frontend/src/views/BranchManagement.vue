<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { Store, MapPin, Phone, Star, Plus, Edit, Trash2, Save, X, ChevronUp, ChevronDown } from 'lucide-vue-next';
import Toast from '../components/Toast.vue';

const router = useRouter();
const API_BASE_URL = 'http://localhost:3001';

// 分店列表
const branches = ref([]);
// 加载状态
const loading = ref(true);
// 编辑模式
const isEditing = ref(false);
// 当前编辑的分店
const currentBranch = ref(null);
// 新增模式
const isAdding = ref(false);
// 表单数据
const formData = ref({
  name: '',
  address: '',
  phone: '',
  distance: 0,
  rating: 0,
  open: true
});

// Toast状态
const showToast = ref(false);
const toastMessage = ref('');
const toastType = ref('success');

// 显示Toast消息
const showToastMessage = (message, type = 'success') => {
  toastMessage.value = message;
  toastType.value = type;
  showToast.value = true;
  setTimeout(() => {
    showToast.value = false;
  }, 3000);
};

// 加载分店列表
const loadBranches = async () => {
  try {
    const res = await fetch(`${API_BASE_URL}/api/merchants/branches`, {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    });
    if (res.ok) {
      const data = await res.json();
      branches.value = data;
    } else {
      showToastMessage('加载分店列表失败', 'error');
    }
  } catch (error) {
    console.error('加载分店列表失败:', error);
    showToastMessage('加载分店列表失败', 'error');
  } finally {
    loading.value = false;
  }
};

// 新增分店
const addBranch = () => {
  isAdding.value = true;
  isEditing.value = false;
  formData.value = {
    name: '',
    address: '',
    phone: '',
    distance: 0,
    rating: 0,
    open: true
  };
};

// 编辑分店
const editBranch = (branch) => {
  isEditing.value = true;
  isAdding.value = false;
  currentBranch.value = branch;
  formData.value = {
    name: branch.name,
    address: branch.address,
    phone: branch.phone,
    distance: branch.distance || 0,
    rating: branch.rating || 0,
    open: branch.open
  };
};

// 保存分店
const saveBranch = async () => {
  try {
    const url = isEditing.value 
      ? `${API_BASE_URL}/api/merchants/branches/${currentBranch.value.id}`
      : `${API_BASE_URL}/api/merchants/branches`;
    
    const method = isEditing.value ? 'PUT' : 'POST';
    
    const res = await fetch(url, {
      method,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: JSON.stringify({
        ...formData.value,
        merchantId: 1 // 所有分店共用同一个商家
      })
    });
    
    if (res.ok) {
      await loadBranches();
      cancelEdit();
      showToastMessage(isEditing.value ? '分店更新成功' : '分店添加成功');
    } else {
      showToastMessage('操作失败，请稍后重试', 'error');
    }
  } catch (error) {
    console.error('保存分店失败:', error);
    showToastMessage('操作失败，请稍后重试', 'error');
  }
};

// 取消编辑
const cancelEdit = () => {
  isEditing.value = false;
  isAdding.value = false;
  currentBranch.value = null;
  formData.value = {
    name: '',
    address: '',
    phone: '',
    distance: 0,
    rating: 0,
    open: true
  };
};

// 删除分店
const deleteBranch = async (branchId) => {
  if (confirm('确定要删除这个分店吗？')) {
    try {
      const res = await fetch(`${API_BASE_URL}/api/merchants/branches/${branchId}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      });
      if (res.ok) {
        await loadBranches();
        showToastMessage('分店删除成功');
      } else {
        showToastMessage('删除失败，请稍后重试', 'error');
      }
    } catch (error) {
      console.error('删除分店失败:', error);
      showToastMessage('删除失败，请稍后重试', 'error');
    }
  }
};

// 切换分店状态
const toggleBranchStatus = async (branch) => {
  try {
    const res = await fetch(`${API_BASE_URL}/api/merchants/branches/${branch.id}/toggle`, {
      method: 'PATCH',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    });
    if (res.ok) {
      await loadBranches();
      showToastMessage(`分店已${branch.open ? '关闭' : '开启'}`);
    } else {
      showToastMessage('操作失败，请稍后重试', 'error');
    }
  } catch (error) {
    console.error('切换分店状态失败:', error);
    showToastMessage('操作失败，请稍后重试', 'error');
  }
};

// 初始化
onMounted(() => {
  loadBranches();
});
</script>

<template>
  <div class="px-6 py-8 space-y-6 min-h-screen bg-slate-50 pb-32">
    <!-- 页面头部 -->
    <header class="flex items-center justify-between">
      <div class="flex items-center gap-4">
        <button @click="router.push('/')" class="p-2 hover:bg-slate-100 rounded-full transition-colors">
          <ChevronUp :size="24" class="text-slate-600" />
        </button>
        <h1 class="text-2xl font-black text-slate-800">门店管理</h1>
      </div>
      <button 
        @click="addBranch"
        class="flex items-center gap-2 px-4 py-2 bg-green-500 text-white rounded-full hover:bg-green-600 transition-colors"
      >
        <Plus :size="18" />
        <span class="font-bold">添加门店</span>
      </button>
    </header>

    <!-- 加载状态 -->
    <div v-if="loading" class="flex justify-center items-center py-12">
      <div class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
    </div>

    <!-- 分店列表 -->
    <div v-else class="space-y-4">
      <div 
        v-for="branch in branches" 
        :key="branch.id"
        class="bg-white rounded-xl p-6 shadow-sm hover:shadow-md transition-shadow"
      >
        <div class="flex justify-between items-start">
          <div>
            <h3 class="text-lg font-bold text-slate-800 flex items-center gap-2">
              <Store :size="18" class="text-green-500" />
              {{ branch.name }}
              <span 
                :class="branch.open ? 'bg-green-100 text-green-600' : 'bg-red-100 text-red-600'"
                class="text-xs px-2 py-1 rounded-full ml-2"
              >
                {{ branch.open ? '营业中' : '已关闭' }}
              </span>
            </h3>
            <p class="text-sm text-slate-500 flex items-center gap-1 mt-2">
              <MapPin :size="14" />
              {{ branch.address }}
            </p>
            <p class="text-sm text-slate-500 flex items-center gap-1 mt-1">
              <Phone :size="14" />
              {{ branch.phone }}
            </p>
            <div class="flex items-center gap-4 mt-2">
              <p class="text-sm text-slate-500">
                距离: {{ branch.distance || 0 }}km
              </p>
              <p class="text-sm text-slate-500 flex items-center gap-1">
                <Star :size="14" class="text-yellow-500" />
                {{ branch.rating || 0 }}
              </p>
            </div>
          </div>
          <div class="flex gap-2">
            <button 
              @click="editBranch(branch)"
              class="p-2 hover:bg-slate-100 rounded-full transition-colors"
              title="编辑"
            >
              <Edit :size="18" class="text-blue-500" />
            </button>
            <button 
              @click="deleteBranch(branch.id)"
              class="p-2 hover:bg-slate-100 rounded-full transition-colors"
              title="删除"
            >
              <Trash2 :size="18" class="text-red-500" />
            </button>
            <button 
              @click="toggleBranchStatus(branch)"
              class="p-2 hover:bg-slate-100 rounded-full transition-colors"
              title="切换状态"
            >
              {{ branch.open ? '关闭' : '开启' }}
            </button>
          </div>
        </div>
      </div>
      
      <!-- 空状态 -->
      <div v-if="branches.length === 0" class="text-center py-12">
        <Store :size="48" class="text-slate-300 mx-auto mb-4" />
        <p class="text-slate-400">暂无分店数据</p>
        <button 
          @click="addBranch"
          class="mt-4 flex items-center gap-2 px-4 py-2 bg-green-500 text-white rounded-full hover:bg-green-600 transition-colors mx-auto"
        >
          <Plus :size="18" />
          <span class="font-bold">添加第一个门店</span>
        </button>
      </div>
    </div>

    <!-- 编辑/新增表单 -->
    <div v-if="isEditing || isAdding" class="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50">
      <div class="bg-white rounded-xl p-6 w-full max-w-md">
        <div class="flex justify-between items-center mb-4">
          <h2 class="text-xl font-bold text-slate-800">{{ isEditing ? '编辑门店' : '新增门店' }}</h2>
          <button @click="cancelEdit" class="p-2 hover:bg-slate-100 rounded-full transition-colors">
            <X :size="20" class="text-slate-500" />
          </button>
        </div>
        
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-semibold text-slate-700 mb-1">门店名称</label>
            <input 
              v-model="formData.name"
              type="text" 
              class="w-full px-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
              placeholder="请输入门店名称"
            />
          </div>
          
          <div>
            <label class="block text-sm font-semibold text-slate-700 mb-1">地址</label>
            <input 
              v-model="formData.address"
              type="text" 
              class="w-full px-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
              placeholder="请输入门店地址"
            />
          </div>
          
          <div>
            <label class="block text-sm font-semibold text-slate-700 mb-1">联系电话</label>
            <input 
              v-model="formData.phone"
              type="tel" 
              class="w-full px-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
              placeholder="请输入联系电话"
            />
          </div>
          
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-1">距离(km)</label>
              <input 
                v-model.number="formData.distance"
                type="number" 
                step="0.1"
                min="0"
                class="w-full px-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
                placeholder="0"
              />
            </div>
            
            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-1">评分</label>
              <input 
                v-model.number="formData.rating"
                type="number" 
                step="0.1"
                min="0"
                max="5"
                class="w-full px-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
                placeholder="0"
              />
            </div>
          </div>
          
          <div class="flex items-center justify-between">
            <label class="text-sm font-semibold text-slate-700">营业状态</label>
            <input 
              v-model="formData.open"
              type="checkbox" 
              class="w-5 h-5 rounded border-slate-300 text-green-500 focus:ring-green-500"
            />
          </div>
        </div>
        
        <div class="flex gap-3 mt-6">
          <button 
            @click="cancelEdit"
            class="flex-1 px-4 py-2 border border-slate-300 rounded-lg hover:bg-slate-50 transition-colors"
          >
            取消
          </button>
          <button 
            @click="saveBranch"
            class="flex-1 px-4 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600 transition-colors flex items-center justify-center gap-2"
          >
            <Save :size="18" />
            <span class="font-bold">{{ isEditing ? '保存修改' : '添加门店' }}</span>
          </button>
        </div>
      </div>
    </div>

    <!-- Toast提示 -->
    <Toast v-if="showToast" :message="toastMessage" :type="toastType" @close="showToast = false" />
  </div>
</template>

<style scoped>
/* 可以添加自定义样式 */
</style>
