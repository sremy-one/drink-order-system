<script setup>
import { ref, watch } from 'vue';
import { Lock, User, ShieldCheck, AlertCircle } from 'lucide-vue-next';
import { useRouter } from 'vue-router';
import api from '../api/axios.js';

const props = defineProps({
  hue: Number
});

const emit = defineEmits(['login']);

const router = useRouter();

const account = ref('');
const password = ref('');
const role = ref('customer');
const isLoading = ref(false);
const errors = ref({});
const errorMessage = ref('');

// 验证码相关
const captchaCode = ref('');
const captchaInput = ref('');
const captchaError = ref('');

const generateCaptcha = () => {
  captchaCode.value = Math.random().toString(36).substring(2, 6).toUpperCase();
  captchaError.value = '';
};

// 密码强度验证
const validatePassword = (pwd) => {
  if (!pwd) {
    return '请填写密码';
  }
  if (pwd.length < 8) {
    return '密码至少8个字符';
  }
  if (!/[A-Z]/.test(pwd)) {
    return '密码需包含大写字母';
  }
  if (!/[a-z]/.test(pwd)) {
    return '密码需包含小写字母';
  }
  if (!/[0-9]/.test(pwd)) {
    return '密码需包含数字';
  }
  return null;
};

const validateForm = () => {
  errors.value = {};
  let isValid = true;

  if (!account.value.trim()) {
    errors.value.account = '请填写账号';
    isValid = false;
  }

  const passwordError = validatePassword(password.value);
  if (passwordError) {
    errors.value.password = passwordError;
    isValid = false;
  }

  if (!captchaInput.value.trim()) {
    captchaError.value = '请填写验证码';
    isValid = false;
  } else if (captchaInput.value.toUpperCase() !== captchaCode.value) {
    captchaError.value = '验证码错误';
    isValid = false;
  }

  return isValid;
};

const resetForm = () => {
  account.value = '';
  password.value = '';
  errors.value = {};
  errorMessage.value = '';
  captchaInput.value = '';
};

const handleSubmit = async () => {
  errorMessage.value = '';
  captchaError.value = '';

  if (!validateForm()) {
    return;
  }

  isLoading.value = true;

  try {
    const response = await api.post('/api/auth/login', {
      email: account.value,
      password: password.value,
      role: role.value
    });

    const { user, token } = response.data;
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(user));
    emit('login', { user, token });
  } catch (error) {
    if (error.response && error.response.data) {
      errorMessage.value = error.response.data.message || '登录失败';
    } else if (error.request) {
      errorMessage.value = '无法连接服务器，请检查网络连接';
    } else {
      errorMessage.value = '发生意外错误，请重试';
    }
    generateCaptcha();
  } finally {
    isLoading.value = false;
  }
};

// 初始化验证码
generateCaptcha();

// 组件卸载时重置表单
watch(() => props.hue, () => {
  resetForm();
  generateCaptcha();
});
</script>

<template>
  <div
    class="w-full max-w-md p-10 rounded-3xl bg-slate-800/70 backdrop-blur-xl border transition-all duration-500"
    :style="{ borderColor: 'var(--on-color)', boxShadow: '0 0 30px var(--glow-alpha)' }"
  >
    <div class="space-y-8">
      <header class="space-y-2 text-center">
        <h2 class="text-3xl font-light tracking-tight text-white transition-all duration-500">
          欢迎回来
        </h2>
        <p class="text-xs text-slate-500 uppercase tracking-widest bg-slate-900/50 py-1 px-3 rounded-full inline-block mx-auto">
          {{ role === 'customer' ? '顾客登录' : '商家登录' }}
        </p>
      </header>

      <div class="flex gap-2 p-1 bg-slate-900/50 rounded-xl">
        <button
          type="button"
          @click="role = 'customer'"
          class="flex-1 py-2.5 rounded-lg text-xs font-semibold uppercase tracking-widest transition-all flex items-center justify-center gap-2"
          :class="role === 'customer' ? 'bg-slate-700 text-white' : 'text-slate-500 hover:text-slate-300'"
        >
          <User class="w-4 h-4" />
          顾客
        </button>
        <button
          type="button"
          @click="role = 'merchant'"
          class="flex-1 py-2.5 rounded-lg text-xs font-semibold uppercase tracking-widest transition-all flex items-center justify-center gap-2"
          :class="role === 'merchant' ? 'bg-slate-700 text-white' : 'text-slate-500 hover:text-slate-300'"
        >
          <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
            <polyline points="9 22 9 12 15 12 15 22"></polyline>
          </svg>
          商家
        </button>
      </div>

      <form class="space-y-6" @submit.prevent="handleSubmit">
        <!-- 错误提示 -->
        <div v-if="errorMessage" class="p-3 bg-red-500/20 border border-red-500/50 rounded-xl text-red-400 text-sm text-center">
          {{ errorMessage }}
        </div>

        <!-- 账号输入 -->
        <div class="space-y-2">
          <label class="text-[10px] uppercase tracking-widest font-semibold text-slate-500 ml-1 flex items-center gap-2">
            <User class="w-3 h-3" />
            账号
          </label>
          <input
            v-model="account"
            type="text"
            placeholder="请输入账号"
            :class="[
              'w-full px-5 py-3.5 bg-slate-900/50 border rounded-xl text-slate-200 placeholder-slate-600 focus:outline-none transition-all',
              errors.account ? 'border-red-500 focus:border-red-500' : 'border-slate-700 focus:border-[var(--on-color)]'
            ]"
          />
          <p v-if="errors.account" class="text-red-400 text-xs mt-1 ml-1">{{ errors.account }}</p>
        </div>

        <!-- 密码输入 -->
        <div class="space-y-2">
          <div class="flex justify-between items-center ml-1">
            <label class="text-[10px] uppercase tracking-widest font-semibold text-slate-500 flex items-center gap-2">
              <Lock class="w-3 h-3" />
              密码
            </label>
          </div>
          <input
            v-model="password"
            type="password"
            placeholder="••••••••"
            :class="[
              'w-full px-5 py-3.5 bg-slate-900/50 border rounded-xl text-slate-200 placeholder-slate-600 focus:outline-none transition-all',
              errors.password ? 'border-red-500 focus:border-red-500' : 'border-slate-700 focus:border-[var(--on-color)]'
            ]"
          />
          <p v-if="errors.password" class="text-red-400 text-xs mt-1 ml-1 flex items-center gap-1">
            <AlertCircle class="w-3 h-3" />
            {{ errors.password }}
          </p>
        </div>

        <!-- 验证码输入 -->
        <div class="space-y-2">
          <label class="text-[10px] uppercase tracking-widest font-semibold text-slate-500 ml-1 flex items-center gap-2">
            <ShieldCheck class="w-3 h-3" />
            验证码
          </label>
          <div class="flex gap-3">
            <input
              v-model="captchaInput"
              type="text"
              placeholder="请输入验证码"
              maxlength="4"
              :class="[
                'flex-1 px-5 py-3.5 bg-slate-900/50 border rounded-xl text-slate-200 placeholder-slate-600 focus:outline-none transition-all uppercase',
                captchaError ? 'border-red-500 focus:border-red-500' : 'border-slate-700 focus:border-[var(--on-color)]'
              ]"
            />
            <button
              type="button"
              @click="generateCaptcha"
              class="px-4 py-3.5 bg-slate-700 hover:bg-slate-600 border border-slate-600 rounded-xl text-slate-200 font-mono tracking-widest transition-all hover:brightness-110"
            >
              {{ captchaCode }}
            </button>
          </div>
          <p v-if="captchaError" class="text-red-400 text-xs mt-1 ml-1">{{ captchaError }}</p>
        </div>

        <!-- 提交按钮 -->
        <button
          type="submit"
          :disabled="isLoading"
          class="w-full py-4 rounded-xl font-bold text-sm uppercase tracking-[0.2em] transition-all hover:brightness-110 active:scale-[0.98] disabled:opacity-50 disabled:cursor-not-allowed"
          :style="{
            backgroundColor: 'var(--on-color)',
            color: '#0f172a',
            boxShadow: '0 4px 20px var(--glow-alpha)'
          }"
        >
          {{ isLoading ? '登录中...' : '登录' }}
        </button>

        <!-- 注册链接 -->
        <div class="text-center pt-2">
          <span class="text-slate-500 text-xs">没有账号？</span>
          <router-link
            to="/register"
            class="text-xs font-semibold transition-colors hover:underline ml-1"
            :style="{ color: 'var(--on-color)' }"
          >
            立即注册！
          </router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
</style>