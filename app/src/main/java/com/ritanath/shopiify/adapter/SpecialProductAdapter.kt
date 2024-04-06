package com.ritanath.shopiify.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.productsadder.Product
import com.ritanath.shopiify.databinding.SpecialRvItemBinding

class SpecialProductAdapter:
    RecyclerView.Adapter<SpecialProductAdapter.SpecialProductViewHolder>() {
    inner class SpecialProductViewHolder(
        private val binding: SpecialRvItemBinding,

    ):RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product?) {
            binding.apply {
                if (product != null) {
                    Glide.with(itemView).load(product.images[0]).into(imageSpecialRvItem)
                    tvSpecialProductName.text = product.name
                    tvSpecialPrdouctPrice.text = product.price.toString()
                }

            }
        }
    }

    private val differCallBack=object:DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return  oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return  oldItem==newItem
        }

    }
    val differ=AsyncListDiffer(this,differCallBack)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialProductViewHolder {

        return SpecialProductViewHolder(SpecialRvItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ))
    }

    override fun getItemCount(): Int {

        return differ.currentList.size
        
    }

    override fun onBindViewHolder(holder: SpecialProductViewHolder, position: Int) {

        val product = differ.currentList[position]
        holder.bind(product)

    }
}