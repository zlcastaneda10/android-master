package com.team8.locky.Models.GSON

import com.google.gson.annotations.SerializedName

data class ReservaCrear(

    @SerializedName("tiempoInicio")
    val tiempoInicio: String,
    @SerializedName("tiempoFin")
    val tiempoFin: String,
    @SerializedName("precioTotal")
    val precioTotal: Int,
    @SerializedName("tamano")
    val tamano: String,
    @SerializedName("nombreLugar")
    val nombreLugar: String,
    @SerializedName("usuario_id")
    val usuarioId: Int
)
