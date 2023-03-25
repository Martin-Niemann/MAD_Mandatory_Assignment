package com.martinniemann.mandatoryassignment

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import com.martinniemann.mandatoryassignment.databinding.SortByBinding

interface SortByDialogListener {
    fun onDialogPositiveClick(sortMethod: String, sortDirection: String)
}

class SortByDialogFragment(private val sortByDialogListener: SortByDialogListener, private val existingSortMethod: String, private val existingSortDirection: String) : DialogFragment(), AdapterView.OnItemSelectedListener {
    private var _binding: SortByBinding? = null
    private val binding get() = _binding!!

    private var sortMethod: String = ""
    private var sortDirection: String = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = SortByBinding.inflate(LayoutInflater.from(context))

        val sortMethodSpinner: Spinner = binding.sortMethod
        sortMethodSpinner.onItemSelectedListener = this
        ArrayAdapter.createFromResource(requireContext(),
                                        R.array.sorting_methods,
                                        android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                sortMethodSpinner.adapter = adapter
                if(existingSortMethod.isNotEmpty()) {
                    sortMethodSpinner.setSelection(adapter.getPosition(existingSortMethod))
                }
        }

        val sortDirectionSpinner: Spinner = binding.sortDirection
        sortDirectionSpinner.onItemSelectedListener = this
        ArrayAdapter.createFromResource(requireContext(),
            R.array.sorting_direction,
            android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                sortDirectionSpinner.adapter = adapter
                if(existingSortDirection.isNotEmpty()) {
                    sortDirectionSpinner.setSelection(adapter.getPosition(existingSortDirection))
                }
        }

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setTitle("Sort by")
            .setPositiveButton(R.string.ok,
                DialogInterface.OnClickListener { dialog, id ->
                    sortByDialogListener.onDialogPositiveClick(sortMethod, sortDirection)
                }).create()
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        when(p0?.id) {
            R.id.sort_method -> {sortMethod = p0.getItemAtPosition(p2).toString()}
            R.id.sort_direction -> {sortDirection = p0.getItemAtPosition(p2).toString()}
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }
}