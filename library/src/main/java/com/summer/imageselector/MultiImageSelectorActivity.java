package com.summer.imageselector;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
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
        if (hasReadStoragePermission()){
            loadImageListFragment();
        } else {
            requestReadStoragePermission();
        }
    }

    /**
     * 加载图片列表Fragment
     */
    private void loadImageListFragment() {
        Bundle bundle = getIntent().getExtras();
        mImageFragement = ImageListFragment.newInstance(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.fl_fragment_container, mImageFragement).commitAllowingStateLoss();
    }

    /**
     * 请求读取sdk卡的权限
     */
    private void requestReadStoragePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.tip_request_permission)
                    .setMessage(R.string.des_request_permissin)
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MultiImageSelectorActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                            dialog.dismiss();
                        }
                    }).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
    }

    /**
     * 判断是否有读取sdk的权限
     * @return
     */
    private boolean hasReadStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mImageFragement.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            loadImageListFragment();
        } else {
            finish();
        }
    }
}
