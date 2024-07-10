package com.yuyang.messi.ui.media.exo;

import static java.lang.Math.min;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.C;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.BaseDataSource;
import androidx.media3.datasource.DataSpec;

import java.io.IOException;

import okio.ByteString;

@UnstableApi
public class AvatarDataSource extends BaseDataSource {
    private static final String TAG = AvatarDataSource.class.getSimpleName();

    private final AvatarDataHandler dataHandler;
    private ByteString currentByteString;
    private int currentPosition = 0;
    private int remainingBytes = 0;
    private boolean opened;

    /**
     * Creates base data source.
     *
     * @param isNetwork Whether the data source loads data through a network.
     */
    public AvatarDataSource(boolean isNetwork) {
        super(isNetwork);
        dataHandler = AvatarDataHandler.getInstance();
    }

    @Override
    public long open(DataSpec dataSpec) throws IOException {
        Log.i(TAG, "open");
        opened = true;
        transferStarted(dataSpec);
        return -1;
    }

    @Nullable
    @Override
    public Uri getUri() {
        return Uri.parse("");
    }

    @Override
    public void close() {
        if (opened) {
            opened = false;
            transferEnded();
        }
        Log.i(TAG, "close");
    }

    @Override
    public int read(@NonNull byte[] buffer, int offset, int length) {
        if (currentByteString == null && dataHandler.isVideoCacheEmpty()) {
            return 0;
        }
        if (currentByteString == null) {
            currentByteString = dataHandler.getNextStream();
            Log.i(TAG, String.valueOf(currentByteString));
            if (currentByteString.equals(ByteString.EMPTY)) {
                return C.RESULT_END_OF_INPUT;
            }
            currentPosition = 0;
//            remainingBytes = currentByteString.size();
            remainingBytes = currentByteString.toByteArray().length;
        }

        int readSize = min(length, remainingBytes);
        System.arraycopy(currentByteString.toByteArray(), currentPosition, buffer, offset, readSize);
        currentPosition += readSize;
        remainingBytes -= readSize;
        if (remainingBytes == 0) {
            currentByteString = null;
        }
        bytesTransferred(readSize);
        return readSize;
    }
}
