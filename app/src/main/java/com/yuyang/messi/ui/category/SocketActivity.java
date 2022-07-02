package com.yuyang.messi.ui.category;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.messi.R;
import com.yuyang.messi.net.socket.Const;
import com.yuyang.messi.net.socket.SocketResponseListener;
import com.yuyang.messi.net.socket.SocketThreadManager;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.messi.utils.SharedPreferencesUtil;

/**
 * 创建者: yuyang
 * 创建日期: 2015-07-04
 * 创建时间: 23:34
 *
 * @author yuyang
 * @version 1.0
 */
public class SocketActivity extends AppBaseActivity {

    private EditText hostText;

    private EditText portText;

    private Button openBtn;

    private Button closeBtn;

    private Button reconnectionBtn;

    private EditText sendText;

    private Button sendBtn;

    private EditText receiveText;

    private Button clearBtn;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_socket;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 主线程中请求网络 连接socket 会报 android.os.NetworkOnMainThreadException 异常
         * 两种解决方案：
         * 下面是第一种，主线程中直接忽略，强制执行，不推荐，但是修改起来简单。
         * 第二种：启动一个线程执行网络连接任务，比如 Thread Runnable Handler（推荐）
         */
//        if(Build.VERSION.SDK_INT > 9) {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//        }

        // 读取sp中存储的服务器 IP 和 端口
        Const.SOCKET_SERVER = SharedPreferencesUtil.getString(Const.SP_KEY_SOCKET_SERVER, "192.168.0.0");
        Const.SOCKET_PORT = SharedPreferencesUtil.getInt(Const.SP_KEY_SOCKET_PORT, 0);

        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("Socket");

        initView();
        initEvents();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferencesUtil.setString(Const.SP_KEY_SOCKET_SERVER, Const.SOCKET_SERVER);
        SharedPreferencesUtil.setInt(Const.SP_KEY_SOCKET_PORT, Const.SOCKET_PORT);
    }

    private void initView() {

        hostText = (EditText) findViewById(R.id.activity_socket_host);
        portText = (EditText) findViewById(R.id.activity_socket_port);

        hostText.setText(Const.SOCKET_SERVER);
        portText.setText(Const.SOCKET_PORT + "");

        openBtn = (Button) findViewById(R.id.activity_socket_open);
        openBtn.setText("连接");
        closeBtn = (Button) findViewById(R.id.activity_socket_close);
        closeBtn.setText("断开");
        reconnectionBtn = (Button) findViewById(R.id.activity_socket_reconnection);
        reconnectionBtn.setText("重连");

        sendText = (EditText) findViewById(R.id.activity_socket_text_send);
        sendBtn = (Button) findViewById(R.id.activity_socket_send);
        sendBtn.setText("发送");

        receiveText = (EditText) findViewById(R.id.activity_socket_text_receive);
        receiveText.setHint("服务器发来的数据...");
        clearBtn = (Button) findViewById(R.id.activity_socket_clear);
        clearBtn.setText("清空");
    }

    public void initEvents() {
        openBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String host = hostText.getText().toString();
                int port = (portText.getText().toString().equals("")) ? 0 : Integer.parseInt(portText.getText().toString());

                if (!TextUtils.isEmpty(host) && port != 0) {
                    Const.SOCKET_SERVER = host;
                    Const.SOCKET_PORT = port;
                }

                SocketThreadManager.sharedInstance().connect();
            }
        });

        closeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketThreadManager.releaseInstance();
            }
        });

        reconnectionBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        sendBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String sendStr = sendText.getText().toString();
                if (!TextUtils.isEmpty(sendStr)) {
                    SocketThreadManager.sharedInstance().sendMsg(sendStr, null);
                }

            }
        });

        clearBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                receiveText.setText("");
            }
        });

        SocketResponseListener.setOnResponseListener(new SocketResponseListener.OnResponseListener() {
            @Override
            public void response(final String response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        receiveText.setText(receiveText.getText().toString() + "\n" + response);
                    }
                });
            }
        });
    }

}
