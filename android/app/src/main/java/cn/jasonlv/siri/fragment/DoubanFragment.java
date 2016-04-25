package cn.jasonlv.siri.fragment;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import cn.jasonlv.siri.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link DoubanFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * DoubanFragment 负责 豆瓣中图书、电影、音乐 信息的显示，抓取.
 *
 */
public class DoubanFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String VideoArg = "video";
    private static final String MusicArg = "music";
    private static final String BookArg = "book";

    // TODO: Rename and change types of parameters
    private String mVideoArg;
    private String mMusicArg;
    private String mBookArg;


    private TextView movieTitleTextView;
    private TextView movieGenreTextView;
    private TextView movieRatingTextView;
    private TextView movieLinkTextView;

    private TextView bookTitleTextView;
    private TextView bookAuthorTextView;
    private TextView bookRatingTextView;
    private TextView bookLinkTextView;

    private TextView musicTitleTextView_1;
    private TextView musicLinkTextView_1;
    private TextView musicRatingTextView_1;

    private TextView musicTitleTextView_2;
    private TextView musicLinkTextView_2;
    private TextView musicRatingTextView_2;
    private TextView musicTitleTextView_3;
    private TextView musicLinkTextView_3;
    private TextView musicRatingTextView_3;



    // TODO: Rename and change types and number of parameters
    public static DoubanFragment newInstance(String v, String m, String b) {
        DoubanFragment fragment = new DoubanFragment();
        Bundle args = new Bundle();
        args.putString(VideoArg, v);
        args.putString(MusicArg, m);
        args.putString(BookArg, b);
        fragment.setArguments(args);
        return fragment;
    }

    public DoubanFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mVideoArg = getArguments().getString(VideoArg);
            mMusicArg = getArguments().getString(MusicArg);
            mBookArg = getArguments().getString(BookArg);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_douban, container, false);

        movieGenreTextView = (TextView)v.findViewById(R.id.movie_genre_textview);
        movieLinkTextView = (TextView)v.findViewById(R.id.movie_link_textview);
        movieRatingTextView = (TextView)v.findViewById(R.id.movie_rating_textview);
        movieTitleTextView = (TextView)v.findViewById(R.id.movie_title_textview);

        bookTitleTextView = (TextView)v.findViewById(R.id.book_title_textview);
        bookAuthorTextView = (TextView)v.findViewById(R.id.book_author_textview);
        bookRatingTextView = (TextView)v.findViewById(R.id.book_rating_textview);
        bookLinkTextView = (TextView)v.findViewById(R.id.book_link_textview);

        musicLinkTextView_1 = (TextView)v.findViewById(R.id.music_link_1);
        musicRatingTextView_1 = (TextView)v.findViewById(R.id.music_rating_1);
        musicTitleTextView_1 = (TextView)v.findViewById(R.id.music_title_1);

        musicLinkTextView_2 = (TextView)v.findViewById(R.id.music_link_2);
        musicRatingTextView_2 = (TextView)v.findViewById(R.id.music_rating_2);
        musicTitleTextView_2 = (TextView)v.findViewById(R.id.music_title_2);
        musicLinkTextView_3 = (TextView)v.findViewById(R.id.music_link_3);
        musicRatingTextView_3 = (TextView)v.findViewById(R.id.music_rating_3);
        musicTitleTextView_3 = (TextView)v.findViewById(R.id.music_title_3);

        if(mBookArg != null){
            new BookGetter().execute(mBookArg);



        }else{
            ((ViewGroup) (LinearLayout)v.findViewById(R.id.book_layout).getParent()).removeView(
                    (LinearLayout)v.findViewById(R.id.book_layout));

        }

        if(mVideoArg != null){
            new VideoGetter().execute(mVideoArg);
        }else{
            ((ViewGroup) (LinearLayout)v.findViewById(R.id.movie_layout).getParent()).removeView(
                    (LinearLayout)v.findViewById(R.id.movie_layout));

        }

        if(mMusicArg != null){
            new MusicGetter().execute(mMusicArg);
        }else{
            ((ViewGroup) (LinearLayout)v.findViewById(R.id.music_layout).getParent()).removeView(
                    (LinearLayout)v.findViewById(R.id.music_layout));

        }

        return v;
    }

    public class MusicGetter extends AsyncTask<String, Void, JSONArray> {
        private final String LOG_TAG;
        private final String mMovieUrl = "http://jasonlv.cn/music/";
        {
            LOG_TAG = MusicGetter.class.getName().toString();
        }

        /*
        {
           "rating":{
              "max":10,
              "numRaters":47896,
              "average":"8.4",
              "min":0
           },
           "author":[
              "\u5357\u6d3e\u4e09\u53d4"
           ],
           "link":"http://book.douban.com/subject/1948901/",
           "title":"\u76d7\u5893\u7b14\u8bb0"
        }
        */
        @Override
        protected void onPostExecute(JSONArray ja) {
            super.onPostExecute(ja);
            try {
                for(int i = 0 ; i < 3 && i < ja.length(); i++){
                    String title = ja.getJSONObject(i).getString("title");
                    String link = ja.getJSONObject(i).getString("link");
                    Double rating = ja.getJSONObject(i).getJSONObject("rating").getDouble("average");

                    switch (i){
                        case 0:
                            musicTitleTextView_1.setText(title);
                            musicRatingTextView_1.setText("豆瓣评分:"+String.valueOf(rating));
                            musicLinkTextView_1.setText("豆瓣主页: "+link);
                            break;
                        case 1:
                            musicTitleTextView_2.setText(title);
                            musicRatingTextView_2.setText("豆瓣评分:"+String.valueOf(rating));
                            musicLinkTextView_2.setText("豆瓣主页: "+link);
                            break;
                        case 2:
                            musicTitleTextView_3.setText(title);
                            musicRatingTextView_3.setText("豆瓣评分:"+String.valueOf(rating));
                            musicLinkTextView_3.setText("豆瓣主页: "+link);
                        default:
                            ;
                    }

                }


            } catch (Exception e) {
                Toast.makeText(getActivity().getApplicationContext(), "网络状况不佳...",
                        Toast.LENGTH_LONG).show();
            }
        }

        protected JSONArray doInBackground(String... params) {
            Log.v(LOG_TAG, params[0]);
            InputStream inputStream;
            HttpURLConnection conn;
            try {
                String query = URLEncoder.encode(params[0], "utf-8");
                conn = (HttpURLConnection) new URL(mMovieUrl+query)
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
                JSONArray jc = new JSONArray(content);
                return jc;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    /**
     *  Book Getter 抓取豆瓣图书信息：
     *  图书信息的格式为JSON:
     *  格式如下：
     * {
     *    "rating":{
     *    "max":10,
     *    "numRaters":47896,
     *    "average":"8.4",
     *    "min":0
     *    },
     *    "author":[
     *    "\u5357\u6d3e\u4e09\u53d4"
     *    ],
     *    "link":"http://book.douban.com/subject/1948901/",
     *    "title":"\u76d7\u5893\u7b14\u8bb0"
     * }
     */
    public class BookGetter extends AsyncTask<String, Void, JSONObject> {
        private final String LOG_TAG;
        private final String mMovieUrl = "http://jasonlv.cn/book/";
        {
            LOG_TAG = BookGetter.class.getName().toString();
        }

        /*
        {
           "rating":{
              "max":10,
              "numRaters":47896,
              "average":"8.4",
              "min":0
           },
           "author":[
              "\u5357\u6d3e\u4e09\u53d4"
           ],
           "link":"http://book.douban.com/subject/1948901/",
           "title":"\u76d7\u5893\u7b14\u8bb0"
        }
        */
        @Override
        protected void onPostExecute(JSONObject jo) {
            super.onPostExecute(jo);
            try {
                String title = jo.getString("title");
                String link = jo.getString("link");
                Double rating = jo.getJSONObject("rating").getDouble("average");
                String author = jo.getJSONArray("author").getString(0);


                bookAuthorTextView.setText("作者: " + author);
                bookTitleTextView.setText(title);
                bookLinkTextView.setText("豆瓣主页: "+ link);
                bookRatingTextView.setText("豆瓣评分: "+ String.valueOf(rating));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        protected JSONObject doInBackground(String... params) {
            Log.v(LOG_TAG, params[0]);
            InputStream inputStream;
            HttpURLConnection conn;
            try {
                /* make http connection.

                 */

                String query = URLEncoder.encode(params[0], "utf-8");
                conn = (HttpURLConnection) new URL(mMovieUrl+query)
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
                return jc;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }


    public class VideoGetter extends AsyncTask<String, Void, JSONObject> {
        private final String LOG_TAG;
        private final String mMovieUrl = "http://jasonlv.cn/movie/";
        {
            LOG_TAG = VideoGetter.class.getName().toString();
        }

        /*
        {
            "rating":{
                "max":10,
                "average":2.9,
                "stars":"15",
                "min":0
            },
            "genres":[
                "\u60ac\u7591",
                "\u5192\u9669"
            ],
            "link":"http://movie.douban.com/subject/25907063/",
            "title":"\u76d7\u5893\u7b14\u8bb0"
        }
        */
        @Override
        protected void onPostExecute(JSONObject jo) {
            super.onPostExecute(jo);
            try {
                String title = jo.getString("title");
                String link = jo.getString("link");
                Double rating = jo.getJSONObject("rating").getDouble("average");
                JSONArray geArray = jo.getJSONArray("genres");

                String genres="";
                for(int i = 0; i < geArray.length(); i++){
                    genres+=geArray.get(i).toString();
                    genres+=" ";
                }

                movieGenreTextView.setText(genres);
                movieTitleTextView.setText(title);
                movieLinkTextView.setText("豆瓣主页: "+ link);
                movieRatingTextView.setText("豆瓣评分: "+ String.valueOf(rating));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        protected JSONObject doInBackground(String... params) {
            Log.v(LOG_TAG, params[0]);
            InputStream inputStream;
            HttpURLConnection conn;
            try {
                String query = URLEncoder.encode(params[0], "utf-8");
                conn = (HttpURLConnection) new URL(mMovieUrl+query)
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
                return jc;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

}
