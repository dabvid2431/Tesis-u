package com.tuempresa.stockapp.utils

import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.tuempresa.stockapp.R

// Aquí puedes agregar funciones de extensión útiles para tu proyecto
fun View.show() { this.visibility = View.VISIBLE }
fun View.hide() { this.visibility = View.GONE }

fun View.showSuccessFeedback(message: String) {
	Snackbar.make(this, message, Snackbar.LENGTH_SHORT)
		.setBackgroundTint(ContextCompat.getColor(context, R.color.colorSuccess))
		.setTextColor(ContextCompat.getColor(context, R.color.colorOnSuccess))
		.show()
}

fun View.showErrorFeedback(message: String) {
	Snackbar.make(this, message, Snackbar.LENGTH_LONG)
		.setBackgroundTint(ContextCompat.getColor(context, R.color.colorError))
		.setTextColor(ContextCompat.getColor(context, R.color.colorOnError))
		.show()
}
