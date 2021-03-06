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
import android.widget.Toast;

import com.summer.imageselector.adapter.PreviewImageAdapter;
import com.summer.imageselector.adapter.SelectedImageAdapter;
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
public class ImagePreviewActivity extends AppCompatActivity implements Callback, View.OnClickListener, SelectedImageAdapter.OnImageItemClickListener {
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
    private SelectedImageAdapter selectedImageAdapter;
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
     * 预览图片列表
     */
    private List<ImageInfo> mPreviewImageList = new ArrayList<>();
    /**
     * 图片加载的Presenter
     */
    private IImagePresenter mPresenter;
    /**
     * 预览图片列表的总大小
     */
    private int totalPreviewImageSize = 0;
    /**
     * 实现选择图片列表拖拽排序
     */
    private ItemTouchHelper.SimpleCallback imageTouchHelperCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.END) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            int fromPostion = viewHolder.getAdapterPosition();
            int targetPosition = target.getAdapterPosition();
            if (selectedImageAdapter != null) {
                selectedImageAdapter.onMove(fromPostion, targetPosition);
            }
            if (previewImageAdapter != null){
                previewImageAdapter.onMove(fromPostion, targetPosition);
            }
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        }

        /**
         * 当item被拖拽或侧滑时调用
         * @param viewHolder
         * @param actionState
         */
        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);
            if (viewHolder != null){
                viewHolder.itemView.setScaleX(1.2f);
                viewHolder.itemView.setScaleY(1.2f);
            }
        }

        /**
         * 当item交互动画结束时触发
         * @param recyclerView
         * @param viewHolder
         */
        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            if (viewHolder != null){
                viewHolder.itemView.setScaleX(1f);
                viewHolder.itemView.setScaleY(1f);
            }
        }
    };

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
            getWindow().setNavigationBarColor(getResources().getColor(R.color.color_2D2D2D));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
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
        tvSelect.setOnClickListener(this);
        tvIndex.setOnClickListener(this);
        tvOk.setOnClickListener(this);
        selectedImageAdapter.setOnItemClickListener(this);
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
        if (!mSelectedImageList.isEmpty()){
            mSelectedRecyclerView.setVisibility(View.VISIBLE);
        }

        if (needLoadAllImages) {
            LoaderManager loaderManager = getSupportLoaderManager();
            mPresenter = new LocalImagePresenterImp(this, loaderManager, this);
            mPresenter.loadImageData();
        } else {
            syncOkStatus();
            if (!mSelectedImageList.isEmpty()){
                mPreviewImageList.addAll(mSelectedImageList);
            }
            previewImageAdapter.setImageInfoList(mPreviewImageList);
            int size = mPreviewImageList.size();
            totalPreviewImageSize = size;
            tvIndex.setText(String.format(getString(R.string.index_of_total), currentPreviewIndex + 1, size));
            if (currentPreviewIndex > 0){
                mPreviewRecyclerView.scrollToPosition(currentPreviewIndex);
            }
            onPagerPositionChanged(currentPreviewIndex);
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        //初始化预览图片RecyclerView，使用PagerSnapHelper实现类似viewpager的效果，并监听选择变化
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
        //初始化已选图片recyclerview，添加添加拖拽排序
        mSelectedRecyclerView = findViewById(R.id.rv_selected_images);
        LinearLayoutManager selectLayoutManager = new LinearLayoutManager(this);
        selectLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mSelectedRecyclerView.setLayoutManager(selectLayoutManager);
        selectedImageAdapter = new SelectedImageAdapter();
        mSelectedRecyclerView.setAdapter(selectedImageAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(imageTouchHelperCallback);
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
        currentPreviewIndex = position;
        tvIndex.setText(String.format(getString(R.string.index_of_total), position + 1, totalPreviewImageSize));
        syncSelectedStatus(position);
    }

    /**
     * 同步预览和底部选中列表状态
     * @param position
     */
    private void syncSelectedStatus(int position) {
        ImageInfo item = previewImageAdapter.getItem(position);
        int selectedIndex = -1;
        //遍历选中图片集合，找到选中的角标
        for (int i = 0; i < mSelectedImageList.size(); i++) {
            ImageInfo imageInfo = mSelectedImageList.get(i);
            if (item.getId() == imageInfo.getId()){
                selectedIndex = i;
                break;
            }
        }
        if (selectedIndex > -1){
            //预览大图在底部选中列表里，修改选中状态
            LinearLayoutManager layoutManager = (LinearLayoutManager) mSelectedRecyclerView.getLayoutManager();
            int firstPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
            int lastPosition = layoutManager.findLastCompletelyVisibleItemPosition();
            if (selectedIndex < firstPosition || selectedIndex > lastPosition){
                //选中图片在屏幕内不可完全可见，滑动列表
                mSelectedRecyclerView.scrollToPosition(selectedIndex);
            }
            tvSelect.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_folder_selected, 0, 0, 0);
        } else {
            tvSelect.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_folder_unselected, 0, 0, 0);
        }
        selectedImageAdapter.notifyItemSelected(selectedIndex);
    }

    @Override
    public void onImageListResult(List<ImageInfo> images) {
        if (images == null)
            return;
        mPreviewImageList.clear();
        mPreviewImageList.addAll(images);
        totalPreviewImageSize = images.size();
        previewImageAdapter.setImageInfoList(mPreviewImageList);

        tvIndex.setText(String.format(getString(R.string.index_of_total), currentPreviewIndex + 1, images.size()));
        syncOkStatus();

        if (currentPreviewIndex > 0) {
            mPreviewRecyclerView.scrollToPosition(currentPreviewIndex);
        }
        onPagerPositionChanged(currentPreviewIndex);
    }

    @Override
    public void onFolderInfoListResult(List<FolderInfo> mImageFolders) {

    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.tv_image_preview_back){
            //将该页面的数据返回上一页，本页面有可能已经对图片做了相关操作（增减、排序）
            setResultAndFinishSelf(RESULT_CANCELED);
        } if (viewId == R.id.tv_select_image){
            //选中或取消选中当前图片
            selectOrUnselectCurrentImage();
        } else if (viewId == R.id.tv_preview_ok){
            //点击完成，返回到调用页
            setResultAndFinishSelf(RESULT_OK);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResultAndFinishSelf(RESULT_CANCELED);
    }

    private void setResultAndFinishSelf(int resultCode) {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(MultiImageSelector.RESULT_MULTI_DATA, mSelectedImageList);
        //这里使用cancel标识符，用来区分点击的是确定还是返回
        setResult(resultCode, intent);
        finish();
    }

    /**
     * 选中或取消选中当前预览的图片
     */
    private void selectOrUnselectCurrentImage() {
        ImageInfo imageInfo = mPreviewImageList.get(currentPreviewIndex);
        int index = mSelectedImageList.indexOf(imageInfo);
        if (index != -1){
            //图片在选中列表中，将其移除
            mSelectedImageList.remove(index);
            selectedImageAdapter.notifyItemRemoved(index);
            //将本角标后的数据的index都减一
            for (int i = index; i<mSelectedImageList.size(); i++){
                ImageInfo info = mSelectedImageList.get(i);
                info.setIndex(info.getIndex() - 1);
            }
            tvSelect.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_folder_unselected, 0, 0, 0);
            if (mSelectedImageList.isEmpty()){
                mSelectedRecyclerView.setVisibility(View.GONE);
            }
        } else {
            //图片不在列表中，判断是否已经达到最大选择数
            if (mSelectedImageList.size() >= maxSelectedCount){
                //已达到最大选择数，给出提示
                String msg = String.format(getString(R.string.cannot_select_more_image), maxSelectedCount);
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                return;
            }
            // 未达到醉倒选择数，将其添加到列表最后一位
            imageInfo.setIndex(mSelectedImageList.size());
            mSelectedImageList.add(imageInfo);
            selectedImageAdapter.notifyItemInserted(mSelectedImageList.size() - 1);
            mSelectedRecyclerView.scrollToPosition(mSelectedImageList.size() - 1);
            selectedImageAdapter.notifyItemSelected(mSelectedImageList.size() - 1);
            tvSelect.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_folder_selected, 0, 0, 0);
            mSelectedRecyclerView.setVisibility(View.VISIBLE);
        }
        syncOkStatus();
    }

    /**
     * 同步完成状态
     */
    private void syncOkStatus(){
        if (mSelectedImageList.size() > 0){
            tvOk.setEnabled(true);
            tvOk.setText(String.format(getString(R.string.image_selected_result), mSelectedImageList.size(), maxSelectedCount));
        } else {
            tvOk.setEnabled(false);
            tvOk.setText(R.string.completed);
        }
    }

    @Override
    public void onImageItemClick(int position) {
        ImageInfo imageInfo = mSelectedImageList.get(position);
        int index = mPreviewImageList.indexOf(imageInfo);
        if (index != -1){
            mPreviewRecyclerView.scrollToPosition(index);
        }
    }
}
