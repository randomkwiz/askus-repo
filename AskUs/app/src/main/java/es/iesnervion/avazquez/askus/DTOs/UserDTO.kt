package es.iesnervion.avazquez.askus.DTOs

import com.google.gson.annotations.SerializedName

//Aquí es donde se recibe el objeto de la petición a la API
data class UserDTO (
    @SerializedName("id") val id: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("email") val email: String?="",
    @SerializedName("isModerador") val isModerador: Boolean?=false,
    @SerializedName("isBanned") val isBanned: Boolean?=false,
    @SerializedName("fechaInicioBaneo") val fechaInicioBaneo: String?="",
    @SerializedName("duracionBaneoEnDias") val duracionBaneoEnDias: Int?=0,
    @SerializedName("fechaCreacionCuenta") val fechaCreacionCuenta: String?="",
    @SerializedName("fechaUltimoAcceso") val fechaUltimoAcceso: String?="",
    @SerializedName("isAdmin") val isAdmin: Boolean?=false
)