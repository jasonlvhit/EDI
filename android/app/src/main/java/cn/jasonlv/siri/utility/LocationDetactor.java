package cn.jasonlv.siri.utility;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Jason Lyu on 2015/6/27.
 *
 * 定位管理器，实时获取当前的位置信息，全局后台运行.
 */
public class LocationDetactor implements LocationListener {
    private final static String LOG_TAG = LocationDetactor.class.getSimpleName();

    protected LocationManager locationManager;
    protected Context mContext;

    // 经度
    private double a;
    // 纬度
    private double o;

    public LocationDetactor(Context context) {
        mContext = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(LOG_TAG, "Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
        a = location.getLatitude();
        o = location.getLongitude();
    }

    public class LocationInfo {
        public String lat;
        public String lon;

        public LocationInfo(String lat_, String lon_){
            lat = lat_;
            lon = lon_;
        }

    }

    public LocationInfo getLocationInfo(){
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        return new LocationInfo(String.valueOf(a), String.valueOf(o));
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude", "status");
    }
}
