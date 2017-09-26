package com.yl.facedetector;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
        Button viewDataButton = (Button) findViewById(R.id.view_data);

        registerButton.setOnClickListener(this);
        viewDataButton.setOnClickListener(this);
        initDatabase();
    }

    // 初始化数据库, FaceRecognizer匹配人脸要求训练数大于2
    private void initDatabase() {
        DatabaseHelper helper = new DatabaseHelper(this);
        if (helper.query().size() == 0) {
            int[] defaultUsers = {R.drawable.user1, R.drawable.user2};
            for (int i = 0; i < defaultUsers.length; i++) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                        defaultUsers[i]);
                String path = helper.saveBitmapToLocal(bitmap);
                UserInfo user = new UserInfo("默认用户" + (i + 1), "男", 25, path);
                helper.insert(user);
            }
        }
        helper.close();
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
            case R.id.view_data:
                startActivity(new Intent(MainActivity.this, ViewDataActivity.class));
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
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.requestPermissionResult(requestCode, grantResults);
    }
}
