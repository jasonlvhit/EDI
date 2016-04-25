package cn.jasonlv.siri.fragment;


import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
import java.util.ArrayList;

import cn.jasonlv.siri.R;

public class SearchFragment extends Fragment {

    private ListView listView;
    private SearchAdapter adapter;

    private ArrayList<String> titlearray = new ArrayList<String>();
    private ArrayList<String> linkArray = new ArrayList<String>();

    private TextView wikiTextView ;
    private TextView wikiLinkView;

    private TextView newsTitleTextView1;
    private TextView newsLinkTextView1;
    private TextView newsTitleTextView2;
    private TextView newsLinkTextView2;
    private TextView newsTitleTextView3;
    private TextView newsLinkTextView3;

    private TextView itemTitleTextView1;
    private TextView itemLinkTextView1;
    private TextView itemTitleTextView2;
    private TextView itemLinkTextView2;
    private TextView itemTitleTextView3;
    private TextView itemLinkTextView3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new SearchAdapter();
        //getActivity().setContentView(R.layout.weather_container);
        //listView=(ListView)findViewById(R.id.list);
        //l1.setAdapter(new dataListAdapter(t1,d1,i1));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_search, container, false);
        //listView = (ListView)v.findViewById(R.id.search_list);
        //listView.setAdapter(adapter);
        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //String url = "http://www.stackoverflow.com";

                String url = linkArray.get(i);
                Intent is = new Intent(Intent.ACTION_VIEW);
                is.setData(Uri.parse(url));
                startActivity(is);
            }
        });
        */

        //setListViewHeightBasedOnChildren(listView);

        wikiTextView = (TextView) v.findViewById(R.id.wiki_content);
        wikiLinkView = (TextView) v.findViewById(R.id.wiki_link);

        newsTitleTextView1 = (TextView) v.findViewById(R.id.news_title_1);
        newsLinkTextView1 = (TextView) v.findViewById(R.id.news_link_1);
        newsTitleTextView2 = (TextView) v.findViewById(R.id.news_title_2);
        newsLinkTextView2 = (TextView) v.findViewById(R.id.news_link_2);
        newsTitleTextView3 = (TextView) v.findViewById(R.id.news_title_3);
        newsLinkTextView3 = (TextView) v.findViewById(R.id.news_link_3);

        itemTitleTextView1 = (TextView)v.findViewById(R.id.item_title_1);
        itemLinkTextView1 = (TextView)v.findViewById(R.id.item_link_1);
        itemTitleTextView2 = (TextView)v.findViewById(R.id.item_title_2);
        itemLinkTextView2 = (TextView)v.findViewById(R.id.item_link_2);
        itemTitleTextView3 = (TextView)v.findViewById(R.id.item_title_3);
        itemLinkTextView3 = (TextView)v.findViewById(R.id.item_link_3);

        new WikiGetter().execute(getArguments().getString("query"));
        new NewsGetter().execute(getArguments().getString("query"));
        new SearchGetter().execute(getArguments().getString("query"));
        return v;
    }

    public static SearchFragment newInstance(String query) {
        SearchFragment fragment = new SearchFragment();

        Bundle args = new Bundle();
        args.putString("query", query);
        fragment.setArguments(args);
        return fragment;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public class SearchAdapter extends BaseAdapter {


        /*
        public WeatherAdapter(ArrayList<String> datearray_, ArrayList<String> weekdayarray_
                ,ArrayList<String> higharray_, ArrayList<String> lowarray_) {
            datearray = datearray_;
            weekdayarray = weekdayarray_;
            higharray = higharray_;
            lowarray = lowarray_;
        }
        */

        public int getCount() {
            // TODO Auto-generated method stub
            return titlearray.size();
        }

        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getActivity().getLayoutInflater();
            View row;
            row = inflater.inflate(R.layout.search_list_item, parent, false);
            TextView title;
            title = (TextView) row.findViewById(R.id.search_item_title);

            title.setText(titlearray.get(position));


            return (row);
        }
    }

    public class SearchGetter extends AsyncTask<String, Void, JSONArray> {
        private final String LOG_TAG = SearchGetter.class.getName();



        @Override
        protected void onPostExecute(JSONArray ja) {
            super.onPostExecute(ja);

            for(int i = 0; i < ja.length() && i < 3; i++){
                try {
                    String title = ja.getJSONObject(i).getString("text").replace('_', ' ');
                    String link = ja.getJSONObject(i).getString("link");

                    switch (i){
                        case 0:
                            itemTitleTextView1.setText(title);
                            itemLinkTextView1.setText(link);
                            break;
                        case 1:
                            itemTitleTextView2.setText(title);
                            itemLinkTextView2.setText(link);
                            break;
                        case 2:
                            itemTitleTextView3.setText(title);
                            itemLinkTextView3.setText(link);
                        default:
                            ;
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        protected JSONArray doInBackground(String... params) {



            InputStream inputStream;
            HttpURLConnection conn;
            try {

                String query = URLEncoder.encode(params[0], "utf-8");
                conn = (HttpURLConnection) new URL("http://jasonlv.cn/search/"+query)
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

                return jc.getJSONArray("result");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public class WikiGetter extends AsyncTask<String, Void, JSONObject> {
        private final String LOG_TAG;
        private final String mWikiUrl = "http://jasonlv.cn/baike/";
        {
            LOG_TAG = WikiGetter.class.getName().toString();
        }

        @Override
        protected void onPostExecute(JSONObject s) {
            super.onPostExecute(s);
            if(s != null) {
                try {
                    wikiTextView.setText(s.getString("text"));
                    wikiLinkView.setText(s.getString("link"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        protected JSONObject doInBackground(String... params) {
            Log.v(LOG_TAG, params[0]);
            InputStream inputStream = null;
            HttpURLConnection conn = null;

            try {
                String query = URLEncoder.encode(params[0], "utf-8");
                conn = (HttpURLConnection) new URL(mWikiUrl+query)
                        .openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            try {
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

    public class NewsGetter extends AsyncTask<String, Void, JSONArray> {
        private final String LOG_TAG;
        private final String mNewsUrl = "http://jasonlv.cn/news/";
        {
            LOG_TAG = NewsGetter.class.getName().toString();
        }

        @Override
        protected void onPostExecute(JSONArray ja) {
            super.onPostExecute(ja);

            for(int i = 0; i < 3 && i < ja.length(); i++){

                try {
                    String title = ja.getJSONObject(i).getString("text");
                    String link = ja.getJSONObject(i).getString("link");

                    switch (i){
                        case 0:
                            newsTitleTextView1.setText(title);
                            newsLinkTextView1.setText(link);
                            break;
                        case 1:
                            newsTitleTextView2.setText(title);
                            newsLinkTextView2.setText(link);
                            break;
                        case 2:
                            newsTitleTextView3.setText(title);
                            newsLinkTextView3.setText(link);
                        default:
                            ;
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        protected JSONArray doInBackground(String... params) {
            Log.v(LOG_TAG, params[0]);
            InputStream inputStream = null;
            HttpURLConnection conn = null;
            try {
                String query = URLEncoder.encode(params[0], "utf-8");
                conn = (HttpURLConnection) new URL(mNewsUrl+query)
                        .openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            try {
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
                return jc.getJSONArray("result");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
