package com.yuyang.messi.ui.finance;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.lifecycle.Observer;

import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.messi.ui.finance.bean.FinanceData;

public class StockAnalysisActivity extends AppBaseActivity {

    private static final String KEY_GID = "key_gid";

    private StockAnalysisViewModel viewModel;

    public static void launchActivity(Context context, String gid) {
        Intent intent = new Intent(context, StockAnalysisActivity.class);
        intent.putExtra(KEY_GID, gid);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_finance_home;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

        viewModel.loadStockInfo();
    }

    private void initView() {

        viewModel = createViewModel(StockAnalysisViewModel.class);
        viewModel.setGid(getIntent().getStringExtra(KEY_GID));
        viewModel.getFinanceData().setValue(new FinanceData());
        viewModel.getFinanceData().postValue(new FinanceData());
        viewModel.getFinanceData().observe(this, new Observer<FinanceData>() {
            @Override
            public void onChanged(FinanceData financeData) {
                HeaderLayout headerLayout = findViewById(R.id.headerLayout);
                headerLayout.showTitle(financeData.getData().getName());
            }
        });
    }
}
