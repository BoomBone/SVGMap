package com.tic.svgmap

import android.graphics.*

/**
 * Created by Ting on 2019-11-24.
 */
data class ProvinceItem(
    val path: Path,
    val name: String,
    //模块颜色
    val drawColor: Int,
    //显示省份信息
    val clickPoint: PointF

) {

    /**
     * 判断点击区域是否在当前省份
     *
     */
    fun isTouch(x: Float, y: Float): Boolean {
        //获取Path矩形区域
        val rectF = RectF()
        path.computeBounds(rectF, true)
        val region = Region()
        //绘制路径
        region.setPath(
            path, Region(
                rectF.left.toInt(),
                rectF.top.toInt(),
                rectF.right.toInt(),
                rectF.bottom.toInt()
            )
        )
        return region.contains(x.toInt(), y.toInt())
    }


    /**
     * 绘制
     */
    fun drawItem(canvas: Canvas, paint: Paint, isSelect: Boolean) {
        if (isSelect) {
            //绘制内部颜色
            paint.clearShadowLayer()
            paint.strokeWidth = 1f
            paint.color = drawColor
            paint.style = Paint.Style.FILL
            canvas.drawPath(path, paint)
            //绘制边界
            paint.style = Paint.Style.STROKE
            paint.color = Color.YELLOW
            canvas.drawPath(path, paint)
        } else {
            paint.strokeWidth = 2f
            paint.color = Color.BLACK
            paint.style = Paint.Style.FILL
            paint.setShadowLayer(8f, 0f, 0f, Color.WHITE)
            canvas.drawPath(path, paint)

            paint.clearShadowLayer()
            paint.color = drawColor
            paint.style = Paint.Style.FILL
            canvas.drawPath(path, paint)
        }
    }

}