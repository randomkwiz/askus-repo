package es.iesnervion.avazquez.askus.retrofit.interfaces

import es.iesnervion.avazquez.askus.DTOs.CreatePostRequestBody
import es.iesnervion.avazquez.askus.DTOs.PostCompletoListadoComentariosDTO
import es.iesnervion.avazquez.askus.DTOs.PostCompletoParaMostrarDTO
import es.iesnervion.avazquez.askus.DTOs.PublicacionDTO
import retrofit2.Call
import retrofit2.http.*

interface PublicacionesInterface {
    //Obtiene todos los posts, sin filtros
    @GET("/api/publicaciones/")
    fun getAllPosts(@Header("Authorization") authToken: String): Call<List<PublicacionDTO>>

    //Obtiene una lista de posts que no sean privados, ni estén borrados y sí estén publicados
    //No auth
    @GET("/api/publicaciones?isPublicado=true&isBorrado=false&isPrivada=false")
    fun getNonDeletedPublicPostedPosts(): Call<List<PublicacionDTO>>

    //Obtiene una lista de posts publicados y no borrados (privados y no privados)
    @GET("/api/publicaciones?isPublicado=true&isBorrado=false")
    fun getNonDeletedPostedPosts(@Header("Authorization") authToken: String): Call<List<PublicacionDTO>>

    //Obtiene una lista de posts publicados y no borrados (privados y no privados)
    //Los obtiene en formato PostVisibleParaMostrar con cantidad de comentarios
    @GET("/api/Publicaciones?type=clsPostCompletoParaMostrarCantidadComentarios")
    fun getListadoPostsCompletosParaMostrarCantidadComentarios(@Header("Authorization") authToken: String):
            Call<List<PostCompletoParaMostrarDTO>>

    //Obtiene una lista de posts publicados y no borrados (privados y no privados) que contengan un tag
    //Los obtiene en formato PostVisibleParaMostrar con cantidad de comentarios
    @GET("api/Publicaciones?type=clsPostCompletoParaMostrarCantidadComentarios")
    fun getListadoPostsCompletosParaMostrarCantidadComentariosPorTag(
        @Header("Authorization") authToken: String
        , @Query("idTag") idTag: Int
    ): Call<List<PostCompletoParaMostrarDTO>>

    @GET("api/Publicaciones/{id}?type=clsPostCompletoParaMostrarListadoComentarios")
    fun getPostParaMostrarConListadoComentarios(
        @Header("Authorization") authToken: String
        , @Path("id") idPost: Int
    ): Call<PostCompletoListadoComentariosDTO>


    @POST("api/Publicaciones")
    fun postNewPost(@Body post: CreatePostRequestBody): Call<Void>
}