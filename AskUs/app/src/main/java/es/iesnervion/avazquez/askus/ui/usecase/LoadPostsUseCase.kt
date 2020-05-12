package es.iesnervion.avazquez.askus.ui.usecase

import com.google.gson.Gson
import es.iesnervion.avazquez.askus.DTOs.PaginHeader
import es.iesnervion.avazquez.askus.DTOs.PostCompletoListadoComentariosDTO
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
    fun getListadoPostsCompletosParaMostrarCantidadComentarios(repositoryInterface: RepositoryInterface,
        token: String,
        pageNumber: Int,
        pageSize: Int) {
        val call = requestInterface.getListadoPostsCompletosParaMostrarCantidadComentarios(
            authToken = token, pageNumber = pageNumber, pageSize = pageSize)
        repositoryInterface.onLoading(true)
        call.enqueue(object : Callback<List<PostCompletoParaMostrarDTO>> {
            override fun onFailure(call: Call<List<PostCompletoParaMostrarDTO>>, t: Throwable) {
                repositoryInterface.onLoading(false)
                repositoryInterface.showError(false)
            }

            override fun onResponse(call: Call<List<PostCompletoParaMostrarDTO>>,
                response: Response<List<PostCompletoParaMostrarDTO>>) {
                repositoryInterface.onLoading(false)
                val jsonString = response.headers().get("Paging-Headers")
                val header = Gson().fromJson(jsonString, PaginHeader::class.java)
                response.body()?.toList()?.let {
                        repositoryInterface.onSuccess(it, header ?: null)
                    }
            }

        })
    }

    /*Gets public and private posted posts that contains a given tag*/
    fun getListadoPostsCompletosParaMostrarCantidadComentariosTag(repositoryInterface: RepositoryInterface,
        token: String,
        idTag: Int,
        pageNumber: Int,
        pageSize: Int) {
        val call = requestInterface.getListadoPostsCompletosParaMostrarCantidadComentariosPorTag(
            authToken = token, idTag = idTag, pageSize = pageSize, pageNumber = pageNumber)
        repositoryInterface.onLoading(true)
        call.enqueue(object : Callback<List<PostCompletoParaMostrarDTO>> {
            override fun onFailure(call: Call<List<PostCompletoParaMostrarDTO>>, t: Throwable) {
                repositoryInterface.onLoading(false)
                repositoryInterface.showError(false)
            }

            override fun onResponse(call: Call<List<PostCompletoParaMostrarDTO>>,
                response: Response<List<PostCompletoParaMostrarDTO>>) {
                repositoryInterface.onLoading(false)
                val jsonString = response.headers().get("Paging-Headers")
                val header = Gson().fromJson(jsonString, PaginHeader::class.java)
                response.body()?.toList()?.let {
                        repositoryInterface.onSuccess(it, header ?: null)
                    }
            }

        })
    }

    /*Get one post with all its comments*/
    fun getPublicacionParaMostrarConComentarios(repositoryInterface: RepositoryInterface,
        token: String, idPost: Int, pageNumber: Int, pageSize: Int) {
        val call = requestInterface.getPostParaMostrarConListadoComentarios(authToken = token,
            idPost = idPost, pageNumber = pageNumber, pageSize = pageSize)
        repositoryInterface.onLoading(true)
        call.enqueue(object : Callback<PostCompletoListadoComentariosDTO> {
            override fun onFailure(call: Call<PostCompletoListadoComentariosDTO>, t: Throwable) {
                repositoryInterface.onLoading(false)
                repositoryInterface.showError(false)
            }

            override fun onResponse(call: Call<PostCompletoListadoComentariosDTO>,
                response: Response<PostCompletoListadoComentariosDTO>) {
                repositoryInterface.onLoading(false)
                val jsonString = response.headers().get("Paging-Headers")
                val header = Gson().fromJson(jsonString, PaginHeader::class.java)
                response.body()?.let {
                        repositoryInterface.onSuccess(listOf(it), header ?: null)
                    }
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
                val jsonString = response.headers().get("Paging-Headers")
                val header = Gson().fromJson(jsonString, PaginHeader::class.java)
                response.body()?.toList()?.let {
                        repositoryInterface.onSuccess(it, header ?: null)
                    }
            }

        })
    }
}