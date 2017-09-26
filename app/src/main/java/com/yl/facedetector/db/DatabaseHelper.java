package com.yl.facedetector.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库辅助类
 * Created by Luke on 2017/8/21.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String CREATE_FACE_TABLE = "create table face_data (" +
            "id integer primary key autoincrement, " +
            "name text, " +
            "sex text, " +
            "age int, " +
            "path text)";
    private SQLiteDatabase db;
    private Context mContext;

    public DatabaseHelper(Context context) {
        super(context, "database", null, 1);
        db = getWritableDatabase();
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_FACE_TABLE);
        Log.d(TAG, "onCreate: " + "table created...");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public String saveBitmapToLocal(Bitmap bitmap) {
        int userNum = db.query("face_data",
                null, null, null, null, null, null).getCount() + 1;
        try {
            String filePath = mContext.getFilesDir() + "/face" + userNum + ".png";
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

    public List<UserInfo> query() {
        Cursor cursor = db.query("face_data", null, null, null, null, null, null);
        List<UserInfo> userList = new ArrayList<>();
        if (cursor.moveToNext()) {
            do {
                UserInfo user = new UserInfo();
                user.setName(cursor.getString(cursor.getColumnIndex("name")));
                user.setSex(cursor.getString(cursor.getColumnIndex("sex")));
                user.setAge(cursor.getInt(cursor.getColumnIndex("age")));
                user.setPath(cursor.getString(cursor.getColumnIndex("path")));
                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return userList;
    }

    public void insert(UserInfo userInfo) {
        ContentValues values = new ContentValues();
        values.put("name", userInfo.getName());
        values.put("sex", userInfo.getSex());
        values.put("age", userInfo.getAge());
        values.put("path", userInfo.getPath());
        db.insert("face_data", null, values);
        values.clear();
    }

    public void close() {
        db.close();
    }
}
