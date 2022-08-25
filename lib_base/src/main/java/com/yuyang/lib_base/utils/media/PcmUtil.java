package com.yuyang.lib_base.utils.media;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 描述：pcm格式的音频转换为wav格式的工具类
 * PCM数据：https://www.cnblogs.com/CoderTian/p/6657844.html
 */
public class PcmUtil {

    private static final String TAG = PcmUtil.class.getSimpleName();

    private int mSampleRate = 44100;// 此处的值必须与录音时的采样率一致
    private int mChannel = AudioFormat.CHANNEL_IN_MONO; //单声道
    private int mEncoding = AudioFormat.ENCODING_PCM_16BIT;

    private final int mBufferSize; //缓存的音频大小

    private static class SingleHolder {
        static PcmUtil mInstance = new PcmUtil();
    }

    public static PcmUtil getInstance() {
        return SingleHolder.mInstance;
    }


    public PcmUtil() {
        this.mBufferSize = AudioRecord.getMinBufferSize(mSampleRate, mChannel, mEncoding);
    }

    /**
     * @param sampleRate sample rate、采样率
     * @param channel    channel、声道
     * @param encoding   Audio data format、音频格式
     */
    public PcmUtil(int sampleRate, int channel, int encoding) {
        this.mSampleRate = sampleRate;
        this.mChannel = channel;
        this.mEncoding = encoding;
        this.mBufferSize = AudioRecord.getMinBufferSize(mSampleRate, mChannel, mEncoding);
    }

    /**
     * pcm文件转wav文件
     *
     * @param inFilePath  源文件路径
     * @param outFilePath 目标文件路径
     * @param deleteOrg   是否删除源文件
     */
    public boolean pcmToWav(String inFilePath, String outFilePath, boolean deleteOrg) {
        File file = new File(inFilePath);
        if (!file.exists()) {
            return false;
        }

        WaveHeader header = new WaveHeader();
        header.audioLength = (int) file.length();
        header.fileLength = header.audioLength + (44 - 8);
        header.samplesPerSec = mSampleRate;

        switch (mChannel) {
            case AudioFormat.CHANNEL_IN_MONO:
                header.channelsNum = 1;
                break;
            case AudioFormat.CHANNEL_IN_STEREO:
                header.channelsNum = 2;
                break;
            default:
                Log.e(TAG, "Not support channel");
                return false;
        }
        switch (mEncoding) {
            case AudioFormat.ENCODING_PCM_8BIT:
                header.bitsPerSample = 8;
                break;
            case AudioFormat.ENCODING_PCM_16BIT:
                header.bitsPerSample = 16;
                break;
            case AudioFormat.ENCODING_PCM_32BIT:
                header.bitsPerSample = 32;
                break;
            default:
                Log.e(TAG, "Not support encoding");
                return false;
        }

        byte[] headerData;
        try {
            headerData = header.getHeader();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return false;
        }

        if (headerData.length != 44) // WAV标准，头部应该是44字节,如果不是44个字节则不进行转换文件
            return false;

        try {
            //先删除目标文件
            File outFile = new File(outFilePath);
            if (outFile.exists())
                outFile.delete();

            FileInputStream in;
            FileOutputStream out;

            in = new FileInputStream(inFilePath);
            out = new FileOutputStream(outFilePath);

            out.write(headerData, 0, headerData.length);

            byte[] data = new byte[mBufferSize];
            while (in.read(data) != -1) {
                out.write(data);
            }
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (deleteOrg) {
            new File(inFilePath).delete();
        }
        return true;
    }

    private static class WaveHeader {
        public final char[] ChunkID = {'R', 'I', 'F', 'F'};
        public final char[] WavFormat = {'W', 'A', 'V', 'E'};
        public final char[] SubChunk1ID = {'f', 'm', 't', ' '};
        public final char[] SubChunk2ID = {'d', 'a', 't', 'a'};
        public final int SubChunk1Size = 16;
        public final short AudioFormat = 0x0001;

        public int fileLength;
        public int audioLength;
        public short channelsNum;
        public int samplesPerSec;
        public short bitsPerSample;

        public byte[] getHeader() throws IOException {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            // RIFF chunk
            writeChar(bos, ChunkID);// ChunkID：RIFF
            writeInt(bos, fileLength);// ChunkSize：存储文件的字节数（不包含ChunkID和ChunkSize这8个字节）
            writeChar(bos, WavFormat);// WAVE Format

            // fmt sub-chunk
            writeChar(bos, SubChunk1ID);// SubChunk1ID：'fmt '
            writeInt(bos, SubChunk1Size);// SubChunk1Size：存储该子块的字节数（不含前面的SubChunk1ID和SubChunk1Size这8个字节） 所以固定16
            writeShort(bos, AudioFormat);// 存储音频文件的编码格式，例如若为PCM则其存储值为1，若为其他非PCM格式的则有一定的压缩
            writeShort(bos, channelsNum);// 通道数，单通道(Mono)值为1，双通道(Stereo)值为2，等等
            writeInt(bos, samplesPerSec);// 采样率，如8k，44.1k等
            writeInt(bos, samplesPerSec * channelsNum * bitsPerSample / 8);// 每秒存储的bit数，其值=SampleRate * NumChannels * BitsPerSample / 8
            writeShort(bos, channelsNum * bitsPerSample / 8);// 块对齐大小，其值=NumChannels * BitsPerSample / 8
            writeShort(bos, bitsPerSample);// 每个采样点的bit数，一般为8,16,32等

            // data sub-chunk
            writeChar(bos, SubChunk2ID);// SubChunk2ID：'data'
            writeInt(bos, audioLength);// SubChunk2Size：内容为接下来的正式的数据部分的字节数
            bos.flush();
            byte[] r = bos.toByteArray();
            bos.close();
            return r;
        }

        private void writeShort(ByteArrayOutputStream bos, int s) throws IOException {
            byte[] myByte = new byte[2];
            myByte[1] = (byte) ((s << 16) >> 24);
            myByte[0] = (byte) ((s << 24) >> 24);
            bos.write(myByte);
        }


        private void writeInt(ByteArrayOutputStream bos, int n) throws IOException {
            byte[] buf = new byte[4];
            buf[3] = (byte) (n >> 24);
            buf[2] = (byte) ((n << 8) >> 24);
            buf[1] = (byte) ((n << 16) >> 24);
            buf[0] = (byte) ((n << 24) >> 24);
            bos.write(buf);
        }

        private void writeChar(ByteArrayOutputStream bos, char[] id) {
            for (char c : id) {
                bos.write(c);
            }
        }
    }
}
