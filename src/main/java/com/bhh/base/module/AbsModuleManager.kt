@file:Suppress("UNCHECKED_CAST")

package com.bhh.base.module

import android.content.Context


abstract class AbsModuleManager: IModuleManager<IModule> {

    override val modules = HashMap<IModuleDefine, IModule>()

    abstract fun init()

    override fun uiInit(context: Context) {
        modules.forEach { it.value.uiInit(context) }
    }

    companion object {
        lateinit var instance: AbsModuleManager
    }
}