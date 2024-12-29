package com.ayberk.emarket.util

import android.graphics.Color
import android.view.View
import com.google.android.material.snackbar.Snackbar

object Snackbar {
    // Hata için Snackbar
    fun showErrorSnackbar(view: View, message: String, duration: Int = Snackbar.LENGTH_SHORT) {
        val snackbar = Snackbar.make(view, message, duration)
        snackbar.setBackgroundTint(Color.parseColor("#F44336"))
        snackbar.setTextColor(Color.WHITE)
        snackbar.setActionTextColor(Color.WHITE)
        snackbar.setAction("Kapat") { snackbar.dismiss() }
        snackbar.show()
    }

    // Başarı için Snackbar
    fun showSuccessSnackbar(view: View, message: String, duration: Int = Snackbar.LENGTH_SHORT) {
        val snackbar = Snackbar.make(view, message, duration)
        snackbar.setBackgroundTint(Color.parseColor("#4CAF50"))
        snackbar.setTextColor(Color.WHITE)
        snackbar.setActionTextColor(Color.WHITE)
        snackbar.setAction("Tamam") { snackbar.dismiss() }
        snackbar.show()
    }

    fun showAlreadyJoinedSnackbar(view: View, message: String, duration: Int = Snackbar.LENGTH_SHORT) {
        val snackbar = Snackbar.make(view, message, duration)
        snackbar.setBackgroundTint(Color.parseColor("#2A59FE"))
        snackbar.setTextColor(Color.WHITE)
        snackbar.setActionTextColor(Color.WHITE)
        snackbar.setAction("Kapat") { snackbar.dismiss() }
        snackbar.show()
    }
}
