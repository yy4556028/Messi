package com.yuyang.lib_base.reverse;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

public class ReverseApp {

    private DexClassLoader dexClassLoader;//能够加载 jar/apk/dex
    private PathClassLoader pathClassLoader;//只能加载系统中已经安装的 Apk 中的 Dex

}
