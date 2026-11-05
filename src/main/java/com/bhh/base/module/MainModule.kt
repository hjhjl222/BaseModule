package com.bhh.base.module

import android.content.Context
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.runBlocking

abstract class MainModule<SModule: SubModule>(name: IModuleDefine): BaseModule(name) {

    override suspend fun start() {
        moduleManager.start()
        super.start()
    }

    override fun uiInit(context: Context) {
        moduleManager.uiInit(context)
    }

    protected fun register(vararg sModules: SModule, autoStart: Boolean = false) = runBlocking {
        sModules.map {
            logger.debug( "register ${it::class.java.simpleName}")
            moduleManager.register(it)!!
        }.joinAll()
        if (autoStart) {
            sModules.map { module ->
                val coroutineContext = module.coroutineContext
                moduleManager.execute(coroutineContext) { module.start() }
            }
        }
    }

    protected fun <T: SModule> getModule(moduleDefine: IModuleDefine): T? {
        return moduleManager.get(moduleDefine)
    }
    protected fun <T: SModule> getModule(name: String?): T? {
        return moduleManager.getByName(name)
    }

    protected fun unregister(sModule: SModule) {
        moduleManager.unregister(sModule)
    }

    override suspend fun release() {
        super.release()
        moduleManager.release()
    }

    private val moduleManager = object : IModuleManager<SModule> {
        override val modules = HashMap<IModuleDefine, SModule>()
        override val coroutineContext by lazy { this@MainModule.coroutineContext }
    }
}



