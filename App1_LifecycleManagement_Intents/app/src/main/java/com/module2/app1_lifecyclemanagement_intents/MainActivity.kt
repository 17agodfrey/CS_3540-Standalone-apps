package com.module2.app1_lifecyclemanagement_intents

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener  {
    private var mFirstName: String? = null
    private var mMiddleName: String? = null
    private var mLastName: String? = null

    private var mEtFirstName: EditText? = null
    private var mEtMiddleName: EditText? = null
    private var mEtLastName: EditText? = null

    private var mButtonSubmit: Button? = null
    private var mButtonCamera: ImageButton? = null

    private var mThumbnailImage: Bitmap? = null
    private var mImagePath: String? = null

    private var mDisplayIntent: Intent? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mEtFirstName = findViewById<View>(R.id.et_first_name) as EditText
        mEtMiddleName = findViewById<View>(R.id.et_middle_name) as EditText
        mEtLastName = findViewById<View>(R.id.et_last_name) as EditText

        mButtonSubmit = findViewById<View>(R.id.button_submit) as Button
        mButtonCamera = findViewById<View>(R.id.button_profile_pic) as ImageButton

        mButtonSubmit!!.setOnClickListener(this)
        mButtonCamera!!.setOnClickListener(this)

        mDisplayIntent = Intent(this, DisplayActivity::class.java)
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.button_submit -> {
                mFirstName = mEtFirstName!!.text.toString()
                mMiddleName = mEtMiddleName!!.text.toString()
                mLastName = mEtLastName!!.text.toString()

                if (mFirstName.isNullOrBlank() || mMiddleName.isNullOrBlank() || mLastName.isNullOrBlank()) {
                    //Complain that there's no text
                    Toast.makeText(this@MainActivity, "Please fill all text fields!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    //Start a new activity and pass the strings to them
                    mDisplayIntent!!.putExtra("FN_DATA", mFirstName)
                    mDisplayIntent!!.putExtra("MN_DATA", mMiddleName)
                    mDisplayIntent!!.putExtra("LN_DATA", mLastName)
                    startActivity(mDisplayIntent) //explicit intent
                }
            }
            R.id.button_profile_pic -> {

                //The button press should open a camera
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                try{
                    cameraLauncher.launch(cameraIntent)
                }catch(ex: ActivityNotFoundException){
                    //Do something here
                }
            }
        }
    }
    private var cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val extras = result.data!!.extras
            mThumbnailImage = extras?.getParcelable("data") as? Bitmap

            //Open a file and write to it
            if (isExternalStorageWritable) {
                mImagePath = saveImage(mThumbnailImage)
                mDisplayIntent!!.putExtra("imagePath", mImagePath)
                mButtonCamera!!.background = BitmapDrawable(resources, mThumbnailImage)
            } else {
                Toast.makeText(this, "External storage not writable.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveImage(finalBitmap: Bitmap?): String {
        val root = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val myDir = File("$root/saved_images")
        myDir.mkdirs()
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fname = "Thumbnail_$timeStamp.jpg"
        val file = File(myDir, fname)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap!!.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
            Toast.makeText(this, "file saved!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file.absolutePath
    }

    private val isExternalStorageWritable: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state
        }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        //Get the strings
        mFirstName = mEtFirstName!!.text.toString()
        mMiddleName = mEtMiddleName!!.text.toString()
        mLastName = mEtLastName!!.text.toString()

        //Put them in the outgoing Bundle
        outState.putString("FN_TEXT", mFirstName)
        outState.putString("MN_TEXT", mMiddleName)
        outState.putString("LN_TEXT", mLastName)
        val backgroundBitmap = (mButtonCamera?.background as? BitmapDrawable)?.bitmap
        outState.putParcelable("BUTTON_BACKGROUND", backgroundBitmap)    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {

        //Restore the view hierarchy automatically
        super.onRestoreInstanceState(savedInstanceState)

        //Restore stuff
        mEtFirstName!!.setText(savedInstanceState.getString("FN_TEXT"))
        mEtMiddleName!!.setText(savedInstanceState.getString("MN_TEXT"))
        mEtLastName!!.setText(savedInstanceState.getString("LN_TEXT"))
        val backgroundBitmap = savedInstanceState.getParcelable<Bitmap>("BUTTON_BACKGROUND")
        if (backgroundBitmap != null) {
            val backgroundDrawable = BitmapDrawable(resources, backgroundBitmap)
            mButtonCamera?.background = backgroundDrawable
        }
    }
}