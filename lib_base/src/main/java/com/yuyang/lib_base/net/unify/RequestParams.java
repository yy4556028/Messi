package com.yuyang.lib_base.net.unify;

import java.io.File;
import java.util.HashMap;

public class RequestParams extends HashMap {

    public boolean isIncludeFile = false;
    //Value instance of String,File

    public void addBodyParameter(String name, String value) {
        put(name, value);
    }

    public void addBodyParameter(String name, File file) {
        isIncludeFile = true;
        put(name, file);
    }

}
