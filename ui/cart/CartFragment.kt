package com.lions.wantitclient.ui.cart

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.lions.wantitclient.Constants
import com.lions.wantitclient.R
import com.lions.wantitclient.data.model.orders.Order
import com.lions.wantitclient.data.model.orders.ProductOrder
import com.lions.wantitclient.data.model.products.OnCartListener
import com.lions.wantitclient.data.model.products.ProductCartAdapter
import com.lions.wantitclient.data.model.products.ProductModel
import com.lions.wantitclient.data.network.products.MainAux
import com.lions.wantitclient.databinding.FragmentCartBinding
import com.lions.wantitclient.ui.order.OrderActivity

class CartFragment : BottomSheetDialogFragment(), OnCartListener {

    private var binding: FragmentCartBinding? = null

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>

    private lateinit var adapter: ProductCartAdapter
    private var totalPrice = 0.0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentCartBinding.inflate(LayoutInflater.from(activity))
        binding?.let {
            val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
            bottomSheetDialog.setContentView(it.root)

            bottomSheetBehavior = BottomSheetBehavior.from(it.root.parent as View)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            setupRecyclerVieW()
            setupButtons()
            getProducts()

            return bottomSheetDialog

        }
        return super.onCreateDialog(savedInstanceState)
    }

    private fun setupRecyclerVieW() {
        binding?.let {
            adapter = ProductCartAdapter(mutableListOf(), this)

            it.recyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = this@CartFragment.adapter

            }

            /* (1..5).forEach{
                 val product = ProductModel(it.toString(), "Product $it",
                 "model is $it", "codigo es $it", "descript $it",
                 it, "color $it", "tamaño $it", "imagen", 11.2,
                         15.50, 17.7)
                 adapter.add(product)
             }*/
        }
    }

    private fun setupButtons() {
        binding?.let {
            it.ibCancel.setOnClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
            it.efab.setOnClickListener {
                //requestOrder()
                requestOrderTransaction()
            }
        }
    }

    private fun getProducts() {
        (activity as? MainAux)?.getProductsCart()?.forEach {
            adapter.add(it)
        }
    }

    private fun requestOrderTransaction() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let { myUser ->
            enableUI(false)
            val products = hashMapOf<String, ProductOrder>()
            adapter.getProducts().forEach { product ->
                products.put(
                    product.id_Product!!,
                    ProductOrder(product.id_Product!!, product.productName!!, product.newQuantity)
                )
            }

            val order = Order(
                clientId = myUser.uid,
                products = products,
                totalPrice = totalPrice,
                status = 1
            )

            val db = FirebaseFirestore.getInstance()

            //nuevo código para crear transacción en FireBase (Clase 159 udemy)
            // reservar el espacio del documento que es va a sobreescribir:
            val requestDoc = db.collection(Constants.COLL_REQUEST).document()
            val productRef =
                db.collection(Constants.COLL_PRODUCTS)  // se obtiene referencia la producto

            // para ejecutar los lotes:
            db.runBatch { batch ->
                batch.set(requestDoc, order)

                order.products.forEach {
                    batch.update(
                        productRef.document(it.key), Constants.PROP_QUANTITY,
                        FieldValue.increment(-it.value.quantity.toLong())
                    )  // en esta línea firestore hace una lectura de la cantidad actual del campo

                }

            }
                .addOnSuccessListener {
                    dismiss()
                    (activity as? MainAux)?.clearCart()
                    startActivity(Intent(context, OrderActivity::class.java))
                    Toast.makeText(activity, "Compra realizada", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    Toast.makeText(activity, "La compra no se realizó!", Toast.LENGTH_LONG).show()
                }
                .addOnCompleteListener {
                    enableUI(true)
                }

        }

    }

    private fun requestOrder() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let { myUser ->
            enableUI(false)
            val products = hashMapOf<String, ProductOrder>()
            adapter.getProducts().forEach { product ->
                products.put(
                    product.id_Product!!,
                    ProductOrder(product.id_Product!!, product.productName!!, product.newQuantity)
                )
            }

            val order = Order(
                clientId = myUser.uid,
                products = products,
                totalPrice = totalPrice,
                status = 1
            )

            val db = FirebaseFirestore.getInstance()
            db.collection("requests")
                .add(order)
                .addOnSuccessListener {
                    dismiss()
                    (activity as? MainAux)?.clearCart()
                    startActivity(Intent(context, OrderActivity::class.java))
                    Toast.makeText(activity, "Compra realizada", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    Toast.makeText(activity, "La compra no se realizó!", Toast.LENGTH_LONG).show()
                }
                .addOnCompleteListener {
                    enableUI(true)
                }

        }

    }

    private fun enableUI(enable: Boolean) {
        binding?.let {
            it.ibCancel.isEnabled = enable
            it.efab.isEnabled = enable
        }
    }

    override fun onDestroyView() {
        (activity as? MainAux)?.updateTotal()
        super.onDestroyView()
        binding = null
    }

    override fun setQuantity(product: ProductModel) {
        adapter.update(product)
    }

    override fun showTotal(total: Double) {
        totalPrice = total
        binding?.let {
            it.tvTotal.text = getString(R.string.product_full_cart, total)
        }
    }

}