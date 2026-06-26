<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ChevronLeft, Send, User, Bot } from 'lucide-vue-next';

const router = useRouter();
const messages = ref([
  {
    id: 1,
    content: '您好！欢迎来到在线客服，请问有什么可以帮助您的？',
    type: 'bot',
    time: '10:00'
  }
]);
const inputMessage = ref('');
const chatContainer = ref(null);

const sendMessage = () => {
  if (!inputMessage.value.trim()) return;
  
  // 添加用户消息
  messages.value.push({
    id: Date.now(),
    content: inputMessage.value,
    type: 'user',
    time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  });
  
  inputMessage.value = '';
  
  // 模拟机器人回复
  setTimeout(() => {
    messages.value.push({
      id: Date.now() + 1,
      content: '感谢您的咨询，我们的客服人员将尽快回复您！',
      type: 'bot',
      time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
    });
    scrollToBottom();
  }, 1000);
  
  scrollToBottom();
};

const scrollToBottom = () => {
  setTimeout(() => {
    if (chatContainer.value) {
      chatContainer.value.scrollTop = chatContainer.value.scrollHeight;
    }
  }, 100);
};

onMounted(() => {
  scrollToBottom();
});
</script>

<template>
  <div class="min-h-screen bg-slate-50 flex flex-col">
    <!-- 头部 -->
    <header class="bg-white px-6 py-4 flex items-center gap-4 shadow-sm">
      <button 
        @click="router.back()" 
        class="p-2 hover:bg-slate-100 rounded-full transition-colors"
      >
        <ChevronLeft :size="24" />
      </button>
      <div class="flex items-center gap-3">
        <div class="w-10 h-10 rounded-full bg-green-500 flex items-center justify-center">
          <User :size="20" class="text-white" />
        </div>
        <div>
          <h1 class="text-lg font-bold text-slate-800">在线客服</h1>
          <p class="text-xs text-green-500">在线</p>
        </div>
      </div>
    </header>

    <!-- 聊天内容 -->
    <div 
      ref="chatContainer"
      class="flex-1 px-6 py-4 overflow-y-auto space-y-4"
    >
      <div 
        v-for="msg in messages" 
        :key="msg.id"
        :class="[
          'flex',
          msg.type === 'user' ? 'justify-end' : 'justify-start'
        ]"
      >
        <div 
          :class="[
            'max-w-xs sm:max-w-md p-4 rounded-2xl',
            msg.type === 'user' 
              ? 'bg-orange-500 text-white rounded-br-sm' 
              : 'bg-white text-slate-800 rounded-bl-sm shadow-sm'
          ]"
        >
          <p class="text-sm leading-relaxed">{{ msg.content }}</p>
          <p 
            :class="[
              'text-xs mt-1 opacity-60',
              msg.type === 'user' ? 'text-white' : 'text-slate-400'
            ]"
          >
            {{ msg.time }}
          </p>
        </div>
      </div>
    </div>

    <!-- 输入框 -->
    <div class="bg-white px-6 py-4 border-t border-slate-100">
      <div class="flex items-center gap-3">
        <input 
          v-model="inputMessage"
          type="text" 
          placeholder="输入消息..." 
          class="flex-1 bg-slate-100 px-4 py-3 rounded-2xl border-none focus:ring-2 focus:ring-orange-500 focus:bg-white transition-colors text-sm"
          @keyup.enter="sendMessage"
        />
        <button 
          @click="sendMessage"
          class="w-12 h-12 bg-orange-500 rounded-full flex items-center justify-center text-white hover:bg-orange-600 transition-colors active:scale-95"
        >
          <Send :size="20" />
        </button>
      </div>
    </div>
  </div>
</template>
