package com.yuyang.messi.ui.category.scroll;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;

import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.messi.R;
import com.yuyang.messi.adapter.AutoGrowListViewAdapter;
import com.yuyang.messi.view.scroll.AutoGrowListView;

import java.util.Arrays;

public class AutoGrowListViewActivity extends AppBaseActivity {

    private AutoGrowListView listView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_autogrowlistview;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("AutoGrowList");

        listView = findViewById(R.id.activity_autoGrowListView);

        String IMG_URLS = "http://img4.duitang.com/uploads/blog/201311/04/20131104193715_NCexN.thumb.jpeg,http://cdn.duitang.com/uploads/blog/201401/07/20140107223310_LH3Uy.thumb.jpeg,http://img5.duitang.com/uploads/item/201405/09/20140509222156_kVexJ.thumb.jpeg,http://img5.duitang.com/uploads/item/201306/14/20130614185903_raNR3.thumb.jpeg,http://img5.duitang.com/uploads/item/201112/11/20111211191621_HU4Bj.thumb.jpg,http://cdn.duitang.com/uploads/item/201408/07/20140807224553_VXaUc.thumb.jpeg,http://img5.duitang.com/uploads/blog/201407/29/20140729105929_GQLwC.thumb.jpeg,http://img4.duitang.com/uploads/blog/201408/04/20140804160432_LnFeB.thumb.jpeg,http://img5.duitang.com/uploads/blog/201408/04/20140804161101_JVJea.thumb.jpeg,http://cdn.duitang.com/uploads/blog/201408/04/20140804093210_FxFNd.thumb.jpeg,http://img5.duitang.com/uploads/blog/201408/04/20140804160314_hRKtv.thumb.jpeg,http://cdn.duitang.com/uploads/item/201408/01/20140801080524_SnGfE.thumb.jpeg,http://img5.duitang.com/uploads/item/201407/23/20140723140958_NSWfE.thumb.jpeg,http://img4.duitang.com/uploads/blog/201407/22/20140722153305_WHejQ.thumb.jpeg,http://img5.duitang.com/uploads/item/201407/21/20140721010148_ZBQwe.thumb.jpeg,http://cdn.duitang.com/uploads/item/201407/23/20140723085122_cmteu.thumb.jpeg,http://img5.duitang.com/uploads/item/201407/23/20140723130620_Z2yJB.thumb.jpeg,http://cdn.duitang.com/uploads/blog/201407/20/20140720204738_NXxLE.thumb.jpeg,http://cdn.duitang.com/uploads/item/201407/20/20140720134916_VGfyh.thumb.jpeg";

        addListHeader();
        addListFooters();

        listView.setBigItemHeight(600);
        listView.setSmallItemHeight(300);

        listView.setAdapter(new AutoGrowListViewAdapter(this, Arrays.asList(IMG_URLS.split(","))));

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            int glowDrawableId = getResources().getIdentifier("overscroll_glow", "drawable", "android");
            Drawable androidGlow = getResources().getDrawable(glowDrawableId);
            androidGlow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
        }
    }

    private void addListHeader() {
        View v = new View(this);
        v.setBackgroundColor(Color.BLUE);
        v.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1500));
        listView.addHeaderView(v);

        v = new View(this);
        v.setBackgroundColor(Color.RED);
        v.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1500));
        listView.addHeaderView(v);
    }

    // 模拟增加 footer
    private void addListFooters() {

        int currentFooterCount = listView.getFooterViewsCount();

        for (int i = 0; i < 4 - currentFooterCount - 1; i++) {
            View v = new View(this);
            v.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1000));
            if (i == 0) v.setBackgroundColor(Color.BLUE);
            if (i == 1) v.setBackgroundColor(Color.RED);
            if (i == 2) v.setBackgroundColor(Color.YELLOW);
            listView.addFooterView(v);
        }

    }

}
