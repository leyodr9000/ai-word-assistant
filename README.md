# AI Word Assistant

背单词 Web 应用。后端 Spring Boot 3（Java 17），前端 Vue 3 + Vite，数据层支持 MySQL 与内置 H2 两种模式。学习页附带一个可选的 AI 助教（OpenAI 兼容接口，流式输出）。

---

## 适用范围

| 项 | 说明 |
|---|---|
| 部署形态 | 自托管。可打包为内嵌前端的单体 JAR，或生成带精简 JRE 的 Windows 绿色版 |
| 数据库 | 开发与多用户部署用 MySQL 8；单机免安装用 H2 文件库（`data/` 目录，可整体迁移） |
| 认证 | 用户名口令登录 + 游客模式（游客数据仅存浏览器） |
| AI 功能 | 需管理员在后台配置 API Key；未配置时该入口不可用，学习功能不受影响 |
| 定位 | 个人 / 小范围自用，未做多租户隔离与横向扩展设计 |

---

## 功能

- **单词学习**：列表与闪卡两种视图，3D 翻面；系统 TTS 发音（美音 / 英音，男 / 女声可选）；支持搜索与乱序。
- **熟词隐藏**：已标熟的词可从列表（或列表 + 卡片）整体隐藏，或仅模糊释义。
- **词书管理**：内置示例词书；支持导入 Excel（表头 `单元 | 单词 | 音标 | 释义`，自动识别表头）。
- **学习进度**：单词标记为未学 / 陌生 / 熟练。登录用户的书与进度存服务端，跨设备可用。
- **学习时长**：只计有效时间——切走标签页或 2 分钟无操作自动暂停。
- **AI 助教**：学习页侧栏问答，SSE 流式输出。
- **管理后台**：用户管理、数据统计（用户 / 词书 / 进度 / 登录记录）、AI 接口配置（Key 仅存服务端，界面不回显明文）。

---

## 快速开始

### 绿色版（免环境）

1. 从 Releases 下载 `AI-Word-Assistant-vX.X.X-win64.zip` 并解压。
2. 运行 `启动.bat`，浏览器打开 `http://localhost:8081`。
3. 数据位于解压目录的 `data/` 文件夹。

绿色版内置精简 JRE 与 H2，无需安装 Java、Node.js 或 MySQL。

### 本地开发

环境：JDK 17+、Node.js 16+、MySQL 8（开发模式）。

```sql
CREATE DATABASE IF NOT EXISTS word_assistant DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

数据库凭据通过环境变量注入，不写入仓库：

```bash
export DB_USERNAME=root
export DB_PASSWORD='<your-password>'
```

```bash
# 后端，端口 8081
cd backend && mvn spring-boot:run

# 前端，端口 5173，/api 已代理到后端
cd frontend && npm install && npm run dev
```

### 免 MySQL 运行

```bash
mvn clean package -DskipTests
java -jar backend/target/wordassistant-0.0.1-SNAPSHOT.jar --spring.profiles.active=standalone
```

standalone profile 使用 H2 文件库，数据落在运行目录的 `data/`。

---

## 账号

| 角色 | 说明 |
|---|---|
| 管理员 | 首次启动自动创建，用户名 `admin`，初始口令见下表 |
| 普通用户 | 开放注册；`admin` 为保留用户名，不可注册 |
| 游客 | 无需登录，数据仅存浏览器本地 |

> **部署到可被他人访问的环境前，必须先用管理员账号登录并修改初始口令。**
> 初始口令以明文硬编码在 `WordAssistantApplication` 的初始化逻辑中，属于首次引导用途，未做强制修改。该设计适用于本机自用；对外暴露前应改为部署时注入，或首次登录强制改密。

---

## 构建

```powershell
powershell -ExecutionPolicy Bypass -File build.ps1
```

脚本依次执行：前端构建 → 产物嵌入后端 `static` → Maven 打包，得到内嵌前端的单体 JAR。绿色版 = 该 JAR + jlink 裁剪运行时 + 启动脚本。

---

## 配置

`backend/src/main/resources/application.yml`：

| 键 | 说明 |
|---|---|
| `server.port` | 默认 8081 |
| `spring.datasource.url` | MySQL 连接串 |
| `spring.datasource.username` | `${DB_USERNAME:root}`，环境变量优先 |
| `spring.datasource.password` | `${DB_PASSWORD:}`，必须由环境变量提供 |
| `cors.allowed-origins` | 前端来源，开发默认 `http://localhost:5173` |

AI 接口（Base URL / API Key / 模型）由管理员在后台配置，存于数据库。

---

## 已知限制

- 默认管理员口令为硬编码引导值，未强制首登修改（见「账号」）。
- 开发态 `application.yml` 中 `ddl-auto: update` 与 `show-sql: true`，不适合生产。
- 未实现刷新令牌、口令强度校验与登录限流。
- 学习时长的暂停判定基于前端事件，浏览器后台节流会影响精度。
- Excel 导入依赖表头识别，列顺序变化需调整表头文字。

---

## 许可证

MIT
