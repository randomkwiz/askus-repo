package es.iesnervion.avazquez.askus.DTOs

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class VotoPublicacionDTO(@SerializedName("idUsuario") val idUsuario: Int,
    @SerializedName("idPublicacion") val idPublicacion: Int,
    @SerializedName("valoracion") val valoracion: Boolean,
    @SerializedName("fechaHoraEmisionVoto") val fechaHoraEmisionVoto: String) : Serializable