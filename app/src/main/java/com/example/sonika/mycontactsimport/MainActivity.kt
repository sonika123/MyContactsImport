package com.example.sonika.mycontactsimport

import android.app.VoiceInteractor
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.os.Build
import android.provider.ContactsContract
import android.view.View
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_main.*
import android.app.Activity
import android.util.Log
import java.nio.file.Files.size
import android.content.DialogInterface
import android.Manifest.permission
import android.Manifest.permission.WRITE_CONTACTS
import android.Manifest.permission.READ_CONTACTS
import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.content.ContentUris
import android.content.ContentUris.withAppendedId
import android.widget.ImageView
import android.graphics.Bitmap
import android.net.Uri
import android.provider.ContactsContract.Contacts.NAME_RAW_CONTACT_ID
import android.provider.ContactsContract.Contacts.openContactPhotoInputStream
import java.net.URI


class MainActivity : AppCompatActivity() {

    private val REQUEST_MULTIPLE_PERMISSIONS = 124;
    var PICK_CONTACT = 1

    var contactID = null
    var photo: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AccessContact()
        btn_load.setOnClickListener(View.OnClickListener {
            val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)

            startActivityForResult(intent, PICK_CONTACT)
        })
    }

    private fun addPermission(permissionsList: MutableList<String>, permission: String): Boolean {

        if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED
                } else {
                    TODO("VERSION.SDK_INT < M")
                }) {

            permissionsList.add(permission)



            if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        !shouldShowRequestPermissionRationale(permission)
                    } else {
                        TODO("VERSION.SDK_INT < M")
                    })

                return false

        }

        return true

    }

    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {

        AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show()
    }

    public override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(reqCode, resultCode, data)
        when (reqCode) {
            PICK_CONTACT ->
                if (resultCode == Activity.RESULT_OK) {
                    val contactData = data!!.data
                    val c = managedQuery(contactData, null, null, null, null)

                    if (c.moveToFirst()) {

                        val id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                        val hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))

                        try {

                            if (hasPhone.equals("1", ignoreCase = true)) {

                                val phones = contentResolver.query(
                                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null)


                                txt_phno.setText("")
                                while (phones.moveToNext()) {
                                    val cNumber = phones.getString(phones.getColumnIndex("data1"))

                                    txt_phno.append("\nPhone Number is: $cNumber")
                                }
                            }

                        val name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))

                        txt_name.text = "Name is: $name"

                        val inputstream =  openContactPhotoInputStream(getContentResolver(),
                                withAppendedId(ContactsContract.Contacts.CONTENT_URI, id.toLong()))
                        photo = BitmapFactory.decodeStream(inputstream)
                        image_contact.setImageBitmap(photo)
                        }
                        catch (ex: Exception) {

                            print(ex)
                        }

                    }
                }
        }

    }

    private fun AccessContact() {

        val permissionsNeeded = ArrayList<String>()

        val permissionsList = ArrayList<String>()

        if (!addPermission(permissionsList, android.Manifest.permission.READ_CONTACTS))
            permissionsNeeded.add("Read Contacts")

        if (!addPermission(permissionsList, android.Manifest.permission.WRITE_CONTACTS))
            permissionsNeeded.add("Write Contacts")

        if (permissionsList.size > 0) {

            if (permissionsNeeded.size > 0) {
                var message = "You need to grant access to " + permissionsNeeded[0]
                for (i in 1 until permissionsNeeded.size)
                    message = message + ", " + permissionsNeeded[i]
                    showMessageOKCancel(message,

                        DialogInterface.OnClickListener { dialog, which ->
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(permissionsList.toTypedArray(),

                                        REQUEST_MULTIPLE_PERMISSIONS)
                            }
                        })

                return

            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissionsList.toTypedArray(),

                        REQUEST_MULTIPLE_PERMISSIONS)
            }

            return

        }

    }
}
