package com.lions.wantitclient.data.network.products

import com.lions.wantitclient.data.model.products.ProductModel

interface MainAux {
    fun getProductsCart(): MutableList<ProductModel>
    fun updateTotal()
    fun getProductSelected(): ProductModel?
    fun showButton(isVisible: Boolean)
    fun addProductToCart(product: ProductModel)
    fun clearCart()
}