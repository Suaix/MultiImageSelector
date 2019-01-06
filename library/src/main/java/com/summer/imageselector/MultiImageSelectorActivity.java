package com.summer.imageselector;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.summer.imageselector.fragment.ImageListFragment;
import com.summer.library.R;

/**
 * 多图选择Activity
 */
public class MultiImageSelectorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21){
            getWindow().setNavigationBarColor(Color.GRAY);
        }
        setContentView(R.layout.activity_multi_selector_container);
        Bundle bundle = getIntent().getExtras();
        getSupportFragmentManager().beginTransaction().add(R.id.fl_fragment_container, ImageListFragment.newInstance(bundle)).commitAllowingStateLoss();
    }
}
