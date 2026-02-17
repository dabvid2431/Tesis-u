package com.tuempresa.stockapp.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.tuempresa.stockapp.api.RetrofitClient
import com.tuempresa.stockapp.models.Notification
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationsViewModel(application: Application) : AndroidViewModel(application) {
    
    val notificationsData = MutableLiveData<List<Notification>>()
    val markAsReadSuccess = MutableLiveData<Boolean>()
    
    private val apiService get() = RetrofitClient.instance
    private val prefs = application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    
    fun fetchNotifications() {
        apiService.getNotifications().enqueue(object : Callback<List<Notification>> {
            override fun onResponse(call: Call<List<Notification>>, response: Response<List<Notification>>) {
                if (response.isSuccessful && response.body() != null) {
                    notificationsData.value = response.body()!!
                }
            }
            override fun onFailure(call: Call<List<Notification>>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
    
    fun markAsRead(notificationId: Int) {
        apiService.markNotificationAsRead(notificationId).enqueue(object : Callback<Notification> {
            override fun onResponse(call: Call<Notification>, response: Response<Notification>) {
                if (response.isSuccessful) {
                    markAsReadSuccess.value = true
                }
            }
            override fun onFailure(call: Call<Notification>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
}
