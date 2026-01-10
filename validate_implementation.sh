#!/bin/bash
# Script de validación para verificar que los cambios implementados funcionan correctamente

echo "🔍 Validando implementación de tests y configuración..."
echo ""

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

ERRORS=0

# 1. Verificar que ClientViewModelTest existe
echo "1️⃣ Verificando ClientViewModelTest..."
if [ -f "app/src/test/java/com/tuempresa/stockapp/viewmodels/ClientViewModelTest.kt" ]; then
    echo -e "${GREEN}✅ ClientViewModelTest.kt existe${NC}"
else
    echo -e "${RED}❌ ClientViewModelTest.kt NO existe${NC}"
    ERRORS=$((ERRORS + 1))
fi

# 2. Verificar que SaleViewModelTest existe
echo "2️⃣ Verificando SaleViewModelTest..."
if [ -f "app/src/test/java/com/tuempresa/stockapp/viewmodels/SaleViewModelTest.kt" ]; then
    echo -e "${GREEN}✅ SaleViewModelTest.kt existe${NC}"
else
    echo -e "${RED}❌ SaleViewModelTest.kt NO existe${NC}"
    ERRORS=$((ERRORS + 1))
fi

# 3. Verificar sonar-project.properties
echo "3️⃣ Verificando sonar-project.properties..."
if [ -f "sonar-project.properties" ]; then
    echo -e "${GREEN}✅ sonar-project.properties existe${NC}"
    if grep -q "sonar.coverage.jacoco.xmlReportPaths" sonar-project.properties; then
        echo -e "${GREEN}✅ Configuración de JaCoCo encontrada${NC}"
    else
        echo -e "${YELLOW}⚠️  Configuración de JaCoCo no encontrada${NC}"
    fi
else
    echo -e "${RED}❌ sonar-project.properties NO existe${NC}"
    ERRORS=$((ERRORS + 1))
fi

# 4. Verificar CI workflow
echo "4️⃣ Verificando CI workflow..."
if [ -f ".github/workflows/ci.yml" ]; then
    echo -e "${GREEN}✅ CI workflow existe${NC}"
    if grep -q "jacocoTestReport" .github/workflows/ci.yml; then
        echo -e "${GREEN}✅ Generación de JaCoCo configurada en CI${NC}"
    else
        echo -e "${YELLOW}⚠️  Generación de JaCoCo no configurada en CI${NC}"
    fi
else
    echo -e "${RED}❌ CI workflow NO existe${NC}"
    ERRORS=$((ERRORS + 1))
fi

# 5. Verificar build.gradle.kts tiene JaCoCo
echo "5️⃣ Verificando configuración JaCoCo en build.gradle.kts..."
if [ -f "app/build.gradle.kts" ]; then
    if grep -q "jacoco" app/build.gradle.kts; then
        echo -e "${GREEN}✅ Plugin JaCoCo configurado${NC}"
    else
        echo -e "${RED}❌ Plugin JaCoCo NO configurado${NC}"
        ERRORS=$((ERRORS + 1))
    fi
else
    echo -e "${RED}❌ app/build.gradle.kts NO existe${NC}"
    ERRORS=$((ERRORS + 1))
fi

# 6. Verificar README sin conflictos
echo "6️⃣ Verificando README.md..."
if [ -f "README.md" ]; then
    if grep -q "<<<<<<< HEAD" README.md || grep -q ">>>>>>>" README.md; then
        echo -e "${RED}❌ README.md tiene conflictos de merge${NC}"
        ERRORS=$((ERRORS + 1))
    else
        echo -e "${GREEN}✅ README.md sin conflictos${NC}"
        if grep -q "Ejecutar tests" README.md; then
            echo -e "${GREEN}✅ README incluye sección de tests${NC}"
        else
            echo -e "${YELLOW}⚠️  README no incluye sección de tests${NC}"
        fi
    fi
else
    echo -e "${RED}❌ README.md NO existe${NC}"
    ERRORS=$((ERRORS + 1))
fi

# 7. Verificar que los repositorios usan Unit (no Void)
echo "7️⃣ Verificando uso de Unit en repositorios..."
VOID_COUNT=$(grep -r "Call<Void>" app/src/main/java/com/tuempresa/stockapp/repositories/ 2>/dev/null | wc -l)
if [ "$VOID_COUNT" -eq 0 ]; then
    echo -e "${GREEN}✅ Todos los repositorios usan Call<Unit> (correcto)${NC}"
else
    echo -e "${RED}❌ Se encontraron $VOID_COUNT usos de Call<Void> (debe ser Unit)${NC}"
    ERRORS=$((ERRORS + 1))
fi

# Resumen
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
if [ $ERRORS -eq 0 ]; then
    echo -e "${GREEN}✅ Validación completada: Todo correcto${NC}"
    echo ""
    echo "📋 Próximos pasos recomendados:"
    echo "   1. Ejecutar: ./gradlew :app:testDebugUnitTest"
    echo "   2. Generar reporte: ./gradlew :app:jacocoTestReport"
    echo "   3. Revisar cobertura: app/build/reports/jacoco/jacocoTestReport/html/index.html"
    echo "   4. Configurar secrets de Sonar en GitHub (opcional)"
    exit 0
else
    echo -e "${RED}❌ Validación completada: Se encontraron $ERRORS error(es)${NC}"
    echo ""
    echo "🔧 Revisa los errores arriba y corrígelos antes de continuar."
    exit 1
fi
