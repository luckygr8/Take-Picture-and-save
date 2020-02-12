package com.luckygr8.takepicture

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    /**
     * @author Lakshay Dutta
     * @see res/xml/provider.xml
     * storage path :: Android/data/com.luckygr8.takepicture/files/Pictures
     */

    private lateinit var imageview: ImageView
    private lateinit var button: Button
    private lateinit var textView: TextView
    private lateinit var context: Context

    private val TAKE_PHOTO = 1
    private var currentPhotoPath = ""
    private var uri: Uri? = null

    private fun bindViews() {

        imageview = findViewById(R.id.imageView)
        button = findViewById(R.id.button)
        textView = findViewById(R.id.textView)

        button.setOnClickListener {

            val photoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (photoIntent.resolveActivity(packageManager) != null) {
                val photo = createFile()
                if (photo != null) {
                    uri = FileProvider.getUriForFile(
                        this,
                        resources.getString(R.string.authority),
                        photo
                    )
                    photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                    startActivityForResult(photoIntent, TAKE_PHOTO)
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable("uri", uri)
        super.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = this
        bindViews()

        if (savedInstanceState != null) {
            val uri: Uri? = savedInstanceState.getParcelable("uri")
            Glide.with(this).load(uri).into(imageview)
            this.uri = uri
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            Glide.with(context).load(uri).into(imageview)
            textView.text = uri.toString()
        }
    }

    private fun createFile(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd").format(Date())
        val fileName = "photo_${timeStamp}_"
        val storage = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            fileName,
            ".jpg",
            storage
        )

        currentPhotoPath = image.absolutePath
        return image
    }

}
