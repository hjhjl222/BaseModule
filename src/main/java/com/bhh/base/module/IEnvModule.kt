package com.bhh.base.module

import com.lpsjcx.channel.common.ICheckResult
import com.lpsjcx.channel.common.SuccessCheckResult
import com.lpsjcx.channel.data.AppConfigInfo
import com.lpsjcx.channel.data.SandboxConfig
import com.bhh.tools.coroutines.ClientDispatcher
import com.bhh.tools.coroutines.GlobalScheduler
import com.bhh.tools.timer.delay
import com.bhh.tools.util.asSeconds

abstract class AbsEnvModule(name: IModuleDefine): MainModule<SubModule>(name) {

    var checkResult: ICheckResult = SuccessCheckResult
        protected set

    protected val checkers = mutableListOf<IChecker>()

    suspend fun isOk(tag: String): Boolean {
        waitInit()
        return checkEnv(tag, false) == SuccessCheckResult
    }

    override suspend fun onInit() {
        super.onInit()
        startLoop()
    }

    private fun startLoop() = ClientDispatcher.launch(GlobalScheduler.Check) {
        while (true) {
            delay(20.asSeconds())
            if (!isNeedCheck()) {
                continue
            }
            startCheck()
        }
    }

    protected open suspend fun isNeedCheck() = true

    protected suspend fun startCheck() = onWaitForAction {
        var checkResult = checkEnv("自动检测", true)
        if (checkResult == SuccessCheckResult) {
            checkResult = loopCheckers()
        }
        logger.info("环境检测结果: $checkResult")
        this.checkResult = checkResult
        checkResultCallback(checkResult)
    }

    protected open suspend fun checkResultCallback(checkResult: ICheckResult) {

    }

    protected suspend fun loopCheckers(): ICheckResult {
        for (i in 0 until checkers.size) {
            val checkResult = checkers[i].check(getAppConfigInfo(), getSandboxConfig())
            if (checkResult != SuccessCheckResult) {
                return checkResult
            }
        }
        return SuccessCheckResult
    }

    protected abstract fun getSandboxConfig(): SandboxConfig?

    protected abstract fun getAppConfigInfo(): AppConfigInfo?

    fun addChecker(checker: IChecker) {
        if (checkers.contains(checker)) {
            return
        }
        checkers.add(checker)
    }

    fun removeChecker(checker: IChecker): Boolean {
        return checkers.remove(checker)
    }

    protected abstract suspend fun checkEnv(params: String, autoStart: Boolean): ICheckResult

    interface IChecker {
        suspend fun check(appConfigInfo: AppConfigInfo?, sandbox: SandboxConfig?): ICheckResult
    }
}