package com.yuyang.messi.kotlinui.category

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yuyang.messi.databinding.ActivityRecyclerviewBinding
import com.yuyang.messi.kotlinui.category.adapter.IngredientsResultAdapter
import com.yuyang.messi.kotlinui.category.bean.IngredientsResultBean
import com.yuyang.messi.ui.base.AppBaseActivity
import com.yuyang.messi.utils.AssetsUtil

class IngredientsResultActivity : AppBaseActivity() {

    companion object {

        private const val KEY_TEXT = "key_text"
        fun start(context: Context, text: String) {
            context.startActivity(
                Intent(context, IngredientsResultActivity::class.java).putExtra(
                    KEY_TEXT,
                    text
                )
            )
        }
    }

    val hashMap: HashMap<String, String> = HashMap()

    private val ingredient_data: MutableList<IngredientsResultBean> = mutableListOf()

    private lateinit var binding: ActivityRecyclerviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecyclerviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            val json = AssetsUtil.readText("ingredients.json")
            val beanList: List<IngredientsResultBean> = Gson().fromJson(
                json,
                object : TypeToken<List<IngredientsResultBean?>?>() {}.type
            )
            ingredient_data.addAll(beanList)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        initView()
    }

    private fun initView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.adapter = IngredientsResultAdapter(analyse())
    }

    private fun analyse(): MutableList<IngredientsResultBean> {
        val resultData: MutableList<IngredientsResultBean> = mutableListOf()

        val originStr = intent.getStringExtra(KEY_TEXT)
        originStr?.trim()?.let {
            val arr = it.split("[\\p{Punct}\\s]+")

            arr.forEach arr@{ originItem ->
                ingredient_data.forEach ingredient@{ ingredientItem ->
                    if (originItem.contains(ingredientItem.name.toString()) ||
                        ingredientItem.name!!.contains(originItem)) {
                        resultData.add(ingredientItem)
                        return@arr
                    }
                }
                val bean = IngredientsResultBean()
                bean.name
                resultData.add(IngredientsResultBean(name = originItem, type = "未知", result = "未知"))
            }
        }
        return resultData
    }
}