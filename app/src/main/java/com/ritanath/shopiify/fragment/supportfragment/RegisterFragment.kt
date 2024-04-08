package com.ritanath.shopiify.fragment.supportfragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ritanath.shopiify.R
import com.ritanath.shopiify.data.User
import com.ritanath.shopiify.databinding.FragmentRegisterBinding
import com.ritanath.shopiify.util.RegisterValidation
import com.ritanath.shopiify.util.Resource
import com.ritanath.shopiify.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.haveAccount.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }


        binding.apply {
            registerbtn.setOnClickListener {
                val user = User(
                    namelogin.text.toString().trim(), emaillogin.text.toString().trim()
                    ,getDate()
                )
                val password = passwordlogin.text.toString()

                viewModel.createUserWithEmailAndPassword(user, password)
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.register.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.registerbtn.startAnimation()
                    }

                    is Resource.Success -> {
                        Log.d("Test", it.data.toString())
                        binding.registerbtn.revertAnimation()
                    }

                    is Resource.Error -> {
                        Log.d("Test", it.message.toString())
                        binding.registerbtn.revertAnimation()

                    }

                    else -> {Unit}
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.validation.collect{
                if(it.email is RegisterValidation.Failed){
                    withContext(Dispatchers.Main){
                       binding.emaillogin.apply {
                           requestFocus()
                           error=it.email.message
                       }
                    }
                }
                if(it.password is RegisterValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.passwordlogin.apply {
                            requestFocus()
                            error=it.password.message
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDate(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val current = LocalDateTime.now().format(formatter)
        return current
    }
}