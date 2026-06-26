<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { ChevronLeft, Headphones, Send, Phone, MessageSquare, HelpCircle } from 'lucide-vue-next';

const router = useRouter();
const query = ref('');
const showHotlineModal = ref(false);
const faq = [
  { q: '订单配送太慢了怎么办？', a: '您可以进入订单详情页点击“催单”，或拨打外卖骑手电话联系。' },
  { q: '可以修改已经下单的饮品吗？', a: '订单在“待接单”状态前可联系客服修改，接单后可能无法修改。' },
  { q: '积分什么时候到账？', a: '订单完成后，积分会在5-10分钟内自动充入您的账户。' },
];

const handleSend = () => {
  if (query.value.trim()) {
    alert('消息已发送至后台，客服将尽快回复！');
    query.value = '';
  }
};

const handleHotlineClick = () => {
  showHotlineModal.value = true;
};

const handleOnlineChat = () => {
  router.push('/online-chat');
};
</script>

<template>
  <div class="px-6 py-8 space-y-8 min-h-screen bg-white">
    <header class="flex items-center gap-4">
      <button @click="router.back()" class="p-2 hover:bg-slate-100 rounded-full transition-colors">
        <ChevronLeft :size="24" />
      </button>
      <h1 class="text-2xl font-black text-slate-800">联系客服 🎧</h1>
    </header>

    <!-- Quick Help -->
    <div class="grid grid-cols-2 gap-4">
      <div @click="handleHotlineClick" class="joy-card flex flex-col items-center gap-3 bg-blue-50/50 border-blue-100 cursor-pointer">
        <div class="w-12 h-12 rounded-2xl bg-blue-500 text-white flex items-center justify-center">
          <Phone :size="24" />
        </div>
        <span class="font-black text-blue-600 text-sm">拨服务热线</span>
      </div>
      <div @click="handleOnlineChat" class="joy-card flex flex-col items-center gap-3 bg-green-50/50 border-green-100 cursor-pointer">
        <div class="w-12 h-12 rounded-2xl bg-green-500 text-white flex items-center justify-center">
          <MessageSquare :size="24" />
        </div>
        <span class="font-black text-green-600 text-sm">在线文字对话</span>
      </div>
    </div>

    <!-- FAQ -->
    <div class="space-y-4">
      <div class="flex items-center gap-2 px-2 text-xs font-black text-slate-400 uppercase tracking-widest">
        <HelpCircle :size="14" />
        常见问题解答
      </div>
      <div class="space-y-3">
        <div 
          v-for="(item, idx) in faq" 
          :key="idx"
          class="p-4 rounded-2xl bg-slate-50 border border-slate-100"
        >
          <div class="font-bold text-slate-800 text-sm mb-1">Q: {{ item.q }}</div>
          <p class="text-xs text-slate-500 leading-relaxed">A: {{ item.a }}</p>
        </div>
      </div>
    </div>

    <!-- Message Section -->
    <div class="fixed bottom-0 left-0 right-0 p-6 bg-white border-t border-slate-50 z-50">
      <div class="flex items-center gap-2 bg-slate-100 p-2 rounded-[2rem]">
        <input 
          v-model="query"
          type="text" 
          placeholder="在这输入你想询问的事项..." 
          class="flex-1 bg-transparent px-4 py-2 border-none focus:ring-0 text-sm"
          @keyup.enter="handleSend"
        />
        <button 
          @click="handleSend"
          class="w-10 h-10 joy-gradient rounded-full flex items-center justify-center text-white active:scale-90 transition-transform"
        >
          <Send :size="18" />
        </button>
      </div>
    </div>

    <!-- 服务热线弹窗 -->
    <div 
      v-if="showHotlineModal" 
      class="fixed inset-0 bg-black/50 flex items-center justify-center z-50"
    >
      <div class="bg-white rounded-2xl p-6 max-w-sm w-full mx-4 text-center">
        <div class="w-16 h-16 rounded-full bg-orange-100 flex items-center justify-center mx-auto mb-4">
          <Phone :size="32" class="text-orange-500" />
        </div>
        <h3 class="text-xl font-bold text-slate-800 mb-2">服务热线</h3>
        <p class="text-slate-500 mb-6">400-123-4567</p>
        <div class="w-12 h-1 bg-slate-200 rounded-full mb-6"></div>
        <p class="text-sm text-slate-400 mb-6">该功能还未开发</p>
        <button 
          @click="showHotlineModal = false"
          class="w-full py-3 bg-orange-500 text-white rounded-xl font-bold hover:bg-orange-600 transition-colors"
        >
          知道了
        </button>
      </div>
    </div>
  </div>
</template>
