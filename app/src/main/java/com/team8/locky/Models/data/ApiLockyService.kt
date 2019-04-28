package com.team8.locky.Models.data

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.team8.locky.Models.GSON.*
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.*


const val API_KEY = "89e8bd89085b41b7a4b142029180210"

//http://api.apixu.com/v1/current.json?key=89e8bd89085b41b7a4b142029180210&q=London&lang=en

interface ApiLockyService {

    // Toma el historial de reservas activas de un Usuario
    @GET("reservas/{id}/actuales")
    fun historialReservasActivas(
        @Path ("id") userId: String = "1"
    ) : Deferred<List<Reserva>>

    // Toma el historial de reserva totales de un Usuario
    @GET("reservas/{id}/historial")
    fun historialReservas(
            @Path("id") userId: String = "1"
    ): Deferred<List<Reserva>>

    // Ver los casilleros de un lugar dado un id
    /*@GET("casilleros/lugar")
    fun casillerosLugar(
        @Path ("id") lugarId: String = "1"
    ) : Deferred<List<Lugar>>*/


    // Crea un usuario
    @POST("usuarios/crear")
    fun crearUsuario(
        @Body usuario: Usuario
    ): Call<Usuario>

    // Crea una reserva
    @POST("reservas/crear")
    fun crearReserva(
        @Body reserva: ReservaCrear
    ) : Call <ReservaCrear>

    // Abrir un Casillero
    /*@POST ("abrir")
    fun abrirCasillero(
        @Body casillero: Casillero
    ) : Call <Casillero>*/

    // Termina una reserva
    @POST ("reservas/terminar")
    fun terminarReserva(
        @Body reservaTerminada: ReservaTerminada
    ) : Call <ReservaTerminada>




    companion object {
        operator fun invoke(): ApiLockyService {
            val okHttpClient = OkHttpClient.Builder()
                .build()

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://locky-back.herokuapp.com/api/")
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiLockyService::class.java)
        }
    }
}