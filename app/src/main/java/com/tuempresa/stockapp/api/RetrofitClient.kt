package com.tuempresa.stockapp.api

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.tuempresa.stockapp.BuildConfig

object RetrofitClient {
    private const val PREFS_NAME = "app_config"
    private const val KEY_SERVER_URL = "server_url"

    @Volatile
    private var appContext: Context? = null

    @Volatile
    private var cachedApi: ApiService? = null

    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    fun getCurrentBaseUrl(context: Context? = appContext): String {
        val stored = context
            ?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            ?.getString(KEY_SERVER_URL, null)
            ?.trim()
            ?.takeIf { it.isNotBlank() }

        return normalizeBaseUrl(stored ?: BuildConfig.BASE_URL)
    }

    fun updateBaseUrl(context: Context, newBaseUrl: String) {
        val normalized = normalizeBaseUrl(newBaseUrl)
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_SERVER_URL, normalized)
            .apply()
        cachedApi = null
    }

    val instance: ApiService
        get() {
            val existing = cachedApi
            if (existing != null) return existing

            synchronized(this) {
                val again = cachedApi
                if (again != null) return again

                val baseUrl = getCurrentBaseUrl()
                val okHttpClient = OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .addHeader("ngrok-skip-browser-warning", "true")
                            .build()
                        chain.proceed(request)
                    }
                    .build()

                val created = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiService::class.java)

                cachedApi = created
                return created
            }
        }

    private fun normalizeBaseUrl(raw: String): String {
        var value = raw.trim()
        if (!value.startsWith("http://") && !value.startsWith("https://")) {
            value = "http://$value"
        }
        if (!value.endsWith("/")) {
            value += "/"
        }
        if (!value.endsWith("api/")) {
            value += "api/"
        }
        return value
    }
}
