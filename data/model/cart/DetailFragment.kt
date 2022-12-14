package com.lions.wantitclient.data.model.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.lions.wantitclient.R
import com.lions.wantitclient.data.model.products.ProductModel
import com.lions.wantitclient.data.network.products.MainAux
import com.lions.wantitclient.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {
    private var binding: FragmentDetailBinding? = null
    private var product: ProductModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        binding?.let {
            return it.root
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getProduct()
        setupButtons()
    }


    private fun getProduct() {
        product = (activity as? MainAux)?.getProductSelected()
        product?.let { product ->
            binding?.let {
                it.tvName.text = product.productName
                it.tvDescription.text = product.description
                it.tvQuantity.text = getString(R.string.detail_quantity, product.quantity)
                setNewQuantity(product)
                /* it.tvTotalPrice.text = getString(R.string.detail_total_price, product.totalPrice(),
                        product.newQuantity, product.priceBase)*/

                Glide.with(this)
                    .load(product.productImage)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_access_time)
                    .error(R.drawable.ic_broken_image)
                    .fitCenter()
                        .centerCrop()
                    .into(it.imgProduct)
            }

        }
    }

    private fun setNewQuantity(product: ProductModel) {
        binding?.let {
            it.etNewQuantity.setText(product.newQuantity.toString())

            val newQuantityStr = getString(
                R.string.detail_total_price, product.totalPrice(),
                product.newQuantity, product.priceBase
            )
            it.tvTotalPrice.text =
                HtmlCompat.fromHtml(newQuantityStr, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
    }

    private fun setupButtons() {
        product?.let { product ->
            binding?.let { binding ->
                binding.ibSub.setOnClickListener {
                    if (product.newQuantity > 1) {
                        product.newQuantity -= 1
                        setNewQuantity(product)
                    }
                }

                binding.ibSum.setOnClickListener {
                    if (product.newQuantity < product.quantity) {
                        product.newQuantity += 1
                        setNewQuantity(product)
                    }
                }

                binding.efab.setOnClickListener {
                    product.newQuantity = binding.etNewQuantity.text.toString().toInt()
                    addToCart(product)
                }

            }

        }
    }

    private fun addToCart(product: ProductModel) {
        (activity as? MainAux)?.let {
            it.addProductToCart(product)
            activity?.onBackPressed()
        }
    }

    override fun onDestroyView() {
        (activity as? MainAux)?.showButton(true)
        super.onDestroyView()
        binding = null
    }

}