package com.team8.locky.Models

data class Locker(val id: String, var estado: String, val numero: Int, var precioHora: Int, var tamano: String) {
    constructor():this("","",0,0,"")
}