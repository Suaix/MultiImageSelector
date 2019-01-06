package com.summer.imageselector.fragment;

import com.summer.imageselector.data.FolderInfo;
import com.summer.imageselector.data.ImageInfo;

import java.util.List;

/**
 * 图片数据相关处理回调
 */
public interface Callback {
    void setImageInfoList(List<ImageInfo> images);

    void setFolderInfoList(List<FolderInfo> mImageFolders);
}
