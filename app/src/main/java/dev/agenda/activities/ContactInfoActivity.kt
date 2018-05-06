package dev.agenda.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import dev.agenda.R
import dev.agenda.models.Contact

import kotlinx.android.synthetic.main.activity_contact_info.*
import kotlinx.android.synthetic.main.content_contact_info.*
import dev.agenda.dialogs.MakeCallDialogFragment


class ContactInfoActivity : AppCompatActivity() {

    private var contact: Contact? = null
    private var numbers: ArrayList<String?> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_info)
        setSupportActionBar(toolbar)


        val intent = getIntent()
        if (Intent.ACTION_SEND == intent.action) {
            contact = intent.extras.getBundle("contact").getParcelable("contact")
            if (contact != null) {
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
        }

        contact?.phone.takeIf { it != null }.apply { numbers.add(this) }
        contact?.homePhone.takeIf { it != null }.apply { numbers.add(this) }
        contact?.workPhone.takeIf { it != null }.apply { numbers.add(this) }

        Log.i("Contact Info Call", "$numbers")
        fab.setOnClickListener { view ->
            /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()*/
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
