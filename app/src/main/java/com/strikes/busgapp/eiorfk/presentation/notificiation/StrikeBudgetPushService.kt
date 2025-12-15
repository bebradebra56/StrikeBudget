package com.strikes.busgapp.eiorfk.presentation.notificiation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.strikes.busgapp.R
import com.strikes.busgapp.StrikeBudgetActivity
import com.strikes.busgapp.eiorfk.presentation.app.StrikeBudgetApplication

private const val STRIKE_BUDGET_CHANNEL_ID = "strike_budget_notifications"
private const val STRIKE_BUDGET_CHANNEL_NAME = "StrikeBudget Notifications"
private const val STRIKE_BUDGET_NOT_TAG = "StrikeBudget"

class StrikeBudgetPushService : FirebaseMessagingService(){
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Обработка notification payload
        remoteMessage.notification?.let {
            if (remoteMessage.data.contains("url")) {
                strikeBudgetShowNotification(it.title ?: STRIKE_BUDGET_NOT_TAG, it.body ?: "", data = remoteMessage.data["url"])
            } else {
                strikeBudgetShowNotification(it.title ?: STRIKE_BUDGET_NOT_TAG, it.body ?: "", data = null)
            }
        }

        // Обработка data payload
        if (remoteMessage.data.isNotEmpty()) {
            strikeBudgetHandleDataPayload(remoteMessage.data)
        }
    }

    private fun strikeBudgetShowNotification(title: String, message: String, data: String?) {
        val strikeBudgetNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Создаем канал уведомлений для Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                STRIKE_BUDGET_CHANNEL_ID,
                STRIKE_BUDGET_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            strikeBudgetNotificationManager.createNotificationChannel(channel)
        }

        val strikeBudgetIntent = Intent(this, StrikeBudgetActivity::class.java).apply {
            putExtras(bundleOf(
                "url" to data
            ))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val strikeBudgetPendingIntent = PendingIntent.getActivity(
            this,
            0,
            strikeBudgetIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val strikeBudgetNotification = NotificationCompat.Builder(this, STRIKE_BUDGET_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.strike_budget_noti_icon)
            .setAutoCancel(true)
            .setContentIntent(strikeBudgetPendingIntent)
            .build()

        strikeBudgetNotificationManager.notify(System.currentTimeMillis().toInt(), strikeBudgetNotification)
    }

    private fun strikeBudgetHandleDataPayload(data: Map<String, String>) {
        data.forEach { (key, value) ->
            Log.d(StrikeBudgetApplication.STRIKE_BUDGET_MAIN_TAG, "Data key=$key value=$value")
        }
    }
}