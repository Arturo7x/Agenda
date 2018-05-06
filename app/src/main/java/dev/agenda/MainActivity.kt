package dev.agenda

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import dev.agenda.adapters.CustomViewPager
import dev.agenda.adapters.MyContactRecyclerViewAdapter
import dev.agenda.adapters.ViewPagerAdapter
import dev.agenda.fragmets.ContactFragment
import dev.agenda.fragmets.FavoriteFragment
import dev.agenda.models.Contact
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ContactFragment.OnListFragmentInteractionListener,
        FavoriteFragment.OnListFragmentFragmentInteractionListener {

    private var contactFragment = ContactFragment()
    private var favoriteFragment = FavoriteFragment()
    private var viewPager: CustomViewPager? = null
    private val adapter: ViewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
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
        viewPager = findViewById(R.id.viewPager1)
        adapter.addFragment(contactFragment, "contactFragment")
        adapter.addFragment(favoriteFragment, "favoriteFragment")
        viewPager?.adapter = adapter
        if (savedInstanceState == null) {
            viewPager?.currentItem = 0
        }else{
            viewPager?.currentItem = savedInstanceState.getInt("page")
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        viewPager?.currentItem?.let { outState?.putInt("page", it) }
    }

    override fun onListFragmentInteraction(contact: Contact, pos: Int, v: View) {
        Toast.makeText(applicationContext, " $pos ,${contact.name} added to favorites", Toast.LENGTH_SHORT).show()
        if (!contact.favorite) {
            (v as ImageView).setImageResource(R.drawable.favorite)
            addContact(contact, pos)
        } else {
            (v as ImageView).setImageResource(R.drawable.favorite_false)
            removeContact(contact)
        }
    }

    override fun onFavFragmentInterActionListener(contact: Contact, pos: Int, v: View) {
        contactFragment.unFav(contact)
        removeContact(contact)
    }

    private fun addContact(contact: Contact, pos: Int) {
        contact.favorite = true
        favoriteFragment.addFavorite(contact, pos)
    }

    private fun removeContact(contact: Contact) {
        contact.favorite = false
        favoriteFragment.removeFavorite(contact)
    }
}
