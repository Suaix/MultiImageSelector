package com.summer.imageselector.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.summer.imageselector.data.ImageInfo;
import com.summer.library.R;

import java.io.File;
import java.util.ArrayList;

/**
 * 预览页面已选则图片列表适配器
 */
public class PreviewSelectedImageAdapter extends RecyclerView.Adapter<PreviewSelectedImageAdapter.ViewHolder> {

    private ArrayList<ImageInfo> mSelectedImageList;

    private ImageInfo currentPreviewImage;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_preview_selected_image, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageInfo imageInfo = mSelectedImageList.get(position);
        Log.i("xia", "onBindViewHolder, imageInfo:/n" + imageInfo.toString());
        Glide.with(holder.ivImage).load(new File(imageInfo.getPath())).into(holder.ivImage);
        if (currentPreviewImage != null && currentPreviewImage.getId() == imageInfo.getId()) {
            holder.selectedMark.setVisibility(View.VISIBLE);
        } else {
            holder.selectedMark.setVisibility(View.GONE);
        }
    }

    /**
     * 移动item位置
     *
     * @param fromPosition 起始位置
     * @param toPosition   目标位置
     */
    public void onMove(int fromPosition, int toPosition) {
        //对原数据进行移动
//        Collections.swap(mSelectedImageList, fromPosition, toPosition);
        ImageInfo fromImageInfo = mSelectedImageList.get(fromPosition);
        fromImageInfo.setIndex(toPosition + 1);
        ImageInfo toImageInfo = mSelectedImageList.get(toPosition);
        toImageInfo.setIndex(fromPosition+1);
        //通知数据移动
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public int getItemCount() {
        return mSelectedImageList == null ? 0 : mSelectedImageList.size();
    }

    /**
     * 设置选择图片列表
     *
     * @param mSelectedImageList 图片列表
     */
    public void setSelectedImageList(ArrayList<ImageInfo> mSelectedImageList) {
        this.mSelectedImageList = mSelectedImageList;
        notifyDataSetChanged();
    }

    /**
     * 设置当前预览的图片信息
     *
     * @param currentPreviewImage 当前预览的图片信息
     */
    public void setCurrentPreviewImage(ImageInfo currentPreviewImage) {
        this.currentPreviewImage = currentPreviewImage;
        Log.i("xia", "setCurrentPreviewImage:" + currentPreviewImage.toString());
        notifyDataSetChanged();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        View selectedMark;
        ImageView ivImage;

        public ViewHolder(View itemView) {
            super(itemView);
            selectedMark = itemView.findViewById(R.id.image_selected);
            ivImage = itemView.findViewById(R.id.iv_image);
        }
    }
}
