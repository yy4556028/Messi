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
import com.yuyang.lib_base.utils.media.AudioDealer
import com.yuyang.lib_base.utils.media.AudioRecorderHelper
import com.yuyang.lib_base.utils.media.MediaPlayerController
import com.yuyang.messi.R
import com.yuyang.messi.databinding.ActivityAudioRecordBinding
import com.yuyang.messi.ui.base.AppBaseActivity
import com.yuyang.messi.ui.media.adapter.AudioRecordAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FilenameFilter
import javax.inject.Inject

@AndroidEntryPoint
class AudioRecordActivity : AppBaseActivity() {

    private lateinit var binding: ActivityAudioRecordBinding

    private val mAudioRecorderHelper: AudioRecorderHelper = AudioRecorderHelper()
    private var mAudioDealer = AudioDealer()

    @Inject
    lateinit var mAdapter: AudioRecordAdapter

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

        FileUtil.deleteDir(File(AudioDealer.AUDIO_DIR), false)
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

            mAudioDealer.startDeal(
                sample,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
            )
            mAudioRecorderHelper.startRecordAudio(
                sample,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
        }
        binding.endRecord.setOnClickListener {
            binding.startRecord.isEnabled = true
            binding.endRecord.isEnabled = false

            mAudioDealer.endDeal()
            mAudioRecorderHelper.stopRecording()

            val fileDir = File(AudioDealer.AUDIO_DIR)
            val wavList = fileDir.listFiles(object : FilenameFilter {
                override fun accept(dir: File?, name: String?): Boolean {
                    return name != null && name.endsWith("wav")
                }
            })?.map { it.absolutePath }
            mAdapter.setData(wavList)
        }

        mAudioRecorderHelper.setRecorderListener(object : AudioRecorderHelper.RecorderListener {
            override fun onReadByteData(readSize: Int, byteData: ByteArray) {
                mAudioDealer.dealData(readSize, byteData)
                val volume = AudioDealer.getVolume(readSize, byteData).toInt()
                runOnUiThread {
                    binding.tvVolume.text = "音量" + volume.toString()
                }
            }
        })

        binding.recyclerView.let {
            it.layoutManager = LinearLayoutManager(this)
            mAdapter.setMyClickListener(object : AudioRecordAdapter.MyClickListener {
                override fun onItemPlay(index: Int) {
                    MediaPlayerController.getInstance()
                        .setPath(mAdapter.getData()?.get(index))
                    MediaPlayerController.getInstance().mediaPlayer.start()
                }

                override fun onItemDetect(index: Int) {
                }

            })
            it.adapter = mAdapter
        }
    }
}