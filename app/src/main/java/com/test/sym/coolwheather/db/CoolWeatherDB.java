package com.test.sym.coolwheather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.test.sym.coolwheather.model.City;
import com.test.sym.coolwheather.model.County;
import com.test.sym.coolwheather.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuran on 16/3/16.
 */
public class CoolWeatherDB {
    public static final String DB_NAME = "cool_weather";
    public static final int VERSION =1;
    private static CoolWeatherDB coolWeatherDB;
    private SQLiteDatabase db;

    // construction method privatization
    private CoolWeatherDB(Context context) {
        CoolWeatherOpenHelper dbHelper =new CoolWeatherOpenHelper(context,DB_NAME,null,VERSION);
        db = dbHelper.getWritableDatabase();
    }
    //get the db instance
    public synchronized static CoolWeatherDB getInstance(Context context) {
        if(coolWeatherDB == null) {
            coolWeatherDB = new CoolWeatherDB(context);
        }
        return coolWeatherDB;
    }
    // save Province information into db
    public void saveProvince(Province province) {
        if (province != null) {
            ContentValues values = new ContentValues();
            values.put("province_name",province.getProvinceName());
            values.put("province_code",province.getProvinceCode());
            db.insert("Province", null, values);
        }
    }
    //get all province information from db
    public List<Province> loadProvinces() {
        List<Province> list  =new ArrayList<Province>();
        Cursor cursor = db.query("Province",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do {
                Province province =new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                list.add(province);
            } while (cursor.moveToNext());
        }
        if (cursor !=null) {
            cursor.close();
        }
        return list;
    }

    //save city information into db
    public void saveCity(City city) {
        if(city != null) {
            ContentValues values = new ContentValues();
            values.put("city_name",city.getCityName());
            values.put("city_code",city.getCityCode());
            values.put("province_id",city.getProvinceId());
            db.insert("City", null, values);
        }
    }
    //get city information from db
    public List loadCities(int provinceId) {
        List<City> list =new ArrayList<City>();
        String[] sProvinceId = new String[]{String.valueOf(provinceId)};
        //Cursor cursor =db.query("City",null,"province_id = ?",new String[]{String.valueOf(provinceId)},null,null,null);
        //Cursor cursor = db.query("City",null,null,null,null,null,null);
        Cursor cursor =db.query("City",null,"province_id = ?",sProvinceId,null,null,null,null);

        if (cursor.moveToFirst()) {
            do {
                City city =new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setProvinceId(provinceId);
                list.add(city);
            } while (cursor.moveToNext());

        }
        if (cursor !=null) {
            cursor.close();
        }
        return list;

    }
    // save county into db
    public void saveCounty(County county) {
        if (county !=null) {
            ContentValues values = new ContentValues();
            values.put("county_name",county.getCountyName());
            values.put("county_code",county.getCountyCode());
            values.put("city_id",county.getCityId());
            db.insert("county",null,values);
        }
    }
    public List loadCounties(int cityId) {
        List<County> list =new ArrayList<County>();
        Cursor cursor =db.query("County",null,"city_id = ?",new String[]{String.valueOf(cityId)},null,null,null,null);
        if (cursor.moveToFirst()) {
            do{
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCityId(cityId);
                list.add(county);
            } while (cursor.moveToNext());

        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }



}
