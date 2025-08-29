# 数据库初始化脚本
Write-Host "=== 数据库初始化脚本 ===" -ForegroundColor Green

# 检查PostgreSQL是否安装
Write-Host "正在检查PostgreSQL..." -ForegroundColor Yellow

try {
    # 尝试使用psql命令
    $psqlPath = Get-Command psql -ErrorAction SilentlyContinue
    if ($psqlPath) {
        Write-Host "找到PostgreSQL客户端: $($psqlPath.Source)" -ForegroundColor Green
        
        # 执行数据库初始化
        Write-Host "正在执行数据库初始化..." -ForegroundColor Yellow
        
        $sqlFile = "src\main\resources\init_database.sql"
        if (Test-Path $sqlFile) {
            Write-Host "找到SQL文件: $sqlFile" -ForegroundColor Green
            
            # 构建psql命令
            $psqlCmd = "psql -h localhost -p 5434 -U postgres -d platform -f `"$sqlFile`""
            Write-Host "执行命令: $psqlCmd" -ForegroundColor Cyan
            
            # 提示用户输入密码
            Write-Host "请输入PostgreSQL密码 (951628437q): " -ForegroundColor Yellow -NoNewline
            $password = Read-Host
            
            # 设置环境变量
            $env:PGPASSWORD = $password
            
            # 执行SQL文件
            Invoke-Expression $psqlCmd
            
            Write-Host "数据库初始化完成！" -ForegroundColor Green
        } else {
            Write-Host "SQL文件不存在: $sqlFile" -ForegroundColor Red
        }
    } else {
        Write-Host "未找到PostgreSQL客户端，请手动执行以下步骤:" -ForegroundColor Red
        Write-Host ""
        Write-Host "1. 连接到你的PostgreSQL数据库:" -ForegroundColor Yellow
        Write-Host "   主机: localhost" -ForegroundColor White
        Write-Host "   端口: 5434" -ForegroundColor White
        Write-Host "   数据库: platform" -ForegroundColor White
        Write-Host "   用户名: postgres" -ForegroundColor White
        Write-Host "   密码: 951628437q" -ForegroundColor White
        Write-Host ""
        Write-Host "2. 执行SQL文件: src\main\resources\init_database.sql" -ForegroundColor Yellow
        Write-Host ""
        Write-Host "3. 或者手动执行以下SQL命令:" -ForegroundColor Yellow
        Write-Host "   - 删除组织相关表" -ForegroundColor White
        Write-Host "   - 创建tb_admin表" -ForegroundColor White
        Write-Host "   - 插入管理员账号: guanliyuan / 123456" -ForegroundColor White
    }
} catch {
    Write-Host "执行失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "=== 脚本执行完成 ===" -ForegroundColor Green 