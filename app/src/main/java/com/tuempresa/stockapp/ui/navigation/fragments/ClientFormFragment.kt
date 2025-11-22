package com.tuempresa.stockapp.ui.navigation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.tuempresa.stockapp.R
import com.tuempresa.stockapp.models.Client
import com.tuempresa.stockapp.viewmodels.ClientViewModel

class ClientFormFragment : Fragment() {
    private lateinit var viewModel: ClientViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_client_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Setup toolbar with back button
        val toolbar = view.findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        
        viewModel = ViewModelProvider(this)[ClientViewModel::class.java]

        val editName = view.findViewById<TextInputEditText>(R.id.editTextClientName)
        val editPhone = view.findViewById<TextInputEditText>(R.id.editTextClientPhone)
        val editEmail = view.findViewById<TextInputEditText>(R.id.editTextClientEmail)
        val editAddress = view.findViewById<TextInputEditText>(R.id.editTextClientAddress)
        val buttonSave = view.findViewById<Button>(R.id.buttonSaveClient)

        buttonSave.setOnClickListener {
            val name = editName.text.toString().trim()
            val phone = editPhone.text.toString().trim()
            val email = editEmail.text.toString().trim()
            val address = editAddress.text.toString().trim()
            
            // Validation
            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "Name is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if (email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(requireContext(), "Invalid email format", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
                // Build a map payload that omits the `id` field to avoid duplicate primary key errors
                val clientMap = mutableMapOf<String, Any>()
                clientMap["name"] = name
                if (phone.isNotBlank()) clientMap["phone"] = phone
                if (email.isNotBlank()) clientMap["email"] = email
                if (address.isNotBlank()) clientMap["address"] = address

                viewModel.createClientMap(clientMap, { created ->
                    if (created != null) {
                        Toast.makeText(requireContext(), "Cliente creado", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    } else {
                        // onError already handled
                    }
                }, { errorMsg ->
                    Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()
                })
        }
    }
}