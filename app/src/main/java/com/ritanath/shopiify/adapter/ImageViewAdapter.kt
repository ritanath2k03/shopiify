package com.ritanath.shopiify.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.ritanath.shopiify.databinding.ImageItemViewpagerBinding

class ImageViewAdapter: RecyclerView.Adapter<ImageViewAdapter.ImageViewAdapterViewHolder>() {
    inner class ImageViewAdapterViewHolder(val binding: ImageItemViewpagerBinding) :
        ViewHolder(binding.root) {
        fun bindNow(image: String) {
            Glide.with(itemView).load(image).into(binding.imageProductDetails)
        }
    }

    val diffCallbak=object :DiffUtil.ItemCallback<String>(){
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem==newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
               return oldItem==newItem
            }

        }
val differ=AsyncListDiffer(this,diffCallbak)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewAdapterViewHolder {
        return ImageViewAdapterViewHolder(ImageItemViewpagerBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ImageViewAdapterViewHolder, position: Int) {
        val image:String=differ.currentList[position]
        holder.bindNow(image)
    }
}