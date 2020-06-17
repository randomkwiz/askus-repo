package es.iesnervion.avazquez.askus.models

import com.google.gson.annotations.SerializedName

class Login(@SerializedName("Username") var nickname: String,
    @SerializedName("Password") var password: String)