package es.iesnervion.avazquez.askus.ui.fragments.tabs.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import es.iesnervion.avazquez.askus.DTOs.PostCompletoParaMostrarDTO
import es.iesnervion.avazquez.askus.DTOs.TagDTO
import es.iesnervion.avazquez.askus.mappers.PublicacionMapper
import es.iesnervion.avazquez.askus.models.Publicacion
import es.iesnervion.avazquez.askus.ui.repositories.PostsRepository
import es.iesnervion.avazquez.askus.ui.repositories.TagsRepository
import es.iesnervion.avazquez.askus.utils.GlobalApplication
import javax.inject.Inject

class MainViewModel : ViewModel() {
    @Inject
    lateinit var postsRepository: PostsRepository
    @Inject
    lateinit var tagsRepository: TagsRepository
    var newPost: Publicacion = Publicacion(id = 0, idAutor = 0, texto = "")
    lateinit var tagList: List<Int>


    fun allVisiblePostsByTag(): LiveData<List<PostCompletoParaMostrarDTO>> {
        return postsRepository.getAllVisiblePostsByGivenTag()
    }

    fun loadingLiveData(): LiveData<Boolean> {
        return postsRepository.getLoadingLiveData()
    }

    fun loadPostsByTag(token: String, idTag: Int) {
        if (idTag == 0) {
            postsRepository.useCaseLoadNonDeletedPostedPosts(token)
        } else {
            postsRepository.useCaseLoadNonDeletedPostedPostsByTag(token, idTag)
        }
    }

    fun sendNewPost() {
        postsRepository.useCaseSendNewPosts(PublicacionMapper().modelToDto(newPost), tagList)
    }

    fun loadTags() {
        tagsRepository.useCaseLoadAllTags()
    }

    fun allTags(): LiveData<List<TagDTO>> {
        return tagsRepository.getAllTags()
    }

    init {
        GlobalApplication.applicationComponent?.inject(this)
    }
}