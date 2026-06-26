<script setup>
import { ref, onMounted, onUnmounted, computed, watch } from 'vue';
import { gsap } from 'gsap';
import { Draggable } from 'gsap/Draggable';

gsap.registerPlugin(Draggable);

const props = defineProps({
  isOn: Boolean,
  hue: Number
});

const emit = defineEmits(['toggle']);

const knobRef = ref(null);
const springY = ref(0);

// Transform springY into a path string for a springy curve effect
const cordPath = computed(() => {
  const baseLength = 80;
  const peakY = baseLength + springY.value;
  // We add a slight curvature as it's pulled
  const curveAmount = springY.value * 0.1;
  return `M 0 0 Q ${curveAmount} ${peakY / 2} 0 ${peakY}`;
});

let draggableInstance = null;

onMounted(() => {
  if (!knobRef.value) return;

  const draggable = Draggable.create(knobRef.value, {
    type: 'y',
    bounds: { minY: 0, maxY: 150 },
    onDrag: function() {
      springY.value = this.y;
    },
    onRelease: function() {
      if (this.y > 50) {
        emit('toggle');
      }
      // Animate snap back
      gsap.to(this.target, {
        y: 0,
        duration: 0.6,
        ease: 'elastic.out(1, 0.3)',
        onUpdate: () => {
          springY.value = gsap.getProperty(this.target, 'y');
        }
      });
    }
  });

  draggableInstance = draggable[0];
});

onUnmounted(() => {
  if (draggableInstance) draggableInstance.kill();
});
</script>

<template>
  <div class="relative w-64 h-96 flex items-center justify-center">
    <svg
      viewBox="-100 -50 200 400"
      class="w-full h-full drop-shadow-2xl"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
    >
      <!-- Light Beam -->
      <path
        class="transition-all duration-700"
        :style="{
          opacity: isOn ? 0.4 : 0,
          transform: isOn ? 'scale(1)' : 'scale(0.8)',
          'transform-origin': 'center 80px'
        }"
        :d="isOn 
          ? 'M -120 350 L 120 350 L 40 80 L -40 80 Z' 
          : 'M -20 120 L 20 120 L 15 80 L -15 80 Z'"
        fill="url(#beamGradientVue)"
      />

      <defs>
        <linearGradient id="beamGradientVue" x1="50%" y1="0%" x2="50%" y2="100%">
          <stop offset="0%" stop-color="var(--on-color)" stop-opacity="0.6"/>
          <stop offset="100%" stop-color="var(--on-color)" stop-opacity="0"/>
        </linearGradient>
      </defs>

      <!-- Lamp Shade -->
      <g transform="translate(0, 0)">
        <path
          d="M -50 80 L 50 80 C 60 80 65 70 60 20 C 55 -30 -55 -30 -60 20 C -65 70 -60 80 -50 80 Z"
          fill="#1e293b"
          stroke="#334155"
          stroke-width="2"
          class="transition-colors duration-500"
        />
        
        <!-- Accent Glow Line on Shade Rim -->
        <path 
          v-show="isOn"
          d="M -50 80 L 50 80" 
          stroke="var(--on-color)" 
          stroke-width="4" 
          stroke-linecap="round" 
          class="transition-opacity duration-500 drop-shadow-[0_0_8px_var(--on-color)]"
        />
        
        <!-- Eyes -->
        <g 
          class="transition-transform duration-500 origin-[0px_25px]"
          :style="{ transform: `rotate(${isOn ? 0 : 180}deg)` }"
        >
          <!-- Left Eye -->
          <circle cx="-15" cy="25" r="4" fill="white" />
          <circle cx="-15" cy="23" r="1.5" fill="black" />
          <!-- Right Eye -->
          <circle cx="15" cy="25" r="4" fill="white" />
          <circle cx="15" cy="23" r="1.5" fill="black" />
        </g>
      </g>

      <!-- Lamp Stem & Base -->
      <path d="M 0 80 L 0 350" stroke="#334155" stroke-width="8" stroke-linecap="round" />
      <ellipse cx="0" cy="350" rx="60" ry="15" fill="#1e293b" stroke="#334155" stroke-width="2" />
      <path 
        :style="{ opacity: isOn ? 0.4 : 0 }"
        d="M -40 350 L 40 350" 
        stroke="var(--on-color)" 
        stroke-width="4" 
        stroke-linecap="round"
        class="transition-opacity duration-500" 
      />

      <!-- Pull Cord -->
      <g transform="translate(35, 60)">
        <path
          :d="cordPath"
          stroke="#94A3B8"
          stroke-width="2"
          stroke-linecap="round"
        />
        <circle
          ref="knobRef"
          cx="0"
          :cy="80 + springY"
          r="8"
          fill="#F1F5F9"
          class="cursor-grab active:cursor-grabbing hover:fill-amber-400 transition-colors"
          style="touch-action: none"
        />
      </g>
    </svg>
    
    <!-- Glow highlight -->
    <Transition
      enter-active-class="transition duration-500"
      enter-from-class="opacity-0"
      enter-to-class="opacity-100"
      leave-active-class="transition duration-500"
      leave-from-class="opacity-100"
      leave-to-class="opacity-0"
    >
      <div
        v-if="isOn"
        class="absolute top-12 left-1/2 -translate-x-1/2 w-48 h-48 blur-3xl rounded-full pointer-events-none opacity-15"
        style="background-color: var(--on-color)"
      />
    </Transition>
  </div>
</template>
