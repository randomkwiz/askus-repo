package es.iesnervion.avazquez.askus.ui.auth.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.iesnervion.avazquez.askus.DTOs.UserDTO
import es.iesnervion.avazquez.askus.retrofit.interfaces.AuthInterface
import es.iesnervion.avazquez.askus.retrofit.interfaces.UsersInterface
import es.iesnervion.avazquez.askus.mappers.UserMapper
import es.iesnervion.avazquez.askus.models.Login
import es.iesnervion.avazquez.askus.models.User
import es.iesnervion.avazquez.askus.utils.GlobalApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class AuthViewModel : ViewModel() {

        var token : MutableLiveData<String> = MutableLiveData()
        var isCorrectLogin : MutableLiveData<Boolean> = MutableLiveData()
        var usuarioActual : User = User()
        var userList : MutableLiveData<List<User>> = MutableLiveData()
        val userMapper : UserMapper = UserMapper()
        lateinit  var login: Login
        @Inject
        lateinit var authInterface : AuthInterface
        @Inject
        lateinit var usersInterface : UsersInterface
        init {
                GlobalApplication.applicationComponent?.inject(this)
        }

        //Comprueba si el login es correcto y si lo es, devuelve un token válido
        fun postAuth() {
                val call: Call<String> = authInterface
                        .getTokenLogin(login)
                call.enqueue(object : Callback<String> {
                        override fun onResponse(
                                call: Call<String>,
                                response: Response<String>
                        ) {
                                if (response.isSuccessful) {    //Si entra aquí es que el login es correcto, porque si no da un 401
                                        token.setValue( response.body()!!.replace("\"", "") )
                                        isCorrectLogin.value = true

                                }else{
                                        token.value = ""
                                        isCorrectLogin.value = false
                                }
                        }

                        override fun onFailure(
                                call: Call<String>,
                                t: Throwable
                        ) {
                                t.stackTrace
                        }
                })
        }


        //Cargar usuarios
        fun loadUsers(){
                if(!token.value.isNullOrEmpty()){
                        val call: Call<List<UserDTO>> = usersInterface
                                .getUserList(token.value.toString())
                        call.enqueue(object : Callback<List<UserDTO>> {
                                override fun onResponse(
                                        call: Call<List<UserDTO>>,
                                        response: Response<List<UserDTO>>
                                ) {
                                        if (response.isSuccessful) {    //Si entra aquí es que el login es correcto, porque si no da un 401
                                               response.body()?.let {
                                                       userList.value =
                                                       userMapper.dtoListToModelList(it as MutableList<UserDTO>) }
                                        }

                                }
                                override fun onFailure(
                                        call: Call<List<UserDTO>>,
                                        t: Throwable
                                ) {
                                        t.stackTrace
                                }
                        })



                }
        }

}