package dev.agenda

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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

import dev.agenda.models.Contact
import java.io.File
import java.io.FileOutputStream


/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [ContactFragment.OnListFragmentInteractionListener] interface.
 */
class ContactFragment : Fragment() {

    // TODO: Customize parameters
    private var contacts: ArrayList<Contact>? = ArrayList()
    private var columnCount = 1

    private var listener: OnListFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
        loadContacts()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_contact_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                Log.i("Size of contacts:"," {${contacts?.size}}")
                adapter = MyContactRecyclerViewAdapter(contacts, listener)
            }
        }
        return view
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction()
    }

    companion object {
        const val ARG_COLUMN_COUNT = "column-count"
        const val PERMISSIONS_REQUEST_READ_CONTACTS = 100
    }


    private fun loadContacts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                        this.requireContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS),
                    PERMISSIONS_REQUEST_READ_CONTACTS)
            //callback onRequestPermissionsResult
        } else {
            getContacts()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadContacts()
            } else {
                //  toast("Permission must be granted in order to display contacts information")
            }
        }
    }


    private fun getContacts() {
        val contactsUri = ContactsContract.Contacts.CONTENT_URI

        // Querying the table ContactsContract.Contacts to retrieve all the
        // contacts
        val contactsCursor = this.activity!!.contentResolver.query(contactsUri,
                null, null, null,
                ContactsContract.Contacts.DISPLAY_NAME + " ASC ")
        if (contactsCursor.moveToFirst()) {
            do {
                val contactId = contactsCursor.getLong(contactsCursor
                        .getColumnIndex("_ID"))

                val dataUri = ContactsContract.Data.CONTENT_URI

                // Querying the table ContactsContract.Data to retrieve
                // individual items like
                // home phone, mobile phone, work email etc corresponding to
                // each contact
                val dataCursor = this.activity!!.contentResolver.query(dataUri,
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
                                val cacheDirectory = this.activity!!.baseContext.cacheDir

                                // Temporary file to store the contact image
                                val tmpFile = File(cacheDirectory.path +
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
                        photoPath = "Dummy"
                    }
                    if (title != "")
                        details += "Title : $title\n"

                    // Adding id, display name, path to photo and other
                    // details to cursor

                    /*mMatrixCursor!!.addRow(arrayOf(
                            contactId.toString(), displayName,
                            photoPath, details)
                    )*/
                    Log.i(TAG, "name: $displayName")
                    Log.i(TAG, "number: $mobilePhone")
                    Log.i(TAG, "path: $photoPath")
                    /*contacts.apply {
                        this?.add(Contact(displayName, mobilePhone, photoPath))
                    }*/
                    contacts?.add(Contact(displayName, mobilePhone, photoPath))
                }
                dataCursor.close()
            } while (contactsCursor.moveToNext())
            contactsCursor.close()
        }
    }


    /*private fun getContacts() {
       val resolver: ContentResolver = this.activity!!.contentResolver;
       val cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI,
               null, null, null, null)

       if (cursor.count > 0) {
           while (cursor.moveToNext()) {
               val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
               val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
               //val image : ByteArray = cursor.getBlob(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI))
               val phoneNumber = (cursor.getString(
                       cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))).toInt()

               if (phoneNumber > 0) {
                   val cursorPhone = this.activity!!.contentResolver.query(
                           ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                           null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", arrayOf(id), null)

                   if (cursorPhone.count > 0) {
                       while (cursorPhone.moveToNext()) {
                           val phoneNumValue = cursorPhone.getString(
                                   cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                           Log.i(TAG, "Name: $name")
                           Log.i(TAG, "number: $phoneNumValue")
                           val imageBytes = cursorPhone.getBlob(
                                   cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO_THUMBNAIL_URI))
                           if (imageBytes != null) {
                               Log.i(TAG, "Image array size: " + imageBytes.size)
                               val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                               val cacheDirectory = activity!!.baseContext.cacheDir
                               // Temporary file to store the contact image
                               val file: String = cacheDirectory.path + "/temp_" + id+".png"
                               val tmpFile = File(file)
                               try {
                                   val fOutStream = FileOutputStream(
                                           tmpFile)
                                   bitmap.compress(
                                           Bitmap.CompressFormat.PNG, 100, fOutStream)
                                   fOutStream.flush()
                                   fOutStream.close()
                               } catch (e: Exception) {
                                   e.printStackTrace()
                               }
                               val imageSrc = tmpFile.path
                               //TODO add method that add contacts to the array
                               contacts!!.add(Contact(name, phoneNumValue, imageSrc))
                           }
                       }
                   }
                   cursorPhone.close()
               }
           }
       } else {
           //   toast("No contacts available!")
       }
       cursor.close()
   }*/

}

