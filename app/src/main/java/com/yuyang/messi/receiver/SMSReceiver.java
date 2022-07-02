package com.yuyang.messi.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsMessage;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SMSReceiver extends BroadcastReceiver {

	private EditText editText;

	private String mySender;

//	public static final String SMS_RECEIVED_ACTION = Telephony.Sms.Intents.DATA_SMS_RECEIVED_ACTION;
	public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";

	public SMSReceiver(Context context, EditText editText, String mySender) {
		this.mySender = mySender;
		this.editText = editText;
		// 实例化过滤器并设置要过滤的广播
        IntentFilter intentFilter = new IntentFilter(SMS_RECEIVED_ACTION);

        context.registerReceiver(this, intentFilter);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(SMS_RECEIVED_ACTION)) {
			Object[] pdus = (Object[]) intent.getExtras().get("pdus");
			for (Object pdu : pdus) {
				
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);

				String sender = smsMessage.getDisplayOriginatingAddress();
				String content = smsMessage.getDisplayMessageBody();

				Pattern p = Pattern.compile("[^0-9]");
				Matcher m = p.matcher(content);

				String captchaStr = m.replaceAll("").trim();

				// 过滤不需要读取的短信的发送号码
//				if (mySender.equals(sender)) {
				if (content.contains("拇指客")) {
					editText.setText(captchaStr);
					abortBroadcast();
				}
			}
		}
	}
}