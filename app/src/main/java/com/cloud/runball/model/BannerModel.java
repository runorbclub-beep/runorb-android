package com.cloud.runball.model;
import com.cloud.runball.bean.BannerData;
import java.io.Serializable;
import java.util.List;

/**
 * 作者： zh
 * 时间： 2020/11/19 0015-上午 11:09
 * 描述： 基类
 * 来源：
 * @author ns467
 */
public class BannerModel extends BasicResponse<BannerModel>  implements Serializable {
    private int count;
    private List<BannerData> list;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<BannerData> getList() {
        return list;
    }

    public void setList(List<BannerData> list) {
        this.list = list;
    }

    @Override
    public String toString(){
       return "count="+count+";BannerModel.list=";
    }
}
