package com.abner.automatic.controller;

import android.text.TextUtils;
import android.util.Log;

import com.abner.automatic.App;
import com.abner.automatic.model.AppInfo;
import com.abner.automatic.util.AppUtils;
import com.abner.automatic.util.JsonUtils;
import com.abner.automatic.util.SharedPreUtils;
import com.yanzhenjie.andserver.annotation.GetMapping;
import com.yanzhenjie.andserver.annotation.PostMapping;
import com.yanzhenjie.andserver.annotation.RequestMapping;
import com.yanzhenjie.andserver.annotation.RequestParam;
import com.yanzhenjie.andserver.annotation.RestController;

import org.json.JSONArray;

import java.util.List;

/**
 * AUTHOR:AbnerMing
 * DATE:2022/6/21
 * INTRODUCE:手机App控制器
 */
@RestController
@RequestMapping(path = "/app")
public class PhoneAppController {

    /**
     * AUTHOR:AbnerMing
     * INTRODUCE:获取手机应用信息
     */
    @GetMapping(path = "/appList")
    List<AppInfo> getAppList() {
        return AppUtils.getAppUtils().getAppInfo(1);
    }

    /**
     * AUTHOR:AbnerMing
     * INTRODUCE:存储脚本信息
     */
    @PostMapping(path = "/save")
    void saveAppScript(
            @RequestParam(name = "appScript") String appScript,
            @RequestParam(name = "appKey") String appKey
    ) {
        SharedPreUtils.put(App.getInstance(), "script_" + appKey, appScript);
    }

    /**
     * AUTHOR:AbnerMing
     * INTRODUCE:获取脚本信息
     */
    @GetMapping(path = "/appScript")
    String getAppScript(@RequestParam(name = "appKey") String appKey) {
        String json = SharedPreUtils.getString(App.getInstance(), "script_" + appKey);
        if (!TextUtils.isEmpty(json)) {
            return json;
        } else {
            return JsonUtils.failedJson(400, "没有找到对应的脚本数据");
        }
    }


    /**
     * AUTHOR:AbnerMing
     * INTRODUCE:开启程序
     */
    @PostMapping(path = "/startApp")
    void startScript(
            @RequestParam(name = "startApp") String startApp,
            @RequestParam(name = "appScript") String appScript,
            @RequestParam(name = "appPack") String appPack
    ) {
        App.Companion.setMAppToastMessage("");
        App.Companion.setMAppScriptMessage("");
        App.Companion.setMStartApp(startApp);
        App.Companion.setMAppPack(appPack);
        App.Companion.setMAppScript(appScript);

    }

    /**
     * AUTHOR:AbnerMing
     * INTRODUCE:当前进度消息
     */
    @GetMapping(path = "/getScriptMessage")
    String getAppScriptMessage() {
        return App.Companion.getMAppScriptMessage();
    }

    /**
     * AUTHOR:AbnerMing
     * INTRODUCE:提示消息
     */
    @GetMapping(path = "/getToastMessage")
    String getAppToastMessage() {
        return App.Companion.getMAppToastMessage();
    }

}
