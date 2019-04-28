package com.team8.locky

import android.app.*
import android.app.PendingIntent.getActivity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import android.view.View
import android.widget.*
import java.text.SimpleDateFormat
import java.util.*

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.AsyncTask
import android.os.Build
import android.os.Vibrator
import android.support.annotation.RequiresApi
import android.support.design.widget.CollapsingToolbarLayout
import android.util.AttributeSet
import android.util.Log
import android.widget.Toast
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import java.sql.Time
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.team8.locky.Models.Locker
import com.team8.locky.Models.GSON.Reserva
import com.team8.locky.Models.GSON.ReservaCrear
import com.team8.locky.Models.ReservaLesser
import com.team8.locky.Models.data.ApiLockyService
import kotlinx.android.synthetic.main.activity_reservation.*
import kotlinx.android.synthetic.main.fragment_my_locker_found.*
import kotlinx.android.synthetic.main.fragment_my_locker_not_found.*
import kotlinx.android.synthetic.main.reservas_record.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.net.URL


data class Reservation (val start_time: Time, val end_time: Time, val return_time: Time, val total_price: Int, val id_locker:Int )


class ReservationActivity : BaseActivity() {

    //Arreglos para los spinner
    var sizes = arrayOf("Pequeño", "Mediano", "Grande")
    //var locations =arrayOf("Alto", "Medio", "Bajo")
    var tarifas = arrayOf("$1.000", "$2.000", "$3.000")



    var textview_date: TextView? = null
    var cal = Calendar.getInstance()
    var textview_in: TextView? = null
    var textview_out: TextView? = null
    lateinit var textview_tarifa : TextView
    lateinit var tiempoInicio: String
    lateinit var tiempoFin: String
    var lugar: String = "Parque de la 93"
    lateinit var casillerosDelLugarList: MutableList<Locker>

    //Info del locker

    lateinit var tamanioLocker : String
    //Alarma
    lateinit var context: Context
    lateinit var alarmManager: AlarmManager

    override fun onCreateView(name: String?, context: Context?, attrs: AttributeSet?): View? {
        return super.onCreateView(name, context, attrs)


    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation)
        Picasso.get().load("https://bit.ly/2CFgvLU").into(toolbarImage)
        val intent = intent
        lugar = intent.getStringExtra("id_place")
        println("HAS INGRESADO A: "+lugar)
        val collapsingToolbarLayout = findViewById<CollapsingToolbarLayout>(R.id.collapsingToolbar)
        collapsingToolbarLayout.setTitle(lugar)

        casillerosDelLugarList = mutableListOf()
        FirebaseApp.initializeApp(this)
        val database = FirebaseDatabase.getInstance()
        val refLockersDelLugar = database.getReference("Lugares/"+ lugar +"/Lockers")
        refLockersDelLugar.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0!!.exists()){
                    casillerosDelLugarList.clear()
                    for(r in p0.children){
                        val casillero = r.getValue(Locker::class.java)
                        if(casillero!!.estado.equals("Disponible")){
                        casillerosDelLugarList.add(casillero!!)}
                    }
                }
            }

        });

        //Asignamos los valores a los spinners
        val size_spinner = findViewById<Spinner>(R.id.size_spinner)
        if (size_spinner != null) {
            val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sizes)
            size_spinner.adapter = arrayAdapter

            size_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    //Toast.makeText(this@ReservationActivity,  " Usted ha seleccionado " + sizes[position] ,Toast.LENGTH_LONG).show()
                    tamanioLocker = sizes[position]
                    textview_tarifa = findViewById(R.id.textViewTarifa)
                    when (tamanioLocker) {

                        sizes.get(0) -> textview_tarifa.setText(tarifas.get(0))
                        sizes.get(1) -> textview_tarifa.setText(tarifas.get(1))
                        sizes.get(2) -> textview_tarifa.setText(tarifas.get(2))
                        else -> textview_tarifa.setText("$---")
                    }


                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Code to perform some action when nothing is selected
                }
            }
        }

        // get the references from layout file
        textview_date = findViewById<TextView>(R.id.fecha_inicio_reserva)
        textview_in = findViewById(R.id.hora_inicio_reserva)
        textview_out = findViewById(R.id.hora_fin_reserva)




        // create an OnDateSetListener
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()

            }
        }

        // when you click on the button, show DatePickerDialog that is set with OnDateSetListener
        textview_date!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                var datepicker = DatePickerDialog(this@ReservationActivity,
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH))
                val today = Date().time
                datepicker.datePicker.minDate = today
                cal.time=Date()

                datepicker.datePicker.maxDate = today +31556900000
                datepicker.show()

            }

        })

    }

    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        // etc.
        val fechaReserva = findViewById<TextView>(R.id.fecha_inicio_reserva).text.toString()
        savedInstanceState.putString("fechaReserva", fechaReserva)
        println("HEYO")
        val horaInicioReserva = findViewById<TextView>(R.id.hora_inicio_reserva).text.toString()
        savedInstanceState.putString("horaInicioReserva", horaInicioReserva)
        val horaFinReserva = findViewById<TextView>(R.id.hora_fin_reserva).text.toString()
        savedInstanceState.putString("horaFinReserva", horaFinReserva)
        super.onSaveInstanceState(savedInstanceState)

    }

    public override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        findViewById<TextView>(R.id.fecha_inicio_reserva).text = savedInstanceState.getString("fechaReserva")
        findViewById<TextView>(R.id.hora_inicio_reserva).text = savedInstanceState.getString("horaInicioReserva")
        findViewById<TextView>(R.id.hora_fin_reserva).text = savedInstanceState.getString("horaFinReserva")

    }

    private fun updateDateInView() {
        val myFormat = "yyyy-MM-dd" // formato dia mes año
        val sdf = SimpleDateFormat(myFormat, Locale.UK)
        textview_date!!.setText(sdf.format(cal.getTime()))
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun clickTimePicker(view: View) {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR)
        val minute = c.get(Calendar.MINUTE)

        val tpd = TimePickerDialog(this,TimePickerDialog.OnTimeSetListener(function = { view, h, m ->
            var minutos :String
            var horas  :String
            if (h<9) horas="0" else horas =""
            if (m<9) minutos="0" else minutos =""

            textview_in!!.setText(""+horas+ h.toString() + " : "+minutos+ m )
        }),hour,minute,true)


        tpd.show()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun changeOutTime(view: View) {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR)
        val minute = c.get(Calendar.MINUTE)

        val tpd = TimePickerDialog(this,TimePickerDialog.OnTimeSetListener(function = { view, h, m ->
            var minutos :String
            var horas : String
            if (h<9) horas="0" else horas =""
            if (m<9) minutos="0" else minutos =""
            textview_out!!.setText(""+horas+ h.toString() + " : "+minutos+ m )
        }),hour,minute,false)

        tpd.show()
    }

    fun checkMyLocker(view: View){
        saveReserva()
    }

    private fun saveReserva(){
        val time = System.currentTimeMillis()
        val database = FirebaseDatabase.getInstance()
        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        val user = FirebaseAuth.getInstance().currentUser
        if(isConnected){
            //Escogiendo casillero de los disponibles segun filtro
            val tamanoEscogido =  findViewById<Spinner>(R.id.size_spinner).getSelectedItem().toString()
            /*val casillerosFiltrados: MutableList<Locker> = mutableListOf()
            for(c in casillerosDelLugarList){
                if(c.tamano.equals(tamanoEscogido)){
                    casillerosFiltrados.add(c)
                }
            }
            if(!casillerosFiltrados.isEmpty()){
                val casilleroAsignado = casillerosFiltrados[0]

                val refReservaciones = database.getReference("Reservaciones")
             */
            val idUsuario = "usuario-1"
            val contextAct = this@ReservationActivity
             try{
                    tiempoInicio = findViewById<TextView>(R.id.fecha_inicio_reserva).text.toString() + " " + findViewById<TextView>(R.id.hora_inicio_reserva).text.toString().replace("\\s".toRegex(), "")
                    println(tiempoInicio)
                    val pattern = "yyyy-MM-dd hh:mm"
                    //val fecha_inicio = SimpleDateFormat(pattern).parse(tiempoInicio)

                    tiempoFin =  findViewById<TextView>(R.id.fecha_inicio_reserva).text.toString() + " " + findViewById<TextView>(R.id.hora_fin_reserva).text.toString().replace("\\s".toRegex(), "")
                    val fecha_fin = SimpleDateFormat(pattern).parse(tiempoFin)

                   val reserva = ReservaCrear(
                        tiempoInicio,
                        tiempoFin,
                        5000,
                        tamanoEscogido,
                         lugar,
                       1
                    )

                 val activity = this
                 val apiService = ApiLockyService()
                 GlobalScope.launch(Dispatchers.Main) {
                 apiService.crearReserva(reserva).enqueue(object : Callback<ReservaCrear> {
                         override fun onResponse(call: Call<ReservaCrear>, response: Response<ReservaCrear>) {
                             Log.d("Crear Reserva", "" + response)
                             if(response.code()==500){
                                showDialogTryAgain(contextAct)
                             }
                             else if(response.code()==200){
                                 showDialogOK(contextAct, activity)
                             }
                         }

                         override fun onFailure(call: Call<ReservaCrear>, t: Throwable) {
                             println("++++++++++++++++++++++++++++++++")
                         }
                    })

                     //Toast.makeText(this@ReservationActivity, "Tu reserva ha sido realizada", Toast.LENGTH_SHORT ).show()
                     context = contextAct
                     alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                     var second =  fecha_fin.time - System.currentTimeMillis()
                     //Hacemos que suene x minutos antes de que llegue la hora final
                     var minutosAntes =10
                     second = second-(1000*60*minutosAntes)


                     val intent = Intent(context, Receiver::class.java)
                     val pendingIntent = PendingIntent.getBroadcast(context,0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                     //val pendingIntent2 = PendingIntent.getBroadcast(context,1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                     alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + second,pendingIntent)

                     //alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + second*3,pendingIntent2)
                     Log.d("MainActivity","Create : "+Date().toString())
                 }

                    //println(fecha_fin.time)

                    // println("parseo de long a date"+SimpleDateFormat(pattern).format(fecha_fin))
                    //val resta = (fecha_fin.time-fecha_inicio.time)/60000
                    //println(resta)

                    //TODO añadir las condiciones
                    /*
                    val reserva = Reserva(
                        reservaId,
                        fecha_inicio.time,
                        fecha_fin.time,
                        0,
                        idLocker = lugar + " - locker" +casilleroAsignado.numero,
                        precioTotal = casilleroAsignado.precioHora,
                        lugarReserva = lugar
                    )
                    val reservaLesser = ReservaLesser(reservaId, fecha_inicio.time, fecha_fin.time)

                    //Reserva en la colección reservas
                    refReservaciones.child(reservaId).setValue(reserva)

                    //TODO Reserva en la colección de usuarios
                    refUsuariosReservaciones.child(reservaId).setValue(reserva)

                    //Reserva en la colección de Lockers
                    refLockersReservaciones.child(reservaId).setValue(reservaLesser).addOnCompleteListener {
                        Toast.makeText(this@ReservationActivity, "Tu reserva ha sido realizada", Toast.LENGTH_SHORT ).show()
                        refLocker.child("estado").setValue("Reservado")
                        finish()

                    }*/

                    /* WIP
                    //we set a tag to be able to cancel all work of this type if needed
                    //we set a tag to be able to cancel all work of this type if needed
                    val workTag = "notificationWork"

                    //store DBEventID to pass it to the PendingIntent and open the appropriate event page on notification click
                    val inputData = Data.Builder().putInt(DBEventIDTag, DBEventID).build()
                    // we then retrieve it inside the NotifyWorker with:
                    // final int DBEventID = getInputData().getInt(DBEventIDTag, ERROR_VALUE);

                    val notificationWork = OneTimeWorkRequest.Builder(NotifyWorker::class.java)
                        .setInitialDelay(calculateDelay(event.getDate()), TimeUnit.MILLISECONDS)
                        .setInputData(inputData)
                        .addTag(workTag)
                        .build()
                    WorkManager.getInstance().enqueue(notificationWork);*/
                }catch (e: Exception){
                 val builder = AlertDialog.Builder(this@ReservationActivity)
                 // Set the alert dialog title
                 builder.setTitle("No se encontraron casilleros con el filtro seleccionado")
                 // Display a message on alert dialog
                 builder.setMessage("Por favor, intenalo de nuevo")
                 // Finally, make the alert dialog using builder
                 val dialog: AlertDialog = builder.create()
                 // Display the alert dialog on app interface
                 dialog.show()
                 println(e)
                 throw(e)
                }
            }
            /**
            else{
                Toast.makeText(this@ReservationActivity, "No se encontraron casilleros con el filtro, intentalo de nuevo", Toast.LENGTH_SHORT ).show()
            }*/

        else{
            Toast.makeText(this@ReservationActivity, "No tienes conexión a internet, por favor intenta de nuevo más tarde", Toast.LENGTH_SHORT ).show()
        }
    }
    private fun showDialogTryAgain(ctx: Context){
        val builder = AlertDialog.Builder(ctx)
        // Set the alert dialog title
        builder.setTitle("No se encontraron casilleros con el filtro seleccionado")
        // Display a message on alert dialog
        builder.setMessage("Por favor, intenalo de nuevo")
        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()
        // Display the alert dialog on app interface
        dialog.show()
    }
    private fun showDialogOK(ctx: Context, activity: Activity){
        val builder = AlertDialog.Builder(ctx)
        // Set the alert dialog title
        builder.setTitle("Tu reserva ha sido realizada")
        // Display a message on alert dialog
        builder.setMessage("Puedes administrar tus reservas en la sección 'Mis Reservas'")
        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton("OK"){dialog, which ->
            // Do something when user press the positive button
            activity.finish()
        }
        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()
        // Display the alert dialog on app interface
        dialog.show()
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
