package es.iesnervion.avazquez.askus.DTOs

import com.google.gson.annotations.SerializedName

data class LogroDTO(@SerializedName("id") val id: Int,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("nombre") val nombre: String)