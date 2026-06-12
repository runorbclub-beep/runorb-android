package com.cloud.runball.model;

import com.cloud.runball.bean.Achievement;
import com.cloud.runball.bean.ChartData;
import com.cloud.runball.bean.RankInfo;

import java.io.Serializable;
import java.util.List;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.model
 * @ClassName: RankModel
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/7/13 14:08
 * @UpdateUser: zhd
 * @UpdateDate: 2021/7/13 14:08
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class RankModel extends BasicResponse<RankModel> implements Serializable {

    int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<RankInfo> getList() {
        return list;
    }

    public void setList(List<RankInfo> list) {
        this.list = list;
    }

    List<RankInfo> list;


}
