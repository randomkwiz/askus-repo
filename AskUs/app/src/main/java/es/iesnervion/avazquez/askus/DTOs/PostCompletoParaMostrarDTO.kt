package es.iesnervion.avazquez.askus.DTOs

import com.google.gson.annotations.SerializedName

data class PostCompletoParaMostrarDTO (
    @SerializedName("id") val id: Int,
    @SerializedName("cantidadComentarios") val cantidadComentarios: Int,
    @SerializedName("IdPost ") val IdPost : Int,
    @SerializedName("tituloPost") val tituloPost: String,
    @SerializedName("cuerpoPost") val cuerpoPost: String,
    @SerializedName("nickAutor") val nickAutor: String,
    @SerializedName("idAutor") val idAutor: Int,
    @SerializedName("fechaCreacion") val fechaCreacion: String?="",
    @SerializedName("fechaPublicacion") val fechaPublicacion: String?="",
    @SerializedName("cantidadVotosPositivos") val cantidadVotosPositivos: Int,
    @SerializedName("cantidadVotosNegativos") val cantidadVotosNegativos: Int,
    @SerializedName("isPostPrivado") val isPostPrivado: Boolean,
    @SerializedName("listadoTags") val listadoTags: List<TagDTO>
)