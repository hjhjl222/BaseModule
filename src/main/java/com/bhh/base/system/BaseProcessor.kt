package com.bhh.base.system

import android.content.Context
import com.bhh.base.module.AbsModuleManager

abstract class BaseProcessor: IInitProcessor {
    protected abstract val moduleManager: AbsModuleManager
    override fun onInit() {
        moduleManager.init()
    }

    override fun onStart() {
    }

    override fun onUIInit(context: Context) {
        moduleManager.uiInit(context)
    }

    override fun onRelease() {
        moduleManager.release()
    }
}