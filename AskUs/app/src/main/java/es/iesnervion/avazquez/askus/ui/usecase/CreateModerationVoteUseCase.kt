package es.iesnervion.avazquez.askus.ui.usecase

import es.iesnervion.avazquez.askus.DTOs.VotoModeracionDTO
import es.iesnervion.avazquez.askus.retrofit.interfaces.VotoModeracionInterface
import es.iesnervion.avazquez.askus.ui.repositories.RepositoryInterface
import es.iesnervion.avazquez.askus.utils.GlobalApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class CreateModerationVoteUseCase {
    @Inject
    lateinit var requestInterface: VotoModeracionInterface

    init {
        GlobalApplication.applicationComponent?.inject(this)
    }

    fun insertVotoModeracion(repositoryInterface: RepositoryInterface,
        voto: VotoModeracionDTO,
        token: String) {
        val call = requestInterface.insertVotoModeracion(newVoto = voto, authToken = token)
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