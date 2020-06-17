package es.iesnervion.avazquez.askus.DTOs

import com.google.gson.annotations.SerializedName

data class VotoModeracionDTO(@SerializedName("idUsuarioModerador") val idUsuario: Int,
    @SerializedName("idPublicacion") val idPublicacion: Int,
    @SerializedName("valoracion") val valoracion: Boolean,
    @SerializedName("fechaHoraEmisionVoto") val fechaHoraEmisionVoto: String)