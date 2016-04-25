package cn.jasonlv.siri.utility;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import cn.jasonlv.siri.activity.MainActivity;
import cn.jasonlv.siri.R;
import cn.jasonlv.siri.db.DBManager;

//import android.app.NotificationManager;

/**
 * Created by Administrator on 2015/7/11.
 */
public class TodoManager {

    static Context context;
    static NotificationManager mNotificationManager;
    static DBManager dbManager;

    public TodoManager(Context c){
        context = c;
        mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        dbManager = DBManager.getInstance(context);

    }

    static public  class TodoItem {
        public int id;
        public String title;
        public String description;
        public String date;
    }

    public void setAlarmTime(Context context,  long timeInMillis) {
        AlarmManager alarmMgr;
        PendingIntent alarmIntent;
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        // Set the alarm to start at 8:30 a.m.
        //Calendar calendar = Calendar.getInstance();
        //calendar.setTimeInMillis(System.currentTimeMillis());
        //calendar.set(Calendar.HOUR_OF_DAY, 23);
        //calendar.set(Calendar.MINUTE, 9);
        Log.e("dddddddddddddd", "d");

        // setRepeating() lets you specify a precise custom interval--in this case,
        // 20 minutes.

        //Log.e("dsaddd", String.valueOf(calendar.getTimeInMillis()));
        Log.e("dsadddsaddsaadsd", String.valueOf(timeInMillis));

        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, timeInMillis,
                1000 * 60 * 20, alarmIntent);
    }

     static public class AlarmReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            /*
            if ("android.alarm.demo.action".equals(intent.getAction())) {
                //第1步中设置的闹铃时间到，这里可以弹出闹铃提示并播放响铃
                //可以继续设置下一次闹铃时间;
                TodoItem item = dbManager.getLatestItem();
                showNotify(item.title, item.description);
                Log.e("todoManager e", "fuck the notify");
                dbManager.deleteOldItem();
            }
            */
            Log.e("todoManager e", "fuck the notify");
            TodoItem item = dbManager.getLatestItem();
            if(item != null) {
                showNotify(item.title, item.description);
                if(item.title.startsWith("启动应用 ")){
                    Intent i = new NativePackageManager(context).getInstalledIntentByName(item.title.substring(5));
                    if(i != null) {
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                    }

                }
            }
            dbManager.deleteOldItem();
        }
    }

    static public void showNotify(String title, String description){

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.mic14)
                        .setContentTitle(title)
                        .setContentText(description);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        //Vibration
        mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(2, mBuilder.build());
    }

    public PendingIntent getDefalutIntent(int flags){
        PendingIntent pendingIntent= PendingIntent.getActivity(context, 1, new Intent(), flags);
        return pendingIntent;
    }

    public void addTodoItem(TodoItem item){
        // add it to database.
        dbManager.add(item);

        // set the alarm event.
        setAlarmTime(context, Long.valueOf(item.date));

    }
}
