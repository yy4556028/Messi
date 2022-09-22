package com.yuyang.messi.kotlinui.circle_menu

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import com.yuyang.lib_base.utils.ToastUtil
import com.yuyang.messi.R
import com.yuyang.messi.databinding.ActivityCircleMenuBinding
import com.yuyang.messi.ui.base.AppBaseActivity
import com.yuyang.messi.ui.media.*
import com.yuyang.messi.ui.media.audio.aidl.AudioActivity
import com.yuyang.messi.ui.media.camerax.CameraXActivity
import com.yuyang.messi.utils.NotificationUtil
import com.yuyang.messi.view.CircleMenuLayout
import java.util.*

class CircleMenuActivity : AppBaseActivity() {

    private lateinit var mBinding: ActivityCircleMenuBinding

    private val galleryLauncher = registerForActivityResult(StartActivityForResult(), ActivityResultCallback { result ->
        if (RESULT_OK != result.resultCode) return@ActivityResultCallback
        val selectImages: ArrayList<String>? = result.data?.getStringArrayListExtra(AlbumActivity.SELECTED_PHOTOS)
        ToastUtil.showToast(selectImages?.size.toString() + "")
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityCircleMenuBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.circleMenu.apply {
            val mItemImgs = intArrayOf(
                R.drawable.album,
                R.drawable.music,
                R.drawable.music,
                R.drawable.video,
                R.drawable.contacts,
                R.drawable.camera,
                R.drawable.camera,
                R.drawable.camera,
                R.drawable.camera
            )

            val mItemTexts = arrayOf(
                "相册", "音频", "录音", "视频", "联系人", "扫名片", "相机1", "相机2", "相机X"
            )

            removeViews(1, mBinding.circleMenu.childCount - 1)
            setMenuItemIconsAndTexts(mItemImgs, mItemTexts)

            setOnMenuItemClickListener(object : CircleMenuLayout.OnMenuItemClickListener {
                override fun itemClick(view: View, pos: Int) {
                    when (pos) {
                        0 -> galleryLauncher.launch(AlbumActivity.getLaunchIntent(activity, 7, true, true, true, null))
                        1 -> startActivity(Intent(activity, AudioActivity::class.java))
                        2 -> startActivity(Intent(activity, AudioRecordActivity::class.java))
                        3 -> startActivity(Intent(activity, VideoActivity::class.java))
                        4 -> startActivity(Intent(activity, ContactsActivity::class.java))
                        5 -> startActivity(Intent(activity, CardScanActivity::class.java))
                        6 -> startActivity(Intent(activity, Camera1Activity::class.java))
                        7 -> startActivity(Intent(activity, Camera2Activity::class.java))
                        8 -> startActivity(Intent(activity, CameraXActivity::class.java))
                        else -> ToastUtil.showToast("$pos click")
                    }
                }

                override fun itemCenterClick(view: View) {
                    NotificationUtil.showNotification(activity, VideoActivity::class.java, "heheda", false)
                    ToastUtil.showToast("Center Click")
                }
            })
        }

        mBinding.pulsatorLayout.start()

        mBinding.countSeekBar.apply {
            progress = mBinding.pulsatorLayout.count - 1
            setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    mBinding.pulsatorLayout.count = progress + 1
                    mBinding.tvCount.text = String.format(Locale.CHINESE, "%d", progress + 1)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            })
        }

        mBinding.durationSeekBar.apply {
            progress = mBinding.pulsatorLayout.duration / 100

            setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    mBinding.pulsatorLayout.duration = progress * 100
                    mBinding.tvDuration.text = String.format(Locale.CHINESE, "%.1f", progress * 0.1f)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            })
        }
    }

    public override fun onResume() {
        super.onResume()
        if (!mBinding.circleMenu.isOpen) {
            mBinding.circleMenu.startAnim(true, 300)
        }
    }
}