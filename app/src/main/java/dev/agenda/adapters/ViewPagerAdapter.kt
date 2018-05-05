package dev.agenda.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.SparseArray
import android.view.ViewGroup
import java.lang.ref.WeakReference


class ViewPagerAdapter( manager : FragmentManager) : FragmentPagerAdapter( manager){
    private val instantiatedFragments = SparseArray<WeakReference<Fragment>>()
    private val mFragmentList : ArrayList<Fragment> = ArrayList()
    private val mFragmentTitleList : ArrayList<String> = ArrayList()

    override fun getItem(position: Int): Fragment {
        return this.mFragmentList[position]
    }

    override fun getCount(): Int {
        return this.mFragmentList.size
    }

    fun addFragment( fragment: Fragment, string: String){
        this.mFragmentList.add(fragment)
        this.mFragmentTitleList.add(string)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position)
        instantiatedFragments.put(position, WeakReference<Fragment>(fragment as Fragment?))
        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        instantiatedFragments.remove(position)
        super.destroyItem(container, position, `object`)
    }

    fun getFragment(position: Int): Fragment? {
        val wr = instantiatedFragments.get(position)
        return wr?.get()
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentTitleList[position]
    }
}