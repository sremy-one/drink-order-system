<script setup>
import { ref, onMounted } from 'vue';
import { useStore } from '../store';
import { Plus, Minus, Trash2, ChevronRight, X, Send } from 'lucide-vue-next';
import CustomModal from '../components/CustomModal.vue';
import Toast from '../components/Toast.vue';
import api from '../api/axios.js';

const store = useStore();
const showAddModal = ref(false);
const showEditModal = ref(false);
const showIssueModal = ref(false);
const editingCoupon = ref(null);
const issuingCoupon = ref(null);

const MEMBER_LEVELS = ['初级', '白银', '黄金', '白金', '超级', '至尊'];

const formData = ref({
  code: '',
  discount: '',
  minimumSpend: '',
  expireDate: '',
  targetMemberLevel: '',
  minConsumption: '',
  isActive: true,
  description: ''
});

const editFormData = ref({
  code: '',
  discount: '',
  minimumSpend: '',
  expireDate: '',
  targetMemberLevel: '',
  minConsumption: '',
  isActive: true,
  description: ''
});

const issueFormData = ref({
  targetMemberLevel: '',
  minConsumption: ''
});

const API_BASE_URL = 'http://localhost:3001';

const showConfirmModal = ref(false);
const confirmMessage = ref('');
const confirmAction = ref(null);
const showAlertModal = ref(false);
const alertMessage = ref('');
const alertType = ref('info');

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

const fetchCoupons = async () => {
  try {
    await store.fetchCoupons();
  } catch (error) {
    console.error('获取优惠券失败:', error);
  }
};

const openAddModal = () => {
  formData.value = {
    code: '',
    discount: '',
    minimumSpend: '',
    expireDate: '',
    targetMemberLevel: '',
    minConsumption: '',
    isActive: true,
    description: ''
  };
  showAddModal.value = true;
};

const openEditModal = (coupon) => {
  editingCoupon.value = coupon;
  editFormData.value = {
    code: coupon.code || '',
    discount: coupon.discount || '',
    minimumSpend: coupon.minimumSpend || '',
    expireDate: coupon.expireDate || '',
    targetMemberLevel: coupon.targetMemberLevel || '',
    minConsumption: coupon.minConsumption || '',
    isActive: coupon.isActive !== false,
    description: coupon.description || ''
  };
  showEditModal.value = true;
};

const openIssueModal = (coupon) => {
  issuingCoupon.value = coupon;
  issueFormData.value = {
    targetMemberLevel: coupon.targetMemberLevel || '',
    minConsumption: coupon.minConsumption || ''
  };
  showIssueModal.value = true;
};

const validateForm = (form, isEdit = false) => {
  if (!form.code || form.code.trim() === '') {
    showAlert('请输入优惠券码', 'warning');
    return false;
  }
  if (!form.discount || parseFloat(form.discount) <= 0 || parseFloat(form.discount) > 10) {
    showAlert('请输入有效的折扣（0-10）', 'warning');
    return false;
  }
  if (!form.minimumSpend || parseFloat(form.minimumSpend) < 0) {
    showAlert('请输入有效的最低消费金额', 'warning');
    return false;
  }
  if (!form.expireDate) {
    showAlert('请选择过期日期', 'warning');
    return false;
  }
  return true;
};

const saveCoupon = async () => {
  const data = editingCoupon.value ? editFormData.value : formData.value;
  
  if (!validateForm(data, !!editingCoupon.value)) {
    return;
  }

  try {
    const url = editingCoupon.value 
      ? `/api/merchant/coupons/${editingCoupon.value.id}`
      : `/api/merchant/coupons`;
    const method = editingCoupon.value ? 'put' : 'post';
    const submitData = { ...data };
    if (submitData.targetMemberLevel === '') submitData.targetMemberLevel = null;
    if (submitData.minConsumption === '' || submitData.minConsumption === null) submitData.minConsumption = null;

    await api[method](url, submitData);

    await fetchCoupons();
    if (editingCoupon.value) {
      showEditModal.value = false;
      showToastMessage('优惠券更新成功');
    } else {
      showAddModal.value = false;
      showToastMessage('优惠券添加成功');
    }
  } catch (error) {
    console.error('保存优惠券失败:', error);
    showAlert('保存优惠券失败，请稍后重试', 'error');
  }
};

const issueCoupon = async () => {
  if (!issuingCoupon.value) return;

  try {
    const response = await api.post(`/api/merchant/coupons/${issuingCoupon.value.id}/issue`);
    showIssueModal.value = false;
    showToastMessage(response.data.message || '优惠券发放成功');
  } catch (error) {
    console.error('发放优惠券失败:', error);
    showAlert('发放优惠券失败，请稍后重试', 'error');
  }
};

const deleteCoupon = (couponId) => {
  showConfirm('确定要删除这个优惠券吗？', async () => {
    try {
      await api.delete(`/api/merchant/coupons/${couponId}`);
      await fetchCoupons();
      showToastMessage('优惠券删除成功');
    } catch (error) {
      console.error('删除优惠券失败:', error);
      showAlert('删除优惠券失败，请稍后重试', 'error');
    }
  });
};

const getConditionText = (coupon) => {
  const parts = [];
  if (coupon.targetMemberLevel) {
    parts.push(`会员等级: ${coupon.targetMemberLevel}`);
  }
  if (coupon.minConsumption && coupon.minConsumption > 0) {
    parts.push(`累计消费: ￥${coupon.minConsumption}`);
  }
  return parts.length > 0 ? parts.join(', ') : '无限制';
};

onMounted(async () => {
  await fetchCoupons();
});
</script>

<template>
  <div class="px-6 py-8 space-y-6 min-h-screen bg-slate-50">
    <header class="flex items-center justify-between">
      <h1 class="text-2xl font-black text-slate-800">优惠券管理</h1>
      <button
        @click="openAddModal"
        class="flex items-center gap-2 px-4 py-2 bg-orange-500 text-white rounded-xl font-bold hover:bg-orange-600 transition-colors"
      >
        <Plus :size="18" />
        发优惠券
      </button>
    </header>

    <div class="space-y-4">
      <div
        v-for="coupon in store.coupons"
        :key="coupon.id"
        class="bg-white rounded-2xl p-4 shadow-sm border border-slate-100 hover:shadow-md transition-shadow"
      >
        <div class="flex justify-between items-start">
          <div class="flex-1 min-w-0">
            <div class="flex items-center gap-2">
              <h3 class="font-bold text-slate-800 line-clamp-1">{{ coupon.code }}</h3>
              <span class="px-2 py-0.5 bg-green-100 text-green-600 rounded-full text-xs font-bold">
                {{ coupon.discount }}折
              </span>
              <span v-if="!coupon.isActive" class="px-2 py-0.5 bg-red-100 text-red-600 rounded-full text-xs font-bold">
                已失效
              </span>
            </div>
            <p v-if="coupon.description" class="text-xs text-slate-500 mt-1">{{ coupon.description }}</p>
            <div class="flex items-center gap-4 mt-2 text-sm text-slate-500">
              <span>最低消费: ￥{{ coupon.minimumSpend }}</span>
              <span>过期日期: {{ coupon.expireDate }}</span>
            </div>
            <div class="mt-2 text-xs text-orange-500">
              发放条件: {{ getConditionText(coupon) }}
            </div>
          </div>
        </div>
        <div class="flex gap-2 mt-4">
          <button
            @click="openIssueModal(coupon)"
            class="flex-1 py-2 bg-orange-50 text-orange-600 rounded-xl text-sm font-bold hover:bg-orange-100 transition-colors"
          >
            <Send :size="14" class="inline mr-1" />
            发放
          </button>
          <button
            @click="openEditModal(coupon)"
            class="flex-1 py-2 bg-blue-50 text-blue-600 rounded-xl text-sm font-bold hover:bg-blue-100 transition-colors"
          >
            编辑
          </button>
          <button
            @click="deleteCoupon(coupon.id)"
            class="flex-1 py-2 bg-red-50 text-red-600 rounded-xl text-sm font-bold hover:bg-red-100 transition-colors"
          >
            <Trash2 :size="14" class="inline mr-1" />
            删除
          </button>
        </div>
      </div>
    </div>

    <!-- 添加优惠券弹窗 -->
    <div
      v-if="showAddModal"
      class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4"
      @click.self="showAddModal = false"
    >
      <div class="bg-white w-full max-w-lg rounded-2xl p-6 space-y-4 max-h-[90vh] overflow-y-auto">
        <div class="flex items-center justify-between">
          <h2 class="text-xl font-bold text-slate-800">添加优惠券</h2>
          <button @click="showAddModal = false" class="p-2 hover:bg-slate-100 rounded-xl">
            <X :size="20" class="text-slate-500" />
          </button>
        </div>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1">优惠券码</label>
            <input
              v-model="formData.code"
              type="text"
              class="w-full px-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:border-blue-500"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1">优惠券描述</label>
            <input
              v-model="formData.description"
              type="text"
              class="w-full px-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:border-blue-500"
              placeholder="例如：新人专享"
            />
          </div>
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-slate-700 mb-1">折扣</label>
              <input
                v-model="formData.discount"
                type="number"
                step="0.1"
                class="w-full px-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:border-blue-500"
                placeholder="例如：8.5"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-slate-700 mb-1">最低消费</label>
              <input
                v-model="formData.minimumSpend"
                type="number"
                class="w-full px-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:border-blue-500"
              />
            </div>
          </div>
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1">过期日期</label>
            <input
              v-model="formData.expireDate"
              type="date"
              class="w-full px-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:border-blue-500"
            />
          </div>
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-slate-700 mb-1">指定会员等级</label>
              <select
                v-model="formData.targetMemberLevel"
                class="w-full px-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:border-blue-500"
              >
                <option value="">不限制</option>
                <option v-for="level in MEMBER_LEVELS" :key="level" :value="level">{{ level }}</option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium text-slate-700 mb-1">累计消费门槛</label>
              <input
                v-model="formData.minConsumption"
                type="number"
                class="w-full px-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:border-blue-500"
                placeholder="不限制则留空"
              />
            </div>
          </div>
        </div>
        <div class="flex gap-3">
          <button
            @click="showAddModal = false"
            class="flex-1 py-3 bg-slate-100 text-slate-600 rounded-xl font-bold hover:bg-slate-200 transition-colors"
          >
            取消
          </button>
          <button
            @click="saveCoupon"
            class="flex-1 py-3 bg-orange-500 text-white rounded-xl font-bold hover:bg-orange-600 transition-colors"
          >
            添加优惠券
          </button>
        </div>
      </div>
    </div>

    <!-- 编辑优惠券弹窗 -->
    <div
      v-if="showEditModal"
      class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4"
      @click.self="showEditModal = false"
    >
      <div class="bg-white w-full max-w-lg rounded-2xl p-6 space-y-4 max-h-[90vh] overflow-y-auto">
        <div class="flex items-center justify-between">
          <h2 class="text-xl font-bold text-slate-800">编辑优惠券</h2>
          <button @click="showEditModal = false" class="p-2 hover:bg-slate-100 rounded-xl">
            <X :size="20" class="text-slate-500" />
          </button>
        </div>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1">优惠券码</label>
            <input
              v-model="editFormData.code"
              type="text"
              class="w-full px-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:border-blue-500"
            />
          </div>
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-slate-700 mb-1">折扣</label>
              <input
                v-model="editFormData.discount"
                type="number"
                step="0.1"
                class="w-full px-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:border-blue-500"
                placeholder="例如：8.5"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-slate-700 mb-1">最低消费</label>
              <input
                v-model="editFormData.minimumSpend"
                type="number"
                class="w-full px-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:border-blue-500"
              />
            </div>
          </div>
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1">过期日期</label>
            <input
              v-model="editFormData.expireDate"
              type="date"
              class="w-full px-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:border-blue-500"
            />
          </div>
        </div>
        <button
          @click="saveCoupon"
          class="w-full py-3 bg-blue-600 text-white rounded-xl font-bold hover:bg-blue-700 transition-colors"
        >
          保存修改
        </button>
      </div>
    </div>

    <!-- 发放优惠券弹窗 -->
    <div
      v-if="showIssueModal"
      class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4"
      @click.self="showIssueModal = false"
    >
      <div class="bg-white w-full max-w-lg rounded-2xl p-6 space-y-4 max-h-[90vh] overflow-y-auto">
        <div class="flex items-center justify-between">
          <h2 class="text-xl font-bold text-slate-800">发放优惠券</h2>
          <button @click="showIssueModal = false" class="p-2 hover:bg-slate-100 rounded-xl">
            <X :size="20" class="text-slate-500" />
          </button>
        </div>
        <div v-if="issuingCoupon" class="space-y-4">
          <div class="bg-orange-50 rounded-xl p-4">
            <p class="text-sm text-slate-600 mb-2">优惠券信息</p>
            <div class="flex items-center gap-2">
              <span class="font-bold text-orange-600">{{ issuingCoupon.code }}</span>
              <span class="px-2 py-0.5 bg-green-100 text-green-600 rounded-full text-xs font-bold">
                {{ issuingCoupon.discount }}折
              </span>
            </div>
            <p class="text-xs text-slate-500 mt-1">最低消费: ￥{{ issuingCoupon.minimumSpend }}</p>
            <p class="text-xs text-slate-500">过期日期: {{ issuingCoupon.expireDate }}</p>
          </div>
          <div class="bg-blue-50 rounded-xl p-4">
            <p class="text-sm text-blue-600 mb-2">发放条件</p>
            <p class="text-xs text-slate-600">{{ getConditionText(issuingCoupon) }}</p>
          </div>
          <div class="bg-green-50 rounded-xl p-4">
            <p class="text-sm text-green-600 mb-2">发放说明</p>
            <p class="text-xs text-slate-600">
              系统将根据优惠券的发放条件，自动向符合条件的用户发放此优惠券。
              符合条件的用户将在其优惠券列表中看到此券。
            </p>
          </div>
        </div>
        <div class="flex gap-3">
          <button
            @click="showIssueModal = false"
            class="flex-1 py-3 bg-slate-100 text-slate-600 rounded-xl font-bold hover:bg-slate-200 transition-colors"
          >
            取消
          </button>
          <button
            @click="issueCoupon"
            class="flex-1 py-3 bg-orange-500 text-white rounded-xl font-bold hover:bg-orange-600 transition-colors"
          >
            确认发放
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