package com.bhh.base.module

import com.bhh.base.coroutine.ModuleEvent
import com.bhh.base.coroutine.SubModuleEvent
import com.bhh.tools.coroutines.event.CallBackEvent
import com.bhh.tools.coroutines.event.EventBus
import com.bhh.tools.coroutines.event.IEvent
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext

interface IEventSender {
    
    fun call(event: IEvent) = runBlocking{
        EventBus.send(event)
    }

    fun <Return: Any, Event: CallBackEvent<Return>>callback(event: Event, context: CoroutineContext) = runBlocking{
        EventBus.sendCallbackEvent(event, context)
    }

    fun call(event: ModuleEvent) = runBlocking{
        EventBus.send(event)
    }

    fun call(event: ModuleEvent, context: CoroutineContext) = runBlocking{
        EventBus.send(event, context)
    }

    fun call(event: SubModuleEvent) = runBlocking{
        EventBus.send(event)
    }

    fun call(event: SubModuleEvent, context: CoroutineContext) = runBlocking{
        EventBus.send(event, context)
    }
}
