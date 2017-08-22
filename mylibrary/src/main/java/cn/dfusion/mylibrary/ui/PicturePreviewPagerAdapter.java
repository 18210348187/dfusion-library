package cn.dfusion.mylibrary.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import cn.bingoogolapple.photopicker.imageloader.BGAImage;
import cn.bingoogolapple.photopicker.util.BGABrowserPhotoViewAttacher;
import cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil;
import cn.bingoogolapple.photopicker.widget.BGAImageView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 图片预览适配器
 */

class PicturePreviewPagerAdapter extends PagerAdapter {
    private ArrayList<String> mPreviewImages;
    // 已加载完的原始图片
    private Bitmap[] mLoadedOrignalImages;
    private PhotoViewAttacher.OnViewTapListener mOnViewTapListener;
    private Activity mActivity;

    private ArrayList<BGAImageView> viewContainter = new ArrayList<>();

    PicturePreviewPagerAdapter(Activity activity, PhotoViewAttacher.OnViewTapListener onViewTapListener, ArrayList<String> previewImages) {
        mOnViewTapListener = onViewTapListener;
        mPreviewImages = previewImages;
        mLoadedOrignalImages = new Bitmap[mPreviewImages.size()];
        mActivity = activity;
        setViewContainter();
    }

    BGAImageView getView(int position){
        return viewContainter.get(position);
    }

    String getItem(int position){
        return mPreviewImages.get(position);
    }

    void setLoadedImage(int position, Bitmap image){
        mLoadedOrignalImages[position] = image;
    }

    boolean isOrginalImage(int position){
        if (mPreviewImages.get(position).startsWith("file://")){
            return true;
        }
        return mLoadedOrignalImages[position] != null;
    }

    private void setViewContainter(){
        for(String ignored : mPreviewImages){
            BGAImageView imageView = new BGAImageView(mActivity);
            final BGABrowserPhotoViewAttacher photoViewAttacher = new BGABrowserPhotoViewAttacher(imageView);
            photoViewAttacher.setOnViewTapListener(mOnViewTapListener);
            imageView.setDelegate(new BGAImageView.Delegate() {
                @Override
                public void onDrawableChanged(Drawable drawable) {
                    if (drawable != null && drawable.getIntrinsicHeight() > drawable.getIntrinsicWidth() && drawable.getIntrinsicHeight() > BGAPhotoPickerUtil.getScreenHeight()) {
                        photoViewAttacher.setIsSetTopCrop(true);
                        photoViewAttacher.setUpdateBaseMatrix();
                    } else {
                        photoViewAttacher.update();
                    }
                }
            });
            viewContainter.add(imageView);
        }
    }

    @Override
    public int getCount() {
        return mPreviewImages == null ? 0 : mPreviewImages.size();
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        BGAImageView imageView = viewContainter.get(position);
        container.addView(imageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        if (mLoadedOrignalImages[position] != null){
            imageView.setImageBitmap(mLoadedOrignalImages[position]);
        }else {
            BGAImage.display(imageView,
                    cn.bingoogolapple.photopicker.R.mipmap.bga_pp_ic_holder_dark,
                    mPreviewImages.get(position),
                    BGAPhotoPickerUtil.getScreenWidth(),
                    BGAPhotoPickerUtil.getScreenHeight());
        }

        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
