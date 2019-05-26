package com.summer.library;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.summer.imageselector.data.ImageInfo;

/**
 * @Author: Summer
 * @Date: 2019-05-26
 * @Package: com.summer.library
 * @Des: 加载请求
 **/
public class LoaderRequest implements ViewTreeObserver.OnPreDrawListener {

    private ImageInfo mImageInfo;
    private ImageView mImageView;

    public LoaderRequest(ImageInfo imageInfo, ImageView imageView){
        this.mImageInfo = imageInfo;
        this.mImageView = imageView;
    }

    public void load(){
        BitmapFactory.Options options = new BitmapFactory.Options();
        int imageWidth = mImageInfo.getWidth();
        int imageHeight = mImageInfo.getHeight();
        if (imageWidth == 0 || imageHeight == 0){
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mImageInfo.getPath(), options);
            mImageInfo.setWidth(options.outWidth);
            mImageInfo.setHeight(options.outHeight);
        }

        int width = mImageView.getWidth();
        int height = mImageView.getHeight();
        if (width == 0 || height == 0){
            ViewTreeObserver treeObserver = mImageView.getViewTreeObserver();
            treeObserver.addOnPreDrawListener(this);
        } else {
            Log.i("xia", "xxxxxxxxxxxxxxxxxxxxxx path:"+mImageInfo.getPath());
            realLoadBitmap();
        }
    }

    private void realLoadBitmap() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        int scale=1;
        int imageWidth = mImageInfo.getWidth();
        int imageHeight = mImageInfo.getHeight();
        int width = mImageView.getWidth();
        int height = mImageView.getHeight();
        if (imageWidth > width || imageHeight > height){
            //以最长边做缩放
            if (imageWidth / width > imageHeight / height){
                scale = imageWidth / width;
            } else {
                scale = imageHeight / height;
            }
        }
        Log.i("xia", "image scale:"+scale+", path:"+mImageInfo.getPath());
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(mImageInfo.getPath(), options);
        mImageView.setImageBitmap(bitmap);
    }

    @Override
    public boolean onPreDraw() {
        Log.i("xia", "onPreDraw width:"+mImageView.getWidth()+", height:"+mImageView.getHeight()+", path:"+mImageInfo.getPath());
        realLoadBitmap();
        ViewTreeObserver observer = mImageView.getViewTreeObserver();
        if (observer.isAlive()){
            observer.removeOnPreDrawListener(this);
        }
        return true;
    }
}
