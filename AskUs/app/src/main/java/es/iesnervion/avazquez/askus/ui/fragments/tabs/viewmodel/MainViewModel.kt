package es.iesnervion.avazquez.askus.ui.fragments.tabs.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import es.iesnervion.avazquez.askus.DTOs.PaginHeader
import es.iesnervion.avazquez.askus.DTOs.PostCompletoParaMostrarDTO
import es.iesnervion.avazquez.askus.DTOs.TagDTO
import es.iesnervion.avazquez.askus.DTOs.VotoPublicacionDTO
import es.iesnervion.avazquez.askus.mappers.PublicacionMapper
import es.iesnervion.avazquez.askus.models.Publicacion
import es.iesnervion.avazquez.askus.ui.repositories.PostsRepository
import es.iesnervion.avazquez.askus.ui.repositories.TagsRepository
import es.iesnervion.avazquez.askus.ui.repositories.VotesRepository
import es.iesnervion.avazquez.askus.utils.GlobalApplication
import javax.inject.Inject

class MainViewModel : ViewModel() {
    @Inject
    lateinit var postsRepository: PostsRepository
    @Inject
    lateinit var tagsRepository: TagsRepository

    @Inject
    lateinit var votesRepository: VotesRepository

    var newPost: Publicacion = Publicacion(id = 0, idAutor = 0, texto = "")
    lateinit var tagList: List<Int>
    var saveStateMenu = 0

    fun allVisiblePostsByTag(): LiveData<List<PostCompletoParaMostrarDTO>> {
        return postsRepository.getAllVisiblePostsByGivenTag()
    }

    fun allVisiblePostsByTagTopRated(): LiveData<List<PostCompletoParaMostrarDTO>> {
        return postsRepository.getAllVisiblePostsByGivenTagTopRated()
    }

    fun allVisiblePostsByTagTopCommented(): LiveData<List<PostCompletoParaMostrarDTO>> {
        return postsRepository.getAllVisiblePostsByGivenTagTopCommented()
    }

    fun loadingLiveData(): LiveData<Boolean> {
        return postsRepository.getLoadingLiveData()
    }

    fun loadPostsByTag(token: String, idTag: Int, pageNumber: Int, pageSize: Int) {
        if (idTag == 0) {
            postsRepository.useCaseLoadNonDeletedPostedPosts(token, pageSize = pageSize,
                pageNumber = pageNumber)
        } else {
            postsRepository.useCaseLoadNonDeletedPostedPostsByTag(token, idTag,
                pageNumber = pageNumber, pageSize = pageSize)
        }
    }

    fun loadPostsByTagTopRated(token: String, idTag: Int, pageNumber: Int, pageSize: Int) {
        if (idTag == 0) {
            postsRepository.useCaseLoadNonDeletedPostedPostsTopRated(token, pageSize = pageSize,
                pageNumber = pageNumber)
        } else {
            postsRepository.useCaseLoadNonDeletedPostedPostsByTagTopRated(token, idTag,
                pageNumber = pageNumber, pageSize = pageSize)
        }
    }

    fun loadPostsByTagTopCommented(token: String, idTag: Int, pageNumber: Int, pageSize: Int) {
        if (idTag == 0) {
            postsRepository.useCaseLoadNonDeletedPostedPostsTopCommented(token, pageSize = pageSize,
                pageNumber = pageNumber)
        } else {
            postsRepository.useCaseLoadNonDeletedPostedPostsByTagTopCommented(token, idTag,
                pageNumber = pageNumber, pageSize = pageSize)
        }
    }

    fun sendNewPost() {
        postsRepository.useCaseSendNewPosts(PublicacionMapper().modelToDto(newPost), tagList)
    }

    fun responseCodePostSent(): LiveData<Int> {
        return postsRepository.getResponseCodePostSent()
    }

    fun getError(): LiveData<Boolean> {
        return postsRepository.finishMessage
    }

    fun loadTags() {
        tagsRepository.useCaseLoadAllTags()
    }

    fun allTags(): LiveData<List<TagDTO>> {
        return tagsRepository.getAllTags()
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

    init {
        GlobalApplication.applicationComponent?.inject(this)
    }
}