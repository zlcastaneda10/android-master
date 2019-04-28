package com.team8.locky

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import app.akexorcist.bluetotohspp.library.BluetoothSPP
import app.akexorcist.bluetotohspp.library.BluetoothState
import app.akexorcist.bluetotohspp.library.DeviceList
import android.app.Activity


class MainActivity : AppCompatActivity() {
    var cont: Int = 0;
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "com.team8.locky"
    private val description = "Recordatorio reserva"

    lateinit var context: Context
    lateinit var alarmManager: AlarmManager

    lateinit var bluetooth: BluetoothSPP

    lateinit var connect: Button
    lateinit var on: Button

    var isOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // BT stuff

        bluetooth = BluetoothSPP(this)

        connect = findViewById(R.id.connect)
        on = findViewById(R.id.on)

        val CLOSE = "n"
        val OPEN = "f"

        if (!bluetooth.isBluetoothAvailable){
            Toast.makeText(getApplicationContext(), "Bluetooth no disponible en su dispositivo", Toast.LENGTH_SHORT).show();
            finish();
        }

        bluetooth.setBluetoothConnectionListener(object : BluetoothSPP.BluetoothConnectionListener {
            override fun onDeviceConnected(name: String, address: String) {
                connect.text = "Conectado a $name"
            }

            override fun onDeviceDisconnected() {
                connect.text = "Se perdio la conexion"
            }

            override fun onDeviceConnectionFailed() {
                connect.text = "No se pudo conectar"
            }
        })

        connect.setOnClickListener(object : View.OnClickListener{

            override fun onClick(v: View) {
                if (bluetooth.serviceState === BluetoothState.STATE_CONNECTED) {
                    bluetooth.disconnect()
                } else {
                    val intent = Intent(applicationContext, DeviceList::class.java)
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE)
                }
            }
        })

        on.setOnClickListener{

            if (isOpen){
                bluetooth.send(OPEN, true)
                isOpen= false
                on.setText("CERRAR")

            }else{
                bluetooth.send(CLOSE, true)
                isOpen=true
                on.setText("ABRIR")
            }
        }

        //alarm service
        context = this
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager


        //  get data from intent
        val profileName = intent.getIntExtra("id_place", 0)
        println(profileName)

        //cargar las preferencias del shared preferences
        cargarPreferencias()

        //Crear notificaciones
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        btn_notify.setOnClickListener {

            val second = 5 * 1000
            val intent = Intent(context, Receiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            val pendingIntent2 = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + second, pendingIntent)
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + second * 3, pendingIntent2)
            Log.d("MainActivity", "Create : " + Date().toString())
            guardarPreferencias()


        }


    }
//Se debe llamar solo en la actividad que genera el bluetooth
    public override fun onStart() {
        super.onStart()
        if (!bluetooth.isBluetoothEnabled) {
            bluetooth.enable()
        } else {
            if (!bluetooth.isServiceAvailable) {
                bluetooth.setupService()
                bluetooth.startService(BluetoothState.DEVICE_OTHER)
            }
        }
    }

    //Apaga el bluetooth cuando ya no se necesita
    public override fun onDestroy() {
        super.onDestroy()
        bluetooth.stopService()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bluetooth.connect(data)
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bluetooth.setupService()
            } else {
                Toast.makeText(applicationContext, "Bluetooth was not enabled.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }


    private fun cargarPreferencias() {
        val preferences: SharedPreferences = getSharedPreferences("Alarma", Context.MODE_PRIVATE)

        var hora: String = preferences.getString("hora", "No hay alarmas")

        println("la hora es: " + hora)
    }

    fun guardarPreferencias() {
        val preferences: SharedPreferences = getSharedPreferences("Alarma", Context.MODE_PRIVATE)
        var hora: String = "lo que sea"

        val editor: SharedPreferences.Editor = preferences.edit()
        cont++
        editor.putString("hora", hora + cont)
        editor.commit()

        println("Se guardaron las preferencias" + cont)
    }


}

