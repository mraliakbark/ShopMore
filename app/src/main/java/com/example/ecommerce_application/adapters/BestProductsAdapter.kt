package com.example.ecommerce_application.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerce_application.data.Product
import com.example.ecommerce_application.databinding.ProductRvItemBinding
import com.example.ecommerce_application.helper.getProductPrice

class BestProductsAdapter: RecyclerView.Adapter<BestProductsAdapter.BestProductsViewHolder>() {

    inner class BestProductsViewHolder(private val binding: ProductRvItemBinding):
        RecyclerView.ViewHolder(binding.root){

        fun bind(product: Product) {
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imgProduct)
                val priceAfterOffer = product.offerPercentage?.getProductPrice(product.price) ?: product.price
                tvNewPrice.text = "₹ ${String.format("%.2f", priceAfterOffer)}"

                if (product.offerPercentage != null) {
                    tvPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    tvNewPrice.visibility = View.VISIBLE
                } else {
                    tvPrice.paintFlags = 0 // Remove strike-through
                    tvNewPrice.visibility = View.INVISIBLE
                }

                tvPrice.text = "₹ ${product.price}"
                tvName.text = product.name
            }
        }

    }

    private val  diffCallback = object : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem== newItem
        }
    }
    val differ = AsyncListDiffer(this,diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestProductsViewHolder {
        return BestProductsViewHolder(
            ProductRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: BestProductsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)

        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onClick: ((Product) -> Unit)? = null

}