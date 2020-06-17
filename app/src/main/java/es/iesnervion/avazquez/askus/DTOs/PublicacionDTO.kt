package es.iesnervion.avazquez.askus.DTOs

import com.google.gson.annotations.SerializedName

open class PublicacionDTO(@SerializedName("id") val id: Int,
    @SerializedName("fechaCreacion") val fechaCreacion: String? = "",
    @SerializedName("fechaPublicacion ") val fechaPublicacion: String? = "",
    @SerializedName("idAutor") val idAutor: Int,
    @SerializedName("isBorrado") val isBorrado: Boolean? = false,
    @SerializedName("isPrivada") val isPrivada: Boolean? = false,
    @SerializedName("isPublicado") val isPublicado: Boolean? = false,
    @SerializedName("texto") val texto: String,
    @SerializedName("titulo") val titulo: String? = "")