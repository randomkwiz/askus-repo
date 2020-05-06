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
import es.iesnervion.avazquez.askus.DTOs.PostCompletoParaMostrarDTO
import es.iesnervion.avazquez.askus.DTOs.PublicacionDTO
import es.iesnervion.avazquez.askus.R
import es.iesnervion.avazquez.askus.adapters.PostAdapter
import es.iesnervion.avazquez.askus.ui.fragments.tabs.viewmodel.MainViewModel
import es.iesnervion.avazquez.askus.utils.AppConstants
import kotlinx.android.synthetic.main.fragment_posts.*

/**
 * A simple [Fragment] subclass.
 */
class PostsListFragment : Fragment() {
    lateinit var viewModel: MainViewModel
    lateinit var sharedPreference: SharedPreferences
    lateinit var adapter: PostAdapter
    lateinit var observerPosts: Observer<List<PostCompletoParaMostrarDTO>>
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
        viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
        initViews()
        initObservers()

    }

    private fun initObservers() {
        observerPosts = Observer { post ->
            if(post.isNotEmpty()){
                adapter = context?.let { PostAdapter(post, it) }!!
                recyclerView.adapter = adapter
            }

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
                //TODO("esto no funciona, no cambia cuando seleccionas otro tag")
                viewModel.allVisiblePostsByTag().observe(viewLifecycleOwner, observerPosts)
            }
            "TOP_RATED" -> {
//                viewModel.allNonDeletedPublicPostedPosts()
//                    .observe(viewLifecycleOwner, observerPosts)
            }
            "TOP_COMMENTED" -> {
                //                viewModel.allNonDeletedPublicPostedPosts()
                //                    .observe(viewLifecycleOwner, observerPosts)
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
