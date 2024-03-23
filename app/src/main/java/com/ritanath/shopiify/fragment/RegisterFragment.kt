package com.ritanath.shopiify.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ritanath.shopiify.R
import com.ritanath.shopiify.data.User
import com.ritanath.shopiify.databinding.FragmentRegisterBinding
import com.ritanath.shopiify.util.Resource
import com.ritanath.shopiify.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            registerbtn.setOnClickListener {
                val user = User(
                    namelogin.text.toString().trim(), emaillogin.text.toString().trim()
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

                    else -> {}
                }
            }
        }
    }
}