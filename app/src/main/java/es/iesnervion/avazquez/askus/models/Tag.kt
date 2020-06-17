package es.iesnervion.avazquez.askus.models

import com.google.gson.annotations.SerializedName

data class Tag(@SerializedName("id") var id: Int,
    @SerializedName("descripcion") var descripcion: String,
    @SerializedName("nombre") var nombre: String)