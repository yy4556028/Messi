package com.yuyang.messi.ui.media.gsy

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.shuyu.gsyvideoplayer.utils.GSYVideoType
import com.yuyang.lib_base.ui.base.BaseActivity
import com.yuyang.messi.databinding.ActivityAvatarGsyBinding
import java.io.File
import java.io.InputStream

/**
 * 没有buffer回调
 */
class AvatarGsyActivity : BaseActivity() {

    private val sampleRate = 16000

    private val TAG = AvatarGsyActivity::class.java.getSimpleName()

    private lateinit var binding: ActivityAvatarGsyBinding

    private lateinit var silenceFile: File

    private var isSendingPcm = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAvatarGsyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initPlayer()
    }

    override fun onPause() {
        super.onPause()
        binding.videoPlayer.onVideoPause()
    }

    override fun onResume() {
        super.onResume()
        binding.videoPlayer.onVideoResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoManager.releaseAllVideos()
    }


    private fun initPlayer() {
        PlayerFactory.setPlayManager(IjkPlayerManager::class.java)
        GSYVideoType.setRenderType(GSYVideoType.TEXTURE)
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_FULL)
        //ijk关闭log
//        IjkPlayerManager.setLogLevel(IjkMediaPlayer.IJK_LOG_SILENT)
        binding.videoPlayer.setVideoAllCallBack(object : GSYSampleCallBack() {
            override fun onAutoComplete(url: String?, vararg objects: Any?) {
                super.onAutoComplete(url, *objects)
                if (url != silenceFile.absolutePath) {
                    binding.sendLocalPcm.isEnabled = true
                    binding.btnSendTts.isEnabled = true
                    playSilence()
                }
            }
        })
        binding.videoPlayer.isNeedShowWifiTip = true

        playSilence()
    }

    private fun playSilence() {
        Log.d(TAG, "playSilence")
        binding.videoPlayer.isLooping = true
        binding.videoPlayer.setUp(silenceFile.absolutePath, true, null)
        binding.videoPlayer.startPlayLogic()
    }

    private fun playLocal(localPath: String) {
        Log.d(TAG, "playLocal")
        binding.videoPlayer.isLooping = false
        binding.videoPlayer.setUp(localPath, false, null)
//        binding.videoPlayer.setUp(MyConstants.rtmpUrl, false, null)
        binding.videoPlayer.startPlayLogic()
    }

    @SuppressLint("MissingPermission")
    private fun initView() {
        binding.sendLocalPcm.setOnClickListener {
            if (isSendingPcm) {
                return@setOnClickListener
            }
            binding.sendLocalPcm.isEnabled = false
            Thread {
//                val inputStream = getResources().openRawResource(R.raw.test)
//                sendPcm(inputStream)
            }.start()
        }


        binding.btnSendTts.setOnClickListener {
            val content: String = binding.ttsInput.getText().toString()
            if (content != "") {
                sendTxt(content)
            }
        }
    }

    private fun sendTxt(textStr: String) {
    }

    private fun sendPcm(inputStream: InputStream) {
        isSendingPcm = false
    }
}