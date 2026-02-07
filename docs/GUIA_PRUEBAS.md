# 🧪 Guía de Pruebas - Funcionalidades Avanzadas

**Fecha**: 6 de febrero de 2026  
**APK**: `app/build/outputs/apk/debug/app-debug.apk`

---

## 📱 Instalación

### Opción 1: Android Studio (Recomendado)
1. Abre Android Studio
2. File → Open → Selecciona carpeta `Tesis-u`
3. Espera a que sincronice (puede tardar 1-2 min)
4. Conecta un dispositivo Android o inicia un emulador
5. Click en ▶️ **Run 'app'** (botón verde)

### Opción 2: Instalación Manual
1. Copia `app-debug.apk` a tu teléfono
2. Abre el archivo APK en el teléfono
3. Si aparece "Fuente desconocida", permite la instalación
4. Instala y abre la app

---

## 🧪 Checklist de Pruebas

### ✅ **1. Exportación a PDF**

**Objetivo**: Generar reportes PDF de productos y ventas

#### Pasos:
1. **Backend activo**: Asegúrate que el backend esté corriendo
   ```bash
   cd stock_backend
   npm start
   ```

2. **En la app**:
   - Abre la app StockApp
   - Ve a **Productos** (o **Ventas**)
   - Menu (⋮) → **Exportar Reportes**
   - Tap en **"Exportar a PDF"**

3. **Verificar**:
   - Debería aparecer un mensaje: "PDF generado exitosamente"
   - Ubicación: `Documents/StockAppReports/Productos_YYYYMMDD_HHMMSS.pdf`
   - Abre el archivo en un visor PDF

**Resultado esperado**: ✅ PDF con tabla de productos/ventas formateada

---

### ✅ **2. Exportación a CSV/Excel**

**Objetivo**: Generar reportes CSV compatibles con Excel

#### Pasos:
1. En la app, ve a **Productos** o **Ventas**
2. Menu → **Exportar Reportes**
3. Tap en **"Exportar a CSV/Excel"**
4. Espera el mensaje de confirmación

#### Verificar:
- Archivo guardado en: `Documents/StockAppReports/Productos_YYYYMMDD_HHMMSS.csv`
- Abre el archivo con:
  - **Excel**: Doble clic o File → Open
  - **Google Sheets**: Upload → Open
  - **Notepad**: Ver datos en formato CSV

**Resultado esperado**: ✅ CSV con columnas: ID, Nombre, Descripción, Precio Compra, Precio Venta, Stock, etc.

---

### ✅ **3. Backup en la Nube (Firebase)**

**Objetivo**: Realizar backup de la base de datos en Firebase Storage

#### ⚠️ Requisito Previo:
**Debes configurar Firebase primero** (ver sección "Configuración Firebase" abajo)

#### Pasos:
1. En la app, ve a **Menu → Backup y Exportación**
2. Tap en **"Realizar Backup"**
3. Espera progreso (puede tardar 5-10 segundos)
4. Verifica mensaje: "Backup exitoso: stockdb_backup_YYYYMMDD_HHMMSS.db"

#### Verificar en Firebase Console:
1. Ve a [Firebase Console](https://console.firebase.google.com)
2. Selecciona tu proyecto
3. Storage → Files
4. Carpeta `backups/` → Verifica archivo `.db`

**Resultado esperado**: ✅ Archivo de backup subido a Firebase Storage

---

### ✅ **4. Restaurar desde Backup**

#### Pasos:
1. Menu → **Backup y Exportación**
2. Tap en **"Ver Backups Disponibles"**
3. Verifica lista de backups
4. Tap en **"Restaurar desde Backup"**
5. Selecciona un backup
6. Confirma restauración

**Resultado esperado**: ✅ Base de datos restaurada desde la nube

---

### ✅ **5. Cifrado de Datos**

**Objetivo**: Verificar que los datos sensibles están cifrados

#### Prueba:
1. Inicia sesión en la app (si hay login)
2. Los datos se guardan automáticamente cifrados
3. **Verificación técnica**:
   - Usa Device File Explorer en Android Studio
   - Navega a: `data/data/com.tuempresa.stockapp/shared_prefs/`
   - Abre `encrypted_user_prefs.xml`
   - Los valores deben estar cifrados (ilegibles)

**Resultado esperado**: ✅ SharedPreferences contiene datos cifrados en AES256-GCM

---

### ✅ **6. Accesibilidad WCAG 2.1**

**Objetivo**: Verificar soporte para usuarios con discapacidades

#### Activar TalkBack (Lector de Pantalla):
1. **En el dispositivo Android**:
   - Configuración → Accesibilidad → TalkBack → Activar
2. **Navega por la app**:
   - Toca cualquier elemento
   - TalkBack debe leer en voz alta la descripción
   - Ejemplo: "Botón Exportar a PDF"

#### Verificar Contraste:
- Todos los textos deben ser legibles
- Ratio mínimo de contraste: 4.5:1

#### Verificar Tamaños:
- Todos los botones deben tener mínimo 48dp de altura
- Áreas táctiles accesibles

**Resultado esperado**: ✅ App totalmente navegable con TalkBack

---

## 🔧 Configuración Firebase (Necesaria para Backup)

### Paso 1: Crear Proyecto Firebase
1. Ve a [Firebase Console](https://console.firebase.google.com)
2. Click en **"Agregar proyecto"**
3. Nombre: `StockApp` (o el que prefieras)
4. Desactiva Google Analytics (opcional)
5. Click en **"Crear proyecto"**

### Paso 2: Agregar App Android
1. En el proyecto Firebase, click en **Android** (ícono robot)
2. Package name: `com.tuempresa.stockapp`
3. App nickname: `StockApp`
4. Click en **"Registrar app"**

### Paso 3: Descargar google-services.json
1. Descarga `google-services.json`
2. **Reemplaza** el archivo en: `app/google-services.json`
3. Sincroniza el proyecto en Android Studio

### Paso 4: Habilitar Firebase Storage
1. En Firebase Console → **Storage**
2. Click en **"Comenzar"**
3. Modo: **Producción** (o pruebas)
4. Ubicación: `us-central` (o tu región)

### Paso 5: Configurar Reglas de Seguridad
En Storage → Rules, pega:

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /backups/{backup} {
      allow read, write: if request.auth != null;
    }
  }
}
```

### Paso 6: Recompilar App
```bash
.\gradlew.bat assembleDebug
```

---

## 📝 Permisos Necesarios

La app solicitará estos permisos en tiempo de ejecución:

1. **Almacenamiento** (para PDF/CSV):
   - WRITE_EXTERNAL_STORAGE (Android ≤ 10)
   - READ_MEDIA_* (Android ≥ 13)

2. **Internet** (para Firebase):
   - Ya configurado en AndroidManifest

3. **Acceso a Red**:
   - Para conectar con Firebase y backend

---

## 🐛 Solución de Problemas

### Problema: "No se puede exportar PDF"
**Solución**: 
- Verifica permisos de almacenamiento
- Configuración → Apps → StockApp → Permisos → Almacenamiento → Permitir

### Problema: "Error de conexión Firebase"
**Solución**:
- Verifica que `google-services.json` esté actualizado
- Habilita Firebase Storage en la consola
- Verifica conexión a internet

### Problema: "Base de datos vacía"
**Solución**:
- Asegúrate que el backend esté corriendo
- Verifica la URL en BuildConfig.BASE_URL
- Crea datos de prueba desde la app

### Problema: "App se cierra al exportar"
**Solución**:
- Verifica logs en Android Studio (Logcat)
- Puede ser falta de permisos
- Verifica que haya datos para exportar

---

## 📊 Checklist de Verificación

Marca ✅ cuando hayas probado cada funcionalidad:

- [ ] ✅ Instalación exitosa de la app
- [ ] ✅ Backend corriendo y conectado
- [ ] ✅ Exportar Productos a PDF
- [ ] ✅ Exportar Ventas a PDF
- [ ] ✅ Exportar Productos a CSV
- [ ] ✅ Exportar Ventas a CSV
- [ ] ✅ Abrir CSV en Excel
- [ ] ✅ Firebase configurado
- [ ] ✅ Realizar backup en la nube
- [ ] ✅ Listar backups disponibles
- [ ] ✅ Restaurar desde backup
- [ ] ✅ Verificar datos cifrados
- [ ] ✅ Probar accesibilidad con TalkBack
- [ ] ✅ Verificar contraste de colores
- [ ] ✅ Navegación completa accesible

---

## 📸 Capturas de Pantalla Sugeridas

Para tu documentación de tesis, toma capturas de:

1. ✅ Pantalla principal de la app
2. ✅ Lista de productos
3. ✅ Diálogo de exportación
4. ✅ PDF generado abierto
5. ✅ CSV abierto en Excel
6. ✅ Confirmación de backup
7. ✅ Lista de backups en Firebase Console
8. ✅ TalkBack activado navegando la app
9. ✅ Permisos solicitados
10. ✅ Reportes en carpeta del dispositivo

---

## 🎯 Resultados Esperados

### Funcionalidades Implementadas:
✅ **Interfaz basada en WCAG 2.1** - Content descriptions, contraste, navegación  
✅ **Notificaciones automáticas** - Backend implementado  
✅ **Informes PDF** - Generación completa de reportes  
✅ **Informes CSV/Excel** - Compatible con todas las hojas de cálculo  
✅ **Cifrado de datos** - AES256-GCM para SharedPreferences  
✅ **Backup en la nube** - Firebase Storage con restauración  

---

## 📞 Soporte

Si encuentras problemas:
1. Revisa los logs en Android Studio (Logcat)
2. Verifica configuración de Firebase
3. Asegúrate que el backend esté corriendo
4. Revisa permisos de la app

**Documentación adicional**:
- [docs/FUNCIONALIDADES_AVANZADAS.md](FUNCIONALIDADES_AVANZADAS.md)
- [docs/SOLUCION_ERROR_POI.md](SOLUCION_ERROR_POI.md)

---

**✅ Todas las funcionalidades del plan de tesis están implementadas y listas para probar**
