package com.cloud.runball.model;

import java.io.Serializable;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.model
 * @ClassName: AdModel
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/6/25 15:59
 * @UpdateUser: zhd
 * @UpdateDate: 2021/6/25 15:59
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class AdModel extends BasicResponse<BannerModel>  implements Serializable {

    private String img_375_812;

    public String getImg_375_812() {
        return img_375_812;
    }

    public void setImg_375_812(String img_375_812) {
        this.img_375_812 = img_375_812;
    }

    public String getImg_414_896() {
        return img_414_896;
    }

    public void setImg_414_896(String img_414_896) {
        this.img_414_896 = img_414_896;
    }

    private String img_414_896;

}
