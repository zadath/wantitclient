package com.lions.wantitclient.data.model.products

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.lions.wantitclient.R
import com.lions.wantitclient.databinding.ItemProductCartBinding

class ProductCartAdapter(
    private val productList: MutableList<ProductModel>,
    private val listener: OnCartListener) :
    RecyclerView.Adapter<ProductCartAdapter.ViewHolder>() {

    private lateinit var context: Context

    private fun calcTotal() {
        var result = 0.0
        for (product in productList) {
            result += product.totalPrice()
        }
        listener.showTotal(result)
    }

    fun getProducts(): List<ProductModel> = productList


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemProductCartBinding.bind(view)

        fun setListener(product: ProductModel) {
            binding.ibSum.setOnClickListener {
                product.newQuantity += 1
                listener.setQuantity(product)
            }

            binding.ibSub.setOnClickListener {
                product.newQuantity -= 1
                listener.setQuantity(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_product_cart, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productList[position]

        holder.setListener(product)
        holder.binding.tvName.text = product.productName
        holder.binding.tvQuantity.text = product.newQuantity.toString()

        Glide.with(context)
            .load(product.productImage)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.ic_access_time)
            .error(R.drawable.ic_broken_image)
            .centerCrop()
            .circleCrop()
            .into(holder.binding.imgProduct)
    }

    override fun getItemCount(): Int = productList.size

    fun add(product: ProductModel) {
        if (!productList.contains(product)) {
            productList.add(product)
            notifyItemInserted(productList.size - 1)
            calcTotal()
        } else {
            update(product)
        }
    }

    fun update(product: ProductModel) {
        val index = productList.indexOf(product)
        if (index != -1) {
            productList.set(index, product)
            notifyItemChanged(index)
            calcTotal()
        }
    }

}