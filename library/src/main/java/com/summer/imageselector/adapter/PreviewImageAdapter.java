package com.summer.imageselector.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.summer.imageselector.data.ImageInfo;
import com.summer.library.R;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * 预览图片适配器
 */
public class PreviewImageAdapter extends RecyclerView.Adapter<PreviewImageAdapter.PreviewImageHolder> {

    private List<ImageInfo> imageInfoList;
    private Activity mActivity;

    public PreviewImageAdapter(Activity activity) {
        mActivity = activity;
    }

    @NonNull
    @Override
    public PreviewImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PreviewImageHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_preview_image, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PreviewImageHolder holder, int position) {
        ImageInfo imageInfo = imageInfoList.get(position);
        Glide.with(mActivity)
                .load(new File(imageInfo.getPath()))
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageInfoList == null ? 0 : imageInfoList.size();
    }

    /**
     * 获取指定位置的图片信息
     * @param position
     * @return
     */
    public ImageInfo getItem(int position) {
        if (imageInfoList == null)
            return null;
        return imageInfoList.get(position);
    }

    public void setImageInfoList(List<ImageInfo> imageInfoList) {
        this.imageInfoList = imageInfoList;
        notifyDataSetChanged();
    }

    public void onMove(int fromPostion, int targetPosition) {
        Collections.swap(imageInfoList, fromPostion, targetPosition);
        notifyItemMoved(fromPostion, targetPosition);
    }

    protected static class PreviewImageHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public PreviewImageHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
        }
    }
}
