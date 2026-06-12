package com.cloud.runball.bean;

import java.io.Serializable;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.bean
 * @ClassName: CountryCodeInfo
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/6/8 11:05
 * @UpdateUser: zhd
 * @UpdateDate: 2021/6/8 11:05
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class CountryCodeInfo implements Serializable {


    /**
     * name_cn : 中国
     * name_en : China
     * code : 86
     */

    private String name_cn;
    private String name_en;
    private String code;

    public String getName_cn() {
        return name_cn;
    }

    public void setName_cn(String name_cn) {
        this.name_cn = name_cn;
    }

    public String getName_en() {
        return name_en;
    }

    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
