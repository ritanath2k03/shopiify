package com.ritanath.shopiify.adapter

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ritanath.shopiify.data.CartSelectedProduct
import com.ritanath.shopiify.databinding.BillingRvItemBinding
import com.ritanath.shopiify.util.getPrice

class BillingAdapter: RecyclerView.Adapter<BillingAdapter.BillingViewHolder>() {

    inner class BillingViewHolder(val binding: BillingRvItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(billingProduct: CartSelectedProduct) {
            binding.apply {
                Glide.with(itemView).load(billingProduct.product.images[0]).into(imageCartProduct)
                tvProductCartName.text = billingProduct.product.name
                tvBillingProductQuantity.text = billingProduct.quantity.toString()

                val priceAfterPercentage = billingProduct.product.offerPercentage.getPrice(billingProduct.product.price)
                tvProductCartPrice.text = "$ ${String.format("%.2f", priceAfterPercentage)}"

                imageCartProductColor.setImageDrawable(ColorDrawable(billingProduct.selectedColor ?: Color.TRANSPARENT))
                tvCartProductSize.text = billingProduct.selectedSize ?: "".also { imageCartProductSize.setImageDrawable(ColorDrawable(Color.TRANSPARENT)) }
            }
        }

    }

    private val diffUtil = object : DiffUtil.ItemCallback<CartSelectedProduct>() {
        override fun areItemsTheSame(oldItem: CartSelectedProduct, newItem: CartSelectedProduct): Boolean {
            return oldItem.product == newItem.product
        }

        override fun areContentsTheSame(oldItem: CartSelectedProduct, newItem: CartSelectedProduct): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillingViewHolder {
        return BillingViewHolder(
            BillingRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: BillingViewHolder, position: Int) {
        val billingProduct = differ.currentList[position]

        holder.bind(billingProduct)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
