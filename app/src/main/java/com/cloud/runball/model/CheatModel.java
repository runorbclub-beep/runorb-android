package com.cloud.runball.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.model
 * @ClassName: CheatModel
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/5/22 11:01
 * @UpdateUser: zhd
 * @UpdateDate: 2021/5/22 11:01
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class CheatModel extends BasicResponse<CheatModel> implements Serializable {
    public List<ErrSpeed> getErr_speed() {
        return err_speed;
    }

    public void setErr_speed(List<ErrSpeed> err_speed) {
        this.err_speed = err_speed;
    }

    public List<ErrSpeed> err_speed=new ArrayList<>();

    public int getInit_circle_count() {
        return init_circle_count;
    }

    public void setInit_circle_count(int init_circle_count) {
        this.init_circle_count = init_circle_count;
    }

    private int init_circle_count=28;

    @Override
    public String toString(){
        return err_speed.toString()+";size="+err_speed.size();
    }

}
