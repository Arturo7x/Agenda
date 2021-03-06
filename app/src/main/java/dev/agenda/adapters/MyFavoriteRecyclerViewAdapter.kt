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
    private var contactsCopy: ArrayList<Contact>? = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_contact, parent, false)
        if (contactsCopy?.size == 0)
            contactsCopy?.addAll(this.mValues!!)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues!![position]
        holder.name.text = item.name
        holder.phone.text = item.phone

        if (!(item.imageSrc?.contains("@drawable/user_hd", false))!!) {
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


    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun filter(s: String) {
        var text = s
        mValues?.clear()
        if (text.isEmpty()) {
            mValues?.addAll(this.contactsCopy!!)
        } else {
            text = text.toLowerCase()
            for (item in this.contactsCopy!!) {
                if (item.name?.toLowerCase()?.contains(text)!! || item.phone?.toLowerCase()?.contains(text)!!) {
                    mValues?.add(item)
                }
            }
        }
        notifyDataSetChanged()
    }

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
        setHasStableIds(true)
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
