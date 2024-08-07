package com.example.ecommerce_application.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerce_application.data.WishlistProduct
import com.example.ecommerce_application.databinding.WishlistProductItemBinding
import com.example.ecommerce_application.helper.getProductPrice

class WishlistAdapter : RecyclerView.Adapter<WishlistAdapter.WishlistProductsViewHolder>() {

    inner class WishlistProductsViewHolder(val binding: WishlistProductItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(wishlistProduct: WishlistProduct) {
            binding.ImageDeleteFromWishlist.setOnClickListener {
                onProductDelete?.invoke(wishlistProduct)
            }
                binding.apply {
                    Glide.with(itemView).load(wishlistProduct.product.images[0])
                        .into(imageWishlistProduct)
                    tvProductWishlistName.text = wishlistProduct.product.name


                    val priceAfterPercentage =
                        wishlistProduct.product.offerPercentage.getProductPrice(wishlistProduct.product.price)
                    tvProductWishlistPrice.text = "₹ ${String.format("%.2f", priceAfterPercentage)}"

//                imageCartProductColor.setImageDrawable(ColorDrawable(cartProduct.selectedColor?: Color.TRANSPARENT))
//                tvCartProductSize.text = cartProduct.selectedSize?:"".also { imageCartProductSize.setImageDrawable(ColorDrawable(Color.TRANSPARENT)) }


                }
            }
        }

        private val diffCallback = object : DiffUtil.ItemCallback<WishlistProduct>() {
            override fun areItemsTheSame(
                oldItem: WishlistProduct,
                newItem: WishlistProduct
            ): Boolean {
                return oldItem.product.id == newItem.product.id
            }

            override fun areContentsTheSame(
                oldItem: WishlistProduct,
                newItem: WishlistProduct
            ): Boolean {
                return oldItem == newItem
            }
        }

        val differ = AsyncListDiffer(this, diffCallback)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): WishlistProductsViewHolder {
            return WishlistProductsViewHolder(
                WishlistProductItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }

        override fun onBindViewHolder(holder: WishlistProductsViewHolder, position: Int) {
            val wishlistProduct = differ.currentList[position]
            holder.bind(wishlistProduct)

            holder.itemView.setOnClickListener {
                onProductClick?.invoke(wishlistProduct)
            }
            holder.binding.ImageDeleteFromWishlist.setOnClickListener {
                onProductDelete?.invoke(wishlistProduct)
//            Log.d("hello")
            }


        }

        override fun getItemCount(): Int {
            return differ.currentList.size
        }

        var onProductClick: ((WishlistProduct) -> Unit)? = null
        var onProductDelete: ((WishlistProduct) -> Unit)? = null
//    var onPlusClick: ((CartProduct) -> Unit)? = null
//    var onMinusClick: ((CartProduct) -> Unit)? = null


    }