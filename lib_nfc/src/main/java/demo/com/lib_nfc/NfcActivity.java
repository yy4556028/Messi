package demo.com.lib_nfc;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.yuyang.lib_base.ui.base.BaseActivity;
import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.ui.header.HeaderRightBean;
import com.yuyang.lib_base.utils.CommonUtil;
import com.yuyang.lib_base.utils.SharedPreferencesBaseUtil;
import com.yuyang.lib_scan.utils.QrCodeCallback;
import com.yuyang.lib_scan.utils.QrCodeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * https://blog.csdn.net/qq_25749749/article/details/86540555
 * CardEmulator优先级比较高，不知道怎么实现的
 */

public class NfcActivity extends BaseActivity {

    private NfcAdapter mNfcAdapter;
    private PendingIntent pi;

    private TextView infoText;
    private ImageView ivQrcode;

    public static void runActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, NfcActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        //初始化NfcAdapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null || !mNfcAdapter.isEnabled()) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("您的手机不支持NFC")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
            return;
        }

        //假如手机的nfc功能没有被打开。则跳到打开nfc功能的界面
        if (!mNfcAdapter.isEnabled()) {
            Intent setNfc = new Intent(Settings.ACTION_NFC_SETTINGS);
            startActivity(setNfc);
        }

        initView();

        //初始化PendingIntent
        // 初始化PendingIntent，当有NFC设备连接上的时候，就交给当前Activity处理
        pi = PendingIntent.getActivity(this, 0, new Intent(this, getClass())
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 当前app正在前端界面运行，这个时候有intent发送过来，那么系统就会调用onNewIntent回调方法，将intent传送过来
        // 我们只需要在这里检验这个intent是否是NFC相关的intent，如果是，就调用处理方法
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            processIntent(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNfcAdapter.enableForegroundDispatch(this, pi, null, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mNfcAdapter.disableForegroundDispatch(this);
    }

    private void initView() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("NFC");
        List<HeaderRightBean> rightBeanList = new ArrayList<>();
        rightBeanList.add(new HeaderRightBean("保存", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(infoText.getText().toString())) {
                    SharedPreferencesBaseUtil.setString(NfcActivity.class.getSimpleName(), infoText.getText().toString());
                    showQRCode(infoText.getText().toString());
                }
            }
        }));
        headerLayout.setRight(rightBeanList);
        infoText = findViewById(R.id.activity_nfc_infoText);
        ivQrcode = findViewById(R.id.activity_nfc_ivQrcode);
        String savedNfc = SharedPreferencesBaseUtil.getString(NfcActivity.class.getSimpleName(), null);

        if (!TextUtils.isEmpty(savedNfc)) {
            infoText.setText(savedNfc);
            showQRCode(savedNfc);
        }
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    private void processIntent(Intent intent) {
        //取出封装在intent中的TAG
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String CardId = ByteArrayToHexString(tagFromIntent.getId());
        infoText.setText(CardId);

        String[] techList = tagFromIntent.getTechList();
        Log.e("MessiNfc", "标签支持的tachnology类型");
        for (String tech : techList) {
            Log.e("MessiNfc", tech);
        }
    }

    private String ByteArrayToHexString(byte[] inarray) {
        int i, j, in;
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        String out = "";

        for (j = 0; j < inarray.length; ++j) {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }

    public void writeNFCTag(Tag tag) {
        if (tag == null) {
            return;
        }
        NdefMessage ndefMessage = new NdefMessage(
                new NdefRecord[]{NdefRecord.createApplicationRecord(getPackageName())});
        int size = ndefMessage.toByteArray().length;
        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();

                if (!ndef.isWritable()) {
                    return;
                }
                if (ndef.getMaxSize() < size) {
                    return;
                }
                ndef.writeNdefMessage(ndefMessage);
                Toast.makeText(this, "ok", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private void showQRCode(String input) {
        QrCodeUtils.generateAsync(
                input,
                CommonUtil.getScreenWidth() / 2,
                null, new QrCodeCallback<Bitmap>() {
                    @Override
                    public void onComplete(boolean success, Bitmap ret) {
                        ivQrcode.setImageBitmap(ret);
                    }
                });
    }
}
