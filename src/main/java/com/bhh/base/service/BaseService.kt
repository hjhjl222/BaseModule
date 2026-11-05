package com.bhh.base.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.annotation.CallSuper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.bhh.tools.log.Logger

abstract class BaseService: Service() {
    protected val log = Logger.get(this::class.simpleName!!)
    protected var isFore: Boolean = false
    protected val notifyId = 10240000


    @CallSuper
    override fun onCreate() {
        super.onCreate()
        log.info("${getContentTitle()} is create")
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        log.info("${getContentTitle()} is destroy")
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @CallSuper
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isFore = intent?.getBooleanExtra(IS_FOREGROUND, false)?:false
        if (isFore && foregroundServiceType == ServiceInfo.FOREGROUND_SERVICE_TYPE_NONE) {
            startForegroundService()
        }
        return startCommandReturn()
    }

    protected open fun startCommandReturn(): Int {
        return START_NOT_STICKY
    }

    private fun startForegroundService() {
        val notification = createNotification()
        startForeground(notifyId, notification)
    }

    protected open fun getResLogo(): Int {
        return androidx.appcompat.R.drawable.abc_ic_menu_selectall_mtrl_alpha
    }

    protected open fun getContentTitle(): String? {
        return this::class.simpleName
    }

    protected open fun getContentText(): String {
        return "Running"
    }

    private fun createNotification(): Notification {
        val channel = NotificationChannel(
            notifyId.toString(),
            getContentTitle(),
            NotificationManager.IMPORTANCE_NONE
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.cancel(notifyId)
        manager.createNotificationChannel(channel)
        return NotificationCompat.Builder(this, notifyId.toString())
            .setContentTitle(getContentTitle())
            .setContentText(getContentText())
            .setSmallIcon(getResLogo())
            .build()
    }

    companion object {
        const val IS_FOREGROUND = "isForeground"
    }
}