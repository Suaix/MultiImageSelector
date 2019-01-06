package com.summer.mutiimageselector;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.summer.imageselector.MultiImageSelector;
import com.summer.imageselector.data.ImageInfo;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<ImageInfo> selectedImageList;
    private TextView tvResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvResult = findViewById(R.id.tv_result);
        findViewById(R.id.bt_launch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MultiImageSelector.with()
                        .maxImageSize(9)
                        .selectedImageList(selectedImageList)
                        .build()
                        .startActivityForResult(MainActivity.this, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            selectedImageList = extras.getParcelableArrayList(MultiImageSelector.RESULT_MULTI_DATA);
            tvResult.setText(selectedImageList.toString());
        }
    }
}
