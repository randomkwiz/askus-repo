package es.iesnervion.avazquez.askus.ui.usecase

import es.iesnervion.avazquez.askus.DTOs.VotoPublicacionDTO
import es.iesnervion.avazquez.askus.retrofit.interfaces.VotoPublicacionInterface
import es.iesnervion.avazquez.askus.ui.repositories.RepositoryInterface
import es.iesnervion.avazquez.askus.utils.GlobalApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class CreateVotesUseCase {
    @Inject
    lateinit var requestInterface: VotoPublicacionInterface

    init {
        GlobalApplication.applicationComponent?.inject(this)
    }

    fun insertVotoPublicacion(repositoryInterface: RepositoryInterface,
        voto: VotoPublicacionDTO,
        token: String) {
        val call = requestInterface.insertVotoPublicacion(newVoto = voto, authToken = token)
        repositoryInterface.onLoading(true)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                repositoryInterface.onLoading(false)
                repositoryInterface.onSuccess(listOf(response.code()), null)
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                repositoryInterface.onLoading(false)
                repositoryInterface.showError(true)
            }
        })
    }
}