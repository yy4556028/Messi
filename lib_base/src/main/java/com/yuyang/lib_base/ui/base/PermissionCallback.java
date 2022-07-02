package com.yuyang.lib_base.ui.base;

import java.util.List;

public interface PermissionCallback {

    /**
     * @param permissionsGrantedNew 新获取授权的权限
     */
    void onGranted(List<String> permissionsGrantedNew);

    /**
     *
     * @param permissionsDeniedNew  新被拒绝的权限
     * @param permissionsDeniedForever  新被拒绝的权限 不再询问
     */
    void onDenied(List<String> permissionsDeniedNew, List<String> permissionsDeniedForever);
}
