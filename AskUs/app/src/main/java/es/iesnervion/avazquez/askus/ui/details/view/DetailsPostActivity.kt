package es.iesnervion.avazquez.askus.ui.details.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.ImageViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import es.iesnervion.avazquez.askus.DTOs.ComentarioParaMostrarDTO
import es.iesnervion.avazquez.askus.DTOs.PostCompletoParaMostrarDTO
import es.iesnervion.avazquez.askus.DTOs.VotoPublicacionDTO
import es.iesnervion.avazquez.askus.R
import es.iesnervion.avazquez.askus.adapters.CommentsAdapter
import es.iesnervion.avazquez.askus.interfaces.RecyclerViewClickListener
import es.iesnervion.avazquez.askus.models.Comentario
import es.iesnervion.avazquez.askus.ui.details.viewmodel.DetailsViewModel
import es.iesnervion.avazquez.askus.ui.home.view.HomeActivity
import es.iesnervion.avazquez.askus.utils.AppConstants
import es.iesnervion.avazquez.askus.utils.AppConstants.EXTRA_PARAM_POST
import es.iesnervion.avazquez.askus.utils.AppConstants.INTERNAL_SERVER_ERROR
import es.iesnervion.avazquez.askus.utils.AppConstants.NO_CONTENT
import es.iesnervion.avazquez.askus.utils.PaginationScrollListener
import es.iesnervion.avazquez.askus.utils.PaginationScrollListener.Companion.PAGE_SIZE
import es.iesnervion.avazquez.askus.utils.PaginationScrollListener.Companion.PAGE_START
import es.iesnervion.avazquez.askus.utils.UtilClass.Companion.getFormattedCurrentDatetime
import kotlinx.android.synthetic.main.activity_details_post.*
import setVisibilityToGone
import setVisibilityToVisible
import slideDown
import slideUp

class DetailsPostActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var intentPost: PostCompletoParaMostrarDTO
    lateinit var token: String
    lateinit var sharedPreference: SharedPreferences
    lateinit var viewModel: DetailsViewModel
    lateinit var commentsAdapter: CommentsAdapter
    var idCurrentUser: Int = 0

    //Pagination
    private var currentPage: Int = PAGE_START
    private var mIsLastPage = false
    private var totalPage = -1
    private var mIsLoading = false
    var itemCount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_post)
        sharedPreference = getSharedPreferences(AppConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        viewModel = ViewModelProviders.of(this).get(DetailsViewModel::class.java)
        intentPost = intent.getSerializableExtra(EXTRA_PARAM_POST) as PostCompletoParaMostrarDTO
        setDataFromPost(intentPost)
        token = sharedPreference.getString("token", "").toString()
        idCurrentUser = sharedPreference.getInt("user_id", 0)
        setCommentsAdapter()
        setSupportActionBar(appbar)
        recyclerView_comments.setHasFixedSize(true)
        //this line shows back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initObservers()
        initListeners()
        val dividerItemDecoration =
                DividerItemDecoration(recyclerView_comments.context, LinearLayout.VERTICAL)
        recyclerView_comments.addItemDecoration(dividerItemDecoration)
        loadDataFirstTime()
    }

    //setea el adapter del listado de comentarios
    private fun setCommentsAdapter() {
        commentsAdapter = CommentsAdapter(intentPost.idAutor, object : RecyclerViewClickListener {
            override fun onClick(view: View, position: Int) {
                when (view.id) {
                    R.id.lbl_author_nick -> {
                        goToUser(commentsAdapter.getItem(position).idUsuario,
                            commentsAdapter.getItem(position).nickAutor)
                    }
                }
            }
        })
        recyclerView_comments.adapter = commentsAdapter
    }

    private fun initListeners() {
        lbl_author_nick.setOnClickListener(this)
        hideCommentBox_btn.setOnClickListener(this)
        openCommentBox_btn.setOnClickListener(this)
        arrow_up.setOnClickListener(this)
        arrow_down.setOnClickListener(this)
        btn_send_comment.setOnClickListener(this)
        recyclerView_comments.addOnScrollListener(object :
            PaginationScrollListener(recyclerView_comments.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                loadItems()
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
        }
        swipeRefreshLayout.setOnRefreshListener {
            onRefresh()
        }
    }

    fun loadItems() {
        mIsLoading = true
        currentPage++
        loadData()
    }

    private fun onRefresh() {
        itemCount = 0
        currentPage = PAGE_START
        mIsLastPage = false
        commentsAdapter.clear()
        loadData()
    }

    private fun loadDataFirstTime() {
        commentsAdapter.clear()
        loadData()
    }

    private fun loadData() {
        if (token.isNotEmpty()) {
            viewModel.loadPostData(token, intentPost.IdPost, pageSize = PAGE_SIZE,
                pageNumber = currentPage, idUsuarioLogeado = idCurrentUser)
        }
    }

    private fun onValuesReady(areValuesReady: Boolean) {
        if (areValuesReady) {
            //Si llega aquí significa que ya están seteados ambos valores
            if (viewModel.currentPost?.IdPost == intentPost.IdPost) {
                //Se actualizan los votos si el post es el mismo
                upvotes_count.text = viewModel.currentPost?.cantidadVotosPositivos.toString()
                downvotes_count.text = viewModel.currentPost?.cantidadVotosNegativos.toString()
                //Se actualizan los colores
                updateOwnVote(viewModel.currentPost?.votoDeUsuarioLogeado)
                this.totalPage = viewModel.currentPaginHeader.totalPages
                viewModel.currentPost?.listadoComentarios?.let { it1 -> addElements(it1) }
            }
        }
    }

    private fun onCommentSent(code: Int) {
        when (code) {
            NO_CONTENT -> {
                showToast(getString(R.string.comment_sent))
                clearCommentEditText()
            }
            else       -> {
                showToast(getString(R.string.error_sending_comment))
                btn_send_comment.isEnabled = true
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun onResponseCodeVoteReceived(code: Int) {
        when (code) {
            INTERNAL_SERVER_ERROR -> {
                //ya has votado aqui
                showSnackBar(getString(R.string.you_cant_vote_twice))
            }
            NO_CONTENT            -> {
                //ok
                showSnackBar(getString(R.string.processed_vote))
                loadData()
            }
            else                  -> {
                //error
                showSnackBar(getString(R.string.there_was_an_error))
            }
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(recyclerView_comments, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun initObservers() {
        viewModel.areValuesReady().observe(this, Observer(::onValuesReady))
        viewModel.getInsertedCommentResponseCode().observe(this, Observer(::onCommentSent))
        viewModel.responseCodeVotoPublicacionSent()
            .observe(this, Observer(::onResponseCodeVoteReceived))
    }

    //limpia los campos de entrada de texto
    //y vuelve a habilitar el btn de enviar
    private fun clearCommentEditText() {
        comment_title.text?.clear()
        comment_text.text!!.clear()
        viewModel.commentToSend.idPublicacion = 0
        viewModel.commentToSend.fechaPublicacion = ""
        viewModel.commentToSend.texto = ""
        viewModel.commentToSend.titulo = ""
        btn_send_comment.isEnabled = true
    }

    //Setea los datos de entrada del usuario a los datos de post actual
    //elimina multiples espacios en blanco y saltos de linea duplicados
    private fun setDataFromPost(currentPost: PostCompletoParaMostrarDTO) {
        appbar.title = currentPost.tituloPost
        lbl_post_title.text = currentPost.tituloPost.replace("(?m)(^ *| +(?= |$))".toRegex(), "")
            .replace("(?m)^$([\r\n]+?)(^$[\r\n]+?^)+".toRegex(), "$1")
        lbl_post_text.text = currentPost.cuerpoPost.replace("(?m)(^ *| +(?= |$))".toRegex(), "")
            .replace("(?m)^$([\r\n]+?)(^$[\r\n]+?^)+".toRegex(), "$1")
        upvotes_count.text = currentPost.cantidadVotosPositivos.toString()
        downvotes_count.text = currentPost.cantidadVotosNegativos.toString()
        lbl_tag_lists.text = currentPost.listadoTags.joinToString()
        lbl_author_nick.text = currentPost.nickAutor
        updateOwnVote(currentPost.votoDeUsuarioLogeado)
    }

    private fun updateOwnVote(voto: VotoPublicacionDTO?) {
        //Primero limpia las casillas
        ImageViewCompat.setImageTintList(arrow_up, null)
        ImageViewCompat.setImageTintList(arrow_down, null)
        //Luego si es necesario pone los tintes
        if (voto != null) {
            if (voto.valoracion) {
                //es positiva
                ImageViewCompat.setImageTintList(arrow_up,
                    ColorStateList.valueOf(Color.parseColor("#0C8C00")))
            } else {
                //es negativa
                ImageViewCompat.setImageTintList(arrow_down,
                    ColorStateList.valueOf(Color.parseColor("#FF0000")))
            }
        }
    }

    override fun onClick(v: View) {
        var valoracion = false
        var isBtnVoteClicked = false
        when (v.id) {
            R.id.arrow_up           -> {
                valoracion = true
                isBtnVoteClicked = true
            }
            R.id.arrow_down         -> {
                valoracion = false
                isBtnVoteClicked = true
            }
            R.id.btn_send_comment   -> {
                onSendCommentClicked()
            }
            R.id.hideCommentBox_btn -> {
                onHideCommentBoxClicked()
            }
            R.id.openCommentBox_btn -> {
                onOpenCommentBoxClicked()
            }
            R.id.lbl_author_nick    -> {
                goToUser(intentPost.idAutor, intentPost.nickAutor)
            }
        }
        if (isBtnVoteClicked) {
            onVoteBtnClicked(valoracion)
        }
    }

    private fun onVoteBtnClicked(valoracion: Boolean) {
        if (intentPost.votoDeUsuarioLogeado == null) {
            val votoPublicacionDTO =
                    VotoPublicacionDTO(idCurrentUser, intentPost.IdPost, valoracion,
                        getFormattedCurrentDatetime())
            viewModel.insertVotoPublicacion(token = token, votoPublicacionDTO = votoPublicacionDTO)
        } else {
            //ya has votado aqui
            Snackbar.make(recyclerView_comments, getString(R.string.you_cant_vote_twice),
                Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun onOpenCommentBoxClicked() {
        hideCommentBox_btn.isClickable =
                true  //para que no le pueda dar dos veces seguidas (hace cosas raras la animacion)
        openCommentBox_btn.isClickable = false
        openCommentBox_btn.setVisibilityToGone()
        openCommentBox_btn.slideDown()
        create_comment_content_l_layout.slideUp()
    }

    private fun onHideCommentBoxClicked() {
        hideCommentBox_btn.isClickable =
                false  //para que no le pueda dar dos veces seguidas (hace cosas raras la animacion)
        openCommentBox_btn.isClickable = true
        create_comment_content_l_layout.slideDown()
        openCommentBox_btn.setVisibilityToVisible()
        openCommentBox_btn.slideUp()
    }

    private fun onSendCommentClicked() {
        if (fieldsAreFilled()) {
            btn_send_comment.isEnabled = false
            viewModel.commentToSend = Comentario(0, getFormattedCurrentDatetime(), 0, idCurrentUser,
                intentPost.IdPost, false, false, comment_text.text.toString(),
                comment_title.text.toString())
            viewModel.insertComment()
        } else {
            Toast.makeText(applicationContext, getString(R.string.fillFields), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun goToUser(idUser: Int, nickUser: String) {
        startActivity(Intent(this, HomeActivity::class.java).putExtra("type", "loadProfileFragment")
            .putExtra("idUserToLoad", idUser).putExtra("nicknameUserToLoad", nickUser))
    }

    private fun fieldsAreFilled(): Boolean {
        return comment_title.text!!.isNotEmpty() && comment_text.text!!.isNotEmpty()
    }

    private fun addElements(items: List<ComentarioParaMostrarDTO>) {
        if (currentPage != PAGE_START) {
            commentsAdapter.removeLoading()
        }
        commentsAdapter.addItems(items.toMutableList())
        swipeRefreshLayout.isRefreshing = false
        //check weather is last page or not
        if (currentPage < totalPage) {
            commentsAdapter.addLoading()
        } else {
            mIsLastPage = true
        }
        mIsLoading = false
        //Aquí sólo entrará cuando el valor de
        //currentPaginHeader esté seteado
        //por eso nunca será null y por eso
        //necesito el MediatorLiveData
        if (commentsAdapter.itemCount >= viewModel.currentPaginHeader.totalCount) {
            commentsAdapter.removeLoading()
        }
        addImgIfNoContent()
    }

    private fun addImgIfNoContent() {
        if (commentsAdapter.itemCount == 0) {
            no_comments_to_show.setVisibilityToVisible()
            progressBar_comments.setVisibilityToGone()
            recyclerView_comments.setVisibilityToGone()
        } else {
            no_comments_to_show.setVisibilityToGone()
            progressBar_comments.setVisibilityToGone()
            recyclerView_comments.setVisibilityToVisible()
        }
    }
}
