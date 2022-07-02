package com.yuyang.messi.bean;

import com.yuyang.messi.room.entity.ModuleEntity;

import java.io.Serializable;
import java.util.List;

public class ModuleGroupBean implements Serializable {

    private String groupName;
    private List<ModuleEntity> itemList;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<ModuleEntity> getItemList() {
        return itemList;
    }

    public void setItemList(List<ModuleEntity> itemList) {
        this.itemList = itemList;
    }
}
