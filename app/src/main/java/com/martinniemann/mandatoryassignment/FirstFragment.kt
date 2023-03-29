package com.martinniemann.mandatoryassignment

import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.martinniemann.mandatoryassignment.databinding.FragmentFirstBinding
import com.martinniemann.mandatoryassignment.models.ListItemCardAdapter
import com.martinniemann.mandatoryassignment.models.SalesItemsViewModel
import io.appwrite.Client
import io.appwrite.services.Account
import kotlinx.coroutines.runBlocking

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment(), SortByDialogListener {
    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val salesItemsViewModel: SalesItemsViewModel by activityViewModels()

    private var sortMethod: String = ""
    private var sortDirection: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenu()

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
            salesItemsViewModel.hasFetchedLiveData.observe(viewLifecycleOwner) { bool ->
                binding.swiperefresh.isRefreshing = false
            }
        }
    }

    private fun setupMenu() {
        val menuHost = requireActivity() as MenuHost
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)
            }

            override fun onMenuItemSelected(item: MenuItem): Boolean {
                return when (item.itemId) {
                    R.id.postItem -> true
                    R.id.filter -> true
                    R.id.sort -> {showSortByDialog()}
                    R.id.logout -> {
                        runBlocking(){logout()}
                    }
                    else -> true
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    // TODO if the server ever hangs,
    //  we probably will so for all eternity
    private suspend fun logout(): Boolean {
        val client = Client(requireContext())
            .setEndpoint("***REMOVED***")
            .setProject("***REMOVED***")
        val account = Account(client)
        account.deleteSession("current")

        val action =
            FirstFragmentDirections.actionFirstFragmentToLoginFragment()
        findNavController().navigate(action)

        return true
    }

    private fun showSortByDialog(): Boolean {
        val dialog = SortByDialogFragment(this, sortMethod, sortDirection)
        dialog.show(parentFragmentManager, "SortByDialogFragment")
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDialogPositiveClick(sortMethod: String, sortDirection: String) {
        // Save the values in this fragment and send them back to the next dialog
        this.sortMethod = sortMethod
        this.sortDirection = sortDirection

        when(sortMethod) {
            "Price" -> {salesItemsViewModel.sortByPrice(sortDirection)}
            "Time" -> {salesItemsViewModel.sortByTime(sortDirection)}
        }
    }
}