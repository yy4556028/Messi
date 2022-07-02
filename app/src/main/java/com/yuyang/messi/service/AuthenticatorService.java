package com.yuyang.messi.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.yuyang.messi.utils.AccountAuthenticator;

public class AuthenticatorService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return new AccountAuthenticator(this).getIBinder();
    }

}
