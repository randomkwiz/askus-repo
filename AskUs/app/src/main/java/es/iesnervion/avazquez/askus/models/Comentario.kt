package es.iesnervion.avazquez.askus.models

import com.google.gson.annotations.SerializedName

data class Comentario(
    @SerializedName("id") var id: Int,
    @SerializedName("fechaPublicacion") var fechaPublicacion: String,
    @SerializedName("idComentarioAlQueResponde ") var idComentarioAlQueResponde: Int? = 0,
    @SerializedName("idUsuario") var idUsuario: Int,
    @SerializedName("isBanned") var isBanned: Boolean? = false,
    @SerializedName("isBorrado") var isBorrado: Boolean? = false,
    @SerializedName("texto") var texto: String,
    @SerializedName("titulo") var titulo: String? = ""
)