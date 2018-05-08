package dev.agenda.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import dev.agenda.R
import dev.agenda.adapters.CustomViewPager
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
    private var adapter: ViewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
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

    // APPBAR LOGIC
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mainmenu, menu)

        val searchItem = menu?.findItem(R.id.navigation_search)
        val searchView = searchItem?.actionView as SearchView

        val addUser = menu.findItem(R.id.add_user)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                contactFragment.fAdapter?.filter(query)
                favoriteFragment.fAdapter?.filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                contactFragment.fAdapter?.filter(newText)
                favoriteFragment.fAdapter?.filter(newText)
                return true
            }
        })

        addUser.setOnMenuItemClickListener {
            val intent = Intent(this, ContactInfoActivity::class.java)
            intent.putExtra("size", contactFragment.contactsSize())
            startActivityForResult(intent, 1)
            true
        }
        return true
    }

    // ACTIVITY LOGIC
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        viewPager = findViewById(R.id.viewPager1)
        adapter.addFragment(contactFragment, "contactFragment")
        adapter.addFragment(favoriteFragment, "favoriteFragment")
        viewPager?.adapter = adapter
        if (savedInstanceState == null) {
            Log.i("Main Activity", "bundle state is null")
            viewPager?.currentItem = 0
        } else {
            Log.i("Main Activity", "Restoring state")
            contactFragment = supportFragmentManager.getFragment(savedInstanceState, "contactFragment") as ContactFragment
            favoriteFragment = supportFragmentManager.getFragment(savedInstanceState, "favoriteFragment") as FavoriteFragment
            viewPager?.currentItem = savedInstanceState.getInt("page")
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        Log.i("Main Activity", "Saving state")
        supportFragmentManager.putFragment(outState, "contactFragment", contactFragment)
        supportFragmentManager.putFragment(outState, "favoriteFragment", favoriteFragment)
        viewPager?.currentItem?.let { outState?.putInt("page", it) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                val c: Contact = data?.extras?.get("contact") as Contact
                Log.i("Main Activity", "Intent got")
                contactFragment.insertContact(c)
            }
        } else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                val c: Contact = data?.extras?.get("contact") as Contact
                Log.i("Main Activity", "Item update")
                if (!c.favorite)
                    contactFragment.update(c)
                else {
                    contactFragment.update(c)
                    favoriteFragment.update(c)
                }
            }
        }

    }

    // FRAGMENTS LISTENERS
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

    override fun showContact(contact: Contact) {
        val intent = Intent(this, ContactInfoActivity::class.java)
        intent.action = Intent.ACTION_SEND
        intent.putExtra("contact", contact)
        startActivityForResult(intent, 2)
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
