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
import com.google.android.material.snackbar.Snackbar
import com.martinniemann.mandatoryassignment.databinding.FragmentFirstBinding
import com.martinniemann.mandatoryassignment.models.ListItemCardAdapter
import com.martinniemann.mandatoryassignment.models.SalesItemsViewModel
import io.appwrite.Client
import io.appwrite.services.Account
import kotlinx.coroutines.runBlocking
import io.github.cdimascio.dotenv.dotenv

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment(), SortByDialogListener {
    private val dotenv = dotenv {
        directory = "/assets"
        filename = "env" // instead of '.env', use 'env'
    }

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val salesItemsViewModel: SalesItemsViewModel by activityViewModels()

    private var sortMethod: String = ""
    private var sortDirection: String = ""

    private lateinit var account: Account

    private var myItemsAreShown: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val client = Client(requireContext())
            .setEndpoint(dotenv["APPWRITE_ENDPOINT"])
            .setProject(dotenv["APPWRITE_PROJECT"])
        account = Account(client)

        setupMenu()

        // do the first load of of items
        salesItemsViewModel.reload()

        salesItemsViewModel.salesItemsLiveData.observe(viewLifecycleOwner) { salesItems ->
            binding.progressbar.visibility = View.GONE
            binding.recyclerView.visibility = if (salesItems.isEmpty()) View.GONE else View.VISIBLE
            if (salesItems.isNotEmpty()) {
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

        binding.swiperefresh.setOnRefreshListener {
            binding.textviewMessage.visibility = View.GONE

            if(!myItemsAreShown) {
                salesItemsViewModel.reload()
            }
            if(myItemsAreShown) {
                getUserSalesItems()
            }

            salesItemsViewModel.hasFetchedLiveData.observe(viewLifecycleOwner) {
                binding.swiperefresh.isRefreshing = false
            }
        }

        binding.addItemButton.setOnClickListener{
            val action =
                FirstFragmentDirections.actionFirstFragmentToAddSalesItemFragment()
            findNavController().navigate(action)
        }

        //binding.addItemButton.setOnClickListener { view ->
          //  Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            //    .setAction("Action", null).show()
        //}
    }

    private fun setupMenu() {
        val menuHost = requireActivity() as MenuHost
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)
            }

            override fun onMenuItemSelected(item: MenuItem): Boolean {
                return when (item.itemId) {
                    R.id.myStore -> {changeStoreStateAllOrUser()}
                    R.id.filter -> true
                    R.id.sort -> {showSortByDialog()}
                    R.id.logout -> {
                        runBlocking {logout()}
                    }
                    else -> true
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun changeStoreStateAllOrUser(): Boolean {
        return if(!myItemsAreShown) {
            binding.addItemButton.visibility = View.VISIBLE
            getUserSalesItems()
            myItemsAreShown = true
            true
        } else {
            binding.addItemButton.visibility = View.GONE
            salesItemsViewModel.reload()
            myItemsAreShown = false
            true
        }
    }
    
    private fun getUserSalesItems() {
        val email: String = runBlocking {account.get().email}
        salesItemsViewModel.filterByEmail(email)
    }

    // TODO if the server ever hangs,
    //  we probably will so for all eternity
    private suspend fun logout(): Boolean {
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