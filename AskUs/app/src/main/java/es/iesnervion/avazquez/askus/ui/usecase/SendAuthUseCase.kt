package es.iesnervion.avazquez.askus.ui.usecase

import es.iesnervion.avazquez.askus.models.Login
import es.iesnervion.avazquez.askus.retrofit.interfaces.AuthInterface
import es.iesnervion.avazquez.askus.ui.repositories.RepositoryInterface
import es.iesnervion.avazquez.askus.utils.GlobalApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class SendAuthUseCase {
    @Inject
    lateinit var requestInterface: AuthInterface

    init {
        GlobalApplication.applicationComponent?.inject(this)
    }

    fun getTokenLogin(repositoryInterface: RepositoryInterface, login: Login) {
        val call = requestInterface.getTokenLogin(login)
        repositoryInterface.onLoading(true)
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                repositoryInterface.onLoading(false)
                if(response.isSuccessful){
                    response.body()?.replace("\"", "")?.toList()
                        ?.let {
                            repositoryInterface.onSuccess(it, null)
                        }
                }else{
                    repositoryInterface.onSuccess(listOf(response.code()), null)
                }

            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                repositoryInterface.onLoading(false)
                repositoryInterface.showError(true)
            }
        })
    }
}