package es.iesnervion.avazquez.askus.ui.usecase

import es.iesnervion.avazquez.askus.DTOs.PublicacionDTO
import es.iesnervion.avazquez.askus.retrofit.interfaces.PublicacionesInterface
import es.iesnervion.avazquez.askus.ui.repositories.RepositoryInterface
import es.iesnervion.avazquez.askus.utils.GlobalApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class LoadPostsUseCase {
    @Inject
    lateinit var requestInterface: PublicacionesInterface

    init {
        GlobalApplication.applicationComponent?.inject(this)
    }

    fun getNonDeletedPublicPostedPosts(repositoryInterface: RepositoryInterface) {
        val call = requestInterface.getNonDeletedPublicPostedPosts()
        repositoryInterface.onLoading(true)
        call.enqueue(object : Callback<List<PublicacionDTO>> {
            override fun onFailure(call: Call<List<PublicacionDTO>>, t: Throwable) {
                repositoryInterface.onLoading(false)
                repositoryInterface.showError(false)
            }

            override fun onResponse(
                call: Call<List<PublicacionDTO>>,
                response: Response<List<PublicacionDTO>>
            ) {
                repositoryInterface.onLoading(false)
                response.body()?.toMutableList()
                    ?.let { repositoryInterface.onSuccess(it) }
            }

        })
    }
}