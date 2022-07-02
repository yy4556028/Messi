package com.yuyang.messi.kotlinui.main

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yuyang.lib_base.ui.base.BaseFragment
import com.yuyang.lib_scan.scanDm.ScanZbarActivity
import com.yuyang.messi.databinding.FragmentMainRightBinding
import com.yuyang.messi.ui.main.ShakeActivity
import com.yuyang.messi.ui.main.TipActivity
import com.yuyang.messi.ui.qrcode.CommonScanActivity
import com.yuyang.messi.ui.qrcode.QRCodeActivity

class MainRightFragment : BaseFragment() {

    private lateinit var mBinding: FragmentMainRightBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentMainRightBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun doOnViewCreated() {

        mBinding.fragmentMainRightScanZxing.setOnClickListener {
            startActivity(Intent(activity, CommonScanActivity::class.java));
            (activity as MainActivity?)?.closeDrawers()
        }
        mBinding.fragmentMainRightScanZbar.setOnClickListener {
            startActivity(Intent(activity, ScanZbarActivity::class.java))
            (activity as MainActivity?)?.closeDrawers()
        }
        mBinding.fragmentMainRightQRCode.setOnClickListener {
            startActivity(Intent(activity, QRCodeActivity::class.java))
            (activity as MainActivity?)?.closeDrawers()
        }
        mBinding.fragmentMainRightShake.setOnClickListener {
            startActivity(Intent(activity, ShakeActivity::class.java))
            (activity as MainActivity?)?.closeDrawers()
        }
        mBinding.fragmentMainRightTip.setOnClickListener {
            Handler(Looper.getMainLooper()).postDelayed({
                val km = requireActivity().getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                if (km.isKeyguardLocked) {
                    val alarmIntent = Intent(activity, TipActivity::class.java)
                    alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(alarmIntent)
                }
            }, 5000)
            (activity as MainActivity?)?.closeDrawers()
        }
    }
}