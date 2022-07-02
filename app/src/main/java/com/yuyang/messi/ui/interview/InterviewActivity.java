package com.yuyang.messi.ui.interview;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.DragStartHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.ui.view.CommonDialog;
import com.yuyang.lib_base.utils.CommonUtil;
import com.yuyang.lib_base.utils.StringDealUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.room.entity.ModuleEntity;
import com.yuyang.messi.ui.base.AppBaseActivity;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * https://www.androidperformance.com/2021/04/24/android-systrace-smooth-in-action-1/ systrace
 * https://www.jianshu.com/p/75aa88d1b575   Android Systrace 使用详解
 * <p>
 * ASM插桩：饿了么的Lancet库
 * https://github.com/xfhy/Android-Notes
 * <p>
 * TortoiseGit
 * <p>
 * 绕过限制：https://weishu.me/2018/06/07/free-reflection-above-android-p/
 * https://weishu.me/2019/03/16/another-free-reflection-above-android-p/
 * <p>
 * CountDownLatch(count)：多个线程结束后 再去执行某一动作
 * <p>
 * https://www.jianshu.com/p/d02362fbd9f2   书籍翻页效果
 */
public class InterviewActivity extends AppBaseActivity {

    private HeaderLayout headerLayout;

    private InterviewAdapter androidAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_interview;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("面试");
        initJava();
        initAndroid();
        initOther();

//        MMKV mmkv = MMKV.defaultMMKV(MMKV.SINGLE_PROCESS_MODE, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            initDragAndDrop();
        }
    }

    private void initJava() {
        RecyclerView recyclerView_java = findViewById(R.id.recyclerView_java);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this, FlexDirection.ROW, FlexWrap.WRAP);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);  //默认
        layoutManager.setAlignItems(AlignItems.STRETCH);  //默认
        recyclerView_java.setLayoutManager(layoutManager);

        InterviewAdapter javaAdapter = new InterviewAdapter(null);
        recyclerView_java.setAdapter(javaAdapter);

        javaAdapter.setNewData(CommonUtil.asList(
                new ModuleEntity("String Builder Buffer"),
                new ModuleEntity("数据结构"),
                new ModuleEntity("RxJava"),
                new ModuleEntity("注解"),
                new ModuleEntity("加密"),
                new ModuleEntity("引用"),
                new ModuleEntity("Volatile关键字"),
                new ModuleEntity("锁的类型"),
                new ModuleEntity("设计模式")
        ));

        javaAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ModuleEntity moduleEntity = javaAdapter.getItem(position);
                if (moduleEntity == null) return;
                if (moduleEntity.getClazz() != null) {
                    startActivity(new Intent(InterviewActivity.this, moduleEntity.getClazz()));
                    return;
                }
                switch (moduleEntity.getName()) {
                    case "String Builder Buffer": {
                        showMyDialog(moduleEntity.getName(),
                                "String：char数组 final不可变 大多方法都是 new String返回\n" +
                                        "StringBuilder 和 StringBuffer 初始容量16，扩容*2+2\n" +
                                        "StringBuffer线程安全：所有方法 synchronized");
                        break;
                    }
                    case "数据结构": {
                        showMyDialog(moduleEntity.getName(),
                                "ArrayList：数组，默认容量10，扩容*1.5，查修快，增删慢(Arrays.copyOf扩容)，物理连续;继承RandomAccess(for比迭代快),clone,Serializable\n\n" +

                                        "LinkedList：双向链表，记录头尾节点，查修慢，增删快，物理不连续，遍历不用for，用forEach;继承Deque(双向队列接口)，clone,Serializable\n\n" +

                                        "CopyOnWriteArrayList：有点像线程安全ArrayList,读写分离(读 原始数组,写 同步写入新数组后set)，适合读多写少(增删改都加锁了)\n\n" +

                                        "HashMap：默认16，key和value可空，数组存放Entry(key-value)，当 HashMap 中的元素个数超过数组大小 * loadFactor(0.75)时，就会进行数组扩容(懒加载)\n" +
                                        "容量为2的倍数，因为要让hash冲突降低，map均匀分布(hash&(length-1) - 1111)\n" +
                                        "JDK1.7：头插法 插前扩容 数组+链表\n" +
                                        "JDK1.8：尾插法 插后扩容 数组+链表+红黑树(单链表长度超过8，转为红黑树)\n\n" +

                                        "HashTable：默认11，不支持null键和值 同步效率低，put的时候其他线程不能get(同一把锁)\n\n" +

                                        "ConcurrentHashMap：多线程+效率高(JDK1.7分段锁 1.8 CAS+sync保证并发更新)\n\n" +

                                        "TreeMap：红黑树，key不能null\n" +
                                        "LinkedHashMap：HashMap子类,内部双向链表维护顺序,支持插入顺序和访问顺序(accessOrder)，重写HashMap三个方法(afterNodeAccess,afterNodeInsert,afterNodeRemove)\n\n" +

                                        "HashSet：内部HashMap实现，put(key,static Obj)，无序，是否重复根据hashCode和equals\n\n" +

                                        "TreeSet：内部TreeMap实现，put(key,static Obj)，有序(自定义对象必须实现Comparable)\n\n" +

                                        "Bundle：内部ArrayMap实现，更多考虑内存优化；Parcelable序列化(HashMap Serializable)\n\n" +

                                        "SparseArray：默认10，key只能int，key value都是数组实现，key按顺序插入，增改差都是二分查找，remove时置DELETE减少copy，适合数据量小 来回查删\n\n" +

                                        "ArrayMap：int[]存hash，Object[]存key value，扩容缩容\n\n" +

                                        "红黑树：二叉查找树的一种，根节点是黑，叶子节点是黑色(null)，每个红节点的两个子节点都是黑\n" +
                                        "RandomAccess：随机访问,for比迭代快\n" +
                                        "transient：不被序列化，实际序列化时，调用writeObj readObj，只读写实际存储的数据而不是整个数组，节省时间空间\n");
                        break;
                    }
                    case "RxJava": {
                        showMyDialog(moduleEntity.getName(),
                                "基于时间流，实现异步操作\n\n" +

                                        "观察者模式，链式调度，事件源头是被观察者，事件结尾是观察者.\n" +
                                        "装饰器模式：为了功能增加防止类爆炸！\n\n" +

                                        "create：耗时操作后切换线程\n" +
                                        "from：将一个数组转化成Observable\n" +
                                        "just：Observable.just(1, 2, 3)\n" +
                                        "timer：Observable.timer(2, 2, TimeUnit.SECONDS)\n" +
                                        "interval：和timer类似\n" +
                                        "range：Observable.range(0, 10)\n" +
                                        "map：转换\n" +
                                        "flatMap：减少for循环，打印所有学生的课程",
                                "异步操作", "链式调度", "观察者模式", "装饰器模式"
                        );
                        break;
                    }
                    case "注解": {
                        showMyDialog(moduleEntity.getName(),
                                "@Retention(生命周期)：\n" +
                                        "Source：源文件有效，编译class丢弃，用于提示\n" +
                                        "Class：class文件有效，虚拟机迭起，用于自动生成代码\n" +
                                        "Runtime：运行时有效，用于自动注入\n\n" +

                                        "@Target：\n" +
                                        "ANNOTATION_TYPE：给注解进行注解\n" +
                                        "Construction：给构造方法注解\n" +
                                        "Field：给属性注解\n" +
                                        "Local_variable：给局部变量注解\n" +
                                        "Method：给方法注解\n" +
                                        "Package：给包注解\n" +
                                        "Parameter：给方法内参数注解\n" +
                                        "Type：给类型注解，如类 接口 枚举\n\n" +

                                        "@Documented：注解会被javadoc工具提取成文档\n" +
                                        "@Inherited：允许子类继承父类中的注解\n\n" +

                                        "手写BindView注解：Runtime+FIELD\n" +
                                        "BindViewProcess.inject(this) ->\n" +
                                        "activity.getClass后遍历所有属性，拿到被注解绑定的属性，拿到注解value做相应操作\n\n" +

                                        "编译时注解"//TODO https://www.jianshu.com/p/b5be6b896a1a
                        );
                        break;
                    }
                    case "加密": {
                        showMyDialog(moduleEntity.getName(),
                                "MD5：哈希散列,产生128bit长度(16进制标识为32个字符)的摘要.不可逆，不同string可能得到相同的MD串\n" +
                                        "SHA1算法:HA1同样是摘要算法，比MD5安全性更强。一般用来检查完整性及数字签名\n\n" +

                                        "对称加密DES：加密单位太短,密钥的位数太短不安全,不能对抗差分和线性密码分析\n" +
                                        "对称加密AES：对内存的需求非常低,分组长度和密钥长度设计灵活,可对抗差分密码分析和线性密码分析\n" +
                                        "非对称加密RSA：密钥很长,安全性好但计算量很大，加密速度较慢\n\n" +

                                        "采用DES与RSA相结合的应用，使它们的优缺点正好互补：DES加密速度快，适合加密较长的报文，可用其加密明文；RSA加密速度慢，安全性好，应用于DES 密钥的加密，可解决DES 密钥分配的问题\n\n" +

                                        "S端生成公私钥(RSA)\n" +
                                        "C端请求服务端得到公钥,生成对称加密的密钥X(DES),用拿到的公钥加密并发给S端\n" +
                                        "S端通过私钥解密拿到密钥X\n" +
                                        "两端通过对称加密通信",
                                "DES", "RSA"
                        );
                        break;
                    }
                    case "引用": {
                        showMyDialog(moduleEntity.getName(),
                                "软引用(SoftReference):内存不足回收\n" +
                                        "弱引用(WeakReference):GC时回收\n" +
                                        "虚引用(PhantomReference):没有引用一样,需配合引用队列使用\n\n" +
                                        "如果软弱引用所引用的对象被GC回收，JAVA虚拟机就会把这个软引用加入到与之关联的引用队列中"
                        );

                        break;
                    }
                    case "Volatile关键字": {
                        showMyDialog(moduleEntity.getName(),
                                "适合在一个线程写，其它线程读\n\n" +

                                        "1.线程间的可见性：当多个线程访问同一个变量时，一个线程修改了这个变量的值，其他线程能够立即看得到修改的值.当一个共享变量被volatile修饰时，它会保证修改的值会立即被更新到主存，当有其他线程需要读取时，它会去内存中读取新值.synchronized和Lock也能够保证可见性\n" +
                                        "2.禁止指令重排：处理器为了提高程序运行效率，可能会对输入代码进行优化\n" +
                                        "MESI(缓存一致性协议)：当CPU写数据时，如果发现操作的变量是共享变量，即在其他CPU中也存在该变量的副本，会发出信号通知其他CPU将该变量的缓存行置为无效状态，因此当其他CPU需要读取这个变量时，发现自己缓存中缓存该变量的缓存行是无效的，那么它就会从内存重新读取\n\n" +
                                        "volatile的原理和实现机制：汇编代码会多出一个lock前缀指令,相当于一个内存屏障,它提供3个功能\n" +
                                        "1.执行到内存屏障这句指令时，在它前面的操作已经全部完成\n" +
                                        "2.强制将对缓存的修改操作立即写入主存\n" +
                                        "3.如果是写操作，它会导致其他CPU中对应的缓存行无效",
                                "线程间的可见性", "禁止指令重排"
                        );
                        break;
                    }
                    case "锁的类型": {
                        showMyDialog(moduleEntity.getName(),
                                "乐观锁：认为一个线程去拿数据的时候不会有其他线程对数据进行更改，所以不会上锁(实现方式：CAS机制、版本号机制)\n\n" +
                                        "悲观锁：认为一个线程去拿数据时一定会有其他线程对数据进行更改(实现方式就是枷锁 比如synchronized)"
                        );
                        break;
                    }
                    case "设计模式": {
                        showMyDialog(moduleEntity.getName(),
                                "代理模式装饰模式区别：代理模式主要是为了访问隔离;装饰模式主要是为了功能增强\n\n" +

                                        "工厂模式建造者模式区别：工厂模式侧重点在于对象的生成过程，而建造者模式主要是侧重对象的各个参数配置\n\n" +

                                        "观察者模式 发布订阅模式区别：发布订阅模式中间多了调度者，解耦\n\n" +

                                        "六大原则：\n" +
                                        "1.单一职责：降低复杂度，提高可读性，降低变更引起的风险\n" +
                                        "2.里氏替换原则：子类可以扩展父类的功能，但不能改变父类原有的功能\n" +
                                        "3.依赖倒置原则：面向接口编程(读报纸杂志书IReader)。高层模块不应该依赖低层模块，二者都应该依赖其抽象；抽象不应该依赖细节；细节应该依赖抽象\n" +
                                        "4.接口隔离原则：将臃肿的接口拆分为独立的几个接口\n" +
                                        "5.迪米特法则：一个对象应该对其他对象保持最少的了解，降低耦合\n" +
                                        "6.开闭原则：类、模块和函数应该对扩展开放，对修改关闭。用抽象构建框架，用实现扩展细节"
                        );
                        break;
                    }
                }
            }
        });
    }

    private void initAndroid() {
        RecyclerView recyclerView_android = findViewById(R.id.recyclerView_android);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this, FlexDirection.ROW, FlexWrap.WRAP);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);  //默认
        layoutManager.setAlignItems(AlignItems.STRETCH);  //默认
        recyclerView_android.setLayoutManager(layoutManager);

        androidAdapter = new InterviewAdapter(null);
        recyclerView_android.setAdapter(androidAdapter);

        androidAdapter.setNewData(CommonUtil.asList(
                new ModuleEntity("屏幕适配"),
                new ModuleEntity("Jetpack"),
                new ModuleEntity("Handler"),
                new ModuleEntity("Binder"),
                new ModuleEntity("稳定性相关"),
                new ModuleEntity("性能优化概述"),
                new ModuleEntity("内存问题根本原因"),
                new ModuleEntity("内存优化"),
                new ModuleEntity("ANR卡顿相关"),
                new ModuleEntity("启动优化"),
                new ModuleEntity("Glide"),
                new ModuleEntity("网络请求过程"),
                new ModuleEntity("OkHttp"),
                new ModuleEntity("Retrofit"),
                new ModuleEntity("应用安装"),
                new ModuleEntity("APK打包流程"),
                new ModuleEntity("APK构造及瘦身"),
                new ModuleEntity("APK加固及反编译"),
                new ModuleEntity("Dex文件相关"),
                new ModuleEntity("热修复原理"),
                new ModuleEntity("插件化"),
                new ModuleEntity("字节码插桩"),
                new ModuleEntity("安全"),
                new ModuleEntity("版本适配"),
                new ModuleEntity("开机"),
                new ModuleEntity("View绘制原理"),
                new ModuleEntity("触摸屏幕"),
                new ModuleEntity("Window,DecorView,WindowManager,ViewRootImpl关系"),
                new ModuleEntity("事件分发"),
                new ModuleEntity("Kotlin内联函数"),
                new ModuleEntity("协程"),
                new ModuleEntity("RecyclerView相关"),
                new ModuleEntity("SharedPreference MMKV"),
//                new ModuleEntity("NDK"),
//                new ModuleEntity("音视频"),
                new ModuleEntity("应用启动流程", InterviewLaunchClickActivity.class),
                new ModuleEntity("Android JVM ART"),
                new ModuleEntity("搭建框架架构"),
                new ModuleEntity("SDK开发")
        ));

        androidAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ModuleEntity moduleEntity = androidAdapter.getItem(position);
                if (moduleEntity == null) return;
                if (moduleEntity.getClazz() != null) {
                    startActivity(new Intent(InterviewActivity.this, moduleEntity.getClazz()));
                    return;
                }
                switch (moduleEntity.getName()) {
                    case "屏幕适配": {
                        showMyDialog(moduleEntity.getName(),
                                "权重适配：线性布局，约束布局 bias百分比 Ratio宽高比 宽高percent\n\n" +

                                        "代码适配：根据当前屏幕的大小动态适配\n\n" +

                                        "图片只放xhdpi：因为主流，其他的会自动缩放相应倍数23468\n\n" +
                                        //ScreenAdaptUtil.setCustomDensity
                                        "头条方案：根据px=dp*density,设置density=设备真实宽(px) / 360(设计图宽度)，在Activity.onCreate中调用\n" +
                                        "缺陷：导致系统或三方View效果和之前不同.density很多场景会被还原如WebView\n\n" +
//                                        https://blankj.com/2018/12/18/android-adapt-screen-killer/
                                        "柯基：基于头条，操作pt(DisplayMetrics#xdpi)，重写Activity.getResources()函数"
                        );
                        break;
                    }
                    case "Jetpack": {//一套组件库
                        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
                        atomicBoolean.compareAndSet(true, false);
                        showMyDialog(moduleEntity.getName(),
                                //https://zhuanlan.zhihu.com/p/260095937
                                "Lifecycle：是一个持有组件生命周期状态(如活动或片段)信息的类，并允许其他对象观察此状态\n" +
                                        "Fragment：LifecycleRegistry在各生命周期handleLifecycleEvent\n" +
                                        "Activity：onCreate里ReportFragment.injectIfNeededIn在各生命周期派发事件\n\n" +

                                        "ViewModel：管理页面状态+单向依赖避免内存泄漏(不持有Activity对象)+状态设置作用域，使状态的共享做到作用域可控(App,Activity,Fragment)\n" +
                                        "ComponentActivity.mViewModelStore.getViewModelStore 方法,Fragment要与activity作用域相同就传getActivity\n" +
                                        "Fragment.getViewModelStore - FragmentManager.getVMS - FragmentManagerViewModel.mViewModelStores.getVMS\n" +
                                        "ViewModelStore：key=DEFAULT_KEY + canonicalName\n" +
                                        "自动保存与恢复原理：当Activity销毁前回调onRetainNonConfigurationInstance()方法保存viewModelStore在NonConfigurationInstance中\n" +
                                        "然后在getViewModelStore方法中又会首先去获取NonConfigurationInstance对象\n" +
                                        "监听LifeCycle，DESTROY时，判断不是配置导致的，就回调onClear\n" +
                                        "Activity销毁后重建，只能通过onSaveInstanceState恢复\n\n" +//onSaveInstanceState保存数据在内存交由ActivityManager管理，评论说官网上说保存在磁盘...

                                        "LiveData(MutableLiveData)：可被观察的数据存储器类，具有生命周期感知能力,仅更新处于活跃生命周期状态的应用组件观察者\n" +
                                        "遍历找到观察者observer，活跃就调用onChange发送数据\n" +
                                        "会在destroy状态自动移除观察者，防止内存泄漏\n" +
                                        "粘性：因为生命周期变活跃时，也会调用dispatchingValue，如果observer.LastVersion<最新LiveData.version就调用onChange\n" +
                                        "解决方案：定义一个中间层，记录是否被observer，没有的话本地保存数据,有的话调super\n" +
                                        "网上方案：反射修改mLastVersion(侵入性强)，SingleLiveEvent(多个观察者，只有第一个能消费)，KotlinFlow(未作了解)\n" +
                                        "高频使用postValue会数据丢失，因为mPendingData为空才抛任务，然后值存在PendingData里，只有第一次的任务执行完才会继续抛，否则只更新PendingData\n" +
                                        "fragment用replace时不会调用DESTROYED，所以不能观察this，要观察viewLifecycleOwner\n\n" +

                                        "Room：SQLite封装库\n\n" +

                                        "DataStore\n\n" +
                                        "Baseline Profiles：优化启动性能\n\n" +//https://juejin.cn/post/7098117781157052429
                                        "JankStats：避免 UI 卡顿\n\n" +
                                        "hilt?");
                        break;
                    }
                    case "Handler": {
                        showMyDialog(moduleEntity.getName(),
                                "Handler：用来处理 Msg ,可在任何线程创建 Handler，指定Looper即可，不指定默认当前线程\n" +
                                        "Looper：loop方法开启后，死循环取MsgQueue中消息,最终发给对应的 Handler 处理\n" +
                                        "MessageQueue：Message队列，没消息时，借助 Linux 的 ePoll机制，阻塞休眠等待，直到有 Message 进入队列将其唤醒\n\n" +
                                        "一个线程一个Looper(prepare方法创建 Looper 并赋值 ThreadLocal,Loop.myLooper 获取)\n" +

                                        "消息持有Handler引用(target)，Handler持有Activity引用\n" +
                                        "HandlerThread：(多个耗时任务需要执行,只启动一个线程)子线程使用Handler，需要三个方法 prepare(创建looper并赋值ThreadLocal),Looper.loop(),Looper.myLooper().quitSafely释放资源\n" +
                                        "Queue存取消息都有 synchronized (this)对象保证线程安全\n" +
                                        "Msg获取应该ObtainMsg,Message内部会维护 MsgPoolSync 50\n" +
                                        "postDelay 会比较Msg.when插入MessageQueue\n" +
                                        "MessageQueue没有消息时，阻塞在loop的queue.next(),主线程会进入休眠状态(释放CPU资源)，直到下个消息到达唤醒(输入事件也可以唤醒)，而ANR是5s未响应，两者不同\n\n" +

                                        //https://wetest.qq.com/lab/view/352.html
                                        "IdleHandler是handler提供的在队列没有立即要执行的任务时，会执行的任务\n" +
                                        "IdleHandler.queueIdle()返回false移除它，返回true在下次msg处理完了的时候继续回调\n" +
                                        "IdleHandler时机不可控\n" +
                                        "应用：DrawerLyt延迟初始化\n" +

                                        "同步屏障：让消息优先被执行.核心代码是设置target=null消息，插入消息链表头，MsgQueue.next优先查找同步屏障消息.(postSyncBarrier不给target赋值Handler,所以target==null,代表消息屏障)，removeSyncBarrier(处理滞留的同步消息)\n" +
                                        "Android 禁止App往MessageQueue插入同步屏障消息，代码会报错，只有系统可以"
                        );
                        break;
                    }
                    case "Binder": {
                        showMyDialog(moduleEntity.getName(),
//                                https://blog.csdn.net/carson_ho/article/details/73560642
                                "为什么多进程：突破内存限制(图库)，功能稳定性(推送长连接),隔离风险避免主进程崩溃\n" +
                                        "Binder优势：效率高(copy1次)，仅次于共享内存(无需copy),socket2次，但共享内存难控制进程不安全,binder基于CS架构易用;\n" +
                                        "安全性好：系统分配UID指明身份，其他依赖上层协议，上层说自己是谁就是谁，不安全；binder支持实名(注册SM)匿名(自己service)\n\n" +

                                        "传统IPC原理：用户空间-内核空间-用户空间(系统API copy_from_user，copy_to_user)\n" +
                                        "BinderIPC原理：发起方用户空间的数据直接拷贝到了接收方内核的内存映射(mmap把物理内存|磁盘文件映射到虚拟内存)中\n" +
                                        "用户空间-内核空间- 数据接收缓存区(映射前后) -用户空间\n" +
                                        "一句话：内核缓存区 和 接收方用户空间 同时映射到同一个共享接收缓存区，所以发送方 copy 到内核空间就相当于直接...\n" +
                                        "Binder驱动(动态可加载内核模块访问内核)\n" +
                                        "1.首先 Binder 驱动在内核空间创建一个数据接收缓存区\n" +
                                        "2.接着在内核空间开辟一块内核缓存区，建立内核缓存区和内核中数据接收缓存区之间的映射关系,以及内核中数据接收缓存区和接收进程用户空间地址的映射关系\n" +
                                        "3.发送方进程通过系统调用 copyfromuser() 将数据 copy 到内核中的内核缓存区，由于内核缓存区和接收进程的用户空间存在内存映射，因此也就相当于把数据发送到了接收进程的用户空间，这样便完成了一次进程间的通信\n\n" +

                                        "Binder线程数目最大为16个,超过会ANR\n" +
                                        "Client Server ServiceManager3个进程通过 Binder驱动交互\n" +
                                        "1.S向SM注册服务 2.C向SM获取服务 3.使用服务\n\n" +

                                        "AIDL细节：\n" +
                                        "1.新建AIDL接口文件，定义提供的接口(无权限修饰符,参数支持4个数，布尔，String，Parcel)\n" +
                                        "2.sync project 在build目录生成相应java文件(含Stub)\n" +
                                        "3.新建Service，Service 里创建一个内部类继承生成的Stub(继承自Binder)，实现接口方法,在onBind返回内部类的实例\n" +
                                        "4.把aidl文件 copy 到第二个项目(包名要一样),然后 sync project\n" +
                                        "    \n同步会生成同名接口文件( Stub继承Binder实现上述接口方法 )" +
                                        "5.通过隐式意图绑定service bindService(new Intent(\"actionInManifest\"),onServiceConnected中通过Stub.asInterface(IBinder)获取接口对象代理对象\n" +
                                        "6.自定义类型：正常建ParcelableBean.java，然后建同名.aidl文件，声明(parcelable AidlBean;)，最后接口中import\n" +
                                        "7.接口方法定向 tag：in服务端copy out服务端参数置空，改动同步到客户端 inout接收完整对象并同步，自己写的话有两个Parcel对象data(C-S) reply(S-C)\n" +
                                        "8.调用mRemote.transact时 同步情况客户端会挂起，一般都是同步，异步通过oneway修饰符修饰方法(oneway方法不能包含out定向)\n" +
                                        "9.回调：定义回调aidl接口文件，在AIDL接口文件中添加注册反注册方法，RemoteCallbackList 来遍历回调\n" +
                                        "10.身份校验：自定义权限(checkCallingPermission不能在onBind中调用，因为onBind没运行在binder线程池,可在onTransact中调),也可以pm.getPackagesForUid(getCallingUid)获取包名校验\n\n" +

                                        "监听Service：onServiceDisconnected，DeathRecipient(linkToDeath + unlinkToDeath)\n\n" +

                                        "Messenger接收消息后立刻通过线程池分发，这样任务串行可以效率.更加适合异步\n\n" +

                                        "Intent传递数据限制大小 1M-8k(mmap开辟共享空间的参数)，实际更小，因为数据要打包，数据包含包头命令等，而且如果是异步多线程，可用共享空间要÷2",
                                "Binder", "AIDL");
                        break;
                    }
                    case "稳定性相关": {
                        showMyDialog(moduleEntity.getName(),
                                "崩溃Crash：\n" +
                                        "崩溃信息：进程名线程名，前后台进程，是否UI线程，崩溃栈\n" +
                                        "系统信息：Logcat输出，机型厂商等，是否root，是否模拟器\n" +
                                        "内存信息：2G以下内存崩溃率明显高，系统可用内存小于10%容易出问题，\n" +//MemoryUtil
                                        "资源信息：文件句柄一般单进程允许最大1024，超过800就危险。线程数超过400就危险。\n\n" +

                                        "崩溃分析：\n" +
                                        "1.确定重点：先解决崩溃次数多和对业务有重大影响的,Native崩溃需要观察signal(SIGSEGV空指针非法指针 SIGABRT调用abort()退出导致)、code、fault add等\n" +
                                        "2.查找共性：上面不能定位出问题，可以看看有没有共性，比如是不是只出现在某款机型上。\n" +
                                        "3.尝试复现\n\n" +

                                        "Ask1：做了哪些稳定性的优化？\n" +
                                        "答：Crash优化(降低崩溃指标到千分之2以下) + 性能稳定性优化 + 业务稳定性优化\n" +
                                        "性能稳定性优化 启动速度优化，内存优化，卡顿优化，绘制优化。线下发现问题优化为主，线上监控为主n" +
                                        "业务稳定性优化：主流程埋点，计算每一步转化率，转化率低于某个值说明功能阻塞。新业务做功能开关或热修复"
                        );
                        break;
                    }
                    case "性能优化概述": {
                        showMyDialog(moduleEntity.getName(),
                                "布局优化：1.减少XML解析时间(删除无用控件，减少层次避免递归解析ViewGroup，栈帧过多爆栈) 2.避免过度绘制GPU耗时长(覆盖绘制，onDraw执行太多) 蓝绿粉红1234\n\n" +
                                        "内存优化：产生场景(静态变量持有 Context 等,单例实例持有 Context 等,匿名内部类持有外部类的实例,没有反注册)\n\n" +
                                        "ANR卡顿优化：产生场景(主线程耗时操作,处理大量数据CPU拿不到时间片)Systrace,BlockCanary.详见 ANR卡顿相关\n\n" +
                                        "线程优化：采用线程池，避免程序中存在大量的Thread。线程池可以重用内部的线程，从而避免了线程的创建和销毁锁带来的性能开销，同时线程池还能有效地控制线程池的最大并发数，避免大量的线程因互相抢占系统资源从而导致阻塞现象的发生.控制核心并发数，尽量和CPU核数保持一致\n\n" +
//                                        https://juejin.cn/post/7080065015197204511
                                        "启动优化：黑白屏(设置启动页主题,背景图片);setContentView(布局层级,过度绘制);延时初始化sdk+懒加载sdk\n\n" +
                                        "网络优化：弱网环境加载缩略图(下载速度,接口时间判断是否弱网)，retry次数，避免多次请求，避免轮询，接口精简\n\n" +
                                        "耗电优化：计算优化(for优化，switch替代if else，避开浮点运算) WakeLock使用不当\n\n" +
//                                        TODO"动画优化：矢量动画\n\n"+//PropertyValuesHolder
                                        "WebView优化：①预先创建WebView，提前将其内核初始化 ②使用WebView缓存池，注意内存泄漏 ③本地预置html和css，WebView创建的时候先预加载本地html，之后通过js脚本填充内容部分 ④WebView复用,单开进程"
                        );
                        break;
                    }
                    case "内存问题根本原因": {
                        showMyDialog(moduleEntity.getName(),
                                "OOM：\n" +
                                        "1.过大内存导致堆或栈不够用，超出进程内存限制\n" +
                                        "2.内存碎片，没有足够可用的连续内存\n" +
                                        "3.打开文件太多：Android6.0限制1024。优化：当文件描述符fd大于1000或连续递增超过50，就触发收集，将对应路径上报\n" +
                                        "4.线程创建太多：系统对每个进程的线程数有限制(每个线程约占2M虚拟内存)\n\n" +

                                        "内存抖动：\n" +
                                        "1.申请次数过多引发GC，GC会stw，引起ANR卡顿\n" +
                                        "2.短时间申请大量对象 申请速度 > 回收速度\n\n" +

                                        "内存泄漏：\n" +
                                        "1.没有反注册。解决：反注册\n" +
                                        "2.长生命周期对象持有短生命周期对象的强引用 or 内部类被长生命周期的实例引用，导致外部类无法释放。解决：外部类的对象使用弱引用，内部类static化\n" +
                                        "3.Dialog依附的Activity销毁了，但是忘记做dialog的dismiss操作.\n" +
                                        "4.WebView导致的内存泄漏 一些机型会出现WebView相关的泄漏，即使调用WebView的destroy也无法销毁。解决：将WebView相关的业务移至另一个进程,AIDL通信",
                                "OOM", "内存抖动", "内存泄漏");
                        break;
                    }
                    case "内存优化": {
//                        https://www.jianshu.com/p/156e47c2cdc7
                        showMyDialog(moduleEntity.getName(),
                                "线下推荐LeakCanary检测，Profiler分析。线上监控为主KOOM\n\n" +

                                        "分析工具：\n" +
                                        "LeakCanary：原理：监听Activity生命周期->onDestroy时创建弱引用和引用队列->延迟5秒判断Activity有没有被回收->如果没有回收,调用GC，再此判断是否回收，如果还没回收，则内存泄露了->dump,分析hprof文件，找到泄露路径通知用户\n" +
                                        "限制：只能用于debug；三方SDK/framework/非Activity 的内存泄漏不方便\n" +
                                        "分析引擎：Shark\n\n" +

                                        "Profiler：\n" +
                                        "1.点击Profiler\n" +
                                        "2.点击Memory\n" +
                                        "3.选择Capture heap dump抓取堆栈选项(也可以选择 抓取原生内存 和 抓取Java/Kotlin 内存)\n" +
                                        "4.点击Record抓取一段时间的内存分配信息(抓取前可以先点上方垃圾桶GC)\n" +
                                        "5.几秒后出结果。有leaks泄漏数量提示,可以选择 需检查的堆 根据类或包分组 展示类或泄漏ActivityFragment.主要关注：Allocations(对象个数) 和 Shallow Size(此对象内存)\n" +
                                        "可以选择 Show nearest GC root only.找到问题,该释放释放.\n\n" +

                                        "MAT(MemoryAnalyzerTool)：效率比较低\n" +
                                        "1.用profiler抓取完，会在左侧dump2次，拿到2个hprof文件\n" +
                                        "2.用hprof-conv -z 1.hprof 2.hprof 转换(否则MAT打开报错)，-z表示排除非 APP 内存的堆\n" +
                                        "3.如果分析一个hprof，打开hprof文件，选择Leak Suspects Report,切换Histogram直方图，排除虚、弱、软引用\n" +
                                        "4.用MAT打开2个hprof，切换为Histogram直方图查看内存中的对象，对象的个数以及大小\n" +
                                        "    Dominator Tree可以列出那个线程，以及线程下面的那些对象占用的空间\n" +
                                        "    Top consumers 通过图形列出最大的object\n" +
                                        "    Leak Suspects 通过MA自动分析泄漏的原因\n" +
                                        "5.用第二次的hprof文件 select baseline 第一次的hprof文件,出现增加了的对象\n" +
                                        "6.选中不应该增加的对象(Activity之类),右击查看引用链(Merge ShortestPaths To GC Roots -> exclude all只看强引用)\n\n" +

                                        "线上监控方案：\n" +
                                        "KOOM快手：\n" +
                                        "内存阈值监控来触发镜像采集：如内存突破阈值，上涨速度突破阈值，线程数和文件描述符突破阈值\n" +
                                        "检测到触发 开始Dump - 暂停虚拟机(调用挂起线程API) - fork子进程(copy on write) - 恢复虚拟机(调用resume线程API) - 子进程dumpHprof - 通知父进程解析\n" +
                                        "裁剪hprof：解析dump在写入文件时hook，过滤不需要的信息，只写入对象大小和引用关系\n" +
                                        "其他部分参考leakCanary\n\n" +

                                        "Probe美团：\n" +//https://tech.meituan.com/2019/11/14/crash-oom-probe-practice.html
                                        "堆内存不足：需要获得内存快照Debug.dumpHprofData\n" +
                                        "在一个后台线程中每隔1S去获取当前进程的内存占用,当内存占用达到设定的阈值时dump\n" +
                                        "HPROF线上分析：对HAHA的原始算法做了诸多优化，成功率也只有65%\n" +
                                        "HPROF回传：写入文件时，拿到字节流进行裁剪操作，只保留引用关系及对象大小，丢弃值和其他\n" +
                                        "最终方案：方案融合,因为部分机型裁剪失败\n" +
                                        "线程数超出限制：获取当前进程所占用的虚拟内存、进程中的线程数量、每个线程的信息（线程名、所属线程组、堆栈信息）以及系统的线程数限制，并将这些信息上传\n" +
                                        "Thread.getAllStackTraces可以得到进程中的所有线程以及对应的堆栈信息\n" +

                                        "GC原理：可达性分析\n" +
                                        "GCRoots：\n" +
                                        "两个栈 虚拟机栈 和 Native 栈中所有引用的对象\n" +
                                        "两个方法区：方法区中的常量和静态变量\n" +
                                        "所有线程对象\n" +
                                        "所有跨代引用对象\n\n" +

                                        "exp:系统内存泄漏：Android6.0以前，输入法存在内存泄漏，利用反射onDestroy置空解决\n\n" +

                                        "解决内存问题最有效的方案：良好的编码习惯(阿里规范)：\n" +
                                        "1.不要使用比需求占用空间更多的数据类型\n" +
                                        "2.数据结构的合理选择(数据量千级以内可用SparseArray，ArrayMap，性能不如HashMap但更节省内存)\n" +
                                        "3.IntentService(Handler,后台耗时,自动停止,串行)代替Service(运行在主线程,不能耗时)" +
                                        "4.统一使用线程池，可以很大程度上避免线程数的溢出问题",
                                "LeakCanary", "MAT(MemoryAnalyzerTool)", "KOOM", "Probe");
                        break;
                    }
                    //https://www.sohu.com/a/473420144_121124376
                    case "ANR卡顿相关": {
                        showMyDialog(moduleEntity.getName(),
                                "应用的卡顿，主要是在主线程阻塞Handler处理消息(dispatchMsg)太耗时导致的\n" +
                                        "Looper是主线程的消息调度者，在Looper的loop方法中，有一个Printer，它在每个Message处理的前后被调用\n" +
                                        "而如果主线程卡住了，就是dispatchMsg里卡住了，用Printer的println前后时间去计算主线程一条Message处理的时长，当时长超过设定的阈值时就判定是卡顿了\n" +
                                        "Looper.getMainLooper.setMessageLogging()\n" +
                                        "Tip：监听到发生卡顿之后， dispatchMessage 早已调用结束，已经出栈，此时再去获取主线程堆栈，堆栈中是不包含卡顿的代码的\n" +
                                        "可以在后台开一个线程，定时获取主线程堆栈，存入Map<时间,堆栈信息>，发生卡顿时取卡顿时间段内的堆栈信息即可\n" +
                                        "此方案只适合线下，因为后台线程频繁获取堆栈影响性能.获取主线程堆栈，会暂停主线程的运行\n\n" +

                                        "线上卡顿监控：\n" +
                                        "通过Gradle Plugin+ASM，编译期在每个方法开始和结束位置分别插入一行代码，统计方法耗时\n" +
                                        "注意点1：避免方法数暴增,方法的入口和出口应插入相同的函数，在编译时提前给每个方法分配一个独立的 ID 作为参数\n" +
                                        "注意点2：过滤一些类似直接 return、i++ 这样的简单函数，并且支持黑名单配置。对一些调用非常频繁的函数，添加到黑名单中来降低整个方案对性能的损耗\n\n" +

                                        "Systrace：精确到Frame（帧）的粒度，能粗略分析定位，但深度有限不能找出引起CPU满载的真正原因\n" +
                                        "使用方式：DDMS,命令行，代码\n" +
                                        "Chrome分析：\n" +
                                        "Alerts区域：会显示出现了jank(一帧画了多次)，measure,draw花了多少时间，会给建议\n" +
                                        "CPU区域：每一行代表一个CPU核心执行任务的时间片，每个色块代表一个执行的进程，色块的长度代表其执行时间，可点击色块看详细信息(进程,线程，开始，持续)\n" +
                                        "应用区域：每一帧就是一个F圆圈，F圆圈 绿色流畅 黄色和红色超过16.6ms，点击圆圈会显示Frame信息，花费时间和建议\n" +
                                        "右侧Alerts按钮会给出汇总，包含Alert类型，次数\n\n" +

                                        "Traceview：\n" +
                                        "Debug.startMethodTracing Debug.stopMethodTracing()\n" +
                                        "DeviceFileExplorer找到生成的.trace，打开\n" +
                                        "可以看到 调用方法的名称 方法调用的次数 方法调用的总时间 方法单独执行的时间\n\n" +

                                        "ANR原理：\n" +
                                        "发送延时消息，对应时间内未收到相应，触发ANR.input需要等收到下一次input事件，才会去检测上一次input事件是否超时\n" +
                                        "以启动Service为例，Service的onCreate方法调用之前会使用Handler发送延时10s的消息，Service 的onCreate方法执行完，会把这个延时消息移除掉\n" +
                                        "前台服务20s，后台服务200秒，前台广播10s，后台广播60秒，输入事件5s，定义在AMS中\n\n" +

                                        "ANR分析：\n" +
                                        "android5.0前：分析 /data/anr/traces.txt文件,分析日志文件时，先搜索main主线程看主线程堆栈，是否因为i锁等待。然后看iowait，CPU，GC，system Server等信息，确定是I/O问题，CPU竞争，还是大量GC导致\n" +
                                        "WatchDog：(volatile _tick)往UI线程postRunnable，睡眠5s，5s后看是否被执行(问题：2s卡顿后才开始睡眠3s后执行完)\n\n"

                                , "线上卡顿监控", "ANR原理");
                        break;
                    }
                    case "启动优化": {
                        showMyDialog(moduleEntity.getName(),
                                "体验优化：黑白屏，通过设置主题(背景设置透明或图片)\n\n" +

                                        "Application启动优化：部分sdk延迟初始化，部分延迟到用的时候再初始化，部分sdk只能主线程初始化\n\n" +

                                        "启动页Activity的优化：布局层级优化，避免I/O操作阻塞主线程"
                                , "黑白屏", "Application启动优化", "启动页Activity");
                        break;
                    }
                    case "Glide": {
                        showMyDialog(moduleEntity.getName(),
                                "with：返回 RequestManager,如果主线程传入Activity或fragment，创建不可见fragment绑定生命周期\n" +
                                        "load：返回 RequestBuilder,收集配置信息\n" +
                                        "into：构建target对象,然后构建Request对象(一堆前面设置的配置),然后runRequest.\n\n" +

                                        "内存缓存(弱引用缓存防止正在使用的图片被LRU回收掉，LRU最近最少使用，图片加载完成放入弱引用缓存，acquired>0，弱引用，=0 LRU)和磁盘缓存.如果遇到url里有可变token，重写GlideUrl，实现getCacheKey方法移除token部分\n\n" +
                                        "CustomTarget;CustomViewTarget<View的类型, 图片类型>(可以在回调中getView获取View)\n" +
                                        "支持GIF\n" +
                                        "缓存 key 包含长宽\n" +
                                        "圆角滤镜之类的变换\n" +
                                        "显示效果(淡入淡出等)\n" +
                                        "尺寸优化inSampleSize\n" +
                                        "内存复用优化(inBitmap机制，BitmapPool)\n" +
                                        "加载大图BitmapRegionDecoder 滑动时内存抖动，卡顿现象比较明显，不能用于线上\n" +
                                        "有加载大图开源库,将大图切片,再判断是否可见，如果可见则加入内存中，否则回收.同时根据不同的缩放比例选择合适的采样率"
                                , "with", "绑定生命周期", "load", "api", "弱引用缓存", "LRU", "内存复用优化");
                        break;
                    }
                    case "网络请求过程": {
                        showMyDialog(moduleEntity.getName(),
                                "应用层协议：HTTP RTMP\n" +
                                        "传输层：TCP\n" +
                                        "网络层：IP(寻址)\n" +
                                        "网络接口层：PPP\n\n" +

                                        "client 发起请求,DNS 解析域名拿到ip地址，经过三次握手与 server 建立 tcp 连接\n" +
                                        "client 发送 http 请求与头信息\n" +
                                        "server 对请求进行应答，响应头信息与返回信息\n" +
                                        "关闭 tcp 连接结束会话 (if 头信息包含 connection:keep-alive 连接复用)\n\n" +
                                        "建立连接 3 次握手,断开连接 4 次挥手"
                        );
                        break;
                    }
                    case "OkHttp": {
                        showMyDialog(moduleEntity.getName(),
                                "OkHttpClient、Request、Call、Response\n\n" +

                                        "分发器Dispatcher：维护线程池与三个双端队列(runningAsync readyAsync sync)\n" +
                                        "连接复用(ConnectionPool连接池,同一个address共享链接，减少建立销毁连接的资源消耗。主要就是管理一个双端队列，可以用的连接就直接用)\n\n" +

                                        "分发器 ask 1、分发器dispatcher如何决定将请求放入runningAsyncCalls还是readyAsyncCalls？\n" +
                                        "答：满足最大请求数(默认64)和单host最大连接数(默认5)，才放入runningAsync\n\n" +

                                        "分发器 ask 2、从readyAsyncCalls移动到runningAsyncCalls的条件是什么？\n" +
                                        "答：每个请求执行完成就会从running移除，同时进行第一步相同逻辑的判断，决定是否移动\n\n" +

                                        "分发器 ask 3、分发器dispatcher线程池是怎么工作的？\n" +
                                        "答：线程池队列用了 核心线程数0 最大线程数 Integer.MAX_VALUE 配合SynchronousQueue(无容量队列),实现无等待缓冲，最大并发\n\n" +

                                        "分发器 ask 4、为什么要使用双端队列，不用 LinkedList？\n" +
                                        "答：双端队列基于数组实现，LinkedList 基于链表，当执行完一个请求或入队新的请求时，对readyAsyncCalls遍历。由于链表物理不连续，效率不高\n\n" +

                                        "拦截器：责任链模式(类似事件分发,将网络请求的各个阶段封装到各个链条中),realCall 的 run 方法中调用 getResponseWithInterceptorChain，该方法中会 new RealInterceptorChain，里面包含了拦截器 list 和 index，通过index来标记执行哪个拦截器,proceed\n" +
                                        "1、RetryAndFollowUpInterceptor 重试与重定向：用户可以设置是否重连，重连的话再走一遍链\n" +
                                        "2、BridgeInterceptor 桥接：对用户构建的Request进行添加(HTTP必备的头如HOST、Content-Length、Content-Type、User-Agent等)或者删除相关头部信息,并添加默认行为(gzip压缩)\n" +
                                        "3、缓存拦截器：第一次拿到响应后根据头信息决定是否缓存,后续如果命中缓存则不会发起网络请求。使用Okio来实现缓存文件的读写。默认只支持get请求缓存.可以缓存method为HEAD和部分POST请求，但实现起来的复杂性很高而收益甚微\n" +
                                        "4、ConnectInterceptor 连接拦截器：内部会维护一个连接池，负责连接复用、创建连接(三次握手等等)、释放连接以及创建连接上的socket流\n" +
                                        "5、CallServerInterceptor 请求拦截器：真正发起了网络请求\n\n" +

                                        "addInterceptor与addNetworkInterceptor区别？\n" +
                                        "答：addInterceptor放在最前面，addNetworkInterceptor放在CallServerInterceptor前，只观察在网络上传输的数据\n\n" +

                                        "OkHttp怎么实现连接池?\n" +
                                        "频繁建立断开Socket非常消耗资源,keepalive可以在一次TCP连接中可以持续发送多份数据而不会断开连接,复用连接需要连接池\n" +
                                        "OkHttp中使用ConnectionPool实现连接池，默认支持5个host,ConnectionPool中维护了一个双端队列Deque，也就是两端都可以进出的队列，用来存储连接,在连接拦截器中维护连接池，定期清理连接\n" +

                                        "其他优化：HttpDNS：防DNS劫持(Http协议去进行DNS解析请求,代替使用域名)\n" +
                                        "构造者模式（OkhttpClient,Request 等各种对象的创建）\n" +
                                        "责任链模式（拦截器的链式调用）\n",
                                "连接复用", "无等待缓冲，最大并发", "责任链模式");
                        break;
                    }
                    case "Retrofit": {
                        showMyDialog(moduleEntity.getName(),
                                "why use Retrofit?\n" +
                                        "1.OkHttp网络请求接口配置繁琐，header，body，参数等\n" +
                                        "2.数据需要用户手动解析,不能复用\n" +
                                        "3.不能自动进行线程切换\n" +
                                        "4.嵌套网络请求，无法避免回调陷阱\n\n" +

                                        "网络请求框架的封装,接口以及注解描述网络请求，解耦\n\n" +

                                        "将请求的Method对象，及其方法注解，方法参数，方法参数注解等信息封装成一个ServiceMethod对象\n" +
                                        "根据ServiceMethod对象，创建一个OkhttpCall对象\n" +
                                        "调用callAdapter.adapt(okhttpCal)方法返回Call<T> 完成api.getData()的执行\n\n" +

                                        "serviceMethodCache ConcurrentHashMap<Method, ServiceMethod>缓存\n" +
                                        "IO优化，比如 buffer 复用\n" +
                                        "数据的压缩加密\n" +
                                        "弱网优化\n\n" +

                                        "动态代理(不需自己写网络请求实现)，建造者模式(Build)，门面模式(组装，忽略细节)，饿汉static final单例模式,(适配器模式，工厂模式，策略模式)\n" +
                                        "converter解析器：Streaming用Streaming解析器，否则用buffer读进内存\n" +
                                        "callAdapter:适配器,把Call返回类型适配为想要的类型(RxJava,LiveData之类),每个Api方法的返回类型都需要一个CallAdapter对象与之对应",
                                "动态代理", "ServiceMethod", "建造者模式", "解析器", "适配器");
                        break;
                    }
                    case "应用安装": {
                        showMyDialog(moduleEntity.getName(),
                                "1.拷贝apk文件到/data/app(system/app)\n" +
                                        "2.解压apk，拷贝dex(dex拷贝到/data/dalvik-cache缓存)，创建应用的数据目录(/data/data/包名)\n" +
                                        "3.PackageManagerService解析AndroidManifest.xml，写入/data/system/packages.xml(权限、应用包名、icon、apk 的安装位置、版本、userID 等等)\n" +
                                        "4.桌面Launcher生成应用入口",
                                "拷贝", "解压", "拷贝dex", "创建", "数据目录", "解析", "生成应用入口");
                        break;
                    }
                    case "APK打包流程": {
                        showMyDialog(moduleEntity.getName(),
                                "打包工具:SDK/build-tools目录下\n\n" +

                                        "1.aidl.exe 将aidl文件转换成Java接口文件\n" +
                                        "2.aapt.exe 打包资源文件，生成R.java文件 和 resources.ap_文件(包含所有资源，resource.arsc，加密过的AndroidManifest.xml)\n" +
                                        "3.javaCompile工具 编译(javac) R、Java 接口文件、Java 源文件 生成.class\n" +
                                        "4.dx.bat 将.class转成dex字节码(压缩常量池,清除冗余信息,加入三方库)\n" +
                                        "5.ApkBuilder(压缩工具) 打包资源文件和dex文件 生成未签名apk\n" +
                                        "6.签名工具签名(真实性,防篡改)\n" +
                                        "7.ZipAlign对齐(使apk包中的所有资源文件距离文件起始偏移为4字节的整数倍，提高加载和运行速度)",
                                "生成.class", "class转成dex字节码", "生成未签名apk", "签名", "对齐");
                        break;
                    }
                    case "APK构造及瘦身": {
                        showMyDialog(moduleEntity.getName(),
                                "Apk组成：\n" +
                                        "lib(so库文件)\n" +
                                        "assets文件夹(不经过aapt编译的assets目录下资源文件)\n" +
                                        "classes.dex(字节码程序, 可执行文件)\n\n" +
                                        "res(经过aapt编译的资源文件)\n" +
                                        "resources.arsc(资源索引表,提供资源ID到资源文件路径的映射关系)\n" +
                                        "AndroidManifest.xml 清单文件(配置组件的注册信息,权限)\n" +
                                        "META-INF(签名信息，验证应用完整性)\n" +

                                        "优化:\n" +
                                        "代码混淆(压缩,移除未使用代码)\n" +
                                        "删除冗余资源(remove unused res)\n" +
                                        "drawable只使用一套 比如x或xx\n" +
                                        "图片优化(tinypng,tint)\n" +
                                        "so库(只保留v7a,v8a)插件化(功能模块按需下载)\n" +
                                        "System.loadLibrary(path),只能加载jniLibs目录下so\n" +
                                        "System.load(path)可以加载任意路径下so"
                                , "混淆", "删除冗余资源", "drawable", "tinypng", "so库");
                        break;
                    }
                    case "APK加固及反编译": {
                        showMyDialog(moduleEntity.getName(),
                                "原应用的dex文件加密，附加在壳程序的dex文件后面，加载时替换dex生成的Element数组。替换原应用的Application并回调onCreate\n\n" +

                                        "代码层加固：1混淆 2Dex加密,启动解密，重新加载解密后的dex\n" +
                                        "JNI层：DexClassLoader动态加载技术完成对加密class.dex的动态加载，dex文件可以附属在assert或raw目录\n\n" +

                                        "增加逆向难度(java代码native化)\n\n" +

                                        "反编译:dex文件通过 dex2jar 转换为jar文件，然后通过jd-gui反编转为class文件\n\n" +

                                        "加固流程:\n" +
                                        "1.把原apk解压，其中的classes.dex文件通过加密程序进行加密\n" +
                                        "2.将生成的加密dex文件和壳dex文件合并,加密的dex文件追加在壳dex文件后面，并在文件末尾追加加密dex文件的大小数值\n\n" +

                                        "解密流程:\n" +
                                        "1.壳程序ProxyApp.attachBaseContext方法内，将加密的dex文件读取出来，解密并保存到资源目录下\n" +
                                        "2.DexClassLoader里DexPathList.findClass(遍历dexElements)，dexElements通过makeDexElements静态方法初始化。" +
                                        "所以可以通过反射得到真正的dex生成的Element数组，替换原来的Element数组\n" +
                                        "3.onCreate中将主App的Application创建出来，并通过反射修改ActivityThread类,替换系统中的Application引用(两个地方)\n" +
                                        "4.创建原Application对象，并调用原Application的onCreate方法启动原程序\n\n" +

                                        "BootClassLoader：framework类加载器,比如Application\n" +
                                        "PathClassLoader：Android类加载器(自己写的),MainActivity AppCompatActivity(三方框架)\n" +
                                        "DexClassLoader：额外提供的\n" +
                                        "DexPathList pathList\n" +
                                        "DexPathList属性dexElements = [patch.dex,class1.dex,class2.dex,...]"
                        );
                        break;
                    }
                    case "Dex文件相关": {
                        showMyDialog(moduleEntity.getName(),
                                "本质上是class文件.java加载类是按class一个一个加载(IO)，因为手机性能问题，进行汇总，各个类能够共享数据，大小约JAR的1/2\n" +
                                        "dex把所有.class的 header头(包含JDK)写成了一个,所有的常量放在一个池里,.dex只维护了一个索引\n\n" +

                                        "文件头：包含checksum(校验码4字节)，签名(20字节,完整性校验)，文件长度(4字节Dex文件的大小)等\n" +
                                        "索引区：字符串索引,类型索引,方法索引...\n" +
                                        "数据区：类的定义区,数据区,链接数据区\n\n" +

                                        "class加载用双亲委派：\n作用1：不会重复加载；\n作用2：防止核心class被篡改(自己定义java.lang.String.class)"
                        );
                        break;
                    }
                    case "热修复原理": {//https://zhuanlan.zhihu.com/p/75465215
                        showMyDialog(moduleEntity.getName(),
                                "阿里AndFix(Android2.3到Android7.0)：native hook 替换 ArtMethod(方法在虚拟机中的实现)\n" +
                                        "过程：生成补丁包,类加载器classLoader 拿到有MethodReplace注解的类，替换method方法(native,java做不到)\n" +
                                        "实时生效;兼容性差,每个版本的实现都有差别,需要做大量适配\n\n" +

                                        "QZone：dex插桩(补丁 dex 插入到 dexElements 最前面)\n" +
                                        "因为class校验(提升性能)问题，往所有类的构造函数里面插入了一段代码，影响性能\n" +
                                        "实现简单;重启生效(Class 重新加载);Art虚拟机 oat 导致的地址偏移,需要在补丁包中打入补丁无关的类(防止类被打上标记)，导致补丁包体积增大\n\n" +

                                        "Tinker：dex替换(和 dex 插桩类似)\n" +
                                        "增量更新(dexdiff bsdiff 工具对比新旧dex,得到差分包,bspatch可以将旧包和patch合成为新包,android中使用NDK so jni完成合成),然后重启加载合成的dex文件.\n" +
                                        "兼容multidex：对于Dalvik：将补丁Dex反射插入到pathlist的前面；对于Art：parent classloader。Dalvik不采用parent classloader是因为Dalvik存在一个checkasses\n" +
                                        "如果A.dex没有引用了其他dex的class，加标记表明不允许修改\n\n" +

                                        "美团Robust：InstantRun\n" +
                                        "原理：给每个 Class 中新增一个 changeQuickRedirect 的静态变量，并在每个方法执行之前，对这个变量进行了判断，如果这个变量被赋值了，就调用补丁类中的方法，如果没有被赋值，还是调用旧方法\n" +
                                        "gradle插件，每个类都插入一段逻辑(字节码插桩),同样生成补丁包,类加载器classLoader，替换插桩添加的本地变量,将方法执行的代码重定向到其他方法\n" +
                                        "兼容性好,实时生效.侵入性高,增大包体积\n\n" +

                                        "资源热修复方案：\n" +
                                        "1.替换 AssetManager：构造新的AssetManager，反射调用 addAssetPath,找到所有引用 AssetManager 的地方，通过反射将其替换为新的 AssetManager\n" +
                                        "2.添加修改的资源到 AssetManager 中，并重新初始化\n\n" +

                                        "so 热修复方案：\n" +
                                        "1.System.loadLibrary 加载已经安装的 apk 中的 so，System.load 加载自定义路径下的 so\n" +
                                        "2.反射注入补丁 so 路径：DexPathList中除了有 dexElements 变量，还有nativeLibraryPathElements，像 dex 插桩一样的方法，将 so 的路径插入到 nativeLibraryPathElements 之前即可"
                        );
                        break;
                    }
                    case "插件化": {//https://hanshuliang.blog.csdn.net/article/details/117391407
                        showMyDialog(moduleEntity.getName(),
                                "模块化：业务导向.解耦+独立管理。把常用的功能、控件、基础类、第三方库、权限等公共部分抽离封装，把业务拆分成N个模块进行独立(module)的管理\n\n" +

                                        "组件化：功能导向.模块化+可转换性。app分成多个模块，功能拆分，单独编译，单独开发，根据需求动态配置组件\n\n" +

                                        "插件化：免安装运行apk。主要问题：如何加载类、如何加载资源、如何管理组件生命周期\n" +
                                        "实现：\n" +
                                        "1.使用 DexClassLoader(插件包加载路径,解压目录,null,父类加载器) 加载 Activity 对应的 Class 字节码类对象\n" +
                                        "2.创建 代理Activity, 通过其生命周期回调管理 插件Activity\n" +
                                        "3.使用 AssetManager 加载插件包资源"
                        );
                        break;
                    }
                    case "字节码插桩": {
                        showMyDialog(moduleEntity.getName(),
                                "Google在Android Gradle的1.5.0 版本以后提供了 Transfrom API，允许开发者在项目的编译过程中操作 .class 文件\n\n" +

                                        "AspectJ：成熟稳定、使用非常简单。性能较低(封装一层自己的类，字节码大，原函数有影响)\n" +//https://www.jianshu.com/p/2e8409bc8c3b
                                        "定义文件@Aspect，@Around(正则) ProceedingJoinPoint\n\n" +

                                        "ASM：更通用，需要对字节码指令有深入了解\n" +
                                        "微信的方案就是字节码插桩\n" +
                                        "需要注意的问题1：避免方法数暴增：在方法的入口和出口应该插入相同的函数，在编译时提前给代码中每个方法分配一个独立的 ID 作为参数\n" +
                                        "需要注意的问题2：过滤简单的函数：过滤一些类似直接 return、i++ 这样的简单函数，并且支持黑名单配置。对一些调用非常频繁的函数，需要添加到黑名单中来降低整个方案对性能的损耗。\n" +
                                        "微信Matrix做了大量优化，对性能影响整体可以接受，不过依然只会在灰度包使用\n" +
                                        ""
                        );
                        break;
                    }
                    case "安全": {
                        showMyDialog(moduleEntity.getName(),
//                                https://www.jianshu.com/p/7f2202c18012/
                                "组件安全：android:exported 是否支持其它应用调用当前组件\n" +
                                        "android:allowBackup置为false，避免应用内数据备份泄露(adb backup 和 adb restore 来备份和恢复应用数据)\n" +
                                        "LocalBroadcast，基于handler实现，效率更高，数据仅限于应用内部传输，避免被拦截伪造\n" +
                                        "WebView:谨慎支持JS功能；https安全及避免被劫持；如不需要，禁止file协议；密码明文保存漏洞WebSettings.setSavePassword(false)\n" +
                                        "秘钥及敏感信息 可以使用JNI将敏感 信息写到Native层\n" +
                                        "signingConfigs：可以将密码保存到不受版本控制的gradle.properties\n" +
                                        "debuggable:false\n" +
                                        "日志：正式版关闭移除日志\n" +
                                        "混淆加固\n" +
                                        "漏洞检测工具\n"
                        );

                        break;
                    }
                    case "版本适配": {
                        showMyDialog(moduleEntity.getName(),
                                "6 动态权限适配\n" +
                                        "7 FileProvider\n" +
                                        "9 刘海屏,停用明文,前台服务\n" +
                                        "10 分区存储\n" +
                                        "11 强制分区存储 应用内通过File,外部通过MediaStore，外部自己创建的文件无需权限(卸载重装后需要权限)"
                        );
                        break;
                    }
                    case "开机": {
                        showMyDialog(moduleEntity.getName(),
                                "启动电源，(引导芯片从ROM)加载引导程序(BootLoader)到RAM执行\n" +
                                        "引导程序(BootLoader)拉起Android系统(Linux)并运行\n" +
                                        "Linux 内核启动,系统设置,在系统文件中寻找init.rc文件，并启动init进程(init.rc相当于配置文件，记录了需要开启的服务)\n" +
                                        "init进程初始化和启动属性服务,并且 fork 出 Zygote 进程\n" +
                                        "Zygote进程创建虚拟机(JVM)并注册JNI方法，创建服务器端Socket，启动SystemServer进程\n" +
                                        "SystemServer 进程 启动Binder线程池和SystemServiceManager，并且启动各种系统服务\n" +
                                        "AMS(被SystemServer进程启动)启动Launcher\n\n" +

                                        "init进程：第一个进程，进程号是1，工作：\n" +
                                        "1.创建和挂载启动所需的文件目录(这些目录系统停止时会消失)\n" +
                                        "2.初始化和启动属性服务(设备的id、序列号、厂商)\n" +
                                        "3.解析init.rc配置文件并启动Zygote进程(c/c++层)\n\n" +

                                        "Zygote进程：\n" +
                                        "1.创建java虚拟机并为java虚拟机注册JNI方法\n" +
                                        "2.通过registerZygoteSocket方法创建服务器Socket,等待 AMS 请求Zygote来创建新的应用程序进程，同时进行预加载类和资源\n" +
                                        "3.启动SystemServer进程\n\n" +

                                        "SystemServer进程：\n" +
                                        "1.启动Binder线程池(native)，这样就可以与其他进程进行通信(对于服务端而言，只需要一个Service就可以了，避免了重复创建Service)\n" +
                                        "2.创建SystemServiceManager,它会对系统服务进行创建、启动和生命周期管理\n" +
                                        "3.启动各种系统服务(80+):引导服务(PowerManagerService,AMS,PMS),核心服务(BatteryService),其他服务(WMS,CameraService,AlarmManagerService)\n\n" +

                                        "AMS 启动 Launcher.java(继承Activity),Launcher 在启动过程中会请求PackageManagerService返回系统中已经安装的应用程序信息\n\n" +
                                        "https://blog.csdn.net/shaoenxiao/article/details/87088982"

                                , "加载引导程序", "拉起Android", "init.rc文件，并启动init", "fork 出 Zygote", "Zygote进程创建虚拟机(JVM)并注册JNI方法，创建服务器端Socket，启动SystemServer进程",
                                "SystemServer 进程 启动Binder线程池和SystemServiceManager", "启动Launcher");
                        break;
                    }
                    case "View绘制原理": {
                        //https://www.jianshu.com/p/386bbb5fa29a
                        showMyDialog(moduleEntity.getName(),
                                "双缓存+Vsync\n\n" +

                                        "CPU负责计算帧数据，把计算好的数据交给GPU，GPU会对图形数据进行渲染，渲染好后放到buffer(图像缓冲区)里存起来，然后Display（屏幕或显示器）负责把Buffer里的数据呈现到屏幕上\n" +
                                        "屏幕刷新率：60Hz 90Hz 硬件刷新画面的频率\n" +
                                        "帧率：60FPS 90FPS 一秒内绘制合成产生的帧数\n" +
                                        "屏幕刷新率比系统帧率快：在前缓冲区内容全部映射到屏幕上之后，后缓冲区尚未准备好下一帧，屏幕将无法读取下一帧，所以只能继续显示当前一帧的图形，造成一帧显示多次，也就是卡顿\n" +
                                        "系统帧率比屏幕刷新率快：屏幕撕裂\n\n" +

                                        "垂直同步(VSync)：当屏幕从缓冲区扫描完一帧到屏幕上之后，开始扫描下一帧之前，中间会有一个时间间隙，此时发出的一个同步Vsync信号，该信号用来切换前缓冲区和后缓冲区\n" +
                                        "但 上层的CPU和GPU并不知道Vsync信号的到来，所以在底层屏幕的Vsync信号发出后并没有及时收到并开始下一帧画面的操作处理，导致丢帧卡顿\n\n" +

                                        "Android4.1引入黄油计划：设计了Choreographer，在系统收到VSync信号后，上层CPU和GPU马上开始进行下一帧画面数据的处理，完成后及时将数据写入到Buffer中\n" +
                                        "Choreographer：1.承上.负责接收和处理 App 的各种更新消息和回调，等到 Vsync 到来的时候统一处理\n" +
                                        "Choreographer：2.启下.负责请求和接收 Vsync 信号\n" +
                                        "应用App有界面UI的变化时，最终都会调用ViewRootImpl.scheduleTraversals(往Choreographer中放入一个CALLBACK_TRAVERSAL类型的绘制任务)\n" +
                                        "Choreographer注册下一个VSync信号，收到VSync信号之后，主线程MessageQueue发送了一个异步消息，消息执行ViewRootImpl#doTraversal\n" +
                                        "然后真正开始绘制一帧的操作.\n\n" +

                                        "ViewRootImpl.setLayoutParams -> requestLayout -> scheduleTraversals -> \n" +
                                        "UI线程插入同步屏障 —> 往Choreographer注册一个Runnable ->\n" +
                                        "ViewRootImpl.doTraversal -> performTraversals -> performMeasure...\n\n" +

                                        "draw(如果开启硬件加速,走硬件绘制的流程.否则走drawSoftware软件绘制的流程)\n\n" +

                                        "requestLayout会触发onMeasure和onLayout，不一定会onDraw(layout过程发现l,t,r,b和以前不一样，那就会触发一次invalidate)\n" +
                                        "invalidate()不会导致onMeasure和onLayout被调用，而OnDraw会被调用\n" +
                                        "invalidate支持区域绘制，传入Rect或ltrb\n\n" +

                                        "Ask:为什么不能在子线程中更新UI？\n" +
                                        "答:更新UI最终会调用ViewRootImpl.requestLayout{checkThread}检查线程(mThread != Thread.currentThread())。\n" +
                                        "View 绘制是在handlerResume中调用，所以 onResume 中获取不到宽高，并可以子线程中更新UI，因为decor还没有绑定window\n" +
                                        "这么设计主要为了方尺不同线程更新UI导致绘制错乱\n" +
                                        "SurfaceView在子线程中绘制(适用被动更新,不会阻塞主线程,加锁,可以控制刷新频率,底层利用双缓存机制,绘图时不会出现闪烁问题)\n" +
                                        "Toast的弹出绘制，也不是主线程"
                        );
                        break;
                    }
                    case "触摸屏幕": {
                        showMyDialog(moduleEntity.getName(),
                                "InputManagerService负责触摸事件的采集(InputReader线程),封装成Event,发通知,请求派发消息(InputDispatcher线程)\n" +
                                        "WMS负责找到目标窗口,通过socket发送到目标窗口APP(from SystemServer)\n" +
                                        "Activity 调用 getWindow().superDispatchTouchEvent\n" +
                                        "PhoneWindow 调用 mDecor.superDispatchTouchEvent\n" +
                                        "ViewGroup 调用 dispatchTouchEvent\n\n" +

                                        "dispatchTouchEvent(如果拦截就给自己onTouchEvent处理，否则如果子View消费就交给子View，否则自己处理)\n" +
                                        "onInterceptTouchEvent(true:拦截,交由本层onTouchEvent,false:不拦截，子View的dispatchTouchEvent，super.onInterceptTouchEvent(ev):通true)\n" +
                                        "onTouchEvent()\n" +
                                        "requestDisallowInterceptTouchEvent"
                        );
                        break;
                    }
                    case "Window,DecorView,WindowManager,ViewRootImpl关系": {
                        showMyDialog(moduleEntity.getName(),
                                "Window：通过控制DecorView提供了一些标准的UI方案，比如背景、标题、虚拟按键等；唯一实现类PhoneWindow；在Activity创建后的attach流程中创建\n" +
                                        "DecorView：界面布局View控件树的根节点\n" +
                                        "WindowManager：一个接口，继承自ViewManager接口，WindowManagerImp实现了WindowManager接口，内部持有WindowManagerGlobal单例，用来操作View\n" +
                                        "ViewRootImpl：所有View的Parent，用来总体管理View的绘制以及与系统WMS窗口管理服务的IPC交互从而实现窗口的开辟\n\n"
                        );
                        break;
                    }
                    case "事件分发": {
                        showMyDialog(moduleEntity.getName(),
                                "一.屏幕到App\n" +
                                        "触摸屏会按照屏幕硬件的触控采样率周期，每隔几毫秒扫描一次，如果有触控事件就会上报到对应的设备驱动\n" +
                                        "硬件驱动被触发，包装Event存到/dev/input/event[x]目录\n" +
                                        "InputManagerService启动InputReader线程从/dev/input/目录拿任务,分发给InputDispatcher线程请求派发消息\n" +
                                        "App中 Window与IMS通信通过 InputChannel(pipe，底层通过socket)，ViewRootImpl.setView中注册InputChannel\n" +
                                        "当收到输入事件消息后，回调NativeInputEventReceiver.handleEvent，最终到InputEventReceiver。dispatchInputEvent\n" +
                                        "App拿到输入事件\n\n" +

                                        "二.App到页面\n" +
                                        "ViewRootImpl.dispatchInputEvent处理事件的传递\n" +
                                        "这个阶段对事件简单分类，视图输入事件，输入法事件，导航面板事件等等\n" +
                                        "经过层层调用到mView(DecorView).dispatchPointerEvent\n" +
                                        "DecorView->Activity->PhoneWindow->DecorView:为了解耦，ViewRootImpl不知道Activity,Activity不知道DecorView\n" +
                                        "最终到ViewGroup.dispatchTouchEvent\n\n" +

                                        "二.页面内部分发\n" +
                                        "只有Down事件或mFirstTouchTarget不为空时才判断是否拦截，判断是否拦截前先判断是否内部拦截disAllowIntercept\n" +
                                        "如果是ViewGroup，先判断是否拦截，如果拦截执行父类View的dispatchTouchEvent，最终到ViewGroup.onTouchEvent\n" +
                                        "如果不拦截，遍历子View是否消费\n" +
                                        "子View设置了onTouchListener且返回true，那么onTouchEvent不再执行，否则执行onTouchEvent，onClickListener就是在onTouchEvent里触发\n" +
                                        "子View消费事件，事件结束，ViewGroup.dispatchTouchEvent也返回true，Activity.dispatchTouchEvent也返回true\n" +
                                        "子View不消费事件，调用super.dispatchTouchEvent，之后ViewGroup和子View逻辑一样onTouchEvent\n" +
                                        "如果ViewGroup和子View都不拦截，dispatchTouchEvent返回false，执行Activity.onTouchEvent\n" +
                                        "后续事件不会走子View的循环判断，因为已经找到目标View，直接分发\n" +
                                        "责任链模式\n\n" +

                                        "cancel:子View处理了Down事件，那么随之而来的Move和Up事件也会交给它处理。但是交给它处理之前，父View还是可以拦截事件的，如果拦截事件，那么子View就会收到一个Cancel事件，并且不会收到后续的Move和Up事件\n" +
                                        "DOWN事件初始化(APP切换) 子View处理事件时被移除 子View被设置CANCEL_NEXT_UP标记时，也会触发cancel\n" +
                                        "滑出子View区域不会触发cancel，也不会触发onClick(PrivateFlag只有PRESSED状态才会触发onClick)\n\n" +

                                        "滑动冲突：\n" +
                                        "外部拦截:父View的onInterceptTouchEvent(down和up不能拦截，否则收不到后续事件或不执行onClick，子View收到cancel)\n" +
                                        "内部拦截:requestDisallowInterceptTouchEvent,父onInterceptTouchEvent(down不拦截,否则子View收不到任何事件)\n" +
                                        "方向不一致:根据滑动方向判断\n" +
                                        "方向一致:根据业务逻辑判断\n\n" +

                                        "View 扩大点击区域：设置View.setTouchDelegate,TouchDelegate(Rect, View)"
                        );
                        break;
                    }
                    case "协程": {
                        showMyDialog(moduleEntity.getName(),
                                "两种方式来启动协程：\n" +
                                        "launch：可以启动新协程，但是不将结果返回给调用方\n" +
                                        "async：可以启动新协程，并且允许使用await暂停函数返回结果。返回的是Deferred接口，继承Job\n\n" +
                                        "三种方式创建协程：\n" +
                                        "runBlocking{}线程阻塞的，适用于单元测试，一般业务开发不会使用这种\n" +
                                        "GlobalScope.launch{}，不会阻塞线程，不推荐生命周期同application，不能取消,容易内存泄漏\n" +
                                        "CoroutineScope(Dispatchers.IO).launch {}"
                        );
                        break;
                    }
                    case "Kotlin内联函数": {
                        showMyDialog(moduleEntity.getName(),
                                "object.let{}\n" +
                                        "it指代当前对象，返回值为函数块的最后一行或指定return表达式\n" +
                                        "适用于处理不为null的操作场景\n\n" +

                                        "with(object){}\n" +
                                        "this指代当前对象或者省略,返回值为函数块的最后一行或指定return表达式。\n" +
                                        "适用于调用同一个类的多个方法时，省去类名直接调用类的方法\n\n" +

                                        "object.run{}\n" +
                                        "this指代当前对象或者省略,返回值为函数块的最后一行或指定return表达式\n" +
                                        "适用于let,with函数任何场景，是let,with的结合\n\n" +

                                        "object.apply{}\n" +
                                        "this指代当前对象或者省略,返回的是传入对象的本身\n" +
                                        "适用于对象实例初始化的时候，需要对对象中的属性进行赋值 或 多层级判空\n\n" +

                                        "object.also{}\n" +
                                        "it指代当前对象，返回的是传入对象的本身\n" +
                                        "适用于let函数的任何场景，可用于多个扩展函数链式调用\n\n"
                        );
                        break;
                    }
                    case "RecyclerView相关": {
                        showMyDialog(moduleEntity.getName(),
                                "1级缓存：mAttachedScrap 和 mChangedScrap，缓存还在屏幕内的 ViewHolder\n" +
                                        "2级缓存：mCachedViews，缓存移除屏幕之外的 ViewHolder，默认缓存容量2，可通过 setViewCacheSize 改变缓存容量大小\n" +
                                        "3级缓存：开发给用户的自定义扩展缓存，需要用户自己管理 View 的创建和缓存//很少用\n" +
                                        "4级缓存：RecycledViewPool，ViewHolder 缓存池，在有限的2级缓存中如果存不下新的 ViewHolder 时，就会把 ViewHolder 存入RecyclerViewPool 中。\n" +
                                        "4级缓存：①按照 Type 来查找 ViewHolder ②每个 Type 默认最多缓存 5 个③可以多个 RecyclerView 共享 RecycledViewPool\n\n" +

                                        "复用：Recycler.onLayout...LinearLayoutManager.fill填充View...Recycler.tryGetViewHolderForPositionByDeadline\n" +
                                        "tryGetViewHolderForPositionByDeadline方法中一级一级查缓存，查不到最后调用createViewHolder\n\n" +

                                        "存入缓存：Adapter.notifyXXX会回调LayoutManager.onLayoutChildren{将屏幕上所有的 ViewHolder 回收到mAttachedScrap 和 mChangedScrap}\n" +
                                        "2,4级缓存触发时机：重新布局,不满足一级缓存(比如ViewType不同等,就会从一级缓存里面移除这个 ViewHolder，添加到这两级缓存里面)\n" +
                                        "如果设置 Adapter.setHasStableIds(ture) 以及其它相关需要的实现，则可以提高效率（使用一级缓存）\n\n" +

                                        "预取PreFetch：可设置预取数量.原理是在RV开始滚动操作的时候启动一个Runnable(GapWorker),跟踪每个view type创建和邦定的平均时间，预测未来创建和邦定的所需时间\n\n"+

                                        "优化：\n" +
                                        "数据处理逻辑-异步\n" +
                                        "分页，新增删除 DiffUtil 差量刷新\n" +
                                        "减少布局层级，不推荐ConstraintLayout\n" +
                                        "xml inflate耗时IO，所以如果Item复用率低 Type多，代码去生成布局\n" +
                                        "多 ViewType 能够共用的部分尽量设计成自定义 View，减少 View 的构造和嵌套\n" +
                                        "高度固定使用setHasFixedSize(true)，避免 requestLayout 浪费资源\n" +
                                        "不要求动画可以关闭\n" +
                                        "setItemViewCacheSize加大缓存 空间换时间\n" +
                                        "多个 RecycledView 共用 Adapter，setRecycledViewPool(pool)共用缓存池"
                                //https://blog.csdn.net/c10WTiybQ1Ye3/article/details/107193802
                                //https://blankj.com/2018/09/29/optimize-recycler-view/
                        );
                        break;
                    }
                    case "SharedPreference MMKV": {
                        showMyDialog(moduleEntity.getName(),
                                "SP：效率低，I/O读写，因为数据格式xml，所以写入方式全量更新，commit同步可能导致ANR(当数据量比较大时，apply也会造成ANR),getXXX也会导致ANR\n\n" +

                                        "MMKV：基于 mmap 内存映射提供一段可供随时写入的内存块，App 只管往里面写数据，由操作系统负责将内存回写到文件，不必担心 crash 导致数据丢失\n" +
                                        "数据序列化方面选用 protobuf 协议，pb 在性能和空间占用上都有不错的表现\n" +
                                        "AES加密，支持多进程，增量更新，性能高\n" +
                                        "稳定性强,即使进程意外死亡,也能够通过 Linux 内核的保护机制, 将进行了文件映射的内存数据刷入到文件中, 提升了数据写入的可靠性"

                                , "SP", "效率低", "I/O读写", "全量更新", "commit同步可能导致ANR", "MMKV", "mmap", "AES加密，支持多进程，增量更新，性能高", "稳定性强");
                        break;
                    }
                    case "Android JVM ART": {
                        showMyDialog(moduleEntity.getName(),
                                "JVM是一种规范,对于汇编的语言规范和处理\n" +
                                        "java文件 -> 编译为class字节码文件 -> CPU能识别的机器码\n\n" +

                                        "Hotspot：应用于java的虚拟机\n" +
                                        "Dalvik：没有遵循JVM规范，相当于定制化JVM\n" +
                                        "Art：Android5.0后替换Dalvik\n\n" +

                                        "栈管运行，堆管存储\n" +
                                        "堆：存储new来的对象，不存基本类型和对象引用,线程共享区域(不安全),OOM\n" +
                                        "新生代(Eden + Survivor1,2(Android合二为一)) + 老年代(2/3)\n" +
                                        "初次被创建的对象存放在Eden区\n" +
                                        "第一次触发GC，Eden区存活的对象被转移到Survivor区的某一块区域\n" +
                                        "再次触发GC，Eden区的对象连同一块Survivor区的对象一起，被转移到了另一块Survivor区\n" +
                                        "创建Eden存不下的大对象 或 EdenGC后仍然存在大量存活对象,没办法放入另外一块Survivor,直接转移到老年代\n" +
                                        "可设置参数调节各区比例 和 进入老年代区域的年龄\n" +
                                        "年轻代GC用 复制清除 算法，速度快且没有内存碎片;老年代GC用 标记清除(碎片多) 或 标记整理 算法，如果申请大对象，可能OOM,默认被移动到老年代的年龄为15,Android年龄为6;对于一些占用较大内存的对象，会被直接送入老年代\n" +
//                                        https://zhuanlan.zhihu.com/p/69136675 //TODO
                                        "逃逸分析：线程逃逸->同步消除，方法逃逸->标量替换，栈上分配\n" +
                                        "回收算法：标记清除(不连续),复制清除(一半闲置),标记整理,分代收集法\n\n" +

                                        "栈：方法调用和执行的空间，每个方法会封装成一个栈帧压入栈中\n" +
                                        "JVM实际只有一个栈;线程私有,生命周期与线程相同。\n" +
                                        "栈帧:存储局部变量表(存储一系列的变量信息)、操作栈(用于计算)、动态链接(提供栈里面的对象在进行实例化的时候，能够查找到堆里面相应的类地址，并进行引用 ?多态?)、返回地址(子方法执行完毕之后，需要回到主方法的原有位置继续执行程序)等信息\n" +
                                        "StackOverflowError,OOM(动态扩栈到无法申请到足够的内存)\n\n" +

                                        "本地方法栈：虚拟机栈执行java方法，本地方法栈执行Native方法\n\n" +

                                        "方法区：线程共享区域(不安全)，存储已被虚拟机加载的类信息、常量、静态变量、即时编译器编译后的代码等数据,可OOM\n\n" +

                                        "程序计数器：保存线程的执行状态,记录节码指令地址行号(Native方法计数器值为空),主要处理Java多线程的问题\n\n" +

                                        "ART：\n" +
                                        "ART内存除堆有所不同外，其他基本与JVM规范类似\n" +
                                        "应用安装的时候,dex中的字节码将被编译成本地机器码，最终得到一个ELF格式的oat文件。\n" +
                                        "之后每次打开应用，执行的都是本地机器码。去除了运行时的解释执行，效率更高，启动更快"
//                                https://blog.csdn.net/u010212533/article/details/102988560
                        );
                        break;
                    }
                    case "搭建框架架构": {
                        showMyDialog(moduleEntity.getName(),
                                "目的：快速开发，性能稳定，降低后期维护成本\n\n" +

                                        "架构设计可大致分三层：\n" +
                                        "上层(视图层)：Activity、Fragment等视图和逻辑调用\n" +
                                        "中层(逻辑层)：业务的三方库以及主要逻辑实现，业务流程在这完成\n" +
                                        "底层：业务无关的框架库(哪个项目都可以用)\n" +
                                        "common模块：基础功能提供，如网络封装，图片框架封装，数据库Room，EventBus，公用util等等\n" +
                                        "Gradle配置统一管理，依赖库版本，APP版本号、插件版本、编译相关版本管理等\n" +
                                        "基类封装：BaseActivity，BaseFragment，BaseViewModel，公用控件封装\n"
                        );
                        break;
                    }
                    case "SDK开发": {
                        showMyDialog(moduleEntity.getName(),
                                "内容封闭，只开放少数接口,最大程度减少sdk接入方需要了解的细节,代码尽量无入侵\n" +
                                        "包尽可能小，尽可能压缩图片和动态链接库\n" +
                                        "统一接口调用方式\n" +
                                        "尽量一个module，多Module无法打包为一个aar(PS:jar包不包含资源文件，一般为纯代码。aar包中含有诸如图片之类的资源文件)\n" +
                                        "初始化时机：轻量级可选择ContentProvider.onCreate\n" +
                                        "resourcePrefix资源前缀(校验xml,values文件夹中name前缀,二进制文件)\n" +
                                        "图片资源前缀，需要abortOnError设置为true，暴漏lint检测.使用baseline，仅新增问题时暴露出来\n" +
                                        "混淆问题\n" +
                                        "最好出个详细文档和demo工程"
                        );
                        break;
                    }
                }
            }
        });
    }

    private void initOther() {
        RecyclerView recyclerView_java = findViewById(R.id.recyclerView_other);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this, FlexDirection.ROW, FlexWrap.WRAP);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);  //默认
        layoutManager.setAlignItems(AlignItems.STRETCH);  //默认
        recyclerView_java.setLayoutManager(layoutManager);

        InterviewAdapter otherAdapter = new InterviewAdapter(null);
        recyclerView_java.setAdapter(otherAdapter);

        otherAdapter.setNewData(CommonUtil.asList(
                new ModuleEntity("敏捷开发"),
                new ModuleEntity("为何换公司"),
                new ModuleEntity("工作中遇到的技术难题"),
                new ModuleEntity("项目亮点难点"),
                new ModuleEntity("人工智能"),
                new ModuleEntity("管理")
        ));

        otherAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ModuleEntity moduleEntity = otherAdapter.getItem(position);
                if (moduleEntity == null) return;
                if (moduleEntity.getClazz() != null) {
                    startActivity(new Intent(InterviewActivity.this, moduleEntity.getClazz()));
                    return;
                }
                switch (moduleEntity.getName()) {
                    case "敏捷开发": {
                        showMyDialog(moduleEntity.getName(),
                                "1.管好你的需求\n" +
                                        "2.使用统一的工具\n" +
                                        "3.可视化"
                        );
                        break;
                    }
                    case "为何换公司": {
                        showMyDialog(moduleEntity.getName(),
                                "1.产品经理提什么需求就实现什么需求，SDK拼接者\n" +
                                        "2.项目没有太多性能要求，无法提升\n" +
                                        "3.想要技术积累成长"
                        );
                        break;
                    }
                    case "工作中遇到的技术难题": {
                        showMyDialog(moduleEntity.getName(),
                                "1.RecyclerView嵌套，Item比较多，共用缓存池\n\n" +

                                        "2.粘性头部搭配XRecyclerView，重写Sticky源码\n\n" +

                                        "2.电视大屏要求常亮，尝试各种方式不行。解决：设置N秒后熄灭，N为最大值\n\n" +

                                        "3.取件列表：进入页面串行三个接口(智能路径规划，任务回执等，弱网),TAB1支付成功后会移到TAB3\n" +
                                        "如果支付完，还未更新到TAB3，此时服务端数据更新到本地，这个单子就被清除了，导致无法移到TAB3\n" +
                                        "第一版解决，用数据库更新一条不存在的数据，查询时会返回存在，APP重启后就没了，因为数据库缓存\n" +
                                        "解决：支付完成到更新数据到TAB3加同步，为了万无一失，TAB3还可以刷新"
                        );
                        break;
                    }
                    case "项目亮点难点": {
                        showMyDialog(moduleEntity.getName(),
                                "1.架构层面\n\n" +
                                        "2.弱网环境如何优化\n\n"
                        );
                        break;
                    }
                    case "人工智能": {
                        showMyDialog(moduleEntity.getName(),
                                "人工智能 包含 机器学习 包含 深度学习\n\n" +

                                        "神经网络就是通过对参数的合理设置来解决分类或者回归问题的\n" +
                                        "1.提取特征向量(作为输入)\n" +
                                        "2.前向传播(定义结构)\n" +
                                        "3.训练数据调整参数(反向传播算法最常用)\n" +
                                        "4.预测未知数据\n\n" +

                                        "多层+非线性解决异或问题\n" +
                                        "自定义损失函数\n\n" +

                                        "神经网络优化：梯度下降，学习率(指数衰减)，过拟合(正则化)\n" +
                                        "参数增多除了导致计算速度减慢,还很容易导致过拟合问题 卷积神经网络可以解决\n\n" +

                                        "全连接神经网络：每相邻两层之间的节点都有边相连\n" +
                                        "卷积神经网络：相邻两层之间只有部分节点相连\n" +
                                        "损失函数以及参数的优化过程也都适用于卷积神经网络，唯一区别就在于相邻两层的连接方式\n\n" +

                                        "卷积神经网络结构：1.输入层 2.卷积层 3.池化层 4.全连接层 5.Softmax(主要用于分类问题)"
                        );
                        break;
                    }
                    case "管理": {
                        showMyDialog(moduleEntity.getName(),
                                "团队贡献者\n\n" +

                                        "制定工作计划，人员选拔分工，资源协调，团队技术培训\n\n"
                        );
                        break;
                    }
                }
            }
        });
    }


    private void showMyDialog(CharSequence title, CharSequence subtitle, String... keys) {
        CommonDialog commonDialog = new CommonDialog(this);
        commonDialog.show();
        commonDialog.setTitle(title);
        commonDialog.setSubtitle(StringDealUtil.highlightKeyword(Color.RED, subtitle, keys));
        commonDialog.setBtnText(null, "确定");
        commonDialog.setOnBtnRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commonDialog.dismiss();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initDragAndDrop() {
        DragStartHelper dragStartHelper = new DragStartHelper(headerLayout.findViewById(com.yuyang.lib_base.R.id.view_header_titleText), new DragStartHelper.OnDragStartListener() {
            @Override
            public boolean onDragStart(View view, DragStartHelper helper) {
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view) {
                    public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
                        super.onProvideShadowMetrics(shadowSize, shadowTouchPoint);
                        helper.getTouchPosition(shadowTouchPoint);
                    }
                };
                ClipData clipData = new ClipData(null,
                        new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN},
                        new ClipData.Item("newPlainText"));
                view.startDragAndDrop(
                        clipData,
                        shadowBuilder,
                        null,
                        View.DRAG_FLAG_OPAQUE);
                return true;
            }
        });
        dragStartHelper.attach();
//        dragStartHelper.detach();

        androidAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                ModuleEntity moduleEntity = androidAdapter.getItem(position);

                /**
                 *  https://cloud.tencent.com/developer/article/1928022
                 *  DRAG_FLAG_GLOBAL  表示可以跨window拖拽，典型的是分屏状态下的拖拽
                 *  DRAG_FLAG_GLOBAL_PERSISTABLE_URI_PERMISSION  通常跟DRAG_FLAG_GLOBAL_URI_READ和DRAG_FLAG_GLOBAL_URI_WRITE配合使用，用来在设备重启时保持权限。直到显示地调用Context.revokeUriPermission（这里我也没懂什么意思）
                 *  DRAG_FLAG_GLOBAL_PREFIX_URI_PERMISSION  通常跟DRAG_FLAG_GLOBAL_URI_READ和DRAG_FLAG_GLOBAL_URI_WRITE配合使用，the URI permission grant applies to any URI that is a prefix match against the original granted URI。
                 *  DRAG_FLAG_GLOBAL_URI_READ  与DRAG_FLAG_GLOBAL一起使用，接收者将能够请求对包含在ClipData对象中的内容URI的读访问。
                 *  DRAG_FLAG_GLOBAL_URI_WRITE  与DRAG_FLAG_GLOBAL一起使用，接收者将能够请求对包含在ClipData对象中的内容URI的写访问。
                 *  DRAG_FLAG_OPAQUE 使拖动的阴影不透明。
                 */
                view.startDragAndDrop(
                        ClipData.newPlainText("Label", moduleEntity.getName()),
                        new View.DragShadowBuilder(view),
                        null,
                        View.DRAG_FLAG_GLOBAL);

                view.performHapticFeedback(
                        HapticFeedbackConstants.LONG_PRESS,
                        HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                );

                return true;
            }
        });

        //MinSdk24 Android 7.0
//        DropHelper.configureView(this,
//                findViewById(R.id.tvDropTarget),
//                new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN},
//                new DropHelper.Options.Builder()
////                        .addInnerEditTexts(null)
//                        .build(),
//                new OnReceiveContentListener() {
//                    @Nullable
//                    @Override
//                    public ContentInfoCompat onReceiveContent(@NonNull View view, @NonNull ContentInfoCompat payload) {
//                        ToastUtil.showToast(payload.getClip().getItemAt(0).getText());
//                        return null;
//                    }
//                });

    }
}
