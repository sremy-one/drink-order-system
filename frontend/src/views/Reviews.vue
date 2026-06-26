<script setup>
import { ref, onMounted } from 'vue';
import { MessageSquare, Star, ArrowLeft, Reply, Send, CheckCircle } from 'lucide-vue-next';
import api from '../api/axios.js';

const isLoading = ref(true);
const reviews = ref([]);
const selectedReview = ref(null);
const replyText = ref('');
const isSubmitting = ref(false);

const fetchReviews = async () => {
  isLoading.value = true;
  try {
    const response = await api.get('/api/merchant/reviews');
    reviews.value = response.data || [];
  } catch (error) {
    console.error('获取评价数据失败:', error);
  } finally {
    isLoading.value = false;
  }
};

const openReply = (review) => {
  selectedReview.value = review;
  replyText.value = review.merchantReply || '';
};

const closeReply = () => {
  selectedReview.value = null;
  replyText.value = '';
};

const submitReply = async () => {
  if (!replyText.value.trim()) return;
  
  isSubmitting.value = true;
  try {
    await api.post(`/api/merchant/reviews/${selectedReview.value.id}/reply`, {
      reply: replyText.value
    });
    
    const index = reviews.value.findIndex(r => r.id === selectedReview.value.id);
    if (index !== -1) {
      reviews.value[index].merchantReply = replyText.value;
    }
    
    closeReply();
  } catch (error) {
    console.error('回复失败:', error);
  } finally {
    isSubmitting.value = false;
  }
};

onMounted(() => {
  fetchReviews();
});
</script>

<template>
  <div class="px-6 py-8 space-y-6 pb-32">
    <header class="flex items-center gap-4">
      <button @click="$router.back()" class="p-2 hover:bg-slate-100 rounded-xl transition-colors">
        <ArrowLeft :size="24" class="text-slate-600" />
      </button>
      <h1 class="text-2xl font-black text-slate-800">收到评价</h1>
    </header>

    <div v-if="isLoading" class="text-center py-12">
      <div class="inline-block animate-spin rounded-full h-8 w-8 border-4 border-slate-200 border-t-slate-600"></div>
    </div>

    <div v-else-if="reviews.length === 0" class="text-center py-12">
      <MessageSquare :size="64" class="mx-auto text-slate-300 mb-4" />
      <p class="text-slate-400">暂无评价数据</p>
    </div>

    <div v-else class="space-y-4">
      <div
        v-for="review in reviews"
        :key="review.id"
        class="bg-white p-5 rounded-2xl shadow-sm border border-slate-100"
      >
        <div class="flex items-start justify-between mb-3">
          <div class="flex items-center gap-3">
            <img :src="review.userAvatar" class="w-10 h-10 rounded-full" />
            <div>
              <h3 class="font-bold text-slate-800">{{ review.userName }}</h3>
              <div class="flex items-center gap-1 mt-1">
                <Star
                  v-for="i in 5"
                  :key="i"
                  :size="14"
                  :class="i <= review.rating ? 'fill-yellow-400 text-yellow-400' : 'text-slate-300'"
                />
              </div>
            </div>
          </div>
          <span class="text-xs text-slate-400">{{ review.createdAt }}</span>
        </div>

        <p class="text-slate-700 mb-3">{{ review.content }}</p>

        <div v-if="review.merchantReply" class="bg-blue-50 p-3 rounded-xl mb-3">
          <div class="flex items-center gap-2 text-blue-600 text-sm font-medium mb-1">
            <CheckCircle :size="14" />
            商家回复
          </div>
          <p class="text-slate-700 text-sm">{{ review.merchantReply }}</p>
        </div>

        <button
          @click="openReply(review)"
          class="flex items-center gap-2 text-sm text-slate-500 hover:text-blue-600 transition-colors"
        >
          <Reply :size="16" />
          {{ review.merchantReply ? '修改回复' : '回复' }}
        </button>
      </div>
    </div>

    <div
      v-if="selectedReview"
      class="fixed inset-0 bg-black/50 flex items-end justify-center z-50"
      @click.self="closeReply"
    >
      <div class="bg-white w-full max-w-lg rounded-t-3xl p-6 space-y-4">
        <div class="flex items-center justify-between">
          <h2 class="text-lg font-bold text-slate-800">回复评价</h2>
          <button @click="closeReply" class="p-2 hover:bg-slate-100 rounded-xl">
            <ArrowLeft :size="20" class="text-slate-500" />
          </button>
        </div>

        <div class="bg-slate-50 p-4 rounded-xl">
          <p class="text-sm text-slate-600">{{ selectedReview.content }}</p>
        </div>

        <textarea
          v-model="replyText"
          placeholder="请输入回复内容..."
          rows="4"
          class="w-full p-4 border border-slate-200 rounded-xl focus:outline-none focus:border-blue-500 resize-none"
        ></textarea>

        <button
          @click="submitReply"
          :disabled="isSubmitting || !replyText.trim()"
          class="w-full py-4 bg-blue-600 text-white rounded-xl font-bold hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
        >
          <Send :size="20" />
          {{ isSubmitting ? '发送中...' : '发送回复' }}
        </button>
      </div>
    </div>
  </div>
</template>