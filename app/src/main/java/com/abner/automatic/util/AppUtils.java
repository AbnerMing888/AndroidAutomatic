package com.abner.automatic.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.SparseIntArray;

import com.abner.automatic.model.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * AUTHOR:AbnerMing
 * DATE:2022/6/21
 * INTRODUCE:
 */
public class AppUtils {

    private Context mContext;

    private AppUtils() {
    }

    private static AppUtils mAppUtils;

    public static AppUtils getAppUtils() {
        if (mAppUtils == null) {
            mAppUtils = new AppUtils();
        }
        return mAppUtils;
    }

    public void initContext(Context context) {
        mContext = context;
    }


    // 获取已安装的应用信息队列
    public ArrayList<AppInfo> getAppInfo(int type) {
        ArrayList<AppInfo> appList = new ArrayList<>();
        SparseIntArray siArray = new SparseIntArray();
        // 获得应用包管理器
        PackageManager pm = mContext.getPackageManager();
        // 获取系统中已经安装的应用列表
        @SuppressLint("WrongConstant")
        List<ApplicationInfo> installList = pm.getInstalledApplications(
                PackageManager.PERMISSION_GRANTED);
        for (int i = 0; i < installList.size(); i++) {
            ApplicationInfo item = installList.get(i);

            // 去掉重复的应用信息
            if (siArray.indexOfKey(item.uid) >= 0) {
                continue;
            }
            // 往siArray中添加一个应用编号，以便后续的去重校验
            siArray.put(item.uid, 1);
            try {
                // 获取该应用的权限列表
                String[] permissions = pm.getPackageInfo(item.packageName,
                        PackageManager.GET_PERMISSIONS).requestedPermissions;
                if (permissions == null) {
                    continue;
                }

                boolean isQueryNetwork = false;
                for (String permission : permissions) {
                    // 过滤那些具备上网权限的应用
                    if (permission.equals("android.permission.INTERNET")) {
                        isQueryNetwork = true;
                        break;
                    }
                }
                // 类型为0表示所有应用，为1表示只要联网应用
                if (type == 0 || (type == 1 && isQueryNetwork)) {
                    AppInfo app = new AppInfo();
                    app.uid = item.uid; // 获取应用的编号
                    app.label = item.loadLabel(pm).toString(); // 获取应用的名称
                    app.package_name = item.packageName; // 获取应用的包名
                    //Drawable drawable = item.loadIcon(pm); // 获取应用的图标

                    if (!app.label.contains(".") && !app.label.contains(" ")) {
                        appList.add(app);
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
        return appList;  // 返回去重后的应用包队列
    }
}
