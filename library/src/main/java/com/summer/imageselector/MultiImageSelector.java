package com.summer.imageselector;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.summer.imageselector.data.ImageInfo;
import com.summer.library.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 多图选择器，这里配置图片选择模式
 */
public class MultiImageSelector {
    /**
     * 单选模式，选择一张图片后即返回
     */
    public static final int SINGLE_MODE = 1 << 0;
    /**
     * 多图选择模式，可选择多张图片，具体数量参考 {@link #maxImageSize}
     */
    public static final int MULTI_MODE = 1 << 1;
    /**
     * 单选模式下数据返回的结果
     */
    public static final String RESULT_SINGLE_DATA = "result_single_data";
    /**
     * 多选模式下数据返回的结果
     */
    public static final String RESULT_MULTI_DATA = "result_multi_data";
    /**
     * 选择模式，默认是多图
     */
    private int mSelectMode;
    /**
     * 多图模式下能选择图片的最大数量，单选模式下无效
     */
    private int maxImageSize;
    /**
     * 已经选择过的图片列表
     */
    private ArrayList<ImageInfo> selectedImageList;

    private MultiImageSelector(Builder builder) {
        this.mSelectMode = builder.selectMode;
        this.maxImageSize = builder.maxImageSize;
        this.selectedImageList = builder.selectedImageList;
    }

    /**
     * 启动图片选择器
     *
     * @param context     启动的页面
     * @param requestCode 请求码，用来处理返回的结果
     */
    public void startActivityForResult(Activity context, int requestCode) {
        Intent intent = new Intent(context, MultiImageSelectorActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("selectMode", mSelectMode);
        bundle.putInt("maxImageSize", maxImageSize);
        bundle.putParcelableArrayList("selectedImageList", selectedImageList);
        intent.putExtras(bundle);
        context.startActivityForResult(intent, requestCode);
    }

    /**
     * 是否有读取存储卡的权限
     *
     * @param context Context
     * @return true：有，false：无
     */
    private boolean hasReadStoragePermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static Builder with() {
        return new Builder();
    }

    public static Builder with(int selectMode) {
        return new Builder(selectMode);
    }

    public static class Builder {
        private int selectMode = MULTI_MODE;
        private int maxImageSize = 9;
        private ArrayList<ImageInfo> selectedImageList;

        public Builder() {
        }

        public Builder(int seletMode) {
            this.selectMode = seletMode;
        }

        public Builder maxImageSize(int maxImageSize) {
            this.maxImageSize = maxImageSize;
            return this;
        }

        public Builder selectedImageList(ArrayList<ImageInfo> selectedImageList) {
            this.selectedImageList = selectedImageList;
            return this;
        }

        public MultiImageSelector build() {
            return new MultiImageSelector(this);
        }
    }
}
