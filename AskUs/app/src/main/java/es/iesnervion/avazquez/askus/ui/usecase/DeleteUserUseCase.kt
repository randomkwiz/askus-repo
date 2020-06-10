package es.iesnervion.avazquez.askus.ui.usecase

import es.iesnervion.avazquez.askus.retrofit.interfaces.UsersInterface
import es.iesnervion.avazquez.askus.ui.repositories.RepositoryInterface
import es.iesnervion.avazquez.askus.utils.GlobalApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class DeleteUserUseCase {
    @Inject
    lateinit var requestInterface: UsersInterface

    init {
        GlobalApplication.applicationComponent?.inject(this)
    }

    fun deleteUser(repositoryInterface: RepositoryInterface, idUser: Int, token: String) {
        val call = requestInterface.deleteUser(idUser = idUser, authToken = token)
        repositoryInterface.onLoading(true)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                repositoryInterface.onLoading(false)
                repositoryInterface.onSuccess(listOf(response.code()), response.code())
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                repositoryInterface.onLoading(false)
                repositoryInterface.showError(true)
            }
        })
    }
}