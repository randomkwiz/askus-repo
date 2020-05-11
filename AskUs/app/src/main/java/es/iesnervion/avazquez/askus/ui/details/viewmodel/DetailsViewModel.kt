package es.iesnervion.avazquez.askus.ui.details.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import es.iesnervion.avazquez.askus.DTOs.ComentarioDTO
import es.iesnervion.avazquez.askus.DTOs.PostCompletoListadoComentariosDTO
import es.iesnervion.avazquez.askus.DTOs.VotoPublicacionDTO
import es.iesnervion.avazquez.askus.ui.repositories.CommentsRepository
import es.iesnervion.avazquez.askus.ui.repositories.PostsRepository
import es.iesnervion.avazquez.askus.ui.repositories.VotesRepository
import es.iesnervion.avazquez.askus.utils.GlobalApplication
import javax.inject.Inject

class DetailsViewModel : ViewModel() {
    @Inject
    lateinit var postsRepository: PostsRepository

    @Inject
    lateinit var votesRepository: VotesRepository

    @Inject
    lateinit var commentsRepository: CommentsRepository
    var currentPost: PostCompletoListadoComentariosDTO? = null
    lateinit var commentToSend: ComentarioDTO

    init {
        GlobalApplication.applicationComponent?.inject(this)
    }

    fun loadPostData(token: String, idPost: Int) {
        postsRepository.useCaseLoadPostWithAllComments(token, idPost)
    }

    fun getPostWithComments(): LiveData<PostCompletoListadoComentariosDTO> {
        return postsRepository.getPostWithAllComments()
    }

    fun insertVotoPublicacion(token: String, votoPublicacionDTO: VotoPublicacionDTO) {
        votesRepository.useCaseInsertVotoPublicacion(votoPublicacion = votoPublicacionDTO,
            token = token)
    }

    fun insertComment() {
        commentsRepository.useCaseInsertComment(commentToSend)
    }

    fun getInsertedCommentResponseCode(): LiveData<Int> {
        return commentsRepository.getResponseCodeCommentSent()
    }
}