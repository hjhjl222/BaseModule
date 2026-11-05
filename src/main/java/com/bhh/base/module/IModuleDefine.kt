package com.bhh.base.module

import com.lpsjcx.channel.data.App

interface IModuleDefine {
    val define: String
    val desc: String
}

abstract class AppSubModuleDefine(app: App): IModuleDefine {
    override val define: String = app.packageName
    override val desc: String = app.name
}