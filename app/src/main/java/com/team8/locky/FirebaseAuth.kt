package com.team8.locky

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import kotlinx.android.synthetic.main.activity_firebase_auth.*
import java.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.common.util.IOUtils.toByteArray
import android.content.pm.PackageManager
import android.content.pm.PackageInfo
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.view.View


class FirebaseAuth : BaseActivity  (){
    lateinit var providers : List <AuthUI.IdpConfig>
    private val MY_REQUEST_CODE: Int = 654 // any number



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_auth)
        // init (esto solo inicializa los provider y se hace una sola vez
        providers = Arrays.asList<AuthUI.IdpConfig>(
            AuthUI.IdpConfig.EmailBuilder().build(),// Email builder
            AuthUI.IdpConfig.FacebookBuilder().build(),// Facebook builder
            AuthUI.IdpConfig.GoogleBuilder().build()// Google builder
            //AuthUI.IdpConfig.PhoneBuilder().build()
        )
        //esto muestra la activity con las opciones de login
        showSignInOptions()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // aqui
        if (requestCode == MY_REQUEST_CODE)
        {
            val response = IdpResponse.fromResultIntent(data)
            if(resultCode == Activity.RESULT_OK)
            {
                finish()
                //val user = FirebaseAuth.getInstance().currentUser
                //Toast.makeText(this,""+user!!.email, Toast.LENGTH_SHORT).show()
                val intent = Intent(this, com.team8.locky.LockyMenu::class.java)
                startActivity(intent)
                
            }else{
                //Este error se genera cuando algo sale mal
                Toast.makeText(this,"Algo salio mal, intentalo de nuevo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun retry(v: View){
        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        if(isConnected) {
            showSignInOptions()
        }
        else{
            Toast.makeText(this, "No tienes conexión a internet, por favor intenta de nuevo más tarde", Toast.LENGTH_SHORT ).show()
        }
    }
    override fun onBackPressed() {
        finish()
    }
    private fun showSignInOptions() {
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                //aqui estan los providers que se agregan a la lista en el onCreate
            .setAvailableProviders(providers)
            .setLogo(R.drawable.ic_locky_big)
                //Aqui puedes modificar el color del fondo y otras cosas
            .setTheme(R.style.AppTheme)
                //es la respuesta y es lo que usa el if para decidir si ingresar o no
            .build(),MY_REQUEST_CODE)

    }

}
