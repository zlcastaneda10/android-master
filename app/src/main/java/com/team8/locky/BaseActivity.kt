package com.team8.locky


import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View


/** Author : http://devdeeds.com
 *  Project : Sample Project - Internet status checking
 *  Date : 24 Feb 2018*/


//THIS IS THE BASE ACTIVITY OF ALL ACTIVITIES OF THE APPLICATION.

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {
    private var mSnackBar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiver(ConnectivityReceiver(),
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }


    private fun showMessage(isConnected: Boolean) {



        if (!isConnected) {

            val messageToUser = "No estas conectado a internet." //TODO
            mSnackBar = Snackbar.make(findViewById(android.R.id.content), messageToUser, Snackbar.LENGTH_LONG) //Assume "rootLayout" as the root layout of every activity.
            mSnackBar?.duration = Snackbar.LENGTH_INDEFINITE
            mSnackBar?.show()
        } else {
            mSnackBar?.dismiss()
        }


    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
        println("NANANANA")
        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        showMessage(isConnected)
    }

    /**
     * Callback will be called when there is change
     */
    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showMessage(isConnected)
    }
}