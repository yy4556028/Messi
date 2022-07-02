package com.yuyang.messi.ui.media;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.messi.R;
import com.yuyang.messi.adapter.SMSAdapter;
import com.yuyang.messi.bean.SMSBean;
import com.yuyang.messi.ui.base.AppBaseActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SmsActivity extends AppBaseActivity {

    //会话
    private static final String CONVERSATIONS = "content://sms/conversations/";
    //查询联系人
    private static final String CONTACTS_LOOKUP = "content://com.android.contacts/phone_lookup/";
    //全部短信
    private static final String SMS_ALL   = "content://sms/";
    //收件箱
//  private static final String SMS_INBOX = "content://sms/inbox";
    //已发送
//  private static final String SMS_SENT  = "content://sms/sent";
    //草稿箱
//  private static final String SMS_DRAFT = "content://sms/draft";

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    private ListView listView;
    private SMSAdapter adapter;

    private List<SMSBean> list;

    private MyAsyncQueryHandler myAsyncQueryHandler;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sms;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("SMS");

        listView = findViewById(R.id.activity_sms_list);
        startQuery();
    }

    private void startQuery() {

        myAsyncQueryHandler = new MyAsyncQueryHandler(getContentResolver());

        Uri uri = Uri.parse(CONVERSATIONS);
        String[] projection = new String[]{"groups.group_thread_id AS group_id", "groups.msg_count AS msg_count",
                "groups.group_date AS last_date", "sms.body AS last_msg", "sms.address AS contact"};

        //查询并按日期倒序
        myAsyncQueryHandler.startQuery(0, null, uri, projection, null, null, "groups.group_date DESC");
    }

    private class MyAsyncQueryHandler extends AsyncQueryHandler {

        public MyAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {

            Cursor mCursor = new CursorWrapper(cursor) {   //对Cursor进行处理,遇到号码后获取对应的联系人名称
                @Override
                public String getString(int columnIndex) {
                    if(super.getColumnIndex("contact") == columnIndex){
                        String contact = super.getString(columnIndex);
                        //读取联系人,查询对应的名称
                        Uri uri = Uri.parse(CONTACTS_LOOKUP + contact);
                        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                        if(cursor.moveToFirst()){
                            String contactName = cursor.getString(cursor.getColumnIndex("display_name"));
                            return contactName;
                        }
                        return contact;
                    }
                    return super.getString(columnIndex);
                }
            };

            if (mCursor != null && mCursor.getCount() > 0) {
                list = new ArrayList<>();

                int groupIdIndex = mCursor.getColumnIndexOrThrow("group_id");
                int msgCountIndex = mCursor.getColumnIndexOrThrow("msg_count");
                int lastMsgIndex = mCursor.getColumnIndexOrThrow("last_msg");
                int contactIndex = mCursor.getColumnIndexOrThrow("contact");
                int lastDateIndex = mCursor.getColumnIndexOrThrow("last_date");

                for (int i = 0; i < mCursor.getCount(); i++) {

                    mCursor.moveToPosition(i);

                    int groupId = mCursor.getInt(groupIdIndex);
                    long msgCount = mCursor.getLong(msgCountIndex);
                    String lastMsg = mCursor.getString(lastMsgIndex);
                    String contact = mCursor.getString(contactIndex);
                    long lastDate = mCursor.getLong(lastDateIndex);

                    SMSBean bean = new SMSBean();
                    bean.setGroupId(groupId + "");
                    bean.setMsgCount(msgCount + "");
                    bean.setLastMsg(lastMsg);
                    bean.setContact(contact);
                    bean.setLastDate(format.format(lastDate));
                    list.add(bean);
                }

            }

            if (list.size() > 0) {
                adapter = new SMSAdapter(getActivity(), list);
                listView.setAdapter(adapter);
            }

            cursor.close();
            mCursor.close();

            super.onQueryComplete(token, cookie, cursor);
        }
    }
}
