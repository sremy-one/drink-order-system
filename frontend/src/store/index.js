import { defineStore } from 'pinia';
import api from '../api/axios.js';

export const useStore = defineStore('main', {
  state: () => ({
    user: null,
    drinks: [],
    orders: [],
    cart: [],
    rewards: [],
    coupons: [],
    pointsProducts: [],
    userCoupons: [],
    availableCoupons: [],
  }),
  getters: {
    cartTotal: (state) => state.cart.reduce((total, item) => total + item.price * item.count, 0),
    cartCount: (state) => state.cart.reduce((total, item) => total + item.count, 0),
  },
  actions: {
    async fetchUser() {
      try {
        const token = localStorage.getItem('token');
        if (!token) return;

        const res = await api.get('/api/auth/me');
        if (res.data) {
          this.user = res.data;
        }
      } catch (error) {
        console.error('Failed to fetch user:', error);
        throw error;
      }
    },
    async fetchDrinks(merchantId = null) {
      try {
        let url = '/api/drinks';
        if (merchantId) {
          url += `?merchantId=${merchantId}`;
        }
        const res = await api.get(url);
        if (res.data) {
          this.drinks = res.data;
        }
      } catch (error) {
        console.error('Failed to fetch drinks:', error);
      }
    },
    async fetchOrders(branchId = null) {
      try {
        const token = localStorage.getItem('token');
        if (!token) return;

        let url = '/api/orders';
        if (this.user && this.user.role !== 'merchant') {
          url += `?customerId=${this.user.id}`;
        } else if (branchId) {
          url += `?branchId=${branchId}`;
        }
        const res = await api.get(url);
        if (res.data) {
          this.orders = res.data;
        }
      } catch (error) {
        console.error('Failed to fetch orders:', error);
        throw error;
      }
    },
    async fetchRewards() {
      try {
        const token = localStorage.getItem('token');
        if (!token) return;

        const res = await api.get('/api/rewards');
        if (res.data) {
          this.rewards = res.data;
        }
      } catch (error) {
        console.error('Failed to fetch rewards:', error);
      }
    },
    async fetchCoupons() {
      try {
        const token = localStorage.getItem('token');
        if (!token) return;

        const res = await api.get('/api/merchant/coupons');
        if (res.data) {
          this.coupons = res.data;
        }
      } catch (error) {
        console.error('Failed to fetch coupons:', error);
        throw error;
      }
    },
    async fetchPointsProducts() {
      try {
        const token = localStorage.getItem('token');
        if (!token) return;

        const res = await api.get('/api/merchant/points-products');
        if (res.data) {
          this.pointsProducts = res.data;
        }
      } catch (error) {
        console.error('Failed to fetch points products:', error);
        throw error;
      }
    },
    async fetchUserCoupons() {
      try {
        const token = localStorage.getItem('token');
        if (!token || !this.user) return;

        const res = await api.get(`/api/user/coupons/my?userId=${this.user.id}`);
        if (res.data) {
          this.userCoupons = res.data;
        }
      } catch (error) {
        console.error('Failed to fetch user coupons:', error);
        throw error;
      }
    },
    async fetchAvailableCoupons() {
      try {
        const token = localStorage.getItem('token');
        if (!token || !this.user) return;

        const res = await api.get(`/api/user/coupons/available?userId=${this.user.id}`);
        if (res.data) {
          this.availableCoupons = res.data;
        }
      } catch (error) {
        console.error('Failed to fetch available coupons:', error);
        throw error;
      }
    },
    async claimCoupon(couponId) {
      try {
        const token = localStorage.getItem('token');
        if (!token || !this.user) return;

        const res = await api.post(`/api/user/coupons/claim?userId=${this.user.id}&couponId=${couponId}`);
        if (res.data) {
          await this.fetchUserCoupons();
          await this.fetchAvailableCoupons();
          return res.data;
        }
      } catch (error) {
        console.error('Failed to claim coupon:', error);
        throw error;
      }
    },
    addToCart(drink) {
      const existing = this.cart.find(item => item.id === drink.id);
      if (existing) {
        existing.count++;
      } else {
        this.cart.push({ ...drink, count: 1 });
      }
    },
    removeFromCart(drinkId) {
      const idx = this.cart.findIndex(item => item.id === drinkId);
      if (idx > -1) {
        if (this.cart[idx].count > 1) {
          this.cart[idx].count--;
        } else {
          this.cart.splice(idx, 1);
        }
      }
    },
    clearCart() {
      this.cart = [];
    },
    logout() {
      this.user = null;
      this.drinks = [];
      this.orders = [];
      this.cart = [];
      this.rewards = [];
      this.coupons = [];
      this.pointsProducts = [];
      this.userCoupons = [];
      this.availableCoupons = [];
      localStorage.removeItem('token');
      localStorage.removeItem('user');
    }
  }
});