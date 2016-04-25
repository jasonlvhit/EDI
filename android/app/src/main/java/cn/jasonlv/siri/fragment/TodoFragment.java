package cn.jasonlv.siri.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import cn.jasonlv.siri.R;
import cn.jasonlv.siri.db.DBManager;
import cn.jasonlv.siri.utility.TodoManager;

public class TodoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    static private ListView listView;
    static private TodoListAdapter adapter;

    static private DBManager dbManager;
    ;

    ArrayList<TodoManager.TodoItem> items = new ArrayList<TodoManager.TodoItem>();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TodoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TodoFragment newInstance(String param1, String param2) {
        TodoFragment fragment = new TodoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public TodoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        adapter = new TodoListAdapter();
        dbManager = DBManager.getInstance(getActivity().getApplicationContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_todo, container, false);
        listView = (ListView)v.findViewById(R.id.todo_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int index = i;
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("删除待办事项?")
                        .setCancelable(false)
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dbManager.deleteItemById(items.get(index).id);

                                new TodoFetcher().execute();
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("否", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        (new TodoFetcher()).execute();

        return v;
    }


    public class TodoListAdapter extends BaseAdapter {


        public int getCount() {
            // TODO Auto-generated method stub
            return items.size();
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
            row = inflater.inflate(R.layout.todo_item_listview, parent, false);
            TextView title = (TextView) row.findViewById(R.id.todo_title);
            TextView description = (TextView) row.findViewById(R.id.todo_description);
            TextView date = (TextView) row.findViewById(R.id.todo_date);

            title.setText(items.get(position).title);
            description.setText(items.get(position).description);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            Calendar calendar = Calendar.getInstance();

            Log.e("dasda", String.valueOf(items.get(position).date));

            calendar.setTimeInMillis(Long.valueOf(items.get(position).date));
            date.setText(sdf.format(calendar.getTime()));

            return (row);
        }
    }

    public class TodoFetcher extends AsyncTask<Void, Void, Void> {
        private final String LOG_TAG = TodoFetcher.class.getName();


        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);

            adapter.notifyDataSetChanged();
        }

        protected Void doInBackground(Void... params) {
            items = dbManager.query();
            return null;
        }
    }

}
