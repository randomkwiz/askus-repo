package es.iesnervion.avazquez.askus.ui.fragments.moderation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import es.iesnervion.avazquez.askus.DTOs.PaginHeader
import es.iesnervion.avazquez.askus.DTOs.PostModeracionDTO
import es.iesnervion.avazquez.askus.DTOs.VotoModeracionDTO
import es.iesnervion.avazquez.askus.ui.repositories.ModerationVotesRepository
import es.iesnervion.avazquez.askus.ui.repositories.PostsRepository
import es.iesnervion.avazquez.askus.utils.GlobalApplication
import javax.inject.Inject

class ModerationViewModel : ViewModel() {
    @Inject
    lateinit var postsRepository: PostsRepository

    @Inject
    lateinit var votoModeracionRepository: ModerationVotesRepository
    var areValuesReady = MediatorLiveData<Boolean>()
    lateinit var currentPaginHeader: PaginHeader
    lateinit var postsList: List<PostModeracionDTO>
    var currentPostPosition: Int = 0

    init {
        GlobalApplication.applicationComponent?.inject(this)
        areValuesReady = MediatorLiveData<Boolean>().apply {
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
            addSource(moderationPosts()) { x ->
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

    fun areValuesReady(): LiveData<Boolean> {
        return areValuesReady
    }

    fun loadingLiveData(): LiveData<Boolean> {
        return postsRepository.getLoadingLiveData()
    }

    fun loadModerationPosts(token: String, pageNumber: Int, pageSize: Int, idUsuarioLogeado: Int) {
        postsRepository.useCaseLoadModerationPost(token, pageSize = pageSize,
            pageNumber = pageNumber, idUsuarioLogeado = idUsuarioLogeado)
    }

    fun getError(): LiveData<Boolean> {
        return postsRepository.finishMessage
    }

    fun getPaginHeaders(): LiveData<PaginHeader> {
        return postsRepository.getModerationPaginHeaders()
    }

    fun moderationPosts(): LiveData<List<PostModeracionDTO>> {
        return postsRepository.getModerationPosts()
    }

    fun insertVotoModeracion(token: String, votoModeracion: VotoModeracionDTO) {
        votoModeracionRepository.useCaseInsertVotoModeracion(votoModeracion = votoModeracion,
            token = token)
    }

    fun responseCodeVotoModeracionSent(): LiveData<Int> {
        return votoModeracionRepository.getResponseCodeVotoModeracionSent()
    }
}