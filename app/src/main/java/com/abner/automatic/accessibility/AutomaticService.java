package com.abner.automatic.accessibility;

import android.app.Notification;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import com.abner.automatic.App;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * AUTHOR:AbnerMing
 * DATE:2022/6/24
 * INTRODUCE:无障碍执行
 */
public class AutomaticService extends BaseAccessibilityService {
    private String TAG = AutomaticService.class.getName();
    private Timer mTimer;
    private TimerTask mTimerTask;
    private String mPackName = "com.abner.automatic";

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.e(TAG, "onServiceConnected");

        mTimer = new Timer();
        // 创建计时器任务
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                startAppScript();
            }
        };
        mTimer.schedule(mTimerTask, 0, 500);
    }

    /**
     * AUTHOR:AbnerMing
     * INTRODUCE:执行脚本信息
     */
    public void startAppScript() {
        String startApp = App.Companion.getMStartApp();
        if (!TextUtils.isEmpty(startApp)) {
            isPackage = true;
            indexPosition = 0;
            App.Companion.setMStartApp("");
            String appPack = App.Companion.getMAppPack();
            String appScript = App.Companion.getMAppScript();
            //证明调用了执行,只执行一次
            if (mPackName.equals(appPack)) {
                //是直接执行脚本
                implementScript(appScript);
            } else {
                //不是，就打开App
                MingUtils.startActivity(this, appPack);
                //两秒后进入到循环
                timeHandler.sendEmptyMessageDelayed(1000, 2000);
            }
        }
    }

    /**
     * AUTHOR:AbnerMing
     * INTRODUCE:执行脚本
     */
    private int scriptSize = 0;//总的脚本个数
    private String[] mScript;

    private void implementScript(String appScript) {
        mScript = appScript.split("\n");

        scriptSize = mScript.length;
        //一步一步去执行
        eachList.clear();//清除内部循环数据


        Log.e(TAG, "-a==程序开始执行……");

        String s = mScript[indexPosition];

        eachScriptList(s, 0);

    }

    /**
     * AUTHOR:AbnerMing
     * INTRODUCE:遍历脚本信息
     */
    private int indexPosition;
    private int eachNumber = 0;//循环次数
    private int eachPosition = 0;//循环标记
    private boolean eachStart = false;//循环开始标记
    private int eachIndex = 0;//下标
    private ArrayList<String> eachList = new ArrayList<>();

    private void eachScriptList(String s, int position) {

        if (eachStart) {
            //程序中是否存在循环，有就存储
            eachList.add(s);
        }

        indexPosition = position;
        if (indexPosition < scriptSize) {
            ifScript(s);
        }
    }

    /**
     * AUTHOR:AbnerMing
     * INTRODUCE:判断脚本
     */
    private void ifScript(String s) {
        //小于就执行程序
        if (s.contains("点击") || s.contains("ck")) {
            try {

                String click = "";
                if (s.contains("点击")) {
                    click = s.replace("点击", "");
                } else {
                    click = s.replace("ck", "");
                }
                //判断是否存在
                WUtils.findTextAndClick(this, click, new CallBackListener() {
                    @Override
                    public void success() {
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        App.Companion.setMAppScriptMessage(indexPosition + "==" + s);
                        Log.e(TAG, indexPosition + "==" + s);
                        containsContent();

                    }

                    @Override
                    public void fail() {
                        App.Companion.setMAppScriptMessage("-c==【" + s + "】未找到当前程序");
                        Log.e(TAG, "-c==【" + s + "】未找到当前程序");
                        //执行发生，错误之后,继续执行下一个逻辑
                        containsContent();

                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                App.Companion.setMAppScriptMessage("-c==【" + s + "】语法错误啊老铁");
                Log.e(TAG, "-c==【" + s + "】语法错误啊老铁");
            }

        } else if (s.contains("控件") || s.contains("vw")) {

            String input = "";
            if (s.contains("控件")) {
                input = s.replace("控件", "");
            } else {
                input = s.replace("vw", "");
            }

            AccessibilityNodeInfo info = findViewByID(App.Companion.getMAppPack() + ":id/" + input);
            WUtils.performClick(info);
            App.Companion.setMAppScriptMessage(indexPosition + "==" + s);
            Log.e(TAG, indexPosition + "==" + s);
            containsContent();

        } else if (s.contains("触摸") || s.contains("th")) {
            String input = "";
            if (s.contains("触摸")) {
                input = s.replace("触摸", "");
            } else {
                input = s.replace("th", "");
            }
            String[] split = input.split("=");

            float x = Float.parseFloat(split[0]);
            float y = Float.parseFloat(split[1]);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                dispatchClick(x, y);
            }
            App.Companion.setMAppScriptMessage(indexPosition + "==" + s);
            Log.e(TAG, indexPosition + "==" + s);
            containsContent();

        } else if (s.contains("输入") || s.contains("it")) {
            try {
                String input = "";
                if (s.contains("输入")) {
                    input = s.replace("输入", "");
                } else {
                    input = s.replace("it", "");
                }
                String[] split = input.split("=");
                AccessibilityNodeInfo info = findViewByID(App.Companion.getMAppPack() + ":id/" + split[0]);
                inputText(info, split[1]);
                containsContent();

            } catch (Exception e) {
                e.printStackTrace();
                App.Companion.setMAppScriptMessage("-c==【" + s + "】语法错误啊老铁");
                Log.e(TAG, "-c==【" + s + "】语法错误啊老铁");
            }


        } else if (s.contains("下拉") || s.contains("dn")) {
            scrollPage(1);
            App.Companion.setMAppScriptMessage(indexPosition + "==" + s);
            Log.e(TAG, indexPosition + "==" + s);
            containsContent();
        } else if (s.contains("上滑") || s.contains("up")) {
            scrollPage(0);
            App.Companion.setMAppScriptMessage(indexPosition + "==" + s);
            Log.e(TAG, indexPosition + "==" + s);
            containsContent();
        } else if (s.contains("左滑") || s.contains("lt")) {
            scrollPage(3);
            App.Companion.setMAppScriptMessage(indexPosition + "==" + s);
            Log.e(TAG, indexPosition + "==" + s);
            containsContent();
        } else if (s.contains("右滑") || s.contains("rt")) {
            scrollPage(2);
            App.Companion.setMAppScriptMessage(indexPosition + "==" + s);
            Log.e(TAG, indexPosition + "==" + s);
            containsContent();
        } else if (s.contains("返回") || s.contains("bk")) {
            WUtils.performBack(this);
            App.Companion.setMAppScriptMessage(indexPosition + "==" + s);
            Log.e(TAG, indexPosition + "==" + s);
            containsContent();
        } else if (s.contains("循环开始") || s.contains("et")) {
            eachStart = true;
            //获取循环次数
            String input = "";
            if (s.contains("循环开始")) {
                input = s.replace("循环开始", "");
            } else {
                input = s.replace("et", "");
            }
            try {
                eachNumber = Integer.parseInt(input) - 1;
                App.Companion.setMAppScriptMessage(indexPosition + "==" + s);
                Log.e(TAG, indexPosition + "==" + s);
                containsContent();
            } catch (Exception e) {
                e.printStackTrace();
                App.Companion.setMAppScriptMessage("-c==【" + s + "】语法错误啊老铁");
                Log.e(TAG, "-c==【" + s + "】语法错误啊老铁");
            }

        } else if (s.contains("循环结束") || s.contains("ep")) {
            eachStart = false;//停止再添加数据
            //获取到循环结束,判断是否循环已经完毕，没有就继续执行
            Log.e(TAG, "内部循环开始，集合数量" + eachList.size() + "===" + eachNumber);

            for (int i = 0; i < eachNumber; i++) {
                for (int j = 0; j < eachList.size(); j++) {
                    String si = eachList.get(j);
                    if (si.contains("上滑") || si.contains("up")) {
                        scrollPage(0);
                    } else if (s.contains("左滑") || si.contains("lt")) {
                        scrollPage(3);
                    } else if (s.contains("右滑") || si.contains("rt")) {
                        scrollPage(2);
                    } else if (si.contains("点击") || si.contains("ck")) {
                        //判断间隔多少秒
                        String click = "";
                        if (s.contains("点击")) {
                            click = s.replace("点击", "");
                        } else {
                            click = s.replace("ck", "");
                        }

                        //判断是否存在
                        WUtils.findTextAndClick(this, click, new CallBackListener() {
                            @Override
                            public void success() {
                                containsContent();
                            }

                            @Override
                            public void fail() {
                                App.Companion.setMAppScriptMessage("-c==【" + si + "】未找到当前程序");
                                Log.e(TAG, "-c==【" + si + "】未找到当前程序");
                                //执行发生，错误之后,继续执行下一个逻辑
                                containsContent();
                            }
                        });
                    }
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //最后一个
                if (i == eachNumber - 1) {
                    App.Companion.setMAppScriptMessage(indexPosition + "==" + s);
                    Log.e(TAG, i + "=====" + (eachNumber - 1) + "====" + indexPosition + "==" + s);
                    containsContent();
                }

            }

        }
    }


    private void containsContent() {
        if (indexPosition == scriptSize - 1) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
            App.Companion.setMAppScriptMessage("-b==程序执行完毕……");
            Log.e(TAG, "-b==程序执行完毕……");
        } else {
            indexPosition += 1;
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
            eachScriptList(mScript[indexPosition], indexPosition);//执行下一个
        }
    }


    /**
     * AUTHOR:AbnerMing
     * INTRODUCE:直到包名一致后进入到脚本执行
     */
    private boolean isPackage = true;

    private void loopPackage() {
        String appPack = App.Companion.getMAppPack();
        String appScript = App.Companion.getMAppScript();
        Log.e(TAG, "当前打开的应用包名：" + mPackName + "==" + appPack);
        if (mPackName.equals(appPack)) {
            //包名一致，那么执行程序
            implementScript(appScript);
        } else {
            if (isPackage) {
                isPackage = false;
                //包名不一致 进行打开
                homeClick();
                MingUtils.startActivity(this, appPack);
                timeHandler.sendEmptyMessageDelayed(1000, 5000);
            } else {
                timeHandler.sendEmptyMessageDelayed(1000, 1000);
            }

        }
    }


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        super.onAccessibilityEvent(event);

        String pack = event.getPackageName().toString();
        if (!pack.contains("com.android")) {

        }
        mPackName = event.getPackageName().toString();
        Log.e(TAG, "event >> TYPE:" + event.getEventType());
        Log.e(TAG, mPackName);

        if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            //获取事件具体信息
            Parcelable parcelable = event.getParcelableData();
            //如果是下拉通知栏消息
            if (parcelable instanceof Notification) {
                //处理通知栏消息

            } else {
                //其它通知信息，包括Toast
                String toastMsg = (String) event.getText().get(0);
                App.Companion.setMAppToastMessage("提示：" + toastMsg);

            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimerTask.cancel();
        mTimer.cancel();
    }

    private Handler timeHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1000) {
                loopPackage();
            }
        }
    };

}
