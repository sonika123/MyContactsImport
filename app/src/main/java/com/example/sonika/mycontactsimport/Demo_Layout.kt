package com.example.sonika.mycontactsimport

import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.demo_layout.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest
import android.R.attr.data
import android.R.attr.timePickerDialogTheme
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.support.v4.app.NotificationCompat.getExtras
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.Menu
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class Demo_Layout : AppCompatActivity() {
    var GALLERY = 1
    var CAMERA = 2
    var CAMERA_PIC_REQUEST = 1337
    var REQUEST_MULTIPLE_PERMISSIONS = 111
    var languages = arrayOf("JAVA", "C", "C++", "Python", "Swift", "Javascript", "PHP")
    val myCalendar = Calendar.getInstance()
    var countryArray = arrayOf("India", "Nepal", "Pakistan", "China", "Australia",
            "Malaysiya")
    var flagArray = arrayOf(R.drawable.india, R.drawable.nepal, R.drawable.pakistan,
            R.drawable.china, R.drawable.australia, R.drawable.malaysiya)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.demo_layout)

        val autocompleteAdapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, languages)
        autocomplete.setAdapter(autocompleteAdapter)

        val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel()
        }



        et_birthday.setOnClickListener(View.OnClickListener {
            // TODO Auto-generated method stub
            DatePickerDialog(this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show()

        })

        val spinnerAdapter = SpinnerAdapter(getApplicationContext(), flagArray, countryArray);
        spinner.setAdapter(spinnerAdapter)



        buttonSave.setOnClickListener(View.OnClickListener {
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)


        })

        AccessPermit()
        imageView_profilepic.setOnClickListener(View.OnClickListener {

                selectOption()


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

    private fun AccessPermit() {

        val permissionsNeeded = ArrayList<String>()

        val permissionsList = ArrayList<String>()

        if (!addPermission(permissionsList, android.Manifest.permission.CAMERA))
            permissionsNeeded.add("Camera")

        if (!addPermission(permissionsList, android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Storage")

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
        private fun selectOption() {

        val options = arrayOf("camera", "gallery")

        val builder = AlertDialog.Builder(this@Demo_Layout)
        builder.setTitle("CHOOSE")
        builder.setItems(options)
        { options, selection ->
            when (selection) {
                1 -> choosePhotoFromGallery()
                0 -> choosePhotoFromCamera()
            }
        }


        builder.show()

    }


    private fun choosePhotoFromGallery()  {
        val intentPickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intentPickPhoto, GALLERY)
    }
    private fun choosePhotoFromCamera() {
        val intentcamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intentcamera, CAMERA)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//             if (requestCode == 1)
//             {
//                val bitmap = data!!.getExtras().get("data") as Bitmap
//                imageView_profilepic.setImageBitmap(bitmap)
//            }
        if (requestCode == GALLERY) {
            if (data != null) {
                val contentURI = data.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    imageView_profilepic!!.setImageBitmap(bitmap)

                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }
        }
        else if (requestCode == CAMERA)
        {
            val thumbnail = data!!.extras!!.get("data") as Bitmap
            saveImage(thumbnail)
            imageView_profilepic!!.setImageBitmap(thumbnail)
        }
    }

    private fun saveImage(myBitmap: Bitmap) : String{
        val bytes = ByteArrayOutputStream()

        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)

        val destination = File(
                (Environment.getExternalStorageDirectory()).toString() + IMAGE_DIRECTORY)

        Log.d("fee",destination.toString())
        if (!destination.exists())
        {

            destination.mkdirs()
        }
        try
        {
            Log.d("heel",destination.toString())
            val fileDetails = File(destination, ((Calendar.getInstance()
                    .getTimeInMillis()).toString() + ".jpg"))
            fileDetails.createNewFile()
            val fo = FileOutputStream(fileDetails)
            fo.write(bytes.toByteArray())
//            MediaScannerConnection.scanFile(this,
//                    arrayOf(fileDetails.getPath()),
//                    arrayOf("image/jpeg"), null)

            Log.d("TAG", "File Saved::--->" + fileDetails.getAbsolutePath())
            fo.close()

            return fileDetails.getAbsolutePath()
        }
        catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }
    companion object {
        val IMAGE_DIRECTORY = "/DCIM/hello"
    }

    private fun updateLabel() {
        val myFormat = "MM/dd/yy"; //In which you need put here
        val sdf =  SimpleDateFormat(myFormat, Locale.US);

            et_birthday.setText(sdf.format(myCalendar.getTime()))

        }


    }