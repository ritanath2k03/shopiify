package com.ritanath.shopiify.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ritanath.shopiify.R
import com.ritanath.shopiify.databinding.FragmentIntroBinding

class IntroFragment : Fragment(R.layout.fragment_intro){
    private lateinit var binding:FragmentIntroBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentIntroBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

            binding.startbtn.setOnClickListener {
                findNavController().navigate(R.id.action_introFragment_to_accountOptionFragment)
            }

    }
}