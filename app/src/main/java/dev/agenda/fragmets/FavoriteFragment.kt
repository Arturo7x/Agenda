package dev.agenda.fragmets

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.agenda.R
import dev.agenda.adapters.MyFavoriteRecyclerViewAdapter

import dev.agenda.models.Contact

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [FavoriteFragment.OnListFragmentFragmentInteractionListener] interface.
 */
class FavoriteFragment : Fragment() {

    private var contacts: ArrayList<Contact>? = null
    private var columnCount = 1
    var fAdapter: MyFavoriteRecyclerViewAdapter? = null //fragment adapter
    private var listener: OnListFragmentFragmentInteractionListener? = null
    private val key1: String = "favorites"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
        Log.i("Favorites Call from", "onCreate")
        contacts = if (savedInstanceState != null)
            savedInstanceState.getParcelableArrayList(key1)
        else
            ArrayList()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_contact_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                Log.i("Favorites Call from", "onCreateView bundle != null")
                if (savedInstanceState != null) {
                    contacts = savedInstanceState.getParcelableArrayList(key1)
                    contacts?.size?.let { fAdapter?.notifyItemRangeChanged(0, it) }
                    fAdapter?.notifyDataSetChanged()
                }
                Log.i("Fav Size of contacts:", " {${contacts?.size}}")
                adapter = listener?.let { MyFavoriteRecyclerViewAdapter(contacts, it) }
                fAdapter = this.adapter as MyFavoriteRecyclerViewAdapter?
            }
        }
        Log.i("Favorites Call from", "onCreateView")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i("Favorites Call from", "onSaveInstanceState")
        outState.putParcelableArrayList(key1, contacts)
    }

    interface OnListFragmentFragmentInteractionListener {
        fun onFavFragmentInterActionListener(contact: Contact, pos: Int, v: View)
        fun showContact(contact: Contact)
    }

    companion object {
        const val ARG_COLUMN_COUNT = "column-count"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    fun update(contact: Contact) {
        val i: Int? = contacts?.indexOf(contacts!!.find { it.id == contact.id })
        Log.i("Contact fragment", "$i")
        i?.let { contacts?.set(it, contact) }
        i?.let { fAdapter?.notifyItemChanged(it) }
    }

    fun addFavorite(contact: Contact, pos: Int) {
        this.contacts?.add(contact)
        contacts?.indexOf(contact)?.let { fAdapter?.notifyItemInserted(it) }
        contacts?.size?.let { fAdapter?.notifyItemRangeChanged(pos, it) }
        fAdapter?.notifyDataSetChanged()
    }

    fun removeFavorite(contact: Contact) {
        val pos = contacts?.indexOf(contact)
        this.contacts?.remove(contact)
        pos?.let { fAdapter?.notifyItemRemoved(it) }
        contacts?.size?.let { pos?.let { it1 -> fAdapter?.notifyItemRangeChanged(it1, it) } }
        fAdapter?.notifyDataSetChanged()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}

