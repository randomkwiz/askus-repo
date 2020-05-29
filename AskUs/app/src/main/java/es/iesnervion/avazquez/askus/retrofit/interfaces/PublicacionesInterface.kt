package es.iesnervion.avazquez.askus.retrofit.interfaces

import es.iesnervion.avazquez.askus.DTOs.*
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
    fun getNonDeletedPostedPosts(
        @Header("Authorization") authToken: String): Call<List<PublicacionDTO>>

    //Obtiene una lista de posts publicados y no borrados (privados y no privados)
    //Los obtiene en formato PostVisibleParaMostrar con cantidad de comentarios
    @GET("/api/Publicaciones?pagination=true")
    fun getListadoPostsCompletosParaMostrarCantidadComentarios(
        @Header("Authorization") authToken: String,
        @Query("pageNumber") pageNumber: Int,
        @Query("idUsuarioLogeado") idUsuarioLogeado: Int,
        @Query("pageSize") pageSize: Int): Call<List<PostCompletoParaMostrarDTO>>

    //Obtiene una lista de posts publicados y no borrados (privados y no privados)
    //Los obtiene en formato PostVisibleParaMostrar con cantidad de comentarios
    @GET("/api/Publicaciones?pagination=true")
    fun getListadoPostsCompletosParaMostrarFromAuthor(@Header("Authorization") authToken: String,
        @Query("pageNumber") pageNumber: Int,
        @Query("idUsuarioLogeado") idUsuarioLogeado: Int,
        @Query("idAutorPosts") idAutorPosts: Int,
        @Query("pageSize") pageSize: Int): Call<List<PostCompletoParaMostrarDTO>>

    //Obtiene una lista de posts publicados y no borrados (privados y no privados)
    //Los obtiene en formato PostVisibleParaMostrar con cantidad de comentarios
    //Top rated
    @GET("/api/Publicaciones?pagination=true&top=TOP_RATED")
    fun getListadoPostsCompletosParaMostrarCantidadComentariosTopRated(
        @Header("Authorization") authToken: String,
        @Query("pageNumber") pageNumber: Int,
        @Query("idUsuarioLogeado") idUsuarioLogeado: Int,
        @Query("pageSize") pageSize: Int): Call<List<PostCompletoParaMostrarDTO>>

    //Obtiene una lista de posts publicados y no borrados (privados y no privados)
    //Los obtiene en formato PostVisibleParaMostrar con cantidad de comentarios
    //Top commented
    @GET("/api/Publicaciones?pagination=true&top=TOP_COMMENTED")
    fun getListadoPostsCompletosParaMostrarCantidadComentariosTopCommented(
        @Header("Authorization") authToken: String,
        @Query("pageNumber") pageNumber: Int,
        @Query("idUsuarioLogeado") idUsuarioLogeado: Int,
        @Query("pageSize") pageSize: Int): Call<List<PostCompletoParaMostrarDTO>>

    //Obtiene una lista de posts publicados y no borrados (privados y no privados) que contengan un tag
    //Los obtiene en formato PostVisibleParaMostrar con cantidad de comentarios
    @GET("api/Publicaciones?type=clsPostCompletoParaMostrarCantidadComentarios&top=ALL")
    fun getListadoPostsCompletosParaMostrarCantidadComentariosPorTag(
        @Header("Authorization") authToken: String,
        @Query("pageNumber") pageNumber: Int,
        @Query("pageSize") pageSize: Int,
        @Query("idUsuarioLogeado") idUsuarioLogeado: Int,
        @Query("idTag") idTag: Int): Call<List<PostCompletoParaMostrarDTO>>

    //Obtiene una lista de posts publicados y no borrados (privados y no privados) que contengan un tag
    //Los obtiene en formato PostVisibleParaMostrar con cantidad de comentarios
    //Top rated
    @GET("api/Publicaciones?type=clsPostCompletoParaMostrarCantidadComentarios&top=TOP_RATED")
    fun getListadoPostsCompletosParaMostrarCantidadComentariosPorTagTopRated(
        @Header("Authorization") authToken: String,
        @Query("pageNumber") pageNumber: Int,
        @Query("pageSize") pageSize: Int,
        @Query("idUsuarioLogeado") idUsuarioLogeado: Int,
        @Query("idTag") idTag: Int): Call<List<PostCompletoParaMostrarDTO>>

    //Obtiene una lista de posts publicados y no borrados (privados y no privados) que contengan un tag
    //Los obtiene en formato PostVisibleParaMostrar con cantidad de comentarios
    //Top commented
    @GET("api/Publicaciones?type=clsPostCompletoParaMostrarCantidadComentarios&top=TOP_COMMENTED")
    fun getListadoPostsCompletosParaMostrarCantidadComentariosPorTagTopCommented(
        @Header("Authorization") authToken: String,
        @Query("pageNumber") pageNumber: Int,
        @Query("pageSize") pageSize: Int,
        @Query("idUsuarioLogeado") idUsuarioLogeado: Int,
        @Query("idTag") idTag: Int): Call<List<PostCompletoParaMostrarDTO>>

    @GET("api/Publicaciones/{id}?type=clsPostCompletoParaMostrarListadoComentarios&pagination=true")
    fun getPostParaMostrarConListadoComentarios(@Header("Authorization") authToken: String,
        @Path("id") idPost: Int,
        @Query("pageNumber") pageNumber: Int,
        @Query("idUsuarioLogeado") idUsuarioLogeado: Int,
        @Query("pageSize") pageSize: Int): Call<PostCompletoListadoComentariosDTO>

    @POST("api/Publicaciones")
    fun postNewPost(@Body post: CreatePostRequestBody): Call<Void>

    /*Obtener post de moderacion*/
    @GET("api/publicaciones?&pagination=true&type=clsPostModeracion&moderacion=true")
    fun getPostModeracion(@Header("Authorization") authToken: String,
        @Query("pageNumber") pageNumber: Int,
        @Query("idUsuarioLogeado") idUsuarioLogeado: Int,
        @Query("pageSize") pageSize: Int): Call<List<PostModeracionDTO>>
}