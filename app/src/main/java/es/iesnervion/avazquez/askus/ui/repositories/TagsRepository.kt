package es.iesnervion.avazquez.askus.ui.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.iesnervion.avazquez.askus.DTOs.TagDTO
import es.iesnervion.avazquez.askus.ui.usecase.LoadTagsUseCase
import javax.inject.Inject

class TagsRepository
@Inject constructor() {
    internal var loadJSONUseCase: LoadTagsUseCase
    private val loadingLiveData = MutableLiveData<Boolean>()
    private val showFinishMessage = MutableLiveData<Boolean>()
    private val allTags = MutableLiveData<List<TagDTO>>()
    val finishMessage: LiveData<Boolean>
        get() = showFinishMessage

    init {
        loadJSONUseCase = LoadTagsUseCase()
    }

    fun getLoadingLiveData(): LiveData<Boolean> {
        return loadingLiveData
    }

    fun useCaseLoadAllTags() {
        loadJSONUseCase.getAllTags(object : RepositoryInterface {
            override fun showError(show: Boolean) {
                showFinishMessage.postValue(show)
            }

            override fun onLoading(loading: Boolean) {
                loadingLiveData.postValue(loading)
            }

            override fun <T, I> onSuccess(data: List<T>, moreInfo: I?) {
                allTags.postValue(data as List<TagDTO>)
            }
        })
    }

    fun getAllTags(): LiveData<List<TagDTO>> {
        return allTags
    }
}