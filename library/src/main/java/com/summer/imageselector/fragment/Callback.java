package com.summer.imageselector.fragment;

import com.summer.imageselector.data.FolderInfo;
import com.summer.imageselector.data.ImageInfo;

import java.util.List;

/**
 * 图片数据相关处理回调
 */
public interface Callback {
    /**
     *  当获取到图片列表结果时回调该方法
     * @param images 图片列表数据集合
     */
    void onImageListResult(List<ImageInfo> images);

    /**
     *  当获取到图片文件夹信息时回调该方法
     * @param mImageFolders 图片文件夹列表信息集合
     */
    void onFolderInfoListResult(List<FolderInfo> mImageFolders);
}
