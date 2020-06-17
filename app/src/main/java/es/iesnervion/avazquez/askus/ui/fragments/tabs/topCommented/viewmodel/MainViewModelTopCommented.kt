package es.iesnervion.avazquez.askus.ui.fragments.tabs.topCommented.viewmodel

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

class MainViewModelTopCommented : ViewModel() {
    @Inject
    lateinit var postsRepository: PostsRepository

    @Inject
    lateinit var votesRepository: VotesRepository
    var areValuesReadyTopCommented = MediatorLiveData<Boolean>()
    lateinit var currentPaginHeader: PaginHeader
    lateinit var postsList: List<PostCompletoParaMostrarDTO>

    init {
        GlobalApplication.applicationComponent?.inject(this)
        //TopCommented
        areValuesReadyTopCommented = MediatorLiveData<Boolean>().apply {
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
            addSource(allVisiblePostsByTagTopCommented()) { x ->
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

    fun areValuesReadyTopCommented(): LiveData<Boolean> {
        return areValuesReadyTopCommented
    }

    fun allVisiblePostsByTagTopCommented(): LiveData<List<PostCompletoParaMostrarDTO>> {
        return postsRepository.getAllVisiblePostsByGivenTagTopCommented()
    }

    fun loadingLiveData(): LiveData<Boolean> {
        return postsRepository.getLoadingLiveData()
    }

    fun loadPostsByTagTopCommented(token: String,
        idTag: Int,
        pageNumber: Int,
        pageSize: Int,
        idUsuarioLogeado: Int) {
        if (idTag == 0) {
            postsRepository.useCaseLoadNonDeletedPostedPostsTopCommented(token, pageSize = pageSize,
                pageNumber = pageNumber, idUsuarioLogeado = idUsuarioLogeado)
        } else {
            postsRepository.useCaseLoadNonDeletedPostedPostsByTagTopCommented(token, idTag,
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