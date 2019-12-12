package com.tic.svgmap

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.PathParser
import androidx.core.graphics.withSave
import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.concurrent.thread
import kotlin.math.abs
import kotlin.math.sqrt
import android.icu.lang.UCharacter.GraphemeClusterBreak.T


/**
 * Created by Ting on 2019-11-24.
 */
class MapView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val colorArray = intArrayOf(-0xdc6429, -0xcf561b, -0x7f340f, -0x1)

    var mPaint: Paint = Paint()
    var itemList = mutableListOf<ProvinceItem>()
    var select: ProvinceItem? = null

    var totalRect: RectF? = null
    private var scale = 1.0f

    companion object {
        const val NONE = 0
        const val DRAG = 1
        const val ZOOM = 2
    }

    var mode = NONE
    //第一个按下的手指的点
    val startPoint = PointF()
    //两个按下手指的触摸点的中点
    var midPoint = PointF()
    //初始两个手指按下的距离
    var oriDis = 1f
    var actionClick = true

    var translateX = 0f
    var translateY = 0f

    //是否显示省份名称
    var shouldShowText = false


    private val loadThread = Thread {
        //获取svg图片输入流
        val inputStream = context.resources.openRawResource(R.raw.unitedkingdom_high)
        //创建解析类DocumentBuilder
        val builderFactory = DocumentBuilderFactory.newInstance()
        var builder: DocumentBuilder? = null
        try {
            builder = builderFactory.newDocumentBuilder()
            //解析输入流，获取Document实例
            val document = builder.parse(inputStream)
            val documentElement = document.documentElement
            //先找到path
            val pathNodeList = documentElement.getElementsByTagName("path")
            var left = -1f
            var right = -1f
            var top = -1f
            var bottom = -1f
            val list = mutableListOf<ProvinceItem>()


            for (i in 0 until pathNodeList.length) {
                val element = pathNodeList.item(i) as Element
                val pathData = element.getAttribute("d")
                val title = element.getAttribute("title")
                //将pathData转换成Path
                val path = PathParser.createPathFromPathData(pathData)
                val proviceItem = ProvinceItem(path, title, colorArray[i % 4])
                val rect = RectF()
                path.computeBounds(rect, true)
                left = if (left == -1f) rect.left else Math.min(left, rect.left)
                right = if (right == -1f) rect.right else Math.max(right, rect.right)
                top = if (top == -1f) rect.top else Math.min(top, rect.top)
                bottom = if (bottom == -1f) rect.bottom else Math.max(bottom, rect.bottom)
                list.add(proviceItem)
            }
            itemList = list
            totalRect = RectF(left, top, right, bottom)
            //                刷新界面
            val handler = Handler(Looper.getMainLooper())
            handler.post {
                requestLayout()
                invalidate()
            }

        } catch (e: Exception) {

        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //获取当前控件的宽高
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        //获取缩放系数，占满整个空间
        if (totalRect != null) {
            //获取地图的宽度
            val mapWidth = if (left < 0) {
                totalRect!!.right - totalRect!!.left
            } else {
                totalRect!!.right + totalRect!!.left
            }

            scale = width / mapWidth
            Log.e(
                "main", "height=$height,width=$width,mapWidth=$mapWidth," +
                        "left=${totalRect?.left},right=${totalRect?.right}"
            )
        }


        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (itemList.size > 0) {
            canvas.save()
            canvas.scale(scale, scale)
            canvas.translate(translateX, translateY)
            for (proviceItem in itemList) {
                if (proviceItem == select) {
                    proviceItem.drawItem(
                        canvas, mPaint, true
                    )
                } else {
                    proviceItem.drawItem(
                        canvas, mPaint, false
                    )
                }
            }
            if (shouldShowText) {
                //绘制文本
                mPaint.color = Color.RED
                mPaint.style = Paint.Style.FILL
                mPaint.textSize = 40f
                if (select != null && select?.clickPoint != null) {
                    canvas.drawText(
                        select!!.name,
                        select!!.clickPoint!!.x,
                        select!!.clickPoint!!.y,
                        mPaint
                    )
                }
            }

            canvas.restore()
        }
    }

    /**
     * 单指点击拖动，双指放大
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        //当前缩放系数
        var currentScaleCount = 0f
        //当前x平移距离
        var currentTranslateX = 0f
        //当前y平移距离
        var currentTranslateY = 0f


        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                //单点触控
                startPoint.set(event.x, event.y)
                mode = DRAG
                actionClick = true

            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                //多点触控
                oriDis = distance(event)
                if (oriDis > 10) {
                    midPoint = midPoint(event)
                    mode = ZOOM
                }
                actionClick = false
            }
            MotionEvent.ACTION_MOVE -> {
                //滑动
                if (mode == DRAG) {
                    //单指拖动
                    if (abs(x - startPoint.x) > 10 || abs(y - startPoint.y) > 10) {
                        currentTranslateX = translateX + x - startPoint.x
                        currentTranslateY = translateY + y - startPoint.y
                        translateX = currentTranslateX
                        translateY = currentTranslateY
                        startPoint.set(x, y)
                        actionClick = false
                        invalidate()
                    }

                } else if (mode == ZOOM) {
                    //两指缩放
                    //当前两指距离
                    val newDist = distance(event)
                    if (abs(newDist - oriDis) > 10) {
                        val scaleInner = newDist / oriDis
                        currentScaleCount = scale + (scaleInner - 1)
                        if (currentScaleCount < 1) {
                            scale = 1f
                        } else {
                            scale = currentScaleCount
                        }

                        oriDis = newDist
                        invalidate()
                    }

                }
            }
            MotionEvent.ACTION_UP -> {
                mode = NONE
                if (actionClick) {
                    handleTouch(x / scale - translateX, y / scale - translateY)
                }
            }

            MotionEvent.ACTION_POINTER_UP -> {
                //多点触控
                mode = NONE
            }
        }
        return true
    }

    private fun handleTouch(x: Float, y: Float) {
        shouldShowText = false
        if (itemList.size > 0) {
            var selectItem: ProvinceItem? = null
            for (provinceItem in itemList) {
                if (provinceItem.isTouch(x, y)) {
                    selectItem = provinceItem
                    provinceItem.clickPoint = PointF(x, y)
                    shouldShowText = true
                }
            }
            if (selectItem != null) {
                select = selectItem
                postInvalidate()
            }
        }
    }


    init {
        mPaint.isAntiAlias = true
        loadThread.start()
    }

    /**
     *
     * 计算连个手指中间点的位置
     * @param event 触摸事件
     * @return 返回中心点坐标
     */
    private fun midPoint(event: MotionEvent): PointF {
        val x = event.getX(0) - event.getX(1) / 2
        val y = event.getY(0) - event.getY(1) / 2
        return PointF(x, y)
    }

    /**
     * 计算两个手指间的距离
     *
     * @param event 触摸事件
     * @return 放回连个手指间的距离
     */
    private fun distance(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return sqrt(x * x + y * y)

    }

}