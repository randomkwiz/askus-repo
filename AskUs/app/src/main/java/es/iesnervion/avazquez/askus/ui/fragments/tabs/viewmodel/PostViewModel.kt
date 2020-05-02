package es.iesnervion.avazquez.askus.ui.fragments.tabs.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import es.iesnervion.avazquez.askus.DTOs.PublicacionDTO
import es.iesnervion.avazquez.askus.ui.repositories.PostsRepository
import es.iesnervion.avazquez.askus.utils.GlobalApplication
import javax.inject.Inject

class PostViewModel : ViewModel() {
    @Inject
    lateinit var postsRepository: PostsRepository

    fun allNonDeletedPublicPostedPosts(): LiveData<List<PublicacionDTO>> {
        return postsRepository.getAllNonDeletedPublicPostedPosts()
    }

    fun allNonDeletedPostedPosts(): LiveData<List<PublicacionDTO>> {
        return postsRepository.getAllNonDeletedPostedPosts()
    }
    fun loadingLiveData(): LiveData<Boolean> {
        return postsRepository.getLoadingLiveData()
    }
    fun loadPosts(token : String){
        postsRepository.useCaseLoadNonDeletedPublicPostedPosts()
        postsRepository.useCaseLoadNonDeletedPostedPosts(token)
    }
    init {
        GlobalApplication.applicationComponent?.inject(this)
    }
}