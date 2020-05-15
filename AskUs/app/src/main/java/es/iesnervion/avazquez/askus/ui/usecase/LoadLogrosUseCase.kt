package es.iesnervion.avazquez.askus.ui.usecase

import es.iesnervion.avazquez.askus.DTOs.LogroDTO
import es.iesnervion.avazquez.askus.retrofit.interfaces.LogrosInterface
import es.iesnervion.avazquez.askus.ui.repositories.RepositoryInterface
import es.iesnervion.avazquez.askus.utils.GlobalApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class LoadLogrosUseCase {
    @Inject
    lateinit var requestInterface: LogrosInterface

    init {
        GlobalApplication.applicationComponent?.inject(this)
    }

    fun getAllLogros(repositoryInterface: RepositoryInterface) {
        val call = requestInterface.getAllLogros()
        repositoryInterface.onLoading(true)
        call.enqueue(object : Callback<List<LogroDTO>> {
            override fun onFailure(call: Call<List<LogroDTO>>, t: Throwable) {
                repositoryInterface.onLoading(false)
                repositoryInterface.showError(false)
            }

            override fun onResponse(call: Call<List<LogroDTO>>,
                response: Response<List<LogroDTO>>) {
                repositoryInterface.onLoading(false)
                response.body()?.toList()
                    ?.let { repositoryInterface.onSuccess(it, response.code()) }
            }
        })
    }

    fun getLogrosDeUsuario(repositoryInterface: RepositoryInterface,
        idUsuario: Int,
        token: String) {
        val call = requestInterface.getLogrosFromUser(idUsuario = idUsuario, authToken = token)
        repositoryInterface.onLoading(true)
        call.enqueue(object : Callback<List<LogroDTO>> {
            override fun onFailure(call: Call<List<LogroDTO>>, t: Throwable) {
                repositoryInterface.onLoading(false)
                repositoryInterface.showError(false)
            }

            override fun onResponse(call: Call<List<LogroDTO>>,
                response: Response<List<LogroDTO>>) {
                repositoryInterface.onLoading(false)
                response.body()?.toList()
                    ?.let { repositoryInterface.onSuccess(it, response.code()) }
            }
        })
    }
}