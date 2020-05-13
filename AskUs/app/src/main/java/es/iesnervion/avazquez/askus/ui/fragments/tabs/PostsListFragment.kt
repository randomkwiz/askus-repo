package es.iesnervion.avazquez.askus.ui.fragments.tabs

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
import es.iesnervion.avazquez.askus.DTOs.PaginHeader
import es.iesnervion.avazquez.askus.DTOs.PostCompletoParaMostrarDTO
import es.iesnervion.avazquez.askus.DTOs.VotoPublicacionDTO
import es.iesnervion.avazquez.askus.R
import es.iesnervion.avazquez.askus.adapters.PostAdapter
import es.iesnervion.avazquez.askus.interfaces.HomeActivityCallback
import es.iesnervion.avazquez.askus.interfaces.RecyclerViewClickListener
import es.iesnervion.avazquez.askus.ui.fragments.tabs.viewmodel.MainViewModel
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
class PostsListFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    lateinit var viewModel: MainViewModel
    lateinit var adapter: PostAdapter
    lateinit var observerPosts: Observer<List<PostCompletoParaMostrarDTO>>
    lateinit var observerLoadingData: Observer<Boolean>
    lateinit var observerResponseCodeVote: Observer<Int>
    lateinit var filterType: String
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
    lateinit var observerTotalPage: Observer<PaginHeader>
    var itemCount = 0

    companion object {
        fun newInstance(filter: String, idTag: Int): PostsListFragment {
            val myFragment = PostsListFragment()
            val args = Bundle()
            args.putString("filter", filter)
            args.putInt("idTag", idTag)
            myFragment.arguments = args
            return myFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        filterType = arguments?.getString("filter") ?: ""
        return inflater.inflate(R.layout.fragment_posts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        //viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        sharedPreference =
                activity!!.getSharedPreferences(AppConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        token = sharedPreference.getString("token", "").toString()
        idCurrentUser = sharedPreference.getInt("user_id", 0)
        idTag = arguments?.getInt("idTag") ?: 0

        initViews()
        initObservers()
        setListeners()
        swipeRefreshLayout.setOnRefreshListener(this)
        initAdapter()
        doApiCall()
    }
    override fun onRefresh() {
        itemCount = 0;
        currentPage = PAGE_START;
        mIsLastPage = false;
        adapter.clear();
        doApiCall();
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
        when (filterType) {
            "ALL"           -> {
                viewModel.loadPostsByTag(token, idTag, currentPage, PAGE_SIZE)
            }
            "TOP_RATED"     -> {
                viewModel.loadPostsByTag(token, idTag, currentPage, PAGE_SIZE)
            }
            "TOP_COMMENTED" -> {
                viewModel.loadPostsByTag(token, idTag, currentPage, PAGE_SIZE)
            }
        }
    }

    private fun initObservers() {
        observerPosts = Observer { post ->
            if (post.isNotEmpty()) {
                when (filterType) {
                    "ALL"           -> {
                        // setAdapter(post)
                        addElements(post.toMutableList())
                    }
                    "TOP_RATED"     -> {
                        // setAdapter(post.sortedByDescending { it.cantidadVotosPositivos })
                        addElements(
                            post.sortedByDescending { it.cantidadVotosPositivos }.toMutableList())
                    }
                    "TOP_COMMENTED" -> {
                        //setAdapter(post.sortedByDescending { it.cantidadComentarios })
                        addElements(
                            post.sortedByDescending { it.cantidadComentarios }.toMutableList())
                    }
                }
            }
        }
        observerLoadingData = Observer { loading ->
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

        observerResponseCodeVote = Observer {
            //Hago esto de imgBtnUpDownVoteHasBeenClicked porque
            //si no, entra aquí cada vez que cambias de pestaña en el view pager
            //y te muestra el snackbar aunque no hayas pulsado el boton
            //porque el observer entra con el ultimo dato del live data
            if (imgBtnUpDownVoteHasBeenClicked) {
                when (it) {
                    INTERNAL_SERVER_ERROR -> {
                        //ya has votado aqui
                        Snackbar.make(recyclerView, // Parent view
                            getString(R.string.you_cant_vote_twice), // Message to show
                            Snackbar.LENGTH_SHORT // How long to display the message.
                        ).show()
                    }
                    NO_CONTENT            -> {
                        //ok
                        Snackbar.make(recyclerView, // Parent view
                            getString(R.string.processed_vote), // Message to show
                            Snackbar.LENGTH_SHORT // How long to display the message.
                        ).show()
                    }
                    else                  -> {
                        //error
                        Snackbar.make(recyclerView, // Parent view
                            getString(R.string.there_was_an_error), // Message to show
                            Snackbar.LENGTH_SHORT // How long to display the message.
                        ).show()
                    }
                }
            }
            imgBtnUpDownVoteHasBeenClicked = false
        }

        observerTotalPage = Observer {
            this.totalPage = it.totalPages
        }

        viewModel.getPaginHeaders().observe(viewLifecycleOwner, observerTotalPage)
        viewModel.responseCodeVotoPublicacionSent()
            .observe(viewLifecycleOwner, observerResponseCodeVote)
        viewModel.allVisiblePostsByTag().observe(viewLifecycleOwner, observerPosts)
        viewModel.loadingLiveData().observe(viewLifecycleOwner, observerLoadingData)
    }

    private fun initViews() {
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
    }

    private fun initAdapter() {
        adapter = PostAdapter(object : RecyclerViewClickListener {
            override fun onClick(view: View, position: Int) {
                var valoracion: Boolean = false
                when (view.id) {
                    R.id.arrow_up        -> {
                        imgBtnUpDownVoteHasBeenClicked = true
                        valoracion = true
                    }
                    R.id.arrow_down      -> {
                        imgBtnUpDownVoteHasBeenClicked = true
                        valoracion = false
                    }
                    R.id.lbl_post_title  -> {
                        if (context is HomeActivityCallback) {
                            (context as HomeActivityCallback).onPostClicked(
                                adapter.getItem(position).IdPost)
                        }
                    }
                    R.id.lbl_post_text   -> {
                        if (context is HomeActivityCallback) {
                            (context as HomeActivityCallback).onPostClicked(
                                adapter.getItem(position).IdPost)
                        }
                    }
                    R.id.lbl_author_nick -> {
                        //TODO que hacer cuando el usuario clicka en el nick del autor
                        //                        Toast.makeText(context,
                        //                            "Has clickado en " + list[position].nickAutor,
                        //                            Toast.LENGTH_LONG)
                        //                            .show()
                    }
                }
                if (imgBtnUpDownVoteHasBeenClicked) {
                    val votoPublicacionDTO =
                            VotoPublicacionDTO(idCurrentUser, adapter.getItem(position).IdPost,
                                valoracion, getFormattedCurrentDatetime())
                    viewModel.insertVotoPublicacion(token = token,
                        votoPublicacionDTO = votoPublicacionDTO)
                }
            }
        })
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
    }
}
