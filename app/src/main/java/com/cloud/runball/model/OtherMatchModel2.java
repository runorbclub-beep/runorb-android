package com.cloud.runball.model;

import com.cloud.runball.bean.OtherMatchInfo;

import java.io.Serializable;
import java.util.List;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.model
 * @ClassName: OtherMatchModel2
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/7/22 16:40
 * @UpdateUser: zhd
 * @UpdateDate: 2021/7/22 16:40
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class OtherMatchModel2 extends  BasicResponse<OtherMatchModel2> implements Serializable {

    private int count=0;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<OtherMatchInfo> getList() {
        return list;
    }

    public void setList(List<OtherMatchInfo> list) {
        this.list = list;
    }

    private List<OtherMatchInfo> list;


}
