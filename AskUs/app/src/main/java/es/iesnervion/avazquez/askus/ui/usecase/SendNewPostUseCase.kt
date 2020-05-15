package es.iesnervion.avazquez.askus.ui.usecase

import es.iesnervion.avazquez.askus.DTOs.CreatePostRequestBody
import es.iesnervion.avazquez.askus.DTOs.PublicacionDTO
import es.iesnervion.avazquez.askus.retrofit.interfaces.PublicacionesInterface
import es.iesnervion.avazquez.askus.ui.repositories.RepositoryInterface
import es.iesnervion.avazquez.askus.utils.GlobalApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class SendNewPostUseCase {
    @Inject
    lateinit var requestInterface: PublicacionesInterface

    init {
        GlobalApplication.applicationComponent?.inject(this)
    }

    fun postNewPost(repositoryInterface: RepositoryInterface,
        publicacion: PublicacionDTO,
        tagList: List<Int>) {
        val call = requestInterface.postNewPost(CreatePostRequestBody(post = publicacion,
            tagList = tagList))
        call.request().body()
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