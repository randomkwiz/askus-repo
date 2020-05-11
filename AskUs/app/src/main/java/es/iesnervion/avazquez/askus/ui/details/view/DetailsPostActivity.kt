package es.iesnervion.avazquez.askus.ui.details.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import es.iesnervion.avazquez.askus.DTOs.ComentarioDTO
import es.iesnervion.avazquez.askus.DTOs.PostCompletoListadoComentariosDTO
import es.iesnervion.avazquez.askus.DTOs.VotoPublicacionDTO
import es.iesnervion.avazquez.askus.R
import es.iesnervion.avazquez.askus.adapters.CommentsAdapter
import es.iesnervion.avazquez.askus.ui.details.viewmodel.DetailsViewModel
import es.iesnervion.avazquez.askus.utils.AppConstants
import es.iesnervion.avazquez.askus.utils.AppConstants.NO_CONTENT
import es.iesnervion.avazquez.askus.utils.UtilClass.Companion.getFormattedCurrentDatetime
import kotlinx.android.synthetic.main.activity_details_post.*

class DetailsPostActivity : AppCompatActivity(), View.OnClickListener {
    var idPost: Int = 0
    lateinit var token: String
    lateinit var sharedPreference: SharedPreferences
    lateinit var viewModel: DetailsViewModel
    lateinit var currentPostObserver: Observer<PostCompletoListadoComentariosDTO>
    lateinit var commentSentObserver: Observer<Int>
    lateinit var commentsAdapter: CommentsAdapter
    var btnSendHasBeenClicked = false
    var idCurrentUser: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_post)
        sharedPreference = getSharedPreferences(AppConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        viewModel = ViewModelProviders.of(this).get(DetailsViewModel::class.java)
        idPost = intent.getIntExtra("idPost", 0)
        token = sharedPreference.getString("token", "").toString()
        idCurrentUser = sharedPreference.getInt("user_id", 0)
        if (token.isNotEmpty()) {
            viewModel.loadPostData(token, idPost)
        }
        setSupportActionBar(appbar);
        recyclerView_comments.setHasFixedSize(true)
        //this line shows back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        if (viewModel.currentPost != null) {
            setData(viewModel.currentPost!!)
        }
        initObservers()
        initListeners()
    }

    private fun initListeners() {
        arrow_up.setOnClickListener(this)
        arrow_down.setOnClickListener(this)
        btn_send_comment.setOnClickListener(this)
        appbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun initObservers() {
        currentPostObserver = Observer {
            viewModel.currentPost = it
            setData(it)
            appbar.title = it.tituloPost
        }
        viewModel.getPostWithComments().observe(this, currentPostObserver)

        commentSentObserver = Observer {
            if (btnSendHasBeenClicked) {
                when (it) {
                    NO_CONTENT -> {
                        Toast.makeText(applicationContext, "Comment sent", Toast.LENGTH_SHORT)
                            .show()
                    }
                    else -> {
                        Toast.makeText(applicationContext,
                            "Error sending comment",
                            Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
            btnSendHasBeenClicked = false
            //TODO creo que no hace falta esto del btn has been clicked porque esto es una activity
        }
        viewModel.getInsertedCommentResponseCode().observe(this, commentSentObserver)
    }

    private fun setData(currentPost: PostCompletoListadoComentariosDTO) {
        appbar.title = currentPost.tituloPost
        lbl_post_title.text = currentPost.tituloPost
        lbl_post_text.text = currentPost.cuerpoPost
        upvotes_count.text = currentPost.cantidadVotosPositivos.toString()
        downvotes_count.text = currentPost.cantidadVotosNegativos.toString()
        lbl_tag_lists.text = currentPost.listadoTags.joinToString()
        lbl_author_nick.text = currentPost.nickAutor
        commentsAdapter = CommentsAdapter(currentPost.listadoComentarios)
        recyclerView_comments.adapter = commentsAdapter
    }

    override fun onClick(v: View) {
        var valoracion: Boolean = false
        var isBtnVoteClicked = false
        when (v.id) {
            R.id.arrow_up -> {
                valoracion = true
                isBtnVoteClicked = true
            }
            R.id.arrow_down -> {
                valoracion = false
                isBtnVoteClicked = true
            }
            R.id.btn_send_comment -> {
                if (fieldsAreFilled()) {
                    viewModel.commentToSend =
                        ComentarioDTO(0,
                            getFormattedCurrentDatetime(),
                            0,
                            idCurrentUser,
                            idPost,
                            false,
                            false,
                            comment_text.text.toString(),
                            comment_title.text.toString())
                    viewModel.insertComment()
                    btnSendHasBeenClicked = true
                }
            }
        }
        if (isBtnVoteClicked) {
            val votoPublicacionDTO =
                VotoPublicacionDTO(idCurrentUser, idPost, valoracion,
                    getFormattedCurrentDatetime()
                )
            viewModel.insertVotoPublicacion(token = token,
                votoPublicacionDTO = votoPublicacionDTO)
        }
    }

    private fun fieldsAreFilled(): Boolean {
        return comment_title.text.isNotEmpty() && comment_text.text.isNotEmpty()
    }
}
