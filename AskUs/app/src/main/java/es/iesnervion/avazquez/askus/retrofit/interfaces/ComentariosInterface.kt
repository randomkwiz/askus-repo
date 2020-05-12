package es.iesnervion.avazquez.askus.retrofit.interfaces

import es.iesnervion.avazquez.askus.DTOs.ComentarioDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ComentariosInterface {
    @POST("/api/comentarios")
    fun insertComentario(@Body newComment: ComentarioDTO
    ): Call<Void>
}