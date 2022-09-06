package com.lions.wantitclient.ui.principal

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.lions.wantitclient.MainActivity
import com.lions.wantitclient.R
import com.lions.wantitclient.databinding.ActivityPrincipalBinding
import com.lions.wantitclient.ui.order.OrderActivity
import com.lions.wantitclient.ui.product.Product

class Principal : AppCompatActivity() {
    private lateinit var binding:ActivityPrincipalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        binding.buttonProduct.setOnClickListener(){
            val intent = Intent(this, Product::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_sign_out -> {
                AuthUI.getInstance().signOut(this)
                    .addOnSuccessListener {
                        //Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .addOnCompleteListener {
                        if(it.isSuccessful){
                            //val intent = Intent(this, MainActivity::class.java)
                            //startActivity(intent)
                            //binding.llProgress.visibility = View.VISIBLE
                            //binding.efab.hide()
                            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
            R.id.action_order_history -> startActivity(Intent(this, OrderActivity::class.java))

           /* R.id.action_profile -> {
                val fragment = ProfileFragment()
                //val fragment = DetailFragment()   //test para ver si levanta el fragment

                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.containerMain, fragment)
                    .addToBackStack(null)
                    .commit()
            }*/

            /*R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }*/
        }
        return super.onOptionsItemSelected(item)
    }
}