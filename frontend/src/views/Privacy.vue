<script setup>
import { useRouter } from 'vue-router';
import { ChevronLeft, ShieldCheck, Eye, EyeOff, Lock, UserCheck } from 'lucide-vue-next';

const router = useRouter();
</script>

<template>
  <div class="px-6 py-8 space-y-8 min-h-screen bg-white">
    <header class="flex items-center gap-4">
      <button @click="router.back()" class="p-2 hover:bg-slate-100 rounded-full transition-colors">
        <ChevronLeft :size="24" />
      </button>
      <h1 class="text-2xl font-black text-slate-800">隐私与权限 🛡️</h1>
    </header>

    <div class="space-y-8">
      <div class="space-y-4">
        <h3 class="px-2 text-xs font-black text-slate-400 uppercase tracking-widest">隐私设置</h3>
        <div class="joy-card divide-y divide-slate-50 !p-0">
          <div class="p-5 flex items-center justify-between">
            <div class="flex items-center gap-4">
              <div class="bg-indigo-50 p-2.5 rounded-xl text-indigo-500"><EyeOff :size="20" /></div>
              <div class="space-y-0.5">
                <span class="font-bold text-slate-700 block">隐藏我的消费动态</span>
                <span class="text-xs text-slate-400">不在好友圈展示您的购买记录</span>
              </div>
            </div>
            <div class="w-12 h-6 bg-orange-500 rounded-full relative">
               <div class="w-5 h-5 bg-white rounded-full absolute right-0.5 top-0.5 shadow-sm"></div>
            </div>
          </div>
          <div class="p-5 flex items-center justify-between">
            <div class="flex items-center gap-4">
              <div class="bg-blue-50 p-2.5 rounded-xl text-blue-500"><Lock :size="20" /></div>
              <div class="space-y-0.5">
                <span class="font-bold text-slate-700 block">登录设备管理</span>
                <span class="text-xs text-slate-400">当前共有 1 台设备在线</span>
              </div>
            </div>
            <button class="text-xs font-bold text-orange-500">查看详情</button>
          </div>
        </div>
      </div>

      <div class="space-y-4">
        <h3 class="px-2 text-xs font-black text-slate-400 uppercase tracking-widest">权限授权</h3>
        <div class="joy-card divide-y divide-slate-50 !p-0">
          <div class="p-5 flex items-center justify-between">
            <div class="flex items-center gap-4">
              <div class="bg-amber-50 p-2.5 rounded-xl text-amber-500"><UserCheck :size="20" /></div>
              <span class="font-bold text-slate-700">通讯录权限</span>
            </div>
            <span class="text-xs font-bold text-slate-300 uppercase tracking-widest">未开启</span>
          </div>
          <div class="p-5 flex items-center justify-between">
            <div class="flex items-center gap-4">
              <div class="bg-pink-50 p-2.5 rounded-xl text-pink-500"><ShieldCheck :size="20" /></div>
              <span class="font-bold text-slate-700">应用行为记录</span>
            </div>
            <span class="text-xs font-bold text-green-500 uppercase tracking-widest">已授权</span>
          </div>
        </div>
      </div>
    </div>

    <div class="p-6 bg-slate-50 rounded-3xl space-y-4">
      <h4 class="text-xs font-black text-slate-800 flex items-center gap-2">
         <ShieldCheck :size="16" />
         安全承诺
      </h4>
      <p class="text-xs text-slate-500 leading-relaxed">
        BubbleJoy 严格遵守《个人信息保护法》，我们承诺不会在未经您允许的情况下将您的信息分享给任何第三方广告商。
      </p>
    </div>
  </div>
</template>
