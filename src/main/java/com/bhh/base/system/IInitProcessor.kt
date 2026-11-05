package com.bhh.base.system

import android.content.Context

internal interface IInitProcessor {
    fun onInit()
    fun onStart()
    fun onUIInit(context: Context)
    fun onRelease()
}