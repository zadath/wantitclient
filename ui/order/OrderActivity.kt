package com.lions.wantitclient.ui.order

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.lions.wantitclient.Constants
import com.lions.wantitclient.R
import com.lions.wantitclient.data.model.orders.Order
import com.lions.wantitclient.data.model.orders.OrderAux
import com.lions.wantitclient.databinding.ActivityOrderBinding
import com.lions.wantitclient.ui.chat.ChatFragment
import com.lions.wantitclient.ui.track.TrackFragment

class OrderActivity : AppCompatActivity(), OnOrderListener, OrderAux {

    private lateinit var binding: ActivityOrderBinding

    private lateinit var adapter: OrderAdapter
    private lateinit var orderSelected: Order

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //debug
        Log.i("OrderActivity", "Paso 1: Estoy en OnCreate - OrderActivity")
        setupRecyclerView()
        setupFirestore()
    }

    private fun setupRecyclerView(){
        //debug
        Log.i("OrderActivity", "Paso 2: Estoy en setupRecyclerView - OrderActivity")
        adapter = OrderAdapter(mutableListOf(), this)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@OrderActivity)
            adapter = this@OrderActivity.adapter
        }
    }

    private fun setupFirestore(){
        //debug
        Log.i("OrderActivity", "Entrando en setupFirestore")
        FirebaseAuth.getInstance().currentUser?.let{ user ->
            val db = FirebaseFirestore.getInstance()
            db.collection("requests")
                .orderBy(Constants.PROP_DATE, Query.Direction.DESCENDING)
                .whereEqualTo(Constants.PROP_CLIENT_ID, user.uid)
                //.whereIn(Constants.PROP_STATUS, listOf(3, 4))  // trae todos los pedidos que cumplan la condición del status = 1 o 3
                //.whereNotIn(Constants.PROP_STATUS, listOf(4))  //trae todos los pedidos que no tengan status = 4
                //.whereGreaterThan(Constants.PROP_STATUS, 4) //obten todos los pedidos en donde el status sea mayor a 2
                .get()
                .addOnSuccessListener {
                    //debug
                    Log.i("OrderActivity", "Entrando en setupFirestore - método addOnSuccessListener")
                    for(document in it){
                        val order = document.toObject(Order::class.java)
                        order.id = document.id
                        adapter.add(order)
                        Log.i("OrderActivity", "setupFirestore-addOnSuccessList:: Orden =$order")
                    }
                }
                .addOnFailureListener{
                    //debug
                    Log.i("OrderActivity", "Entrando en setupFirestore - método addOnFailureListener")
                    Toast.makeText(this, "Error al consultar datos en DB", Toast.LENGTH_SHORT).show()

                }
        }


    }

    override fun onTrack(order: Order) {
        orderSelected = order
        //debug
        Log.i("OrderActivity", "Entrando en onTrack")

        val fragment = TrackFragment()
        supportFragmentManager
            .beginTransaction()
            .add(R.id.containerMain, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onStartChat(order: Order) {
        //debug
        Log.i("OrderActivity", "Entrando en onStartChat")
        orderSelected = order

        val fragment = ChatFragment()

        supportFragmentManager
            .beginTransaction()
            .add(R.id.containerMain, fragment)
            .addToBackStack(null)
            .commit()
    }

    //override fun getOrderSelected(): Order = orderSelected
    override fun getOrderSelected(): Order{
        //debug
        Log.i("OrderActivity", "Entrando en getOrderSelected")
        return orderSelected
    }


}