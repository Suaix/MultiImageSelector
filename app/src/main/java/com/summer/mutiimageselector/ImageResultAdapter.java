package com.summer.mutiimageselector;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.summer.imageselector.data.ImageInfo;
import com.summer.library.Utils;

import java.io.File;
import java.util.List;

public class ImageResultAdapter extends RecyclerView.Adapter<ImageResultAdapter.ImageHolder> {

    private List<ImageInfo> list;

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result_image, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, int position) {
        ImageInfo imageInfo = list.get(position);
        Glide.with(holder.imageView).load(new File(imageInfo.getPath())).into(holder.imageView);
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = false;
//        int imageSize = Utils.getScreenSize(holder.imageView.getContext()).x / 3;
//        int sampleSize;
//        if (imageInfo.getWidth() / imageSize > imageInfo.getHeight() / imageSize){
//            sampleSize = imageInfo.getWidth() / imageSize;
//        } else {
//            sampleSize = imageInfo.getHeight() / imageSize;
//        }
//        Log.i("xia", "sampleSize="+sampleSize+", imageInfo:"+imageInfo.toString()+", imageSize:"+imageSize);
//        if (sampleSize < 1){
//            sampleSize = 1;
//        }
//        options.inSampleSize = sampleSize;
//        Bitmap bitmap = BitmapFactory.decodeFile(imageInfo.getPath(), options);
//        holder.imageView.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void setList(List<ImageInfo> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    protected static class ImageHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ImageHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.siv_result_image);
        }
    }
}
