package es.iesnervion.avazquez.askus.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import es.iesnervion.avazquez.askus.R
import es.iesnervion.avazquez.askus.adapters.TabAdapter
import es.iesnervion.avazquez.askus.interfaces.HomeActivityCallback
import es.iesnervion.avazquez.askus.ui.fragments.tabs.PostsListFragment
import es.iesnervion.avazquez.askus.ui.fragments.tabs.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment(), View.OnClickListener {
    lateinit var viewModel : MainViewModel
    lateinit var sharedPreference: SharedPreferences
    var idTag = 0
    companion object {
        fun newInstance(idTag: Int): HomeFragment {
            val myFragment = HomeFragment()
            val args = Bundle()
            args.putInt("idTag", idTag)
            myFragment.arguments = args
            return myFragment
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        idTag = arguments?.getInt("idTag") ?: 0
        //        viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
        //        sharedPreference =
        //            activity!!.getSharedPreferences(AppConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        //        //TODO cambiar esto
        //        sharedPreference.getString("token", "")
        //            ?.let { viewModel.loadPostsByTag(it, idTag, pageSize = 10, pageNumber = PAGE_START) }
        fab_add_post.setOnClickListener(this)
        initViewPager()
    }

    private fun initViewPager() {
        val adapter = TabAdapter(childFragmentManager)
        adapter.addFragment(PostsListFragment.newInstance("ALL", idTag),
            resources.getString(R.string.allPosts))
        adapter.addFragment(PostsListFragment.newInstance("TOP_RATED", idTag),
            resources.getString(R.string.top_rated))
        adapter.addFragment(PostsListFragment.newInstance("TOP_COMMENTED", idTag),
            resources.getString(R.string.top_commented))
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun onClick(v: View) {
        if (context is HomeActivityCallback)
            (context as HomeActivityCallback).onAddPostClicked(arguments?.getInt("idTag") ?: 0)
    }
}
