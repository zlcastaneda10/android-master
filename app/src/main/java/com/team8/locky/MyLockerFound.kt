package com.team8.locky

import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.support.v4.os.HandlerCompat.postDelayed
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Button
import android.widget.Toast
import app.akexorcist.bluetotohspp.library.BluetoothSPP
import app.akexorcist.bluetotohspp.library.BluetoothState
import app.akexorcist.bluetotohspp.library.DeviceList
import com.github.nisrulz.sensey.Sensey
import com.github.nisrulz.sensey.WristTwistDetector
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.team8.locky.Models.GSON.ReservaTerminada
import com.team8.locky.Models.data.ApiLockyService
import kotlinx.android.synthetic.main.fragment_my_locker_found.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.lang.NumberFormatException
import java.text.SimpleDateFormat



// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MyLockerFound.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MyLockerFound.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class MyLockerFound : Fragment(){

    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    var idReserva: String = ""
    var idLocker: String = ""
    var lugarReserva : String? = ""
    var tiempoFin: Long = 0
    var precioTotal: Int = 0
    val ha: Handler = Handler()
    //BT Stuff
    lateinit var bluetooth: BluetoothSPP
    lateinit var connect: Button
    lateinit var on: Button
    lateinit var sound: Button
    var isOpen = true
    val CLOSE = "c"
    val OPEN = "o"
    val SOUND = "s"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        Sensey.getInstance().init(context);

        val wristTwistListener = WristTwistDetector.WristTwistListener {
            if (isOpen){
                bluetooth.send(CLOSE, true)
                isOpen= false
                on.setText("DESBLOQUEAR")

            }else{
                bluetooth.send(OPEN, true)
                isOpen=true
                on.setText("BLOQUEAR")
            }
        }
        Sensey.getInstance().startWristTwistDetection(wristTwistListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        idReserva = getArguments()!!.getString("idReserva")
        idLocker = getArguments()!!.getString("idLocker")
        lugarReserva = getArguments()!!.getString("lugarReserva")
        val tiempoFinString = getArguments()!!.getString("tiempoFin")
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val output = formatter.format(parser.parse(tiempoFinString))
        val date = format.parse(output)
        tiempoFin = date.getTime();
        precioTotal =getArguments()!!.getInt("precioTotal")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_locker_found, container, false)
    }



    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val database = FirebaseDatabase.getInstance()
        //val refUsuariosReservaciones = database.getReference("Usuarios/usuario-1/Reservaciones")
        //val refReservaciones = database.getReference("Reservaciones")
        val refLocker = database.getReference("Lugares/"+lugarReserva+"/Lockers/"+idLocker)
        val btn_devolverCasillero = view.findViewById<Button>(R.id.devolverCasillero) as Button
        val messageToUser = "No tienes conexión, pero el casillero se ha devuelto" //TODO

        btn_devolverCasillero.setOnClickListener {
            try{

                val time = System.currentTimeMillis()
                val connectivityManager = getActivity()!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                if(!isConnected){
                    Toast.makeText(getActivity()!!, "No tienes conexión, pero el casillero se ha devuelto", Toast.LENGTH_SHORT ).show()
                    btn_devolverCasillero.setActivated(false)
                    btn_devolverCasillero.isClickable=false
                    btn_devolverCasillero.text = "DEVUELTO"
                    btn_devolverCasillero.setBackgroundColor(Color.GRAY);
                    ha.removeCallbacksAndMessages(null)
                }
                val format = SimpleDateFormat("yyyy-MM-dd HH:mm")
                val actualDateFormat = format.format(time)
                val reservaTerminada = ReservaTerminada(precioTotal,actualDateFormat,Integer.parseInt(idReserva))
                println("precio"+precioTotal)
                println("fecha"+actualDateFormat)
                println("idReserva"+Integer.parseInt(idReserva))
                val apiService = ApiLockyService()
                val activity = getActivity()
                //MANEJO SIN CONEXION TODO
                GlobalScope.launch(Dispatchers.Main) {
                    apiService.terminarReserva(reservaTerminada).enqueue(object :
                        retrofit2.Callback<ReservaTerminada> {
                        override fun onResponse(call: Call<ReservaTerminada>, response: Response<ReservaTerminada>) {
                            Log.d("Reserva Terminada","")
                            try{
                                activity!!.onBackPressed()}
                            catch(e: Exception){
                            }
                        }

                        override fun onFailure(call: Call<ReservaTerminada>, t: Throwable) {
                            Log.d("Reserva Terminada Fallo", "" + t)
                        }
                    })

                }

                //refUsuariosReservaciones.child(idReserva).child("tiempoRetorno").setValue(time)
                //refReservaciones.child(idReserva).child("tiempoRetorno").setValue(time).addOnCompleteListener()
                /*.addOnFailureListener(){
                    Toast.makeText(getActivity()!!, "Ocurrio un error en la transacción ", Toast.LENGTH_SHORT ).show()
                }*/
            }
            catch(e:Exception){
                Toast.makeText(getActivity()!!, "Ocurrio un error en la transacción ", Toast.LENGTH_SHORT ).show()
                println(e.message)
            }
        }
        val textViewnumeroCasilero = view.findViewById<TextView>(R.id.numeroCasillero)
        var numeroCasillero = idLocker
        textViewnumeroCasilero.setText("LOCKER "+ numeroCasillero.toString())
        val textViewlugarReserva = view.findViewById<TextView>(R.id.lugarCasillero)
        textViewlugarReserva.setText(lugarReserva)
        val textViewprecioTotal = view.findViewById<TextView>(R.id.precioTotal)
        textViewprecioTotal.setText("$" + precioTotal)
        val textViewtiempoRestante = view.findViewById<TextView>(R.id.tRestanteCasillero)
        println("TiempoFin"+tiempoFin)
        println("TiempoCurrent"+(tiempoFin - System.currentTimeMillis())/60000)
        var tiempoRestante = ((tiempoFin - System.currentTimeMillis())/60000)
        textViewtiempoRestante.setText("" + tiempoRestante + "m")
        if(tiempoRestante<0) {
            textViewtiempoRestante.setTextColor(Color.parseColor("#DC143C"))
        }
        ha.postDelayed(object : Runnable {

            override fun run() {
                tiempoRestante = (tiempoFin - System.currentTimeMillis())/60000
                textViewtiempoRestante.setText(""+tiempoRestante+" m")
                ha.postDelayed(this, 60000)
                if(tiempoRestante<0) {
                    textViewtiempoRestante.setTextColor(Color.parseColor("#DC143C"))
                }
            }
        }, 60000)


        //BT stuff

        bluetooth = BluetoothSPP(getActivity())
        connect = view.findViewById(R.id.conectar)
        on = view.findViewById(R.id.abrir)
        sound = view.findViewById(R.id.bell)

        on.setEnabled(false)
        on.setBackgroundColor(Color.GRAY)

        if (!bluetooth.isBluetoothAvailable){
            Toast.makeText(getActivity()!!, "Bluetooth no disponible en su dispositivo", Toast.LENGTH_SHORT).show()
            getActivity()!!.onBackPressed()
        }

        bluetooth.setBluetoothConnectionListener(object : BluetoothSPP.BluetoothConnectionListener {
            override fun onDeviceConnected(name: String, address: String) {
                connect.text = "Conectado a $name"
                on.setEnabled(true)
                on.setBackgroundColor(Color.parseColor("#03a8f2"));
            }

            override fun onDeviceDisconnected() {
                connect.text = "Se perdio la conexion"
                on.setEnabled(false)
                on.setBackgroundColor(Color.GRAY)
            }

            override fun onDeviceConnectionFailed() {
                connect.text = "No se pudo conectar"
                on.setEnabled(false)
                on.setBackgroundColor(Color.GRAY)
            }
        })

        connect.setOnClickListener(object : View.OnClickListener{

            override fun onClick(v: View) {
                if (bluetooth.serviceState === BluetoothState.STATE_CONNECTED) {
                    bluetooth.disconnect()
                } else {
                    val intent = Intent(getActivity()!!, DeviceList::class.java)
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE)
                }
            }
        })

        on.setOnClickListener{

            if (isOpen){
                bluetooth.send(CLOSE, true)
                isOpen= false
                on.setText("DESBLOQUEAR")

            }else{
                bluetooth.send(OPEN, true)
                isOpen=true
                on.setText("BLOQUEAR")
            }
        }

        sound.setOnClickListener{bluetooth.send(SOUND, true)}


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
                Toast.makeText(getActivity()!!, "Bluetooth was not enabled.", Toast.LENGTH_SHORT).show()
                getActivity()!!.onBackPressed()
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyLockerFound.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyLockerFound().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
