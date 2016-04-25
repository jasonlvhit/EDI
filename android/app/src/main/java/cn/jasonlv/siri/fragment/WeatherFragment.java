package cn.jasonlv.siri.fragment;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import java.util.ArrayList;

import cn.jasonlv.siri.R;

public class WeatherFragment extends Fragment {

    private ListView listView;
    private WeatherAdapter adapter;
    //ArrayList<String> datearray, weekdayarray, higharray, lowarray;
    ArrayList<String> datearray = new ArrayList<String>();
    ArrayList<String> weekdayarray  = new ArrayList<String>();
    ArrayList<String> higharray = new ArrayList<String>();
    ArrayList<String> lowarray  = new ArrayList<String>();

    TextView date_1;
    TextView weekday_1;
    TextView high_1;
    TextView low_1;

    TextView date_2;
    TextView weekday_2;
    TextView high_2;
    TextView low_2;
    TextView date_3;
    TextView weekday_3;
    TextView high_3;
    TextView low_3;
    TextView date_4;
    TextView weekday_4;
    TextView high_4;
    TextView low_4;
    TextView date_5;
    TextView weekday_5;
    TextView high_5;
    TextView low_5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new WeatherAdapter();
        //getActivity().setContentView(R.layout.weather_container);
        //listView=(ListView)findViewById(R.id.list);
        //l1.setAdapter(new dataListAdapter(t1,d1,i1));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.weather_container, container, false);
        //listView = (ListView)v.findViewById(R.id.weather_list);
        //listView.setAdapter(adapter);

        date_1 = (TextView)v.findViewById(R.id.weather_date_1);
        date_2 = (TextView)v.findViewById(R.id.weather_date_2);
        date_3 = (TextView)v.findViewById(R.id.weather_date_3);
        date_4 = (TextView)v.findViewById(R.id.weather_date_4);
        date_5 = (TextView)v.findViewById(R.id.weather_date_5);

        weekday_1 = (TextView)v.findViewById(R.id.weather_weekday_1);
        weekday_2 = (TextView)v.findViewById(R.id.weather_weekday_2);
        weekday_3 = (TextView)v.findViewById(R.id.weather_weekday_3);
        weekday_4 = (TextView)v.findViewById(R.id.weather_weekday_4);
        weekday_5 = (TextView)v.findViewById(R.id.weather_weekday_5);

        high_1 = (TextView)v.findViewById(R.id.weather_high_1);
        high_2 = (TextView)v.findViewById(R.id.weather_high_2);
        high_3 = (TextView)v.findViewById(R.id.weather_high_3);
        high_4 = (TextView)v.findViewById(R.id.weather_high_4);
        high_5 = (TextView)v.findViewById(R.id.weather_high_5);

        low_1 = (TextView) v.findViewById(R.id.weather_low_1);
        low_2 = (TextView) v.findViewById(R.id.weather_low_2);
        low_3 = (TextView) v.findViewById(R.id.weather_low_3);
        low_4 = (TextView) v.findViewById(R.id.weather_low_4);
        low_5 = (TextView) v.findViewById(R.id.weather_low_5);


        new WeatherGetter().execute();
        return v;
    }

    public static WeatherFragment newInstance(String lat, String lon) {
        WeatherFragment fragment = new WeatherFragment();

        Bundle args = new Bundle();
        args.putString("lat", lat);
        args.putString("lon", lon);
        fragment.setArguments(args);
        return fragment;
    }

    public class WeatherAdapter extends BaseAdapter{


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
            return datearray.size();
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
            row = inflater.inflate(R.layout.weather_list_item, parent, false);
            TextView date, weekday, high, low;
            date = (TextView) row.findViewById(R.id.weather_date);
            weekday = (TextView) row.findViewById(R.id.weather_weekday);
            high = (TextView)row.findViewById(R.id.weather_high);
            low = (TextView)row.findViewById(R.id.weather_low);

            date.setText(datearray.get(position));
            weekday.setText(weekdayarray.get(position));
            high.setText(higharray.get(position));
            low.setText(lowarray.get(position));

            return (row);
        }
    }

    public class WeatherGetter extends AsyncTask<Void, Void, JSONArray> {
        private final String mWeatherUrl = "http://jasonlv.cn/weather/";
        private final String LOG_TAG = WeatherGetter.class.getName();



        @Override
        protected void onPostExecute(JSONArray ja) {
            super.onPostExecute(ja);
            Log.e(LOG_TAG, ja.toString());
            for(int i = 0; i < ja.length(); i++){
                try {
                    JSONObject o = ja.getJSONObject(i);
                    datearray.add(o.getString("date"));
                    weekdayarray.add(o.getString("day"));
                    higharray.add(o.getString("high"));
                    lowarray.add(o.getString("low"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            date_1.setText(datearray.get(0));
            date_2.setText(datearray.get(1));
            date_3.setText(datearray.get(2));
            date_4.setText(datearray.get(3));
            date_5.setText(datearray.get(4));

            weekday_1.setText(weekdayarray.get(0));
            weekday_2.setText(weekdayarray.get(1));
            weekday_3.setText(weekdayarray.get(2));
            weekday_4.setText(weekdayarray.get(3));
            weekday_5.setText(weekdayarray.get(4));

            high_1.setText(higharray.get(0));
            high_2.setText(higharray.get(1));
            high_3.setText(higharray.get(2));
            high_4.setText(higharray.get(3));
            high_5.setText(higharray.get(4));

            low_1.setText(lowarray.get(0));
            low_2.setText(lowarray.get(1));
            low_3.setText(lowarray.get(2));
            low_4.setText(lowarray.get(3));
            low_5.setText(lowarray.get(4));




            //
            // adapter.notifyDataSetChanged();
        }

        protected JSONArray doInBackground(Void... params) {



            InputStream inputStream;
            HttpURLConnection conn;
            try {
                /*conn = (HttpURLConnection) new URL("http://jasonlv.cn/weather/?lat="+
                        getArguments().getString("lat")+"&lon="+
                        getArguments().getString("lon"))
                        .openConnection();
                */

                conn = (HttpURLConnection) new URL("http://jasonlv.cn/weather/?lat=45.75&lon=126.63")
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
                return jc.getJSONObject("query").getJSONObject("results").getJSONObject("channel")
                        .getJSONObject("item").getJSONArray("forecast");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

}
