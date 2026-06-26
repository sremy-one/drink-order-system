<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useStore } from '../store';
import { ChevronLeft, Bell, Smartphone, Trash2, Info, ChevronRight, X, LogOut } from 'lucide-vue-next';

const router = useRouter();
const store = useStore();
const showLogoutModal = ref(false);
const cancelPressed = ref(false);
const confirmPressed = ref(false);

const handleLogout = () => {
  showLogoutModal.value = true;
};

const confirmLogout = () => {
  store.logout();
  showLogoutModal.value = false;
  router.push('/login');
};

const cancelLogout = () => {
  showLogoutModal.value = false;
};
</script>

<template>
  <div class="px-6 py-8 space-y-8 min-h-screen bg-slate-50">
    <header class="flex items-center gap-4">
      <button @click="router.back()" class="p-2 hover:bg-slate-100 rounded-full transition-colors">
        <ChevronLeft :size="24" />
      </button>
      <h1 class="text-2xl font-black text-slate-800">系统设置 ⚙️</h1>
    </header>

    <div class="space-y-6">
      <div class="joy-card divide-y divide-slate-50 !p-0 overflow-hidden">
        <div class="p-5 flex items-center justify-between group cursor-pointer hover:bg-slate-50">
          <div class="flex items-center gap-4">
            <div class="bg-indigo-50 p-2.5 rounded-xl text-indigo-500"><Bell :size="20" /></div>
            <span class="font-bold text-slate-700">通知设置</span>
          </div>
          <ChevronRight :size="20" class="text-slate-300" />
        </div>
        <div class="p-5 flex items-center justify-between group cursor-pointer hover:bg-slate-50">
          <div class="flex items-center gap-4">
            <div class="bg-blue-50 p-2.5 rounded-xl text-blue-500"><Smartphone :size="20" /></div>
            <span class="font-bold text-slate-700">版本更新</span>
          </div>
          <div class="flex items-center gap-2">
            <div class="w-1.5 h-1.5 rounded-full bg-orange-500 animate-ping"></div>
            <span class="text-xs font-bold text-orange-500">New</span>
          </div>
        </div>
        <div @click="alert('清除成功！')" class="p-5 flex items-center justify-between group cursor-pointer hover:bg-slate-50">
          <div class="flex items-center gap-4">
            <div class="bg-red-50 p-2.5 rounded-xl text-red-500"><Trash2 :size="20" /></div>
            <span class="font-bold text-slate-700">清除缓存</span>
          </div>
          <span class="text-xs font-bold text-slate-300 tabular-nums">245.8 MB</span>
        </div>
      </div>

      <div class="joy-card divide-y divide-slate-50 !p-0">
        <div class="p-5 flex items-center justify-between group cursor-pointer hover:bg-slate-50">
          <div class="flex items-center gap-4">
            <div class="bg-slate-100 p-2.5 rounded-xl text-slate-500"><Info :size="20" /></div>
            <span class="font-bold text-slate-700">关于 BubbleJoy</span>
          </div>
          <ChevronRight :size="20" class="text-slate-300" />
        </div>
      </div>
    </div>

    <div class="pt-12 px-12">
      <button @click="handleLogout" class="w-full py-4 rounded-[2rem] border-2 border-red-100 text-red-500 font-black text-sm uppercase tracking-widest hover:bg-red-50 transition-colors">
        退出当前账户
      </button>
    </div>

    <div class="text-center">
      <p class="text-[10px] text-slate-300 font-mono tracking-widest uppercase">App ID: BJ-8892-0X11</p>
    </div>

    <!-- Logout Confirmation Modal -->
    <div v-if="showLogoutModal" class="fixed inset-0 bg-black/60 backdrop-blur-md z-[200] flex items-center justify-center p-8">
      <div class="bg-white rounded-[3rem] p-8 w-full max-w-sm space-y-6 animate-in zoom-in-95 duration-300">
        <div class="flex items-center justify-between">
          <h2 class="text-2xl font-black text-slate-800">退出登录</h2>
          <button @click="cancelLogout" class="p-2 hover:bg-slate-100 rounded-xl">
            <X :size="24" class="text-slate-400" />
          </button>
        </div>

        <div class="flex justify-center py-4">
          <div class="w-20 h-20 bg-red-50 rounded-full flex items-center justify-center">
            <LogOut :size="40" class="text-red-500" />
          </div>
        </div>

        <div class="text-center space-y-2">
          <p class="text-slate-700 font-medium">确定要退出当前账户吗？</p>
          <p class="text-slate-400 text-sm">退出后需要重新登录才能使用</p>
        </div>

        <div class="flex gap-3">
          <button
            @click="cancelLogout"
            @touchstart="cancelPressed = true"
            @touchend="cancelPressed = false"
            @mousedown="cancelPressed = true"
            @mouseup="cancelPressed = false"
            @mouseleave="cancelPressed = false"
            :class="[
              'flex-1 py-4 rounded-2xl font-bold text-sm transition-all duration-200',
              cancelPressed
                ? 'cancel-gradient-pressed text-white scale-95 shadow-inner'
                : 'cancel-gradient text-white shadow-lg shadow-purple-200 hover:shadow-xl hover:shadow-purple-300'
            ]"
          >
            取消
          </button>
          <button
            @click="confirmLogout"
            @touchstart="confirmPressed = true"
            @touchend="confirmPressed = false"
            @mousedown="confirmPressed = true"
            @mouseup="confirmPressed = false"
            @mouseleave="confirmPressed = false"
            :class="[
              'flex-2 py-4 rounded-2xl font-black text-sm transition-all duration-200 active:scale-95',
              confirmPressed
                ? 'logout-gradient-pressed text-white scale-95 shadow-lg'
                : 'logout-gradient text-white shadow-lg shadow-red-200 hover:shadow-xl hover:shadow-red-300'
            ]"
          >
            确认退出
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.logout-gradient {
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%);
}

.logout-gradient-pressed {
  background: linear-gradient(135deg, #FF5252 0%, #FF7029 100%);
}

.cancel-gradient {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.cancel-gradient-pressed {
  background: linear-gradient(135deg, #5a67d8 0%, #6b46c1 100%);
}

.shadow-inner {
  box-shadow: inset 0 2px 8px rgba(0, 0, 0, 0.15);
}
</style>
