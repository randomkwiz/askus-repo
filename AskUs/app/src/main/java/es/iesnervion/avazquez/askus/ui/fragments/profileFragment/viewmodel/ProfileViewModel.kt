package es.iesnervion.avazquez.askus.ui.fragments.profileFragment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import es.iesnervion.avazquez.askus.DTOs.LogroDTO
import es.iesnervion.avazquez.askus.DTOs.PostCompletoParaMostrarDTO
import es.iesnervion.avazquez.askus.DTOs.ProfileDTO
import es.iesnervion.avazquez.askus.ui.repositories.LogrosRepository
import es.iesnervion.avazquez.askus.ui.repositories.PostsRepository
import es.iesnervion.avazquez.askus.ui.repositories.UsersRepository
import es.iesnervion.avazquez.askus.utils.GlobalApplication
import javax.inject.Inject

class ProfileViewModel : ViewModel() {
    @Inject
    lateinit var usersRepository: UsersRepository

    @Inject
    lateinit var postRepository: PostsRepository

    @Inject
    lateinit var logrosRepository: LogrosRepository
    var areValuesReady = MediatorLiveData<Boolean>()
    lateinit var allLogros: List<LogroDTO>
    lateinit var myPosts: List<PostCompletoParaMostrarDTO>
    lateinit var idLogrosFromUser: List<Int>
    fun loadingLiveData(): LiveData<Boolean> {
        return usersRepository.getLoadingLiveData()
    }

    fun getError(): LiveData<Boolean> {
        return usersRepository.finishMessage
    }

    fun loadUserProfile(idUser: Int) {
        usersRepository.useCaseLoadUserProfile(idUser = idUser)
    }

    fun loadAllLogros() {
        logrosRepository.useCaseLoadAllLogros()
    }

    fun loadMyPosts(token: String, pageSize: Int, pageNumber: Int, idUsuarioLogeado: Int) {
        postRepository.useCaseLoadNonDeletedPostedPosts(token = token, pageSize = pageSize,
            pageNumber = pageNumber, idUsuarioLogeado = idUsuarioLogeado)
    }

    fun myPosts(): LiveData<List<PostCompletoParaMostrarDTO>> {
        return postRepository.getAllVisiblePostsByGivenTag()
    }

    fun loadLogrosFromUser(token: String, idUser: Int) {
        logrosRepository.useCaseLoadLogrosFromUser(token = token, idUser = idUser)
    }

    fun getAllLogros(): LiveData<List<LogroDTO>> {
        return logrosRepository.getAllLogros()
    }

    fun getLogrosFromUser(): LiveData<List<LogroDTO>> {
        return logrosRepository.getLogrosFromUser()
    }

    fun getUserProfile(): LiveData<ProfileDTO> {
        return usersRepository.getUserProfile()
    }

    fun getAreValuesReady(): LiveData<Boolean> {
        return areValuesReady
    }

    init {
        GlobalApplication.applicationComponent?.inject(this)
        areValuesReady = MediatorLiveData<Boolean>().apply {
            var allLogrosFlag = false
            var logrosFromUserFlag = false
            var postsFromUserFlag = false
            value = false
            addSource(getAllLogros()) { x ->
                x?.let {
                    allLogrosFlag = x.size > 0
                    allLogros = x.toList()
                    if (allLogrosFlag && logrosFromUserFlag && postsFromUserFlag) {
                        value = true
                    }
                }
            }
            addSource(getLogrosFromUser()) { x ->
                x?.let {
                    logrosFromUserFlag = x.size >= 0
                    idLogrosFromUser = x.map { it.id }
                    if (allLogrosFlag && logrosFromUserFlag && postsFromUserFlag) {
                        value = true
                    }
                }
            }
            addSource(myPosts()) { x ->
                x?.let {
                    postsFromUserFlag = x.size >= 0
                    myPosts = x.map { it }
                    if (allLogrosFlag && logrosFromUserFlag && postsFromUserFlag) {
                        value = true
                    }
                }
            }
        }
    }
}