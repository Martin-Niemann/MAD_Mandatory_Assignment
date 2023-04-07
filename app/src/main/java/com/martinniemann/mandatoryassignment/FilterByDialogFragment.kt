package com.martinniemann.mandatoryassignment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import com.martinniemann.mandatoryassignment.databinding.FilterByBinding

interface FilterByDialogListener {
    fun onFilterByDialogPositiveClick(filterMethod: String, filterValue: String)
}

class FilterByDialogFragment(private val filterByDialogListener: FilterByDialogListener, private val existingFilterMethod: String, private val existingFilterValue: String) : DialogFragment(), AdapterView.OnItemSelectedListener {
    private var _binding: FilterByBinding? = null
    private val binding get() = _binding!!

    private var filterMethod: String = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FilterByBinding.inflate(LayoutInflater.from(context))

        val filterMethodSpinner: Spinner = binding.filterMethod
        filterMethodSpinner.onItemSelectedListener = this
        ArrayAdapter.createFromResource(requireContext(),
            R.array.filter_methods,
            android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                filterMethodSpinner.adapter = adapter
                if(existingFilterMethod.isNotEmpty()) {
                    filterMethodSpinner.setSelection(adapter.getPosition(existingFilterMethod))
                }
        }

        if (existingFilterValue.isNotEmpty()) {
            binding.filterValue.setText(existingFilterValue)
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setTitle("Filter by")
            .setPositiveButton(R.string.ok, null)
            .show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if(filterMethod == "Price" && (binding.filterValue.text.isNullOrEmpty() || !binding.filterValue.text.all { char -> char.isDigit() }))
            {
                binding.filterValue.error = "Price must be a whole number"
            }
            else {
                filterByDialogListener.onFilterByDialogPositiveClick(filterMethod, binding.filterValue.text.toString())
                dialog.dismiss()
            }
        }

        return dialog


    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        filterMethod = p0?.getItemAtPosition(p2).toString()

        when(filterMethod) {
            "Description" -> {binding.filterValue.inputType = InputType.TYPE_TEXT_VARIATION_FILTER}
            "Price"-> {binding.filterValue.inputType = InputType.TYPE_CLASS_NUMBER}
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }
}