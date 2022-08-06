package com.abner.automatic

import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.abner.automatic.accessibility.MingUtils
import com.abner.automatic.accessibility.WUtils
import com.abner.automatic.util.NetUtils
import java.util.*


/**
 *AUTHOR:AbnerMing
 *DATE:2022/6/22
 *INTRODUCE:主页
 */
class HomeActivity : AppCompatActivity() {
    private var mServerManager: ServerManager? = null
    private var mTvMessage: TextView? = null
    private var mBtnStart: Button? = null
    private var mRootUrl: String? = null
    private var mIsStart = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mTvMessage = findViewById(R.id.tv_message)
        mBtnStart = findViewById(R.id.btn_start)

        mServerManager = ServerManager(this)
        mServerManager?.register()

        if (NetUtils.getNetworkAvailableType(this) != 0) {
            mTvMessage?.setText(R.string.no_wlan)
        }

        mBtnStart!!.setOnClickListener {
            if (mIsStart) {
                startServer()
            } else {
                mServerManager?.stopServer()
            }

        }

    }

    /**
     * AUTHOR:AbnerMing
     * INTRODUCE:启动服务
     */
    private fun startServer() {
        if (NetUtils.getNetworkAvailableType(this) != 0) {
            Toast.makeText(this, "远程管理需要连接WLAN", Toast.LENGTH_SHORT).show()
            return
        }
        if (!MingUtils.isAccessibilitySettingsOn(this)) {
            WUtils.goAccess(this)
            return
        }
        mServerManager!!.startServer()
    }

    fun onServerStart(ip: String) {
        if (!TextUtils.isEmpty(ip)) {
            val addressList: MutableList<String?> = LinkedList()
            mRootUrl = "http://$ip:9999/"
            addressList.add("请保证手机和电脑连接的是同一个网络")
            addressList.add("请在PC端浏览器输入:")
            addressList.add(mRootUrl)
            mTvMessage!!.text = TextUtils.join("\n", addressList)
            mIsStart = false
            mBtnStart?.setText(R.string.stop_server)
        } else {
            mRootUrl = null
            mTvMessage!!.setText(R.string.server_ip_error)
        }
    }

    fun onServerError(message: String) {
        mRootUrl = null
        mTvMessage!!.text = message
    }

    fun onServerStop() {
        mRootUrl = null
        mTvMessage!!.setText(R.string.server_stop_succeed)
        mIsStart = true
        mBtnStart?.setText(R.string.start_server)
    }

    override fun onDestroy() {
        super.onDestroy()
        mServerManager!!.unRegister()
        mServerManager!!.stopServer()
    }
}