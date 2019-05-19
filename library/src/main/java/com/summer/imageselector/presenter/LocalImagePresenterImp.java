package com.summer.imageselector.presenter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.summer.imageselector.data.FolderInfo;
import com.summer.imageselector.data.ImageInfo;
import com.summer.imageselector.fragment.Callback;
import com.summer.library.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 本地图片数据处理实现类
 */
public class LocalImagePresenterImp implements IImagePresenter, LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * 加载所有图片数据的id
     */
    private final int ID_LOAD_ALL = 0;
    /**
     * 加载指定目录图片数据的id
     */
    private final int ID_LOAD_FOLDER = 1;

    private Context mContext;
    /**
     * 数据库加载管理器
     */
    private LoaderManager mLoaderManager;
    private Callback mCallback;

    private final String[] IMAGE_PROJECTION = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT};
    /**
     * 是否已经生成过文件夹列表
     */
    private boolean hasGenerateFolders = false;

    private List<FolderInfo> mImageFolders = new ArrayList<>();

    public LocalImagePresenterImp(Context context, LoaderManager loaderManager, Callback callback) {
        mContext = context;
        mLoaderManager = loaderManager;
        mCallback = callback;
    }

    /**
     * 加载图片数据
     */
    @Override
    public void loadImageData() {
        if (mLoaderManager == null) {
            return;
        }
        mLoaderManager.initLoader(ID_LOAD_ALL, null, this);
    }

    @Override
    public void reloadDataByFolder(FolderInfo folderInfo) {
        if (folderInfo.getId() == -1) {
            //加载所有的图片
            mLoaderManager.restartLoader(ID_LOAD_ALL, null, this);
        } else {
            //加载单个文件夹
            List<ImageInfo> imageInfoList = folderInfo.getImageInfoList();
            mCallback.onImageListResult(imageInfoList);
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        CursorLoader cursorLoader = null;
        if (id == ID_LOAD_ALL) {
            cursorLoader = new CursorLoader(mContext,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                    IMAGE_PROJECTION[5] + ">0 AND " + IMAGE_PROJECTION[4] + "=? OR " + IMAGE_PROJECTION[4] + "=? " + "OR " + IMAGE_PROJECTION[4] + " =? ",
                    new String[]{"image/jpeg", "image/png", "image/jpg"}, IMAGE_PROJECTION[3] + " DESC");
        } else if (id == ID_LOAD_FOLDER) {
            cursorLoader = new CursorLoader(mContext,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                    IMAGE_PROJECTION[5] + ">0 AND " + IMAGE_PROJECTION[1] + " like '%" + args.getString("path") + "%'",
                    null, IMAGE_PROJECTION[3] + " DESC");
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            if (data.getCount() > 0) {
                List<ImageInfo> images = new ArrayList<>();
                data.moveToFirst();
                do {
                    String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                    if (!fileExist(path)) {
                        continue;
                    }
                    int id = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                    String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                    long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[3]));
                    int width = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[6]));
                    int height = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[7]));

                    ImageInfo image = null;
                    if (!TextUtils.isEmpty(name)) {
                        image = new ImageInfo(id, path, dateTime, width, height);
                        images.add(image);
                    }
                    if (!hasGenerateFolders) {
                        File folderFile = new File(path).getParentFile();
                        if (folderFile != null && folderFile.exists()) {
                            String fp = folderFile.getAbsolutePath();
                            FolderInfo f = getFolder(fp);
                            if (f == null) {
                                FolderInfo folder = new FolderInfo();
                                folder.setName(folderFile.getName());
                                folder.setPath(fp);
                                folder.setCover(image);
                                List<ImageInfo> imageList = new ArrayList<>();
                                imageList.add(image);
                                folder.setImageInfoList(imageList);
                                mImageFolders.add(folder);
                            } else {
                                f.getImageInfoList().add(image);
                            }
                        }
                    }

                } while (data.moveToNext());

                mCallback.onImageListResult(images);

                if (!hasGenerateFolders) {
                    if (mImageFolders != null && mImageFolders.size() > 0) {
                        FolderInfo allFolderInfo = new FolderInfo();
                        FolderInfo firstFolderInfo = mImageFolders.get(0);
                        allFolderInfo.setName(mContext.getString(R.string.all_images));
                        allFolderInfo.setCover(firstFolderInfo.getCover());
                        allFolderInfo.setId(-1);
                        mImageFolders.add(0, allFolderInfo);
                        mCallback.onFolderInfoListResult(mImageFolders);
                    }
                    hasGenerateFolders = true;
                }
            }
        }
    }

    private FolderInfo getFolder(String path) {
        for (FolderInfo folderInfo : mImageFolders) {
            if (TextUtils.equals(folderInfo.getPath(), path)) {
                return folderInfo;
            }
        }
        return null;
    }

    private boolean fileExist(String path) {
        if (!TextUtils.isEmpty(path)) {
            return new File(path).exists();
        }
        return false;
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
