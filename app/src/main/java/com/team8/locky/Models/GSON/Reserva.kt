package com.team8.locky.Models.GSON

import com.google.gson.annotations.SerializedName

data class Reserva(

        @SerializedName("tiempo_inicio")
        val tiempoInicio: String,
        @SerializedName("tiempo_fin")
        val tiempoFin: String,
        @SerializedName("precio_total")
        val precioTotal: Int,
        @SerializedName("nombre_lugar")
        val nombreLugar: String,
        @SerializedName("usuario_id")
        val usuarioId: Int,

        @SerializedName("tiempo_retorno")
        val tiempoRetorno: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("locker_id")
        val lockerId: Int





)
