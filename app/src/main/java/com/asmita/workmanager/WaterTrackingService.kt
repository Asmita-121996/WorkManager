package com.asmita.workmanager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class WaterTrackingService : Service() {

    private var fluidBalanceMilliliters = 0f
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private lateinit var serviceHandler: Handler

    companion object {
        const val EXTRA_INTAKE_AMOUNT_MILLILITERS = "intake"
        private const val NOTIFICATION_ID = 0x3A7A
    }

    override fun onCreate() {
        super.onCreate()

        Log.e("Asmita==>", " ===============onCreate===============")

        notificationBuilder = startForegroundService()
       /* val handlerThread = HandlerThread("RouteTracking").apply {
            start()
        }
        serviceHandler = Handler(handlerThread.looper)*/
        updateFluidBalance()
    }

    override fun onStartCommand(
        intent: Intent?, flags: Int, startId:
        Int
    ): Int {

        Log.e("Asmita==>", " ===============onStartCommand===============")
        val returnValue = super.onStartCommand(intent, flags, startId)
        val intakeAmountMilliliters = intent?.getFloatExtra(EXTRA_INTAKE_AMOUNT_MILLILITERS, 0f)
        intakeAmountMilliliters?.let {
            addToFluidBalance(it)
        }
        return returnValue
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.e("Asmita==>", " ==============onBind==============")
        return null
    }

    private fun getPendingIntent(): PendingIntent {
        if (Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {
           return PendingIntent.getActivity(
                this,
                0,
                Intent(this, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            return  PendingIntent.getActivity(
                this,
                0,
                Intent(this, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): String {

        val channelId = "FluidBalanceTracking"
        val channelName = "Fluid Balance Tracking"
        val channel =
            NotificationChannel(
                channelId, channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager
        service.createNotificationChannel(channel)
        return channelId
    }

    private fun getNotificationBuilder(
        pendingIntent: PendingIntent,
        channelId: String
    ) =
        NotificationCompat.Builder(this, channelId)
            .setContentTitle("Tracking your fluid balance")
            .setContentText("Tracking")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setTicker("Fluid balance tracking started")

    private fun startForegroundService(): NotificationCompat.Builder {

        val pendingIntent = getPendingIntent()
        val channelId = if (Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {
            createNotificationChannel()
        } else {
            ""
        }
        val notificationBuilder = getNotificationBuilder(
            pendingIntent,
            channelId
        )
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
        return notificationBuilder
    }

    private fun addToFluidBalance(amountMilliliters: Float) {
        synchronized(this) {
            fluidBalanceMilliliters += amountMilliliters
            updateFluidBalance()
        }
    }

    private fun updateFluidBalance() {
        //serviceHandler.postDelayed({
           // updateFluidBalance()
           // addToFluidBalance(-0.144f)
            notificationBuilder.setContentText(
                "Your fluid balance: %.2f".format(fluidBalanceMilliliters)
            )
            startForeground(NOTIFICATION_ID, notificationBuilder.build())
        //}, 10000L)
    }

    override fun onDestroy() {
        serviceHandler.removeCallbacksAndMessages(null)
    }

}

