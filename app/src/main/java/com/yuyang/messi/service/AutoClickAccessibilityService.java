package com.yuyang.messi.service;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class AutoClickAccessibilityService extends AccessibilityService {

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        try {
            List<AccessibilityNodeInfo> list = event.getSource().findAccessibilityNodeInfosByText("您已预约");

            if (null != list) {
                for (AccessibilityNodeInfo info : list) {
                    if (info.getText().toString().equals("您已预约")) {
                        //找到你的节点以后 就直接点击他就行了
                        info.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                        info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInterrupt() {

    }
}
