# SVG矢量图打造不规则自定义控件

Android默认不能直接支持的svg格式的，需要先将文件转换成vector矢量图



### SVG在Android中能做什么

- **APP图标**：在SDK23后，APP的图标都是由SVG来表示
- **自定义控件**：不规则控件，复杂的交互，子控件重叠判断，图表等都可以用SVG来做
- **复杂动画**：如根据用户滑动动态显示动画，路径动画

### SVG语法

- M=moveto(M X,Y) : 将画笔移动到指定的位置
- L = lineto(L X,Y) : 画直线到指定的位置
- H=horizontal(lineto(H X)) : 画水平线到指定的X坐标位置
- V = vectical(lineto(V Y)) ：画垂线到指定的Y坐标位置
- Q = quadratic Belzier curve(Q X,Y,ENDX,ENDY) ： 二次贝塞尔曲线
- C = curveto(Q X1,Y1,X2,Y2,ENDX,ENDY) ：三次贝塞尔曲线
- S = smooth curveto(S X1,Y1,ENDX,ENDY):平滑过度
- Z = closepath():闭合路径

## 示例，完成地图绘制，并且能够正常点击省份

需求分析

1. 显示SVG
2. 各个省份可以单独点击
3. 缩放及拖动

> 下载SVG地图的网站[https://www.amcharts.com/svg-maps/](https://www.amcharts.com/svg-maps/)

注SVG图片转换为xml文件时，会提示报错，根据提示修改SVG文

转xml方法：右键->New->Vector asset

#### 显示SVG

1.如何解析获取svg中的信息？

1.1拆分各个省份：名称，路径信息，颜色

1.2解析文件并在画布中绘制