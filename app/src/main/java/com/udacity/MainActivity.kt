package com.udacity

import android.app.DownloadManager
import android.app.Notification.EXTRA_NOTIFICATION_ID
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var selectedURL = ""
    private var selectedOption = ""

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        notificationManager = ContextCompat.getSystemService(
                applicationContext,
                NotificationManager::class.java
        ) as NotificationManager

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        createChannel(CHANNEL_ID, "ChannelCreated")

        radio_group.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.glide_button -> {
                    selectedURL = glideURL
                    selectedOption = resources.getString(R.string.glide_text_button)
                }
                R.id.load_app_button -> {
                    selectedURL = loadAppURL
                    selectedOption = resources.getString(R.string.load_app_button_text)
                }
                R.id.retrofit_button -> {
                    selectedURL = retrofitURL
                    selectedOption = resources.getString(R.string.retrofit_button_text)
                }
            }
        }

        download_button.setOnClickListener {
            if (selectedURL != "") {
                download()
                download_button.setState(ButtonState.Loading)
            } else {
                Toast.makeText(this, R.string.select_file, Toast.LENGTH_SHORT)
                        .show()
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            //retrieve extended data from the intent
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            //gets the notificationManager
            val notificationManager = getSystemService(NotificationManager::class.java)
            //status for the notification text
            var downloadStatusString: String? = null
            //status for the download, either success or fail
            var downloadStatus: Int? = null


            //gets the downloadManager
            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            //query to get download with the id 'id'
            val query = id?.let { DownloadManager.Query().setFilterById(it) }
            //gets the downloads that have been requested with the 'query' id's
            val cursor = downloadManager.query(query)

            //moves to the first column of cursor (in this case only has one)
            if (cursor.moveToFirst()) {
                //gets the status of the download at the first column
                downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
            }

            //sets the 'downloadStatusString' by checking the value of downloadStatus
            when(downloadStatus) {
                DownloadManager.STATUS_SUCCESSFUL -> downloadStatusString = "Success"
                DownloadManager.STATUS_FAILED -> downloadStatusString = "Fail"
            }

            //sends the notification of the download
            if (context != null){
                if (downloadStatusString != null) {
                    notificationManager.sendNotification(selectedOption, context, downloadStatusString)

                }
            }

            //switches the state of the button to the next state
            download_button.setState(ButtonState.Completed)
        }
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(selectedURL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
            )
                    .apply {
                        setShowBadge(false)
                    }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "File downloaded"

            val notificationManager = getSystemService(
                    NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

    fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context, status: String) {

        val intent = Intent(applicationContext, DetailActivity::class.java).apply {
            putExtra("status", status)
            putExtra("file", messageBody)
        }

        pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        action = NotificationCompat.Action(null, resources.getString(R.string.check_status), pendingIntent)

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)

                .setSmallIcon(R.drawable.ic_assistant_black_24dp)
                .setContentTitle(applicationContext
                        .getString(R.string.notification_title))
                .setContentText(getString(R.string.app_description))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(action)

        notify(0, builder.build())
    }

    companion object {
        private const val glideURL = "https://github.com/bumptech/glide"
        private const val loadAppURL = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
        private const val retrofitURL = "https://github.com/square/retrofit"

        private const val CHANNEL_ID = "channelId"
    }

}
