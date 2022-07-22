package com.example.unlock

import android.content.Context
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ImageView
import kotlinx.coroutines.*
import kotlin.math.sqrt

class PicUnlockView: ViewGroup{

    var callBack:((String)->Unit)? = null

    private val dotSize = dp2px(60)
    private var lineSize = 0
    private var space = 0
    private val dotViews = arrayListOf<ImageView>()
    private var lastSelectedDotView:ImageView? = null
    private val allSelectedDotViews = arrayListOf<ImageView>()
    private val allSelectedLineViews = arrayListOf<ImageView>()
    private val passwordBuilder = StringBuilder()
    private val allLineTags = listOf(
        12,23,45,56,78,89,
        14,25,36,47,58,69,
        24,35,57,68,
        15,26,48,59
    )

    constructor(context: Context):super(context){initUI()}
    constructor(context: Context,attrs:AttributeSet):super(context,attrs){initUI()}
    constructor(context: Context,attrs:AttributeSet,style:Int):super(context,attrs,style){initUI()}

    fun initUI(){
        //创建9个点
        initNineDot()
        //6条横线
        initLandscapeLine()
        //6条竖线
        initVerticalLine()
        //添加斜线
        initSlashLine()
    }

    private fun initSlashLine(){
        for (row in 0..1){
            for (column in 0..1) {
                ImageView(context).also {
                    it.setImageResource(R.drawable.line_left)
                    it.tag = 24+row*33+11*column
                    it.visibility = INVISIBLE
                    addView(it)
                    Log.v("pxd","sl:${it.tag}")
                }
            }
        }
        for (row in 0..1){
            for (column in 0..1) {
                ImageView(context).also {
                    it.setImageResource(R.drawable.line_right)
                    it.tag = 15+row*33+11*column
                    it.visibility = INVISIBLE
                    addView(it)
                    Log.v("pxd","sr:${it.tag}")
                }
            }
        }
    }

    private fun initVerticalLine(){
        for (row in 0..1){
            for (column in 0..2) {
                ImageView(context).also {
                    it.setImageResource(R.drawable.line_vertical)
                    it.tag = 14+row*33+11*column
                    it.visibility = INVISIBLE
                    addView(it)
                    Log.v("pxd","v:${it.tag}")
                }
            }
        }
    }

    fun initLandscapeLine(){
        for (row in 0..2){
            for (column in 0..1) {
                ImageView(context).also {
                    it.setImageResource(R.drawable.line_horizontal)
                    it.tag = 12+row*33+11*column
                    it.visibility = INVISIBLE
                    addView(it)
                    Log.v("pxd","h:${it.tag}")
                }
            }
        }
    }

    private fun initNineDot(){
        for (i in 1..9){
            ImageView(context).apply {
                setImageResource(R.drawable.dot_normal)
                tag = "$i"
                addView(this)
                dotViews.add(this)
            }
        }
    }

    //对9个点进行布局
    private fun layoutNineDot(){
        for (row in 0..2){
            for (column in 0..2){
                val left = column*(dotSize+space)
                val top = row*(dotSize+space)
                val right = left + dotSize
                val bottom = top + dotSize

                val index = row*3+column
                val dotView = getChildAt(index)
                dotView.layout(left,top,right,bottom)
            }
        }
    }

    private fun layoutHorizontalLine(){
        for (row in 0..2){
            for (column in 0..1){
                val left = dotSize + column*(space+dotSize)
                val top = dotSize/2 + row*(space+dotSize)
                val right = left + lineSize
                val bottom = top + dp2px(2)

                //找到这根线在父容器里面的索引
                val index = 9 + row*2 + column
                val lineView = getChildAt(index)
                lineView.layout(left,top,right,bottom)
            }
        }
    }

    private fun layoutSlashLine(){
        for (row in 0..1){
            for (column in 0..1){

                val left = dotSize/2.0 + dotSize* sqrt(2.0) /4 + column*(space+dotSize)
                val top = dotSize/2.0+dotSize* sqrt(2.0) /4 + row*(space+dotSize)
                val right = left + space+(1- sqrt(2.0)/2)*dotSize
                val bottom = top + space+(1- sqrt(2.0)/2)*dotSize

                //找到这根线在父容器里面的索引
                val index = 21 + row*2 + column
                val lineView = getChildAt(index)
                lineView.layout(left.toInt(),top.toInt(),right.toInt(),bottom.toInt())
            }
        }

        for (row in 0..1){
            for (column in 0..1){

                val left = dotSize/2.0 + dotSize* sqrt(2.0) /4 + column*(space+dotSize)
                val top = dotSize/2.0+dotSize* sqrt(2.0) /4 + row*(space+dotSize)
                val right = left + space+(1- sqrt(2.0)/2)*dotSize
                val bottom = top + space+(1- sqrt(2.0)/2)*dotSize

                //找到这根线在父容器里面的索引
                val index = 25 + row*2 + column
                val lineView = getChildAt(index)
                lineView.layout(left.toInt(),top.toInt(),right.toInt(),bottom.toInt())
            }
        }
    }

    private fun layoutVerticalLine(){
        for (row in 0..1){
            for (column in 0..2){
                val left = dotSize/2 + column*(space+dotSize)
                val top = dotSize + row*(space+dotSize)
                val right = left + dp2px(2)
                val bottom = top + lineSize

                //找到这根线在父容器里面的索引
                val index = 15 + row*3 + column
                val lineView = getChildAt(index)
                lineView.layout(left,top,right,bottom)
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        //计算两个点之间的尺寸
        space  =  (width - 3*dotSize)/2
        lineSize = space
    }

    //定义自己的规则
    //这个方法里面不要去大量创建或者运算
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        layoutNineDot()
        layoutHorizontalLine()
        layoutVerticalLine()
        layoutSlashLine()
    }

    //dp -> px
    fun dp2px(dp:Int) = (resources.displayMetrics.density*dp).toInt()

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action){
            MotionEvent.ACTION_DOWN ->{dealWithTouchPoint(event.x,event.y)}
            MotionEvent.ACTION_MOVE ->{dealWithTouchPoint(event.x,event.y)}
            MotionEvent.ACTION_UP ->{dealWithResult()}
        }
        return true
    }

    private fun dealWithResult(){
        callBack ?.let {
            it(passwordBuilder.toString())
        }
    }

    private fun dealWithTouchPoint(x:Float,y:Float){
        //遍历9个点 判断触摸点是否在某个点中
        dotViews.forEach { currentDotView ->
            //获取这个View的Rect区域
            val rect = RectF(
                currentDotView.x,
                currentDotView.y,
                currentDotView.x + currentDotView.width,
                currentDotView.y + currentDotView.height
            )
            //判断触摸点是否在rect区域
            if (rect.contains(x,y)){
                //判断是不是第一个点
                if (lastSelectedDotView == null){
                    //直接点亮
                    changeSelectedDotViewStatus(currentDotView,ViewState.SELECTED)
                }else {
                    //判断是否已经点亮过
                    if (!allSelectedDotViews.contains(currentDotView)){
                        //判断是否有路径 上一个点的tag和当前这个点的tag形成线的tag
                        val lastTag = (lastSelectedDotView!!.tag as String).toInt()
                        val currentTag = (currentDotView!!.tag as String).toInt()
                        //获取亮点间可能的线的tag
                        val lineTag = if (lastTag < currentTag) {
                            lastTag*10+currentTag
                        } else {
                            currentTag*10+lastTag
                        }

                        //判断tags数组中是否有这个值
                        if (allLineTags.contains(lineTag)){
                            //存在这条线 使用tag从父容器中获取这个子控件
                            val lineView = findViewWithTag<ImageView>(lineTag)
                            //点亮点
                            changeSelectedDotViewStatus(currentDotView,ViewState.SELECTED)
                            //点亮线
                            changeSelectedLineStatus(lineView,ViewState.SELECTED)
                        }
                    }
                }
            }
        }
    }

    //切换状态
    private fun changeSelectedDotViewStatus(view: ImageView, state: ViewState){
        if (state == ViewState.NORMAL){
            view.setImageResource(R.drawable.dot_normal)
        }else{
            view.setImageResource(R.drawable.dot_selected)
            lastSelectedDotView = view
            allSelectedDotViews.add(view)
            passwordBuilder.append(view.tag as String)
        }
    }

    //切换线的状态
    private fun changeSelectedLineStatus(line:ImageView, state: ViewState){
        if (state == ViewState.NORMAL){
            line.visibility = INVISIBLE
        }else{
            line.visibility = VISIBLE
            allSelectedLineViews.add(line)
        }
    }

    fun clear(){
        //启动一个协程 在子线程中执行
        CoroutineScope(Dispatchers.IO).launch {
            //延迟1s
            delay(1000)
            //切换到主线程中
            withContext(Dispatchers.Main){
                //点亮的点
                allSelectedDotViews.forEach {
                    changeSelectedDotViewStatus(it,ViewState.NORMAL)
                }
                //点亮的线
                allSelectedLineViews.forEach {
                    changeSelectedLineStatus(it,ViewState.NORMAL)
                }
                //各种数组清空
                lastSelectedDotView = null
                allSelectedLineViews.clear()
                allSelectedDotViews.clear()
                passwordBuilder.clear()
            }
        }
    }

    enum class ViewState{
        NORMAL,SELECTED
    }

    interface UnlockListener{
        fun passwordPicDidFinished(password:String)
    }
}