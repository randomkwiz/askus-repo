package es.iesnervion.avazquez.askus.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import es.iesnervion.avazquez.askus.R
import es.iesnervion.avazquez.askus.adapters.TabAdapter
import es.iesnervion.avazquez.askus.ui.fragments.tabs.PostsListFragment
import es.iesnervion.avazquez.askus.ui.fragments.tabs.viewmodel.MainViewModel
import es.iesnervion.avazquez.askus.utils.AppConstants
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {
    lateinit var viewModel : MainViewModel
    lateinit var sharedPreference: SharedPreferences
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
        viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
        sharedPreference =
            activity!!.getSharedPreferences(AppConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        sharedPreference.getString("token", "")?.let {
            arguments?.getInt("idTag")?.let { it1 -> viewModel.loadPostsByTag(it, it1) }
        }
        initViewPager()
    }

    private fun initViewPager() {
        val adapter = TabAdapter(childFragmentManager)
        adapter.addFragment(PostsListFragment.newInstance("ALL"),
            resources.getString(R.string.allPosts))
        adapter.addFragment(PostsListFragment.newInstance("TOP_RATED"),
            resources.getString(R.string.top_rated))
        adapter.addFragment(PostsListFragment.newInstance("TOP_COMMENTED"),
            resources.getString(R.string.top_commented))
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
    }
}
