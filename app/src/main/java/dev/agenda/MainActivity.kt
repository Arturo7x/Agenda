package dev.agenda

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import dev.agenda.adapters.CustomViewPager
import dev.agenda.adapters.ViewPagerAdapter
import dev.agenda.fragmets.BlankFragment
import dev.agenda.fragmets.ContactFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ContactFragment.OnListFragmentInteractionListener {

    private var frag = ContactFragment()
    private var frag2 = BlankFragment()
    private var viewPager : CustomViewPager? = null
    private val adapter : ViewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_favorites -> {
                viewPager?.currentItem = 0
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_search -> {
                viewPager?.currentItem = 1
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        viewPager  = findViewById(R.id.viewPager1)
        adapter.addFragment(frag, "frag")
        adapter.addFragment(frag2, "frag2")
        viewPager?.adapter = adapter
        if (savedInstanceState == null) {
            viewPager?.currentItem = 0
        }
    }

    override fun onListFragmentInteraction() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
