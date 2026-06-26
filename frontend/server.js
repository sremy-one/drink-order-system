import express from 'express';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// Set NODE_ENV if not set
if (!process.env.NODE_ENV) {
  process.env.NODE_ENV = 'development';
}

const app = express();
const PORT = 3000;

app.use(express.json());

// Mock Database
const db = {
  user: {
    name: 'Joy Traveler',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Joy',
    balance: 88.50,
    coupons: 3,
    points: 1250,
  },
  drinks: [
    { id: 1, name: '招牌丝袜奶茶', price: 18, category: '奶茶', image: 'https://images.unsplash.com/photo-1576092768241-dec231879fc3?q=80&w=400&fit=crop', description: '港式正宗，丝滑入心。' },
    { id: 2, name: '芝士莓莓', price: 28, category: '果茶', image: 'https://images.unsplash.com/photo-1546173159-315724a9369b?q=80&w=400&fit=crop', description: '浓郁芝士，酸甜莓果。' },
    { id: 3, name: '冰摇桃桃', price: 22, category: '果茶', image: 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?q=80&w=400&fit=crop', description: '粉嫩桃肉，清爽一夏。' },
    { id: 4, name: '生椰拿铁', price: 25, category: '咖啡', image: 'https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?q=80&w=400&fit=crop', description: '清甜椰奶与浓缩咖啡。' },
    { id: 5, name: '抹茶红豆冰', price: 20, category: '沙冰', image: 'https://images.unsplash.com/photo-1627435601361-ec25f5b1d0e5?q=80&w=400&fit=crop', description: '浓郁抹茶，软糯红豆。' },
  ],
  orders: [
    { id: '123456', type: '自取', status: '已完成', items: [{ name: '芝士莓莓', count: 1 }], total: 28, time: '2024-03-20 14:20' },
    { id: '123457', type: '外卖', status: '配送中', items: [{ name: '生椰拿铁', count: 2 }], total: 50, time: '2024-03-21 10:15' },
  ]
};

// API Routes
app.get('/api/user', (req, res) => res.json(db.user));
app.get('/api/drinks', (req, res) => res.json(db.drinks));
app.get('/api/orders', (req, res) => res.json(db.orders));

app.post('/api/orders', (req, res) => {
  const newOrder = {
    id: Math.random().toString(36).substr(2, 6).toUpperCase(),
    ...req.body,
    pickupCode: req.body.pickupCode || Math.floor(1000 + Math.random() * 9000).toString(),
    status: '制作中',
    time: new Date().toISOString().replace('T', ' ').substr(0, 16)
  };
  db.orders.unshift(newOrder);
  res.json(newOrder);
});

// Vite Middleware
if (process.env.NODE_ENV !== 'production') {
  const { createServer: createViteServer } = await import('vite');
  const vite = await createViteServer({
    server: { middlewareMode: true },
    appType: 'spa',
  });
  app.use(vite.middlewares);
} else {
  const distPath = path.join(process.cwd(), 'dist');
  app.use(express.static(distPath));
  app.get('*', (req, res) => {
    res.sendFile(path.join(distPath, 'index.html'));
  });
}

app.listen(PORT, '0.0.0.0', () => {
  console.log(`BubbleJoy server running at http://localhost:${PORT}`);
});
