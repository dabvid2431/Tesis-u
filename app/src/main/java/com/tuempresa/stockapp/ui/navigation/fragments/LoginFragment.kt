package com.tuempresa.stockapp.ui.navigation.fragments

import android.content.Context
import android.os.Bundle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.core.content.edit
import androidx.navigation.fragment.findNavController
import androidx.navigation.NavOptions
import com.tuempresa.stockapp.R
import com.tuempresa.stockapp.api.RetrofitClient
import com.tuempresa.stockapp.viewmodels.UserViewModel
import com.tuempresa.stockapp.utils.Resource

class LoginFragment : Fragment() {
    private lateinit var viewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        RetrofitClient.initialize(requireContext().applicationContext)
        viewModel = ViewModelProvider(this)[UserViewModel::class.java]

        val editUsername = view.findViewById<EditText>(R.id.editTextUsername)
        val editPassword = view.findViewById<EditText>(R.id.editTextPassword)
        val buttonLogin = view.findViewById<Button>(R.id.buttonLogin)
        val buttonGoToCreateAccount = view.findViewById<Button>(R.id.buttonGoToCreateAccount)
        val buttonConfigureServer = view.findViewById<Button>(R.id.buttonConfigureServer)
        val textCurrentServer = view.findViewById<TextView>(R.id.textCurrentServer)
        val textError = view.findViewById<TextView>(R.id.textLoginError)

        val refreshServerLabel: () -> Unit = {
            val baseUrl = RetrofitClient.getCurrentBaseUrl(requireContext())
            textCurrentServer.text = "Servidor: $baseUrl"
        }
        refreshServerLabel()

        buttonConfigureServer.setOnClickListener {
            showServerConfigDialog(refreshServerLabel)
        }

        buttonLogin.setOnClickListener {
            val username = editUsername.text.toString().trim()
            val password = editPassword.text.toString().trim()
            if (username.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(username, password)
            } else {
                textError.visibility = View.VISIBLE
                textError.text = "Completa todos los campos"
            }
        }

        buttonGoToCreateAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_createAccountFragment)
        }

        viewModel.user.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    textError.visibility = View.GONE
                }
                is Resource.Success -> {
                    textError.visibility = View.GONE
                    val user = resource.data
                    if (user != null) {
                        Toast.makeText(requireContext(), "Bienvenido ${user.username}", Toast.LENGTH_SHORT).show()
                        // Guardar rol en SharedPreferences para controlar vistas según permiso
                        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                        prefs.edit {
                            putString("user_role", user.role)
                            putString("username", user.username)
                            putInt("user_id", user.id)
                        }

                        // Navegación al menú correspondiente (admin o vendedor)
                        // AdminMenuFragment muestra el layout correcto según el rol
                        val navController = findNavController()
                        val options = NavOptions.Builder()
                            .setPopUpTo(R.id.loginFragment, true)
                            .build()
                        navController.navigate(R.id.action_loginFragment_to_adminMenuFragment, null, options)
                    }
                }
                is Resource.Error -> {
                    textError.visibility = View.VISIBLE
                    textError.text = resource.message ?: "Error desconocido"
                }
            }
        }
    }

    private fun showServerConfigDialog(onSaved: () -> Unit) {
        val input = EditText(requireContext()).apply {
            setText(RetrofitClient.getCurrentBaseUrl(requireContext()))
            hint = "https://tu-servidor.com/api/"
            setPadding(40, 24, 40, 24)
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Configurar servidor")
            .setMessage("Usa una URL pública para funcionar en cualquier red")
            .setView(input)
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Guardar") { _, _ ->
                val typed = input.text?.toString()?.trim().orEmpty()
                if (typed.isBlank()) {
                    Toast.makeText(requireContext(), "Ingresa una URL válida", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                RetrofitClient.updateBaseUrl(requireContext(), typed)
                onSaved()
                Toast.makeText(requireContext(), "Servidor actualizado", Toast.LENGTH_SHORT).show()
            }
            .show()
    }
}
