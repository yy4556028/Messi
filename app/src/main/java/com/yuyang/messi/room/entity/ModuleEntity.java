package com.yuyang.messi.room.entity;

import android.app.Activity;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.yuyang.lib_base.BaseApp;
import com.yuyang.messi.room.typeconverter.ClassTypeConverter;

import java.io.Serializable;

@Entity(tableName = "ModuleEntity"
//        , indices = {@Index(value = {"name"}, unique = true)}//通过indices 将name字段设置成唯一约束
)
public class ModuleEntity implements Serializable {

    @PrimaryKey(autoGenerate = true)//主键 自增
    private int id;

    @ColumnInfo(name = "name") //实际数据库中的字段group_name
    private String name;

    private String icon;

    private String netUrl;

    //    @Ignore //忽略这个字段
    @TypeConverters(ClassTypeConverter.class)
    private Class<? extends Activity> clazz;

    @ColumnInfo(defaultValue = "0")
    private int badgeCount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getNetUrl() {
        return netUrl;
    }

    public void setNetUrl(String netUrl) {
        this.netUrl = netUrl;
    }

    public Class<? extends Activity> getClazz() {
        return clazz;
    }

    public void setClazz(Class<? extends Activity> clazz) {
        this.clazz = clazz;
    }

    public int getBadgeCount() {
        return badgeCount;
    }

    public void setBadgeCount(int badgeCount) {
        this.badgeCount = badgeCount;
    }



    public ModuleEntity() {
    }

    @Ignore
    public ModuleEntity(String name) {
        this.name = name;
    }

    @Ignore
    public ModuleEntity(String name, Class<? extends Activity> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    public int getIconRes() {
        if (icon == null) return 0;
        return BaseApp.getInstance().getResources().getIdentifier(icon, "drawable", BaseApp.getInstance().getPackageName());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ModuleEntity entity = (ModuleEntity) obj;

        return name.equals(entity.name);
    }
}
