# ============================================================
#  AI Word Assistant 一键打包脚本 (Windows)
#  产物: backend/target/wordassistant-0.0.1-SNAPSHOT.jar
#        (内嵌前端页面, 使用 --spring.profiles.active=standalone
#         以内置 H2 数据库运行, 无需 MySQL)
#  依赖: JDK 17+, Node.js 16+, Maven 3.8+
#  用法: 在项目根目录执行  powershell -ExecutionPolicy Bypass -File build.ps1
# ============================================================
$ErrorActionPreference = "Stop"
$root = $PSScriptRoot

Write-Host "[1/3] 构建前端 (vite build)..." -ForegroundColor Cyan
Push-Location "$root\frontend"
npm install
npm run build
Pop-Location

Write-Host "[2/3] 嵌入前端产物到后端 static..." -ForegroundColor Cyan
$dst = "$root\backend\src\main\resources\static"
if (Test-Path $dst) { Remove-Item -Recurse -Force $dst }
New-Item -ItemType Directory -Path $dst | Out-Null
Copy-Item -Recurse -Force "$root\frontend\dist\*" $dst

Write-Host "[3/3] 打包后端 JAR (mvn package)..." -ForegroundColor Cyan
Push-Location "$root\backend"
mvn clean package -DskipTests
Pop-Location

Write-Host ""
Write-Host "打包完成!" -ForegroundColor Green
Write-Host "绿色版运行: java -jar backend\target\wordassistant-0.0.1-SNAPSHOT.jar --spring.profiles.active=standalone"
Write-Host "开发模式:   后端 mvn spring-boot:run (需本地 MySQL), 前端 npm run dev"
