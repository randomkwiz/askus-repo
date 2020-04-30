package es.iesnervion.avazquez.askus.models

import com.google.gson.annotations.SerializedName

data class VotoModeracion(
    @SerializedName("idUsuario") var idUsuario: Int,
    @SerializedName("idPublicacion") var idPublicacion: Int,
    @SerializedName("valoracion") var valoracion: Boolean,
    @SerializedName("fechaHoraEmisionVoto") var fechaHoraEmisionVoto: String
)