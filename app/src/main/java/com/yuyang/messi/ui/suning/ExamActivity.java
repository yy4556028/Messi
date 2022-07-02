package com.yuyang.messi.ui.suning;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.yamap.lib_chat.keyboard.utils.KeyboardUtil;
import com.yuyang.lib_baidu.utils.BaiduRecognizeService;
import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.utils.CollectionUtil;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.bean.ImageBean;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.messi.ui.category.adapter.ExamRecyclerAdapter;
import com.yuyang.messi.ui.common.photo.PhotoGalleryActivity;
import com.yuyang.messi.ui.media.AlbumActivity;
import com.yuyang.messi.view.Progress.CustomProgressDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 考试
 */
public class ExamActivity extends AppBaseActivity {

    private EditText etSearch;
    private ImageView eraseView;
    private Button btnSearch;

    private Button btnSelectImg;
    private Button btnOcr;
    private TextView tvStatus;

    private ExamRecyclerAdapter recyclerAdapter;

    private CustomProgressDialog progressDialog;

    private boolean hasGotToken;

    private int currentIndex;
    private ArrayList<ImageBean> allImgList;
    private final ArrayList<ImageBean> unOcrList = new ArrayList<>();
    private final HashMap<ImageBean, String> resultMap = new HashMap<>();

    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (RESULT_OK != result.getResultCode()) return;
            allImgList = result.getData().getParcelableArrayListExtra(AlbumActivity.SELECTED_PHOTOS);
            recyclerAdapter.updateData(allImgList);

            updateStatusText();
        }
    });

    @Override
    protected int getLayoutId() {
        return R.layout.activity_exam;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAccessTokenWithAkSk();
        initView();
        initEvents();

        progressDialog = new CustomProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private void initAccessTokenWithAkSk() {
        OCR.getInstance(this).initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                String token = result.getAccessToken();
                hasGotToken = true;
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast("AK，SK方式获取token失败" + "\r\n" + error.getMessage());
                    }
                });
            }
        }, getApplicationContext(), "mBploLaojMSe02yq7pQvOB0O", "q4NBIyoxOM5MpBaxAoh9KFj5ntuWmGF2");
    }

    private void initView() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        headerLayout.showTitle("苏宁考试");

        etSearch = findViewById(R.id.activity_exam_etSearch);
        eraseView = findViewById(R.id.activity_exam_eraseView);
        btnSearch = findViewById(R.id.activity_exam_btnSearch);
        btnSelectImg = findViewById(R.id.activity_exam_btnSelectImg);
        btnOcr = findViewById(R.id.activity_exam_btnOcr);
        tvStatus = findViewById(R.id.activity_exam_tvStatus);
        RecyclerView recyclerView = findViewById(R.id.activity_exam_rvAll);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recyclerView.setAdapter(recyclerAdapter = new ExamRecyclerAdapter(null));
    }

    private void initEvents() {

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    final String query = etSearch.getText().toString();
                    KeyboardUtil.showHideIme(getActivity(), false);
                    if (!TextUtils.isEmpty(query)) {
//
                    }
                    return true;
                }
                return false;
            }
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                eraseView.setVisibility(TextUtils.isEmpty(s) ? View.GONE : View.VISIBLE);
                if (TextUtils.isEmpty(s) || resultMap.size() == 0) {
                    btnSearch.setEnabled(false);
                } else {
                    btnSearch.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        eraseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearch.setText(null);
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String searchKey = etSearch.getText().toString();
                if (TextUtils.isEmpty(searchKey)) {
                    ToastUtil.showToast("请输入要检索得文字");
                    return;
                }
                List<ImageBean> resultList = new ArrayList<>();
                for (Map.Entry<ImageBean, String> entry : resultMap.entrySet()) {
                    if (entry.getValue().contains(searchKey)) {
                        resultList.add(entry.getKey());
                    }
                }
                if (resultList.size() == 0) {
                    ToastUtil.showToast("未检索到结果，尝试输入好辨别的连续文字");
                } else {
                    PhotoGalleryActivity.launchActivity(getActivity(), resultList, 0);
                }
            }
        });
        btnSelectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.launch(AlbumActivity.getLaunchIntent(getActivity(), 1000, false, false, false, allImgList));
            }
        });
        btnOcr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasGotToken) {
                    ToastUtil.showToast("token还未成功获取");
                    return;
                }

                if (CollectionUtil.isEmpty(unOcrList)) {
                    ToastUtil.showToast("请先选择图片");
                    return;
                }

                currentIndex = 0;
                recGeneralBasic();
            }
        });
    }

    private void recGeneralBasic() {
        if (currentIndex >= unOcrList.size()) {
            progressDialog.dismiss();
            updateStatusText();
            if (TextUtils.isEmpty(etSearch.getText().toString()) || resultMap.size() == 0) {
                btnSearch.setEnabled(false);
            } else {
                btnSearch.setEnabled(true);
            }
            return;
        }
        progressDialog.show(String.format("正在识别 %s/%s", currentIndex + 1, unOcrList.size()));

        BaiduRecognizeService.recGeneralBasic(this, unOcrList.get(currentIndex).getPath(),
                new BaiduRecognizeService.ServiceListener() {
                    @Override
                    public void onResult(String result) {
                        resultMap.put(unOcrList.get(currentIndex), result);
                        currentIndex++;
                        recGeneralBasic();
                    }
                });
    }

    private void updateStatusText() {
        unOcrList.clear();
        List<ImageBean> resultList = new ArrayList<>(resultMap.keySet());
        for (ImageBean imageBean : allImgList) {
            if (!resultList.contains(imageBean)) {
                unOcrList.add(imageBean);
            }
        }
        tvStatus.setText(String.format("已识别：%s，未识别：%s", resultMap.size(), unOcrList.size()));
    }

    @Override
    public void onBackPressed() {
        if (resultMap.size() > 0) {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("退出将清空识别结果")
                    .setCancelable(true)
                    .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ExamActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .create()
                    .show();
            return;
        }
        super.onBackPressed();
    }
}

