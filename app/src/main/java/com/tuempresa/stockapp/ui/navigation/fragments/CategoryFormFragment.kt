package com.tuempresa.stockapp.ui.navigation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.tuempresa.stockapp.R
import com.tuempresa.stockapp.models.Category
import com.tuempresa.stockapp.viewmodels.CategoryViewModel

class CategoryFormFragment : Fragment() {
    private lateinit var viewModel: CategoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_category_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[CategoryViewModel::class.java]

        val editName = view.findViewById<EditText>(R.id.editTextCategoryName)
        val editDesc = view.findViewById<EditText>(R.id.editTextCategoryDescription)
        val buttonSave = view.findViewById<Button>(R.id.buttonSaveCategory)

        buttonSave.setOnClickListener {
            val name = editName.text.toString().trim()
            val desc = editDesc.text.toString().trim()
            if (name.isNotEmpty() && desc.isNotEmpty()) {
                val category = Category(id = 0, name = name, description = desc)
                viewModel.createCategory(category) { result ->
                    if (result != null) {
                        Toast.makeText(requireContext(), "Categoría creada", Toast.LENGTH_SHORT).show()
                        editName.text.clear()
                        editDesc.text.clear()
                    } else {
                        Toast.makeText(requireContext(), "Error al crear", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
