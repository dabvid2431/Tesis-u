package com.tuempresa.stockapp.ui.navigation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tuempresa.stockapp.R
import com.tuempresa.stockapp.models.Notification
import com.tuempresa.stockapp.ui.adapters.NotificationAdapter
import com.tuempresa.stockapp.viewmodels.NotificationsViewModel

class NotificationsFragment : Fragment() {
    private val viewModel: NotificationsViewModel by viewModels()
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var textNoNotifications: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        recyclerView = view.findViewById(R.id.recyclerViewNotifications)
        recyclerView.layoutManager = LinearLayoutManager(context)
        textNoNotifications = view.findViewById(R.id.textNoNotifications)
        progressBar = view.findViewById(R.id.progressBar) ?: ProgressBar(requireContext())
        
        adapter = NotificationAdapter(listOf()) { notificationId ->
            viewModel.markAsRead(notificationId)
        }
        
        recyclerView.adapter = adapter
        
        loadNotifications()
        observeViewModel()
    }
    
    private fun loadNotifications() {
        progressBar.visibility = View.VISIBLE
        viewModel.fetchNotifications()
    }
    
    private fun observeViewModel() {
        viewModel.notificationsData.observe(viewLifecycleOwner) { notifications ->
            progressBar.visibility = View.GONE
            
            if (notifications.isNotEmpty()) {
                textNoNotifications.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                adapter.updateList(notifications)
            } else {
                recyclerView.visibility = View.GONE
                textNoNotifications.visibility = View.VISIBLE
            }
        }
        
        viewModel.markAsReadSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                viewModel.fetchNotifications()
            }
        }
    }
}
