package es.iesnervion.avazquez.askus.retrofit.interfaces

import es.iesnervion.avazquez.askus.DTOs.TagDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TagsInterface {
    @GET("/api/tags")
    fun getAllTags() : Call<List<TagDTO>>

    //Obtiene los tags de una publicación
    @GET("/api/tags")
    fun getTagsFromPost(@Query("idPublicacion") idPublicacion : Int) : Call<List<TagDTO>>
}