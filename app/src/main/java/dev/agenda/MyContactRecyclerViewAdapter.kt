package dev.agenda

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView


import dev.agenda.ContactFragment.OnListFragmentInteractionListener
import dev.agenda.models.Contact

import kotlinx.android.synthetic.main.fragment_contact.view.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class MyContactRecyclerViewAdapter(
        private var mValues: ArrayList<Contact>?,
        private val mListener: OnListFragmentInteractionListener?)
    : RecyclerView.Adapter<MyContactRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Contact
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction()
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
        if( !(item.imageSrc?.contains("drawable",false))!!){
            holder.image.setImageBitmap(BitmapFactory.decodeFile(item.imageSrc))
        }
        holder.star.setImageResource( if(item.favorite){
            R.drawable.favorite
        }else{
            R.drawable.favorite_false
        })
        holder.star.setOnClickListener {
            if(!item.favorite){
                holder.star.setImageResource(R.drawable.favorite)
                item.favorite = !item.favorite
            }else{
                holder.star.setImageResource(R.drawable.favorite_false)
                item.favorite = !item.favorite
            }
        }
        /*with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }*/
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
