package com.martinniemann.mandatoryassignment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.martinniemann.mandatoryassignment.databinding.FragmentSecondBinding
import com.martinniemann.mandatoryassignment.models.SalesItem
import com.martinniemann.mandatoryassignment.models.SalesItemsViewModel
import io.appwrite.Client
import io.appwrite.services.Account
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.runBlocking

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {
    private val dotenv = dotenv {
        directory = "/assets"
        filename = "env" // instead of '.env', use 'env'
    }

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val salesItemsViewModel: SalesItemsViewModel by activityViewModels()

    private lateinit var account: Account

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val client = Client(requireContext())
            .setEndpoint(dotenv["APPWRITE_ENDPOINT"])
            .setProject(dotenv["APPWRITE_PROJECT"])
        account = Account(client)

        val bundle = requireArguments()
        val secondFragmentArgs: SecondFragmentArgs = SecondFragmentArgs.fromBundle(bundle)
        val position = secondFragmentArgs.position

        val salesItem: SalesItem? = salesItemsViewModel[position]

        if(salesItem != null) {
            binding.description.text = salesItem.description
            binding.price.text = salesItem.price.toString()
            binding.sellerEmail.text = salesItem.sellerEmail
            binding.sellerPhone.text = salesItem.sellerPhone
            binding.time.text = salesItem.humanDate()

            val userEmail = runBlocking {account.get().email}

            if(salesItem.sellerEmail == userEmail) {
                binding.removeItemButton.visibility = View.VISIBLE
            }

            binding.removeItemButton.setOnClickListener {
                binding.progressBar.visibility = View.VISIBLE
                salesItemsViewModel.delete(salesItem.id)

                salesItemsViewModel.removeStatusLiveData.observe(viewLifecycleOwner) {
                    val action =
                        SecondFragmentDirections.actionSecondFragmentToFirstFragment()
                    findNavController().navigate(action)
                }
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