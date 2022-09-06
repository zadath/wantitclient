package com.lions.wantitclient

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.lions.wantitclient.databinding.ActivityMainBinding
import com.lions.wantitclient.ui.principal.Principal

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val response = IdpResponse.fromResultIntent(it.data)

            if (it.resultCode == RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {

                    // Obtener Token
                    val preferences = PreferenceManager.getDefaultSharedPreferences(this)
                    val token = preferences.getString("token", null)

                    token?.let {
                        val db = FirebaseFirestore.getInstance()
                        val tokenMap = hashMapOf(Pair("token", token))

                        db.collection("users")
                            .document(user.uid)
                            .collection("tokens")
                            .add(tokenMap)
                            .addOnSuccessListener {
                                Log.i("Registered token ", token)
                                preferences.edit {
                                    putString(token, null)  // limpiar token
                                        .apply()
                                }
                            }
                            .addOnFailureListener {
                                Log.i("No registered token ", token)
                            }
                    }

                    //ir al principal
                    val intent = Intent(this, Principal::class.java)
                    startActivity(intent)
                    finish()
                    //Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show()
                }
            } else {
                if (response == null) {
                    finish()
                } else {
                    response.error?.let { itError ->
                        if (itError.errorCode == ErrorCodes.NO_NETWORK) {
                            Toast.makeText(this, "No tienes conexión a internet", Toast.LENGTH_LONG)
                                .show()
                        } else {
                            Toast.makeText(this, "Código de error: $itError", Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
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

        configAuth()

        // fcm
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.i("Get token ", token.toString())
            } else {
                Log.i("Get token Fai! ", task.exception.toString())
            }
        }
    }

    private fun configAuth() {

        firebaseAuth = FirebaseAuth.getInstance()
        authStateListener = FirebaseAuth.AuthStateListener { auth ->
            if (auth.currentUser != null) {
                val intent = Intent(this, Principal::class.java)
                startActivity(intent)
            } else {
                val providers = arrayListOf(
                    AuthUI.IdpConfig.EmailBuilder().build(),
                    AuthUI.IdpConfig.GoogleBuilder().build(),
                    AuthUI.IdpConfig.PhoneBuilder().build()
                )
                // Para enlazar layout personalizado view_login.xml
                /*val loginView = AuthMethodPickerLayout
                    .Builder(R.layout.view_login)
                    .setEmailButtonId(R.id.btnEmail)
                    .setGoogleButtonId(R.id.btnGoogle)
                    .setPhoneButtonId(R.id.phone_button)
                    .build() */  // este objeto no lo estoy utilizando por error al ejecutar


                resultLauncher.launch(
                    AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false)
                        //.setAuthMethodPickerLayout(loginView)  //se omite llamado por error al ejecutar
                        .build()
                )
            }
        }
        /* try {
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                 val info = getPackageManager().getPackageInfo(
                     "com.example.ishopclient",
                     PackageManager.GET_SIGNING_CERTIFICATES)
                 for (signature in info.signingInfo.apkContentsSigners) {
                     val md = MessageDigest.getInstance("SHA");
                     md.update(signature.toByteArray());
                     Log.d("API >= 28 KeyHash:",
                         Base64.encodeToString(md.digest(), Base64.DEFAULT));
                 }
             } else {
                 val info = getPackageManager().getPackageInfo(
                     "com.example.ishopclient",
                     PackageManager.GET_SIGNATURES);
                 for (signature in info.signatures) {
                     val md = MessageDigest.getInstance("SHA");
                     md.update(signature.toByteArray());
                     Log.d("API < 28 KeyHash:",
                         Base64.encodeToString(md.digest(), Base64.DEFAULT));
                 }
             }
         } catch (e: Exception) {
             e.printStackTrace()
         }*/

    }

    override fun onResume() {
        super.onResume()
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    override fun onPause() {
        super.onPause()
        firebaseAuth.removeAuthStateListener(authStateListener)
    }
}