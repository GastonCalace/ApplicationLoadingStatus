package com.udacity

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.graphics.Color
import android.os.Bundle
import android.renderscript.ScriptGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.udacity.R.string.glide_text_button
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    private lateinit var notificationManager: NotificationManager

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val file = intent.getStringExtra("file")
        val status = intent.getStringExtra("status")

        notificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.cancelAll()

        file_name_dynamic_label.text = file
        status_name_dynamic_label.text = status

        if (status == "Success")
            status_name_dynamic_label.setTextColor(Color.parseColor(resources.getString(R.color.colorPrimaryDark)))
        else if (status == "Fail")
            status_name_dynamic_label.setTextColor(Color.RED)

        ok_button.setOnClickListener {
            onBackPressed()
        }
    }

}
