package com.yuyang.messi.utils.media;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Handler;
import android.os.HandlerThread;

import java.nio.ByteBuffer;

public class VideoCodec {

    private Handler mHandler;
    private MediaMuxer mediaMuxer;
    private MediaCodec mediaCodec;
    private int videoTrack;
    private boolean isRecording;

    /**
     * 启动录制
     */
    public void startRecoding(String path, int width, int height, int degrees) {
        try {
            //avc -> H264   hevc -> H265
            MediaFormat format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height);
            //色彩空间 YUV RGB等   大多数设备只支持YUV作为输入
            format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar);
            format.setInteger(MediaFormat.KEY_BIT_RATE, 500_000);//码率 exp:1kbps
            format.setInteger(MediaFormat.KEY_FRAME_RATE, 20);//帧率  图像每秒的个数
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 2);//关键帧间隔 两个I帧之间叫gop
            mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
            mediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mediaCodec.start();

            //混合器 音频+视频 mp4   把声音图像压缩数据放入mp4盒子
            MediaMuxer mediaMuxer = new MediaMuxer(path, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            mediaMuxer.setOrientationHint(degrees);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HandlerThread handlerThread = new HandlerThread("video_codec");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());
        isRecording = true;
    }

    public void queueEncode(byte[] buffer) {
        if (!isRecording) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //立即得到有效输入缓存区
                int index = mediaCodec.dequeueInputBuffer(0);
                if (index >= 0) {
                    ByteBuffer inputBuffer = mediaCodec.getInputBuffer(index);
                    inputBuffer.clear();
                    inputBuffer.put(buffer, 0, buffer.length);
                    mediaCodec.queueInputBuffer(index, 0, buffer.length, System.nanoTime() / 1000, 0);
                }
                while (true) {
                    //获得输出缓冲区（编码后的数据从输出缓冲区获得）
                    MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                    int encoderStatus = mediaCodec.dequeueOutputBuffer(bufferInfo, 10_000);
                    //稍后重试
                    if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                        break;
                    } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        //输出格式发生改变 第一次总会调用，所以在这里开启混合器
                        MediaFormat newFormat = mediaCodec.getOutputFormat();
                        videoTrack = mediaMuxer.addTrack(newFormat);
                        mediaMuxer.start();
                    } else if (encoderStatus ==MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                        //忽略
                    } else {
                        //正常 则 encoderStatus 获得缓冲区下标
                        ByteBuffer encodedData = mediaCodec.getOutputBuffer(encoderStatus);
                        //如果当前的buffer是配置信息 不管它 不用写出去
                        if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                            bufferInfo.size = 0;
                        }

                        if (bufferInfo.size != 0) {
                            //设置从哪里开始读数据(读出来就是编码后的数据)
                            encodedData.position(bufferInfo.offset);
                            //设置能读数据的长度
                            encodedData.limit(bufferInfo.offset + bufferInfo.size);
                            //写出为mp4
                            mediaMuxer.writeSampleData(videoTrack, encodedData, bufferInfo);
                        }
                        //释放这个缓冲区 后续可以存放新的编码后的数据拉
                        mediaCodec.releaseOutputBuffer(encoderStatus, false);
                    }
                }
            }
        });
    }

    public void stopRecording() {
        mediaCodec.stop();
    }
}
