package com.bhh.base.module

import android.content.Context

interface IModule {
    val name: IModuleDefine
    suspend fun waitInit()
    suspend fun init()
    suspend fun start()
    suspend fun release()
    fun uiInit(context: Context) {}
}