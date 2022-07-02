package com.yuyang.messi.room.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.yuyang.messi.room.entity.ModuleEntity;

import java.util.List;

/**
 * 数据库访问接口 所有增删改查等操作都在此声明
 */
@Dao
public interface ModuleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addModule(ModuleEntity... moduleEntities);

    @Delete
    void deleteModule(ModuleEntity... moduleEntities);

    @Update
    int updateModule(ModuleEntity... moduleEntities);

    @Query("delete from ModuleEntity where name = :name")
    void deleteModuleByName(String name);

//    @Query("SELECT * FROM ModuleEntity ORDER BY name DESC")
    @Query("SELECT * FROM ModuleEntity")
    List<ModuleEntity> getAllModule();

//    @Query("SELECT * FROM ModuleEntity ORDER BY name DESC")
    @Query("SELECT * FROM ModuleEntity")
    LiveData<List<ModuleEntity>> getAllModuleLive();

    @Query("delete from ModuleEntity")
    void clear();
}
