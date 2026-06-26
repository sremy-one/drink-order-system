import { createRouter, createWebHistory } from 'vue-router';
import Home from '../views/Home.vue';
import Order from '../views/Order.vue';
import OrderList from '../views/OrderList.vue';
import OrderDetail from '../views/OrderDetail.vue';
import Profile from '../views/Profile.vue';
import Coupons from '../views/Coupons.vue';
import MyCoupons from '../views/MyCoupons.vue';
import PointsExchange from '../views/PointsExchange.vue';
import Activities from '../views/Activities.vue';
import ExchangeHistory from '../views/ExchangeHistory.vue';
import Support from '../views/Support.vue';
import OnlineChat from '../views/OnlineChat.vue';
import Addresses from '../views/Addresses.vue';
import Balance from '../views/Balance.vue';
import Checkout from '../views/Checkout.vue';
import Privacy from '../views/Privacy.vue';
import Settings from '../views/Settings.vue';
import EditProfile from '../views/EditProfile.vue';
import DrinkHistory from '../views/DrinkHistory.vue';
import ConsumptionStats from '../views/ConsumptionStats.vue';
import Login from '../views/Login.vue';
import Register from '../views/Register.vue';
import TodayIncome from '../views/TodayIncome.vue';
import Reviews from '../views/Reviews.vue';
import ProductManagement from '../views/ProductManagement.vue';
import CouponManagement from '../views/CouponManagement.vue';
import PointsMall from '../views/PointsMall.vue';
import DrinkRanking from '../views/DrinkRanking.vue';
import ActivityManagement from '../views/ActivityManagement.vue';
import MerchantSelect from '../views/MerchantSelect.vue';

const routes = [
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  { path: '/', component: Home },
  { path: '/merchant-select', component: MerchantSelect },
  { path: '/order', component: Order },
  { path: '/checkout', component: Checkout },
  { path: '/orders', component: OrderList },
  { path: '/order/:id', component: OrderDetail },
  { path: '/profile', component: Profile },
  { path: '/coupons', component: Coupons },
  { path: '/my-coupons', component: MyCoupons },
  { path: '/consumption-stats', component: ConsumptionStats },
  { path: '/exchange', component: PointsExchange },
  { path: '/activities', component: Activities },
  { path: '/activity-management', component: ActivityManagement },
  { path: '/history', component: ExchangeHistory },
  { path: '/support', component: Support },
  { path: '/online-chat', component: OnlineChat },
  { path: '/addresses', component: Addresses },
  { path: '/balance', component: Balance },
  { path: '/privacy', component: Privacy },
  { path: '/settings', component: Settings },
  { path: '/edit-profile', component: EditProfile },
  { path: '/drink-history', component: DrinkHistory },
  { path: '/today-income', component: TodayIncome },
  { path: '/reviews', component: Reviews },
  { path: '/product-management', component: ProductManagement },
  { path: '/branch-management', component: () => import('../views/BranchManagement.vue') },
  { path: '/coupon-management', component: CouponManagement },
  { path: '/points-mall', component: PointsMall },
  { path: '/drink-ranking', component: DrinkRanking },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');
  const isLoginPage = to.path === '/login' || to.path === '/register';

  if (!token && !isLoginPage) {
    next('/login');
  } else if (token && isLoginPage) {
    next('/');
  } else {
    next();
  }
});

export default router;