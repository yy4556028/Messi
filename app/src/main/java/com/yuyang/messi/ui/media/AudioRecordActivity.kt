package com.yuyang.messi.ui.media

import android.Manifest
import android.annotation.SuppressLint
import android.media.AudioFormat
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuyang.lib_base.ui.header.HeaderLayout
import com.yuyang.lib_base.utils.FileUtil
import com.yuyang.lib_base.utils.ToastUtil
import com.yuyang.lib_base.utils.media.AudioRecorderHelper
import com.yuyang.lib_base.utils.media.MediaPlayerController
import com.yuyang.messi.R
import com.yuyang.messi.databinding.ActivityAudioRecordBinding
import com.yuyang.messi.ui.base.AppBaseActivity
import com.yuyang.messi.ui.media.adapter.AudioRecordAdapter
import java.io.File

class AudioRecordActivity : AppBaseActivity() {

    private lateinit var binding: ActivityAudioRecordBinding

    private val mAudioRecorderHelper = AudioRecorderHelper()

    private var mAdapter: AudioRecordAdapter? = null

    private val permissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            val deniedAskList: MutableList<String> = arrayListOf()
            val deniedNoAskList: MutableList<String> = arrayListOf()
            for ((key, value) in result) {
                if (!value) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, key)) {
                        deniedAskList.add(key)
                    } else {
                        deniedNoAskList.add(key)
                    }
                }
            }
            if (deniedAskList.size == 0 && deniedNoAskList.size == 0) { //全通过
            } else if (deniedNoAskList.size > 0) {
                finish()
            } else {
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAudioRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        permissionsLauncher.launch(
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )

        FileUtil.deleteDir(File(AudioRecorderHelper.FILE_DIR) , false)
    }

    override fun onDestroy() {
        super.onDestroy()
        MediaPlayerController.getInstance().setMediaPlayerListener(null)
        MediaPlayerController.getInstance().releaseMediaPlayer()
    }

    @SuppressLint("MissingPermission")
    private fun initView() {
        val headerLayout = findViewById<HeaderLayout>(R.id.headerLayout)
        headerLayout.showTitle("录音")

        binding.startRecord.setOnClickListener {
            binding.startRecord.isEnabled = false
            binding.endRecord.isEnabled = true

            var sample = 44100
            when (binding.radioGroup.checkedRadioButtonId) {
                R.id.rbSample_44100 -> sample = 44100
                R.id.rbSample_16000 -> sample = 16000
                R.id.rbSample_8000 -> sample = 8000
            }

            mAudioRecorderHelper.startRecordAudio(
                sample,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
        }
        binding.endRecord.setOnClickListener {
            binding.startRecord.isEnabled = true
            binding.endRecord.isEnabled = false
            mAudioRecorderHelper.stopRecording()

            mAdapter?.data = mAudioRecorderHelper.wavList
        }

        mAudioRecorderHelper.setRecorderListener(object:AudioRecorderHelper.RecorderListener{
            override fun onGenerateWav(wavFilePath: String?) {
                runOnUiThread{
                    mAdapter?.data = mAudioRecorderHelper.wavList
                }
            }

            override fun onVolume(volume: Double) {
                runOnUiThread {
                    binding.tvVolume.text = volume.toInt().toString()
                }
            }
        })

        binding.recyclerView.let {
            it.layoutManager = LinearLayoutManager(this)
            mAdapter = AudioRecordAdapter(null)
            mAdapter?.setMyClickListener(object : AudioRecordAdapter.MyClickListener {
                override fun onItemPlay(index: Int) {
                    MediaPlayerController.getInstance()
                        .setPath(mAdapter?.data?.get(index))
                    MediaPlayerController.getInstance().mediaPlayer.start()
                }

                override fun onItemDetect(index: Int) {
                    detectSnore(mAdapter?.data?.get(index))
                }

            })
            it.adapter = mAdapter
        }
    }

    private fun detectSnore(path: String?) {
        ToastUtil.showToast("未实现")
    }
}