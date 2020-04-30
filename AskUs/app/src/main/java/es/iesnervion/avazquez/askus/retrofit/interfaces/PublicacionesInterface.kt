package es.iesnervion.avazquez.askus.retrofit.interfaces

import es.iesnervion.avazquez.askus.DTOs.PublicacionDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface PublicacionesInterface {
    //Obtiene todos los posts, sin filtros
    @GET("/api/publicaciones/")
    fun getAllPosts(@Header("Authorization") authToken:String): Call<List<PublicacionDTO>>

    //Obtiene una lista de posts que no sean privados, ni estén borrados y sí estén publicados
    //No auth
    @GET("/api/publicaciones?isPublicado=true&isBorrado=false&isPrivada=false")
    fun getNonDeletedPublicPostedPosts(): Call<List<PublicacionDTO>>

    //Obtiene una lista de posts publicados y no borrados (privados y no privados)
    @GET("/api/publicaciones?isPublicado=true&isBorrado=false")
    fun getNonDeletedPostedPosts(@Header("Authorization") authToken:String): Call<List<PublicacionDTO>>
}