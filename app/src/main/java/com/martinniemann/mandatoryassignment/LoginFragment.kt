package com.martinniemann.mandatoryassignment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.martinniemann.mandatoryassignment.databinding.FragmentLoginBinding
import io.appwrite.Client
import io.appwrite.exceptions.AppwriteException
import io.appwrite.services.Account
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.runBlocking

class LoginFragment : Fragment() {
    private val dotenv = dotenv {
        directory = "/assets"
        filename = "env" // instead of '.env', use 'env'
    }

    private var _binding: FragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val client = Client(requireContext())
            .setEndpoint(dotenv["APPWRITE_ENDPOINT"])
            .setProject(dotenv["APPWRITE_PROJECT"])
        val account = Account(client)

        binding.registerButton.setOnClickListener {
            runBlocking()
            {
                try {
                    if(binding.email.text.isNullOrEmpty() ||
                        !android.util.Patterns.EMAIL_ADDRESS.matcher(binding.email.text.toString())
                            .matches()
                    ) {
                        throw IllegalArgumentException("You must enter a valid email address for your new account.")
                    }

                    if(binding.password.text.isNullOrEmpty() ||
                        !binding.password.text!!.matches(".{8,}".toRegex())) {
                        throw IllegalArgumentException("You must enter a password that is a least eight characters long")
                    }

                    if(binding.phoneNumber.text.isNullOrEmpty() ||
                        !binding.phoneNumber.text!!.matches("^[+]\\d{10}".toRegex())) {
                        throw IllegalArgumentException("You must enter a valid phone number including your country code for your new account.")
                    }

                    // due to arbitrary decisions by the Appwrite folks,
                    // creating an anonymous account, then upgrading it to a standard account
                    // by updating it with an email address and a password gives us the ability
                    // to then make use of the user preferences feature, allowing us to
                    // store additional information for the user as key-value pairs.

                    // this is a hugely flawed way to create an account,
                    // because if updateEmail or updatePhone throw an exception,
                    // a new anonymous account or a standard account without a phone number
                    // now exists in the database.
                    // this is why I am doing manual checks of user input above
                    // in order to try and avoid such a situation,
                    // even though Appwrite also does its own checks and throws exceptions.
                    account.createAnonymousSession()
                    account.updateEmail(binding.email.text.toString(), binding.password.text.toString())
                    account.updatePhone(binding.phoneNumber.text.toString(), binding.password.text.toString())

                    val action =
                        LoginFragmentDirections.actionLoginFragmentToFirstFragment()
                    findNavController().navigate(action)
                } catch (e: Exception) {
                    when(e) {
                        is AppwriteException, is IllegalArgumentException -> {
                            binding.errorMessage.text = e.message
                            binding.errorMessage.setError("")
                        }
                    }
                }
            }
        }

        binding.loginButton.setOnClickListener {
            runBlocking()
            {
                try {
                    account.createEmailSession(binding.email.text.toString(), binding.password.text.toString())
                    val action =
                        LoginFragmentDirections.actionLoginFragmentToFirstFragment()
                    findNavController().navigate(action)
                } catch (e: AppwriteException) {
                    Log.e("Error", e.message.toString());
                    binding.errorMessage.text = e.message
                    binding.errorMessage.setError("")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}