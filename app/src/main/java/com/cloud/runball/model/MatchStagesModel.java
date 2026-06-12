package com.cloud.runball.model;

import com.cloud.runball.bean.MatchStageData;

import java.io.Serializable;
import java.util.List;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.model
 * @ClassName: matchStagesModel
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/5/22 17:55
 * @UpdateUser: zhd
 * @UpdateDate: 2021/5/22 17:55
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MatchStagesModel extends BasicResponse<MatchStagesModel> implements Serializable {

    private int count;
    private List<MatchStageData> list;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<MatchStageData> getList() {
        return list;
    }

    public void setList(List<MatchStageData> list) {
        this.list = list;
    }

}
