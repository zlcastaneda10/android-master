package com.team8.locky.Models.GSON

import com.google.gson.annotations.SerializedName

data class ReservaTerminada(
    val precioTotal: Int,
    val tiempoRetorno: String,
    val idReserva: Int
)