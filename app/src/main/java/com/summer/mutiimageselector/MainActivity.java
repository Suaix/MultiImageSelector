package com.summer.mutiimageselector;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.summer.imageselector.MultiImageSelector;
import com.summer.imageselector.data.ImageInfo;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<ImageInfo> selectedImageList;
    private RecyclerView recyclerView;
    private ImageResultAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.rv_result);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new ImageResultAdapter();
        recyclerView.setAdapter(adapter);
        findViewById(R.id.bt_launch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MultiImageSelector.with(MainActivity.this, MultiImageSelector.MULTI_MODE)
                        .maxImageSize(9)
                        .selectedImageList(selectedImageList)
                        .registerImageLoader(new GlideImageLoader())
                        .build()
                        .show(1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            selectedImageList = extras.getParcelableArrayList(MultiImageSelector.RESULT_MULTI_DATA);
            adapter.setList(selectedImageList);
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            ImageInfo imageInfo = extras.getParcelable(MultiImageSelector.RESULT_SINGLE_DATA);
            if (selectedImageList == null){
                selectedImageList = new ArrayList<ImageInfo>();
            }
            selectedImageList.add(imageInfo);
            adapter.setList(selectedImageList);
        }
    }
}
