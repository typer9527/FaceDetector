package com.yl.facedetector.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.yl.facedetector.R;
import com.yl.facedetector.adapter.UserAdapter;
import com.yl.facedetector.db.DatabaseHelper;
import com.yl.facedetector.db.UserInfo;

import java.util.List;

public class ViewDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DatabaseHelper helper = new DatabaseHelper(this);
        List<UserInfo> users = helper.query();
        helper.close();
        recyclerView.setAdapter(new UserAdapter(users));
    }
}
