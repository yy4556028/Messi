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
        binding.btnIngredients.setOnClickListener {
            startActivity(Intent(this, IngredientsActivity::class.java))
        }
        binding.btnRecommendedVegetables.setOnClickListener {
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
        binding.btnStarch.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("淀粉")
                .setMessage(
                    "土豆淀粉(生粉)：最常见，勾芡变浓稠，\n\n" +
                            "红薯淀粉：粘度高，吸附力强，适合油炸包裹\n\n" +
                            "玉米淀粉：粘度适中，用途最广，适合煎 炸 挂糊 腌肉上浆，排粉起嫩滑作用\n\n" +
                            "木薯粉：粘度高无异味，适用精调甜品，如芋圆 虾片 汤圆\n\n" +
                            "小麦淀粉(澄粉)：无筋面粉，透明度好，适用广式点心如水晶虾脚，肠粉 晶莹剔透"
                )
                .setCancelable(true)
                .setPositiveButton("确定") { _, _ -> }
                .setNegativeButton("取消") { _, _ -> }
                .show()
        }
        binding.btnSoySauce.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("酱油")
                .setMessage(
                    "倒立摇会起泡，优质的泡沫均匀，挂壁很久\n" +
                            "配料表无添加自然发酵\n\n" +
                            "谷氨酸钠 呈味核 是人为提鲜\n\n" +
                            "不选脱脂大豆(豆渣)"
                )
                .setCancelable(true)
                .setPositiveButton("确定") { _, _ -> }
                .setNegativeButton("取消") { _, _ -> }
                .show()
        }
        binding.btnRoast.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("烤肉")
                .setMessage(
                    "1一斤牛肉切好\n" +
                            "清水95g 酱油5g 食用盐3g 小苏打2g 白砂糖3g\n\n" +
                            "牛肉放里拌匀，吸收完汤汁\n\n" +
                            "加几滴芝麻香油 50g洋葱 20g食用油拌匀\n\n" +
                            "想调味可以加1g黑胡椒或白胡椒"
                )
                .setCancelable(true)
                .setPositiveButton("确定") { _, _ -> }
                .setNegativeButton("取消") { _, _ -> }
                .show()
        }
        binding.btnWss.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("维生素")
                .setMessage(
                    "维生素A：脂溶性。大多数人不缺除非非洲难民。高剂量补充可能增加健康人群或疾病患者死亡率，食物获取就可以(鸡蛋黄，牛奶，动物肝脏，胡萝卜，玉米，菠菜)\n\n" +
                            "维生素B：水溶性。爱喝酒 吃东西不全面 吃素 可以补\n\n" +
                            "维生素B1：玉米燕麦豆类\n\n" +
                            "维生素B2：牛奶鸡蛋动物肝脏\n\n" +
                            "维生素B3：新鲜的蔬菜水果\n\n" +
                            "维生素B9(叶酸)：新鲜的蔬菜水果。预防胎儿神经管缺陷。孕妇0.4mg/日\n\n" +
                            "维生素B12：肉\n\n" +
                            "维生素C：水溶性。过量补充会增加结石风险。如果蔬菜水果吃得少可以适当补\n\n" +
                            "维生素D：脂溶性。含维生D食物少。很多人基本都缺。帮助钙吸收。\n\n" +
                            "维生素E：脂溶性。缺乏很罕见。高剂量会在体内蓄积中毒增加死亡率"
                )
                .setCancelable(true)
                .setPositiveButton("确定") { _, _ -> }
                .setNegativeButton("取消") { _, _ -> }
                .show()
        }
    }
}