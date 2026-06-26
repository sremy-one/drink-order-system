<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useStore } from '../store';
import { ChevronLeft, Camera, User, CheckCircle2 } from 'lucide-vue-next';
import Toast from '../components/Toast.vue';
import api from '../api/axios';

const router = useRouter();
const store = useStore();
const name = ref('');
const avatar = ref('');
const newAvatar = ref(null);

const showToast = ref(false);
const toastMessage = ref('');
const toastType = ref('success');

const API_BASE_URL = 'http://localhost:3001';

onMounted(async () => {
  if (!store.user) await store.fetchUser();
  name.value = store.user.name;
  avatar.value = store.user.avatar ? 'http://localhost:3001' + store.user.avatar : '';
});

const handleAvatarUpload = () => {
  const input = document.createElement('input');
  input.type = 'file';
  input.accept = 'image/*';
  input.onchange = async (e) => {
    const file = e.target.files[0];
    if (file) {
      newAvatar.value = file;
      const reader = new FileReader();
      reader.onload = (e) => {
        avatar.value = e.target.result;
      };
      reader.readAsDataURL(file);
    }
  };
  input.click();
};

const saveProfile = async () => {
  if (!name.value.trim()) {
    showToastMessage('昵称不能为空哦', 'error');
    return;
  }

  try {
    const formData = new FormData();
    formData.append('name', name.value);
    if (newAvatar.value) {
      formData.append('avatar', newAvatar.value);
    }

    const res = await api.put('/api/user/profile', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });

    if (res.status === 200) {
      await store.fetchUser();
      // 更新本地avatar变量，确保页面显示最新的头像
      avatar.value = store.user.avatar ? 'http://localhost:3001' + store.user.avatar : '';
      showToastMessage('基本资料已更新！');
      setTimeout(() => {
        router.back();
      }, 1000);
    } else {
      showToastMessage('更新失败，请稍后重试', 'error');
    }
  } catch (error) {
    console.error('更新资料失败:', error);
    showToastMessage('更新失败，请稍后重试', 'error');
  }
};

const showToastMessage = (message, type = 'success') => {
  toastMessage.value = message;
  toastType.value = type;
  showToast.value = true;
  setTimeout(() => {
    showToast.value = false;
  }, 3000);
};
</script>

<template>
  <div class="px-6 py-8 space-y-8 min-h-screen bg-slate-50">
    <header class="flex items-center gap-4">
      <button @click="router.back()" class="p-2 hover:bg-slate-100 rounded-full transition-colors">
        <ChevronLeft :size="24" />
      </button>
      <h1 class="text-2xl font-black text-slate-800">编辑资料 ✏️</h1>
    </header>

    <div class="flex flex-col items-center gap-6">
      <div class="relative group">
        <img :src="avatar" class="w-32 h-32 rounded-full border-4 border-white shadow-xl" />
        <button @click="handleAvatarUpload" class="absolute bottom-0 right-0 bg-orange-500 text-white p-3 rounded-full border-4 border-slate-50 shadow-lg active:scale-90 transition-transform">
          <Camera :size="20" />
        </button>
      </div>
      <p class="text-[10px] text-slate-400 font-bold uppercase tracking-widest">点击图标上传新头像</p>
    </div>

    <div class="space-y-6">
      <div class="space-y-2">
        <label class="px-2 text-xs font-black text-slate-400 uppercase tracking-widest">我的昵称</label>
        <div class="joy-card flex items-center gap-4 bg-white">
          <User :size="20" class="text-slate-300" />
          <input 
            v-model="name"
            type="text" 
            placeholder="请输入您的昵称" 
            class="flex-1 bg-transparent border-none focus:ring-0 text-slate-800 font-bold"
          />
        </div>
      </div>

      <div class="space-y-2">
        <label class="px-2 text-xs font-black text-slate-400 uppercase tracking-widest">会员等级</label>
        <div class="joy-card bg-slate-100/50 flex justify-between items-center opacity-60">
          <span class="text-slate-500 font-bold">黄金会员</span>
          <CheckCircle2 :size="18" class="text-green-500" />
        </div>
      </div>
    </div>

    <div class="pt-10">
      <button @click="saveProfile" class="w-full joy-btn-primary py-5 rounded-[2rem] text-lg font-black shadow-orange-100 italic">
        保存更改
      </button>
    </div>

    <Toast
      :show="showToast"
      :message="toastMessage"
      :type="toastType"
      @close="showToast = false"
    />
  </div>
</template>
