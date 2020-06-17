package es.iesnervion.avazquez.askus.ui.fragments.tabs.all.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import es.iesnervion.avazquez.askus.DTOs.*
import es.iesnervion.avazquez.askus.mappers.PublicacionMapper
import es.iesnervion.avazquez.askus.models.Publicacion
import es.iesnervion.avazquez.askus.ui.repositories.PostsRepository
import es.iesnervion.avazquez.askus.ui.repositories.TagsRepository
import es.iesnervion.avazquez.askus.ui.repositories.UsersRepository
import es.iesnervion.avazquez.askus.ui.repositories.VotesRepository
import es.iesnervion.avazquez.askus.utils.GlobalApplication
import javax.inject.Inject

class MainViewModel : ViewModel() {
    @Inject
    lateinit var postsRepository: PostsRepository

    @Inject
    lateinit var tagsRepository: TagsRepository

    @Inject
    lateinit var usersRepository: UsersRepository

    @Inject
    lateinit var votesRepository: VotesRepository
    var newPost: Publicacion = Publicacion(id = 0, idAutor = 0, texto = "")
    lateinit var tagList: List<Int>
    var saveStateMenu = 0
    var areValuesReadyAll = MediatorLiveData<Boolean>()
    lateinit var currentPaginHeader: PaginHeader
    lateinit var postsList: List<PostCompletoParaMostrarDTO>

    init {
        GlobalApplication.applicationComponent?.inject(this)
        //All
        areValuesReadyAll = MediatorLiveData<Boolean>().apply {
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
            addSource(allVisiblePostsByTag()) { x ->
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

    fun areValuesReadyAll(): LiveData<Boolean> {
        return areValuesReadyAll
    }

    fun allVisiblePostsByTag(): LiveData<List<PostCompletoParaMostrarDTO>> {
        return postsRepository.getAllVisiblePostsByGivenTag()
    }

    fun loadingLiveData(): LiveData<Boolean> {
        return postsRepository.getLoadingLiveData()
    }

    fun loadPostsByTag(token: String,
        idTag: Int,
        pageNumber: Int,
        pageSize: Int,
        idUsuarioLogeado: Int) {
        if (idTag == 0) {
            postsRepository.useCaseLoadNonDeletedPostedPosts(token, pageSize = pageSize,
                pageNumber = pageNumber, idUsuarioLogeado = idUsuarioLogeado)
        } else {
            postsRepository.useCaseLoadNonDeletedPostedPostsByTag(token, idTag,
                pageNumber = pageNumber, pageSize = pageSize, idUsuarioLogeado = idUsuarioLogeado)
        }
    }

    fun loadMyUser(id: Int, token: String) {
        usersRepository.useCaseLoadFullUser(token = token, id = id)
    }

    fun getFullUser(): LiveData<UserDTO> {
        return usersRepository.getFullUser()
    }

    fun makeUserAModerator(token: String, idUser: Int) {
        usersRepository.useCaseMakeUserAModerator(token = token, idUser = idUser)
    }

    fun getResponseCodeMakeUserAModerator() = usersRepository.getResponseCodeMakeUserAModerator()
    fun getResponseCodePasswordChange() = usersRepository.getResponseCodeChangePassword()
    fun getResponseCodeDeleteUser() = usersRepository.getResponseCodeDeleteUser()
    fun sendNewPost() {
        postsRepository.useCaseSendNewPosts(PublicacionMapper().modelToDto(newPost), tagList)
    }

    fun changePassword(token: String, idUser: Int, newPassword: String) {
        usersRepository.useCaseChangePassword(token = token, idUser = idUser,
            newPassword = newPassword)
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

    fun deleteAccount(idCurrentUser: Int, token: String) {
        usersRepository.useCaseDeleteUser(token = token, idUser = idCurrentUser)
    }
}