package es.iesnervion.avazquez.askus.ui.usecase

import es.iesnervion.avazquez.askus.DTOs.PostCompletoParaMostrarDTO
import es.iesnervion.avazquez.askus.DTOs.TagDTO
import es.iesnervion.avazquez.askus.retrofit.interfaces.PublicacionesInterface
import es.iesnervion.avazquez.askus.retrofit.interfaces.TagsInterface
import es.iesnervion.avazquez.askus.ui.repositories.RepositoryInterface
import es.iesnervion.avazquez.askus.utils.GlobalApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class LoadTagsUseCase {
    @Inject
    lateinit var requestInterface: TagsInterface

    init {
        GlobalApplication.applicationComponent?.inject(this)
    }

    fun getAllTags(repositoryInterface: RepositoryInterface) {
        val call = requestInterface.getAllTags()
        repositoryInterface.onLoading(true)
        call.enqueue(object : Callback<List<TagDTO>> {
            override fun onFailure(call: Call<List<TagDTO>>, t: Throwable) {
                repositoryInterface.onLoading(false)
                repositoryInterface.showError(false)
            }

            override fun onResponse(call: Call<List<TagDTO>>, response: Response<List<TagDTO>>) {
                repositoryInterface.onLoading(false)
                response.body()?.toList()
                    ?.let { repositoryInterface.onSuccess(it) }
            }

        })
    }
}