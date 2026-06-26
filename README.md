# 饮品点单系统 (Drink Order System)

一个功能完整的饮品在线点单系统，支持用户点单、商家管理、优惠券、积分兑换等功能。

## 项目结构

```
drink-order-system/
├── backend/          # 后端服务 (Spring Boot)
├── frontend/         # 前端应用 (Vue 3)
└── uploads/          # 上传的图片资源
```

## 技术栈

### 后端
- **Java 17** - 开发语言
- **Spring Boot 3.2.0** - 应用框架
- **Spring Security** - 安全认证
- **MyBatis 3.0.3** - ORM 框架
- **MySQL** - 数据库
- **JWT (jjwt 0.11.5)** - 身份认证
- **Lombok** - 简化代码

### 前端
- **Vue 3.5.13** - 前端框架
- **Vue Router 4.6.4** - 路由管理
- **Pinia 3.0.4** - 状态管理
- **Vite 6.2.0** - 构建工具
- **Tailwind CSS 4.1.14** - CSS 框架
- **Axios** - HTTP 请求
- **Express** - 开发服务器
- **GSAP** - 动画库
- **D3.js** - 数据可视化
- **Lucide Vue Next** - 图标库

## 主要功能

### 用户端
- 用户注册/登录
- 浏览饮品菜单
- 购物车管理
- 在线下单
- 订单历史查看
- 个人中心管理
- 收货地址管理
- 优惠券领取与使用
- 积分商城兑换
- 活动浏览

### 商家端
- 商家登录
- 商品管理
- 订单管理
- 门店管理
- 优惠券管理
- 活动管理
- 销售统计
- 今日收入查看

## 快速开始

### 环境要求
- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Maven 3.6+

### 后端启动

```bash
cd backend

# 创建数据库
mysql -u root -p
CREATE DATABASE drink_order_system;

# 配置数据库连接
# 修改 src/main/resources/application.yml 中的数据库配置

# 运行项目
mvn spring-boot:run
```

后端服务将在 `http://localhost:3001` 启动。

### 前端启动

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端应用将在 `http://localhost:3000` 启动。

## API 接口

后端提供 RESTful API，主要接口包括：

- `/api/auth/*` - 认证相关
- `/api/drinks/*` - 饮品管理
- `/api/orders/*` - 订单管理
- `/api/users/*` - 用户管理
- `/api/coupons/*` - 优惠券管理
- `/api/merchant/*` - 商家管理
- `/api/points/*` - 积分相关

## 项目特色

- 响应式设计，支持多端适配
- JWT 身份认证，安全可靠
- 文件上传功能支持饮品图片管理
- 积分系统与优惠券系统联动
- 商家与用户双端支持
- 实时订单管理

## 许可证

MIT License
