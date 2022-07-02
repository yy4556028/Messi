package com.yuyang.lib_base.ui.viewmodel;

import com.yuyang.lib_base.ui.base.BaseViewModel;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 带等待框的Observe
 * Created by jiazh on 2018/8/15.
 */
public abstract class ProcessObserver<T> implements Observer<T> {

    private final BaseViewModel viewModel;

    private String msg;

    //TODO 未来会有多种样式
    private int type;

    /**
     * 构造
     *
     * @param viewModel BaseViewModel
     */
    protected ProcessObserver(final BaseViewModel viewModel) {

        this.viewModel = viewModel;
    }

    /**
     * 构造
     *
     * @param viewModel BaseViewModel
     * @param message   等待框提示内容
     */
    protected ProcessObserver(final BaseViewModel viewModel, String message) {

        this.msg = message;
        this.viewModel = viewModel;
    }

    /**
     * 构造
     *
     * @param viewModel BaseViewModel
     * @param message   等待框提示内容
     * @param type      弹窗样式
     */
    protected ProcessObserver(final BaseViewModel viewModel, String message, int type) {

        this.msg = message;
        this.viewModel = viewModel;
        this.type = type;
    }

    @Override
    public void onSubscribe (final Disposable d) {

        viewModel.getLoading().postValue(new LiveProcess(true, msg));
    }

    protected abstract void doOnNext (final T t);

    protected abstract void doOnError (final Throwable e);

    @Override
    public void onNext (final T t) {

        doOnNext(t);
    }

    @Override
    public void onError (final Throwable throwable) {

        viewModel.getLoading().postValue(new LiveProcess(false, msg));
        doOnError(throwable);
    }

    @Override
    public void onComplete () {

        viewModel.getLoading().postValue(new LiveProcess(false, msg));
    }
}
