package com.yuyang.lib_base.ui.base;

import android.os.Bundle;
import androidx.annotation.Nullable;

public abstract class BaseLazyXFragment extends BaseFragment {

    protected boolean isLazyLoaded;

    private boolean isPrepared;

    private boolean isNeedRefresh;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onInit();
        isPrepared = true;
        lazyLoad();
    }

    @Override
    public void onResume() {
        super.onResume();
        lazyLoad();
    }

    private void lazyLoad() {
        if (!isHidden() && isResumed() && isPrepared) {
            if (!isLazyLoaded) {
                onLazyLoad(true);
                isLazyLoaded = true;
                isNeedRefresh = false;
            } else if (isNeedRefresh) {
                onLazyLoad(false);
                isNeedRefresh = false;
            }
        }
    }

    public void lazyRefresh() {
        isNeedRefresh = true;
        lazyLoad();
    }

    public void setNeedRefresh(boolean needRefresh) {
        isNeedRefresh = needRefresh;
    }

    public abstract void onInit();

    public abstract void onLazyLoad(boolean isFirst);
}
