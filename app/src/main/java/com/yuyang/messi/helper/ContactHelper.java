package com.yuyang.messi.helper;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.yuyang.messi.MessiApp;
import com.yuyang.messi.bean.ContactBean;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ContactHelper {

    public interface ContactResultCallback {
        void onResultCallback(List<ContactBean> contactBeanList);
    }

    public static void loadContact(AppCompatActivity activity, ContactResultCallback resultCallback) {
        LoaderManager.getInstance(activity).initLoader(3, null, new ContactLoaderCallbacks(activity, resultCallback));
    }

    static class ContactLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

        private final WeakReference<Context> context;
        private final ContactResultCallback resultCallback;

        ContactLoaderCallbacks(Context context, ContactResultCallback resultCallback) {
            this.context = new WeakReference<>(context);
            this.resultCallback = resultCallback;
        }

        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new ContactLoader(context.get());
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
            if (cursor == null) return;

            List<ContactBean> contactBeanList = new ArrayList<>();

            int idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            int sortKeyIndex = cursor.getColumnIndex(ContactsContract.Contacts.SORT_KEY_PRIMARY);
            int photoIdIndex = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID);
            int lookUpKeyIndex = cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY);
            int mimetypeIndex = cursor.getColumnIndex(ContactsContract.Data.MIMETYPE);

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
//                getContactDetailInfo(cursor.getString(mimetypeIndex), cursor);

                long contactId = cursor.getLong(idIndex);

                ContactBean contactBean = new ContactBean();
                contactBean.setContactId(contactId);
                contactBean.setDisplayName(cursor.getString(nameIndex));
                contactBean.setSortKey(cursor.getString(sortKeyIndex));
                contactBean.setLookUpKey(cursor.getString(lookUpKeyIndex));

                long photoId = cursor.getLong(photoIdIndex);//是否有头像
                if (photoId > 0) {
                    contactBean.setPhotoUri(ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId));
                }

                contactBeanList.add(contactBean);
            }
            if (resultCallback != null) {
                resultCallback.onResultCallback(contactBeanList);
            }
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        }
    }

    private static class ContactLoader extends CursorLoader {

        private ContactLoader(Context context) {
            super(context);

            setUri(ContactsContract.Contacts.CONTENT_URI);//联系人信息
//            setUri(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);//手机号码信息
            setSortOrder(ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED asc");
        }
    }

    private static void getContactDetailInfo(String mimetype, Cursor cursor) {
        switch (mimetype) {
            case ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE: {    // 获得通讯录中联系人的名字
                String display_name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME));
                String prefix = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.PREFIX));
                String firstName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
                String middleName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME));
                String lastname = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
                String suffix = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.SUFFIX));
                String phoneticFirstName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.PHONETIC_FAMILY_NAME));
                String phoneticMiddleName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.PHONETIC_MIDDLE_NAME));
                String phoneticLastName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.PHONETIC_GIVEN_NAME));
                break;
            }
            case ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE: {     // 获取电话信息
                int phoneType = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                switch (phoneType) {
                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE: {   // 手机
                        String mobile = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        break;
                    }
                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME: {    // 住宅电话
                        String homeNum = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK: {    // 单位电话
                        String jobNum = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK: {    // 单位传真
                        String workFax = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME: {     // 住宅传真
                        String homeFax = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    case ContactsContract.CommonDataKinds.Phone.TYPE_PAGER: {    // 寻呼机
                        String pager = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    case ContactsContract.CommonDataKinds.Phone.TYPE_CALLBACK: {// 回拨号码
                        String quickNum = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    case ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN: { // 公司总机
                        String jobTel = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    case ContactsContract.CommonDataKinds.Phone.TYPE_CAR: {  // 车载电话
                        String carNum = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    case ContactsContract.CommonDataKinds.Phone.TYPE_ISDN: {    // ISDN
                        String isdn = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    case ContactsContract.CommonDataKinds.Phone.TYPE_MAIN: {     // 总机
                        String tel = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    case ContactsContract.CommonDataKinds.Phone.TYPE_RADIO: {    // 无线装置
                        String wirelessDev = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    case ContactsContract.CommonDataKinds.Phone.TYPE_TELEX: {   // 电报
                        String telegram = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    case ContactsContract.CommonDataKinds.Phone.TYPE_TTY_TDD: {      // TTY_TDD
                        String tty_tdd = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE: {     // 单位手机
                        String jobMobile = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_PAGER: {  // 单位寻呼机
                        String jobPager = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    case ContactsContract.CommonDataKinds.Phone.TYPE_ASSISTANT: {   // 助理
                        String assistantNum = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    case ContactsContract.CommonDataKinds.Phone.TYPE_MMS: {     // 彩信
                        String mms = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                }
                break;
            }
            case ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE: {    // 查找email地址
                int emailType = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                switch (emailType) {
                    case ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM: {    // 邮件地址
                        String customEmail = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        break;
                    }
                    case ContactsContract.CommonDataKinds.Email.TYPE_HOME: {    // 住宅邮件地址
                        String homeEmail = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        break;
                    }
                    case ContactsContract.CommonDataKinds.Email.TYPE_WORK: {    // 单位邮件地址
                        String workEmail = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        break;
                    }
                    case ContactsContract.CommonDataKinds.Email.TYPE_MOBILE: {  // 手机邮件地址
                        String mobileEmail = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        break;
                    }
                    case ContactsContract.CommonDataKinds.Email.TYPE_OTHER: {   // 其他邮件地址
                        String otherEmail = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        break;
                    }
                }


                break;
            }
            case ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE: {
                int eventType = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.TYPE));// 取出事件类型

                if (eventType == ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY) {    // 生日
                    String birthday = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE));
                }
                if (eventType == ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY) {     // 周年纪念日
                    String anniversary = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE));
                }
                break;
            }
            case ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE: {
                int protocal = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Im.PROTOCOL));// 即时消息类型
                if (ContactsContract.CommonDataKinds.Im.TYPE_CUSTOM == protocal) {
                    String workMsg = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));
                } else if (ContactsContract.CommonDataKinds.Im.PROTOCOL_MSN == protocal) {
                    String workMsg = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));
                } else if (ContactsContract.CommonDataKinds.Im.PROTOCOL_QQ == protocal) {
                    String instantsMsg = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));
                }
                break;
            }
            case ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE: {  //备注信息
                String remark = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
                break;
            }
            case ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE: {  //昵称
                String nickName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Nickname.NAME));
                break;
            }
            case ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE: {  //组织信息
                int orgType = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TYPE));
                // 单位
                if (orgType == ContactsContract.CommonDataKinds.Organization.TYPE_CUSTOM) {
                    //     if (orgType == Organization.TYPE_WORK) {
                    String company = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY));
                    String jobTitle = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
                    String department = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DEPARTMENT));
                }
                break;
            }
            case ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE: {   //网站信息
                int webType = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Website.TYPE));
                switch (webType) {
                    case ContactsContract.CommonDataKinds.Website.TYPE_CUSTOM: {   //主页
                        String home = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Website.URL));
                    }
                    case ContactsContract.CommonDataKinds.Website.TYPE_HOME: {   //主页
                        String home = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Website.URL));
                    }
                    case ContactsContract.CommonDataKinds.Website.TYPE_HOMEPAGE: {  // 个人主页
                        String homePage = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Website.URL));
                    }
                    case ContactsContract.CommonDataKinds.Website.TYPE_WORK: {  // 工作主页
                        String workPage = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Website.URL));
                    }
                }
                break;
            }
            case ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE: {  //通讯地址
                int postalType = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));

                String street = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                String ciry = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                String box = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX));
                String area = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.NEIGHBORHOOD));
                String state = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
                String zip = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
                String country = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
                if (postalType == ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK) {    //单位通讯地址
                } else if (postalType == ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME) {     // 住宅通讯地址
                } else if (postalType == ContactsContract.CommonDataKinds.StructuredPostal.TYPE_OTHER) {    // 其他通讯地址
                }
                break;
            }
        }
    }

    ///////////////////////////////////////////

    private static ContentResolver resolver;

    private static ContentResolver getContentResolver() {
        if (resolver == null)
            resolver = MessiApp.getInstance().getContentResolver();
        return resolver;
    }

    public static List<String> getAllPhoneNumbers(String lookupKey) {

        List<String> phoneList = new ArrayList<>();

        Cursor cur = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.Data.LOOKUP_KEY + "=?",
                new String[]{lookupKey},
                null);

        while (cur.moveToNext()) {
            int phoneIndex = cur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER);
            phoneList.add(cur.getString(phoneIndex));
        }

        cur.close();

        return phoneList;
    }

    public static List<String> getAllEmails(String lookupKey) {

        List<String> emailList = new ArrayList<>();

        Cursor cur = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Email.ADDRESS},
                ContactsContract.Data.LOOKUP_KEY + "=?",
                new String[]{lookupKey},
                null);

        while (cur.moveToNext()) {
            int phoneIndex = cur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.ADDRESS);
            emailList.add(cur.getString(phoneIndex));
        }

        cur.close();

        return emailList;
    }
}
