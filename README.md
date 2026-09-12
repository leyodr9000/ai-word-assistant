# AI Word Assistant

一个用 Vue 3 + Spring Boot 做的背单词应用。功能围绕"背单词"这一件事:卡片翻面记忆、词书管理、学习进度记录,学习页右侧还有一个可以随时提问的 AI 助教(需要自己配 API Key)。

界面是玻璃拟态风格,支持深色模式和自定义背景,算是在练手过程中顺手打磨的部分。

## 下载绿色版

不想装环境的话,直接从 [Releases](../../releases) 下载 Windows 绿色版:

1. 下载 `AI-Word-Assistant-vX.X.X-win64.zip`,解压到任意目录
2. 双击 **启动.bat**,浏览器会自动打开 http://localhost:8081
3. 数据保存在解压目录的 `data` 文件夹,备份、迁移、删除都只动这个文件夹

绿色版内置精简 JRE 和 H2 数据库,不需要装 Java、Node.js 或 MySQL。

## 功能

- **单词学习**:列表 / 闪卡两种视图,3D 翻面,系统 TTS 发音(可选美音/英音、男/女声),支持搜索和乱序
- **隐藏熟词**:可以把已标熟的单词从列表(或列表+卡片)里整体藏起来,只背没掌握的;也可以只模糊释义
- **词书管理**:内置一份示例词书;支持导入 Excel 词书(表头:单元 | 单词 | 音标 | 释义),自动识别表头
- **进度同步**:单词按"未学 / 陌生 / 熟练"标记。登录用户的词书、进度、学习时长存在数据库里,换设备登录数据还在;游客模式的数据只存在浏览器里
- **学习时长**:只计有效学习时间——切走标签页或 2 分钟没操作会自动暂停,挂机不算时长
- **AI 助教**:学习页侧边栏随时提问,流式回答。管理员在后台配置 DeepSeek 等 OpenAI 兼容接口的 API Key 后可用
- **管理后台**:用户管理、真实数据统计(用户/词书/进度/登录记录)、AI 接口配置(Key 只存服务端,页面上看不到明文)

## 账号

- 系统内置管理员:`admin / admin123`(首次启动自动创建,登录后请在右上角 ⚙ 设置里修改密码)
- 普通用户注册即可使用;`admin` 是保留用户名,不可注册
- 游客不登录也能用,数据仅保存在浏览器本地

## 本地开发

环境:JDK 17+、Node.js 16+;开发模式需要本地 MySQL 8(连接配置在 `backend/src/main/resources/application.yml`)。

```sql
CREATE DATABASE IF NOT EXISTS word_assistant DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

```bash
# 后端 (端口 8081)
cd backend
mvn spring-boot:run

# 前端 (端口 5173, 已配置 /api 代理到后端)
cd frontend
npm install
npm run dev
```

没有 MySQL 也能跑:打包后用内置 H2 启动,数据落在运行目录的 `data` 文件夹:

```bash
mvn clean package -DskipTests
java -jar backend/target/wordassistant-0.0.1-SNAPSHOT.jar --spring.profiles.active=standalone
```

## 打包

```powershell
powershell -ExecutionPolicy Bypass -File build.ps1
```

脚本会依次执行前端构建、产物嵌入后端 `static`、Maven 打包,得到内嵌前端的单 JAR。
Release 里的绿色版 = 这个 JAR + jlink 裁剪的运行时 + 启动脚本。

## 许可证

MIT
