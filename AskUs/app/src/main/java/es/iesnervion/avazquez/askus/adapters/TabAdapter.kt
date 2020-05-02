package es.iesnervion.avazquez.askus.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import java.util.*

class TabAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    private val mFragmentList: MutableList<Fragment> =
        ArrayList()
    private val mFragmentTitleList: MutableList<String> =
        ArrayList()
    fun addFragment(fragment: Fragment,
        title: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    override fun getPageTitle(position: Int): CharSequence {
        return mFragmentTitleList[position]
    }

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }
}