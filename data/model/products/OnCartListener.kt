package com.lions.wantitclient.data.model.products

interface OnCartListener {
    fun setQuantity(product: ProductModel)
    fun showTotal(total: Double)
}