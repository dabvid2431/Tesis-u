# Script de validacion para Windows PowerShell
# Verifica que los cambios implementados funcionan correctamente

Write-Host "Validando implementacion de tests y configuracion..." -ForegroundColor Cyan
Write-Host ""

$errors = 0

# 1. Verificar que ClientViewModelTest existe
Write-Host "1. Verificando ClientViewModelTest..." -ForegroundColor Yellow
if (Test-Path "app\src\test\java\com\tuempresa\stockapp\viewmodels\ClientViewModelTest.kt") {
    Write-Host "  [OK] ClientViewModelTest.kt existe" -ForegroundColor Green
} else {
    Write-Host "  [ERROR] ClientViewModelTest.kt NO existe" -ForegroundColor Red
    $errors++
}

# 2. Verificar que SaleViewModelTest existe
Write-Host "2. Verificando SaleViewModelTest..." -ForegroundColor Yellow
if (Test-Path "app\src\test\java\com\tuempresa\stockapp\viewmodels\SaleViewModelTest.kt") {
    Write-Host "  [OK] SaleViewModelTest.kt existe" -ForegroundColor Green
} else {
    Write-Host "  [ERROR] SaleViewModelTest.kt NO existe" -ForegroundColor Red
    $errors++
}

# 3. Verificar sonar-project.properties
Write-Host "3. Verificando sonar-project.properties..." -ForegroundColor Yellow
if (Test-Path "sonar-project.properties") {
    Write-Host "  [OK] sonar-project.properties existe" -ForegroundColor Green
    $content = Get-Content "sonar-project.properties" -Raw
    if ($content -match "sonar\.coverage\.jacoco\.xmlReportPaths") {
        Write-Host "  [OK] Configuracion de JaCoCo encontrada" -ForegroundColor Green
    } else {
        Write-Host "  [WARN] Configuracion de JaCoCo no encontrada" -ForegroundColor Yellow
    }
} else {
    Write-Host "  [ERROR] sonar-project.properties NO existe" -ForegroundColor Red
    $errors++
}

# 4. Verificar CI workflow
Write-Host "4. Verificando CI workflow..." -ForegroundColor Yellow
if (Test-Path ".github\workflows\ci.yml") {
    Write-Host "  [OK] CI workflow existe" -ForegroundColor Green
    $content = Get-Content ".github\workflows\ci.yml" -Raw
    if ($content -match "jacocoTestReport") {
        Write-Host "  [OK] Generacion de JaCoCo configurada en CI" -ForegroundColor Green
    } else {
        Write-Host "  [WARN] Generacion de JaCoCo no configurada en CI" -ForegroundColor Yellow
    }
} else {
    Write-Host "  [ERROR] CI workflow NO existe" -ForegroundColor Red
    $errors++
}

# 5. Verificar build.gradle.kts tiene JaCoCo
Write-Host "5. Verificando configuracion JaCoCo en build.gradle.kts..." -ForegroundColor Yellow
if (Test-Path "app\build.gradle.kts") {
    $content = Get-Content "app\build.gradle.kts" -Raw
    if ($content -match "jacoco") {
        Write-Host "  [OK] Plugin JaCoCo configurado" -ForegroundColor Green
    } else {
        Write-Host "  [ERROR] Plugin JaCoCo NO configurado" -ForegroundColor Red
        $errors++
    }
} else {
    Write-Host "  [ERROR] app\build.gradle.kts NO existe" -ForegroundColor Red
    $errors++
}

# 6. Verificar README sin conflictos
Write-Host "6. Verificando README.md..." -ForegroundColor Yellow
if (Test-Path "README.md") {
    $content = Get-Content "README.md" -Raw
    if ($content -match "<<<<<<< HEAD" -or $content -match ">>>>>>>") {
        Write-Host "  [ERROR] README.md tiene conflictos de merge" -ForegroundColor Red
        $errors++
    } else {
        Write-Host "  [OK] README.md sin conflictos" -ForegroundColor Green
        if ($content -match "Ejecutar tests") {
            Write-Host "  [OK] README incluye seccion de tests" -ForegroundColor Green
        } else {
            Write-Host "  [WARN] README no incluye seccion de tests" -ForegroundColor Yellow
        }
    }
} else {
    Write-Host "  [ERROR] README.md NO existe" -ForegroundColor Red
    $errors++
}

# 7. Verificar que los repositorios usan Unit (no Void)
Write-Host "7. Verificando uso de Unit en repositorios..." -ForegroundColor Yellow
$voidFiles = Get-ChildItem -Path "app\src\main\java\com\tuempresa\stockapp\repositories\" -Filter "*.kt" -Recurse | Select-String -Pattern "Call<Void>" | Select-Object -ExpandProperty Path -Unique
if ($voidFiles.Count -eq 0) {
    Write-Host "  [OK] Todos los repositorios usan Call<Unit> (correcto)" -ForegroundColor Green
} else {
    Write-Host "  [ERROR] Se encontraron $($voidFiles.Count) archivo(s) con Call<Void> (debe ser Unit)" -ForegroundColor Red
    $errors++
}

# Resumen
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
if ($errors -eq 0) {
    Write-Host "[OK] Validacion completada: Todo correcto" -ForegroundColor Green
    Write-Host ""
    Write-Host "Proximos pasos recomendados:" -ForegroundColor Cyan
    Write-Host "  1. Ejecutar: .\gradlew.bat :app:testDebugUnitTest"
    Write-Host "  2. Generar reporte: .\gradlew.bat :app:jacocoTestReport"
    Write-Host "  3. Revisar cobertura: app\build\reports\jacoco\jacocoTestReport\html\index.html"
    Write-Host "  4. Configurar secrets de Sonar en GitHub (opcional)"
    exit 0
} else {
    Write-Host "[ERROR] Validacion completada: Se encontraron $errors error(es)" -ForegroundColor Red
    Write-Host ""
    Write-Host "Revisa los errores arriba y corrijelos antes de continuar." -ForegroundColor Yellow
    exit 1
}