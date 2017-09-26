package com.yl.facedetector.util;

import android.graphics.Bitmap;
import android.util.Log;

import com.yl.facedetector.db.UserInfo;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * 人脸匹配
 * Created by Luke on 2017/8/22.
 */

public class FaceMatcher {

    private static final String TAG = "FaceMatcher";
    private static int counter;
    public final int UNFINISHED = -2;
    public final int NO_MATCHER = -1;
    private final int MAX_COUNTER = 45;
    private final double MY_SIMILARITY = 0.8;
    private List<String> mPathList;

    public FaceMatcher(List<UserInfo> users) {
        counter = 0;
        mPathList = new ArrayList<>();
        for (UserInfo user : users) {
            mPathList.add(user.getPath());
        }
    }

    public int histogramMatch(Bitmap bitmap) {
        if (counter < MAX_COUNTER) {
            Mat testMat = new Mat();
            Utils.bitmapToMat(bitmap, testMat);
            // 转灰度矩阵
            Imgproc.cvtColor(testMat, testMat, Imgproc.COLOR_RGB2GRAY);
            // 把矩阵的类型转换为Cv_32F，因为在c++代码中会判断类型
            testMat.convertTo(testMat, CvType.CV_32F);
            for (int i = 0; i < mPathList.size(); i++) {
                String path = mPathList.get(i);
                Mat mat = Imgcodecs.imread(path);
                Imgproc.resize(mat, mat, new Size(320, 320));
                Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY);
                mat.convertTo(mat, CvType.CV_32F);
                // 直方图比较
                double similarity = Imgproc.compareHist(mat, testMat,
                        Imgproc.CV_COMP_CORREL);
                Log.e(TAG, "histogramMatch: " + similarity);
                if (similarity >= MY_SIMILARITY) {
                    Log.e(TAG, "histogramMatch: " + similarity + ", " + i);
                    return i;
                }
                if (similarity < MY_SIMILARITY && i == mPathList.size() - 1) {
                    Log.e(TAG, "histogramMatch: " + counter);
                    counter++;
                }
            }
            return UNFINISHED;
        } else {
            Log.e(TAG, "histogramMatch: 匹配结束");
            return NO_MATCHER;
        }
    }

}
