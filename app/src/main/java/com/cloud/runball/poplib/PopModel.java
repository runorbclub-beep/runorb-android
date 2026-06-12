package com.cloud.runball.poplib;



import androidx.annotation.DrawableRes;

import java.io.Serializable;

/**
 *
 * @author zhanglifeng
 * @date 2017/3/28
 * Pop Data Model
 */

public class PopModel implements Serializable {

    private int drawableId;
    private String itemDesc;

    public PopModel(){}
    public PopModel(String desc){
        this.itemDesc=desc;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(@DrawableRes int drawableId) {
        this.drawableId = drawableId;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

}
