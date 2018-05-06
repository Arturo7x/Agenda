package dev.agenda.adapters

import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import dev.agenda.R


import dev.agenda.fragmets.ContactFragment.OnListFragmentInteractionListener
import dev.agenda.fragmets.FavoriteFragment
import dev.agenda.models.Contact

import kotlinx.android.synthetic.main.fragment_contact.view.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class MyContactRecyclerViewAdapter
    : RecyclerView.Adapter<MyContactRecyclerViewAdapter.ViewHolder> {

    private var mValues: ArrayList<Contact>?
    private var mListener: OnListFragmentInteractionListener? = null
    private var mOnClickListener: View.OnClickListener? = null
    private var mOnClickListenerIntent: View.OnClickListener? = null

    constructor(mValues: ArrayList<Contact>?, mListener: OnListFragmentInteractionListener?) : super() {
        this.mValues = mValues
        this.mListener = mListener
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Contact
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mValues?.indexOf(item)?.let { mListener?.onListFragmentInteraction(item, it, v) }
        }
        mOnClickListenerIntent = View.OnClickListener { v ->
            val item = v.tag as Contact
            mListener?.showContact(item)
        }
    }

    constructor(mValues: ArrayList<Contact>?, mListener: FavoriteFragment.OnListFragmentFragmentInteractionListener) : super() {
        this.mValues = mValues
        this.mListener = mListener
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Contact
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mValues?.indexOf(item)?.let { mListener.onFavFragmentInterActionListener(item, it, v) }
        }
        mOnClickListenerIntent = View.OnClickListener { v ->
            val item = v.tag as Contact
            mListener.showContact(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_contact, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues!![position]
        holder.name.text = item.name
        holder.phone.text = item.phone

        if (!(item.imageSrc?.contains("drawable", false))!!) {
            holder.image.setImageBitmap(BitmapFactory.decodeFile(item.imageSrc))
        }

        holder.star.setImageResource(if (item.favorite) {
            R.drawable.favorite
        } else {
            R.drawable.favorite_false
        })

        with(holder.mView) {
            Log.i("Test", "Clicked")
            tag = item
            setOnClickListener(mOnClickListenerIntent)
        }

        if (mListener is FavoriteFragment.OnListFragmentFragmentInteractionListener) {
            with(holder.star) {
                tag = item
                setOnClickListener(mOnClickListener)
            }
        } else {
            with(holder.star) {
                tag = item
                setOnClickListener(mOnClickListener)
            }
        }
    }

    override fun getItemCount(): Int = mValues!!.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val name: TextView = mView.contact_name
        val phone: TextView = mView.contact_phone
        val image: ImageView = mView.contact_img
        val star: ImageView = mView.favorite

        override fun toString(): String {
            return super.toString() + " '" + name.text + "'"
        }
    }
}
