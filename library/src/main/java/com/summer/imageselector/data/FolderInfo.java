package com.summer.imageselector.data;

import java.util.List;

/**
 * 图片文件夹信息
 */
public class FolderInfo {
    /**
     * id
     */
    private int id;
    /**
     * 名称
     */
    private String name;
    /**
     * 路径
     */
    private String path;
    /**
     * 封面
     */
    private ImageInfo cover;
    /**
     * 文件夹下图片列表
     */
    private List<ImageInfo> imageInfoList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ImageInfo getCover() {
        return cover;
    }

    public void setCover(ImageInfo cover) {
        this.cover = cover;
    }

    public List<ImageInfo> getImageInfoList() {
        return imageInfoList;
    }

    public void setImageInfoList(List<ImageInfo> imageInfoList) {
        this.imageInfoList = imageInfoList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
