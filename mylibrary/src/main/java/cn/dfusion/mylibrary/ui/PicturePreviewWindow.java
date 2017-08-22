package cn.dfusion.mylibrary.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import cn.bingoogolapple.androidcommon.adapter.BGAOnNoDoubleClickListener;
import cn.bingoogolapple.photopicker.activity.BGAPPToolbarActivity;
import cn.bingoogolapple.photopicker.imageloader.BGAImage;
import cn.bingoogolapple.photopicker.imageloader.BGAImageLoader;
import cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil;
import cn.bingoogolapple.photopicker.widget.BGAHackyViewPager;
import cn.bingoogolapple.photopicker.widget.BGAImageView;
import cn.dfusion.mylibrary.R;
import cn.dfusion.mylibrary.manager.PicturePreviewSavePhotoTask;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 图片预览弹出框
 */

public class PicturePreviewWindow extends BGAPPToolbarActivity
        implements PhotoViewAttacher.OnViewTapListener {
    // 存储卡不可用提示
    private static final String EXTRA_SDCARD_TIP = "EXTRA_SDCARD_TIP";
    // 缩略图集合
    private static final String EXTRA_PREVIEW_THUMB_IMAGES = "EXTRA_PREVIEW_THUMB_IMAGES";
    // 原始图集合
    private static final String EXTRA_PREVIEW_ORIG_IMAGES = "EXTRA_PREVIEW_ORIG_IMAGES";

    private String mSdcardTip;
    private ArrayList<String> mOrigImages;

    /**
     * 获取查看多张图片的intent
     *
     * @param context              上下文
     * @param sdcardTip            存储卡可用提示
     * @param saveImgDir           保存图片的目录，如果传null，则没有保存图片功能
     * @param previewThumbImages   当前预览的图片目录里的图片路径集合
     *                             注意：本地图片添加前缀"file://"
     * @param origImages           原始图片集合
     * @param currentPosition      当前预览图片的位置
     * @return Intent
     */
    public static Intent newIntent(Context context, String sdcardTip, File saveImgDir,
                                   ArrayList<String> previewThumbImages, ArrayList<String> origImages,int currentPosition) {
        Intent intent = newIntent(context, saveImgDir, currentPosition);
        intent.putExtra(EXTRA_SDCARD_TIP, sdcardTip);
        intent.putStringArrayListExtra(EXTRA_PREVIEW_THUMB_IMAGES, previewThumbImages);
        intent.putStringArrayListExtra(EXTRA_PREVIEW_ORIG_IMAGES,origImages);
        return intent;
    }

    // 判断存储卡是否可用
    private static boolean hasSdcard(String sdcardTip) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            BGAPhotoPickerUtil.show(sdcardTip);
            return false;
        }
        return true;
    }

    // 修改BGAPhotoPreviewActivity <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    private static final String EXTRA_SAVE_IMG_DIR = "EXTRA_SAVE_IMG_DIR";
    private static final String EXTRA_CURRENT_POSITION = "EXTRA_CURRENT_POSITION";
    private static final String EXTRA_IS_SINGLE_PREVIEW = "EXTRA_IS_SINGLE_PREVIEW";

    // 计数
    private TextView mTitleTv;
    private TextView mOriganlTv;
    private BGAHackyViewPager mContentHvp;
    private ProgressBar mProgressBar;
    private PicturePreviewPagerAdapter mPhotoPageAdapter;

    private File mSaveImgDir;

    private boolean mIsHidden = false;
    private PicturePreviewSavePhotoTask mSavePhotoTask;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mSavePhotoTask = null;
            mProgressBar.setVisibility(View.GONE);
        }
    };

    /**
     * 上一次标题栏显示或隐藏的时间戳
     */
    private long mLastShowHiddenTime;

    /**
     * 获取查看多张图片的intent
     *
     * @param context         上下文
     * @param saveImgDir      保存图片的目录，如果传null，则没有保存图片功能
     * @param currentPosition 当前预览图片的位置
     * @return Intent
     */
    public static Intent newIntent(Context context, File saveImgDir, int currentPosition) {
        Intent intent = new Intent(context, PicturePreviewWindow.class);
        intent.putExtra(EXTRA_SAVE_IMG_DIR, saveImgDir);
        intent.putExtra(EXTRA_CURRENT_POSITION, currentPosition);
        intent.putExtra(EXTRA_IS_SINGLE_PREVIEW, false);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setNoLinearContentView(R.layout.picture_preview_window);
        mContentHvp = getViewById(R.id.picture_preview_window_content);
        mProgressBar = getViewById(R.id.picture_preview_window_progress_bar);
    }

    @Override
    protected void setListener() {
        mContentHvp.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                renderTitleTv();
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mSdcardTip = getIntent().getStringExtra(EXTRA_SDCARD_TIP);
        mSaveImgDir = (File) getIntent().getSerializableExtra(EXTRA_SAVE_IMG_DIR);
        if (mSaveImgDir != null && !mSaveImgDir.exists()) {
            mSaveImgDir.mkdirs();
        }

        ArrayList<String> previewImages = getIntent().getStringArrayListExtra(EXTRA_PREVIEW_THUMB_IMAGES);
        mOrigImages = getIntent().getStringArrayListExtra(EXTRA_PREVIEW_ORIG_IMAGES);

        int currentPosition = getIntent().getIntExtra(EXTRA_CURRENT_POSITION, 0);

        mPhotoPageAdapter = new PicturePreviewPagerAdapter(this, this, previewImages);
        mContentHvp.setAdapter(mPhotoPageAdapter);
        mContentHvp.setCurrentItem(currentPosition);

        // 过2秒隐藏标题栏
        mToolbar.postDelayed(new Runnable() {
            @Override
            public void run() {
                hiddenTitleBar();
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_picture_preview, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_item_pricture_preview_top_toolbar);
        View actionView = menuItem.getActionView();

        mTitleTv = actionView.findViewById(R.id.picture_preview_top_toolbar_title);
        mOriganlTv = actionView.findViewById(R.id.picture_preview_top_toolbar_orignal);
        ImageView mDownloadTv = actionView.findViewById(R.id.picture_preview_top_toolbar_download);

        mDownloadTv.setOnClickListener(new BGAOnNoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if (mSavePhotoTask == null) {
                    savePic();
                }
            }
        });

        mOriganlTv.setOnClickListener(new BGAOnNoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                downloadOrginal();
            }
        });

        if (mSaveImgDir == null) {
            mDownloadTv.setVisibility(View.INVISIBLE);
        }

        renderTitleTv();

        return true;
    }

    private void renderTitleTv() {
        if (mTitleTv == null || mPhotoPageAdapter == null) {
            return;
        }
        mTitleTv.setText((mContentHvp.getCurrentItem() + 1) + "/" + mPhotoPageAdapter.getCount());
        if (mPhotoPageAdapter.isOrginalImage(mContentHvp.getCurrentItem())) {
            mOriganlTv.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.GONE);
        } else {
            mOriganlTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onViewTap(View view, float x, float y) {
        if (System.currentTimeMillis() - mLastShowHiddenTime > 500) {
            mLastShowHiddenTime = System.currentTimeMillis();
            if (mIsHidden) {
                showTitleBar();
            } else {
                hiddenTitleBar();
            }
        }
    }

    private void showTitleBar() {
        if (mToolbar != null) {
            ViewCompat.animate(mToolbar).translationY(0).setInterpolator(new DecelerateInterpolator(2)).setListener(new ViewPropertyAnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(View view) {
                    mIsHidden = false;
                }
            }).start();
        }
    }

    private void hiddenTitleBar() {
        if (mToolbar != null) {
            ViewCompat.animate(mToolbar).translationY(-mToolbar.getHeight()).setInterpolator(new DecelerateInterpolator(2)).setListener(new ViewPropertyAnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(View view) {
                    mIsHidden = true;
                }
            }).start();
        }
    }

    // 查看原图
    private synchronized void downloadOrginal() {
        final int currentPosition = mContentHvp.getCurrentItem();
        String url = mOrigImages.get(currentPosition);
        if (url.length() == 0){
            BGAPhotoPickerUtil.show(url);
            return;
        }
        final BGAImageView imageView = mPhotoPageAdapter.getView(currentPosition);
        mProgressBar.setVisibility(View.VISIBLE);
        BGAImage.download(url, new BGAImageLoader.DownloadDelegate() {
            @Override
            public void onSuccess(String path, Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
                mPhotoPageAdapter.setLoadedImage(currentPosition, bitmap);
                mOriganlTv.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailed(String path) {
                imageView.setImageResource(R.drawable.ic_error);
                mOriganlTv.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    // 保存原始图片
    private synchronized void savePic() {
        // 判断存储卡是否可用
        if (!hasSdcard(mSdcardTip)) {
            return;
        }
        if (mSavePhotoTask != null) {
            BGAPhotoPickerUtil.show(cn.bingoogolapple.photopicker.R.string.bga_pp_save_img_failure);
            return;
        }

        String url = mOrigImages.get(mContentHvp.getCurrentItem());
        if (url.length() == 0){
            BGAPhotoPickerUtil.show(url);
            return;
        }

        // 通过MD5加密url生成文件名，避免多次保存同一张图片
        File file = new File(mSaveImgDir, BGAPhotoPickerUtil.md5(url) + url.substring(url.lastIndexOf(".")));
        if (file.exists()) {
            BGAPhotoPickerUtil.showSafe(getString(cn.bingoogolapple.photopicker.R.string.bga_pp_save_img_success_folder, mSaveImgDir.getAbsolutePath()));
            return;
        }

        mProgressBar.setVisibility(View.VISIBLE);
        mSavePhotoTask = new PicturePreviewSavePhotoTask();
        final File finalFile = file;
        mSavePhotoTask.save(url, finalFile, new PicturePreviewSavePhotoTask.PicturePreviewSavePhotoTaskHandler() {
            @Override
            public void onSuccess() {
                handler.sendEmptyMessage(1);
                BGAPhotoPickerUtil.showSafe(getResources().getString(cn.bingoogolapple.photopicker.R.string.bga_pp_save_img_success_folder,
                        finalFile.getParentFile().getAbsolutePath()));
            }

            @Override
            public void onFailure() {
                handler.sendEmptyMessage(1);
                BGAPhotoPickerUtil.show(cn.bingoogolapple.photopicker.R.string.bga_pp_save_img_failure);
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (mSavePhotoTask != null) {
            mSavePhotoTask.cancelTask();
            mSavePhotoTask = null;
        }
        super.onDestroy();
    }
}
