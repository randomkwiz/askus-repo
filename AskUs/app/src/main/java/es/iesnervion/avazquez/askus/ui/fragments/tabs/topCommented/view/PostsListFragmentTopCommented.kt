package es.iesnervion.avazquez.askus.ui.fragments.tabs.topCommented.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import es.iesnervion.avazquez.askus.DTOs.PostCompletoParaMostrarDTO
import es.iesnervion.avazquez.askus.DTOs.VotoPublicacionDTO
import es.iesnervion.avazquez.askus.R
import es.iesnervion.avazquez.askus.adapters.PostAdapter
import es.iesnervion.avazquez.askus.interfaces.HomeActivityCallback
import es.iesnervion.avazquez.askus.interfaces.RecyclerViewClickListener
import es.iesnervion.avazquez.askus.ui.fragments.tabs.topCommented.viewmodel.MainViewModelTopCommented
import es.iesnervion.avazquez.askus.utils.AppConstants
import es.iesnervion.avazquez.askus.utils.AppConstants.INTERNAL_SERVER_ERROR
import es.iesnervion.avazquez.askus.utils.AppConstants.NO_CONTENT
import es.iesnervion.avazquez.askus.utils.PaginationScrollListener
import es.iesnervion.avazquez.askus.utils.PaginationScrollListener.Companion.PAGE_SIZE
import es.iesnervion.avazquez.askus.utils.PaginationScrollListener.Companion.PAGE_START
import es.iesnervion.avazquez.askus.utils.UtilClass.Companion.getFormattedCurrentDatetime
import kotlinx.android.synthetic.main.fragment_posts.*
import setVisibilityToGone
import setVisibilityToVisible

/**
 * A simple [Fragment] subclass.
 */
class PostsListFragmentTopCommented : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    lateinit var viewModel: MainViewModelTopCommented
    lateinit var adapter: PostAdapter
    lateinit var sharedPreference: SharedPreferences
    lateinit var token: String
    var idTag: Int = 0
    var imgBtnUpDownVoteHasBeenClicked = false
    var idCurrentUser = 0

    //Pagination
    private var currentPage: Int = PAGE_START
    private var mIsLastPage = false
    private var totalPage = 2
    private var mIsLoading = false
    var itemCount = 0

    companion object {
        fun newInstance(idTag: Int): PostsListFragmentTopCommented {
            val myFragment = PostsListFragmentTopCommented()
            val args = Bundle()
            args.putInt("idTag", idTag)
            myFragment.arguments = args
            return myFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_posts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
        //viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel = ViewModelProviders.of(this).get(MainViewModelTopCommented::class.java)
        sharedPreference =
                activity!!.getSharedPreferences(AppConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        token = sharedPreference.getString("token", "").toString()
        idCurrentUser = sharedPreference.getInt("user_id", 0)
        idTag = arguments?.getInt("idTag") ?: 0
        initViews()
        setListeners()
        swipeRefreshLayout.setOnRefreshListener(this)
        initAdapter()
        initObservers()
        doApiCall()
    }

    override fun onRefresh() {
        itemCount = 0
        currentPage = PAGE_START
        mIsLastPage = false
        adapter.clear()
        doApiCall()
    }

    private fun setListeners() {
        recyclerView.addOnScrollListener(object :
            PaginationScrollListener(recyclerView.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                mIsLoading = true
                currentPage++
                doApiCall()
            }

            override fun getIsLastPage(): Boolean {
                return mIsLastPage
            }

            override fun getIsLoading(): Boolean {
                return mIsLoading
            }
        })
    }

    private fun doApiCall() {
        viewModel.loadPostsByTagTopCommented(token, idTag, pageNumber = currentPage,
            pageSize = PAGE_SIZE, idUsuarioLogeado = idCurrentUser)
    }

    private fun onValuesReady(areLoaded: Boolean) {
        if (areLoaded) {
            this.totalPage = viewModel.currentPaginHeader.totalPages
            //Si llega aquí significa que ya están seteados ambos valores
            if (idTag == 0   //si es 0 porque significa que pide todos los posts
                || viewModel.postsList.all { post -> idTag in post.listadoTags.map { it.id } } //o que todos los posts tengan el tag indicado
            ) {
                addElements(viewModel.postsList.toMutableList())
            }
        }
    }

    private fun onLoadedData(loading: Boolean) {
        if (loading) {
            if (!swipeRefreshLayout.isRefreshing) {
                if (adapter.itemCount == 0) {
                    progressBar.setVisibilityToVisible()
                    recyclerView.setVisibilityToGone()
                }
            }
        } else {
            progressBar.setVisibilityToGone()
            recyclerView.setVisibilityToVisible()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun onResponseCodeVoteReceived(it: Int) {
        //Hago esto de imgBtnUpDownVoteHasBeenClicked porque
        //si no, entra aquí cada vez que cambias de pestaña en el view pager
        //y te muestra el snackbar aunque no hayas pulsado el boton
        //porque el observer entra con el ultimo dato del live data
        if (imgBtnUpDownVoteHasBeenClicked) {
            when (it) {
                INTERNAL_SERVER_ERROR -> {
                    //ya has votado aqui
                    Snackbar.make(recyclerView, getString(R.string.you_cant_vote_twice),
                        Snackbar.LENGTH_SHORT).show()
                }
                NO_CONTENT            -> {
                    //ok
                    Snackbar.make(recyclerView, getString(R.string.processed_vote),
                        Snackbar.LENGTH_SHORT).show()
                }
                else                  -> {
                    //error
                    Snackbar.make(recyclerView, getString(R.string.there_was_an_error),
                        Snackbar.LENGTH_SHORT).show()
                }
            }
        }
        imgBtnUpDownVoteHasBeenClicked = false
    }

    private fun initObservers() {
        //adapter.clear()
        viewModel.responseCodeVotoPublicacionSent()
            .observe(viewLifecycleOwner, Observer(::onResponseCodeVoteReceived))
        viewModel.loadingLiveData().observe(viewLifecycleOwner, Observer(::onLoadedData))
        viewModel.areValuesReadyTopCommented()
            .observe(viewLifecycleOwner, Observer(::onValuesReady))
    }

    private fun initViews() {
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
    }

    private fun initAdapter() {
        adapter = PostAdapter(object : RecyclerViewClickListener {
            override fun onClick(view: View, position: Int) {
                val currentItem = adapter.getItem(position)
                var valoracion = false
                when (view.id) {
                    R.id.arrow_up                 -> {
                        imgBtnUpDownVoteHasBeenClicked = true
                        valoracion = true
                    }
                    R.id.arrow_down               -> {
                        imgBtnUpDownVoteHasBeenClicked = true
                        valoracion = false
                    }
                    R.id.lbl_post_title_post_row  -> {
                        postClicked(post = currentItem)
                    }
                    R.id.lbl_post_text_post_row   -> {
                        postClicked(post = currentItem)
                    }
                    R.id.lbl_author_nick_post_row -> {
                        onUserClicked(adapter.getItem(position).idAutor,
                            adapter.getItem(position).nickAutor)
                    }
                }
                if (imgBtnUpDownVoteHasBeenClicked) {
                    if (currentItem.votoDeUsuarioLogeado == null) {
                        val votoPublicacionDTO =
                                VotoPublicacionDTO(idCurrentUser, adapter.getItem(position).IdPost,
                                    valoracion, getFormattedCurrentDatetime())
                        viewModel.insertVotoPublicacion(token = token,
                            votoPublicacionDTO = votoPublicacionDTO)
                    } else {
                        //ya has votado aqui
                        Snackbar.make(recyclerView, getString(R.string.you_cant_vote_twice),
                            Snackbar.LENGTH_SHORT).show()
                        imgBtnUpDownVoteHasBeenClicked = false
                    }
                }
            }
        })
        //        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        recyclerView.adapter = adapter
    }

    private fun addElements(items: List<PostCompletoParaMostrarDTO>) {
        if (currentPage != PAGE_START) {
            adapter.removeLoading()
        }
        adapter.addItems(items.toMutableList())
        swipeRefreshLayout.isRefreshing = false
        //check weather is last page or not
        if (currentPage < totalPage) {
            adapter.addLoading()
        } else {
            mIsLastPage = true
        }
        mIsLoading = false
        //Aquí sólo entrará cuando el valor de
        //currentPaginHeader esté seteado
        //por eso nunca será null y por eso
        //necesito el MediatorLiveData
        if (adapter.itemCount >= viewModel.currentPaginHeader.totalCount) {
            adapter.removeLoading()
        }
        addImgIfNoContent()
    }

    private fun addImgIfNoContent() {
        if (adapter.itemCount == 0) {
            img_nothing_to_show.setVisibilityToVisible()
            progressBar.setVisibilityToGone()
            recyclerView.setVisibilityToGone()
        } else {
            img_nothing_to_show.setVisibilityToGone()
            progressBar.setVisibilityToGone()
            recyclerView.setVisibilityToVisible()
        }
    }

    private fun postClicked(post: PostCompletoParaMostrarDTO) {
        if (context is HomeActivityCallback) {
            (context as HomeActivityCallback).onPostClicked(post)
        }
    }

    private fun onUserClicked(idUser: Int, nickname: String) {
        if (context is HomeActivityCallback) {
            (context as HomeActivityCallback).onUserClicked(idUser = idUser, nickname = nickname,
                fromDetails = false)
        }
    }

    override fun onStart() {
        super.onStart()
        doApiCall()
        //Toast.makeText(context,"TOP COMMENTED entra en on start", Toast.LENGTH_SHORT).show()
    }
}
