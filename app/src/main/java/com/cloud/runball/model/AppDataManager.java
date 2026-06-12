package com.cloud.runball.model;

import com.cloud.runball.bean.CountryCodeInfo;
import com.cloud.runball.bean.UserPlay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class AppDataManager {

    private AppDataManager(){}

    private static AppDataManager instance;

    public static AppDataManager getInstance() {
        if (instance == null) {
            synchronized (AppDataManager.class) {
                if (instance == null) {
                    instance = new AppDataManager();
                }
            }
        }
        return instance ;
    }

    UserInfoModel userInfoModel;
    public void setUserInfoModel(UserInfoModel model){
          this.userInfoModel=model;
    }

    public UserInfoModel getUserInfoModel(){
        return this.userInfoModel;
    }

    //运动结束
    PlayOverModel mPlayOverModel;
    public void setPlayOverModel(PlayOverModel model){
      this.mPlayOverModel=model;
    }

    public PlayOverModel getPlayOverModel(){
        return this.mPlayOverModel;
    }

    String language="";
    public void setLanguage(String language){
        if(language.startsWith("zh")){

        }else if(language.startsWith("en") || language.equalsIgnoreCase("en-rUS")){
            language="en";
        }else if(language.startsWith("ja")){

        }
        this.language=language;
    }


    public String getCountry(){
        if(language.startsWith("zh")){
          return "chinese";
        }else if(language.startsWith("en")){
            return "english";
        }else if(language.startsWith("ja")){
            return "japanese";
        }
        return "chinese";
    }

    private String androidId;
    public void setAndroidId(String androidId){
        this.androidId=androidId;
    }

    public String getAndroidId(){
        return androidId;
    }


    List<CountryCodeInfo> countryCodes=new ArrayList<>();
    public void clearCountryCodes(){
        countryCodes.clear();
    }

    public void putAllCountryCodes(List<CountryCodeInfo> list){
        countryCodes.addAll(list);
    }

    public  List<CountryCodeInfo> getCountryCodes(){
        return countryCodes;
    }

    //--------------------------------赛事列表-------------------------------------------



    //-------------------------------作弊配置-----------------------------------------

    List<ErrSpeed> errSpeeds=new ArrayList<>();
    public List<ErrSpeed> getErrSpeeds(){
        return errSpeeds;
    }

    public void addAllErrSpeeds(Collection<ErrSpeed> c){
        errSpeeds.addAll(c);
    }
    AdModel model;
    public void setAdSplashData(AdModel data){
        this.model=data;
    }

    public AdModel getSpalshDate(){
        return model;
    }

}
