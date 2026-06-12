package com.cloud.runball.bean;


import com.contrarywind.interfaces.IPickerViewData;

import java.util.List;

/**
 * @author:
 * @date: 2017/3/16 15:36
 */

public class JsonBean2 implements IPickerViewData {


    private String name;
    private String code;
    private List<CityBean> child;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<CityBean> getCityList() {
        return child;
    }

    public void setCityList(List<CityBean> child) {
        this.child = child;
    }

    // 实现 IPickerViewData 接口，
    // 这个用来显示在PickerView上面的字符串，
    // PickerView会通过IPickerViewData获取getPickerViewText方法显示出来。
    @Override
    public String getPickerViewText() {
        return this.name;
    }



    public static class CityBean {


        private String name;
        private String code;
        private List<ChildBean> child;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public List<ChildBean> getArea() {
            return child;
        }

        public void setArea(List<ChildBean> child) {
            this.child = child;
        }
    }


    public static class ChildBean{
        private String name;
        private String code;
        private List<ChildBean> child;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public List<ChildBean> getChild() {
            return child;
        }

        public void setChild(List<ChildBean> child) {
            this.child = child;
        }

        @Override
        public String toString(){
            return name;
        }
    }

}
