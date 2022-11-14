package com.yuyang.lib_base.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.setFragmentResult
import com.yuyang.lib_base.R

class ConfirmDialogFragment : AppCompatDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_fragment_common, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(null)
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)

        view.findViewById<TextView>(R.id.tvTitle).text = arguments?.getString(KEY_TITLE)
        view.findViewById<TextView>(R.id.tvSubtitle).text = arguments?.getString(KEY_SUBTITLE)
        view.findViewById<View>(R.id.btnLeft).setOnClickListener { dismiss() }
        view.findViewById<View>(R.id.btnRight).setOnClickListener {
            dismiss()
            setFragmentResult(TAG, Bundle())
        }
    }

    companion object {
        private const val TAG = "ConfirmDialogFragment"
        private const val KEY_TITLE = "key_title"
        private const val KEY_SUBTITLE = "key_subtitle"

        @JvmStatic
        fun showDialog(
            fragmentManager: FragmentManager,
            title: String?,
            subTitle: String,
            listener: FragmentResultListener?
        ) {
            val dialog = ConfirmDialogFragment()
            val bundle = Bundle()
            bundle.putString(KEY_TITLE, title)
            bundle.putString(KEY_SUBTITLE, subTitle)
            dialog.arguments = bundle

            if (listener != null) {
                fragmentManager.setFragmentResultListener(
                    TAG,
                    dialog,
                    listener
                )
            }
            dialog.show(fragmentManager, TAG)
        }
    }
}
