package com.summer.imageselector.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.summer.imageselector.ImagePreviewActivity;
import com.summer.imageselector.MultiImageSelector;
import com.summer.imageselector.adapter.FolderAdapter;
import com.summer.imageselector.adapter.ImageAdapter;
import com.summer.imageselector.data.FolderInfo;
import com.summer.imageselector.data.ImageInfo;
import com.summer.imageselector.presenter.IImagePresenter;
import com.summer.imageselector.presenter.LocalImagePresenterImp;
import com.summer.library.R;
import com.summer.library.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 图片列表Fragment
 */
public class ImageListFragment extends Fragment implements Callback, View.OnClickListener, ImageAdapter.OnImageClickListener {

    private TextView mTitleDes;
    private TextView mOkButton;
    private RecyclerView mRecyclerView;
    private TextView mImageFolderDes;
    private TextView mImagePreview;

    private IImagePresenter mPresenter;

    private ImageAdapter imageAdapter;

    private ListPopupWindow mFoloderPopwindow;

    private FolderAdapter folderAdapter;

    private int selectMode;
    private int maxImageSize;
    private ArrayList<ImageInfo> mSelectedImageList;

    private final int PREVIEW_IMAGE_REQUEST_CODE = 1;

    public static ImageListFragment newInstance(Bundle bundle) {
        ImageListFragment fragment = new ImageListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arg = getArguments();
        if (arg != null) {
            selectMode = arg.getInt("selectMode");
            maxImageSize = arg.getInt("maxImageSize");
            mSelectedImageList = arg.getParcelableArrayList("selectedImageList");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_image_list, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mTitleDes = view.findViewById(R.id.tv_title_des);
        mOkButton = view.findViewById(R.id.tv_ok);
        mRecyclerView = view.findViewById(R.id.rv_image_list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        imageAdapter = new ImageAdapter(this, selectMode, maxImageSize);
        imageAdapter.setSelectedImageList(mSelectedImageList);
        mRecyclerView.setAdapter(imageAdapter);
        mRecyclerView.addItemDecoration(new ImageItemDecoration());
        mImageFolderDes = view.findViewById(R.id.tv_image_folder_des);
        mImagePreview = view.findViewById(R.id.tv_image_preview);

        if (selectMode == MultiImageSelector.MULTI_MODE) {
            onImageSelectedChanged(mSelectedImageList != null ? mSelectedImageList.size() : 0);
        }
        createFolderPopwindow();

        mTitleDes.setOnClickListener(this);
        mOkButton.setOnClickListener(this);
        mImageFolderDes.setOnClickListener(this);
        mImagePreview.setOnClickListener(this);
        imageAdapter.setOnImageClickListener(this);
    }

    private void createFolderPopwindow() {
        Point screenSize = Utils.getScreenSize(getContext());
        int height = screenSize.y * 4 / 5;
        mFoloderPopwindow = new ListPopupWindow(getContext());
        mFoloderPopwindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        folderAdapter = new FolderAdapter();
        mFoloderPopwindow.setAdapter(folderAdapter);
        mFoloderPopwindow.setContentWidth(screenSize.x);
        mFoloderPopwindow.setWidth(screenSize.x);
        mFoloderPopwindow.setHeight(height);
        mFoloderPopwindow.setAnchorView(mImageFolderDes);
        mFoloderPopwindow.setModal(true);
        mFoloderPopwindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // 更换文件夹
                FolderInfo item = (FolderInfo) folderAdapter.getItem(position);
                mPresenter.reloadDataByFolder(item);
                folderAdapter.setLastSelectedIndex(position);
                mFoloderPopwindow.dismiss();
                mImageFolderDes.setText(item.getName());
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LoaderManager loaderManager = getActivity().getSupportLoaderManager();
        mPresenter = new LocalImagePresenterImp(getActivity(), loaderManager, this);
        mPresenter.loadImageData();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_title_des) {
            finishImageSelector();
        } else if (id == R.id.tv_ok) {
            completeImageSelect();
        } else if (id == R.id.tv_image_folder_des) {
            showImageFolderPopwindow();
        } else if (id == R.id.tv_image_preview) {
            previewSelectedImages();
        }
    }

    /**
     * 预览选中的图片
     */
    private void previewSelectedImages() {
        ArrayList<ImageInfo> selectedImages = getSortSelectedImageList();
        ImagePreviewActivity.launch(getActivity(), PREVIEW_IMAGE_REQUEST_CODE, selectedImages, 0, maxImageSize, false);
    }

    @NonNull
    private ArrayList<ImageInfo> getSortSelectedImageList() {
        ArrayList<ImageInfo> selectedImages = imageAdapter.getSelectedImages();
        if (selectedImages != null){
            Collections.sort(selectedImages, new Comparator<ImageInfo>() {
                @Override
                public int compare(ImageInfo imageInfo1, ImageInfo imageInfo2) {
                    return imageInfo1.getIndex() - imageInfo2.getIndex();
                }
            });
        }
        return selectedImages;
    }

    /**
     * 展示图片文件夹窗口
     */
    private void showImageFolderPopwindow() {
        if (mFoloderPopwindow == null) {
            createFolderPopwindow();
        }
        if (mFoloderPopwindow.isShowing()) {
            mFoloderPopwindow.dismiss();
        } else {
            mFoloderPopwindow.show();
        }
    }

    /**
     * 完成图片选择，将结果返回并关闭页面
     */
    private void completeImageSelect() {
        ArrayList<ImageInfo> selectedImages = getSortSelectedImageList();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(MultiImageSelector.RESULT_MULTI_DATA, selectedImages);
        setResultAndFinish(bundle);
    }

    /**
     * 结束图片选择操作
     */
    private void finishImageSelector() {
        getActivity().finish();
    }

    @Override
    public void onImageListResult(List<ImageInfo> images) {
        if (imageAdapter != null) {
            imageAdapter.setImageInfoList(images);
        }
    }

    @Override
    public void onFolderInfoListResult(List<FolderInfo> mImageFolders) {
        if (folderAdapter != null) {
            folderAdapter.setFolderInfoList(mImageFolders);
        }
    }

    /**
     * 点击了图片
     *
     * @param position 图片位置
     */
    @Override
    public void onImageClick(int position) {
        if (selectMode == MultiImageSelector.SINGLE_MODE) {
            //单选模式，点击图片直接返回结果
            ImageInfo resultImage = imageAdapter.getItem(position);
            Bundle bundle = new Bundle();
            bundle.putParcelable(MultiImageSelector.RESULT_SINGLE_DATA, resultImage);
            setResultAndFinish(bundle);
        } else {
            //多选模式，点击图片去预览，预览整个图片库
            ArrayList<ImageInfo> sortSelectedImageList = getSortSelectedImageList();
            ImagePreviewActivity.launch(getActivity(), PREVIEW_IMAGE_REQUEST_CODE, sortSelectedImageList, position, maxImageSize, true);
        }
    }

    /**
     * 点击了选择框
     *
     * @param selectedSize 选中图片的数量
     */
    @Override
    public void onImageSelectedChanged(int selectedSize) {
        if (selectedSize > 0) {
            mOkButton.setEnabled(true);
            mOkButton.setText(String.format(getString(R.string.image_selected_result), selectedSize, maxImageSize));
            mImagePreview.setEnabled(true);
            mImagePreview.setText(String.format(getString(R.string.preview_image), selectedSize));
        } else {
            mOkButton.setEnabled(false);
            mOkButton.setText(R.string.completed);
            mImagePreview.setEnabled(false);
            mImagePreview.setText(R.string.preview);
        }
    }

    /**
     * 设置结果并关闭页面
     *
     * @param bundle
     */
    private void setResultAndFinish(Bundle bundle) {
        Intent intent = new Intent();
        intent.putExtras(bundle);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PREVIEW_IMAGE_REQUEST_CODE && data != null){
            ArrayList<ImageInfo> selectedImageList = data.getParcelableArrayListExtra(MultiImageSelector.RESULT_MULTI_DATA);
            if (selectedImageList != null){
                Collections.sort(selectedImageList, new Comparator<ImageInfo>() {
                    @Override
                    public int compare(ImageInfo imageInfo1, ImageInfo imageInfo2) {
                        return imageInfo1.getIndex() - imageInfo2.getIndex();
                    }
                });
                if (resultCode == Activity.RESULT_OK){
                    //在预览页面点击的是完成，将结果返回上个页面
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList(MultiImageSelector.RESULT_MULTI_DATA, selectedImageList);
                    setResultAndFinish(bundle);
                } else if (resultCode == Activity.RESULT_CANCELED){
                    // 在预览页面是点击返回，可能对结果进行了排序或增减
                    mSelectedImageList = selectedImageList;
                    imageAdapter.setSelectedImageList(mSelectedImageList);
                    imageAdapter.notifyDataSetChanged();
                }
            }
        }
    }
}
