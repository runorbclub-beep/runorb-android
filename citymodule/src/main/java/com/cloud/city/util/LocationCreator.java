package com.cloud.city.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.core.app.ActivityCompat;
import com.cloud.runball.basecomm.utils.RxBus;
import java.io.IOException;
import java.util.List;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.utils
 * @ClassName: LocationUtils
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/7/8 15:41
 * @UpdateUser: zhd
 * @UpdateDate: 2021/7/8 15:41
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class LocationCreator {
    public  String cityName;
    private  Geocoder geocoder;
    private Context mContext;
    LocationManager locationManager;

    private static volatile LocationCreator instance;

    private LocationCreator(Context context){
        this.mContext=context;
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
    }

    public static LocationCreator self(Context context) {
        if (instance == null) {
            synchronized (LocationCreator.class) {
                if (instance == null) {
                    instance = new LocationCreator(context);
                }
            }
        }
        return instance;
    }


    final LocationListener locationListener=new LocationListener(){
        @Override
        public void onLocationChanged(Location location) {
//            String tempCityName = updateWithNewLocation(location);
//            if(!TextUtils.isEmpty(tempCityName)){
//                RxBus.getDefault().post(tempCityName);
//            }
            Address address = updateWithNewLocation(location);
            if(address != null){
                RxBus.getDefault().post(address);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    /**
     * 通过地理坐标获取城市名 其中CN分别是city和name的首字母缩写
     */
    public  boolean executeCNBylocation() {
        geocoder = new Geocoder(mContext);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        //通过最后一次的地理位置来获取Location对象
        if (ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30*1000, 50, locationListener);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//            String queryed_name = updateWithNewLocation(location);
//            if((queryed_name!=null)&&(0!=queryed_name.length())){
//                cityName = queryed_name;
//                RxBus.getDefault().post(cityName);
//            }
            Address address = updateWithNewLocation(location);
            if(address!=null) {
                RxBus.getDefault().post(address);
            }
            return true;
        }
        return false;
    }



    public void removeCityLocationListener(){
        if(locationListener!=null){
            locationManager.removeUpdates(locationListener);
        }
    }

    /**
     * 更新location  return cityName
     * @param location
     * @return
     */
    public Address updateWithNewLocation(Location location){
//        String mcityName = "";
        double lat = 0;
        double lng = 0;
        List<Address> addList = null;
        if(location!=null){
            lat = location.getLatitude();
            lng = location.getLongitude();
        }else{
            cityName = "无法获取地理信息";
        }
        try {
            addList = geocoder.getFromLocation(lat, lng, 1);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }


//        cityName = addList.get(0).getCountryCode();
        if (addList == null) {
            return null;
        }
        if (addList.size() > 0) {
            return addList.get(0);
        }
        return null;

//        if(addList!=null&&addList.size()>0){
//            for(int i=0;i<addList.size();i++){
//                Address add = addList.get(i);
//                mcityName += add.getLocality();
//            }
//        }
//        if(mcityName.length()!=0){
//            return mcityName.substring(0, (mcityName.length()-1));
//        }else{
//            return mcityName;
//        }
    }
}
