Write-Output "Instalando dependencias del backend..."
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$repoDir = Join-Path $scriptDir '..' | Resolve-Path | Select-Object -ExpandProperty Path
Push-Location -Path (Join-Path $repoDir 'stock_backend')
npm install
Pop-Location
Write-Output "¡Listo! Para arrancar el servidor: cd stock_backend; npm start"