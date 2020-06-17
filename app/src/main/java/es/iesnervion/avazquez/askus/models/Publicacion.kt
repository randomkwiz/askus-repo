package es.iesnervion.avazquez.askus.models

import com.google.gson.annotations.SerializedName

open class Publicacion(@SerializedName("id") var id: Int,
    @SerializedName("fechaCreacion") var fechaCreacion: String? = "",
    @SerializedName("fechaPublicacion ") var fechaPublicacion: String? = "",
    @SerializedName("idAutor") var idAutor: Int,
    @SerializedName("isBorrado") var isBorrado: Boolean? = false,
    @SerializedName("isPrivada") var isPrivada: Boolean? = false,
    @SerializedName("isPublicado") var isPublicado: Boolean? = false,
    @SerializedName("texto") var texto: String,
    @SerializedName("titulo") var titulo: String? = "")