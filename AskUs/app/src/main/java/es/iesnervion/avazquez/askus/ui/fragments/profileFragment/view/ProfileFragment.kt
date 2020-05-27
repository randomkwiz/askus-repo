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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import es.iesnervion.avazquez.askus.DTOs.ProfileDTO
import es.iesnervion.avazquez.askus.R
import es.iesnervion.avazquez.askus.adapters.LogroAdapter
import es.iesnervion.avazquez.askus.ui.fragments.profileFragment.viewmodel.ProfileViewModel
import es.iesnervion.avazquez.askus.utils.AppConstants
import kotlinx.android.synthetic.main.fragment_profile.*
import round
import setVisibilityToGone
import setVisibilityToVisible
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
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
    lateinit var adapter: LogroAdapter
    var idUserToLoad = 0
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
        progress_bar.setVisibilityToVisible()
        recyclerview_logros.setVisibilityToGone()
        swipeRefreshLayout.setOnRefreshListener(this)
        initRecyclerView()
        initObservers()
    }

    private fun initRecyclerView() {
        recyclerview_logros.setHasFixedSize(true)
        val layoutManager = GridLayoutManager(context, 3)
        recyclerview_logros.layoutManager = layoutManager
    }

    private fun initObservers() {
        areValuesReadyObserver = Observer {
            if (it) {
                adapter = LogroAdapter(listaLogrosOriginal = viewModel.allLogros,
                    listaLogrosConseguidos = viewModel.idLogrosFromUser)
                recyclerview_logros.adapter = adapter
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
                    recyclerview_logros.setVisibilityToVisible()
                } else {
                    progress_bar.setVisibilityToVisible()
                    swipeRefreshLayout.setVisibilityToGone()
                    recyclerview_logros.setVisibilityToGone()
                }
            } else {
                if (swipeRefreshLayout.isRefreshing) {
                    swipeRefreshLayout.isRefreshing = false
                    progress_bar.setVisibilityToGone()
                    swipeRefreshLayout.setVisibilityToVisible()
                    recyclerview_logros.setVisibilityToVisible()
                } else {
                    progress_bar.setVisibilityToGone()
                    swipeRefreshLayout.setVisibilityToVisible()
                    recyclerview_logros.setVisibilityToVisible()
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
}
