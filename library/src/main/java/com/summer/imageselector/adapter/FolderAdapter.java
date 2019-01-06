package com.summer.imageselector.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.summer.imageselector.data.FolderInfo;
import com.summer.imageselector.data.ImageInfo;
import com.summer.library.R;

import java.io.File;
import java.util.List;

/**
 * 文件夹列表适配器
 */
public class FolderAdapter extends BaseAdapter {

    private List<FolderInfo> folderInfoList;
    private int lastSelectedIndex = 0;

    @Override
    public int getCount() {
        return folderInfoList != null ? folderInfoList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return folderInfoList == null ? null : folderInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        Context context = viewGroup.getContext();
        FolderHolder holder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_folder_layout, viewGroup, false);
            holder = new FolderHolder();
            holder.ivFolderCover = view.findViewById(R.id.iv_folder_image);
            holder.tvFolderName = view.findViewById(R.id.tv_folder_name);
            holder.tvFolderDes = view.findViewById(R.id.tv_folder_image_des);
            holder.ivFolderStatus = view.findViewById(R.id.iv_folder_select_status);
            view.setTag(holder);
        } else {
            holder = (FolderHolder) view.getTag();
        }
        FolderInfo folderInfo = folderInfoList.get(position);
        Glide.with(holder.ivFolderCover)
                .load(new File(folderInfo.getCover().getPath()))
                .into(holder.ivFolderCover);
        holder.tvFolderName.setText(folderInfo.getName());
        List<ImageInfo> imageInfoList = folderInfo.getImageInfoList();
        int imageSize = imageInfoList == null ? 0 : imageInfoList.size();
        holder.tvFolderDes.setText(String.format(context.getString(R.string.folder_des), imageSize));
        if (position == lastSelectedIndex) {
            holder.ivFolderStatus.setImageResource(R.drawable.ic_folder_selected);
        } else {
            holder.ivFolderStatus.setImageResource(0);
        }
        return view;
    }

    public void setFolderInfoList(List<FolderInfo> folderInfoList) {
        if (folderInfoList != null) {
            this.folderInfoList = folderInfoList;
            notifyDataSetChanged();
        }
    }

    public void setLastSelectedIndex(int index) {
        this.lastSelectedIndex = index;
    }

    static class FolderHolder {
        public ImageView ivFolderCover;
        public TextView tvFolderName;
        public TextView tvFolderDes;
        public ImageView ivFolderStatus;
    }
}
