package es.iesnervion.avazquez.askus.ui.usecase

import es.iesnervion.avazquez.askus.DTOs.PostCompletoParaMostrarDTO
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

    /*Gets public and private posted posts*/
    fun getListadoPostsCompletosParaMostrarCantidadComentarios(repositoryInterface: RepositoryInterface, token : String) {
        val call = requestInterface.getListadoPostsCompletosParaMostrarCantidadComentarios(token)
        repositoryInterface.onLoading(true)
        call.enqueue(object : Callback<List<PostCompletoParaMostrarDTO>> {
            override fun onFailure(call: Call<List<PostCompletoParaMostrarDTO>>, t: Throwable) {
                repositoryInterface.onLoading(false)
                repositoryInterface.showError(false)
            }

            override fun onResponse(call: Call<List<PostCompletoParaMostrarDTO>>,
                response: Response<List<PostCompletoParaMostrarDTO>>) {
                repositoryInterface.onLoading(false)
                response.body()?.toList()
                    ?.let { repositoryInterface.onSuccess(it) }
            }

        })
    }

    /*Gets public and private posted posts that contains a given tag*/
    fun getListadoPostsCompletosParaMostrarCantidadComentariosTag(repositoryInterface: RepositoryInterface, token : String, idTag : Int) {
        val call = requestInterface.getListadoPostsCompletosParaMostrarCantidadComentariosPorTag(token, idTag)
        repositoryInterface.onLoading(true)
        call.enqueue(object : Callback<List<PostCompletoParaMostrarDTO>> {
            override fun onFailure(call: Call<List<PostCompletoParaMostrarDTO>>, t: Throwable) {
                repositoryInterface.onLoading(false)
                repositoryInterface.showError(false)
            }

            override fun onResponse(call: Call<List<PostCompletoParaMostrarDTO>>,
                response: Response<List<PostCompletoParaMostrarDTO>>) {
                repositoryInterface.onLoading(false)
                response.body()?.toList()
                    ?.let { repositoryInterface.onSuccess(it) }
            }

        })
    }


    /*Gets public posted posts*/
    fun getNonDeletedPublicPostedPosts(repositoryInterface: RepositoryInterface) {
        //TODO - cambiar esto para que devuelva en formato PostCompletoParaMostrar
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
                response.body()?.toList()
                    ?.let { repositoryInterface.onSuccess(it) }
            }

        })
    }
}