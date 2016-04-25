package cn.jasonlv.siri.utility;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/7/01.
 */

/**
 * 通讯录管理工具，搜索匹配本地通讯录信息.
 */
public class ContactsManager {
    Context mContext;


    public class Contact {
        public String name;
        public String number;

        Contact(String n, String nu){
            name = n;
            number = nu;
        }
    }


    public ContactsManager(Context context){
        mContext = context;
    }

    /**
     * 返回匹配的通讯录信息
     * @param list ： 语音识别的若干结果
     * @return Contact 匹配的通讯录信息
     */
    public Contact getContactInfo(ArrayList<String> list){
        ContentResolver cr = mContext.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        //Toast.makeText(NativeContentProvider.this, "Name: " + name + ", Phone No: " + phoneNo, Toast.LENGTH_SHORT).show();
                        //System.out.println("Name: " + name + ", Phone No: " + phoneNo);
                        for(String l : list){
                            System.out.println(name+" "+l);
                            //System.out.println()
                            if(l.equals(name)){

                                return new Contact(name, phoneNo);
                            }
                        }
                    }
                    pCur.close();
                }
            }
        }
        return null;
    }


    /**
     * 根据姓名返回相应电话号码，若无，返回null
     * @param pname 通讯录姓名，
     * @return String 电话号码.
     */
    public String getContactNumber(String pname){

        Log.e("hhhh", pname);

        ContentResolver cr = mContext.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        //Toast.makeText(NativeContentProvider.this, "Name: " + name + ", Phone No: " + phoneNo, Toast.LENGTH_SHORT).show();
                        //System.out.println("Name: " + name + ", Phone No: " + phoneNo);

                        if(pname.equals(name)){
                            Log.e("dsdasd", name + ":"+ phoneNo);
                            return phoneNo;
                        }

                    }
                    pCur.close();
                }
            }
        }
        return null;
    }


    /**
     * 使用本地Contact的Content Provider，显示所有的通讯录信息，姓名和电话.
     */
    public void getContactList(){
        ContentResolver cr = mContext.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        //Toast.makeText(NativeContentProvider.this, "Name: " + name + ", Phone No: " + phoneNo, Toast.LENGTH_SHORT).show();
                        System.out.println("Name: " + name + ", Phone No: " + phoneNo);
                    }
                    pCur.close();
                }
            }
        }
    }
}
