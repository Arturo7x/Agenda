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
import dev.agenda.adapters.MyContactRecyclerViewAdapter
import dev.agenda.R

import dev.agenda.models.Contact

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [FavoriteFragment.OnListFragmentFragmentInteractionListener] interface.
 */
class FavoriteFragment : Fragment() {

    private var contacts: ArrayList<Contact>? = null
    private var columnCount = 1
    private var fAdapter: MyContactRecyclerViewAdapter? = null //fragment adapter
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
        val view = inflater.inflate(R.layout.fragment_contact_list, container, false)
        return view
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
                Log.i("F Size of contacts:", " {${contacts?.size}}")
                adapter = MyContactRecyclerViewAdapter(contacts,listener as OnListFragmentFragmentInteractionListener)
                fAdapter = this.adapter as MyContactRecyclerViewAdapter?
            }
        }
        Log.i("Favorites Call from", "onCreateView")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i("Favorites Call from", "onSaveInstanceState")
        outState.putParcelableArrayList(key1, contacts)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.i("Favorites Call from", "onViewStateRestored")
        if (savedInstanceState != null) {
            contacts = savedInstanceState.getParcelableArrayList(key1)
            contacts?.size?.let { fAdapter?.notifyItemRangeChanged(0, it) }
            fAdapter?.notifyDataSetChanged()
        }
    }

    interface OnListFragmentFragmentInteractionListener : ContactFragment.OnListFragmentInteractionListener {
        fun onFavFragmentInterActionListener(contact:Contact, pos: Int, v : View)
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

    fun addFavorite( contact: Contact, pos: Int){
        this.contacts?.add(contact)
        contacts?.indexOf(contact)?.let { fAdapter?.notifyItemInserted(it) }
        contacts?.size?.let { fAdapter?.notifyItemRangeChanged(pos, it) }
    }

    fun removeFavorite( contact: Contact){
        val pos = contacts?.indexOf(contact)
        this.contacts?.remove(contact)
        pos?.let { fAdapter?.notifyItemRemoved(it) }
        contacts?.size?.let { pos?.let { it1 -> fAdapter?.notifyItemRangeChanged(it1, it) } }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}

