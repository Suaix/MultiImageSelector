package com.summer.imageselector.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 图片信息实体
 */
public class ImageInfo implements Parcelable {
    /**
     * 图片id
     */
    private int id;
    /**
     * 图片的路径
     */
    private String path;
    /**
     * 图片宽度
     */
    private int width;
    /**
     * 图片的高度
     */
    private int height;
    /**
     * 添加的时间
     */
    private long addedTime;
    /**
     * 选中的顺序
     */
    private int index;

    public ImageInfo(int id, String path, long addedTime, int width, int height) {
        this.id = id;
        this.path = path;
        this.addedTime = addedTime;
        this.width = width;
        this.height = height;
    }

    protected ImageInfo(Parcel in) {
        id = in.readInt();
        path = in.readString();
        width = in.readInt();
        height = in.readInt();
        addedTime = in.readLong();
        index = in.readInt();
    }

    public static final Creator<ImageInfo> CREATOR = new Creator<ImageInfo>() {
        @Override
        public ImageInfo createFromParcel(Parcel in) {
            return new ImageInfo(in);
        }

        @Override
        public ImageInfo[] newArray(int size) {
            return new ImageInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(path);
        parcel.writeInt(width);
        parcel.writeInt(height);
        parcel.writeLong(addedTime);
        parcel.writeInt(index);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ImageInfo) {
            ImageInfo imageInfo = (ImageInfo) obj;
            return id == imageInfo.getId();
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "ImageInfo{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", addedTime=" + addedTime +
                ", index=" + index +
                '}';
    }
}
