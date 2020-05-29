package es.iesnervion.avazquez.askus.ui.fragments.profileFragment.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import es.iesnervion.avazquez.askus.DTOs.ProfileDTO
import es.iesnervion.avazquez.askus.R
import es.iesnervion.avazquez.askus.adapters.LogroAdapter
import es.iesnervion.avazquez.askus.adapters.PostAdapter
import es.iesnervion.avazquez.askus.interfaces.RecyclerViewClickListener
import es.iesnervion.avazquez.askus.ui.fragments.profileFragment.viewmodel.ProfileViewModel
import es.iesnervion.avazquez.askus.utils.AppConstants
import es.iesnervion.avazquez.askus.utils.PaginationScrollListener.Companion.PAGE_SIZE
import es.iesnervion.avazquez.askus.utils.PaginationScrollListener.Companion.PAGE_START
import kotlinx.android.synthetic.main.fragment_profile.*
import round
import setVisibilityToGone
import setVisibilityToVisible
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    companion object {
        fun newInstance(idUserToLoad: Int): ProfileFragment {
            val myFragment = ProfileFragment()
            val args = Bundle()
            args.putInt("idUserToLoad", idUserToLoad)
            myFragment.arguments = args
            return myFragment
        }
    }

    lateinit var viewModel: ProfileViewModel
    lateinit var sharedPreference: SharedPreferences
    lateinit var areValuesReadyObserver: Observer<Boolean>
    lateinit var dataFromUserObserver: Observer<ProfileDTO>
    lateinit var loadingObserver: Observer<Boolean>
    lateinit var token: String
    lateinit var logroAdapter: LogroAdapter
    lateinit var postAdapter: PostAdapter
    var showPosts = false
    var idUserToLoad = 0
    var pageNumber = PAGE_START
    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        viewModel.loadAllLogros()
        sharedPreference =
                activity!!.getSharedPreferences(AppConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        token = sharedPreference.getString("token", "").toString()
        idUserToLoad = arguments?.getInt("idUserToLoad") ?: 0
        idUserToLoad.let {
            viewModel.loadUserProfile(it)
        }
        idUserToLoad.let { viewModel.loadLogrosFromUser(token, it) }
        viewModel.loadAllLogros()
        viewModel.loadMyPosts(token = token, pageSize = PAGE_SIZE, pageNumber = pageNumber,
            idUsuarioLogeado = idUserToLoad)
        progress_bar.setVisibilityToVisible()
        profile__recyclerview.setVisibilityToGone()
        postAdapter = PostAdapter(listener = object : RecyclerViewClickListener {
            override fun onClick(view: View, position: Int) {
                //no-op
            }
        })
        initListeners()
        initRecyclerView()
        initObservers()
    }

    private fun initRecyclerView() {
        if (showPosts) {
            initRecyclerViewForPosts()
        } else {
            initRecyclerViewForLogros()
        }
    }

    private fun initListeners() {
        swipeRefreshLayout.setOnRefreshListener(this)
        profile__btn_logros.setOnClickListener(this)
        profile__btn_posts.setOnClickListener(this)
    }

    private fun initRecyclerViewForLogros() {
        profile__recyclerview.setHasFixedSize(true)
        val layoutManager = GridLayoutManager(context, 3)
        profile__recyclerview.layoutManager = layoutManager
    }

    private fun initRecyclerViewForPosts() {
        profile__recyclerview.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        profile__recyclerview.layoutManager = layoutManager
    }

    private fun initObservers() {
        areValuesReadyObserver = Observer {
            if (it) {
                if (!showPosts) {
                    logroAdapter = LogroAdapter(listaLogrosOriginal = viewModel.allLogros,
                        listaLogrosConseguidos = viewModel.idLogrosFromUser)
                    profile__recyclerview.adapter = logroAdapter
                }
                postAdapter.addItems(viewModel.myPosts.toMutableList())
            }
        }
        viewModel.getAreValuesReady().observe(viewLifecycleOwner, areValuesReadyObserver)

        dataFromUserObserver = Observer {
            if (it.idUsuario == idUserToLoad) {
                lbl_profile_nickname.text = it.nickname
                lbl_written_comments.text = it.cantidadComentariosEscritos.toString()
                lbl_sent_posts.text = it.cantidadPostsEnviados.toString()
                lbl_posted_posts.text = it.cantidadPostsPublicados.toString()
                user_mark.text = it.notaMediaEnPosts.round()
                //user_mark.text = it.notaMediaEnPosts.toString()
                last_login_date.text = formatDate(it?.fechaUltimoAcceso.toString())
                register_date.text = formatDate(it?.fechaRegistro.toString())
            }
        }
        viewModel.getUserProfile().observe(viewLifecycleOwner, dataFromUserObserver)

        loadingObserver = Observer {
            if (it) {
                if (swipeRefreshLayout.isRefreshing) {
                    progress_bar.setVisibilityToGone()
                    swipeRefreshLayout.setVisibilityToVisible()
                    profile__recyclerview.setVisibilityToVisible()
                } else {
                    progress_bar.setVisibilityToVisible()
                    swipeRefreshLayout.setVisibilityToGone()
                    profile__recyclerview.setVisibilityToGone()
                }
            } else {
                if (swipeRefreshLayout.isRefreshing) {
                    swipeRefreshLayout.isRefreshing = false
                    progress_bar.setVisibilityToGone()
                    swipeRefreshLayout.setVisibilityToVisible()
                    profile__recyclerview.setVisibilityToVisible()
                } else {
                    progress_bar.setVisibilityToGone()
                    swipeRefreshLayout.setVisibilityToVisible()
                    profile__recyclerview.setVisibilityToVisible()
                }
            }
        }
        viewModel.loadingLiveData().observe(viewLifecycleOwner, loadingObserver)
    }

    override fun onRefresh() {
        idUserToLoad.let {
            viewModel.loadUserProfile(it)
        }
        idUserToLoad.let { viewModel.loadLogrosFromUser(token, it) }
    }

    private fun formatDate(date: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
        val outputFormat = SimpleDateFormat("dd-MM-yyyy")
        val date: Date = inputFormat.parse(date)
        return outputFormat.format(date)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.profile__btn_logros -> {
                showPosts = false
            }
            R.id.profile__btn_posts  -> {
                showPosts = true
            }
        }
        initRecyclerView()
        setAdapter()
    }

    private fun setAdapter() {
        if (showPosts) {
            profile__recyclerview.adapter = postAdapter
        } else {
            profile__recyclerview.adapter = logroAdapter
        }
    }
}
