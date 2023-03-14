package com.martinniemann.mandatoryassignment

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.martinniemann.mandatoryassignment.databinding.FragmentFirstBinding
import com.martinniemann.mandatoryassignment.models.ListItemCardAdapter
import com.martinniemann.mandatoryassignment.models.SalesItemsViewModel

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val salesItemsViewModel: SalesItemsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        salesItemsViewModel.salesItemsLiveData.observe(viewLifecycleOwner) {salesItems ->
            binding.progressbar.visibility = View.GONE
            binding.recyclerView.visibility = if (salesItems == null) View.GONE else View.VISIBLE
            if (salesItems != null) {
                binding.textviewMessage.visibility = View.GONE
                val adapter = ListItemCardAdapter(salesItems) { position ->
                    val action =
                        FirstFragmentDirections.actionFirstFragmentToSecondFragment(position)
                    findNavController().navigate(action)
                }

                var columns = 1
                val currentOrientation = this.resources.configuration.orientation
                if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                    columns = 2
                } else if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
                    columns = 1
                }
                binding.recyclerView.layoutManager = GridLayoutManager(this.context, columns)

                binding.recyclerView.adapter = adapter
            }
        }

        salesItemsViewModel.errorMessageLiveData.observe(viewLifecycleOwner) { errorMessage ->
            binding.textviewMessage.visibility = View.VISIBLE
            binding.textviewMessage.text = errorMessage
        }

        salesItemsViewModel.reload()

        binding.swiperefresh.setOnRefreshListener {
            salesItemsViewModel.reload()
            binding.swiperefresh.isRefreshing = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}