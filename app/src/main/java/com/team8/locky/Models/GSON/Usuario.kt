package com.team8.locky.Models.GSON
import com.google.gson.annotations.SerializedName

data class Usuario (
    val id : Int,
    val nombre : String,
    val celular : Int,
    val genero : String,
    val fechaNacimiento : String,
    val ocupacion : String,
    val email : String,
    val photoUrl : String
)

