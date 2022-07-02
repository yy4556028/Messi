package com.yuyang.lib_base.ui.base;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.yuyang.lib_base.utils.ReflectUtils;

/**
 * ViewModelFactory
 * <p>
 * create by 18010426 at 2018/08/03
 */
public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private static volatile ViewModelFactory INSTANCE;

    private final Application mApplication;

    private ViewModelFactory(Application application) {

        mApplication = application;
    }

    public static ViewModelFactory getInstance (Application application) {

        if (INSTANCE == null) {
            synchronized (ViewModelFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ViewModelFactory(application);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public <T extends ViewModel> T create (Class<T> modelClass) {

        return ReflectUtils.reflect(modelClass).newInstance(mApplication).get();
    }
}
