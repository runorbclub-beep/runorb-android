package com.cloud.runball.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.utils
 * @ClassName: GetJsonDataUtil
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/2/8 11:46
 * @UpdateUser: zhd
 * @UpdateDate: 2021/2/8 11:46
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class GetJsonDataUtil {
    public String getJson(Context context, String fileName) {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
