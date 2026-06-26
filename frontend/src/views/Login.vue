<script setup>
import { ref, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import Lamp from '../components/Lamp.vue';
import LoginForm from '../components/LoginForm.vue';

const router = useRouter();
const isOn = ref(false);
const hue = ref(200);
const author = ref("AI Studio Artist");
const date = ref(new Date().toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' }));

const toggleLamp = () => {
  isOn.value = !isOn.value;
  hue.value = Math.floor(Math.random() * 360);

  const audio = new Audio('https://assets.mixkit.co/active_storage/sfx/2571/2571-preview.mp3');
  audio.volume = 0.3;
  audio.play().catch(() => {});
};

const handleLogin = (data) => {
  const { user } = data;

  if (user.role === 'merchant') {
    router.push('/');
  } else {
    router.push('/');
  }
};

watch([isOn, hue], ([newOn, newHue]) => {
  document.documentElement.style.setProperty('--on', newOn ? '1' : '0');
  document.documentElement.style.setProperty('--shade-hue', newHue.toString());
  document.documentElement.style.setProperty('--on-color', `hsl(${newHue}, 90%, 65%)`);
  document.documentElement.style.setProperty('--glow-alpha', `hsla(${newHue}, 90%, 65%, 0.15)`);
}, { immediate: true });

onMounted(() => {
  document.documentElement.style.setProperty('--on', isOn.value ? '1' : '0');
  document.documentElement.style.setProperty('--shade-hue', hue.value.toString());

  const token = localStorage.getItem('token');
  if (token) {
    router.push('/');
  }
});
</script>

<template>
  <div class="min-h-screen bg-[#121921] text-white flex flex-col font-sans transition-colors duration-700 overflow-hidden relative">
    <div
      class="absolute inset-0 pointer-events-none transition-opacity duration-1000"
      :style="{
        background: `radial-gradient(circle at 50% 40%, var(--glow-alpha) 0%, transparent 70%)`,
        opacity: isOn ? 0.8 : 0
      }"
    />

    <main class="flex-1 flex flex-col lg:flex-row items-center justify-center gap-12 lg:gap-24 p-8 relative z-10">
      <div class="flex flex-col items-center">
        <Lamp :is-on="isOn" :hue="hue" @toggle="toggleLamp" />
        <span class="mt-4 text-[10px] font-mono tracking-[0.3em] text-slate-500 uppercase opacity-50">
          拉动灯绳开启登录
        </span>
      </div>

      <Transition
        enter-active-class="transform transition duration-500 ease-out"
        enter-from-class="opacity-0 scale-95 translate-y-4"
        enter-to-class="opacity-100 scale-100 translate-y-0"
        leave-active-class="transform transition duration-300 ease-in"
        leave-from-class="opacity-100 scale-100 translate-y-0"
        leave-to-class="opacity-0 scale-95 translate-y-4"
      >
        <LoginForm v-if="isOn" :hue="hue" @login="handleLogin" />
      </Transition>
    </main>

    <footer class="w-full px-12 py-8 flex flex-col md:flex-row justify-between items-center gap-4 text-[10px] font-mono uppercase tracking-[0.2em] text-slate-600 relative z-10 border-t border-white/5">
      <div>System Date: {{ date }}</div>
      <div class="flex items-center gap-2">
        <span>Designer Protocol:</span>
        <span class="text-slate-400">{{ author }}_v2</span>
      </div>
    </footer>
  </div>
</template>

<style>
</style>