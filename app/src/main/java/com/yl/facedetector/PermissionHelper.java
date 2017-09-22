package com.yl.facedetector;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * 运行时权限封装
 * Created by Luke on 2017/8/30.
 */

public class PermissionHelper {

    private static final String TAG = "PermissionHelper";
    private Context mContext;
    private String mRequestPermission;
    private static int mRequestCode;
    private static RequestListener mListener;

    private PermissionHelper(Context context) {
        this.mContext = context;
    }

    // 链式传参
    public static PermissionHelper with(Context context) {
        return new PermissionHelper(context);
    }

    public PermissionHelper requestPermission(String permission) {
        this.mRequestPermission = permission;
        return this;
    }

    public PermissionHelper requestCode(int requestCode) {
        this.mRequestCode = requestCode;
        return this;
    }

    public PermissionHelper setListener(RequestListener listener) {
        this.mListener = listener;
        return this;
    }

    // 权限请求主体
    public void request() {
        if (ContextCompat.checkSelfPermission(mContext, mRequestPermission)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) mContext,
                    new String[]{mRequestPermission}, mRequestCode);
        } else {
            mListener.onGranted();
        }
    }

    // 权限请求结果的回调, 在onRequestPermissionsResult(...)调用
    public static void requestPermissionResult(int requestCode, int[] grantResults) {
        if (requestCode == mRequestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.
                    PERMISSION_GRANTED) {
                mListener.onGranted();
            } else {
                mListener.onDenied();
            }
        }
    }

    public interface RequestListener {
        void onGranted();

        void onDenied();
    }
}
