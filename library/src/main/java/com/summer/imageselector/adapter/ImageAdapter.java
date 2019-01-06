package com.summer.imageselector.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.summer.imageselector.MultiImageSelector;
import com.summer.imageselector.data.ImageInfo;
import com.summer.library.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片adapter
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageHolder> {
    private List<ImageInfo> imageInfoList;
    private ArrayList<ImageInfo> selectedImageList;

    private Fragment mFragment;
    private int mSelectMode;
    private int mMaxImageSize;

    private OnImageClickListener mListener;

    private int lastSelectedIndex = 0;

    public ImageAdapter(Fragment fragment, int selectMode, int maxImageSize) {
        mFragment = fragment;
        mSelectMode = selectMode;
        mMaxImageSize = maxImageSize;
    }

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull final ImageHolder holder, int position) {
        final ImageInfo imageInfo = imageInfoList.get(position);
        if (imageInfo != null) {
            Glide.with(mFragment).load(new File(imageInfo.getPath())).into(holder.imageView);
        }

        final int itemPosition = position;
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onImageClick(itemPosition);
                }
            }
        });
        if (mSelectMode == MultiImageSelector.SINGLE_MODE) {
            holder.tvImageIndex.setVisibility(View.GONE);
            return;
        }
        if (selectedImageList != null && selectedImageList.size() > 0) {
            if (selectedImageList.contains(imageInfo)) {
                ImageInfo selectedImageInfo = selectedImageList.get(selectedImageList.indexOf(imageInfo));
                imageInfo.setIndex(selectedImageInfo.getIndex());
                updateImageSelectStatus(holder, imageInfo);
            } else {
                updateImageSelectStatus(holder, imageInfo);
            }
        }
        holder.flImageIndex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageInfo.getIndex() > 0) {
                    //当前图片为选中状态，将其置为未选中态，同时同步其他选中图片顺序；
                    lastSelectedIndex--;
                    int index = imageInfo.getIndex();
                    imageInfo.setIndex(0);
                    selectedImageList.remove(imageInfo);
                    syncSelectedImageIndex(index);
                    updateImageSelectStatus(holder, imageInfo);
                } else {
                    if (selectedImageList != null && selectedImageList.size() >= mMaxImageSize) {
                        String notiMsg = String.format(mFragment.getContext().getString(R.string.cannot_select_more_image), mMaxImageSize);
                        Toast.makeText(mFragment.getContext(), notiMsg, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //当前是未选中态，将其置为选中态
                    lastSelectedIndex++;
                    imageInfo.setIndex(lastSelectedIndex);
                    if (selectedImageList == null) {
                        selectedImageList = new ArrayList<>();
                    }
                    selectedImageList.add(imageInfo);
                    updateImageSelectStatus(holder, imageInfo);
                }
                if (mListener != null) {
                    mListener.onImageSelectedChanged(selectedImageList.size());
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            Object payload = payloads.get(0);
            if (payload instanceof Integer) {
                int index = (int) payload;
                holder.tvImageIndex.setText(String.valueOf(index));
            }
        }
    }

    /**
     * 同步选中图片的序列
     *
     * @param removedIndex 被移除的序列，小于该序列的图片的序列不变，大于该序列的图片的序列减一
     */
    private void syncSelectedImageIndex(int removedIndex) {
        for (ImageInfo info : selectedImageList) {
            int index = info.getIndex();
            if (index > removedIndex) {
                int targetIndex = index - 1;
                info.setIndex(targetIndex);
                int position = imageInfoList.indexOf(info);
                imageInfoList.get(position).setIndex(targetIndex);
                notifyItemChanged(position, targetIndex);
            }
        }
    }

    @Override
    public int getItemCount() {
        return imageInfoList != null ? imageInfoList.size() : 0;
    }

    private void updateImageSelectStatus(ImageHolder holder, ImageInfo imageInfo) {
        if (imageInfo.getIndex() > 0) {
            holder.tvImageIndex.setBackgroundResource(R.drawable.bg_circle_green);
            holder.tvImageIndex.setText(String.valueOf(imageInfo.getIndex()));
        } else {
            holder.tvImageIndex.setBackgroundResource(R.drawable.bg_circle_white);
            holder.tvImageIndex.setText("");
        }
    }

    /**
     * 设置原图片信息
     *
     * @param imageInfoList 原图片信息
     */
    public void setImageInfoList(List<ImageInfo> imageInfoList) {
        this.imageInfoList = imageInfoList;
        notifyDataSetChanged();
    }

    /**
     * 设置已选中图片信息
     *
     * @param selectedImageList 已选图片信息
     */
    public void setSelectedImageList(ArrayList<ImageInfo> selectedImageList) {
        if (selectedImageList != null) {
            this.selectedImageList = selectedImageList;
            lastSelectedIndex = this.selectedImageList.size();
        }
    }

    /**
     * 设置图片点击监听
     *
     * @param listener 图片点击监听器
     */
    public void setOnImageClickListener(OnImageClickListener listener) {
        mListener = listener;
    }

    /**
     * 获取指定位置的图片数据
     *
     * @param position 位置
     * @return ImageInfo
     */
    public ImageInfo getItem(int position) {
        if (position >= 0 && position < imageInfoList.size()) {
            return imageInfoList.get(position);
        }
        return null;
    }

    public ArrayList<ImageInfo> getSelectedImages() {
        return selectedImageList;
    }

    public interface OnImageClickListener {
        /**
         * 点击了图片
         *
         * @param position 图片位置
         */
        void onImageClick(int position);

        /**
         * 点击了选择框
         *
         * @param selectedSize 选中图片的数量
         */
        void onImageSelectedChanged(int selectedSize);
    }

    protected static class ImageHolder extends RecyclerView.ViewHolder {
        AppCompatImageView imageView;
        TextView tvImageIndex;
        View flImageIndex;

        public ImageHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.siv_image);
            tvImageIndex = itemView.findViewById(R.id.tv_image_selected_index);
            flImageIndex = itemView.findViewById(R.id.fl_image_select);
        }
    }
}
