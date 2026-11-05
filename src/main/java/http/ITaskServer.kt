package http

import com.lpsjcx.channel.data.ITaskSendInfo
import com.lpsjcx.channel.data.RespTaskResult
import com.lpsjcx.channel.data.TaskInfoData
import com.lpsjcx.channel.data.TaskLoadInfo
import com.lpsjcx.channel.data.TaskResp
import com.bhh.tools.http.BaseResult
import com.bhh.tools.http.IHttpServer

interface ITaskServer: IHttpServer {

    suspend fun loadTask(param: TaskLoadInfo): RespTaskResult?

    suspend fun uploadTaskData(param: ITaskSendInfo): BaseResult<TaskResp>?

    suspend fun request(data: String): BaseResult<String>?
}