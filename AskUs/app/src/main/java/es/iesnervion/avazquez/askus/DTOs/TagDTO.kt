package es.iesnervion.avazquez.askus.DTOs

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TagDTO(
    @SerializedName("id") val id: Int,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("nombre") val nombre: String
) : Serializable
{
    override fun toString(): String {
        return nombre
    }
}