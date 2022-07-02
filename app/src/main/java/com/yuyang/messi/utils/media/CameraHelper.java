package com.yuyang.messi.utils.media;

public class CameraHelper {

    public byte[] nv21ToI420(byte[] nv21) {
        final byte[] i420 = new byte[nv21.length];
        System.arraycopy(nv21, 0, i420, 0, nv21.length * 2 / 3);
        int index = nv21.length * 2 / 3;
        for (int i = nv21.length * 2 / 3; i < nv21.length; i += 2) {
            i420[index++] = nv21[i + 1];
        }
        for (int i = nv21.length * 2 / 3; i < nv21.length; i += 2) {
            i420[index++] = nv21[i];
        }
        return i420;
    }
}
