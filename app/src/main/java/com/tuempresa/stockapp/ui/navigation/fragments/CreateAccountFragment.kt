package com.tuempresa.stockapp.ui.navigation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Spinner
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.tuempresa.stockapp.R
import com.tuempresa.stockapp.models.User
import com.tuempresa.stockapp.utils.Resource
import com.tuempresa.stockapp.viewmodels.UserViewModel

class CreateAccountFragment : Fragment() {
    private lateinit var viewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[UserViewModel::class.java]

        val editUsername = view.findViewById<EditText>(R.id.editTextNewUsername)
        val editPassword = view.findViewById<EditText>(R.id.editTextNewPassword)
        val spinnerRole = view.findViewById<Spinner>(R.id.spinnerRole)
        val buttonCreate = view.findViewById<Button>(R.id.buttonCreateAccount)
        val textError = view.findViewById<TextView>(R.id.textCreateError)

        val roles = arrayOf("admin", "vendedor")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRole.adapter = adapter

        buttonCreate.setOnClickListener {
            val username = editUsername.text.toString().trim()
            val password = editPassword.text.toString().trim()
            val role = spinnerRole.selectedItem.toString()
            if (username.isNotEmpty() && password.isNotEmpty()) {
                // Crear un mapa solo con los campos requeridos
                val userMap = hashMapOf(
                    "username" to username,
                    "password" to password,
                    "role" to role
                )
                viewModel.createUserMap(userMap)
            } else {
                textError.visibility = View.VISIBLE
                textError.text = "Completa todos los campos"
            }
        }

        viewModel.user.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is com.tuempresa.stockapp.utils.Resource.Loading -> {
                    textError.visibility = View.GONE
                }
                is com.tuempresa.stockapp.utils.Resource.Success -> {
                    textError.visibility = View.GONE
                    Toast.makeText(requireContext(), "Cuenta creada", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                is com.tuempresa.stockapp.utils.Resource.Error -> {
                    textError.visibility = View.VISIBLE
                    textError.text = resource.message ?: "Error desconocido"
                }
            }
        }
    }
}
