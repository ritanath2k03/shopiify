package com.ritanath.shopiify.fragment.supportfragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.ritanath.shopiify.R
import com.ritanath.shopiify.activity.ShoppingActivity
import com.ritanath.shopiify.databinding.FragmentIntroBinding
import com.ritanath.shopiify.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroFragment : Fragment(R.layout.fragment_intro) {
    private lateinit var binding: FragmentIntroBinding

    private val viewModel by viewModels<LoginViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIntroBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkUser()
        binding.startbtn.setOnClickListener {
            findNavController().navigate(R.id.action_introFragment_to_accountOptionFragment)
        }
    }

    private fun checkUser() {
        viewModel.isLoggedIn.observe(requireActivity(), Observer { isLoggedIn ->
            if (isLoggedIn) {
                Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
        })
    }
}