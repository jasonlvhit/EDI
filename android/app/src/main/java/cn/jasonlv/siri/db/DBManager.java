package cn.jasonlv.siri.db;

/**
 * Created by Administrator on 2015/7/01.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import cn.jasonlv.siri.utility.TodoManager;
import cn.jasonlv.siri.utility.TodoManager.TodoItem;


/**
 * DBManager 管理所有的数据库操作，提供简单接口.
 */
public class DBManager {
    private DBHelper helper;
    private SQLiteDatabase db;

    static private DBManager mInstance = null;

    public DBManager(Context context) {
        helper = DBHelper.getInstance(context);
        //helper = new DBHelper(context);
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
    }

    /**
     * 多个 DB 实例会导致同步问题
     * @param context
     * @return DBManager
     */
    static public DBManager getInstance(Context context){
        if(mInstance == null){
            mInstance = new DBManager(context);
        }
        return mInstance;
    }


    /**
     * add todo item
     * @param item
     */
    public void add(TodoItem item) {
        db.beginTransaction();	//开始事务
        Log.e("da", String.valueOf(item.date));
        try {

            db.execSQL("INSERT INTO todo (title, description, date) VALUES(?, ?,"+String.valueOf(item.date) +")", new Object[]{item.title, item.description});

            db.setTransactionSuccessful();	//设置事务成功完成
        } finally {
            db.endTransaction();	//结束事务
        }
    }

    /**
     *  删除最旧的待办事项
     */
    public void deleteOldItem() {
        db.execSQL("DELETE FROM" +
                " todo" +
                " WHERE" +
                " _id = (SELECT MIN(_id) FROM todo)");
    }

    public void deleteItemById(int id){
        db.execSQL("DELETE from todo where _id="+String.valueOf(id));

    }

    /**
     * query all items, return list
     * @return List<TodoItem>
     */
    public ArrayList<TodoItem> query() {
        ArrayList<TodoManager.TodoItem> items = new ArrayList<TodoManager.TodoItem>();
        Cursor c = null;
        try {
            c = db.rawQuery("SELECT * FROM todo", null);
            while (c.moveToNext()) {
                TodoItem item = new TodoItem();
                item.id = c.getInt(c.getColumnIndex("_id"));
                item.title = c.getString(c.getColumnIndex("title"));
                item.description = c.getString(c.getColumnIndex("description"));
                item.date = c.getString(c.getColumnIndex("date"));
                items.add(item);
            }
        }finally {
            c.close();
        }

        return items;
    }


    /**
     * 返回最后的待办事项
     * @return TodoItem
     */
    public TodoItem getLatestItem() {
        Cursor c = null;
        try {
            c = db.rawQuery("SELECT * FROM todo ORDER BY date ASC LIMIT 1", null);
            if (c.moveToFirst()) {
                TodoItem item = new TodoItem();
                item.id = c.getInt(c.getColumnIndex("_id"));
                item.title = c.getString(c.getColumnIndex("title"));
                item.description = c.getString(c.getColumnIndex("description"));
                item.date = c.getString(c.getColumnIndex("date"));
                return item;
            }
        }finally {
            c.close();
        }

        return null;
    }

    /**
     * close the database.
     */
    public void closeDB() {
        db.close();
    }
}
