package com.tuempresa.stockapp.utils

import android.content.Context
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat

/**
 * Utilidades para mejorar la accesibilidad según WCAG 2.1
 * Proporciona funciones helper para hacer la app accesible para usuarios con discapacidades
 */
object AccessibilityHelper {
    
    /**
     * Verifica si el modo de accesibilidad está activo
     */
    fun isAccessibilityEnabled(context: Context): Boolean {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        return am.isEnabled && am.isTouchExplorationEnabled
    }
    
    /**
     * Configura content description para una vista con información de producto
     */
    fun setProductAccessibility(view: View, name: String, price: Double, stock: Int) {
        view.contentDescription = "Producto: $name. Precio: $${String.format("%.2f", price)}. Stock disponible: $stock unidades"
        ViewCompat.setImportantForAccessibility(view, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES)
    }
    
    /**
     * Configura content description para una imagen
     */
    fun setImageAccessibility(imageView: ImageView, description: String) {
        imageView.contentDescription = description
        ViewCompat.setImportantForAccessibility(imageView, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES)
    }
    
    /**
     * Configura un botón con descripción accesible
     */
    fun setButtonAccessibility(button: View, action: String) {
        button.contentDescription = action
        ViewCompat.setImportantForAccessibility(button, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES)
    }
    
    /**
     * Configura un EditText con hint accesible
     */
    fun setEditTextAccessibility(editText: EditText, label: String, hint: String? = null) {
        editText.contentDescription = label
        if (hint != null) {
            editText.hint = hint
        }
        ViewCompat.setImportantForAccessibility(editText, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES)
    }
    
    /**
     * Anuncia un mensaje a través del lector de pantalla
     */
    fun announceForAccessibility(view: View, message: String) {
        view.announceForAccessibility(message)
    }
    
    /**
     * Envía un evento de accesibilidad personalizado
     */
    fun sendAccessibilityEvent(context: Context, view: View, eventType: Int, text: String) {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        if (am.isEnabled) {
            val event = AccessibilityEvent.obtain(eventType)
            event.text.add(text)
            view.sendAccessibilityEventUnchecked(event)
        }
    }
    
    /**
     * Configura una vista como encabezado para navegación estructurada
     */
    fun setAsHeading(view: TextView, level: Int = 1) {
        ViewCompat.setAccessibilityDelegate(view, object : androidx.core.view.AccessibilityDelegateCompat() {
            override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(host, info)
                info.isHeading = true
            }
        })
    }
    
    /**
     * Agrupa vistas relacionadas para accesibilidad
     */
    fun groupRelatedViews(parentView: View, childViews: List<View>, groupDescription: String) {
        parentView.contentDescription = groupDescription
        childViews.forEach { child ->
            ViewCompat.setImportantForAccessibility(child, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO)
        }
        ViewCompat.setImportantForAccessibility(parentView, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES)
    }
    
    /**
     * Configura accesibilidad para una tarjeta de ítem en lista
     */
    fun setListItemAccessibility(
        itemView: View,
        position: Int,
        totalItems: Int,
        itemDescription: String
    ) {
        itemView.contentDescription = "Elemento ${position + 1} de $totalItems. $itemDescription"
        ViewCompat.setImportantForAccessibility(itemView, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES)
    }
    
    /**
     * Configura accesibilidad para botones de acción (editar, eliminar, etc.)
     */
    fun setActionButtonAccessibility(button: View, action: String, targetName: String) {
        button.contentDescription = "$action $targetName"
        ViewCompat.setImportantForAccessibility(button, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES)
    }
    
    /**
     * Configura una vista para que sea clickeable con descripción clara
     */
    fun setClickableAccessibility(view: View, description: String, action: String) {
        view.contentDescription = "$description. Toque dos veces para $action"
        view.isClickable = true
        view.isFocusable = true
        ViewCompat.setImportantForAccessibility(view, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES)
    }
    
    /**
     * Verifica si un texto tiene suficiente contraste según WCAG 2.1 AA
     * Ratio mínimo requerido: 4.5:1 para texto normal, 3:1 para texto grande
     */
    fun hasAccessibleContrast(foregroundColor: Int, backgroundColor: Int, isLargeText: Boolean = false): Boolean {
        val ratio = calculateContrastRatio(foregroundColor, backgroundColor)
        return if (isLargeText) {
            ratio >= 3.0 // WCAG AA para texto grande
        } else {
            ratio >= 4.5 // WCAG AA para texto normal
        }
    }
    
    /**
     * Calcula el ratio de contraste entre dos colores
     */
    private fun calculateContrastRatio(color1: Int, color2: Int): Double {
        val l1 = calculateRelativeLuminance(color1)
        val l2 = calculateRelativeLuminance(color2)
        
        val lighter = maxOf(l1, l2)
        val darker = minOf(l1, l2)
        
        return (lighter + 0.05) / (darker + 0.05)
    }
    
    /**
     * Calcula la luminancia relativa de un color según WCAG
     */
    private fun calculateRelativeLuminance(color: Int): Double {
        val r = android.graphics.Color.red(color) / 255.0
        val g = android.graphics.Color.green(color) / 255.0
        val b = android.graphics.Color.blue(color) / 255.0
        
        val rsRGB = if (r <= 0.03928) r / 12.92 else Math.pow((r + 0.055) / 1.055, 2.4)
        val gsRGB = if (g <= 0.03928) g / 12.92 else Math.pow((g + 0.055) / 1.055, 2.4)
        val bsRGB = if (b <= 0.03928) b / 12.92 else Math.pow((b + 0.055) / 1.055, 2.4)
        
        return 0.2126 * rsRGB + 0.7152 * gsRGB + 0.0722 * bsRGB
    }
    
    /**
     * Solicita foco en una vista específica para accesibilidad
     */
    fun requestAccessibilityFocus(view: View) {
        view.post {
            view.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
        }
    }
}
