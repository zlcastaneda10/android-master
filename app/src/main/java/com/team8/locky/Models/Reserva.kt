package com.team8.locky.Models

class Reserva(val id: String?, val tiempoInicio: Long, val tiempoFin: Long,
              var tiempoRetorno: Long, var precioTotal: Int = 0, val idLocker: String = "001",
              val idUsuario: String = "usuario-1", val lugarReserva: String = "Parque NA"){

    constructor():this("",0,0,0
    ,0,"","")
}