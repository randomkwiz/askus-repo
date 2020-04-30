package es.iesnervion.avazquez.askus.retrofit.interfaces

import es.iesnervion.avazquez.askus.models.Login
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthInterface {

    @POST("/api/login/authenticate")
    fun getTokenLogin(@Body login : Login)
            : Call<String>
}