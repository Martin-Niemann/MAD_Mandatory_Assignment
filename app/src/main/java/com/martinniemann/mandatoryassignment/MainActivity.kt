package com.martinniemann.mandatoryassignment

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.activity.viewModels
import com.martinniemann.mandatoryassignment.databinding.ActivityMainBinding
import com.martinniemann.mandatoryassignment.models.SalesItemsViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val salesItemsViewModel: SalesItemsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        salesItemsViewModel.updateStatusLiveData.observe(this) { bool ->
            Snackbar.make(binding.root, "Item has been posted", Snackbar.LENGTH_SHORT).show()
        }
        salesItemsViewModel.removeStatusLiveData.observe(this) { bool ->
            Snackbar.make(binding.root, "Item has been removed", Snackbar.LENGTH_SHORT).show()
        }
        salesItemsViewModel.errorMessageLiveData.observe(this) { message ->
            Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}