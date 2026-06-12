package com.cloud.runball.bean;

import com.contrarywind.interfaces.IPickerViewData;

public class SexOption implements IPickerViewData {

    private String code;

    private String name;

    public SexOption(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getPickerViewText() {
        return name;
    }

}
