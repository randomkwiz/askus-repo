package es.iesnervion.avazquez.askus.DTOs

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PostCompletoListadoComentariosDTO(
    @SerializedName("IdPost") val IdPost: Int,
    @SerializedName("listadoComentarios") val listadoComentarios: List<ComentarioParaMostrarDTO>,
    @SerializedName("tituloPost") val tituloPost: String,
    @SerializedName("cuerpoPost") val cuerpoPost: String,
    @SerializedName("nickAutor") val nickAutor: String,
    @SerializedName("idAutor") val idAutor: Int,
    @SerializedName("fechaCreacion") val fechaCreacion: String? = "",
    @SerializedName("fechaPublicacion") val fechaPublicacion: String? = "",
    @SerializedName("cantidadVotosPositivos") val cantidadVotosPositivos: Int,
    @SerializedName("cantidadVotosNegativos") val cantidadVotosNegativos: Int,
    @SerializedName("isPostPrivado") val isPostPrivado: Boolean,
    @SerializedName("listadoTags") val listadoTags: List<TagDTO>,
    @SerializedName("votoDeUsuarioLogeado") val votoDeUsuarioLogeado: VotoPublicacionDTO?
) : Serializable