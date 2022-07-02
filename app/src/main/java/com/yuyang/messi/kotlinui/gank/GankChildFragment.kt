package com.yuyang.messi.kotlinui.gank

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yuyang.lib_base.ui.base.BaseFragment
import com.yuyang.lib_base.utils.ToastUtil
import com.yuyang.messi.R
import com.yuyang.messi.bean.GankBean
import com.yuyang.messi.net.okhttp.OkHttpUtil
import com.yuyang.messi.net.okhttp.callback.StringCallback
import com.yuyang.messi.net.retrofit.Apis
import okhttp3.Call
import org.json.JSONException
import org.json.JSONObject

class GankChildFragment private constructor() : BaseFragment() {

    companion object {
        const val GANK_TYPE = "type"

        fun newInstance(type: String?): GankChildFragment? {
            val gankChildFragment = GankChildFragment()
            val bundle = Bundle()
            bundle.putString(GANK_TYPE, type)
            gankChildFragment.arguments = bundle
            return gankChildFragment
        }
    }

    private var recyclerView: RecyclerView? = null
    private var recyclerAdapter: GankChildRecyclerAdapter? = null
    private var mType: String? = null
    private val mCurrentPageIndex = 1

    override fun getLayoutId(): Int {
        return R.layout.common_recycler
    }

    override fun doOnViewCreated() {
        mType = requireArguments().getString(GANK_TYPE)
        recyclerView = view?.findViewById(R.id.common_recycler_recyclerView)
        recyclerView?.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = GankChildRecyclerAdapter(requireActivity()).also { recyclerAdapter = it }
        }
        OkHttpUtil
                .get()
                .url(getUrl(mCurrentPageIndex))
                .build()
                .execute(object : StringCallback() {
                    override fun onError(call: Call, e: Exception, id: Int) {}
                    override fun onResponse(response: String, id: Int) {
                        try {
                            val jsonResponse = JSONObject(response)
                            val error = jsonResponse.getBoolean("error")
                            if (error) {
                                ToastUtil.showToast(response)
                                return
                            }
                            val list = Gson().fromJson<List<GankBean>>(jsonResponse.getString("results"), object : TypeToken<List<GankBean>>() {}.type)
                            recyclerAdapter?.updateData(list)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            ToastUtil.showToast(e.message)
                        }
                    }
                })
    }

    private fun getUrl(mCurrentPageIndex: Int): String {
        return Apis.GanHuo + "/" + mType + "/20/" + mCurrentPageIndex
    }

}