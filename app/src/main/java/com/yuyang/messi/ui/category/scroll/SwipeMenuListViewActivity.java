package com.yuyang.messi.ui.category.scroll;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;
import com.yuyang.lib_base.net.common.RxUtils;
import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.utils.AppInfoUtil;
import com.yuyang.lib_base.utils.AppUtils;
import com.yuyang.lib_base.utils.PixelUtils;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.lib_base.utils.security.MD5Util;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.messi.ui.category.adapter.SwipeMenuListViewAdapter;
import com.yuyang.lib_base.utils.ClipboardUtil;
import com.yuyang.messi.utils.IntentUtil;
import com.yuyang.messi.utils.PinyinUtil;
import com.yuyang.messi.utils.TransitionUtil;
import com.yuyang.messi.view.Progress.CustomProgressDialog;
import com.yuyang.messi.view.scroll.swipemenulistview.SwipeMenu;
import com.yuyang.messi.view.scroll.swipemenulistview.SwipeMenuCreator;
import com.yuyang.messi.view.scroll.swipemenulistview.SwipeMenuItem;
import com.yuyang.messi.view.scroll.swipemenulistview.SwipeMenuListView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;

public class SwipeMenuListViewActivity extends AppBaseActivity {

    private SwipeMenuListView listView;
    private SwipeMenuListViewAdapter listAdapter;
    private CustomProgressDialog progressDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_swipe_menu_list_view;
    }

    @Override
    protected void initTransition() {
        super.initTransition();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TransitionUtil.setEnterTransition(getActivity(), TransitionUtil.slideEnd, 1500);
            TransitionUtil.setReturnTransition(getActivity(), TransitionUtil.slideEnd, 1500);

            TransitionUtil.setExitTransition(getActivity(), TransitionUtil.slideStart, 1500);
            TransitionUtil.setReenterTransition(getActivity(), TransitionUtil.slideStart, 1500);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initListView();
        initEvent();
        progressDialog = new CustomProgressDialog(this);
        progressDialog.show();

        Observable.create(new ObservableOnSubscribe<List<ApplicationInfo>>() {
            @Override
            public void subscribe(ObservableEmitter<List<ApplicationInfo>> emitter) throws Exception {
                List<ApplicationInfo> appList = AppInfoUtil.getAllAppInfoList();
                Collections.sort(appList, new Comparator<ApplicationInfo>() {
                    @Override
                    public int compare(ApplicationInfo o1, ApplicationInfo o2) {
                        String o1FullSpell = PinyinUtil.getFullSpell(o1.loadLabel(getPackageManager()).toString(), null);
                        String o2FullSpell = PinyinUtil.getFullSpell(o2.loadLabel(getPackageManager()).toString(), null);
                        if (o1FullSpell == null) return -1;
                        if (o2FullSpell == null) return 1;
                        return o1FullSpell.compareTo(o2FullSpell);
                    }
                });
                emitter.onNext(appList);
            }
        })
                .compose(RxUtils.io2main())
                .subscribe(new Consumer<List<ApplicationInfo>>() {
                    @Override
                    public void accept(List<ApplicationInfo> applicationInfos) throws Exception {
                        listAdapter.updateData(applicationInfos);
                        progressDialog.dismiss();
                    }
                });
    }

    private void initView() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("SwipeMenu");

        listView = findViewById(R.id.activity_swipe_menu_list);
    }

    private void initListView() {
        listAdapter = new SwipeMenuListViewAdapter(null);
        listView.setAdapter(listAdapter);
        listView.setCloseInterpolator(new BounceInterpolator());

        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

                // Create different menus depending on the view type
                switch (menu.getViewType()) {
                    case 0:
                        createMenu0(menu);
                        break;
                    case 1:
                        createMenu1(menu);
                        break;
                    case 2:
                        createMenu2(menu);
                        break;
                    case 3:
                        createMenu3(menu);
                        break;
                }
            }
        };

        // set creator
        listView.setMenuCreator(creator);
    }

    private void initEvent() {

        // step 2. listener item click event
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                                                @Override
                                                public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
//                                                    menu.getViewType();
                                                    final ApplicationInfo item = listAdapter.appList.get(position);
                                                    switch (index) {
                                                        case 0:
                                                            // open
                                                            Intent intent = AppUtils.getLaunchAppIntent(item.packageName, true);
                                                            if (intent == null) {
                                                                ToastUtil.showToast("不支持打开该应用");
                                                            } else {
                                                                startActivity(intent);
                                                            }
                                                            break;
                                                        case 1:
                                                            // delete
                                                            new AlertDialog.Builder(getActivity())
                                                                    .setTitle("提示")
                                                                    .setMessage("确定卸载该应用？")
                                                                    .setNegativeButton("取消", null)
                                                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                                            IntentUtil.uninstallApk(getActivity(), item.packageName);
                                                                        }
                                                                    })
                                                                    .setCancelable(true)
                                                                    .show();
                                                            break;
                                                    }
                                                    return false;
                                                }
                                            }

        );

        // set SwipeListener
        listView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

                                        @Override
                                        public void onSwipeStart(int position) {
                                            // swipe start
                                        }

                                        @Override
                                        public void onSwipeEnd(int position) {
                                            // swipe end
                                        }
                                    }

        );

        // set MenuStateChangeListener
        listView.setOnMenuStateChangeListener(new SwipeMenuListView.OnMenuStateChangeListener() {
                                                  @Override
                                                  public void onMenuOpen(int position) {
                                                  }

                                                  @Override
                                                  public void onMenuClose(int position) {
                                                  }
                                              }

        );

        // test item long click
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                                                @Override
                                                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                                                    ApplicationInfo item = listAdapter.appList.get(position);
                                                    Signature signature = AppInfoUtil.getAppSignature(item.packageName);
                                                    String signMd5 = MD5Util.md5(signature.toByteArray());

                                                    ClipboardUtil.setText(signMd5);
                                                    Snackbar.make(listView, "签名信息已复制到剪切板", Snackbar.LENGTH_SHORT).show();
                                                    return false;
                                                }
                                            }
        );
    }

    private void createMenu0(SwipeMenu menu) {

        SwipeMenuItem openItem = new SwipeMenuItem(getActivity()); // create "open" item
        openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE))); // set item background
        openItem.setWidth(PixelUtils.dp2px(90)); // set item width
        openItem.setTitle("Open"); // set item title
        openItem.setTitleSize(18); // set item title font size
        openItem.setTitleColor(Color.WHITE); // set item title font color
        menu.addMenuItem(openItem); // add to menu

        SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
        deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
        deleteItem.setWidth(PixelUtils.dp2px(90));
        deleteItem.setIcon(R.drawable.activity_swipe_menu_list_view_delete);
        menu.addMenuItem(deleteItem);
    }

    private void createMenu1(SwipeMenu menu) {

        SwipeMenuItem item1 = new SwipeMenuItem(getActivity());
        item1.setBackground(new ColorDrawable(Color.rgb(0xE5, 0x18, 0x5E)));
        item1.setWidth(PixelUtils.dp2px(90));
        item1.setIcon(R.drawable.activity_swipe_menu_list_view_favorite);
        menu.addMenuItem(item1);

        SwipeMenuItem item2 = new SwipeMenuItem(getActivity());
        item2.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
        item2.setWidth(PixelUtils.dp2px(90));
        item2.setIcon(R.drawable.activity_swipe_menu_list_view_good);
        menu.addMenuItem(item2);
    }

    private void createMenu2(SwipeMenu menu) {

        SwipeMenuItem item1 = new SwipeMenuItem(getActivity());
        item1.setBackground(new ColorDrawable(Color.rgb(0xE5, 0xE0, 0x3F)));
        item1.setWidth(PixelUtils.dp2px(90));
        item1.setIcon(R.drawable.activity_swipe_menu_list_view_important);
        menu.addMenuItem(item1);

        SwipeMenuItem item2 = new SwipeMenuItem(getActivity());
        item2.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
        item2.setWidth(PixelUtils.dp2px(90));
        item2.setIcon(R.drawable.activity_swipe_menu_list_view_discard);
        menu.addMenuItem(item2);
    }

    private void createMenu3(SwipeMenu menu) {

        SwipeMenuItem item1 = new SwipeMenuItem(getActivity());
        item1.setBackground(new ColorDrawable(Color.rgb(0x30, 0xB1, 0xF5)));
        item1.setWidth(PixelUtils.dp2px(90));
        item1.setIcon(R.drawable.activity_swipe_menu_list_view_about);
        menu.addMenuItem(item1);

        SwipeMenuItem item2 = new SwipeMenuItem(getActivity());
        item2.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
        item2.setWidth(PixelUtils.dp2px(90));
        item2.setIcon(R.drawable.activity_swipe_menu_list_view_share);
        menu.addMenuItem(item2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_swipe_menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_switch) {
            if (item.getTitle().toString().equalsIgnoreCase("left")) {
                listView.setSwipeDirection(SwipeMenuListView.DIRECTION_RIGHT);
                item.setTitle("right");
            } else if (item.getTitle().toString().equalsIgnoreCase("right")) {
                listView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
                item.setTitle("left");
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
