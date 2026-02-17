package com.tuempresa.stockapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.tuempresa.stockapp.R
import com.tuempresa.stockapp.api.RetrofitClient
import com.tuempresa.stockapp.offline.OfflineSyncScheduler

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        RetrofitClient.initialize(applicationContext)
        OfflineSyncScheduler.schedulePeriodicSync(applicationContext)
        
        // Configuración de navegación con Navigation Component
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
    }
}