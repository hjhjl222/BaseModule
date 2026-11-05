package com.bhh.base.module

import com.lpsjcx.channel.common.ExecType
import com.lpsjcx.channel.common.SandboxAction
import com.lpsjcx.channel.common.TaskAction
import com.lpsjcx.channel.common.TaskMode
import com.lpsjcx.channel.data.AccountShadeInfo
import com.lpsjcx.channel.data.AppConfigInfo
import com.lpsjcx.channel.data.AppRegion
import com.lpsjcx.channel.data.ExecData
import com.lpsjcx.channel.data.ExecResult
import com.lpsjcx.channel.data.SandboxConfig
import com.lpsjcx.channel.data.TaskSendInfo
import com.lpsjcx.channel.task.MergeTask
import com.bhh.tools.app.IApplicationHandler
import com.bhh.tools.log.Logger
import com.bhh.tools.util.newInstanceFromJSON
import com.bhh.tools.util.toJSON
import http.ITaskServer
import java.util.UUID

abstract class AppDataProcessor(protected val subModule: AppSubModule, protected val app: AppRegion, protected val taskServer: ITaskServer, protected val httpClient: AppDataHttpClient): IApplicationHandler {
    protected val logger = Logger.get(this::class.java.name)

    open suspend fun exec(data: ExecData, mode: TaskMode, sandboxConfig: SandboxConfig): ExecResult? {
        val type = ExecType.find(data.type)
        val result = when(type) {
            ExecType.REPORT_PRODUCT -> {
                sandboxConfig.addCount(SandboxAction.PRODUCT, application!!, true)
                TaskSendInfo::class.newInstanceFromJSON(data.data)?.let { taskData -> taskServer.uploadTaskData(taskData) }
            }

            ExecType.REPORT_SEARCH_RESULT -> {
                sandboxConfig.addCount(SandboxAction.SEARCH, application!!, true)
                TaskSendInfo::class.newInstanceFromJSON(data.data)?.let { taskData -> taskServer.uploadTaskData(taskData) }
            }

            ExecType.REPORT_SHOP_PRODUCT_LIST -> {
                sandboxConfig.addCount(SandboxAction.SHOP_LIST, application!!, true)
                TaskSendInfo::class.newInstanceFromJSON(data.data)?.let { taskData -> taskServer.uploadTaskData(taskData) }
            }

            else -> null
        }
        return ExecResult(data.type, result?.toJSON())
    }

    open suspend fun setSandboxConfig(sandbox: SandboxConfig, appConfigInfo: AppConfigInfo) {

    }

    open fun setAccountInfo(accountShadeInfo: AccountShadeInfo) {

    }

    open fun uploadTaskResult(action: TaskAction, clear: Boolean) {

    }

    open fun makeProductInfo(task: MergeTask,shopId: String?, itemId: String?, json: String): TaskSendInfo? {
        if (shopId.isNullOrBlank() || itemId.isNullOrBlank()) {
            logger.error("makeProductInfo error: shopId $shopId itemId $itemId")
            return null
        }
        val sourceSite = subModule.sourceSite
        val frameConfig = sourceSite.detailFrame
        logger.info("frameConfig: $frameConfig")
        val taskId = task.taskId
        val url = "https://${sourceSite.value}/product/${shopId}/${itemId}"
        logger.info("create task send extra: $taskId $url")

        val extra = HashMap<String, Any>()
        extra["url"] = url
        extra["source_site"] = sourceSite.value
        extra["shop_id"] = shopId
        extra["item_id"] = itemId
        return task.makeTaskSendInfo(taskId, frameConfig.pid, json, extra, isDirectional = true)
    }

    fun makeSearchResultInfo(task: MergeTask,page: Int, json: String): TaskSendInfo? {
        val sourceSite = subModule.sourceSite
        val processorID = sourceSite.searchGoodsFrame.pid
        return task.makeTaskSendInfo(task.taskId, processorID, json,task.taskInfo.params?: HashMap(), page)
    }

    fun makeDetailRecommendResultInfo(task: MergeTask, json: String): TaskSendInfo? {
        val sourceSite = subModule.sourceSite
        val processorID = sourceSite.detailRecommendFrame.pid
        return task.makeTaskSendInfo(task.taskId, processorID, json,task.taskInfo.params?: HashMap())
    }

    fun makeHotScaleListInfo(task: MergeTask, shopId: String?, json: String): TaskSendInfo? {
        if (shopId.isNullOrBlank()) {
            logger.error("makeHotScaleListInfo error: shopId $shopId")
            return null
        }
        val sourceSite = subModule.sourceSite
        val taskId = "${sourceSite.hotScalesFrame.pid}_${UUID.randomUUID().toString().replace("-","")}"
        val extra = HashMap<String, Any>()
        extra["source_site"] = sourceSite.value
        extra["source_shop_id"] = shopId
        extra["input"] = shopId
        val frameConfig = sourceSite.hotScalesFrame
        return task.makeTaskSendInfo(taskId, frameConfig.pid, json,task.taskInfo.params?: HashMap())
    }
}