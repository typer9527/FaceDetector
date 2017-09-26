package com.yl.facedetector;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_face.FaceRecognizer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.javacpp.opencv_core.CV_32SC1;
import static org.bytedeco.javacpp.opencv_face.createFisherFaceRecognizer;
import static org.bytedeco.javacpp.opencv_imgcodecs.CV_LOAD_IMAGE_GRAYSCALE;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;

/**
 * 人脸匹配
 * Created by Luke on 2017/8/1.
 */

public class MyFaceRecognizer {

    private static final String TAG = "MyFaceRecognizer";
    private static int counter;
    private final static int MAX_COUNTER = 30;
    private Context mContext;
    private List<String> mPathList;

    public MyFaceRecognizer(Context context, List<UserInfo> users) {
        mContext = context;
        counter = 0;
        mPathList = new ArrayList<>();
        for (UserInfo user : users) {
            mPathList.add(user.getPath());
        }
    }

    private String saveTempBitmap(Bitmap bitmap) {
        try {
            String filePath = mContext.getFilesDir() + "/face_temp.png";
            File file = new File(filePath);
            OutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            return filePath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void recognise(Bitmap bitmap) {
        if (counter < MAX_COUNTER) {
            MatVector images = new MatVector(mPathList.size());
            Mat labels = new Mat(mPathList.size(), 1, CV_32SC1);
            IntBuffer labelsBuf = labels.createBuffer();
            for (int i = 0; i < mPathList.size(); i++) {
                String path = mPathList.get(i);
                Mat img = imread(path, CV_LOAD_IMAGE_GRAYSCALE);
                images.put(i, img);
                labelsBuf.put(i, i);
            }
            FaceRecognizer faceRecognizer = createFisherFaceRecognizer();
            // FaceRecognizer faceRecognizer = createEigenFaceRecognizer();
            // FaceRecognizer faceRecognizer = createLBPHFaceRecognizer()

            faceRecognizer.train(images, labels);

            String path = saveTempBitmap(bitmap);
            Mat testImage = imread(path, CV_LOAD_IMAGE_GRAYSCALE);
            IntPointer label = new IntPointer(1);
            DoublePointer confidence = new DoublePointer(1);
            faceRecognizer.predict(testImage, label, confidence);
            int predictedLabel = label.get(0);
            if (confidence.get(0) > 10000) {
                Log.d(TAG, "Predicted label: " + predictedLabel);
                Log.d(TAG, "Confidence: " + confidence.get(0));
            } else {
                counter++;
                Log.d(TAG, "counter: " + counter);
                Log.d(TAG, "Predicted label: " + predictedLabel);
                Log.d(TAG, "Confidence: " + confidence.get(0));
            }
        } else {
            Log.d(TAG, "recognise: " + "匹配结束");
        }
    }
}
