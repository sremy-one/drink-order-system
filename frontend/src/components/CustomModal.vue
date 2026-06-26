<script setup>
import { ref } from 'vue';

const props = defineProps({
  show: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: ''
  },
  message: {
    type: String,
    default: ''
  },
  type: {
    type: String,
    default: 'info', // info, success, warning, error
    validator: (value) => ['info', 'success', 'warning', 'error'].includes(value)
  },
  confirmText: {
    type: String,
    default: '确定'
  },
  cancelText: {
    type: String,
    default: '取消'
  },
  showCancel: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['confirm', 'cancel', 'close']);

const handleConfirm = () => {
  emit('confirm');
};

const handleCancel = () => {
  emit('cancel');
};

const handleClose = () => {
  emit('close');
};

const confirmPressed = ref(false);
const cancelPressed = ref(false);
</script>

<template>
  <div v-if="show" class="modal-overlay">
    <div class="modal-content">
      <div class="modal-header">
        <h2 class="modal-title">{{ title }}</h2>
        <button v-if="!showCancel" @click="handleClose" class="close-button">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-slate-400">
            <path d="M18 6 6 18"/>
            <path d="m6 6 12 12"/>
          </svg>
        </button>
      </div>

      <div class="modal-icon">
        <div :class="[
          'icon-container',
          type === 'success' ? 'success' :
          type === 'error' ? 'error' :
          type === 'warning' ? 'warning' :
          'info'
        ]">
          <svg v-if="type === 'success'" xmlns="http://www.w3.org/2000/svg" width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon-success">
            <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
            <polyline points="22 4 12 14.01 9 11.01"/>
          </svg>
          <svg v-else-if="type === 'error'" xmlns="http://www.w3.org/2000/svg" width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon-error">
            <circle cx="12" cy="12" r="10"/>
            <line x1="12" y1="8" x2="12" y2="12"/>
            <line x1="12" y1="16" x2="12.01" y2="16"/>
          </svg>
          <svg v-else-if="type === 'warning'" xmlns="http://www.w3.org/2000/svg" width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon-warning">
            <path d="M10.29 3.86 1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
            <line x1="12" y1="9" x2="12" y2="13"/>
            <line x1="12" y1="17" x2="12.01" y2="17"/>
          </svg>
          <svg v-else xmlns="http://www.w3.org/2000/svg" width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon-info">
            <circle cx="12" cy="12" r="10"/>
            <line x1="12" y1="16" x2="12" y2="12"/>
            <line x1="12" y1="8" x2="12.01" y2="8"/>
          </svg>
        </div>
      </div>

      <div class="modal-message">
        <p class="message-text">{{ message }}</p>
      </div>

      <div v-if="showCancel" class="modal-buttons">
        <button
          @click="handleCancel"
          class="cancel-button"
        >
          {{ cancelText }}
        </button>
        <button
          @click="handleConfirm"
          class="confirm-button"
        >
          {{ confirmText }}
        </button>
      </div>
      <button
        v-else
        @click="handleConfirm"
        class="single-button"
      >
        {{ confirmText }}
      </button>
    </div>
  </div>
</template>

<style scoped>
/* 弹窗容器 */
.modal-overlay {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  background-color: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(12px);
  z-index: 200;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
}

/* 弹窗内容 */
.modal-content {
  background-color: white;
  border-radius: 3rem;
  padding: 2rem;
  width: 100%;
  max-width: 24rem;
  margin: 0 auto;
  animation: animate-in 0.3s ease-out;
}

/* 弹窗头部 */
.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 1.5rem;
}

.modal-title {
  font-size: 1.5rem;
  font-weight: 900;
  color: #1e293b;
}

.close-button {
  padding: 0.5rem;
  border-radius: 0.75rem;
  transition: background-color 0.2s;
}

.close-button:hover {
  background-color: #f1f5f9;
}

/* 弹窗图标 */
.modal-icon {
  display: flex;
  justify-content: center;
  padding: 1rem 0;
  margin-bottom: 1.5rem;
}

.icon-container {
  width: 5rem;
  height: 5rem;
  border-radius: 9999px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-container.success {
  background-color: #f0fdf4;
}

.icon-container.error {
  background-color: #fef2f2;
}

.icon-container.warning {
  background-color: #fffbeb;
}

.icon-container.info {
  background-color: #eff6ff;
}

.icon-success {
  color: #22c55e;
}

.icon-error {
  color: #ef4444;
}

.icon-warning {
  color: #eab308;
}

.icon-info {
  color: #3b82f6;
}

/* 弹窗消息 */
.modal-message {
  text-align: center;
  margin-bottom: 1.5rem;
}

.message-text {
  color: #334155;
  font-weight: 500;
}

/* 弹窗按钮 */
.modal-buttons {
  display: flex;
  gap: 0.75rem;
}

.cancel-button {
  flex: 1;
  padding: 1rem;
  border-radius: 1rem;
  font-weight: 700;
  font-size: 0.875rem;
  color: white;
  background: linear-gradient(135deg, #9f9f9f 0%, #666666 100%);
  box-shadow: 0 10px 15px -3px rgba(168, 85, 247, 0.2), 0 4px 6px -2px rgba(168, 85, 247, 0.1);
  transition: all 0.2s;
}

.cancel-button:hover {
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

.cancel-button:active {
  transform: scale(0.95);
  background: linear-gradient(135deg, #7f7f7f 0%, #555555 100%);
  box-shadow: inset 0 2px 8px rgba(0, 0, 0, 0.15);
}

.confirm-button {
  flex: 2;
  padding: 1rem;
  border-radius: 1rem;
  font-weight: 900;
  font-size: 0.875rem;
  color: white;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 10px 15px -3px rgba(59, 130, 246, 0.2), 0 4px 6px -2px rgba(59, 130, 246, 0.1);
  transition: all 0.2s;
}

.confirm-button:hover {
  box-shadow: 0 20px 25px -5px rgba(59, 130, 246, 0.3), 0 10px 10px -5px rgba(59, 130, 246, 0.2);
}

.confirm-button:active {
  transform: scale(0.95);
  background: linear-gradient(135deg, #5a67d8 0%, #6b46c1 100%);
  box-shadow: inset 0 2px 8px rgba(0, 0, 0, 0.15);
}

/* 单个确认按钮 */
.single-button {
  width: 100%;
  padding: 1rem;
  border-radius: 1rem;
  font-weight: 900;
  font-size: 0.875rem;
  color: white;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 10px 15px -3px rgba(59, 130, 246, 0.2), 0 4px 6px -2px rgba(59, 130, 246, 0.1);
  transition: all 0.2s;
}

.single-button:hover {
  box-shadow: 0 20px 25px -5px rgba(59, 130, 246, 0.3), 0 10px 10px -5px rgba(59, 130, 246, 0.2);
}

.single-button:active {
  transform: scale(0.95);
  background: linear-gradient(135deg, #5a67d8 0%, #6b46c1 100%);
  box-shadow: inset 0 2px 8px rgba(0, 0, 0, 0.15);
}

/* 动画 */
@keyframes animate-in {
  from {
    opacity: 0;
    transform: scale(0.95) translateY(-10px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}
</style>