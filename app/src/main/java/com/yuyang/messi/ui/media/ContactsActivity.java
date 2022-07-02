package com.yuyang.messi.ui.media;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.yuyang.lib_base.bean.PopBean;
import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.ui.view.picker.BottomChooseDialog;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.bean.ContactBean;
import com.yuyang.messi.helper.ContactHelper;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.messi.ui.media.adapter.ContactRecyclerAdapter;
import com.yuyang.messi.utils.PinyinUtil;
import com.yuyang.messi.view.QuickAlphabeticBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ContactsActivity extends AppBaseActivity {

    private SearchView searchView;

    private QuickAlphabeticBar alphabetBar;

    private RecyclerView recyclerView;
    private ContactRecyclerAdapter recyclerAdapter;
    private LinearLayoutManager linearLayoutManager;

    private List<ContactBean> allBeanList;

    private final ActivityResultLauncher<String[]> permissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                List<String> deniedAskList = new ArrayList<>();
                List<String> deniedNoAskList = new ArrayList<>();
                for (Map.Entry<String, Boolean> stringBooleanEntry : result.entrySet()) {
                    if (!stringBooleanEntry.getValue()) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), stringBooleanEntry.getKey())) {
                            deniedAskList.add(stringBooleanEntry.getKey());
                        } else {
                            deniedNoAskList.add(stringBooleanEntry.getKey());
                        }
                    }
                }

                if (deniedAskList.size() == 0 && deniedNoAskList.size() == 0) {//全通过
                    loadContacts();
                } else if (deniedNoAskList.size() > 0) {
                    finish();
                } else {
                    finish();
                }
            });

    private final ActivityResultLauncher<String> callPhoneLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                }
            });

    @Override
    protected int getLayoutId() {
        return R.layout.activity_contacts;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initEvent();

        permissionsLauncher.launch(new String[]{Manifest.permission.READ_CONTACTS});
    }

    private void initView() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("联系人");

        searchView = findViewById(R.id.activity_contacts_search);
        int id = searchView.getContext()
                .getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        TextView textView = searchView.findViewById(id);
        textView.setTextColor(getResources().getColor(R.color.colorPrimary));
        textView.setHintTextColor(getResources().getColor(R.color.theme_alpha));

        recyclerView = findViewById(R.id.activity_contacts_recyclerView);
        recyclerView.setLayoutManager(linearLayoutManager = new LinearLayoutManager(getActivity()));

        recyclerAdapter = new ContactRecyclerAdapter(getActivity());

        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(recyclerAdapter);
        recyclerView.addItemDecoration(headersDecor);
        recyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                headersDecor.invalidateHeaders();
            }
        });
        recyclerView.setAdapter(recyclerAdapter);

        alphabetBar = findViewById(R.id.activity_contacts_alphabetBar);
        alphabetBar.setTextView((TextView) findViewById(R.id.activity_contacts_fastPositionText));
    }

    private void initEvent() {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ToastUtil.showToast(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterData(newText);
                return false;
            }
        });

        recyclerAdapter.setListener(new ContactRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ContactBean contactBean) {
            }

            @Override
            public void onItemPhoneClick(ContactBean contactBean) {
                List<PopBean> popBeanList = new ArrayList<>();
                for (String phoneNumber : ContactHelper.getAllPhoneNumbers(contactBean.getLookUpKey())) {
                    popBeanList.add(new PopBean(null, phoneNumber));
                }

                if (popBeanList.size() == 0) {
                    ToastUtil.showToast("没有电话信息");
                    return;
                }

                BottomChooseDialog.showSingle(getActivity(), "拨打电话", popBeanList, new BottomChooseDialog.SingleChoiceListener() {
                    @SuppressLint("CheckResult")
                    @Override
                    public void onItemClick(int index, final PopBean popBean) {
                        //"tel:" + phone + "," + fenPhone：是自动拨打分机号码
                        //"tel:" + phone + ";" + fenPhone：是需要用户确认，确认之后，自动拨打分机号码
                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + popBean.getName())));
                        } else {
                            callPhoneLauncher.launch(Manifest.permission.CALL_PHONE);
                        }
                    }
                });
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
                    return;
                }
                int position = linearLayoutManager.findFirstVisibleItemPosition();
                if (position >= recyclerAdapter.headerCount) {
                    alphabetBar.updateSection(recyclerAdapter.getSectionForPosition(position));
                }
            }
        });
        alphabetBar.setOnChangeListener(new QuickAlphabeticBar.OnChangeListener() {
            @Override
            public void onChange(String alphabet) {
                int position = recyclerAdapter.getPositionForSection(alphabet.charAt(0));
                if (position != -1) {
                    linearLayoutManager.scrollToPositionWithOffset(position, 0);
                }
            }
        });
    }

    private void loadContacts() {
        ContactHelper.loadContact(getActivity(), new ContactHelper.ContactResultCallback() {
            @Override
            public void onResultCallback(List<ContactBean> contactBeanList) {

                Set<String> sectionSet = new HashSet<>();

                for (ContactBean contactBean : contactBeanList) {
                    if (TextUtils.isEmpty(contactBean.getDisplayName())) {
                        continue;
                    }
                    String fullSpell = PinyinUtil.getFullSpell(contactBean.getDisplayName(), null);
                    if (TextUtils.isEmpty(fullSpell)) {
                        continue;
                    }
                    contactBean.setFullSpell(fullSpell);
                    String firstSpell = fullSpell.substring(0, 1).toUpperCase();
                    if (firstSpell.matches("[A-Z]")) {
                        contactBean.setFirstSpell(firstSpell);
                    } else {
                        contactBean.setFirstSpell("#");
                    }
                    sectionSet.add(contactBean.getFirstSpell());
                }

                Collections.sort(contactBeanList, new ContactsComparator());

                allBeanList = contactBeanList;

                recyclerAdapter.setData(allBeanList);
                alphabetBar.setSectionSet(sectionSet);
            }
        });
    }

    private void filterData(String filterStr) {

        Set<String> sectionSet = new HashSet<>();
        List<ContactBean> filterBeanList = new ArrayList<>();

        if (TextUtils.isEmpty(filterStr)) {
            filterBeanList = allBeanList;
        } else {
            for (ContactBean contactBean : allBeanList) {
                String name = contactBean.getDisplayName();
                if (name.toLowerCase().contains(filterStr.toLowerCase())
                        || contactBean.getFullSpell().toLowerCase().contains(filterStr.toLowerCase())) {
                    filterBeanList.add(contactBean);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterBeanList, new ContactsComparator());

        for (ContactBean contactBean : filterBeanList) {
            sectionSet.add(contactBean.getFirstSpell());
        }
        recyclerAdapter.setData(filterBeanList);
        alphabetBar.setSectionSet(sectionSet);
    }

    private static class ContactsComparator implements Comparator<ContactBean> {

        public int compare(ContactBean o1, ContactBean o2) {
//            if (TextUtils.equals(o1.getFirstSpell(), "#")
//                    && !TextUtils.equals(o2.getFirstSpell(), "#")) {
//                return 1;
//            } else if (!TextUtils.equals(o1.getFirstSpell(), "#")
//                    && TextUtils.equals(o2.getFirstSpell(), "#")) {
//                return -1;
//            } else {
//                return o1.getFullSpell().toUpperCase().compareTo(o2.getFullSpell().toUpperCase());
//            }
            if (TextUtils.equals(o1.getSortKey().substring(0, 1), "#")
                    && !TextUtils.equals(o2.getSortKey().substring(0, 1), "#")) {
                return 1;
            } else if (!TextUtils.equals(o1.getSortKey().substring(0, 1), "#")
                    && TextUtils.equals(o2.getSortKey().substring(0, 1), "#")) {
                return -1;
            } else {
                return o1.getSortKey().toUpperCase().compareTo(o2.getSortKey().toUpperCase());
            }
        }
    }
}
