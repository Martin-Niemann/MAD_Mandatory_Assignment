package com.martinniemann.mandatoryassignment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.martinniemann.mandatoryassignment.databinding.FragmentAddSalesItemBinding
import com.martinniemann.mandatoryassignment.models.SalesItem
import com.martinniemann.mandatoryassignment.models.SalesItemsViewModel
import io.appwrite.Client
import io.appwrite.services.Account
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.runBlocking

class AddSalesItemFragment : Fragment() {
    private val dotenv = dotenv {
        directory = "/assets"
        filename = "env" // instead of '.env', use 'env'
    }

    private var _binding: FragmentAddSalesItemBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val salesItemsViewModel: SalesItemsViewModel by activityViewModels()

    private lateinit var account: Account

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddSalesItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val client = Client(requireContext())
            .setEndpoint(dotenv["APPWRITE_ENDPOINT"])
            .setProject(dotenv["APPWRITE_PROJECT"])
        account = Account(client)

        binding.postItemButton.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE

            val email: String = runBlocking {account.get().email}
            val phone: String = runBlocking {account.get().phone}

            val salesItem = SalesItem(binding.description.text.toString(),
                                                 binding.price.text.toString().toInt(),
                                                 email,
                                                 phone,
                                                 System.currentTimeMillis()/1000)
            salesItemsViewModel.add(salesItem)

            salesItemsViewModel.updateStatusLiveData.observe(viewLifecycleOwner) {
                val action =
                    AddSalesItemFragmentDirections.actionAddSalesItemFragmentToFirstFragment()
                findNavController().navigate(action)
            }
        }

        salesItemsViewModel.errorMessageLiveData.observe(viewLifecycleOwner) {errorMessage ->
            binding.progressBar.visibility = View.GONE
            binding.errorMessage.text = errorMessage
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}