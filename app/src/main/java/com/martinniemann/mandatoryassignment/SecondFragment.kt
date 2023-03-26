package com.martinniemann.mandatoryassignment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.martinniemann.mandatoryassignment.databinding.FragmentSecondBinding
import com.martinniemann.mandatoryassignment.models.SalesItem
import com.martinniemann.mandatoryassignment.models.SalesItemsViewModel

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val salesItemsViewModel: SalesItemsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = requireArguments()
        val secondFragmentArgs: SecondFragmentArgs = SecondFragmentArgs.fromBundle(bundle)
        val position = secondFragmentArgs.position

        val salesItem: SalesItem? = salesItemsViewModel[position]

        if(salesItem != null) {
            binding.description.text = salesItem.description
            binding.price.text = salesItem.price.toString()
            binding.sellerEmail.text = salesItem.sellerEmail
            binding.sellerPhone.text = salesItem.sellerPhone
            binding.time.text = salesItem.time.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}