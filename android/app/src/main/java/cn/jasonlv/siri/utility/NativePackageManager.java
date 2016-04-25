package cn.jasonlv.siri.utility;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;

import java.util.List;

/**
 * Created by Administrator on 2015/6/29.
 */

/**
 * 本地应用管理器.
 */
public class NativePackageManager {
    static private Context mContext;
    static private List<PackageInfo> mPacks;

    public NativePackageManager(Context context){
        mContext = context;
        mPacks = mContext.getPackageManager().getInstalledPackages(0);
    }

    /**
     * 根据名称，获得相应应用的Intent.， 若无，返回null.
     * @param name
     * @return Intent
     */
    public Intent getInstalledIntentByName(String name) {

        for(int i=0;i<mPacks.size();i++) {
            PackageInfo p = mPacks.get(i);
            if ((p.versionName == null)) {
                continue ;
            }
            if(p.applicationInfo.loadLabel(mContext.getPackageManager()).toString().equals(name)){
                Intent LaunchIntent = mContext.getPackageManager().getLaunchIntentForPackage(p.packageName);
                return LaunchIntent;
            };
        }
        return null;
    }
}
