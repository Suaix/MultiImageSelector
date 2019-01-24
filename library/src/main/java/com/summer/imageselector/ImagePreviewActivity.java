package com.summer.imageselector;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.TextView;

import com.summer.imageselector.adapter.PreviewImageAdapter;
import com.summer.imageselector.adapter.PreviewSelectedImageAdapter;
import com.summer.imageselector.data.FolderInfo;
import com.summer.imageselector.data.ImageInfo;
import com.summer.imageselector.fragment.Callback;
import com.summer.imageselector.presenter.IImagePresenter;
import com.summer.imageselector.presenter.LocalImagePresenterImp;
import com.summer.library.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 图片预览页面
 */
public class ImagePreviewActivity extends AppCompatActivity implements Callback {
    private RecyclerView mPreviewRecyclerView;
    private RecyclerView mSelectedRecyclerView;
    private TextView tvIndex;
    private TextView tvOk;
    private TextView tvSelect;
    /**
     * 图片预览适配器
     */
    private PreviewImageAdapter previewImageAdapter;
    /**
     * 选中图片的适配器
     */
    private PreviewSelectedImageAdapter selectedImageAdapter;
    /**
     * 当前预览图片的角标
     */
    private int currentPreviewIndex;
    /**
     * 最大图片选择数量
     */
    private int maxSelectedCount;
    /**
     * 是否需要加载所有图片
     */
    private boolean needLoadAllImages;
    /**
     * 当前已选中的图片列表
     */
    private ArrayList<ImageInfo> mSelectedImageList;
    /**
     * 图片加载的Presenter
     */
    private IImagePresenter mPresenter;
    /**
     * 预览图片列表的总大小
     */
    private int totalPreviewImageSize = 0;

    /**
     * 启动图片预览页面
     *
     * @param activity           调用页面Activity
     * @param requestCode        请求码
     * @param selectedImageList  当前已经选中的图片列表
     * @param currentSelectIndex 当前预览图片的角标，该角标为在大图预览列表里的角标
     * @param maxSelectedCount   最大选择图片数
     * @param loadAllImages      是否需要加载所有图片，当预览所有本地图片时传递true，预览选中的图片时传false
     */
    public static void launch(Activity activity, int requestCode, ArrayList<ImageInfo> selectedImageList, int currentSelectIndex, int maxSelectedCount, boolean loadAllImages) {
        Intent intent = new Intent(activity, ImagePreviewActivity.class);
        intent.putParcelableArrayListExtra("selected_images", selectedImageList);
        intent.putExtra("current_selected_index", currentSelectIndex);
        intent.putExtra("max_selected_count", maxSelectedCount);
        intent.putExtra("load_all_images", loadAllImages);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(Color.WHITE);
        }
        setContentView(R.layout.activity_image_preview);
        initView();
        initData();
        addListener();
    }

    /**
     * 添加监听
     */
    private void addListener() {
        tvSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        Intent intent = getIntent();
        mSelectedImageList = intent.getParcelableArrayListExtra("selected_images");
        currentPreviewIndex = intent.getIntExtra("current_selected_index", 0);
        maxSelectedCount = intent.getIntExtra("max_selected_count", 9);
        needLoadAllImages = intent.getBooleanExtra("load_all_images", false);
        if (mSelectedImageList == null) {
            mSelectedImageList = new ArrayList<>();
        }

        selectedImageAdapter.setSelectedImageList(mSelectedImageList);

        if (needLoadAllImages) {
            LoaderManager loaderManager = getSupportLoaderManager();
            mPresenter = new LocalImagePresenterImp(this, loaderManager, this);
            mPresenter.loadImageData();
        } else {
            previewImageAdapter.setImageInfoList(mSelectedImageList);
            int size = mSelectedImageList.size();
            totalPreviewImageSize = size;
            tvIndex.setText(String.format(getString(R.string.index_of_total), currentPreviewIndex + 1, size));
            if (size > 0) {
                tvOk.setText(String.format(getString(R.string.image_selected_result), size, maxSelectedCount));
            }
            if (currentPreviewIndex > 0) {
                mPreviewRecyclerView.scrollToPosition(currentPreviewIndex);
            }
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mPreviewRecyclerView = findViewById(R.id.rv_preview_image);
        LinearLayoutManager previewLayoutManager = new LinearLayoutManager(this);
        previewLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mPreviewRecyclerView.setLayoutManager(previewLayoutManager);
        previewImageAdapter = new PreviewImageAdapter(this);
        mPreviewRecyclerView.setAdapter(previewImageAdapter);
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper() {
            @Override
            public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
                int targetSnapPosition = super.findTargetSnapPosition(layoutManager, velocityX, velocityY);
                onPagerPositionChanged(targetSnapPosition);
                return targetSnapPosition;
            }
        };
        pagerSnapHelper.attachToRecyclerView(mPreviewRecyclerView);

        mSelectedRecyclerView = findViewById(R.id.rv_selected_images);
        LinearLayoutManager selectLayoutManager = new LinearLayoutManager(this);
        selectLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mSelectedRecyclerView.setLayoutManager(selectLayoutManager);
        selectedImageAdapter = new PreviewSelectedImageAdapter();
        mSelectedRecyclerView.setAdapter(selectedImageAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.END) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int fromPostion = viewHolder.getAdapterPosition();
                int targetPosition = target.getAdapterPosition();
                if (selectedImageAdapter != null) {
                    selectedImageAdapter.onMove(fromPostion, targetPosition);
                }
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });
        touchHelper.attachToRecyclerView(mSelectedRecyclerView);

        tvIndex = findViewById(R.id.tv_image_preview_back);
        tvOk = findViewById(R.id.tv_preview_ok);
        tvSelect = findViewById(R.id.tv_select_image);
    }

    /**
     * 当图片位置发生变化时，变更图片顺序
     *
     * @param position
     */
    private void onPagerPositionChanged(int position) {
        if (position >= totalPreviewImageSize)
            return;
        tvIndex.setText(String.format(getString(R.string.index_of_total), position + 1, totalPreviewImageSize));
        ImageInfo item = previewImageAdapter.getItem(position);
        selectedImageAdapter.setCurrentPreviewImage(item);
    }

    @Override
    public void setImageInfoList(List<ImageInfo> images) {
        if (images == null)
            return;
        totalPreviewImageSize = images.size();
        previewImageAdapter.setImageInfoList(images);
        tvIndex.setText(String.format(getString(R.string.index_of_total), currentPreviewIndex + 1, images.size()));
        int selectedImageSize = mSelectedImageList.size();
        if (selectedImageSize > 0) {
            tvOk.setText(String.format(getString(R.string.image_selected_result), selectedImageSize, maxSelectedCount));
        }
        if (currentPreviewIndex > 0) {
            mPreviewRecyclerView.scrollToPosition(currentPreviewIndex);
        }
    }

    @Override
    public void setFolderInfoList(List<FolderInfo> mImageFolders) {

    }
}