package com.summer.imageselector.presenter;

import com.summer.imageselector.data.FolderInfo;

/**
 * 处理图片数据的接口定义
 */
public interface IImagePresenter {
    /**
     * 加载图片数据
     */
    void loadImageData();

    /**
     * 根据文件夹重新加载图片数据
     * @param folderInfo 文件夹信息
     */
    void reloadDataByFolder(FolderInfo folderInfo);
}
