package dev.agenda.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.widget.Toast
import dev.agenda.R
import dev.agenda.models.Contact

import kotlinx.android.synthetic.main.activity_contact_info.*
import kotlinx.android.synthetic.main.content_contact_info.*
import dev.agenda.dialogs.MakeCallDialogFragment
import dev.agenda.utilities.loadImage
import java.io.*


class ContactInfoActivity : AppCompatActivity() {

    private var contact: Contact? = null
    private var numbers: ArrayList<String?> = ArrayList()
    private var editing: Boolean = true
    private var imageSrc: String = "@drawable/user_hd"

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.contact_info_menu, menu)
        val save = menu?.findItem(R.id.save)
        save?.setOnMenuItemClickListener {
            if (!editing) {
                Log.i("Contact Info Call", "Check clicked")
                addUser()
                setIntentResultBack()
                Toast.makeText(applicationContext, "${contact?.name} added to list", Toast.LENGTH_SHORT).show()
            } else {
                Log.i("Contact Info Call", "Check clicked")
                save()
                setIntentResultBack()
                Toast.makeText(applicationContext, "saved", Toast.LENGTH_SHORT).show()
            }
            true
        }
        return true
    }

    // ACTIVITY LOGIC
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_info)
        toolbar.title = contact?.name
        setSupportActionBar(toolbar)
        supportActionBar?.title = contact?.name

        handleIntent()
        handleCall()
        addListeners()

    }

    private fun addListeners() {
        add_photo.setOnClickListener {
            val pickIntent = Intent(Intent.ACTION_PICK)
            pickIntent.type = "image/*"
            val result = 1
            startActivityForResult(pickIntent, result)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val cacheDirectory = baseContext?.cacheDir
            val imageUri = data?.data
            val photoByte: ByteArray? = imageToByte(imageUri)
            imageSrc = loadImage(cacheDirectory, contact?.id, photoByte)
            contact?.imageSrc = imageSrc
            Log.i("Contact Info Call", imageSrc)
            activity_user_image.setImageBitmap(BitmapFactory.decodeFile(imageSrc))
        }
    }

    private fun imageToByte(uri: Uri?): ByteArray? {
        var data: ByteArray? = null
        try {
            val cr = baseContext.contentResolver
            val inputStream = cr.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            data = baos.toByteArray()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        return data
    }

    private fun addUser() {
        contact = Contact(
                contact?.id,
                activity_display_name_edit.text.toString(),
                activity_mobile_phone_edit.text.toString(),
                contact?.imageSrc,
                false,
                activity_home_phone_edit?.text.toString(),
                activity_work_phone_edit.text.toString(),
                activity_nickname_edit.text.toString(),
                activity_home_email_edit.text.toString(),
                activity_work_email_edit.text.toString(),
                activity_company_name_edit.text.toString(),
                ""
        )
    }

    private fun save() {
        contact?.name = activity_display_name_edit.text.toString()
        contact?.phone = activity_mobile_phone_edit.text.toString()
        contact?.imageSrc = imageSrc
        contact?.homePhone = activity_home_phone_edit?.text.toString()
        contact?.workPhone = activity_work_phone_edit.text.toString()
        contact?.nickName = activity_nickname_edit.text.toString()
        contact?.homeEmail = activity_home_email_edit.text.toString()
        contact?.workEmail = activity_work_email_edit.text.toString()
        contact?.companyName = activity_company_name_edit.text.toString()
    }

    private fun setIntentResultBack() {
        val intent = Intent()
        intent.putExtra("contact", contact)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun handleIntent() {
        val intent = intent
        if (Intent.ACTION_SEND == intent.action) {
            contact = intent.extras.getParcelable("contact")
            if (contact != null) {
                editing = true
                activity_display_name_edit.setText(contact?.name)
                activity_mobile_phone_edit.setText(contact?.phone)
                activity_home_phone_edit.setText(contact?.homePhone)
                activity_work_phone_edit.setText(contact?.workPhone)
                activity_nickname_edit.setText(contact?.nickName)
                activity_home_email_edit.setText(contact?.homeEmail)
                activity_work_email_edit.setText(contact?.workEmail)
                activity_company_name_edit.setText(contact?.companyName)
                if (!contact?.imageSrc?.contains("drawable")!!) {
                    activity_user_image.setImageBitmap(BitmapFactory.decodeFile(contact?.imageSrc))
                }
            }
        } else {
            editing = false
            contact = Contact()
            contact?.id = intent.extras.getInt("size").toString()
            activity_display_name_edit.setText("")
            activity_mobile_phone_edit.setText("")
            activity_home_phone_edit.setText("")
            activity_work_phone_edit.setText("")
            activity_nickname_edit.setText("")
            activity_home_email_edit.setText("")
            activity_work_email_edit.setText("")
            activity_company_name_edit.setText("")
        }
    }

    private fun handleCall() {
        contact?.phone.takeIf { it != null }.apply { numbers.add(this) }
        contact?.homePhone.takeIf { it != null }.apply { numbers.add(this) }
        contact?.workPhone.takeIf { it != null }.apply { numbers.add(this) }

        Log.i("Contact Info Call", "$numbers")
        fab.setOnClickListener { view ->
            val dialog = MakeCallDialogFragment()
            val bundle = Bundle()
            bundle.putStringArrayList("numbers", numbers)
            dialog.arguments = bundle
            dialog.show(fragmentManager, "makeCallFragment")
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }
/*
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            val intent = NavUtils.getParentActivityIntent(this)
            intent!!.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            NavUtils.navigateUpTo(this, intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
*/
}
