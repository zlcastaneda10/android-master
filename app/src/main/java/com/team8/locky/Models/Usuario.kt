package com.team8.locky.Models

import com.team8.locky.Models.Reserva

class Usuario(val nombre: String, val telefono: String, val genero: String, val nacimiento: String,
              val ocupacion: String, val locky: String = "amo locky", val reservas :List<Reserva> = listOf<Reserva>()) {
}