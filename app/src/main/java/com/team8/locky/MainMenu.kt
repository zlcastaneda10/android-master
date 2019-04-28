package com.team8.locky

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.*
import com.team8.locky.ui.mainmenu.MainMenuFragment

class MainMenu : AppCompatActivity() {
    lateinit var ref: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainMenuFragment.newInstance())
                .commitNow()
        }

        val database = FirebaseDatabase.getInstance()
        ref = database.getReference("Lockers/Parque de la 95")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0!!.exists()) {
                    println(p0.value)
                }
            }
        });
    }

}
