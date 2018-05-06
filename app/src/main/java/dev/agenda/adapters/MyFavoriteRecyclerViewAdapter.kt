package dev.agenda.adapters

import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import dev.agenda.R


import dev.agenda.fragmets.FavoriteFragment
import dev.agenda.models.Contact

import kotlinx.android.synthetic.main.fragment_contact.view.*

class MyFavoriteRecyclerViewAdapter
(private var mValues: ArrayList<Contact>?, private var mListener: FavoriteFragment.OnListFragmentFragmentInteractionListener)
    : RecyclerView.Adapter<MyFavoriteRecyclerViewAdapter.ViewHolder>() {

    private var mOnClickListener: View.OnClickListener? = null
    private var mOnClickListenerIntent: View.OnClickListener? = null

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

        with(holder.star) {
            tag = item
            setOnClickListener(mOnClickListener)
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

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Contact
            mValues?.indexOf(item)?.let { mListener.onFavFragmentInterActionListener(item, it, v) }
        }
        mOnClickListenerIntent = View.OnClickListener { v ->
            val item = v.tag as Contact
            mListener.showContact(item)
        }
    }
}
