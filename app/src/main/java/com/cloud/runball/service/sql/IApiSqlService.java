package com.cloud.runball.service.sql;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.cloud.runball.service.sql.entity.PlayInfo;
import com.cloud.runball.service.sql.entity.SpeedDetail;

import java.util.List;

@Dao
public interface IApiSqlService {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdateSpeedDetail(SpeedDetail data);

    @Query("select * from tb_speed_detail where tb_user_play_id = :userPlayId")
    List<SpeedDetail>querySpeedDetail(long userPlayId);

    @Query("delete from tb_speed_detail where tb_user_play_id = :userPlayId")
    void deleteSpeedDetail(long userPlayId);

    @Query("delete from tb_speed_detail")
    void deleteSpeedDetail();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertOrUpdatePlayInfo(PlayInfo data);

    @Update
    int updatePlayInfo(PlayInfo data);

    @Query("select * from tb_play_info where source = :playType and upload_status in ('0', '1', '2', '4')")
    List<PlayInfo> queryPlayInfoPopupList(int playType);

    @Query("select * from tb_play_info where upload_status in ('0', '1', '2', '4')")
    List<PlayInfo> queryAllPlayInfoList();

    @Query("select * from tb_play_info where tb_sql_id = :sqlId")
    PlayInfo queryPlayInfo(long sqlId);

    @Query("delete from tb_play_info where tb_sql_id = :sqlId")
    void deletePlayInfo(long sqlId);

    @Query("delete from tb_play_info ")
    void deletePlayInfo();

//    and :startDate < stop_time and :endDate > stop_time , long startDate, long endDate
    @Query("select * from tb_play_info where source in (:source) and upload_status in ('0', '1', '2', '4') and :startDate < stop_time and :endDate > stop_time ")
    List<PlayInfo> queryPlayInfoList(int[] source, long startDate, long endDate);

}
