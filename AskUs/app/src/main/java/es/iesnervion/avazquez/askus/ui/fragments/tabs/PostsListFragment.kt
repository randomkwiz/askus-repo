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
import es.iesnervion.avazquez.askus.DTOs.PublicacionDTO
import es.iesnervion.avazquez.askus.R
import es.iesnervion.avazquez.askus.adapters.PostAdapter
import es.iesnervion.avazquez.askus.ui.fragments.tabs.viewmodel.PostViewModel
import es.iesnervion.avazquez.askus.utils.AppConstants
import kotlinx.android.synthetic.main.fragment_posts.*

/**
 * A simple [Fragment] subclass.
 */
class PostsListFragment : Fragment() {
    lateinit var viewModel: PostViewModel
    lateinit var sharedPreference: SharedPreferences
    lateinit var adapter: PostAdapter
    lateinit var observerPosts: Observer<List<PublicacionDTO>>
    lateinit var observerLoadingData: Observer<Boolean>
    lateinit var filterType: String

    companion object {
        fun newInstance(txt: String): PostsListFragment {
            var myFragment = PostsListFragment()
            val args = Bundle()
            args.putString("txt", txt)
            myFragment.setArguments(args)
            return myFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        filterType = arguments?.getString("txt") ?: ""
        return inflater.inflate(R.layout.fragment_posts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreference =
            activity!!.getSharedPreferences(AppConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        viewModel = ViewModelProviders.of(activity!!).get(PostViewModel::class.java)
        initViews()
        initObservers()
        sharedPreference.getString("token", "")?.let {
            viewModel.loadPosts(it)
        }
    }

    private fun initObservers() {
        observerPosts = Observer { publicacionDTO ->
            adapter = context?.let { PostAdapter(publicacionDTO, it) }!!
            recyclerView.adapter = adapter
        }
        observerLoadingData = Observer { loading ->
            if (loading) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        }
        when (filterType) {
            "ALL" -> {
                viewModel.allNonDeletedPostedPosts().observe(viewLifecycleOwner, observerPosts)
            }
            "PUBLICS" -> {
                viewModel.allNonDeletedPublicPostedPosts()
                    .observe(viewLifecycleOwner, observerPosts)
            }
        }
        viewModel.loadingLiveData().observe(viewLifecycleOwner, observerLoadingData)
    }

    private fun initViews() {
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
    }
}
