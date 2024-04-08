package com.ritanath.shopiify.fragment.supportfragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ritanath.shopiify.R
import com.ritanath.shopiify.activity.ShoppingActivity
import com.ritanath.shopiify.databinding.FragmentLoginBinding
import com.ritanath.shopiify.util.Resource
import com.ritanath.shopiify.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.gotoregister.setOnClickListener { findNavController().navigate(R.id.action_loginFragment_to_registerFragment) }
        binding.apply {
            binding.loginbtn.setOnClickListener {
                val email = emaillogin.text.toString().trim()
                val password = passwordlogin.text.toString()
                viewModel.login(email, password)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.loginstate.collect {
                when (it) {

                    is Resource.Success -> {
                        binding.loginbtn.revertAnimation()
                        Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        binding.loginbtn.revertAnimation()
                    }

                    is Resource.Loading -> {
                        binding.loginbtn.startAnimation()
                    }

                    else -> {
                        Unit
                    }
                }
            }
        }
    }

}