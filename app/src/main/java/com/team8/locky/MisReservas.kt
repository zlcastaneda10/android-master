package com.team8.locky

import android.app.PendingIntent.getActivity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.team8.locky.Models.GSON.*

import com.team8.locky.Models.data.ApiLockyService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MisReservas.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MisReservas.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class MisReservas : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    lateinit var ref: DatabaseReference
    lateinit var reservasList: MutableList<Reserva>
    lateinit var listView: ListView
    lateinit var swipeLayout: SwipeRefreshLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        FirebaseApp.initializeApp(getActivity() as Context)
        val database = FirebaseDatabase.getInstance()
        reservasList = mutableListOf()
        ref = database.getReference("Usuarios/usuario-1/Reservaciones")
        return inflater.inflate(R.layout.fragment_mis_reservas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView = getView()!!.findViewById<ListView>(R.id.reservasListView)
        val connectivityManager = this.context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        val apiService = ApiLockyService()
        if(isConnected) {
            GlobalScope.launch(Dispatchers.Main) {
                reservasList = apiService.historialReservasActivas("1").await() as MutableList<Reserva>
                val adapter = ReservaAdapter(LockyMenu.ctx, R.layout.reservas, reservasList)
                listView.adapter = adapter
            }
        }


        swipeLayout = view!!.findViewById(R.id.swiperefresh) as SwipeRefreshLayout
        swipeLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            val connectivityManager = this.context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
            if(isConnected) {
            GlobalScope.launch(Dispatchers.Main) {
                reservasList = apiService.historialReservasActivas("1").await() as MutableList<Reserva>
                val adapter = ReservaAdapter(LockyMenu.ctx,R.layout.reservas,reservasList)
                listView.adapter = adapter
             }
            }
            swipeLayout.isRefreshing = false
        })
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val connectivityManager = this.context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        if(isConnected) {
            val apiService = ApiLockyService()
            GlobalScope.launch(Dispatchers.Main) {

                // Llamar a historial entero
                val historialResponse = apiService.historialReservas("1").await()
                for (i in historialResponse) {
                    println(i)
                }


            // Historial de reservas activas
            val historialResponse2 = apiService.historialReservasActivas("1").await()
            for (i in historialResponse2) { println("h2: "+i) }


                // Crear Usuarios
                /*val testUser = Usuario(
                    8,
                    "Zulma",
                    4545456,
                    "Femenino",
                    "1994",
                    "sad",
                    "asd@asd.com",
                    "https://www.elgrafico.mx/sites/default/files/styles/f5-689x388/public/2016/10/27/collagelalalalalla.jpg?itok=N2pKxxp1&c=72a7ae1c2e0560aba12c47edf68112b4"
                )
                apiService.crearUsuario(testUser).enqueue(object : Callback<Usuario> {
                    override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {

                        Log.d("WebServices", "" + response)

                    }

                    override fun onFailure(call: Call<Usuario>, t: Throwable) {
                        // loginResponseInterface.onSuccess("","", loginDetail);
                        Log.d("WebServices", "" + t)
                    }
                })*/


            // Crear Reserva
                /*
            val testReserva = Reserva(
                "26-04-2019",
                44,
                1,
                3000,
                "26-04-2019 15:44",
                "26-04-2019 16:44",
                "",
                "upd",
                1,
                "ParqueCedritos"
            )
            apiService.crearReserva(testReserva).enqueue(object : Callback<Reserva> {
                override fun onResponse(call: Call<Reserva>, response: Response<Reserva>) {
                    Log.d("Crear Reserva", "" + response)
                }

                override fun onFailure(call: Call<Reserva>, t: Throwable) {
                    Log.d("Crear Reserva", "" + t)
                }
            })*/

            //Abrir Casillero
            /*val testCasillero = Casillero("token",1)
            apiService.abrirCasillero(testCasillero).enqueue(object : Callback<Casillero> {
                override fun onResponse(call: Call<Casillero>, response: Response<Casillero>) {
                    Log.d("Abrir Casillero", "" + response)
                }

                override fun onFailure(call: Call<Casillero>, t: Throwable) {
                    Log.d("Abrir Casillero", "" + t)
                }
            })*/

            // Terminar una Reserva
            val testReservaTerminada = ReservaTerminada(3000,"2019-04-27 15:30", 3)
            apiService.terminarReserva(testReservaTerminada).enqueue(object : Callback<ReservaTerminada> {
                override fun onResponse(call: Call<ReservaTerminada>, response: Response<ReservaTerminada>)
                {
                    Log.d("Reserva Terminada ", "" + response)
                }

                override fun onFailure(call: Call<ReservaTerminada>, t: Throwable) {
                    Log.d("Reserva Terminada", "" + t)
                }
            })

            // Dar casilleros de un lugar (Puede que no funcione porque no esta implementado en back)
               //val casillerosResponse = apiService.casillerosLugar("1").await() /
                // for ( i in casillerosResponse) { println ("Casillero:" + i)}

            }
        }


    }
    override fun onResume() {
        super.onResume()

        val lockyMenu = getActivity()!! as LockyMenu
        lockyMenu.setFragmentMisCasilleros()
        val apiService = ApiLockyService()
        val connectivityManager = this.context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        if(isConnected) {
            GlobalScope.launch(Dispatchers.Main) {
                reservasList = apiService.historialReservasActivas("1").await() as MutableList<Reserva>
                val adapter = ReservaAdapter(LockyMenu.ctx,R.layout.reservas,reservasList)
                listView.adapter = adapter
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }


        override fun onDetach() {
        super.onDetach()
        listener = null
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
         * @return A new instance of fragment MisReservas.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MisReservas().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
