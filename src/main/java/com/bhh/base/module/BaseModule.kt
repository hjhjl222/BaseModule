package com.bhh.base.module

import androidx.annotation.CallSuper
import com.lpsjcx.channel.TraceManager
import com.lpsjcx.channel.data.TraceEvent
import com.bhh.tools.coroutines.GlobalScheduler
import com.bhh.tools.coroutines.event.EventBus
import com.bhh.tools.log.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.sync.Semaphore

abstract class BaseModule(override val name: IModuleDefine): IModule, CoroutineScope, IEventSender {
    protected val logger by lazy { Logger.get(getLogTag()) }
    protected var activate = false
    private val semaphore = Semaphore(1)
    protected val waitSemaphore = Semaphore(1)
    private val disposables = arrayListOf<EventBus.Disposable>()
    protected val job = Job()
    override val coroutineContext = GlobalScheduler.Default + job
    protected open fun registerEvent(): ArrayList<EventBus.Disposable>{
        return arrayListOf()
    }

    protected open fun getLogTag(): String {
        return name.define
    }

    protected open suspend fun onInit() {}
    protected open suspend fun onStart() {}


    @CallSuper
    override suspend fun init() {
        logger.info( "init ${name.define} module")
        onActionCatchError {
            semaphore.acquire()
            waitSemaphore.acquire()
        }
        onInit()
        disposables.addAll(registerEvent())
    }

    final override suspend fun waitInit() {
        onActionCatchError {
            waitSemaphore.acquire()
            waitSemaphore.release()
        }
    }

    @CallSuper
    override suspend fun start() {
        logger.info("start: ${name.define}")
        onStart()
        onActionCatchError {
            releaseSemaphore()
            waitSemaphore.release()
        }
        activate = true
    }

    @CallSuper
    override suspend fun release() {
        activate = false
        onActionCatchError {
            releaseSemaphore()
        }
        job.cancel()
        disposables.forEach {
            it.dispose()
        }
        disposables.clear()
        logger.info( "release:${name.define}")
    }

    private fun releaseSemaphore() {
        if (semaphore.availablePermits == 0) {
            semaphore.release()
        }
    }

    protected suspend fun <T>onWaitForAction(action: suspend ()->T):T? {
        if (!activate) {
            throw RuntimeException("this method can not be called after start")
        }
        try {
            waitSemaphore.acquire()
            return action()
        } catch (e: Exception) {
            logger.error(e.message, e)
            return null
        } finally {
            waitSemaphore.release()
        }
    }

    private suspend fun onActionCatchError(onAction: suspend () -> Unit) {
        try {
            onAction()
        } catch (e: Exception) {
            logger.info( e.message + "\r\n" + e.stackTraceToString())
        }
    }
}

abstract class SubModule(name: IModuleDefine, val parentModule: MainModule<*>): BaseModule(name)