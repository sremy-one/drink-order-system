<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import * as d3 from 'd3';

const props = defineProps(['hue']);
const chartRef = ref(null);

const equation = ref('sin(x)');
const zoom = ref(1);

const drawChart = () => {
  if (!chartRef.value) return;
  const container = d3.select(chartRef.value);
  container.selectAll('*').remove();

  const width = chartRef.value.clientWidth;
  const height = 400;
  const margin = { top: 20, right: 20, bottom: 20, left: 20 };

  const svg = container.append('svg')
    .attr('width', width)
    .attr('height', height)
    .attr('class', 'overflow-visible');

  const x = d3.scaleLinear()
    .domain([-10 * zoom.value, 10 * zoom.value])
    .range([margin.left, width - margin.right]);

  const y = d3.scaleLinear()
    .domain([-5 * zoom.value, 5 * zoom.value])
    .range([height - margin.bottom, margin.top]);

  // Grid
  svg.append('g')
    .attr('class', 'grid opacity-10')
    .attr('transform', `translate(0,${y(0)})`)
    .call(d3.axisBottom(x).ticks(20).tickSize(-height).tickFormat(''));

  svg.append('g')
    .attr('class', 'grid opacity-10')
    .attr('transform', `translate(${x(0)},0)`)
    .call(d3.axisLeft(y).ticks(10).tickSize(-width).tickFormat(''));

  // Axes
  svg.append('line')
    .attr('x1', margin.left).attr('y1', y(0))
    .attr('x2', width - margin.right).attr('y2', y(0))
    .attr('stroke', 'rgba(255,255,255,0.2)').attr('stroke-width', 2);

  svg.append('line')
    .attr('x1', x(0)).attr('y1', margin.top)
    .attr('x2', x(0)).attr('y2', height - margin.bottom)
    .attr('stroke', 'rgba(255,255,255,0.2)').attr('stroke-width', 2);

  // Function Path
  const data = d3.range(-15, 15, 0.1);
  const line = d3.line()
    .x(d => x(d))
    .y(d => {
      try {
        if (equation.value === 'sin(x)') return y(Math.sin(d));
        if (equation.value === 'cos(x)') return y(Math.cos(d));
        if (equation.value === 'x²') return y(d * d);
        if (equation.value === 'tan(x)') return y(Math.tan(d));
        return y(0);
      } catch { return y(0); }
    })
    .curve(d3.curveBasis);

  const path = svg.append('path')
    .datum(data)
    .attr('fill', 'none')
    .attr('stroke', props.hue || '#38bdf8')
    .attr('stroke-width', 3)
    .attr('stroke-linecap', 'round')
    .attr('d', line)
    .attr('class', 'drop-shadow-[0_0_8px_rgba(56,189,248,0.8)]');

  // Animation
  const totalLength = path.node().getTotalLength();
  path
    .attr('stroke-dasharray', totalLength + ' ' + totalLength)
    .attr('stroke-dashoffset', totalLength)
    .transition()
    .duration(2000)
    .ease(d3.easeExpInOut)
    .attr('stroke-dashoffset', 0);
};

onMounted(() => {
  drawChart();
  window.addEventListener('resize', drawChart);
});

onUnmounted(() => {
  window.removeEventListener('resize', drawChart);
});

const setEquation = (eq) => {
  equation.value = eq;
  drawChart();
};
</script>

<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between mb-8">
      <div class="flex gap-2">
        <button 
          v-for="eq in ['sin(x)', 'cos(x)', 'x²', 'tan(x)']" 
          :key="eq"
          @click="setEquation(eq)"
          class="px-4 py-2 rounded-lg text-sm font-medium transition-all"
          :class="equation === eq ? 'bg-sky-500 text-white' : 'bg-white/5 hover:bg-white/10 text-slate-400'"
        >
          {{ eq }}
        </button>
      </div>
      <div class="flex items-center gap-4">
        <span class="text-xs uppercase font-bold text-slate-500">缩放层级</span>
        <input type="range" v-model.number="zoom" min="0.5" max="3" step="0.1" @input="drawChart" class="w-32 accent-sky-500" />
      </div>
    </div>

    <div class="glass-card p-6 min-h-[400px] relative overflow-hidden">
      <div ref="chartRef" class="w-full h-full"></div>
      
      <!-- Overlay Info -->
      <div class="absolute top-4 right-4 text-right space-y-1">
        <div class="text-[10px] font-mono text-slate-500 uppercase tracking-widest">Real-time Computation</div>
        <div class="text-sm font-bold text-white tabular-nums">P: {{ equation }}</div>
      </div>
    </div>
  </div>
</template>
