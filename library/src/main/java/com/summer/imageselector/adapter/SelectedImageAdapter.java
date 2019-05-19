package com.summer.imageselector.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.summer.imageselector.ImagePreviewActivity;
import com.summer.imageselector.data.ImageInfo;
import com.summer.library.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * 预览页面已选则图片列表适配器
 */
public class SelectedImageAdapter extends RecyclerView.Adapter<SelectedImageAdapter.ImageHolder> {
    /**
     * 选中图片列表
     */
    private ArrayList<ImageInfo> mSelectedImageList;
    /**
     * 当前选中图片的角标，初始值为-1（即未选中任何图片）
     */
    private int currentSelectedIndex = -1;

    private OnImageItemClickListener listener;

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_preview_selected_image, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, final int position) {
        ImageInfo imageInfo = mSelectedImageList.get(position);
        Glide.with(holder.ivImage).load(new File(imageInfo.getPath())).into(holder.ivImage);
        if (position == currentSelectedIndex) {
            holder.selectedMark.setVisibility(View.VISIBLE);
        } else {
            holder.selectedMark.setVisibility(View.GONE);
        }
        holder.ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemSelected(position);
                if (listener != null){
                    listener.onImageItemClick(position);
                }
            }
        });
    }

    /**
     * 移动item位置
     *
     * @param fromPosition 起始位置
     * @param toPosition   目标位置
     */
    public void onMove(int fromPosition, int toPosition) {
        //对原数据进行移动
        ImageInfo fromImageInfo = mSelectedImageList.get(fromPosition);
        fromImageInfo.setIndex(toPosition + 1);
        ImageInfo toImageInfo = mSelectedImageList.get(toPosition);
        toImageInfo.setIndex(fromPosition+1);
        Collections.swap(mSelectedImageList, fromPosition, toPosition);
        if (currentSelectedIndex == fromPosition){
            //如果要移动的图片也是当前选中的图片，需要将当前选中的位置也修改为移动后位置
            currentSelectedIndex = toPosition;
        }
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
     * @param index 选中图片的角标
     */
    public void notifyItemSelected(int index) {
        if (index == currentSelectedIndex){
            //角标不合法或者没有变化，不做操作
            return;
        }
        //先将当前选中的位置设置为未选中态
        notifyItemChanged(currentSelectedIndex, false);
        if (index >= 0 && index < mSelectedImageList.size()){
            //再将新的角标位置选中
            notifyItemChanged(index, true);
        }
        currentSelectedIndex = index;
    }

    public void setOnItemClickListener(OnImageItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnImageItemClickListener{
        void onImageItemClick(int position);
    }

    protected static class ImageHolder extends RecyclerView.ViewHolder {

        View selectedMark;
        ImageView ivImage;

        public ImageHolder(View itemView) {
            super(itemView);
            selectedMark = itemView.findViewById(R.id.image_selected);
            ivImage = itemView.findViewById(R.id.iv_image);
        }
    }
}
