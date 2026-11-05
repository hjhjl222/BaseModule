package com.bhh.base.module

import android.content.Context
import com.bhh.tools.log.Logger
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

private val log = Logger.get("ModuleManager")
interface IModuleManager<Module: IModule>: CoroutineScope {

    val modules:HashMap<IModuleDefine, Module>

    fun <T: Module> get(name: IModuleDefine): T?{
        return modules[name] as? T
    }

    fun <T: Module> getByName(name: String?): T?{
        return  modules.keys.find { it.define == name }?.let { return get(it) }
    }

    fun forEach() {
        modules.forEach { (t, u) ->
            log.info("${t.define} -> ${u.name}")
        }
    }

    fun register(module: Module): Job? {
        if (modules.containsKey(module.name)) {
            return null
        }
        modules[module.name] = module
        return execInit(module)
    }

    fun unregister(module: Module){
        modules.remove(module.name).apply {
            execRelease(module)
        }
    }

    fun initHolder(finish:()->Unit) = runBlocking(coroutineContext) {
        log.info("waiting acquire, module size: ${modules.size}")
        modules.forEach { it.value.waitInit() }
        finish()
        log.info("waiting acquire continue")
    }

    open fun initProcessCallback(module: Module) {
        log.info( "${module.name} init finish")
    }

    open fun doneInit() {
        log.info("doneInit")
    }

    fun register(vararg modules: Module) = launch(coroutineContext) {
        modules.map { register(it)!! }.joinAll()
        doneInit()
    }

    fun uiInit(context: Context) {
        modules.map { it.value.uiInit(context) }
    }

    fun start(module: Module) = launch(coroutineContext) {
        module.start()
    }

    /**
     * call in Application.onCreate()
     */
    fun start() = launch(coroutineContext) {
        modules.map {
            val module = it.value
            execute(if (module is BaseModule) module.coroutineContext else coroutineContext){
                it.value.start()
        }}.joinAll()
    }

    fun release() = runBlocking {
        modules.map { execRelease(it.value) }.joinAll()
        modules.clear()
    }

    fun releaseOnMain() = runBlocking {
        modules.map { it.value.release() }
        modules.clear()
    }

    fun execInit(module: Module) = execute(if (module is BaseModule) module.coroutineContext else coroutineContext) {
        try {
            module.init()
            initProcessCallback(module)
            if (module is MainModule<*>) {
                module.start()
            }
        } catch (e: Exception) {
            log.error("${e.message} -> \r\n${e.stackTraceToString()}")
        }
    }

    fun execRelease(module: Module) = execute(coroutineContext) {
        try {
            module.release()
            log.info("release success ${module.name}")
        } catch (e: Exception) {
            log.error("${e.message} -> \r\n${e.stackTraceToString()}")
        }
    }

    fun execute(coroutineCont: CoroutineContext = coroutineContext,function: (suspend () -> Unit)) : Job {
        return launch(coroutineCont) {
            try {
                function()
            } catch (e: Exception) {
                log.error("${e.message} -> \r\n${e.stackTraceToString()}")
            }
        }
    }
}