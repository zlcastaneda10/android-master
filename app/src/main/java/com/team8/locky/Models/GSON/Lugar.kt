package com.team8.locky.Models.GSON
import com.google.gson.annotations.SerializedName

data class Lugar (

    @SerializedName("lugar_id")
    val id : Int,
    val nombre : String,
    val localidad: String,
    val tipo : String,
    val createdAt : String,
    val updatedAt : String
)

