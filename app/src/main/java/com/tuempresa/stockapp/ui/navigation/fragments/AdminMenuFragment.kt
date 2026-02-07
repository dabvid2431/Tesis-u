package com.tuempresa.stockapp.ui.navigation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.card.MaterialCardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.tuempresa.stockapp.R

class AdminMenuFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Elegir layout según rol: admin -> menú completo; vendedor -> menú reducido
        val prefs = context?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val role = prefs?.getString("user_role", "")
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
        if (role == "vendedor") {
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
        }
    }
}
