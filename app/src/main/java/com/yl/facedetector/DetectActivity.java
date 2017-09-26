package com.yl.facedetector;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class DetectActivity extends AppCompatActivity implements
        CvCameraViewListener2 {
    private static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);
    public final static int FLAG_REGISTER = 1;
    public final static int FLAG_VERIFY = 2;
    private Mat mRgba;
    private Mat mGray;
    private CascadeClassifier mJavaDetector;
    private float mRelativeFaceSize = 0.2f;
    private int mAbsoluteFaceSize = 0;
    private CameraBridgeViewBase mOpenCvCameraView;
    List<UserInfo> userList;
    private Bitmap mDetectedFace;
    private FaceMatcher matcher;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent;
            switch (msg.what) {
                case FLAG_REGISTER:
                    if (mDetectedFace == null) {
                        mDetectedFace = (Bitmap) msg.obj;
                        int result = matcher.histogramMatch(mDetectedFace);
                        if (result == matcher.UNFINISHED) {
                            mDetectedFace = null;
                        } else if (result == matcher.NO_MATCHER) {
//                            intent = new Intent(DetectActivity.this,
//                                    RegisterActivity.class);
//                            intent.putExtra("Face", mDetectedFace);
//                            startActivity(intent);
                            ToastUtil.showToast(DetectActivity.this, "未注册", 0);
                            finish();
                        } else {
                            intent = new Intent();
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    static {
        System.loadLibrary("opencv_java3");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configWindowSetting();
        setContentView(R.layout.activty_detect);

        mOpenCvCameraView = (CameraBridgeViewBase)
                findViewById(R.id.fd_activity_surface_view);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        // 切换前置摄像
        mOpenCvCameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);
        mOpenCvCameraView.setCvCameraViewListener(this);

        init();
        DatabaseHelper helper = new DatabaseHelper(DetectActivity.this);
        userList = helper.query();
        matcher = new FaceMatcher(userList);
        helper.close();
    }

    // 配置摄像机，全屏、横屏和常亮
    private void configWindowSetting() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    private void init() {
        try {
            InputStream is = getResources()
                    .openRawResource(R.raw.lbpcascade_frontalface);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            File cascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
            FileOutputStream os = new FileOutputStream(cascadeFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
            mJavaDetector = new CascadeClassifier(cascadeFile.getAbsolutePath());
            if (mJavaDetector.empty()) {
                mJavaDetector = null;
            }
            cascadeDir.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mOpenCvCameraView.enableView();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onDestroy() {
        super.onDestroy();
        mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
    }

    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();
        // 翻转矩阵以适应前置摄像头
        Core.flip(mRgba, mRgba, 1);
        Core.flip(mGray, mGray, 1);
        // 控制检测矩阵区域和大小
        Rect rect = new Rect(
                new Point(mGray.width() / 2 - 300, mGray.height() / 2 - 300),
                new Size(600, 600));
        mGray = new Mat(mGray, rect);

        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
        }
        MatOfRect faces = new MatOfRect();
        if (mJavaDetector != null) {
            mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2,
                    new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        }
        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++) {
            Point point = new Point(facesArray[i].x + 420, facesArray[i].y + 220);
            facesArray[i] = new Rect(point, facesArray[i].size());
            if (facesArray[i].height > 400 && facesArray[i].height < 500) {
                Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(),
                        FACE_RECT_COLOR, 3);
                // 获取并利用message传递当前检测的人脸
                Mat faceMat = new Mat(mRgba, facesArray[i]);
                Imgproc.resize(faceMat, faceMat, new Size(320, 320));
                Bitmap bitmap = Bitmap.createBitmap(faceMat.width(),
                        faceMat.height(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(faceMat, bitmap);
                Message message = Message.obtain();
                message.what = 1;
                message.obj = bitmap;
                mHandler.sendMessage(message);
            }
        }
        return mRgba;
    }
}
