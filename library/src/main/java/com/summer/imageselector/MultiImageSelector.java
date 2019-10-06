package com.summer.imageselector;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.summer.imageselector.data.ImageInfo;
import com.summer.library.IImageLoader;
import com.summer.library.LocalImageLoader;

import java.util.ArrayList;

/**
 * Author: Summer
 * Date: 2019-05-26
 * Package: com.summer.imageselector
 * Des: 多图选择器，通过该选择器配置图片选择属性并启动页面
 **/
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
    private Activity activity;
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

    public static IImageLoader imageLoader;

    private MultiImageSelector(Builder builder) {
        this.activity = builder.activity;
        this.mSelectMode = builder.selectMode;
        this.maxImageSize = builder.maxImageSize;
        this.selectedImageList = builder.selectedImageList;
        if (builder.imageLoader != null){
            imageLoader = builder.imageLoader;
        } else {
            imageLoader = new LocalImageLoader();
        }
    }

    /**
     * 启动图片选择器
     *
     * @param requestCode 请求码，用来处理返回的结果
     */
    public void show(int requestCode) {
        Intent intent = new Intent(activity, MultiImageSelectorActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("selectMode", mSelectMode);
        bundle.putInt("maxImageSize", maxImageSize);
        bundle.putParcelableArrayList("selectedImageList", selectedImageList);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, requestCode);
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

    public static Builder with(Activity activity) {
        return new Builder(activity);
    }

    public static Builder with(Activity activity, int selectMode) {
        return new Builder(activity, selectMode);
    }
    /**
     * Author: Summer
     * Date: 2019-05-26
     * Package: com.summer.imageselector
     * Des: 图片选择器的构建者
     **/
    public static class Builder {
        private Activity activity;
        private int selectMode = MULTI_MODE;
        private int maxImageSize = 9;
        private ArrayList<ImageInfo> selectedImageList;
        private IImageLoader imageLoader;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder(Activity activity, int seletMode) {
            this.activity = activity;
            this.selectMode = seletMode;
        }

        /**
         * 选择图片的最大个数，默认最大选择9张图
         * @param maxImageSize
         * @return
         */
        public Builder maxImageSize(int maxImageSize) {
            this.maxImageSize = maxImageSize;
            return this;
        }

        /**
         * 已选择的图片集合
         * @param selectedImageList
         * @return
         */
        public Builder selectedImageList(ArrayList<ImageInfo> selectedImageList) {
            this.selectedImageList = selectedImageList;
            return this;
        }

        /**
         * 注册图片加载器，可以将项目现有的图片加载库实现{@link IImageLoader}，默认使用{@link LocalImageLoader}加载图片
         * 这里允许注册自己的图片加载器是为了避免重复导入三方图片加载库，默认的是使用BitmapFactory加载本地图片
         * @param imageLoader
         * @return
         */
        public Builder registerImageLoader(IImageLoader imageLoader){
            this.imageLoader = imageLoader;
            return this;
        }

        public MultiImageSelector build() {
            return new MultiImageSelector(this);
        }
    }
}
