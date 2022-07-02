package com.yuyang.messi.ui.base;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;

public class BaseFragmentFactory extends FragmentFactory {

    @NonNull
    @Override
    public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String className) {
        return super.instantiate(classLoader, className);
    }
}
