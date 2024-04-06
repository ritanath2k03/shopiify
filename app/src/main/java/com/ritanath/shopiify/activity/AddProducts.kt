package com.ritanath.shopiify.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.productsadder.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.ritanath.shopiify.databinding.ActivityAddProductsBinding
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.ColorPickerView
import com.skydoves.colorpickerview.kotlin.colorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.util.UUID

class AddProducts : AppCompatActivity() {

    private val storage = Firebase.storage.reference
    private val selectedImages = mutableListOf<Uri>()
    private val colors = mutableListOf<Int>()
    private val firestore = Firebase.firestore
    private val binding by lazy { ActivityAddProductsBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.apply {
            binding.buttonupload.setOnClickListener {
                val validattion = validateProducts()
                if (validattion) {
                    saveProducts()
                }
            }
        }
        binding.buttonColorPicker.setOnClickListener {
            ColorPickerDialog.Builder(this)
                .setPositiveButton("Select", object : ColorEnvelopeListener {
                    override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                        envelope?.let {
                            colors.add(it.color)
                            addColorToList()
                        }
                    }

                })
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }

        val selectedImageFromUser =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    if (intent?.clipData != null) {
                        val count = intent.clipData?.itemCount ?: 0
                        for (i in 0 until count) {
                            val uriImage = intent.clipData?.getItemAt(i)?.uri
                            uriImage?.let {
                                selectedImages.add(it)
                            }
                        }
                    } else {
                        val uriImage = intent?.data
                        uriImage?.let {
                            selectedImages.add(it)

                        }
                    }
                    updateImage() // Assuming this method updates the UI with the selected images
                }
            }

        binding.buttonImagesPicker.setOnClickListener {
            val imageIntent = Intent(Intent.ACTION_GET_CONTENT)
            imageIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            imageIntent.type = "image/*"
            selectedImageFromUser.launch(imageIntent) // Pass imageIntent instead of intent
        }

    }

    private fun updateImage() {
        binding.tvSelectedImages.text = selectedImages.size.toString()
    }


    private fun addColorToList() {
        var c = ""
        colors.forEach { cc ->
            c = "$c${Integer.toHexString(cc)}  "
        }
        binding.tvSelectedColors.setText("$c ")
    }

    private fun saveProducts() {
        binding.buttonupload.startAnimation()
        val allSizes = getSizes(binding.edSizes.text.toString().trim())
        val name = binding.edName.text.toString().trim()
        val price = binding.edPrice.text.toString().trim()
        val catagory = binding.edCategory.text.toString().trim()
        val imageArray = getImages()
        val description = binding.edDescription.text.toString().trim()
        val offer = binding.offerPercentage.text.toString().trim()
        val images = mutableListOf<String>()
        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                binding.buttonupload.startAnimation()
            }
            try {
                async {
                    imageArray.forEach {
                        val id = UUID.randomUUID().toString()
                        launch {
                            val imageStorage = storage.child("products/images/$id")
                            val res = imageStorage.putBytes(it).await()
                            val downloadUrl = res.storage.downloadUrl.await().toString()
                            images.add(downloadUrl)
                        }
                    }
                }.await()
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    binding.buttonupload.revertAnimation()
                }
            }
            val product = Product(
                UUID.randomUUID().toString(),
                name,
                catagory,
                price.toFloat(),
                if (offer.isEmpty()) null else offer.toFloat(),
                if (description.isEmpty()) null else description,
                if (colors.isEmpty()) null else colors,
                sizes = null,
                images
            )
            firestore.collection("Products").add(product).addOnSuccessListener {
                Log.d("Tag", "Product Added")
                binding.buttonupload.revertAnimation()
                binding.edName.setText("")
                binding.edCategory.setText("")
                binding.edPrice.setText("")
                binding.offerPercentage.setText("")
                binding.edSizes.setText("")
                colors.clear()
                images.clear()
            }.addOnFailureListener {
                Log.d("Tag", it.toString())
                binding.buttonupload.revertAnimation()
            }
        }
    }

    private fun getImages(): List<ByteArray> {
        val imageArray = mutableListOf<ByteArray>()
        selectedImages.forEach {
            val stream = ByteArrayOutputStream()
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)) {
                imageArray.add(stream.toByteArray())
            }
        }
        return imageArray
    }

    private fun getSizes(toString: String): List<String>? {
        if (toString.isEmpty()) return null
        return toString.split(",")
    }

    private fun validateProducts(): Boolean {

        if (binding.edPrice.text.toString().trim().isEmpty()) return false
        if (binding.edName.text.toString().trim().isEmpty()) return false
        if (binding.edCategory.text.toString().trim().isEmpty()) return false
        if (selectedImages.isEmpty()) return false
        return true
    }


}