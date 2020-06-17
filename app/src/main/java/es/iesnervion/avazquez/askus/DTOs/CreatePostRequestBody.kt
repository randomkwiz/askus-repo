package es.iesnervion.avazquez.askus.DTOs

import com.google.gson.annotations.SerializedName

class CreatePostRequestBody(post: PublicacionDTO,
    @SerializedName("listadoIdTags") val tagList: List<Int>) :
    PublicacionDTO(post.id, post.fechaCreacion, post.fechaPublicacion, post.idAutor, post.isBorrado,
        post.isPrivada, post.isPublicado, post.texto, post.titulo)
