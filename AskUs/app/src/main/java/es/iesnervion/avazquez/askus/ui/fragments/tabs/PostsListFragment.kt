package es.iesnervion.avazquez.askus.ui.fragments.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import es.iesnervion.avazquez.askus.DTOs.PostCompletoParaMostrarDTO
import es.iesnervion.avazquez.askus.R
import es.iesnervion.avazquez.askus.adapters.PostAdapter
import es.iesnervion.avazquez.askus.ui.fragments.tabs.viewmodel.MainViewModel
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
    lateinit var filterType: String

    companion object {
        fun newInstance(txt: String): PostsListFragment {
            val myFragment = PostsListFragment()
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
                progressBar.setVisibilityToVisible()
                recyclerView.setVisibilityToGone()
            } else {
                progressBar.setVisibilityToGone()
                recyclerView.setVisibilityToVisible()
            }
        }
        viewModel.allVisiblePostsByTag().observe(viewLifecycleOwner, observerPosts)
        viewModel.loadingLiveData().observe(viewLifecycleOwner, observerLoadingData)
    }

    private fun initViews() {
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
    }

    private fun setAdapter(list: List<PostCompletoParaMostrarDTO>) {
        adapter = context?.let {
            PostAdapter(list, it)
        }!!
        recyclerView.adapter = adapter
    }
}
