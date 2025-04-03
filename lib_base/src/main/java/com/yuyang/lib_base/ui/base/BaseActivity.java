package com.yuyang.lib_base.ui.base;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.yuyang.lib_base.ui.viewmodel.LiveProcess;
import com.yuyang.lib_base.utils.SystemBarUtil;
import com.yuyang.lib_base.utils.statusbar.MyStatusBarUtil;

public abstract class BaseActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;

    private BaseViewModel mBaseViewModel;

    public BaseActivity getActivity() {
        return this;
    }

    public BaseViewModel getBaseViewModel() {
        return mBaseViewModel;
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        setStatusBar();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setStatusBar();
    }

    @Override
    protected void onDestroy() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        super.onDestroy();
    }

    public void setStatusBar() {
        String plan = "planTest";

        if (TextUtils.equals("planTest", plan)) {
            SystemBarUtil.immersive(this);
        } else if (TextUtils.equals("theme", plan)) {
            //do nothing
        }

        //深色文字图标风格
        if (!MyStatusBarUtil.setStatusBarDarkTheme(this, true)) {
            MyStatusBarUtil.setStatusBarColor(this, 0x55000000);
        }

//        MaterialColors.isColorLight
//        WindowManager.LayoutParams.setFitInsetsTypes(WindowInsetsCompat.Type.navigationBars());
//        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
    }

    /**
     * 获取ViewModel 实例
     *
     * @param modelClass ViewModel.class
     * @param <VM>       ViewModel
     * @return ViewModel T的实例
     */
    protected <VM extends BaseViewModel> VM createViewModel(@NonNull Class<VM> modelClass) {

        ViewModelFactory factory = ViewModelFactory.getInstance(getApplication());
//        VM viewModel = ViewModelProviders.of(this, factory).get(modelClass);
        VM viewModel = new ViewModelProvider(this).get(modelClass);
        mBaseViewModel = viewModel;
        initCommonObserve(mBaseViewModel);
        return viewModel;
    }

    protected void initCommonObserve(@NonNull final BaseViewModel baseViewModel) {
        baseViewModel.getLoading().observe(this, new Observer<LiveProcess>() {

            @Override
            public void onChanged(@Nullable final LiveProcess liveProcess) {

                showLoading(liveProcess);
            }
        });
    }

    /*****************************************  LoadingProgress *******************************************/

    protected final boolean isLoading() {
        return mProgressDialog != null && mProgressDialog.isShowing();
    }

    protected final void showLoading(LiveProcess liveProcess) {
        if (liveProcess == null) {
            return;
        }
        if (liveProcess.isShow()) {
            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(this);
            }
            mProgressDialog.setCancelable(liveProcess.isCancelable());
            mProgressDialog.setMessage(liveProcess.getMessage());
            mProgressDialog.show();
        } else {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    }

    /*****************************************  LoadingProgress *******************************************/
}