package es.iesnervion.avazquez.askus.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import es.iesnervion.avazquez.askus.R
import es.iesnervion.avazquez.askus.adapters.TabAdapter
import es.iesnervion.avazquez.askus.ui.fragments.tabs.PostsListFragment
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {
    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager()
    }

    private fun initViewPager() {
        val adapter = TabAdapter(childFragmentManager)
        adapter.addFragment(PostsListFragment.newInstance("ALL"),
            resources.getString(R.string.allPosts))
        adapter.addFragment(PostsListFragment.newInstance("PUBLICS"),
            resources.getString(R.string.public_posts))
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
    }
}
