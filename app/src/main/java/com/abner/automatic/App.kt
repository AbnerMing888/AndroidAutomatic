package com.abner.automatic

import android.app.Application
import com.abner.automatic.util.AppUtils

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        mInstance = this
        AppUtils.getAppUtils().initContext(this)
    }

    companion object {
        private var mInstance: App? = null
        var mStartApp = ""//当前脚本是否开启
        var mAppPack = "";//包名
        var mAppScript = "";//脚本信息
        var mAppScriptMessage = ""//当前进度消息
        var mAppToastMessage = ""//提示消息


        @JvmStatic
        val instance: App
            get() = mInstance!!
    }
}