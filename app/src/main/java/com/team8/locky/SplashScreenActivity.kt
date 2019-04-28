package com.team8.locky

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.content.Intent

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler().postDelayed({
            val intent = Intent(this,FirebaseAuth::class.java)
           startActivity(intent)
            finish()
        },1000)

    }
}
