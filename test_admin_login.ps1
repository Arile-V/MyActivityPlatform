# 测试管理员登录接口
$uri = "http://localhost:8079/admin/login"
$body = @{
    username = "guanliyuan"
    password = "123456"
} | ConvertTo-Json

Write-Host "测试管理员登录接口..."
Write-Host "URL: $uri"
Write-Host "请求体: $body"

try {
    $response = Invoke-RestMethod -Uri $uri -Method POST -ContentType "application/json" -Body $body
    Write-Host "响应成功: $($response | ConvertTo-Json -Depth 3)" -ForegroundColor Green
} catch {
    Write-Host "请求失败: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $statusCode = $_.Exception.Response.StatusCode
        $statusDescription = $_.Exception.Response.StatusDescription
        Write-Host "HTTP状态码: $statusCode - $statusDescription" -ForegroundColor Red
    }
} 