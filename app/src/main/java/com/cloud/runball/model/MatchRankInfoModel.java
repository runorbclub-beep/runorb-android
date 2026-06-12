package com.cloud.runball.model;

import com.cloud.runball.bean.MatchRankInfoData;
import java.io.Serializable;
import java.util.List;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.model
 * @ClassName: MatchRankInfoModel
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/5/22 18:08
 * @UpdateUser: zhd
 * @UpdateDate: 2021/5/22 18:08
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MatchRankInfoModel extends BasicResponse<MatchRankInfoModel> implements Serializable {
    private int count;
    private List<MatchRankInfoData> list;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<MatchRankInfoData> getList() {
        return list;
    }

    public void setList(List<MatchRankInfoData> list) {
        this.list = list;
    }
}
