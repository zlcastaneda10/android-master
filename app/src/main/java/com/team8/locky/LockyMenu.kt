package com.team8.locky

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.team8.locky.Models.GSON.Reserva
import com.team8.locky.mapMenu.MapActivity
import kotlinx.android.synthetic.main.activity_firebase_auth.*
import kotlinx.android.synthetic.main.activity_locky_menu.*
import com.google.firebase.auth.FirebaseAuth
import android.support.v4.content.ContextCompat.startActivity
import android.Manifest.permission
import android.Manifest.permission.CALL_PHONE
import android.support.v4.app.ActivityCompat
import android.os.Build
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.team8.locky.Models.data.ApiLockyService
import kotlinx.android.synthetic.main.reservas_record.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class LockyMenu : BaseActivityLockyMenu() {

    lateinit var ref: DatabaseReference
    lateinit var reservasList: MutableList<Reserva>
    val misReservas: MisReservas = MisReservas()
    val reservasNotFound: MyLockerNotFound = MyLockerNotFound()


    companion object {
        lateinit var ctx: Context
    }

    init {
        ctx = this
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_busqueda -> {
                println("search pressed")
                setFragment(Search())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_miCasillero -> {
                println("myLocker pressed")
                setFragmentMisCasilleros()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_perfil -> {
                println("profile pressed")
                setFragment(Profile())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    fun goToMap(v: View) {
        val intent = Intent(this, MapActivity::class.java)
        startActivity(intent)
    }

    fun goToLogin(v:View){
        val intent = Intent(this, FirebaseAuth::class.java)
        startActivity(intent)
    }


    fun goToMain(v: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun logOut(v: View){
            //signouts
            AuthUI.getInstance().signOut(this@LockyMenu)
                .addOnCompleteListener {
                    //btn_sign_out.isEnabled = false
                    //showSignInOptions()
                    finish()
                    val intent = Intent(this, com.team8.locky.FirebaseAuth::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this@LockyMenu, e.message, Toast.LENGTH_SHORT).show()
                }
    }
    fun showRecord(v: View) {
        val record = Record()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_frame, record).addToBackStack("tag")
        fragmentTransaction.commit()
    }
    fun contact(v: View) {
        if(isPermissionGranted()){
            call_action();
        }

    }
    fun call_action(){
        val intent = Intent(Intent.ACTION_CALL)

        intent.data = Uri.parse("tel:3053871079" )
        startActivity(intent)
    }

    fun isPermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG", "Permission is granted")
                return true
            } else {

                Log.v("TAG", "Permission is revoked")
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 1)
                return false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG", "Permission is granted")
            return true
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {

            1 -> {

                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(applicationContext, "Permission granted", Toast.LENGTH_SHORT).show()
                    call_action()
                } else {
                    Toast.makeText(applicationContext, "Permission denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }// other 'case' lines to check for other
        // permissions this app might request
    }
    fun showHowToLocky(v: View) {
        val howToLocky = HowToLocky()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_frame, howToLocky).addToBackStack("tag")
        fragmentTransaction.commit()
    }
    fun goToDetails(v: View) {
        val idReserva = v.tag
        lateinit var selectedReserva: Reserva
        println("idSELECCIÓN:"+idReserva)
        for(r in reservasList){
           println("RESERVAS"+r.id)
           if(r.id==idReserva){
               selectedReserva = r
           }
        }
        val bundle = Bundle()
        bundle.putString("idReserva",selectedReserva.id.toString())
        bundle.putString("idLocker", selectedReserva.lockerId.toString())
        bundle.putString("lugarReserva", selectedReserva.nombreLugar)
        bundle.putString("tiempoFin", selectedReserva.tiempoFin)
        bundle.putInt("precioTotal", selectedReserva.precioTotal)
        val mylocker = MyLockerFound()
        mylocker.setArguments(bundle)
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_frame, mylocker).addToBackStack("tag")
        fragmentTransaction.commit()

    }

    fun goToReservation(v:View){
        val intent = Intent(this, ReservationActivity::class.java)
        intent.putExtra("id_place",1)
        startActivity(intent)

        /**
        val intent = Intent(this, MainMenu::class.java)
        intent.putExtra("id_place",1)
        startActivity(intent)*/
    }

    fun updateMyLocker(){

    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_locky_menu)
        FirebaseApp.initializeApp(this)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        reservasList = mutableListOf()
        val database = FirebaseDatabase.getInstance()
        ref = database.getReference("Reservaciones")
        setFragment(Search())
        val apiService = ApiLockyService()
        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        if(isConnected) {
            GlobalScope.launch(Dispatchers.Main) {
                reservasList = apiService.historialReservasActivas("1").await() as MutableList<Reserva>
                for (i in reservasList) {
                    println(i)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val apiService = ApiLockyService()
        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        if(isConnected) {
            GlobalScope.launch(Dispatchers.Main) {
                reservasList = apiService.historialReservasActivas("1").await() as MutableList<Reserva>
                for (i in reservasList) {
                    println(i)
                }
            }
        }
    }

    fun setFragment(fragment: Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_frame, fragment)
        fragmentTransaction.commit()
    }

    fun setFragmentMisCasilleros(){
        if(reservasList.isEmpty()) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.main_frame, reservasNotFound)
            fragmentTransaction.commit()
        }
        else{
            for(r in reservasList){
                println(r.id)
                println("WTF")
            }

            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.main_frame, misReservas)
            fragmentTransaction.commit()
        }
    }
}
