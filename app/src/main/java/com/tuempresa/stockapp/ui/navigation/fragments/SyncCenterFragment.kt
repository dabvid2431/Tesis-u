package com.tuempresa.stockapp.ui.navigation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.tuempresa.stockapp.R
import com.tuempresa.stockapp.offline.OfflineSyncScheduler
import com.tuempresa.stockapp.offline.SyncQueueEntity
import com.tuempresa.stockapp.offline.SyncQueueRepository
import com.tuempresa.stockapp.ui.adapters.SyncQueueAdapter

class SyncCenterFragment : Fragment() {
    private lateinit var repository: SyncQueueRepository
    private lateinit var adapter: SyncQueueAdapter
    private lateinit var summaryText: TextView
    private lateinit var emptyText: TextView
    private lateinit var toolbarBadge: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_sync_center, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = SyncQueueRepository(requireContext())

        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbarSyncCenter)
        summaryText = view.findViewById(R.id.textSyncSummary)
        emptyText = view.findViewById(R.id.textSyncEmpty)
        toolbarBadge = view.findViewById(R.id.textSyncToolbarBadge)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerSyncQueue)
        val buttonSyncNow = view.findViewById<MaterialButton>(R.id.buttonSyncNow)

        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        adapter = SyncQueueAdapter(emptyList())
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        buttonSyncNow.setOnClickListener {
            OfflineSyncScheduler.enqueueImmediateSync(requireContext())
            Toast.makeText(requireContext(), "Sincronización iniciada", Toast.LENGTH_SHORT).show()
        }

        loadQueue()
    }

    override fun onResume() {
        super.onResume()
        loadQueue()
    }

    private fun loadQueue() {
        val items = repository.getAllItems()
        val failedCount = repository.getFailedOnlyCount()
        val pendingOnlyCount = repository.getPendingOnlyCount()
        val pendingCount = items.count {
            it.status == SyncQueueEntity.STATUS_PENDING || it.status == SyncQueueEntity.STATUS_FAILED
        }
        summaryText.text = "Pendientes: $pendingCount · Total en cola: ${items.size}"

        when {
            failedCount > 0 -> {
                toolbarBadge.setBackgroundResource(R.drawable.badge_warning)
                toolbarBadge.setTextColor(resources.getColor(R.color.colorOnWarning, null))
                toolbarBadge.text = "ERR ${if (failedCount > 99) "99+" else failedCount}"
            }
            pendingOnlyCount > 0 -> {
                toolbarBadge.setBackgroundResource(R.drawable.badge_error)
                toolbarBadge.setTextColor(resources.getColor(R.color.colorOnError, null))
                toolbarBadge.text = "PEND ${if (pendingOnlyCount > 99) "99+" else pendingOnlyCount}"
            }
            else -> {
                toolbarBadge.setBackgroundResource(R.drawable.badge_success)
                toolbarBadge.setTextColor(resources.getColor(R.color.colorOnSuccess, null))
                toolbarBadge.text = "OK"
            }
        }

        emptyText.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        adapter.submit(items)
    }
}
