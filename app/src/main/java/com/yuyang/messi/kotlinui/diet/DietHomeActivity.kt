package com.yuyang.messi.kotlinui.diet

import android.content.Intent
import android.os.Bundle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yuyang.lib_base.ui.header.HeaderLayout
import com.yuyang.messi.R
import com.yuyang.messi.databinding.ActivityDietHomeBinding
import com.yuyang.messi.ui.base.AppBaseActivity

/**
 * 饮食主页
 */
class DietHomeActivity : AppBaseActivity() {

    private lateinit var binding: ActivityDietHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDietHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val headerLayout = findViewById<HeaderLayout>(R.id.headerLayout)
        headerLayout.showTitle("饮食健康")

        initView()
    }

    private fun initView() {
        binding.btIngredients.setOnClickListener {
            startActivity(Intent(this, IngredientsActivity::class.java))
        }
        binding.btRecommendedVegetables.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("推荐蔬菜")
                .setMessage(
                    "铁：茼蒿\n" +
                            "钙：油菜\n" +
                            "叶酸：菠菜\n" +
                            "蛋白质：莲藕\n" +
                            "纤维素：红薯\n" +
                            "高纤维素：豌豆\n" +
                            "维生素C：番茄\n" +
                            "维生素B：芹菜\n" +
                            "胡萝卜素：西兰花\n"
                )
                .setCancelable(true)
                .setPositiveButton("确定") { _, _ -> }
                .setNegativeButton("取消") { _, _ -> }
                .show()
        }
    }
}