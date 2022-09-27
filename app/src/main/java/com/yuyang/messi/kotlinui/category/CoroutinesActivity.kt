package com.yuyang.messi.kotlinui.category

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenResumed
import com.yuyang.lib_base.ui.header.HeaderLayout
import com.yuyang.lib_base.utils.ToastUtil
import com.yuyang.messi.R
import com.yuyang.messi.ui.base.AppBaseActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.asFlow
import kotlin.coroutines.CoroutineContext

/**
 * https://zhuanlan.zhihu.com/p/135203063
 * https://www.jianshu.com/p/0ec9c85fbf9b
 * https://blog.csdn.net/c10WTiybQ1Ye3/article/details/119465581
 *
 * 两种方式来启动协程：
 * launch：可以启动新协程，但是不将结果返回给调用方。
 * async：可以启动新协程，并且允许使用await暂停函数返回结果。返回的是Deferred接口，继承Job
 *
 * 三种方式创建协程：
 * runBlocking{}线程阻塞的，适用于单元测试，一般业务开发不会使用这种
 * GlobalScope.launch{}，不会阻塞线程，不推荐 生命周期同application，不能取消,容易内存泄漏
 * CoroutineScope(Dispatchers.IO).launch {}
 */
class CoroutinesActivity : AppBaseActivity() {

    public override fun getLayoutId(): Int {
        return R.layout.common_header_recycler
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val headerLayout = findViewById<HeaderLayout>(R.id.headerLayout)
        headerLayout.showTitle(title as String?)
        Thread.currentThread().name
        testCoroutines()
        testSequence()


        lifecycleScope.launch{
            (1..10).asFlow()
                .collect {
                        num->
                }
            whenResumed {  }
        }
        MainScope().launch {  }
//        viewModelScope.launch{
//
//        }
    }
}

fun testCoroutines() {
    CoroutineScope(Dispatchers.Main).launch {
        val sum1 = async { jobbb1() }
        val sum2 = async { jobbb2() }

        val count = sum1.await() + sum2.await()
        ToastUtil.showToast(count.toString())
    }
    ToastUtil.showToast("223")
}

private suspend fun jobbb1(): Int {
    delay(5000)
//    withContext()
    return 1
}

private suspend fun jobbb2(): Int {
    delay(15000)
    return 2
}

fun testSequence() {
    val wordSequence = listOf("The", "quick", "brown", "fox", "jumps", "over", "the", "lazy", "dog")
        .asSequence()//将列表转换为序列

    val lengthsSequence = wordSequence
        .filter { it.length > 3 }
        .map { it.length }
        .take(4)
    //待续
}

class MainActivityFacede : CoroutineScope {
    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    fun destroy() {
        job.cancel()
    }
}