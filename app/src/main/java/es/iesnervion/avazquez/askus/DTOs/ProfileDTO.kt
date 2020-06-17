package es.iesnervion.avazquez.askus.DTOs

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProfileDTO(@SerializedName("idUsuario") val idUsuario: Int,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("fechaUltimoAcceso") val fechaUltimoAcceso: String,
    @SerializedName("fechaRegistro") val fechaRegistro: String,
    @SerializedName("listadoLogros") val listadoLogros: List<LogroDTO>,
    @SerializedName("cantidadComentariosEscritos") val cantidadComentariosEscritos: Int,
    @SerializedName("cantidadPostsEnviados") val cantidadPostsEnviados: Int,
    @SerializedName("cantidadPostsPublicados") val cantidadPostsPublicados: Int,
    @SerializedName("notaMediaEnPosts") val notaMediaEnPosts: Float) : Serializable