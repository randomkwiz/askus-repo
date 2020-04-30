package es.iesnervion.avazquez.askus.DTOs

import com.google.gson.annotations.SerializedName

data class ComentarioDTO(
    @SerializedName("id") val id: Int,
    @SerializedName("fechaPublicacion") val fechaPublicacion: String,
    @SerializedName("idComentarioAlQueResponde ") val idComentarioAlQueResponde : Int?=0,
    @SerializedName("idUsuario") val idUsuario: Int,
    @SerializedName("isBanned") val isBanned: Boolean?=false,
    @SerializedName("isBorrado") val isBorrado: Boolean?=false,
    @SerializedName("texto") val texto: String,
    @SerializedName("titulo") val titulo: String?=""
)