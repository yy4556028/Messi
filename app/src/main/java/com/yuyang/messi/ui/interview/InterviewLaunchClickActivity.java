package com.yuyang.messi.ui.interview;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.utils.StringDealUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.messi.ui.common.photo.PhotoShowActivity;

/**
 * https://www.jianshu.com/p/37370c1d17fc
 */
public class InterviewLaunchClickActivity extends AppBaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_interview_launcherclick;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("应用冷启动流程");

        findViewById(R.id.imageView).setOnClickListener(v -> {
            PhotoShowActivity.launchActivity(this, R.mipmap.interview_click_launch, v);
        });

        TextView tvDetail = findViewById(R.id.tvDetail);
        tvDetail.append(StringDealUtil.highlightKeyword(Color.RED, "一.Launcher 进程通知 AMS 启动新进程\n", "一.Launcher 进程通知 AMS 启动新进程\n"));
        tvDetail.append(
                "1.Activity.startActivityForResult\n" +
                        "2.Instrumentation.execStartActivity{ 调用AMS代理的startActivity }\n" +
                        "3.AMS.startActivity(通过 AMS 本地代理跨进程调用SystemServer进程的AMS.startActivity)\n" +
                        "主进程逻辑结束\n\n");

        tvDetail.append(StringDealUtil.highlightKeyword(Color.RED, "二.通知Launcher 执行 onPause(),Launcher 执行完onPause() 通知 AMS\n", "二.通知Launcher 执行 onPause(),Launcher 执行完onPause() 通知 AMS\n"));
        tvDetail.append(StringDealUtil.highlightKeyword(Color.BLUE,
                "1.AMS：startActivity -> startActivityAsUser -> getActivityStartController().obtainStarter().set(...建造者模式).execute() 调用 ActivityStarter.execute\n" +
                        "2.ActivityStarter：execute -> executeRequest -> startActivityUnchecked(不同版本)\n" +
                        "    startActivityInner:检查当前前台Resume状态的Activity是桌面应用，所以通过 startPausingLocked 通知桌面Activity进入Paused状态,桌面应用执行完onPause,binder调用activityPaused通知AMS执行完成(不同版本不同类，方法一样)\n" +
                        "    AMS收到activityPaused调用后，继续执行启动应用的逻辑，判断需要启动的Activity所在的进程是否存在，如果不存在需要先startProcessAsync创建应用进程\n" +
                        "3.ActivityStackSupervisor：startSpecificActivity{进程存在 调用realStartActivityLocked,进程不存在执行 (AMS)mService.startProcessAsync(不同版本)}\n\n",
                "AMS：startActivity", "ActivityStarter.execute", "startActivityUnchecked", "通知桌面Activity进入Paused状态,桌面应用执行完onPause,binder调用activityPaused通知AMS执行完成", "startProcessAsync创建应用进程"));

        tvDetail.append(StringDealUtil.highlightKeyword(Color.RED,
                "进程存在：\n" +
                        "1.ActivityStackSupervisor.realStartActivityLocked -> (AMS)mService.(ClientLifecycleManager)getLifecycleManager.scheduleTransaction(ClientTransaction transaction)\n" +
                        "2.ClientLifecycleManager：scheduleTransaction -> 调用了参数 ClientTransaction.schedule() -> IApplicationThread.scheduleTransaction -> (ActivityThread extend ClientTransactionHandler).scheduleTransaction\n" +
                        "AMS逻辑结束,回到主进程\n\n" +

                        "1.ClientTransactionHandler：scheduleTransaction{ sendMsg(scheduleTransaction) } -> AT.TransactionExecutor.execute(ClientTransaction)\n" +
                        "2.pause时传入PauseActivityItem：execute -> AT.handlePauseActivity -> AT.performPauseActivity -> performPauseActivityIfNeeded -> Instrumentation.callActivityOnPause -> Activity.performPause -> onPause\n" +
                        "3.start时传入LaunchActivityItem：execute -> AT.handleLaunchActivity -> AT.performLaunchActivity -> Instrumentation,callActivityOnCreate -> Activity.performCreate -> onCreate\n" +
                        "进程存在结束\n\n",
                "进程存在："));

        tvDetail.append(StringDealUtil.highlightKeyword(Color.RED, "三.AMS 请求 zygote 进程 fork 应用进程. 新进程中 创建 AT 并调用main(). AMS 回调各种生命周期方法\n", "三.AMS 请求 zygote 进程 fork 应用进程. 新进程中 创建 AT 并调用main(). AMS 回调各种生命周期方法\n"));
        tvDetail.append(StringDealUtil.highlightKeyword(Color.RED,
                "进程不存在：\n" +
                        "1.AMS.startProcessLocked{AMS通知zygote进程fork应用进程，然后分配内存空间等(socket)，创建新进程的时候，AMS 会保存一个 ProcessRecord 信息:uid + process名}\n" +
                        "2.应用进程启动后完成：①设置默认的java异常处理机制 ②JNI调用启动进程的binder线程池 ③反射创建 ActivityThread 并调用其 main 入口方法\n" +
                        "3.main方法：①创建并启动主线程的loop消息循环[Looper.prepareMainLooper(),Looper.loop()] ②创建ActivityThread并调用attach\n" +
                        "4.attach方法内分为系统和非系统,非系统 调用AMS.attachApplication{通过binder通知AMS已经启动,AMS保存AppThread代理以控制应用进程}\n" +
                        "主线程初始化完成后，主线程就进入阻塞状态，等待 Message\n" +
                        "5.AMS.attachApplication：①通过oneway异步binder(AppThread)调用bindApplication{sendMsg(BIND_APPLICATION)},AT Handler处理消息调用 handleBindApplication ②ATMS.attachApplication 继续执行启动应用Activity的操作\n" +
                        "5.1.handleBindApplication：①根据传入的ApplicationInfo信息创建应用的LoadedApk ②创建应用Application的Context、创建类加载器 触发Art虚拟机加载应用APK的Dex文件到内存中，并加载应用APK的Resource资源 ③调用LoadedApk的makeApplication函数创建应用的Application对象 ④执行应用Application#onCreate函数(Instrumentation)\n" +
                        "5.2.AMS.attachApplication -> ... -> ActivityStackSupervisor.realStartActivityLocked{通过LaunchActivityItem封装Binder通知应用进程执行Launch Activity动作，再通过ResumeActivityItem封装Binder通知应用进程执行Launch Resume动作}\n" +
                        "6.App进程收到系统binder调用后，调用 handleLaunchActivity -> performLaunchActivity{创建Activity的Context;通过反射创建Activity;执行Activity的attach动作与window关联(其中会创建应用窗口的PhoneWindow对象并设置WindowManager;执行Activity的onCreate，并在setContentView中创建窗口的DecorView对象)}",

                "进程不存在", "AMS通知zygote进程fork应用进程，然后分配内存空间等", "反射创建 ActivityThread 并调用其 main 入口方法", "创建并启动主线程的loop消息循环", "创建ActivityThread并调用attach",
                "通知AMS已经启动,AMS保存AppThread代理以控制应用进程", "调用bindApplication", "AT Handler处理消息调用 handleBindApplication", "继续执行启动应用Activity的操作", "makeApplication函数创建应用的Application", "执行应用Application#onCreate",
                "ActivityStackSupervisor.realStartActivityLocked", "handleLaunchActivity", "通过反射创建Activity", "执行Activity的attach动作与window关联\n\n"));

        tvDetail.append("Activity.onAttach(){ 构建PhoneWindow }\n");
        tvDetail.append("Activity.onCreate(){ setContentView()委托给PhoneWindow，在其中创建 DecorView，根据主题解析系统预定义文件作为 DecorView 的子View，xml中有一个id为content的容器 作为setContentView的父View }\n");
        tvDetail.append("Activity.onResume(){ DecorView 先被设为invisible，然后被添加到窗口，其间会构建 ViewRootImpl，它是 app 和 WMS 双向通信的纽带，ViewRootImpl.requestLayout()会被调用 以出发View树绘制 }\n");
        tvDetail.append("经过垂直同步，监听到渲染回调，执行渲染。DecorView被渲染完成，就被设为 visible，界面显示\n");
    }
}
