package com.tuempresa.stockapp.ui.navigation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.tuempresa.stockapp.R
import com.tuempresa.stockapp.offline.SyncQueueRepository

class AdminMenuFragment : Fragment() {
    private var isAdminView: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Elegir layout según rol: admin -> menú completo; vendedor -> menú reducido
        val prefs = context?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val role = prefs?.getString("user_role", "")
        isAdminView = role != "vendedor"
        return if (role == "vendedor") {
            inflater.inflate(R.layout.fragment_vendor_menu, container, false)
        } else {
            inflater.inflate(R.layout.fragment_admin_menu, container, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Enlazar botones según el layout que se haya inflado
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val role = prefs.getString("user_role", "")
        val username = prefs.getString("username", "")?.trim().orEmpty()
        val displayName = username.ifBlank { "usuario" }

        if (role == "vendedor") {
            view.findViewById<TextView>(R.id.textVendorTitle)?.text = "Hola, $displayName"
            view.findViewById<TextView>(R.id.textVendorSubtitle)?.text = "Panel de vendedor · accesos rápidos"
            // Menú ampliado para vendedor: Ventas, Clientes, Productos (consulta) y Notificaciones
            view.findViewById<View>(R.id.buttonVendorSales)?.setOnClickListener {
                findNavController().navigate(R.id.saleListFragment)
            }
            view.findViewById<View>(R.id.buttonVendorClients)?.setOnClickListener {
                findNavController().navigate(R.id.clientListFragment)
            }
            view.findViewById<View>(R.id.buttonVendorProducts)?.setOnClickListener {
                findNavController().navigate(R.id.productListFragment)
            }
            view.findViewById<View>(R.id.buttonVendorNotifications)?.setOnClickListener {
                findNavController().navigate(R.id.notificationsFragment)
            }
        } else {
            view.findViewById<TextView>(R.id.textAdminWelcome)?.text = "Hola, $displayName"
            // Menú admin completo: enlazar tarjetas
            view.findViewById<MaterialCardView>(R.id.cardProducts)?.setOnClickListener {
                findNavController().navigate(R.id.productListFragment)
            }
            view.findViewById<MaterialCardView>(R.id.cardCategories)?.setOnClickListener {
                findNavController().navigate(R.id.categoryListFragment)
            }
            view.findViewById<MaterialCardView>(R.id.cardSuppliers)?.setOnClickListener {
                findNavController().navigate(R.id.supplierListFragment)
            }
            view.findViewById<MaterialCardView>(R.id.cardClients)?.setOnClickListener {
                findNavController().navigate(R.id.clientListFragment)
            }
            view.findViewById<MaterialCardView>(R.id.cardPurchases)?.setOnClickListener {
                findNavController().navigate(R.id.purchaseListFragment)
            }
            view.findViewById<MaterialCardView>(R.id.cardSales)?.setOnClickListener {
                findNavController().navigate(R.id.saleListFragment)
            }
            view.findViewById<MaterialCardView>(R.id.cardReports)?.setOnClickListener {
                findNavController().navigate(R.id.reportsFragment)
            }
            view.findViewById<MaterialCardView>(R.id.cardNotifications)?.setOnClickListener {
                findNavController().navigate(R.id.notificationsFragment)
            }
            view.findViewById<MaterialCardView>(R.id.cardSyncCenter)?.setOnClickListener {
                findNavController().navigate(R.id.syncCenterFragment)
            }
            updateSyncBadge(view)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isAdminView) {
            view?.let { updateSyncBadge(it) }
        }
    }

    private fun updateSyncBadge(root: View) {
        val repository = SyncQueueRepository(requireContext())
        val pendingCount = repository.getPendingCount()
        val failedCount = repository.getFailedOnlyCount()
        val pendingOnlyCount = repository.getPendingOnlyCount()

        val badge = root.findViewById<TextView>(R.id.textSyncCenterBadge)
        val pendingLabel = root.findViewById<TextView>(R.id.textSyncCenterPending)

        if (failedCount > 0) {
            badge?.visibility = View.VISIBLE
            badge?.setBackgroundResource(R.drawable.badge_warning)
            badge?.setTextColor(resources.getColor(R.color.colorOnWarning, null))
            badge?.text = if (failedCount > 99) "99+" else failedCount.toString()
            pendingLabel?.text = "$failedCount con error · $pendingOnlyCount pendientes"
        } else if (pendingOnlyCount > 0) {
            badge?.visibility = View.VISIBLE
            badge?.setBackgroundResource(R.drawable.badge_error)
            badge?.setTextColor(resources.getColor(R.color.colorOnError, null))
            badge?.text = if (pendingOnlyCount > 99) "99+" else pendingOnlyCount.toString()
            pendingLabel?.text = "$pendingOnlyCount pendiente(s) por sincronizar"
        } else {
            badge?.visibility = View.VISIBLE
            badge?.setBackgroundResource(R.drawable.badge_success)
            badge?.setTextColor(resources.getColor(R.color.colorOnSuccess, null))
            badge?.text = "OK"
            pendingLabel?.text = "Sin pendientes"
        }
    }
}
