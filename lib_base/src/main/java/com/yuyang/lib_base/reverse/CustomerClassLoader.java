package com.yuyang.lib_base.reverse;

import java.io.File;

import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;

public class CustomerClassLoader extends PathClassLoader {

//    private static final boolean VEROSE_DEBUG;
    private boolean initialized;
    private final String libPath;
    private final String mDexOutputPath;
    private DexFile[] mDexs;
    private File[] mFiles;
    private String[] mLibPaths;
    private String[] mPaths;
    private String[] mZips;
    private final String path;

    public CustomerClassLoader(String argPath, String dexOutputPath, String librarySearchPath, ClassLoader classLoader) {
        super("", librarySearchPath, classLoader);
        if (argPath != null && dexOutputPath != null) {
            this.path = argPath;
            this.libPath = librarySearchPath;
            this.mDexOutputPath = dexOutputPath;
            try {
//                this.ensureInit();
            } catch (Exception e) {

            }
            return;
        }
        throw new NullPointerException();
    }
}
