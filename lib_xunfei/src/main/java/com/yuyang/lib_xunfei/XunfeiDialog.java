package com.yuyang.lib_xunfei;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

public class XunfeiDialog extends Dialog {

    private WaveformView waveformView;
    private SiriView siriView;

    private Context context;

    public XunfeiDialog(Context context) {
        super(context, R.style.XunfeiDialog);
        this.context = context;
        init();
    }

    private void init() {
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_xunfeidialog);
        waveformView = findViewById(R.id.view_xunfeidialog_waveformView);
        siriView = findViewById(R.id.view_xunfeidialog_siriView);
    }

    public void updateAmplitude(float amplitude) {
        waveformView.updateAmplitude(amplitude);
        siriView.setWaveHeight(amplitude);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
