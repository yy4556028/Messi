package com.yuyang.messi.ui.category.scroll;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.yuyang.lib_base.recyclerview.layout_manager.flow_drag.FlowDragLayoutConstant;
import com.yuyang.lib_base.recyclerview.layout_manager.flow_drag.FlowDragLayoutManager;
import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.messi.R;
import com.yuyang.messi.adapter.FlowDragRecyclerAdapter;
import com.yuyang.messi.recycler.DragItemTouchCallBack;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.messi.utils.SnackBarUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FlowDragActivity extends AppBaseActivity {

    private RecyclerView recyclerView;
    private FlowDragRecyclerAdapter adapter;
    private FlowDragLayoutManager layoutManager;

    private int lastClickPosition = 0;

    private List<String> essayList;
    private List<String> tagList;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_flow_drag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initEvent();
        initData();
        adapter.updateData(tagList, FlowDragRecyclerAdapter.ShowingType.TAGS);
    }

    private void initView() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("FlowDrag");

        recyclerView = findViewById(R.id.activity_flow_drag_recycler);
        recyclerView.setLayoutManager(layoutManager = new FlowDragLayoutManager());
        adapter = new FlowDragRecyclerAdapter(this);
        ItemTouchHelper.Callback callback = new DragItemTouchCallBack(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);
    }

    private void initEvent() {
        adapter.setOnFlowClickListener(new FlowDragRecyclerAdapter.OnFlowClickListener() {
            @Override
            public void onClick(int position) {
                SnackBarUtil.makeShort(recyclerView, adapter.dataList.get(position)).info();
                lastClickPosition = position;
            }
        });
    }

    private void initData() {
        String TEST_ESSAY = "Occasionally, Dad would get out his mandolin and play for the family. We three children: Trisha," +
                " Monte and I, George Jr., would often sing along. Songs such as the Tennessee Waltz, Harbor Lights and around Christmas time," +
                " the well-known rendition of Silver Bells. \"Silver Bells, Silver Bells, its Christmas time in the city\" would ring throughout the house." +
                " One of Dad's favorite hymns was \"The Old Rugged Cross\". We learned the words to the hymn when we were very young, and would sing it with Dad when he would play and sing." +
                " Another song that was often shared in our house was a song that accompanied the Walt Disney series: Davey Crockett." +
                " Dad only had to hear the song twice before he learned it well enough to play it. \"Davey, Davey Crockett, King of the Wild Frontier\" was a favorite song for the family." +
                " He knew we enjoyed the song and the program and would often get out the mandolin after the program was over. I could never get over how he could play the songs so well" +
                " after only hearing them a few times. I loved to sing, but I never learned how to play the mandolin. This is something I regret to this day.";

        String[] essayArray = TEST_ESSAY.split(" ");
        essayList = new ArrayList<>();
        Collections.addAll(essayList, essayArray);

        tagList = new ArrayList<>();
        tagList.add("RxJava");
        tagList.add("JavaScript");
        tagList.add("PHP");
        tagList.add("Python");
        tagList.add("??????");
        tagList.add("??????");
        tagList.add("????????????");
        tagList.add("????????????");
        tagList.add("????????????");
        tagList.add("???????????????");
        tagList.add("?????????????????????");
        tagList.add("Github");
        tagList.add("?????????");
        tagList.add("???????????????");
        tagList.add("????????????");
        tagList.add("????????????");
        tagList.add("???????????????");
        tagList.add("???????????????");
        tagList.add("????????????");
        tagList.add("ImageView");
        tagList.add("wheel????????????");
        tagList.add("ViewPager");
        tagList.add("????????????");
        tagList.add("????????????");
        tagList.add("dialog");
        tagList.add("????????????");
        tagList.add("??????");
        tagList.add("?????????????????????");
        tagList.add("video");
        tagList.add("??????ViewPager");
        tagList.add("??????");
        tagList.add("????????????");
        tagList.add("Retrofit");
        tagList.add("??????????????????");
        tagList.add("??????");
        tagList.add("????????????");
        tagList.add("EditText");
        tagList.add("????????????");
        tagList.add("???????????????");
        tagList.add("?????????????????????");
        tagList.add("????????????");
        tagList.add("????????????");
        tagList.add("???????????????");
        tagList.add("???????????????");
        tagList.add("?????????????????????");
        tagList.add("??????????????????");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_flow_drag, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.actionGravityLeft:{
                layoutManager.setAlignMode(FlowDragLayoutConstant.LEFT);
                break;
            }
            case R.id.actionGravityCenter:{
                layoutManager.setAlignMode(FlowDragLayoutConstant.CENTER);
                break;
            }
            case R.id.actionGravityRight:{
                layoutManager.setAlignMode(FlowDragLayoutConstant.RIGHT);
                break;
            }
            case R.id.actionGravitySide:{
                layoutManager.setAlignMode(FlowDragLayoutConstant.TWO_SIDE);
                break;
            }
            case R.id.actionShowTags:{
                adapter.updateData(tagList, FlowDragRecyclerAdapter.ShowingType.TAGS);
                lastClickPosition = 0;
                break;
            }
            case R.id.actionShowEssay:{
                adapter.updateData(essayList, FlowDragRecyclerAdapter.ShowingType.ESSAY);
                lastClickPosition = 0;
                break;
            }
            case R.id.actionAddItem:{
                adapter.dataList.add(lastClickPosition, "??????");
                adapter.notifyItemInserted(lastClickPosition);
                break;
            }
            case R.id.actionRemoveItem:{
                adapter.dataList.remove(lastClickPosition);
                adapter.notifyItemRemoved(lastClickPosition);
                break;
            }
        }
        return true;
    }
}
