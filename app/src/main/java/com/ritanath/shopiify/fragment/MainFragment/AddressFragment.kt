package com.ritanath.shopiify.fragment.MainFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ritanath.shopiify.data.Address
import com.ritanath.shopiify.databinding.FragmentAdderssBinding
import com.ritanath.shopiify.util.Resource
import com.ritanath.shopiify.viewmodel.AddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddressFragment : Fragment() {
    
    val viewModel by viewModels<AddressViewModel>()
    private lateinit var binding: FragmentAdderssBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenStarted {
            viewModel.addNewAddress.collectLatest {
                when (it) {
                    is Resource.Loading -> binding.progressbarAddress.visibility = View.VISIBLE
                    is Resource.Success -> {
                        binding.progressbarAddress.visibility = View.INVISIBLE
                        clearText()
                        findNavController().navigateUp()

                    }

                    is Resource.Error -> Toast.makeText(
                        requireContext(),
                        it.message,
                        Toast.LENGTH_SHORT
                    ).show()

                    else -> Unit
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.error.collectLatest {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun clearText() {

              binding.edAddressTitle.setText("")
              binding.edFullName.setText("")
              binding.edStreet.setText("")
              binding.edPhone.setText("")
              binding.edCity.setText("")
              binding.edState.setText("")



    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdderssBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            buttonSave.setOnClickListener {
                val addressTitle = edAddressTitle.text.toString()
                val fullName = edFullName.text.toString()
                val street = edStreet.text.toString()
                val phone = edPhone.text.toString()
                val city = edCity.text.toString()
                val state = edState.text.toString()
                val address = Address(addressTitle, fullName, street, phone, city, state)
                viewModel.addAddress(address)

            }
        }
    }
}