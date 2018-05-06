package dev.agenda.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import dev.agenda.R
import dev.agenda.models.Contact

import kotlinx.android.synthetic.main.activity_contact_info.*
import kotlinx.android.synthetic.main.content_contact_info.*
import android.support.v4.app.NavUtils
import android.view.MenuItem


class ContactInfoActivity : AppCompatActivity() {

    private var contact : Contact? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_info)
        setSupportActionBar(toolbar)

        val intent = getIntent()
        if( Intent.ACTION_SEND == intent.action){
            contact = intent.extras.getBundle("contact").getParcelable("contact")
            if( contact != null ) {
                activity_display_name_edit.setText(contact?.name)
                activity_mobile_phone_edit.setText(contact?.phone)
                activity_home_phone_edit.setText(contact?.homePhone)
                activity_work_phone_edit.setText(contact?.workPhone)
                activity_nickname_edit.setText(contact?.nickName)
                activity_home_email_edit.setText(contact?.homeEmail)
                activity_work_email_edit.setText(contact?.workEmail)
                activity_company_name_edit.setText(contact?.companyName)
                if(!contact?.imageSrc?.contains("drawable")!!) {
                    activity_user_image.setImageBitmap(BitmapFactory.decodeFile(contact?.imageSrc))
                }
            }
        }

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
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
