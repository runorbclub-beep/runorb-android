package com.cloud.runball.model;

import java.io.Serializable;
import java.util.List;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.model
 * @ClassName: PKDataDetailModel
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/4/16 11:30
 * @UpdateUser: zhd
 * @UpdateDate: 2021/4/16 11:30
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class PKDataDetailModel implements Serializable {


    /**
     * pk_type : 0
     * list : [{"user_pk_list_id":"40932192271994880","duration":178,"user_group":"red","group_win":"","distance":0,"speed_max":0,"user_id":"34717147481509888","user_name":"Wangke","pk_type":0,"created_time":1619506574,"user_img":"https://api-all-sporter.megacombine.com/user_image/2021/04/2021-04-24/user_img","start_date":"2021.04.27 14:56","is_win":0}]
     */

    private int pk_type;
    private List<ListPkItem> list;

    public int getPk_type() {
        return pk_type;
    }

    public void setPk_type(int pk_type) {
        this.pk_type = pk_type;
    }

    public List<ListPkItem> getList() {
        return list;
    }

    public void setList(List<ListPkItem> list) {
        this.list = list;
    }

}
