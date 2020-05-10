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
import com.google.android.material.snackbar.Snackbar
import es.iesnervion.avazquez.askus.DTOs.PostCompletoParaMostrarDTO
import es.iesnervion.avazquez.askus.DTOs.VotoPublicacionDTO
import es.iesnervion.avazquez.askus.R
import es.iesnervion.avazquez.askus.adapters.PostAdapter
import es.iesnervion.avazquez.askus.interfaces.RecyclerViewClickListener
import es.iesnervion.avazquez.askus.ui.fragments.tabs.viewmodel.MainViewModel
import es.iesnervion.avazquez.askus.utils.AppConstants
import es.iesnervion.avazquez.askus.utils.AppConstants.INTERNAL_SERVER_ERROR
import es.iesnervion.avazquez.askus.utils.AppConstants.NO_CONTENT
import es.iesnervion.avazquez.askus.utils.UtilClass.Companion.getFormattedCurrentDatetime
import kotlinx.android.synthetic.main.fragment_posts.*
import setVisibilityToGone
import setVisibilityToVisible

/**
 * A simple [Fragment] subclass.
 */
class PostsListFragment : Fragment() {
    lateinit var viewModel: MainViewModel
    lateinit var adapter: PostAdapter
    lateinit var observerPosts: Observer<List<PostCompletoParaMostrarDTO>>
    lateinit var observerLoadingData: Observer<Boolean>
    lateinit var observerResponseCodeVote: Observer<Int>
    lateinit var filterType: String
    lateinit var sharedPreference: SharedPreferences
    lateinit var token: String
    var imgBtnUpDownVoteHasBeenClicked = false
    var idCurrentUser = 0

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
        viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
        sharedPreference =
            activity!!.getSharedPreferences(AppConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        token = sharedPreference.getString("token", "").toString()
        idCurrentUser = sharedPreference.getInt("user_id", 0)
        initViews()
        initObservers()
        setListeners()
        swipeRefreshLayout.setOnRefreshListener {
            // Esto se ejecuta cada vez que se realiza el gesto
            sharedPreference.getString("token", "")?.let {
                arguments?.getInt("idTag")?.let { it1 -> viewModel.loadPostsByTag(it, it1) }
            }
        }
        initContent()
    }

    private fun setListeners() {
    }

    private fun initContent() {
        val post = viewModel.allVisiblePostsByTag().value
        if (!post.isNullOrEmpty()) {
            when (filterType) {
                "ALL" -> {
                    setAdapter(post)
                }
                "TOP_RATED" -> {
                    setAdapter(post.sortedByDescending { it.cantidadVotosPositivos })
                }
                "TOP_COMMENTED" -> {
                    setAdapter(post.sortedByDescending { it.cantidadComentarios })
                }
            }
        }
    }

    private fun initObservers() {
        observerPosts = Observer { post ->
            if (post.isNotEmpty()) {
                when (filterType) {
                    "ALL" -> {
                        setAdapter(post)
                    }
                    "TOP_RATED" -> {
                        setAdapter(post.sortedByDescending { it.cantidadVotosPositivos })
                    }
                    "TOP_COMMENTED" -> {
                        setAdapter(post.sortedByDescending { it.cantidadComentarios })
                    }
                }
            }
        }
        observerLoadingData = Observer { loading ->
            if (loading) {
                if (!swipeRefreshLayout.isRefreshing) {
                    progressBar.setVisibilityToVisible()
                    recyclerView.setVisibilityToGone()
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
                        Snackbar.make(
                            recyclerView, // Parent view
                            getString(R.string.you_cant_vote_twice), // Message to show
                            Snackbar.LENGTH_SHORT // How long to display the message.
                        ).show()
                    }
                    NO_CONTENT -> {
                        //todo ok
                        Snackbar.make(
                            recyclerView, // Parent view
                            getString(R.string.processed_vote), // Message to show
                            Snackbar.LENGTH_SHORT // How long to display the message.
                        ).show()
                    }
                    else -> {
                        //error
                        Snackbar.make(
                            recyclerView, // Parent view
                            getString(R.string.there_was_an_error), // Message to show
                            Snackbar.LENGTH_SHORT // How long to display the message.
                        ).show()
                    }
                }
            }
            imgBtnUpDownVoteHasBeenClicked = false
        }

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

    private fun setAdapter(list: List<PostCompletoParaMostrarDTO>) {
        adapter = PostAdapter(list, object : RecyclerViewClickListener {
            override fun onClick(view: View, position: Int) {
                imgBtnUpDownVoteHasBeenClicked = true
                val valoracion: Boolean = when (view.id) {
                    R.id.arrow_up -> {
                        true
                    }
                    else -> {
                        false
                    }
                }
                val votoPublicacionDTO =
                    VotoPublicacionDTO(idCurrentUser, list[position].IdPost, valoracion,
                        getFormattedCurrentDatetime()
                    )
                viewModel.insertVotoPublicacion(token = token,
                    votoPublicacionDTO = votoPublicacionDTO)
            }
        })
        recyclerView.adapter = adapter
    }
}
