package com.lions.wantitclient.ui.order

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lions.wantitclient.R
import com.lions.wantitclient.data.model.orders.Order
import com.lions.wantitclient.databinding.ItemOrderBinding

class OrderAdapter(
    private val orderList: MutableList<Order>,
    private val listener: OnOrderListener
) :
    RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    private lateinit var context: Context

    private val aValues: Array<String> by lazy {
        context.resources.getStringArray(R.array.status_value)
    }

    private val aKeys: Array<Int> by lazy {
        context.resources.getIntArray(R.array.status_key).toTypedArray()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //debug
        Log.i("OrderAdapter", "onCreateViewHolder")
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //debug
        Log.i("OrderAdapter", "onBindViewHolder")
        val order = orderList[position]
        holder.setListener(order)
        holder.binding.tvId.text = order.id
        var names = ""
        order.products.forEach {
            names += "${it.value.name}, "
            //debug
            Log.i("OrderAdapter", "onBindViewHolder-imprimir name = $names")
        }

        holder.binding.tvProductNames.text = names.dropLast(2)

        holder.binding.tvTotalPrice.text = order.totalPrice.toString()

        val index = aKeys.indexOf(order.status)
        val statusStr =
            if (index != -1) aValues[index] else context.getString(R.string.order_status_unknown)
        holder.binding.tvStatus.text = context.getString(R.string.order_status, statusStr)
    }

    //override fun getItemCount(): Int = orderList.size
    override fun getItemCount(): Int{

        val tamanio = orderList.size
        //debug
        Log.i("OrderAdapter", "Estoy en getItemCount:: tamanio = $tamanio")
        return tamanio
    }

    fun add(order: Order) {
        //debug
        Log.i("OrderAdapter", "add(order):: order = $order")
        orderList.add(order)
        notifyItemInserted(orderList.size - 1)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemOrderBinding.bind(view)

        fun setListener(order: Order) {
            //debug
            Log.i("OrderAdapter", "inner class ViewHolder-setListener")
            binding.btnTrack.setOnClickListener {
                listener.onTrack(order)
            }

            binding.chpChat.setOnClickListener {
                listener.onStartChat(order)
            }

        }

    }
}