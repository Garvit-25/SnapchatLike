package com.example.snapchat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class ViewSnapactivity : AppCompatActivity() {

    var messageTextView :TextView? = null
    var snapImageView : ImageView? = null
    val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_snapactivity)

        messageTextView = findViewById(R.id.captionTextView)
        snapImageView = findViewById(R.id.snapImageView)

        messageTextView?.text = intent.getStringExtra("message")

        val task = ImageDownloader()
        val image : Bitmap
        try{
            image = task.execute(intent.getStringExtra("imageUrl") ).get()
            snapImageView?.setImageBitmap(image)
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    inner class ImageDownloader : AsyncTask<String,Void,Bitmap> (){
        override fun doInBackground(vararg urls: String?): Bitmap? {
            try{
                val url = URL(urls[0])
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()

                val inputStream = connection.inputStream
                return BitmapFactory.decodeStream(inputStream)
            }catch (e : Exception){
                e.printStackTrace()
                return null
            }
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.currentUser?.uid as String).child("snaps").child(intent.getStringExtra("snapKey") as String).removeValue()

        FirebaseStorage.getInstance().getReference().child("images").child(intent.getStringExtra("imageName") as String).delete()
    }
}