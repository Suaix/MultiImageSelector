package com.summer.library;

import android.util.Log;
import android.widget.ImageView;

import com.summer.imageselector.data.ImageInfo;

/**
 * 本地图片加载器
 */
public class LocalImageLoader implements IImageLoader{

    @Override
    public void loadBitmap(ImageInfo imageInfo, final ImageView imageView) {
        Log.i("xia", "LocalImageLoader load bitmap **********************");
        LoaderRequest request = new LoaderRequest(imageInfo, imageView);
        request.load();
    }

}
