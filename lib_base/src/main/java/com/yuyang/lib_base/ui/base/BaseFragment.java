package com.yuyang.lib_base.ui.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

public abstract class BaseFragment extends Fragment {

    protected View rootView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
//        if (rootView == null) {
//            rootView = inflater.inflate(getLayoutId(), container, false);
//        } else {
//            ViewGroup parent = (ViewGroup) rootView.getParent();
//            if (null != parent) {
//                parent.removeView(rootView);
//            }
//        }
//        return rootView;
        // TODO: 2021/11/1 注释上面 看看下面会不会出问题
        final int layoutId = getLayoutId();
        if (layoutId != 0) {
            rootView = inflater.inflate(layoutId, container, false);
            return rootView;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        doOnViewCreated();
    }

    protected <T extends View> T $(int id) {
        if (rootView == null) {
            return null;
        }
        return rootView.findViewById(id);
    }

    /**
     * Find出来的View,已绑定点击事件，直接在doOnclick里进行监听
     */
    protected final <T extends View> T findViewById(@IdRes int id) {
        if (rootView == null) {
            return null;
        }
        return rootView.findViewById(id);
    }

    public View getContentView() {
        return rootView;
    }

    protected int getLayoutId() {
        return 0;
    }

    protected abstract void doOnViewCreated();

    /**
     * 获取ViewModel 实例
     *
     * @param modelClass ViewModel.class
     * @param <VM>        ViewModel
     * @return ViewModel T的实例
     */
    protected <VM extends BaseViewModel> VM createViewModel_scopeActivity(@NonNull Class<VM> modelClass) {

        final FragmentActivity activity = getActivity();
        if (activity != null) {
            VM viewModel = new ViewModelProvider(activity).get(modelClass);
            return viewModel;
        }
        return null;
    }

    /**
     * 获取ViewModel 实例
     *
     * @param modelClass ViewModel.class
     * @param <VM>        ViewModel
     * @return ViewModel T的实例
     */
    protected <VM extends BaseViewModel> VM createViewModel_scopeFragment(@NonNull Class<VM> modelClass) {
        return new ViewModelProvider(this).get(modelClass);
    }
}
