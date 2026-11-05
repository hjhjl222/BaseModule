package com.bhh.base.system

import android.app.Application
import android.content.Context
import com.bhh.tools.app.ApplicationHandler

abstract class Application: Application() {
    protected abstract val initProcessor: BaseProcessor

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        ApplicationHandler.onCreate(this)
        initProcessor.onInit()
    }

    override fun onCreate() {
        super.onCreate()
        initProcessor.onStart()
    }

    override fun onTerminate() {
        super.onTerminate()
        initProcessor.onRelease()
        ApplicationHandler.onTerminate(this)
    }

    fun onViewPost(context: Context) {
        initProcessor.onUIInit(context)
    }

}