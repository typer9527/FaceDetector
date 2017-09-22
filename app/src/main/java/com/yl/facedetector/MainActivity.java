package com.yl.facedetector;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button registerButton = (Button) findViewById(R.id.register);

        registerButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register:
                requestCameraPermission(new PermissionHelper.RequestListener() {
                    @Override
                    public void onGranted() {
                        startActivity(new Intent(MainActivity.this,
                                DetectActivity.class));
                    }

                    @Override
                    public void onDenied() {
                        ToastUtil.showToast(MainActivity.this, "权限拒绝", 0);
                    }
                });
                break;
            default:
                break;
        }
    }

    private void requestCameraPermission(PermissionHelper.RequestListener listener) {
        PermissionHelper.with(MainActivity.this)
                .requestPermission(Manifest.permission.CAMERA)
                .requestCode(1)
                .setListener(listener)
                .request();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.requestPermissionResult(requestCode, grantResults);
    }
}
