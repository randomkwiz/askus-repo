package es.iesnervion.avazquez.askus.ui.usecase

import es.iesnervion.avazquez.askus.DTOs.ComentarioDTO
import es.iesnervion.avazquez.askus.retrofit.interfaces.ComentariosInterface
import es.iesnervion.avazquez.askus.ui.repositories.RepositoryInterface
import es.iesnervion.avazquez.askus.utils.GlobalApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class CreateCommentUseCase {
    @Inject
    lateinit var requestInterface: ComentariosInterface

    init {
        GlobalApplication.applicationComponent?.inject(this)
    }

    fun insertComentario(repositoryInterface: RepositoryInterface, comentario: ComentarioDTO) {
        val call = requestInterface.insertComentario(comentario)
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