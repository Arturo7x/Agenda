package dev.agenda.dialogs

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import dev.agenda.R
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.widget.Toast


class MakeCallDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val numbers = arguments.getStringArrayList("numbers")
        val builder = AlertDialog.Builder(activity)
        /* 0 = cel phone
           1 = home phone
           2 = work phone
         */
        val callIntent = Intent(Intent.ACTION_CALL)
        val call = fun(number: String) {
            callIntent.data = Uri.parse("tel:$number")
            if (ActivityCompat.checkSelfPermission(this.activity,
                            Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            startActivity(callIntent)
        }
        val msg = "No number found"
        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(this.activity.applicationContext, msg, duration)

        builder.setTitle(R.string.dialog_title)
                .setItems(R.array.call_options, { _, which ->
                    when (which) {
                        0 -> {
                            if (numbers.get(0) != null)
                                call(numbers?.get(0)!!)
                            else {
                                toast.show()
                                dialog.cancel()
                            }
                        }
                        1 -> {
                            if (numbers.get(1) != null)
                                call(numbers?.get(1)!!)
                            else {
                                toast.show()
                                dialog.cancel()
                            }
                        }
                        2 -> {
                            if (numbers.get(2) != null)
                                call(numbers?.get(2)!!)
                            else {
                                toast.show()
                                dialog.cancel()
                            }
                        }
                    }
                }).setNegativeButton(R.string.text_cancel, { _, _ -> dialog.cancel() })
        return builder.create()
    }

}