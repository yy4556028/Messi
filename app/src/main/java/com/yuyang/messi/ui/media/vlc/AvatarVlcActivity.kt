package com.yuyang.messi.ui.media.vlc

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import com.yuyang.lib_base.ui.base.BaseActivity
import com.yuyang.messi.databinding.ActivityAvatarVlcBinding
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.util.DisplayManager

/**
 * buffer的时候会黑屏
 */
class AvatarVlcActivity : BaseActivity() {

    private val sampleRate = 16000

    private val TAG = AvatarVlcActivity::class.java.getSimpleName()

    private lateinit var binding: ActivityAvatarVlcBinding

    private var libVLC: LibVLC? = null
    private var mediaPlayer: MediaPlayer? = null

    private var isSendingPcm = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAvatarVlcBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initPlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    private fun initPlayer() {
        libVLC = LibVLC(this)
        mediaPlayer = MediaPlayer(libVLC)

        binding.vlcVideoLayout.let {
            mediaPlayer!!.attachViews(
                it,
                DisplayManager(this, null, true, false, false),
                false,
                false
            )
//            mediaPlayer!!.videoScale = MediaPlayer.ScaleType.SURFACE_FILL
        }

        PlaybackStateCompat.REPEAT_MODE_ONE
        mediaPlayer!!.setEventListener { event ->
            if (event.type == 267 || event.type == 268) {
                return@setEventListener
            }
//            println("setEventListener -> " + event.type)
            if (event.type == MediaPlayer.Event.Stopped) {
                binding.sendLocalPcm.isEnabled = true
                binding.btnSendTts.isEnabled = true
                playSilence()
            }
        }

        playSilence()
    }

    private fun releasePlayer() {
        mediaPlayer!!.detachViews()
        mediaPlayer!!.release()
        libVLC!!.release()
    }

    private fun playSilence() {
        Log.d(TAG, "playSilence")
//        mediaPlayer!!.media = Media(libVLC, silenceFile.absolutePath)
//        mediaPlayer!!.play()
    }

    private fun playLocal(localPath: String) {
        Log.d(TAG, "playLocal $localPath")
        mediaPlayer!!.media = Media(libVLC, localPath)
//        mediaPlayer!!.media = Media(libVLC, Uri.parse("rtmp://106.75.79.212/live/aabb.flv"))
        mediaPlayer!!.play()
    }

    @SuppressLint("MissingPermission")
    private fun initView() {
        binding.sendLocalPcm.setOnClickListener {
            if (isSendingPcm) {
                return@setOnClickListener
            }
            binding.sendLocalPcm.isEnabled = false
            Thread {
            }.start()
        }



        binding.btnSendTts.setOnClickListener {
            val content: String = binding.ttsInput.getText().toString()
            if (content != "") {
            }
        }
    }
}