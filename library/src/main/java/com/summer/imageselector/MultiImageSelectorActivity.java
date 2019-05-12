package com.summer.imageselector;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.summer.imageselector.fragment.ImageListFragment;
import com.summer.library.R;

/**
 * 多图选择Activity
 */
public class MultiImageSelectorActivity extends AppCompatActivity {

    private ImageListFragment mImageFragement;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21){
            getWindow().setNavigationBarColor(Color.GRAY);
        }
        setContentView(R.layout.activity_multi_selector_container);
        Bundle bundle = getIntent().getExtras();
        mImageFragement = ImageListFragment.newInstance(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.fl_fragment_container, mImageFragement).commitAllowingStateLoss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mImageFragement.onActivityResult(requestCode, resultCode, data);
    }
}
