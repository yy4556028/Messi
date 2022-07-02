package com.yuyang.messi.room.database;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.yuyang.lib_base.utils.LogUtil;
import com.yuyang.messi.MessiApp;
import com.yuyang.messi.room.dao.ModuleDao;
import com.yuyang.messi.room.entity.ModuleEntity;

import java.io.File;

/**
 * data/data/packageName/databases
 */
@Database(entities = {ModuleEntity.class}, version = 1, exportSchema = false)
public abstract class ModuleDatabase extends RoomDatabase {

    private static ModuleDatabase database;

    public static synchronized ModuleDatabase getInstance() {
        if (database == null) {
            database = Room.databaseBuilder(MessiApp.getInstance(), ModuleDatabase.class, "ModuleEntity")
                    .allowMainThreadQueries()//操作数据库必须在子线程中，为了简单此处直接使用allowMainThreadQueries()强制在主线程运行，正常开发不允许
                    .build();
        }
        return database;
    }

    public abstract ModuleDao getModuleDao();
    // 若entities有多个实例，则应该写多个Dao

    /**
     * 升降级
     */

    public void UpgradeForce() {
        ModuleDatabase moduleDatabase = Room.databaseBuilder(MessiApp.getInstance(), ModuleDatabase.class, "ModuleEntity")
                .fallbackToDestructiveMigration() //升级破坏式: 当数据库版本变化时不做数据迁移,直接删除原有数据库
                .fallbackToDestructiveMigrationOnDowngrade()//降级的时候,当未匹配到版本的时候就会直接删除表然后重新创建
                .build();
    }

    public void UpgradeAdd() {
        Migration migration = new Migration(2, 3) {
            @Override
            public void migrate(@NonNull SupportSQLiteDatabase database) {
                database.execSQL("ALTER TABLE user ADD COLUMN type INTEGER NOT NULL DEFAULT 1");
            }
        };
        ModuleDatabase moduleDatabase = Room.databaseBuilder(MessiApp.getInstance(), ModuleDatabase.class, "ModuleEntity")
                .addMigrations(migration) //保留原有数据
                .build();
    }

    /**
     * sqlLet没有删除字段语句，只能创建新的数据库定义需要的字段，将原有数据库数据复制过去，删除旧数据库后再将新数据库重命名
     */
    public void UpgradeDelete() {
        Migration migration = new Migration(3, 4) {
            @Override
            public void migrate(@NonNull SupportSQLiteDatabase database) {
                database.execSQL("CREATE TABLE ModuleEntity_temp (id INTEGER PRIMARY KEY NOT NULL , group_name TEXT," + "name TEXT)");
                database.execSQL("INSERT INTO ModuleEntity_temp (id, group_name, name) " + "SELECT id, group_name, name FROM ModuleEntity");
                database.execSQL("DROP TABLE ModuleEntity");
                database.execSQL("ALTER TABLE ModuleEntity_temp RENAME to ModuleEntity");
            }
        };
        ModuleDatabase moduleDatabase = Room.databaseBuilder(MessiApp.getInstance(), ModuleDatabase.class, "ModuleEntity")
                .addMigrations(migration) //保留原有数据
                .build();
    }

    public void deleteTable() {
        File dataBaseDir = new File(MessiApp.getInstance().getApplicationInfo().dataDir + "/databases");
        File[] fileList = dataBaseDir.listFiles();
        for (File file : fileList) {
            if (file.getName().startsWith("ModuleEntity")) {
                if (file.delete()) {
                    LogUtil.e(ModuleDatabase.class.getSimpleName(), file + " deleted");
                } else {
                    LogUtil.e(ModuleDatabase.class.getSimpleName(), file + " delete fail");
                }
                LogUtil.e("dataBaseDir qqq", file.getName());
            }
        }
    }
}