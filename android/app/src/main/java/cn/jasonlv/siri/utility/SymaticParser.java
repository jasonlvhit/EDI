package cn.jasonlv.siri.utility;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cn.jasonlv.siri.activity.TodoActivity;

/**
 * Created by Jason Lyu on 2015/7/09.
 *
 * 语义解析工具，负责部分的语音解析.
 */
public class SymaticParser {

    private static final String LOG_TAG = SymaticParser.class.getSimpleName();

    /**
     * 判断语音输入是否与导航相关.
     * @param content 语音输入
     * @return true: 是，false，否
     */
    public static boolean isAboutMapIntent(String content){
        if(content.startsWith("从")&& content.contains("到")&&content.endsWith("怎么走"))
            return true;
        return false;
    }


    /**
     * 抽取导航的两个端点
     * @param content
     * @return
     */
    public static ArrayList<String> extractMapEnds(String content){
        ArrayList<String> ends = new ArrayList<String>();
        String[] tokens = content.split("到");
        ends.add(tokens[0].substring(1));
        ends.add(tokens[1].substring(0, tokens[1].length()-3));
        Log.d(LOG_TAG, ends.toString());
        return ends;
    }

    /**
     * 语音内容是否与电影相关
     * @param result
     * @return
     * @throws JSONException
     */
    public static String isMovieInfo(String result) throws JSONException {
        JSONObject res = new JSONObject(result);
        String str = res.getJSONObject("content").getString("json_res");
        JsonArray obj_array = new JsonParser().parse(str).getAsJsonObject()
                .get("results").getAsJsonArray();

        Log.d("parse movie info", obj_array.toString());

        if(obj_array.size() <= 0) return null;
        for(int i = 0; i < obj_array.size(); i++){

            String type = (obj_array.get(i)).getAsJsonObject().get("domain").getAsString();
            Log.d("type:::", type);
            if(type.equals("video")){
                return (obj_array.get(i)).getAsJsonObject().get("object")
                        .getAsJsonObject().get("name").getAsString();
            }
        }
        return null;
    }


    /**
     * 内容是否与书籍相关，若相关，返回关键词
     * @param result
     * @return
     * @throws JSONException
     */
    public static String isNovelInfo(String result) throws JSONException{
        JSONObject res = new JSONObject(result);
        String str = res.getJSONObject("content").getString("json_res");
        JsonArray obj_array = new JsonParser().parse(str).getAsJsonObject()
                .get("results").getAsJsonArray();

        Log.e("parse movie info", obj_array.toString());

        if(obj_array.size() <= 0) return null;
        for(int i = 0; i < obj_array.size(); i++){

            String type = (obj_array.get(i)).getAsJsonObject().get("domain").getAsString();
            Log.e("type:::", type);
            if(type.equals("novel")){
                return (obj_array.get(i)).getAsJsonObject().get("object")
                        .getAsJsonObject().get("name").getAsString();
            }
        }
        return null;
    }


    /**
     *
     * @param result : Baidu语义解析工具返回的结果集，格式JSON.
     * @return String 音乐名称或相关关键字，提供给音乐搜索工具.
     * @throws JSONException
     */
    public static String isMusicInfo(String result) throws JSONException{
        JSONObject res = new JSONObject(result);
        String str = res.getJSONObject("content").getString("json_res");
        JsonArray obj_array = new JsonParser().parse(str).getAsJsonObject()
                .get("results").getAsJsonArray();

        Log.e("parse movie info", obj_array.toString());

        if(obj_array.size() <= 0) return null;
        for(int i = 0; i < obj_array.size(); i++){

            String type = (obj_array.get(i)).getAsJsonObject().get("domain").getAsString();
            Log.e("type:::", type);
            if(type.equals("music")){
                if(obj_array.get(i).getAsJsonObject().get("object")
                        .getAsJsonObject().has("composer")){
                    return obj_array.get(i).getAsJsonObject().get("object")
                            .getAsJsonObject().get("composer").getAsJsonArray().get(0).getAsString();
                }
                try {
                    String re =  (obj_array.get(i)).getAsJsonObject().get("object")
                            .getAsJsonObject().get("name").getAsString();
                    return re;
                }catch (Exception e){
                    return null;
                }

            }
        }
        return null;
    }


    /**
     * 内容是否与人物相关
     * @param result
     * @return
     * @throws JSONException
     */
    public static String isPersonInfo(String result) throws JSONException{
        JSONObject res = new JSONObject(result);
        String str = res.getJSONObject("content").getString("json_res");
        JsonArray obj_array = new JsonParser().parse(str).getAsJsonObject()
                .get("results").getAsJsonArray();

        Log.e("parse person info", obj_array.toString());

        if(obj_array.size() <= 0) return null;
        for(int i = 0; i < obj_array.size(); i++){

            String type = (obj_array.get(i)).getAsJsonObject().get("domain").getAsString();
            Log.e("type:::", type);
            if(type.equals("person")){
                return (obj_array.get(i)).getAsJsonObject().get("object")
                        .getAsJsonObject().get("person").getAsString();
            }
        }
        return null;
    }


    public static boolean isAboutTodo(String content){
        if((content.startsWith("明天")
                || content.startsWith("今天")
                || content.startsWith("后天"))
                && content.contains("点")){
            return true;
        } else if(content.contains("日") && content.contains("月") && content.contains("点")) {
            return true;
        }

        return false;
    }


    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

    /**
     * 汉字转数字
     * @param str
     * @return
     */
    public static int parseHourStr(String str){
        int hour = 0;
        switch (str.charAt(0)){
            case '一':
                return 1;
            case '二':
                return 2;
            case '三':
                return 3;
            case '四':
                return 4;
            case '五':
                return 5;
            case '六':
                return 6;
            case '七':
                return 7;
            case '八':
                return 8;
            case '九':
                return 9;
            case '十':
                hour = 10;
                break;
            default:
                return 0;
        }
        if(str.length()>1){
            if(str.charAt(1) == '一')
                hour += 1;
            if(str.charAt(1) == '二')
                hour += 2;
        }
        return hour;

    }

    //Convert Date to Calendar
    private Calendar dateToCalendar(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;

    }

    //Convert Calendar to Date
    private static Date calendarToDate(Calendar calendar) {
        return calendar.getTime();
    }


    /**
     * 提取待办事项-
     * @param content
     * @param context
     * @return
     * @throws ParseException
     */
    static public TodoManager.TodoItem extractTodoItem(String content, Context context) throws ParseException {
        if(!isAboutTodo(content)) return null;
        TodoManager todoManager = new TodoManager(context);

        /*
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss");
        formatter.setLenient(false);


        String oldTime = "2012-07-11 10:55:21";
         = formatter.format(new Date());
        long oldMillis = oldDate.getTime();


        Calendar c = Calendar.getInstance();
        //int seconds = c.get(Calendar.SECOND);
        c.
        */
        if(content.startsWith("今天")){
            String[] tokens = content.split("点");
            String hours_str = tokens[0].substring(2);
            int hours = 0;
            if(hours_str.startsWith("晚上")){
                hours += 12;
                hours_str = hours_str.substring(2);
            }
            hours += parseHourStr(hours_str);

            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR, hours);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);

            Date date = calendarToDate(c);

            TodoManager.TodoItem item = new TodoManager.TodoItem();
            item.date = String.valueOf(date.getTime());
            item.title = "EDI：待办事项";
            item.description = tokens[1];

            todoManager.addTodoItem(item);
        }
        if(content.startsWith("明天")){
            String[] tokens = content.split("点");
            String hours_str = tokens[0].substring(2);
            int hours = 0;
            if(hours_str.startsWith("晚上")){
                hours += 12;
                hours_str = hours_str.substring(2);
            }
            hours += parseHourStr(hours_str);

            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR, hours);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);

            Date date = addDays(calendarToDate(c), 1);

            TodoManager.TodoItem item = new TodoManager.TodoItem();
            item.date = String.valueOf(date.getTime());
            item.title = "EDI：待办事项";
            item.description = tokens[1];

            todoManager.addTodoItem(item);
        }

        if(content.startsWith("后天")){
            String[] tokens = content.split("点");
            String hours_str = tokens[0].substring(2);
            int hours = 0;
            if(hours_str.startsWith("晚上")){
                hours += 12;
                hours_str = hours_str.substring(2);
            }
            hours += parseHourStr(hours_str);

            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR, hours);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);

            Date date = addDays(calendarToDate(c), 2);

            TodoManager.TodoItem item = new TodoManager.TodoItem();
            item.date = String.valueOf(date.getTime());
            item.title = "EDI：待办事项";
            item.description = tokens[1];

            todoManager.addTodoItem(item);
        }
        if(content.contains("日") && content.contains("月") && content.contains("点")){



            String[] tokens = content.split("月");
            int month = parseHourStr(tokens[0]);
            content = tokens[1];
            tokens = content.split("日");
            /* fuck */



            String hours_str = tokens[0].substring(2);
            int hours = 0;
            if(hours_str.startsWith("晚上")){
                hours += 12;
                hours_str = hours_str.substring(2);
            }
            hours += parseHourStr(hours_str);

            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR, hours);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);

            Date date = addDays(calendarToDate(c), 2);

            TodoManager.TodoItem item = new TodoManager.TodoItem();
            item.date = String.valueOf(date.getTime());
            item.title = "EDI：待办事项";
            item.description = tokens[1];

            todoManager.addTodoItem(item);

        }
        return null;
    }

    /**
     * 是否是定时事件
     * @param content
     * @return
     */
    public static boolean isAlarmEvents(String content){
        if(content.contains("分钟后启动") || content.contains("分钟后打开")){
            return true;
        }
        return false;
    }


    /**
     * 提取待办事项信息，提取具体启动应用名称，新建待办事项，提交待办事项，
     * 启动待办事项Activity.
     * @param content
     * @param context
     */
    public static void extractAlarmEvents(String content, Context context){
        String[] tokens = null;
        if(content.contains("分钟后启动")) {
            tokens = content.split("分钟后启动");
        }else{
            tokens = content.split("分钟后打开");
        }
        int interval = parseHourStr(tokens[0]);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, interval);


        Intent intent = new NativePackageManager(context).getInstalledIntentByName(tokens[1]);
        if(intent == null){
            Toast.makeText(context, "没有应用"+ tokens[1],
                    Toast.LENGTH_LONG).show();
        }else {

            TodoManager.TodoItem item = new TodoManager.TodoItem();
            item.date = String.valueOf(calendar.getTimeInMillis());
            item.title = "启动应用 "+tokens[1];
            item.description = "";

            new TodoManager(context).addTodoItem(item);

            Intent i = new Intent(context, TodoActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }

}
