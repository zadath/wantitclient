package com.lions.wantitclient.ui.order

import com.lions.wantitclient.data.model.orders.Order

interface OnOrderListener {
    fun onTrack(order: Order)
    fun onStartChat(order: Order)

}