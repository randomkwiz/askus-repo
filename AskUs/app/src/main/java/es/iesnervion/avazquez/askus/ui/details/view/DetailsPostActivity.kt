package es.iesnervion.avazquez.askus.ui.details.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import es.iesnervion.avazquez.askus.DTOs.ComentarioParaMostrarDTO
import es.iesnervion.avazquez.askus.DTOs.PaginHeader
import es.iesnervion.avazquez.askus.DTOs.PostCompletoListadoComentariosDTO
import es.iesnervion.avazquez.askus.DTOs.VotoPublicacionDTO
import es.iesnervion.avazquez.askus.R
import es.iesnervion.avazquez.askus.adapters.CommentsAdapter
import es.iesnervion.avazquez.askus.models.Comentario
import es.iesnervion.avazquez.askus.ui.details.viewmodel.DetailsViewModel
import es.iesnervion.avazquez.askus.utils.AppConstants
import es.iesnervion.avazquez.askus.utils.AppConstants.NO_CONTENT
import es.iesnervion.avazquez.askus.utils.PaginationScrollListener
import es.iesnervion.avazquez.askus.utils.PaginationScrollListener.Companion.PAGE_SIZE
import es.iesnervion.avazquez.askus.utils.PaginationScrollListener.Companion.PAGE_START
import es.iesnervion.avazquez.askus.utils.UtilClass.Companion.getFormattedCurrentDatetime
import kotlinx.android.synthetic.main.activity_details_post.*
import setVisibilityToGone
import setVisibilityToVisible

class DetailsPostActivity : AppCompatActivity(), View.OnClickListener {
    var idPost: Int = 0
    lateinit var token: String
    lateinit var sharedPreference: SharedPreferences
    lateinit var viewModel: DetailsViewModel
    lateinit var observerLoadingData: Observer<Boolean>
    lateinit var currentPostObserver: Observer<PostCompletoListadoComentariosDTO>
    lateinit var commentSentObserver: Observer<Int>
    lateinit var areValuesReadyObserver: Observer<Boolean>
    lateinit var commentsAdapter: CommentsAdapter
    var idCurrentUser: Int = 0

    //Pagination
    private var currentPage: Int = PaginationScrollListener.PAGE_START
    private var mIsLastPage = false
    private var totalPage = -1
    private var mIsLoading = false
    lateinit var observerTotalPage: Observer<PaginHeader>

    //    var areTotalPagesReady = false
    //    var isCurrentPostReady = false
    //    var isCurrentPageAlreadyPosted = false
    var itemCount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_post)
        sharedPreference = getSharedPreferences(AppConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        viewModel = ViewModelProviders.of(this).get(DetailsViewModel::class.java)
        idPost = intent.getIntExtra("idPost", 0)
        token = sharedPreference.getString("token", "").toString()
        idCurrentUser = sharedPreference.getInt("user_id", 0)

        commentsAdapter = CommentsAdapter()
        commentsAdapter.clear()
        recyclerView_comments.adapter = commentsAdapter
        viewModel.loadPostData(token, idPost, pageSize = PAGE_SIZE, pageNumber = currentPage)
        setSupportActionBar(appbar);
        recyclerView_comments.setHasFixedSize(true)
        //this line shows back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        initObservers()
        initListeners()
        val dividerItemDecoration =
                DividerItemDecoration(recyclerView_comments.context, LinearLayout.VERTICAL)
        recyclerView_comments.addItemDecoration(dividerItemDecoration)
    }

    private fun initListeners() {
        arrow_up.setOnClickListener(this)
        arrow_down.setOnClickListener(this)
        btn_send_comment.setOnClickListener(this)
        recyclerView_comments.addOnScrollListener(object :
            PaginationScrollListener(recyclerView_comments.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                mIsLoading = true
                currentPage++
                loadData()
            }

            override fun getIsLastPage(): Boolean {
                return mIsLastPage
            }

            override fun getIsLoading(): Boolean {
                return mIsLoading
            }
        })
        appbar.setNavigationOnClickListener {
            onBackPressed()
            finish()
        }
        swipeRefreshLayout.setOnRefreshListener {
            itemCount = 0;
            currentPage = PAGE_START;
            mIsLastPage = false;
            commentsAdapter.clear();
            loadData()
        }
    }

    private fun loadData() {
        if (token.isNotEmpty() && totalPage > -1) {
            viewModel.loadPostData(token, idPost, pageSize = PAGE_SIZE, pageNumber = currentPage)
        }
    }

    private fun initObservers() {
        areValuesReadyObserver = Observer {
            if (it) {
                viewModel.currentPost?.let { it1 -> setData(it1) }
                viewModel.currentPost?.listadoComentarios?.let { it1 -> addElements(it1) }
            }
        }
        viewModel.areValuesReady().observe(this, areValuesReadyObserver)

        observerTotalPage = Observer {
            if (it.totalPages != this.totalPage) {
                this.totalPage = it.totalPages
            }
        }
        viewModel.getPaginHeaders().observe(this, observerTotalPage)
        currentPostObserver = Observer {
            viewModel.currentPost = it
        }
        viewModel.getPostWithComments().observe(this, currentPostObserver)

        commentSentObserver = Observer {
            when (it) {
                NO_CONTENT -> {
                    Toast.makeText(applicationContext, getString(R.string.comment_sent),
                        Toast.LENGTH_SHORT).show()
                    clearCommentEditText()
                }
                else       -> {
                    Toast.makeText(applicationContext, getString(R.string.error_sending_comment),
                        Toast.LENGTH_SHORT).show()
                    btn_send_comment.isEnabled = true
                }
            }
        }
        viewModel.getInsertedCommentResponseCode().observe(this, commentSentObserver)

        observerLoadingData = Observer { loading ->
            if (loading) {
                if (!swipeRefreshLayout.isRefreshing) {
                    progressBar.setVisibilityToVisible()
                    recyclerView_comments.setVisibilityToGone()
                    ctlLayout.setVisibilityToGone()
                    create_comment_content_l_layout.setVisibilityToGone()
                }
            } else {
                progressBar.setVisibilityToGone()
                recyclerView_comments.setVisibilityToVisible()
                ctlLayout.setVisibilityToVisible()
                create_comment_content_l_layout.setVisibilityToVisible()
                swipeRefreshLayout.isRefreshing = false
            }
        }
        //viewModel.loadingLiveData().observe(this, observerLoadingData)
    }

    private fun clearCommentEditText() {
        comment_title.text.clear()
        comment_text.text.clear()
        viewModel.commentToSend.idPublicacion = 0
        viewModel.commentToSend.fechaPublicacion = ""
        viewModel.commentToSend.texto = ""
        viewModel.commentToSend.titulo = ""
        btn_send_comment.isEnabled = true
    }

    private fun setData(currentPost: PostCompletoListadoComentariosDTO) {
        appbar.title = currentPost.tituloPost
        lbl_post_title.text = currentPost.tituloPost
        lbl_post_text.text = currentPost.cuerpoPost
        upvotes_count.text = currentPost.cantidadVotosPositivos.toString()
        downvotes_count.text = currentPost.cantidadVotosNegativos.toString()
        lbl_tag_lists.text = currentPost.listadoTags.joinToString()
        lbl_author_nick.text = currentPost.nickAutor
    }

    private fun clearData() {
        appbar.title = ""
        lbl_post_title.text = ""
        lbl_post_text.text = ""
        upvotes_count.text = 0.toString()
        downvotes_count.text = 0.toString()
        lbl_tag_lists.text = ""
        lbl_author_nick.text = ""
    }

    override fun onClick(v: View) {
        var valoracion: Boolean = false
        var isBtnVoteClicked = false
        when (v.id) {
            R.id.arrow_up         -> {
                valoracion = true
                isBtnVoteClicked = true
            }
            R.id.arrow_down       -> {
                valoracion = false
                isBtnVoteClicked = true
            }
            R.id.btn_send_comment -> {
                if (fieldsAreFilled()) {
                    btn_send_comment.isEnabled = false
                    viewModel.commentToSend =
                            Comentario(0, getFormattedCurrentDatetime(), 0, idCurrentUser, idPost,
                                false, false, comment_text.text.toString(),
                                comment_title.text.toString())
                    viewModel.insertComment()
                } else {
                    Toast.makeText(applicationContext, getString(R.string.fillFields),
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
        if (isBtnVoteClicked) {
            val votoPublicacionDTO = VotoPublicacionDTO(idCurrentUser, idPost, valoracion,
                getFormattedCurrentDatetime())
            viewModel.insertVotoPublicacion(token = token, votoPublicacionDTO = votoPublicacionDTO)
        }
    }

    private fun fieldsAreFilled(): Boolean {
        return comment_title.text.isNotEmpty() && comment_text.text.isNotEmpty()
    }

    private fun addElements(items: List<ComentarioParaMostrarDTO>) {
        if (currentPage != PAGE_START) {
            commentsAdapter.removeLoading()
        }
        commentsAdapter.addItems(items.toMutableList())
        swipeRefreshLayout.isRefreshing = false
        //check weather is last page or not
        if (totalPage == -1) {
            totalPage = viewModel.getPaginHeaders().value?.totalPages ?: -1
        }

        if (currentPage < totalPage) {
            commentsAdapter.addLoading()
        } else {
            mIsLastPage = true
        }
        mIsLoading = false
    }

    override fun onStop() {
        super.onStop()
        clearData()
        commentsAdapter.clear()
        viewModel.currentPost = null
    }
}
