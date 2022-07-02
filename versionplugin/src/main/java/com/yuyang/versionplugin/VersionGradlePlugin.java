package com.yuyang.versionplugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class VersionGradlePlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        System.out.println("VersionGradlePlugin 打印测试");
    }
}
