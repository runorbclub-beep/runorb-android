package com.cloud.runball.bean;

import com.contrarywind.interfaces.IPickerViewData;

public class PickerOption implements IPickerViewData {

    private String label;
    private int value;

    public PickerOption(String label, int value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String getPickerViewText() {
        return label;
    }

}
