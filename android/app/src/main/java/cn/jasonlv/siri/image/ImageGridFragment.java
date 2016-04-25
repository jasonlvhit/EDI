package cn.jasonlv.siri.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import cn.jasonlv.siri.Constant;
import cn.jasonlv.siri.R;

/**
 * @author Jason Lyu(jasonlyuhit@outlook.com)
 *
 * ImageGridFragment 以网格的形式展示搜索到的图片，单击网格中的图片
 * 调用新的SimpleImageActivity， 以PagerImage的形式展示图片.
 *
 */
public class ImageGridFragment extends AbsListViewBaseFragment {

    private static final String LOG_TAG = ImageGridFragment.class.getSimpleName();

    public static final int INDEX = 1;

    public static ArrayList<String> IMAGE_URLS = new ArrayList<String>();
    private ImageAdapter adapter;

    private ProgressBar spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        IMAGE_URLS.clear();

        View rootView = inflater.inflate(R.layout.fr_image_grid, container, false);
        listView = (GridView) rootView.findViewById(R.id.grid);
        adapter = new ImageAdapter(getActivity());

        ((GridView) listView).setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Constant.IMAGE_POSITION = position;
                startImagePagerActivity(position);

            }
        });
        spinner = (ProgressBar)rootView.findViewById(R.id.loading);
        new ImageGetter().execute(getArguments().getString("KEYWORD"));
        return rootView;
    }

    /**
     * Adapter for display of images.
     */
    private static class ImageAdapter extends BaseAdapter {

        //
        // IMAGES in Constant for test.
        // private static final String[] IMAGE_URLS = Constant.IMAGES;


        private LayoutInflater inflater;

        private DisplayImageOptions options;

        /**
         *
         * @param context
         */
        ImageAdapter(Context context) {
            inflater = LayoutInflater.from(context);

            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.ic_stub)
                    .showImageForEmptyUri(R.drawable.ic_empty)
                    .showImageOnFail(R.drawable.ic_error)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
        }

        @Override
        public int getCount() {
            return IMAGE_URLS.size();
        }

        /**
         * 给定position， 返回相应位置的图片
         * @param position
         * @return Object
         */
        @Override
        public Object getItem(int position) {
            return null;
        }


        /**
         *
         * @param position
         * @return
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            View view = convertView;
            if (view == null) {
                view = inflater.inflate(R.layout.item_grid_image, parent, false);
                holder = new ViewHolder();
                assert view != null;
                holder.imageView = (ImageView) view.findViewById(R.id.image);
                holder.progressBar = (ProgressBar) view.findViewById(R.id.progress);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            ImageLoader.getInstance()
                    .displayImage(IMAGE_URLS.get(position), holder.imageView, options, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            holder.progressBar.setProgress(0);
                            holder.progressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            holder.progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            holder.progressBar.setVisibility(View.GONE);
                        }
                    }, new ImageLoadingProgressListener() {
                        @Override
                        public void onProgressUpdate(String imageUri, View view, int current, int total) {
                            holder.progressBar.setProgress(Math.round(100.0f * current / total));
                        }
                    });

            return view;
        }
    }

    static class ViewHolder {
        ImageView imageView;
        ProgressBar progressBar;
    }

    public class ImageGetter extends AsyncTask<String, Void, JSONArray> {
        private final String mImageView = "http://jasonlv.cn/image/";
        private final String LOG_TAG = ImageGetter.class.getName();



        @Override
        protected void onPostExecute(JSONArray ja) {
            super.onPostExecute(ja);
            spinner.setVisibility(View.INVISIBLE);
            IMAGE_URLS.clear();
            if(ja != null) {

                for (int i = 0; i < ja.length(); i++) {
                    try {
                        IMAGE_URLS.add(ja.getString(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }else{
                Toast.makeText(getActivity().getApplicationContext(), "网络状况不佳...",
                        Toast.LENGTH_LONG).show();

            }


            //
            adapter.notifyDataSetChanged();
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            spinner.setVisibility(View.VISIBLE);

        }

        protected JSONArray doInBackground(String... params) {



            InputStream inputStream;
            HttpURLConnection conn;
            try {
                Log.e(LOG_TAG, params[0]);
                String query = URLEncoder.encode(params[0], "utf-8");
                conn = (HttpURLConnection) new URL(mImageView+query)
                        .openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");

                conn.setDoInput(true);
                conn.connect();
                inputStream = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        inputStream, "UTF-8"));
                StringBuffer outBuffer = new StringBuffer();
                String string;
                while ((string = br.readLine()) != null)
                    outBuffer.append(string);
                String content = outBuffer.toString();
                inputStream.close();
                Log.v(LOG_TAG, content);
                JSONObject jc = new JSONObject(content);
                //Log.v("result:", jc.getJSONObject("result").toString());
                //Log.v("array result:", jc.getJSONObject("result").getJSONArray("forecast").toString());
                return jc.getJSONArray("images");
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity().getApplicationContext(), "网络状况不佳...",
                        Toast.LENGTH_LONG).show();
            }

            return null;
        }
    }
}