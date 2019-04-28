package com.team8.locky.Models

import com.team8.locky.Models.Locker

class Lugar (val id: String, val latitud: Int, val longitud: String,
             val descripcion: String, val foto: String, val tipo: String,  val localidad:String, val nombre: String,
             val lockers :List<Locker>){
}