package com.tic.svgmap

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.PathParser
import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.concurrent.thread

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


    private val loadThread = Thread {
        val inputStream = context.resources.openRawResource(R.raw.unitedkingdom_high)
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

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (itemList.size > 0) {
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
        }
    }

    init {
        mPaint.isAntiAlias = true
        loadThread.start()
    }

}