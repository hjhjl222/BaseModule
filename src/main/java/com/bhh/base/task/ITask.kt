package com.bhh.base.task

import androidx.annotation.CallSuper
import kotlinx.coroutines.*

interface ITask {
    suspend fun run()
}

interface ITaskExecutor<Task: ITask>: CoroutineScope {
    val tasks: ArrayList<Task>

    fun addTask(vararg task: Task) {
        tasks.addAll(task)
    }

    @CallSuper
    fun exec() = launch(coroutineContext) {
        tasks.map {
            async { it.run() }
        }.joinAll()
    }
}

