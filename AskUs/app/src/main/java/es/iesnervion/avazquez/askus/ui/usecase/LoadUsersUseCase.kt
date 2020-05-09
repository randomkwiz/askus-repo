package es.iesnervion.avazquez.askus.ui.usecase

import es.iesnervion.avazquez.askus.retrofit.interfaces.UsersInterface
import es.iesnervion.avazquez.askus.ui.repositories.RepositoryInterface
import es.iesnervion.avazquez.askus.utils.GlobalApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class LoadUsersUseCase {
    @Inject
    lateinit var requestInterface: UsersInterface

    init {
        GlobalApplication.applicationComponent?.inject(this)
    }

    fun getIDUserByNickname(repositoryInterface: RepositoryInterface,
        token: String,
        nickname: String) {
        val call = requestInterface.getIDUserByNickname(authToken = token, nickname = nickname)
        repositoryInterface.onLoading(true)
        call.enqueue(object : Callback<Int> {
            override fun onFailure(call: Call<Int>, t: Throwable) {
                repositoryInterface.onLoading(false)
                repositoryInterface.showError(false)
            }

            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                repositoryInterface.onLoading(false)
                repositoryInterface.onSuccess(listOf(response.body()))
            }
        })
    }
}