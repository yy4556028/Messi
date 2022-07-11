package com.yuyang.lib_base.ui.widget;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public class UnPeekLiveData<T> extends MutableLiveData<T> {

  private int mVersion = 0; // 被观察者的版本
  private int observerVersion = 0; // 观察者的版本

  @Override
  public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
    // 每次订阅的时候，先把版本同步
    observerVersion = mVersion;
    super.observe(
        owner,
        new Observer<T>() {
          @Override
          public void onChanged(T t) {
            if (mVersion != observerVersion) {
              observer.onChanged(t);
            }
          }
        });
  }

  @Override
  public void observeForever(@NonNull Observer<? super T> observer) {
    observerVersion = mVersion;
    super.observeForever(
        new Observer<T>() {
          @Override
          public void onChanged(T t) {
            if (mVersion != observerVersion) {
              observer.onChanged(t);
            }
          }
        });
  }

  @MainThread
  public void setValue(T value) {
    mVersion++;
    super.setValue(value);
  }
}
