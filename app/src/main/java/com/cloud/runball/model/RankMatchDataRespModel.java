package com.cloud.runball.model;

import java.io.Serializable;
import java.util.List;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.model
 * @ClassName: RankMatchDataRespModel
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/5/20 19:14
 * @UpdateUser: zhd
 * @UpdateDate: 2021/5/20 19:14
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class RankMatchDataRespModel extends BasicResponse<RankMatchDataRespModel> implements Serializable {

    private int count;
    private List<RankMatchDataModel> list;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<RankMatchDataModel> getList() {
        return list;
    }

    public void setList(List<RankMatchDataModel> list) {
        this.list = list;
    }


}
