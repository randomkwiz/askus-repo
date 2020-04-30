package es.iesnervion.avazquez.askus.retrofit.interfaces

import es.iesnervion.avazquez.askus.DTOs.UserDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface UsersInterface {

    @GET("/api/Users/")
    fun getUserList(@Header("Authorization") authToken:String): Call<List<UserDTO>>
}