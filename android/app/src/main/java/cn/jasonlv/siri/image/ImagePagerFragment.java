package cn.jasonlv.siri.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

import cn.jasonlv.siri.Constant;
import cn.jasonlv.siri.R;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Jason Lyu on 2015/7/08.
 *
 * 单页图片显示，支持左右滑动.
 */
public class ImagePagerFragment extends BaseFragment {
    private final static String LOG_TAG = ImagePagerFragment.class.getSimpleName();

    public static final int INDEX = 2;

    private static ArrayList<String> IMAGE_URLS;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        IMAGE_URLS = getArguments().getStringArrayList("IMAGE_URL");

        View rootView = inflater.inflate(R.layout.fr_image_pager, container, false);
        ViewPager pager = (ViewPager) rootView.findViewById(R.id.pager);
        pager.setAdapter(new ImageAdapter(getActivity()));
        //pager.setCurrentItem(getArguments().getInt(Constant.Extra.IMAGE_POSITION, 0));
        pager.setCurrentItem(Constant.IMAGE_POSITION);
        return rootView;
    }

    private static class ImageAdapter extends PagerAdapter {

        //private static final String[] IMAGE_URLS = Constant.IMAGES;

        private LayoutInflater inflater;
        private DisplayImageOptions options;

        ImageAdapter(Context context) {
            inflater = LayoutInflater.from(context);

            options = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.ic_empty)
                    .showImageOnFail(R.drawable.ic_error)
                    .resetViewBeforeLoading(true)
                    .cacheOnDisk(true)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .considerExifParams(true)
                    .displayer(new FadeInBitmapDisplayer(300))
                    .build();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return IMAGE_URLS.size();
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);
            assert imageLayout != null;
            ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
            PhotoViewAttacher mAttacher = new PhotoViewAttacher(imageView);
            /*ImageViewTouch mImage = (ImageViewTouch)imageLayout.findViewById(R.id.image);
            mImage.setDisplayType(DisplayType.FIT_IF_BIGGER);
            mImage.setImageURI(Uri.parse(IMAGE_URLS.get(position)));


            mImage.setSingleTapListener(
                    new ImageViewTouch.OnImageViewTouchSingleTapListener() {

                        @Override
                        public void onSingleTapConfirmed() {
                            Log.d(LOG_TAG, "onSingleTapConfirmed");
                        }
                    }
            );

            mImage.setDoubleTapListener(
                    new ImageViewTouch.OnImageViewTouchDoubleTapListener() {

                        @Override
                        public void onDoubleTap() {
                            Log.d(LOG_TAG, "onDoubleTap");
                        }
                    }
            );

            mImage.setOnDrawableChangedListener(
                    new ImageViewTouchBase.OnDrawableChangeListener() {

                        @Override
                        public void onDrawableChanged(Drawable drawable) {
                            Log.i(LOG_TAG, "onBitmapChanged: " + drawable);
                        }
                    }
            );
            */

            final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);

            ImageLoader.getInstance().displayImage(IMAGE_URLS.get(position) , imageView, options, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    spinner.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    String message = null;
                    switch (failReason.getType()) {
                        case IO_ERROR:
                            message = "Input/Output error";
                            break;
                        case DECODING_ERROR:
                            message = "Image can't be decoded";
                            break;
                        case NETWORK_DENIED:
                            message = "Downloads are denied";
                            break;
                        case OUT_OF_MEMORY:
                            message = "Out Of Memory error";
                            break;
                        case UNKNOWN:
                            message = "Unknown error";
                            break;
                    }
                    Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();

                    spinner.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    spinner.setVisibility(View.GONE);
                }
            });

            view.addView(imageLayout, 0);
            mAttacher.update();
            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }
}