package com.yuyang.lib_base.utils.media;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

// https://www.jianshu.com/p/37eb3b989e5d
// http://t.zoukankan.com/eguid-p-6821585.html
public class DataConvertUtil {

    public static short[] byteToShort(byte[] bytes, ByteOrder byteOrder) {
        int byteLength = bytes.length;
        //byte[]转成short[]，数组长度缩减一半
        int shortLength = byteLength >> 1;
        //把byte[]数组装进byteBuffer缓冲
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes, 0, byteLength);
        //将byteBuffer转成小端序并获取shortBuffer
        ShortBuffer shortBuffer = byteBuffer.order(byteOrder).asShortBuffer();
        short[] shorts = new short[shortLength];
        //取出shortBuffer中的short数组
        shortBuffer.get(shorts, 0, shortLength);
        return shorts;
    }

    public static byte[] shortToByte(short[] shorts, ByteOrder byteOrder) {

        int count = shorts.length;
        byte[] dest = new byte[count << 1];
        for (int i = 0; i < count; i++) {
            dest[i * 2 + (byteOrder == ByteOrder.BIG_ENDIAN ? 0 : 1)] = (byte) (shorts[i] >> 8);
            dest[i * 2 + (byteOrder == ByteOrder.BIG_ENDIAN ? 1 : 0)] = (byte) (shorts[i]);
        }
        return dest;
    }
}
