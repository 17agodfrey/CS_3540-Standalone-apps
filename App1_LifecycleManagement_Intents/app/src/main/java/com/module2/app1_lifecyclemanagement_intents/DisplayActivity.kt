package com.module2.app1_lifecyclemanagement_intents

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView

class DisplayActivity : AppCompatActivity() {
    var mTvMessage: TextView? = null
    var mMessage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display)

        mTvMessage = findViewById<View>(R.id.message) as TextView

        val receivedIntent = intent

        mMessage = receivedIntent.getStringExtra("FN_DATA") + " " +
                receivedIntent.getStringExtra("LN_DATA") + " is logged in!"
        mTvMessage!!.text = mMessage
    }
}