package com.summer.mutiimageselector;

import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.summer.imageselector.data.ImageInfo;
import com.summer.library.IImageLoader;

import java.io.File;

public class GlideImageLoader implements IImageLoader {
    @Override
    public void loadBitmap(ImageInfo imageInfo, ImageView imageView) {
        Log.i("xia", "user GlideImageLoader load bitmap");
        Glide.with(imageView).load(new File(imageInfo.getPath())).into(imageView);
    }
}
