package com.summer.library;

import android.widget.ImageView;

import com.summer.imageselector.data.ImageInfo;

/**
 * 加载图片的加载器接口
 */
public interface IImageLoader {
    void loadBitmap(ImageInfo imageInfo, ImageView imageView);
}
