$ports = @(6969, 8088, 5173, 5000, 5005, 8000, 5001, 5002)
foreach ($port in $ports) {
    $connections = Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue
    if ($connections) {
        foreach ($conn in $connections) {
            $processId = $conn.OwningProcess
            if ($processId -ne 0 -and $processId -ne 4) {
                Write-Host "Killing process $processId on port $port"
                Stop-Process -Id $processId -Force -ErrorAction SilentlyContinue
            }
        }
    }
}
Write-Host "Project services stopped."
