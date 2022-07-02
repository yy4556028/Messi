package com.yamap.lib_chat.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Handler;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yamap.lib_chat.Constants;
import com.yamap.lib_chat.R;
import com.yamap.lib_chat.utils.AudioPlayerHelper;
import com.yamap.lib_chat.utils.AudioRecorderHelper;


/**
 * 录音按钮
 */
public class RecordButton extends Button {

    private static int[] recordImageIds = {
            R.drawable.record_animate_0,
            R.drawable.record_animate_1,
            R.drawable.record_animate_2,
            R.drawable.record_animate_3,
            R.drawable.record_animate_4,
            R.drawable.record_animate_5,
            R.drawable.record_animate_6,
            R.drawable.record_animate_7,
            R.drawable.record_animate_8};

    private RelativeLayout recordTipLyt;
    private LinearLayout recordAlertLyt;
    private ImageView micImage;
    private TextView recordingHint;

    private AudioRecorderHelper recorderHelper = new AudioRecorderHelper();

    private Dialog recordDialog;
    private PowerManager.WakeLock wakeLock;

    public RecordButton(Context context) {
        super(context);
        init();
    }

    public RecordButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public RecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setSaveDir(String dir) {
        recorderHelper.setVoiceFilePath(dir);
    }

    private void init() {
        setText(getResources().getString(R.string.btn_text_speak));
        wakeLock = ((PowerManager) getContext().getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Messi");

        recorderHelper.setMaxLength(Constants.CHAT_VOICE_MAX_LENGTH);
        recorderHelper.setRecorderListener(new AudioRecorderHelper.RecorderListener() {
            @Override
            public void onProgress(final long currentLength) {

                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int secondLeft = (int) (recorderHelper.getMaxLength() / 1000) - (int) (currentLength / 1000);

                        if (secondLeft <= 5) {
                            recordingHint.setText(String.format(getResources().getString(R.string.chat_record_time_tip), secondLeft + ""));
                        }
                    }
                });
            }

            @Override
            public void onDb(final int db) {
                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Activity activity = (Activity) getContext();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int level = 0;
                                if (db < 30)
                                    level = 0;
                                else
                                    level = (int) (((float) db - 30) / 7);
                                micImage.setImageResource(recordImageIds[level]);
                            }
                        });
                    }
                });
            }

            @Override
            public void onComplete(final String voiceFilePath, final long voiceTimeLength) {
                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        recordDialog.dismiss();

                        if (voiceTimeLength > 0 && !TextUtils.isEmpty(voiceFilePath)) {
                            if (recordEventListener != null) {
                                recordEventListener.onFinishedRecord(voiceFilePath, voiceTimeLength);
                            }
                        } else if (voiceTimeLength == AudioRecorderHelper.ERROR_INVALID_FILE) {
                            Toast.makeText(getContext(), getResources().getString(R.string.Recording_without_permission), Toast.LENGTH_SHORT).show();
                        } else if (voiceTimeLength == AudioRecorderHelper.LENGTH_TOO_SHORT) {
                            Toast.makeText(getContext(), getResources().getString(R.string.The_recording_time_is_too_short), Toast.LENGTH_SHORT).show();
                        } else {
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                AudioPlayerHelper.getInstance().stopPlayer();
                initRecordDialog();
                startRecording();
                recordDialog.show();
                break;
            case MotionEvent.ACTION_UP:
                if (wakeLock.isHeld())
                    wakeLock.release();
                if (event.getY() < 0) {
                    recorderHelper.discardRecording();
                    recordDialog.dismiss();
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recorderHelper.stopRecording();
                        }
                    }, 500);//怕最后录不进去
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getY() < 0) {
                    recordTipLyt.setVisibility(INVISIBLE);
                    recordAlertLyt.setVisibility(VISIBLE);
                } else {
                    recordTipLyt.setVisibility(VISIBLE);
                    recordAlertLyt.setVisibility(INVISIBLE);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                recorderHelper.discardRecording();
                recordDialog.dismiss();
                break;
            default:
                break;
        }
        return true;
    }

    private void initRecordDialog() {
        if (null == recordDialog) {
            recordDialog = new Dialog(getContext(), R.style.record_dialog_style);
            View view = inflate(getContext(), R.layout.record_dialog, null);
            recordTipLyt = (RelativeLayout) view.findViewById(R.id.widget_voice_recorder_tip);
            recordAlertLyt = (LinearLayout) view.findViewById(R.id.record_dialog_alert);
            micImage = (ImageView) view.findViewById(R.id.record_dialog_image_right);
            recordingHint = (TextView) view.findViewById(R.id.record_dialog_hint);
            recordDialog.setContentView(view, new LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            recordDialog.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {

                }
            });

            LayoutParams lp = recordDialog.getWindow().getAttributes();
            lp.gravity = Gravity.CENTER;
        }
    }

    private void startRecording() {
        try {
            wakeLock.acquire();
            this.setVisibility(View.VISIBLE);
            recordTipLyt.setVisibility(VISIBLE);
            recordAlertLyt.setVisibility(INVISIBLE);
            recordingHint.setText(getContext().getString(R.string.slide_up_to_cancel));
            recorderHelper.startRecording();
            recordEventListener.onStartRecord();
        } catch (Exception e) {
            e.printStackTrace();
            if (wakeLock.isHeld())
                wakeLock.release();
            if (recorderHelper != null)
                recorderHelper.discardRecording();
            this.setVisibility(View.INVISIBLE);
            Toast.makeText(getContext(), getResources().getString(R.string.recoding_fail), Toast.LENGTH_SHORT).show();
        }
    }

    public interface RecordEventListener {
        void onFinishedRecord(String audioPath, long voiceTimeLength);

        void onStartRecord();
    }

    private RecordEventListener recordEventListener;

    public void setRecordEventListener(RecordEventListener listener) {
        recordEventListener = listener;
    }
}
