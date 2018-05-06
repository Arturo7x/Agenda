package dev.agenda.fragmets

import android.Manifest
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.Fragment
import android.support.v4.content.PermissionChecker.*
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
import java.io.File
import java.io.FileOutputStream
import java.lang.ref.WeakReference


@Suppress("IMPLICIT_CAST_TO_ANY")
/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [ContactFragment.OnListFragmentInteractionListener] interface.
 */
class ContactFragment : Fragment() {

    private var contacts: ArrayList<Contact>? = null
    private var columnCount = 1
    private var fAdapter: MyContactRecyclerViewAdapter? = null //fragment adapter
    private var listener: OnListFragmentInteractionListener? = null
    private var contactsLoader: LoadContacts? = null
    private val key1: String = "contacts"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
        Log.i("Call from", "onCreate")
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
                Log.i("Size of contacts:", " {${contacts?.size}}")
                adapter = MyContactRecyclerViewAdapter(contacts, listener)
                fAdapter = this.adapter as MyContactRecyclerViewAdapter?
            }
        }
        Log.i("Call from", "onCreateView")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i("Call from", "onSaveInstanceState")
        outState.putParcelableArrayList(key1, contacts)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.i("Call from", "onViewStateRestored")
        if (savedInstanceState != null)
            contacts = savedInstanceState.getParcelableArrayList(key1)
        else
            loadContacts()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(contact: Contact, pos: Int, v: View)
        fun showContact(contact: Contact)
    }

    companion object {
        const val ARG_COLUMN_COUNT = "column-count"
        const val PERMISSIONS_REQUEST_READ_CONTACTS = 100
    }

    private fun loadContacts() =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                            this.requireContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                Log.i("Call from", "loadContacts if")
                requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS),
                        PERMISSIONS_REQUEST_READ_CONTACTS)
                //callback onRequestPermissionsResult
            } else {
                Log.i("Call from", "loadContacts else")
                contactsLoader = LoadContacts(this.activity?.baseContext!!, this.activity!!, this.contacts, this.fAdapter)
                contactsLoader?.execute()
            }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        Log.i("Call from", "OnRequestPermissionsResult")
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadContacts()
            } else {
                //  toast("Permission must be granted in order to display contacts information")
            }
        }
    }

    fun unFav(contact: Contact) {
        contacts?.indexOf(contact)?.let { contacts?.get(it)?.favorite = false }
        fAdapter?.notifyDataSetChanged()
    }

    class LoadContacts(context: Context, activity: Activity, private val contacts: ArrayList<Contact>?, private val adapter: MyContactRecyclerViewAdapter?) : AsyncTask<Void, Void, Void>() {

        private val context: WeakReference<Context> = WeakReference(context)
        private val act: WeakReference<Activity> = WeakReference(activity)

        override fun doInBackground(vararg params: Void?): Void? {
            val baseContext = context.get()
            val activity = act.get()
            val contactsUri = ContactsContract.Contacts.CONTENT_URI

            // Querying the table ContactsContract.Contacts to retrieve all the
            // contacts
            val contactsCursor = activity?.contentResolver?.query(contactsUri,
                    null, null, null,
                    ContactsContract.Contacts.DISPLAY_NAME + " ASC ")
            if (contactsCursor?.moveToFirst()!!) {
                do {
                    val contactId = contactsCursor.getLong(contactsCursor
                            .getColumnIndex("_ID"))

                    val dataUri = ContactsContract.Data.CONTENT_URI

                    // Querying the table ContactsContract.Data to retrieve
                    // individual items like
                    // home phone, mobile phone, work email etc corresponding to
                    // each contact
                    val dataCursor = activity.contentResolver.query(dataUri,
                            null,
                            ContactsContract.Data.CONTACT_ID + "=" + contactId,
                            null, null)

                    var displayName: String?
                    var nickName: String? = ""
                    var homePhone: String? = ""
                    var mobilePhone: String? = ""
                    var workPhone: String? = ""
                    var photoPath: String? = ""
                    var photoByte: ByteArray?
                    var homeEmail: String? = ""
                    var workEmail: String? = ""
                    var companyName: String? = ""
                    var title: String? = ""

                    if (dataCursor.moveToFirst()) {
                        // Getting Display Name
                        displayName = dataCursor
                                .getString(dataCursor
                                        .getColumnIndex(ContactsContract.Data.DISPLAY_NAME))
                        do {

                            // Getting NickName
                            if (dataCursor.getString(dataCursor
                                            .getColumnIndex("mimetype"))
                                    == ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE)
                                nickName = dataCursor.getString(dataCursor
                                        .getColumnIndex("data1"))

                            // Getting Phone numbers
                            if (dataCursor.getString(dataCursor
                                            .getColumnIndex("mimetype")) ==
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE) {
                                when (dataCursor.getInt(dataCursor
                                        .getColumnIndex("data2"))) {
                                    ContactsContract.CommonDataKinds.Phone.TYPE_HOME ->
                                        homePhone = dataCursor.getString(dataCursor
                                                .getColumnIndex("data1"))
                                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE ->
                                        mobilePhone = dataCursor
                                                .getString(dataCursor
                                                        .getColumnIndex("data1"))
                                    ContactsContract.CommonDataKinds.Phone.TYPE_WORK ->
                                        workPhone = dataCursor.getString(dataCursor
                                                .getColumnIndex("data1"))
                                }
                            }

                            // Getting EMails
                            if (dataCursor.getString(dataCursor
                                            .getColumnIndex("mimetype")) == ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE) {
                                when (dataCursor.getInt(dataCursor
                                        .getColumnIndex("data2"))) {
                                    ContactsContract.CommonDataKinds.Email.TYPE_HOME ->
                                        homeEmail = dataCursor.getString(dataCursor
                                                .getColumnIndex("data1"))

                                    ContactsContract.CommonDataKinds.Email.TYPE_WORK ->
                                        workEmail = dataCursor.getString(dataCursor
                                                .getColumnIndex("data1"))
                                }
                            }

                            // Getting Organization details
                            if (dataCursor.getString(dataCursor
                                            .getColumnIndex("mimetype"))
                                    == ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE) {
                                companyName = dataCursor.getString(dataCursor
                                        .getColumnIndex("data1"))
                                title = dataCursor.getString(dataCursor
                                        .getColumnIndex("data4"))
                            }

                            // Getting Photo
                            if (dataCursor.getString(
                                            dataCursor.getColumnIndex("mimetype"))
                                    == ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE) {
                                photoByte = dataCursor.getBlob(dataCursor
                                        .getColumnIndex("data15"))

                                if (photoByte != null) {
                                    val bitmap = BitmapFactory
                                            .decodeByteArray(photoByte, 0,
                                                    photoByte.size)

                                    // Getting Caching directory
                                    val cacheDirectory = baseContext?.cacheDir

                                    // Temporary file to store the contact image
                                    val tmpFile = File((cacheDirectory?.path) +
                                            "/tmp" + contactId + ".png")

                                    // The FileOutputStream to the temporary
                                    // file
                                    try {
                                        val fOutStream = FileOutputStream(tmpFile)

                                        // Writing the bitmap to the temporary
                                        // file as png file
                                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOutStream)

                                        // Flush the FileOutputStream
                                        fOutStream.flush()

                                        // Close the FileOutputStream
                                        fOutStream.close()

                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }

                                    photoPath = tmpFile.path
                                }

                            }

                        } while (dataCursor.moveToNext())

                        var details = ""

                        // Concatenating various information to single string
                        if (homePhone != "")
                            details = "HomePhone : $homePhone\n"
                        if (mobilePhone != "")
                            details += "MobilePhone : $mobilePhone\n"
                        if (workPhone != "")
                            details += "WorkPhone : $workPhone\n"
                        if (nickName != "")
                            details += "NickName : $nickName\n"
                        if (homeEmail != "")
                            details += "HomeEmail : $homeEmail\n"
                        if (workEmail != "")
                            details += "WorkEmail : $workEmail\n"
                        if (companyName != "") {
                            details += "CompanyName : $companyName\n"
                        }
                        if (photoPath == "") {
                            photoPath = "@drawable/user_hd"
                        }
                        if (title != "")
                            details += "Title : $title\n"

                        /* Log.i(TAG, "name: $displayName")
                        Log.i(TAG, "number: $mobilePhone")
                        Log.i(TAG, "path: $photoPath")*/
                        if (homePhone == "") {
                            contacts?.add(Contact(displayName, mobilePhone, photoPath, false))
                        } else {
                            contacts?.add(
                                    Contact(displayName, mobilePhone, photoPath, false,
                                            homePhone, workPhone, nickName, homeEmail, workEmail,
                                            companyName, title))
                        }

                    }
                    dataCursor.close()
                } while (contactsCursor.moveToNext())
                contactsCursor.close()
            }
            Log.i(TAG, "${contacts?.size} contacts loaded")
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            adapter?.notifyDataSetChanged()
        }

    }
}

