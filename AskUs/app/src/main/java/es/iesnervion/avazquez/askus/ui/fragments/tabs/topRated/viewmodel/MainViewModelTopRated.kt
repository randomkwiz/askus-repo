package es.iesnervion.avazquez.askus.ui.fragments.tabs.topRated.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import es.iesnervion.avazquez.askus.DTOs.PaginHeader
import es.iesnervion.avazquez.askus.DTOs.PostCompletoParaMostrarDTO
import es.iesnervion.avazquez.askus.DTOs.VotoPublicacionDTO
import es.iesnervion.avazquez.askus.ui.repositories.PostsRepository
import es.iesnervion.avazquez.askus.ui.repositories.VotesRepository
import es.iesnervion.avazquez.askus.utils.GlobalApplication
import javax.inject.Inject

class MainViewModelTopRated : ViewModel() {
    @Inject
    lateinit var postsRepository: PostsRepository

    @Inject
    lateinit var votesRepository: VotesRepository
    var areValuesReadyTopRated = MediatorLiveData<Boolean>()
    lateinit var currentPaginHeader: PaginHeader
    lateinit var postsList: List<PostCompletoParaMostrarDTO>

    init {
        GlobalApplication.applicationComponent?.inject(this)
        //TopRated
        areValuesReadyTopRated = MediatorLiveData<Boolean>().apply {
            var totalPagesFlag = false
            var postsListFlag = false
            value = false
            addSource(getPaginHeaders()) { x ->
                x?.let {
                    totalPagesFlag = x.totalPages >= 0
                    currentPaginHeader = x
                    if (totalPagesFlag && postsListFlag) {
                        value = true
                    }
                }
            }
            addSource(allVisiblePostsByTagTopRated()) { x ->
                x?.let {
                    postsListFlag = x.size >= 0
                    postsList = x
                    if (totalPagesFlag && postsListFlag) {
                        value = true
                    }
                }
            }
        }
    }

    fun areValuesReadyTopRated(): LiveData<Boolean> {
        return areValuesReadyTopRated
    }

    fun allVisiblePostsByTagTopRated(): LiveData<List<PostCompletoParaMostrarDTO>> {
        return postsRepository.getAllVisiblePostsByGivenTagTopRated()
    }

    fun loadingLiveData(): LiveData<Boolean> {
        return postsRepository.getLoadingLiveData()
    }

    fun loadPostsByTagTopRated(token: String,
        idTag: Int,
        pageNumber: Int,
        pageSize: Int,
        idUsuarioLogeado: Int) {
        if (idTag == 0) {
            postsRepository.useCaseLoadNonDeletedPostedPostsTopRated(token, pageSize = pageSize,
                pageNumber = pageNumber, idUsuarioLogeado = idUsuarioLogeado)
        } else {
            postsRepository.useCaseLoadNonDeletedPostedPostsByTagTopRated(token, idTag,
                pageNumber = pageNumber, pageSize = pageSize, idUsuarioLogeado = idUsuarioLogeado)
        }
    }

    fun getPaginHeaders(): LiveData<PaginHeader> {
        return postsRepository.getPaginHeaders()
    }

    fun insertVotoPublicacion(token: String, votoPublicacionDTO: VotoPublicacionDTO) {
        votesRepository.useCaseInsertVotoPublicacion(votoPublicacion = votoPublicacionDTO,
            token = token)
    }

    fun responseCodeVotoPublicacionSent(): LiveData<Int> {
        return votesRepository.getResponseCodeVotoPublicacionSent()
    }
}