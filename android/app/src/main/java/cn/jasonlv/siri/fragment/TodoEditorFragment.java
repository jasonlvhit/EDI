package cn.jasonlv.siri.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import cn.jasonlv.siri.R;
import cn.jasonlv.siri.activity.TodoActivity;
import cn.jasonlv.siri.utility.TodoManager;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link TodoEditorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TodoEditorFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    static public Button setTimeBtn;
    static public Button setDateBtn;
    static private Button submitBtn;

    private static EditText editTextTitle;
    static private EditText editTextDescription;
    static private TextView textViewDate;
    static private TextView textViewTime;

    static int setHour = 0;
    static int setDay = 1;
    static int setYear = 1990;
    static int setMin = 0;
    static int setMon = 1;

    static private TodoManager todoManager;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TodoEditorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TodoEditorFragment newInstance(String param1, String param2) {
        TodoEditorFragment fragment = new TodoEditorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public TodoEditorFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        todoManager = new TodoManager(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_todo_editor, container, false);

        editTextDescription = (EditText)v.findViewById(R.id.todo_editor_description);
        editTextTitle = (EditText)v.findViewById(R.id.todo_title_editor);

        setDateBtn = (Button)v.findViewById(R.id.set_date_editor);
        setTimeBtn = (Button)v.findViewById(R.id.set_time_editor);
        submitBtn = (Button)v.findViewById(R.id.submit_todo);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();

                c.set(setYear, setMon, setDay, setHour, setMin);
                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");



                Log.e("dsad", format1.format(c.getTime()));

                long mill = c.getTime().getTime();
                if(System.currentTimeMillis() > mill){
                    Toast.makeText(getActivity().getApplicationContext(), "待办事项时间早于当前时间",
                            Toast.LENGTH_LONG).show();
                }else {

                    Log.e("lslslslaslasflaslfdaslkjads", String.valueOf(mill));

                    TodoManager.TodoItem item = new TodoManager.TodoItem();
                    item.title = editTextTitle.getText().toString();
                    item.description = editTextDescription.getText().toString();
                    item.date = String.valueOf(mill);

                    todoManager.addTodoItem(item);

                    Intent intent = new Intent(getActivity(), TodoActivity.class);
                    startActivity(intent);
                }
            }
        });

        setDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
        });

        setTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(view);
            }
        });

        //textViewDate = (TextView)v.findViewById(R.id.todo_date_editor);
        //textViewTime = (TextView)v.findViewById(R.id.todo_time_editor);

        return v;
    }




    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            //textViewTime.setText(String.valueOf(hourOfDay) + "h " + String.valueOf(minute) + "m ");
            setTimeBtn.setText(String.valueOf(hourOfDay) + " : " + String.valueOf(minute));
            setHour = hourOfDay;
            setMin = minute;
        }
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            //textViewDate.setText(String.valueOf(year) + " " + String.valueOf(month+1) + " " + String.valueOf(day));
            setDateBtn.setText(String.valueOf(year) + " - " + String.valueOf(month+1) + " - " + String.valueOf(day));
            setYear = year;
            setDay = day;
            setMon = month;
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

}
