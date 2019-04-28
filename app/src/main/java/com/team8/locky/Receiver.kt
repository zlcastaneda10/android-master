package com.team8.locky

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import java.util.*
import android.R.attr.action
import android.support.v4.content.ContextCompat.getSystemService
import android.os.Vibrator





class Receiver : BroadcastReceiver() {
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    val channelId = "com.team8.locky"
    val description = "Recordatorio reserva"

    override fun onReceive(context: Context?, intent: Intent?) {
        /*val action:String = intent!!.action

        if (action.equals("reservation.end")) {
            val state = intent.extras.getString("tipo")
            Log.d("MainActivity","Receive : "+state)

        }*/

        Log.d("MainActivity","Receive : "+ Date().toString())

        val tReservaTerminar = "Tu reserva esta por terminar!";
        val cReservaTerminar = "Tu reserva termina en 10 minutos";


        val intent1 = Intent(context, LockyMenu::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 1, intent1, PendingIntent.FLAG_UPDATE_CURRENT)
        val notificationManager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
        v!!.vibrate(300)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(context, channelId)
                .setContentTitle(tReservaTerminar)
                .setContentText(cReservaTerminar)
                .setSmallIcon(R.drawable.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(context!!.resources, R.drawable.ic_launcher))
                .setContentIntent(pendingIntent)
        } else {
            builder = Notification.Builder(context)
                .setContentTitle(tReservaTerminar)
                .setContentText(cReservaTerminar)
                .setSmallIcon(R.drawable.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(context!!.resources, R.drawable.ic_launcher))
                .setContentIntent(pendingIntent)
        }
        notificationManager.notify(1, builder.build())


    }

}


