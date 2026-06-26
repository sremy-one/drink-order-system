<script setup>
import { ref, watch, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { Lock, User, Store, ShieldCheck, AlertCircle, CheckCircle2, ArrowLeft } from 'lucide-vue-next';
import api from '../api/axios.js';

const router = useRouter();

const role = ref('customer');
const account = ref('');
const password = ref('');
const confirmPassword = ref('');
const nickname = ref('');
const shopName = ref('');
const phone = ref('');
const address = ref('');
const isLoading = ref(false);
const errors = ref({});
const errorMessage = ref('');
const showSuccess = ref(false);
const successMessage = ref('');

// 验证码相关
const captchaCode = ref('');
const captchaInput = ref('');
const captchaError = ref('');

const generateCaptcha = () => {
  captchaCode.value = Math.random().toString(36).substring(2, 6).toUpperCase();
  captchaError.value = '';
};

// 账号格式验证（邮箱格式）
const validateAccount = (acc) => {
  if (!acc || !acc.trim()) {
    return '请填写账号';
  }
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(acc)) {
    return '账号需为邮箱格式（如：example@qq.com）';
  }
  return null;
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

// 手机号格式验证
const validatePhone = (phoneNum) => {
  if (!phoneNum || !phoneNum.trim()) {
    return '请填写联系电话';
  }
  const phoneRegex = /^1[3-9]\d{9}$/;
  if (!phoneRegex.test(phoneNum)) {
    return '请输入有效的手机号（如：13800138000）';
  }
  return null;
};

const validateForm = () => {
  errors.value = {};
  let isValid = true;

  const accountError = validateAccount(account.value);
  if (accountError) {
    errors.value.account = accountError;
    isValid = false;
  }

  const passwordError = validatePassword(password.value);
  if (passwordError) {
    errors.value.password = passwordError;
    isValid = false;
  }

  if (!confirmPassword.value) {
    errors.value.confirmPassword = '请确认密码';
    isValid = false;
  } else if (confirmPassword.value !== password.value) {
    errors.value.confirmPassword = '两次输入的密码不一致';
    isValid = false;
  }

  if (role.value === 'customer') {
    if (!nickname.value.trim()) {
      errors.value.nickname = '请填写昵称';
      isValid = false;
    } else if (nickname.value.trim().length < 2) {
      errors.value.nickname = '昵称至少2个字符';
      isValid = false;
    } else if (nickname.value.trim().length > 20) {
      errors.value.nickname = '昵称最多20个字符';
      isValid = false;
    }
  }

  if (role.value === 'merchant') {
    if (!shopName.value.trim()) {
      errors.value.shopName = '请填写店铺名称';
      isValid = false;
    } else if (shopName.value.trim().length < 2) {
      errors.value.shopName = '店铺名称至少2个字符';
      isValid = false;
    } else if (shopName.value.trim().length > 50) {
      errors.value.shopName = '店铺名称最多50个字符';
      isValid = false;
    }

    const phoneError = validatePhone(phone.value);
    if (phoneError) {
      errors.value.phone = phoneError;
      isValid = false;
    }
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

const handleSubmit = async () => {
  errorMessage.value = '';
  captchaError.value = '';
  showSuccess.value = false;

  if (!validateForm()) {
    return;
  }

  isLoading.value = true;

  try {
    const response = await api.post('/api/auth/register', {
      email: account.value,
      password: password.value,
      name: role.value === 'customer' ? nickname.value : shopName.value,
      shopName: role.value === 'merchant' ? shopName.value : null,
      phone: role.value === 'merchant' ? phone.value : null,
      address: role.value === 'merchant' ? address.value : null,
      role: role.value
    });

    const userName = role.value === 'customer' ? nickname.value : shopName.value;
    const greetings = role.value === 'customer'
      ? `🎉 恭喜 ${userName}，注册成功！`
      : `🎊 恭喜 ${userName}，店铺注册成功！`;

    successMessage.value = greetings;
    showSuccess.value = true;

    setTimeout(() => {
      router.push('/login');
    }, 2000);
  } catch (error) {
    if (error.response && error.response.data) {
      const message = error.response.data.message || '注册失败';
      if (message.includes('账号') && message.includes('注册')) {
        errors.value.account = message;
      } else {
        errorMessage.value = message;
      }
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

const goBack = () => {
  router.push('/login');
};

// 切换角色时清除相关字段的错误
watch(role, () => {
  errors.value = {};
  errorMessage.value = '';
});

onMounted(() => {
  generateCaptcha();
});
</script>

<template>
  <div class="min-h-screen bg-[#121921] text-white flex flex-col font-sans transition-colors duration-700 overflow-hidden relative">
    <div
      class="absolute inset-0 pointer-events-none"
      style="background: radial-gradient(circle at 50% 40%, var(--glow-alpha, hsla(200, 90%, 65%, 0.15)) 0%, transparent 70%); opacity: 0.8;"
    />

    <main class="flex-1 flex items-center justify-center p-8 relative z-10">
      <div
        class="w-full max-w-md p-10 rounded-3xl bg-slate-800/70 backdrop-blur-xl border transition-all duration-500"
        style="border-color: var(--on-color, #38bdf8); box-shadow: 0 0 30px var(--glow-alpha, hsla(200, 90%, 65%, 0.15)); --on-color: #38bdf8; --glow-alpha: hsla(200, 90%, 65%, 0.15);"
      >
        <div class="space-y-8">
          <button
            @click="goBack"
            class="flex items-center gap-2 text-slate-500 hover:text-white transition-colors text-xs uppercase tracking-widest"
          >
            <ArrowLeft class="w-4 h-4" />
            返回登录
          </button>

          <header class="space-y-2 text-center">
            <h2 class="text-3xl font-light tracking-tight text-white transition-all duration-500">
              创建账号
            </h2>
            <p class="text-xs text-slate-500 uppercase tracking-widest bg-slate-900/50 py-1 px-3 rounded-full inline-block mx-auto">
              {{ role === 'customer' ? '顾客注册' : '商家注册' }}
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
              <Store class="w-4 h-4" />
              商家
            </button>
          </div>

          <form class="space-y-5" @submit.prevent="handleSubmit">
            <div v-if="errorMessage" class="p-3 bg-red-500/20 border border-red-500/50 rounded-xl text-red-400 text-sm text-center">
              {{ errorMessage }}
            </div>

            <transition name="slide-fade">
              <div v-if="showSuccess" class="p-4 bg-green-500/20 border border-green-500/50 rounded-xl text-green-400 text-sm text-center space-y-2">
                <CheckCircle2 class="w-8 h-8 mx-auto text-green-400" />
                <p class="font-medium">{{ successMessage }}</p>
                <p class="text-xs text-green-400/70">即将跳转到登录页...</p>
              </div>
            </transition>

            <transition name="slide-fade">
              <div v-if="role === 'customer' && !showSuccess" class="space-y-2">
                <label class="text-[10px] uppercase tracking-widest font-semibold text-slate-500 ml-1 flex items-center gap-2">
                  <User class="w-3 h-3" />
                  昵称
                </label>
                <input
                  v-model="nickname"
                  type="text"
                  placeholder="取一个你喜欢的昵称吧！"
                  maxlength="20"
                  :class="[
                    'w-full px-5 py-3.5 bg-slate-900/50 border rounded-xl text-slate-200 placeholder-slate-600 focus:outline-none transition-all',
                    errors.nickname ? 'border-red-500 focus:border-red-500' : 'border-slate-700 focus:border-[var(--on-color)]'
                  ]"
                />
                <p v-if="errors.nickname" class="text-red-400 text-xs mt-1 ml-1">{{ errors.nickname }}</p>
              </div>
            </transition>

            <transition name="slide-fade">
              <div v-if="role === 'merchant' && !showSuccess" class="space-y-2">
                <label class="text-[10px] uppercase tracking-widest font-semibold text-slate-500 ml-1 flex items-center gap-2">
                  <Store class="w-3 h-3" />
                  店铺名称
                </label>
                <input
                  v-model="shopName"
                  type="text"
                  placeholder="请输入店铺名称"
                  maxlength="50"
                  :class="[
                    'w-full px-5 py-3.5 bg-slate-900/50 border rounded-xl text-slate-200 placeholder-slate-600 focus:outline-none transition-all',
                    errors.shopName ? 'border-red-500 focus:border-red-500' : 'border-slate-700 focus:border-[var(--on-color)]'
                  ]"
                />
                <p v-if="errors.shopName" class="text-red-400 text-xs mt-1 ml-1">{{ errors.shopName }}</p>
              </div>
            </transition>

            <transition name="slide-fade">
              <div v-if="!showSuccess" class="space-y-2">
                <label class="text-[10px] uppercase tracking-widest font-semibold text-slate-500 ml-1 flex items-center gap-2">
                  <User class="w-3 h-3" />
                  账号
                </label>
                <input
                  v-model="account"
                  type="text"
                  placeholder="请输入邮箱账号（如：example@qq.com）"
                  :class="[
                    'w-full px-5 py-3.5 bg-slate-900/50 border rounded-xl text-slate-200 placeholder-slate-600 focus:outline-none transition-all',
                    errors.account ? 'border-red-500 focus:border-red-500' : 'border-slate-700 focus:border-[var(--on-color)]'
                  ]"
                />
                <p v-if="errors.account" class="text-red-400 text-xs mt-1 ml-1">{{ errors.account }}</p>
              </div>
            </transition>

            <transition name="slide-fade">
              <div v-if="!showSuccess" class="space-y-2">
                <label class="text-[10px] uppercase tracking-widest font-semibold text-slate-500 ml-1 flex items-center gap-2">
                  <Lock class="w-3 h-3" />
                  密码
                </label>
                <input
                  v-model="password"
                  type="password"
                  placeholder="••••••••"
                  :class="[
                    'w-full px-5 py-3.5 bg-slate-900/50 border rounded-xl text-slate-200 placeholder-slate-600 focus:outline-none transition-all',
                    errors.password ? 'border-red-500 focus:border-red-500' : 'border-slate-700 focus:border-[var(--on-color)]'
                  ]"
                />
                <div v-if="password" class="space-y-1">
                  <p v-if="errors.password" class="text-red-400 text-xs mt-1 ml-1 flex items-center gap-1">
                    <AlertCircle class="w-3 h-3" />
                    {{ errors.password }}
                  </p>
                  <div class="flex flex-wrap gap-x-4 gap-y-1 text-[10px] text-slate-500 ml-1">
                    <span :class="password.length >= 8 ? 'text-green-400' : ''">✓ 至少8字符</span>
                    <span :class="/[A-Z]/.test(password) ? 'text-green-400' : ''">✓ 大写字母</span>
                    <span :class="/[a-z]/.test(password) ? 'text-green-400' : ''">✓ 小写字母</span>
                    <span :class="/[0-9]/.test(password) ? 'text-green-400' : ''">✓ 数字</span>
                  </div>
                </div>
              </div>
            </transition>

            <transition name="slide-fade">
              <div v-if="!showSuccess" class="space-y-2">
                <label class="text-[10px] uppercase tracking-widest font-semibold text-slate-500 ml-1 flex items-center gap-2">
                  <Lock class="w-3 h-3" />
                  确认密码
                </label>
                <input
                  v-model="confirmPassword"
                  type="password"
                  placeholder="••••••••"
                  :class="[
                    'w-full px-5 py-3.5 bg-slate-900/50 border rounded-xl text-slate-200 placeholder-slate-600 focus:outline-none transition-all',
                    errors.confirmPassword ? 'border-red-500 focus:border-red-500' : 'border-slate-700 focus:border-[var(--on-color)]'
                  ]"
                />
                <p v-if="errors.confirmPassword" class="text-red-400 text-xs mt-1 ml-1">{{ errors.confirmPassword }}</p>
              </div>
            </transition>

            <transition name="slide-fade">
              <div v-if="role === 'merchant' && !showSuccess" class="space-y-2">
                <label class="text-[10px] uppercase tracking-widest font-semibold text-slate-500 ml-1 flex items-center gap-2">
                  <svg class="w-3 h-3" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"></path>
                  </svg>
                  联系电话
                </label>
                <input
                  v-model="phone"
                  type="tel"
                  placeholder="请输入手机号（如：13800138000）"
                  maxlength="11"
                  :class="[
                    'w-full px-5 py-3.5 bg-slate-900/50 border rounded-xl text-slate-200 placeholder-slate-600 focus:outline-none transition-all',
                    errors.phone ? 'border-red-500 focus:border-red-500' : 'border-slate-700 focus:border-[var(--on-color)]'
                  ]"
                />
                <p v-if="errors.phone" class="text-red-400 text-xs mt-1 ml-1">{{ errors.phone }}</p>
              </div>
            </transition>

            <transition name="slide-fade">
              <div v-if="role === 'merchant' && !showSuccess" class="space-y-2">
                <label class="text-[10px] uppercase tracking-widest font-semibold text-slate-500 ml-1 flex items-center gap-2">
                  <svg class="w-3 h-3" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path>
                    <circle cx="12" cy="10" r="3"></circle>
                  </svg>
                  店铺地址
                </label>
                <input
                  v-model="address"
                  type="text"
                  placeholder="请输入店铺地址（选填）"
                  class="w-full px-5 py-3.5 bg-slate-900/50 border border-slate-700 focus:border-[var(--on-color)] rounded-xl text-slate-200 placeholder-slate-600 focus:outline-none transition-all"
                />
              </div>
            </transition>

            <transition name="slide-fade">
              <div v-if="!showSuccess" class="space-y-2">
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
            </transition>

            <transition name="slide-fade">
              <button
                v-if="!showSuccess"
                type="submit"
                :disabled="isLoading"
                class="w-full py-4 rounded-xl font-bold text-sm uppercase tracking-[0.2em] transition-all hover:brightness-110 active:scale-[0.98] disabled:opacity-50 disabled:cursor-not-allowed"
                style="background-color: var(--on-color, #38bdf8); color: #0f172a; box-shadow: 0 4px 20px var(--glow-alpha, hsla(200, 90%, 65%, 0.15)); --on-color: #38bdf8;"
              >
                {{ isLoading ? '注册中...' : '注册' }}
              </button>
            </transition>

            <div v-if="!showSuccess" class="text-center pt-2">
              <span class="text-slate-500 text-xs">已有账号？</span>
              <router-link
                to="/login"
                class="text-xs font-semibold transition-colors hover:underline ml-1"
                style="color: var(--on-color, #38bdf8);"
              >
                立即登录
              </router-link>
            </div>
          </form>
        </div>
      </div>
    </main>
  </div>
</template>

<style scoped>
.slide-fade-enter-active,
.slide-fade-leave-active {
  transition: all 0.3s ease;
}

.slide-fade-enter-from,
.slide-fade-leave-to {
  transform: translateY(10px);
  opacity: 0;
}
</style>