package com.cloud.runball.model;

import com.cloud.runball.bean.PKDataResp;
import com.cloud.runball.bean.PlayData;

import java.io.Serializable;
import java.util.List;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.model
 * @ClassName: PKDataRespModel
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/4/15 20:41
 * @UpdateUser: zhd
 * @UpdateDate: 2021/4/15 20:41
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class PKDataRespModel extends BasicResponse<PKDataRespModel> implements Serializable {
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<PKDataResp> getList() {
        return list;
    }

    public void setList(List<PKDataResp> list) {
        this.list = list;
    }

    private List<PKDataResp> list;

    @Override
    public String toString(){
        return ""+list.toString();
    }
}
