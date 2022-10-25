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
        binding.btnRecommendedFoods.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("推荐食物")
                .setMessage(
                    "蛋白质十佳：1.鸡蛋：健康人每日吃一个鸡蛋，蛋白蛋黄都要吃\n"+
                            "2.牛奶\n"+
                            "3.鱼肉\n"+
                            "4.虾肉\n"+
                            "5.鸡肉\n"+
                            "6.鸭肉\n"+
                            "7.瘦牛肉\n"+
                            "8.瘦羊肉\n"+
                            "9.瘦猪肉\n"+
                            "10.大豆\n\n"+

                    "健康食品：番茄，菠菜，大蒜，花生，菜花，蓝莓，燕麦，绿茶，红酒，桂鱼\n\n" +
                            "水果：木瓜，草莓，橙子，柑橘，猕猴桃，芒果，杏，柿子，西瓜\n\n" +
                            "蔬菜：红薯，芦笋，白菜，花椰菜，芹菜，茄子，甜菜，胡萝卜，荠菜，大花蕙兰，金针菇，雪红，大白菜\n\n" +
                            "肉类：鸡鸭鹅鱼兔\n\n" +
                            "健脑：菠菜，韭菜，南瓜，洋葱，西兰花，辣椒，豌豆，番茄，胡萝卜，小青菜，蒜苗，芹菜，贝壳类(核桃，花生，开心果，腰果，松子，杏仁，大豆)，糙米，猪肝等\n\n" +
                            "心脏：每天吃50g鱼可减少40%心脏病发病率，尤其深海鱼。野生马齿苋\n\n" +

                            "BBC评健康：杏仁，释迦，海鲈鱼，多宝鱼，奇亚籽，南瓜籽，猪乸菜，肥猪肉，甜菜叶，鲷鱼"
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
                            "维生素B：水溶性。爱喝酒、吃东西不全面、吃素的可以补\n\n" +
                            "维生素B1：玉米燕麦豆类\n\n" +
                            "维生素B2：牛奶鸡蛋动物肝脏\n\n" +
                            "维生素B3：新鲜的蔬菜水果\n\n" +
                            "维生素B4：动物内脏、肉类、豆制品、虾、沙丁鱼、蚝、菠菜、黑木耳、鱿鱼、蘑菇等\n\n" +
                            "维生素B5：食物中几乎无所不在\n\n" +
                            "维生素B6：白肉含量较高\n\n" +
                            "维生素B7：动物肝脏、动物肾脏、大豆粉、牛奶、羊奶、蛋黄。水果含量较少\n\n" +
                            "维生素B8：啤酒酵母、动物肝脏、全谷物、哈密瓜、柑橘类水果（柠檬除外）、香蕉、小麦胚芽、坚果、豆类、葡萄干、卷心菜等。人体可自己产生\n\n" +
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
        binding.btnGB.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("执行标准")
                .setMessage(
                    "大米：\n" +
                            "GB/T 1354 普通大米 不好\n" +
                            "GB/T 19266 五常大米\n" +
                            "GB/T 18824 盘锦大米\n" +
                            "GB/T 22438 原阳大米\n\n" +

                            "油：\n" +
                            "GB/T 2716 调和油 不好\n" +
                            "GB/T 23347 橄榄油\n" +
                            "GB/T 1536 菜籽油\n" +
                            "GB/T 1534 花生油\n" +
                            "GB/T 1535 大豆油\n" +
                            "GB/T 19111 玉米油\n" +
                            "GB/T 10464 葵花籽油\n\n" +

                            "面粉：\n" +
                            "GB/T 8607 高筋面粉\n" +
                            "GB/T 1355 中筋面粉\n" +
                            "GB/T 8608 低筋面粉"
                )
                .setCancelable(true)
                .setPositiveButton("确定") { _, _ -> }
                .setNegativeButton("取消") { _, _ -> }
                .show()
        }
    }
}