package es.iesnervion.avazquez.askus.DTOs

import com.google.gson.annotations.SerializedName

data class PostModeracionDTO(@SerializedName("idPublicacion") val idPublicacion: Int,
    @SerializedName("tituloPost") val tituloPost: String,
    @SerializedName("cuerpoPost") val cuerpoPost: String)
