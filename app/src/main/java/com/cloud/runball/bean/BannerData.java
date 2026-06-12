package com.cloud.runball.bean;

import java.io.Serializable;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.bean
 * @ClassName: BannerData
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/5/25 10:22
 * @UpdateUser: zhd
 * @UpdateDate: 2021/5/25 10:22
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class BannerData implements Serializable {


    /**
     * matchs_banner_id : 50741580218241024
     * img_path : http://10.20.73.103:89/matchs_image/2021/05/2021-05-20/competition.png
     * banner_matchs_id : 48988132351152128
     */

    private String matchs_banner_id;
    private String img_path;
    private String banner_matchs_id;
    private int is_quartets;

    public String getMatchs_banner_id() {
        return matchs_banner_id;
    }

    public void setMatchs_banner_id(String matchs_banner_id) {
        this.matchs_banner_id = matchs_banner_id;
    }

    public String getImg_path() {
        return img_path;
    }

    public void setImg_path(String img_path) {
        this.img_path = img_path;
    }

    public String getBanner_matchs_id() {
        return banner_matchs_id;
    }

    public void setBanner_matchs_id(String banner_matchs_id) {
        this.banner_matchs_id = banner_matchs_id;
    }

    public int getIs_quartets() {
        return is_quartets;
    }

    public void setIs_quartets(int is_quartets) {
        this.is_quartets = is_quartets;
    }
}
