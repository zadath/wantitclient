package com.lions.wantitclient.ui.product

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.lions.wantitclient.R
import com.lions.wantitclient.data.model.cart.DetailFragment
import com.lions.wantitclient.data.model.products.OnProductListener
import com.lions.wantitclient.data.model.products.ProductAdapter
import com.lions.wantitclient.data.model.products.ProductCartAdapter
import com.lions.wantitclient.data.model.products.ProductModel
import com.lions.wantitclient.data.network.products.MainAux
import com.lions.wantitclient.databinding.ActivityProductBinding
import com.lions.wantitclient.ui.cart.CartFragment

class Product : AppCompatActivity(), OnProductListener, MainAux {

    private lateinit var binding: ActivityProductBinding
    private lateinit var adapter: ProductAdapter
    private lateinit var firestoreListener: ListenerRegistration
    private var productSelected: ProductModel? = null
    val productCartList = mutableListOf<ProductModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configRecyclerView()
        configButtons()
        // binding.efab.show()

    }

    override fun onResume() {
        super.onResume()
        configFirestoreRealtime()
    }

    override fun onPause() {
        super.onPause()
        firestoreListener.remove()
    }

    private fun configRecyclerView() {
        adapter = ProductAdapter(mutableListOf(), this)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@Product, LinearLayoutManager.VERTICAL, false)
            adapter = this@Product.adapter
        }

    }

    private fun configButtons() {
        binding.btnViewCart.setOnClickListener {
            val fragment = CartFragment()
            fragment.show(
                supportFragmentManager.beginTransaction(),
                CartFragment::class.java.simpleName
            )
        }
    }


    private fun configFirestoreRealtime() {
        val db = FirebaseFirestore.getInstance()
        val productRef = db.collection("products")

        firestoreListener = productRef.addSnapshotListener { snapshots, error ->
            if (error != null) {
                Toast.makeText(this, "Error al consulta la Base de Datos", Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }

            for (snapshot in snapshots!!.documentChanges) {
                val product = snapshot.document.toObject(ProductModel::class.java)
                product.id_Product = snapshot.document.id
                when (snapshot.type) {
                    DocumentChange.Type.ADDED -> adapter.add(product)
                    DocumentChange.Type.MODIFIED -> adapter.update(product)
                    DocumentChange.Type.REMOVED -> adapter.delete(
                        (product)
                    )
                }
            }

        }

    }

    override fun onClick(product: ProductModel) {
        val index = productCartList.indexOf(product)
        if (index != -1) {
            productSelected = productCartList[index]
        } else {
            productSelected = product
        }

        val fragment = DetailFragment()
        supportFragmentManager
            .beginTransaction()
            .add(R.id.containerMain, fragment)
            .addToBackStack(null)
            .commit()

        showButton(false)
    }

    override fun getProductsCart(): MutableList<ProductModel> = productCartList

    override fun updateTotal() {
        var total = 0.0
        productCartList.forEach { product ->
            total += product.totalPrice()

        }

        if (total == 0.0) {
            binding.tvTotal.text = getString(R.string.product_empty_cart)
        } else {
            binding.tvTotal.text = getString(R.string.product_full_cart, total)
        }
    }

    override fun getProductSelected(): ProductModel? = productSelected

    override fun showButton(isVisible: Boolean) {
        binding.btnViewCart.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun addProductToCart(product: ProductModel) {
        val index = productCartList.indexOf(product)
        if (index != -1) {
            productCartList.set(index, product)
        } else {
            productCartList.add(product)
        }

        updateTotal()
    }

    override fun clearCart() {
        productCartList.clear()
    }

}