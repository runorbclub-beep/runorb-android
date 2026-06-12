package com.cloud.runball.service.sql;

import androidx.annotation.NonNull;
import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.cloud.runball.App;
import com.cloud.runball.service.sql.entity.PlayInfo;
import com.cloud.runball.service.sql.entity.SpeedDetail;

@Database(
    entities = { PlayInfo.class, SpeedDetail.class },
    version = 2,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

  private static AppDatabase instance;

  public static AppDatabase getInstance() {
    if (instance == null) {
      instance = Room
          .databaseBuilder(App.self(), AppDatabase.class, "play_data_1020206.db")
          .addMigrations(new Migration(1, 2) {
            @Override
            public void migrate(@NonNull SupportSQLiteDatabase database) {
              database.execSQL("ALTER TABLE tb_play_info ADD COLUMN exponent_speed_max INT NOT NULL DEFAULT 0");
            }
          })
          .allowMainThreadQueries()
          .build();
    }
    return instance;
  }

  public abstract IApiSqlService apiSqlService();

}
