# 🎓 AI Word Assistant - 智能英语词汇伴侣

AI Word Assistant 是一款基于 **Vue 3 + Spring Boot** 构建的现代化、高颜值英语词汇学习工具。系统不仅提供了优雅的拟物玻璃态 (Glassmorphism) 视觉体验，还深度融合了大语言模型 (LLM)，为您配备了一位专属的 24 小时英语 AI 助教。

---

## ✨ 核心特性 (Features)

### 🎨 极致的 UI/UX 体验
- **Glassmorphism 拟物玻璃风**：全站采用现代化的磨砂玻璃质感设计，配合柔和的动态渐变背景。
- **原生级深色模式 (True Dark Mode)**：不仅仅是黑色遮罩！一键切换至 iOS 级别的暗色模式，所有UI组件、字体颜色、边框阴影均自适应重构，完美保护视力。
- **高自由度个性化**：支持用户自定义上传本地壁纸或使用 URL 链接作为全站背景，并持久化保存。
- **丝滑交互**：基于 Tailwind CSS 编写的 3D 卡片翻转、全局柔和色彩过渡、沉浸式的专注学习体验。

### 🧠 核心学习功能
- **沉浸式词汇卡片**：支持“列表”与“闪卡”双模式切换，附带真人纯正发音 (TTS) 和音标展示。
- **多词书管理系统**：内置大学核心词汇，支持用户自由上传 JSON 格式的自定义专属词书。
- **学习进度追踪**：科学分类单词为“未学”、“陌生”、“熟练”，系统自动记录学习时间，并实时同步云端。

### 🤖 智能 AI 助教
- **无缝对话**：学习界面内置 AI 侧边栏，遇到不懂的例句、语法或词根词缀，可随时提问。
- **动态 Prompt**：管理员可在后台配置全局 AI 系统提示词 (System Prompt)，自定义助教的人设和教学风格。

### 🛡️ 管理后台
- **用户管理**：管理员专属面板，可查看系统内所有注册用户并进行权限管理。
- **API 动态配置**：支持在可视化界面中实时修改大模型（如 DeepSeek）的 API Key 和请求地址，无需重启后端服务。

---

## 🛠️ 技术栈 (Tech Stack)

### 前端 (Frontend)
- **Vue 3** (Composition API)
- **Vite** (Next Generation Frontend Tooling)
- **Tailwind CSS v3** (Utility-first CSS framework)
- **Vue Router** (SPA Routing)

### 后端 (Backend)
- **Java / Spring Boot**
- **Spring Data JPA / Hibernate**
- **MySQL** 数据库
- **Hutool / OkHttp** (LLM API Request)

---

## 📦 绿色版下载 (推荐普通用户)

无需安装 Java / Node / MySQL,下载即用:

1. 前往本仓库 [Releases](../../releases) 页面,下载 `AI-Word-Assistant-vX.X.X-win64.zip`
2. 解压到任意目录,双击 **启动.bat**
3. 浏览器自动打开 `http://localhost:8081`,开始学习!

> 绿色版内置精简 JRE 与 H2 文件数据库,数据保存在解压目录的 `data` 文件夹中,
> 备份/迁移/删除都只需操作该文件夹。

---

## 🚀 快速启动 (Getting Started)

### 1. 环境要求
- Node.js >= 16.x
- Java JDK >= 17
- MySQL >= 8.0

### 2. 数据库配置
1. 在 MySQL 中创建名为 `word_assistant` 的数据库：
   ```sql
   CREATE DATABASE IF NOT EXISTS `word_assistant` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
2. 执行根目录下的 `database.sql` 初始化表结构。
3. 修改后端 `application.properties` 中的数据库账号和密码。

### 3. 启动后端 (Spring Boot)
1. 进入 `backend` 目录。
2. 使用 Maven 安装依赖并启动项目：
   ```bash
   mvn spring-boot:run
   ```
3. 后端服务将默认运行在 `http://localhost:8081`(配置见 `backend/src/main/resources/application.yml`)。

> 💡 **没有 MySQL?** 使用内置 H2 数据库的绿色模式运行后端:
> ```bash
> mvn clean package -DskipTests
> java -jar backend/target/wordassistant-0.0.1-SNAPSHOT.jar --spring.profiles.active=standalone
> ```
> 数据将保存在运行目录的 `data` 文件夹中。

### 4. 启动前端 (Vue 3)
1. 进入 `frontend` 目录。
2. 安装依赖并启动开发服务器：
   ```bash
   npm install
   npm run dev
   ```
3. 访问终端中输出的地址（通常为 `http://localhost:5173`）。

---

## 🛠️ 一键打包 (Packaging)

在项目根目录执行:

```powershell
powershell -ExecutionPolicy Bypass -File build.ps1
```

脚本会依次完成: 前端 `vite build` → 产物嵌入后端 `static` 资源 → `mvn package`,
最终得到**内嵌前端的单 JAR**(`backend/target/wordassistant-0.0.1-SNAPSHOT.jar`)。
配合 `--spring.profiles.active=standalone` 参数即可在无 MySQL 环境下运行。

---

## 👨‍💻 用户指南

- **普通用户**：点击首页直接注册账号即可进入专属学习空间。
- **管理员账号**：系统默认拦截账号名为 `admin` 的注册，注册即自动授予 `ADMIN` 权限。使用 `admin` 登录后，顶部导航栏会出现“后台管理”入口。

---

## 📜 许可证 (License)

本项目采用 MIT License。欢迎自由学习、修改与分享！
