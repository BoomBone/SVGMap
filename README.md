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

1.3点击事件处理（缩放 平移）

SVG文件示例（就是我们平时常见的xml文件)
```
<?xml version="1.0" encoding="utf-8"?>
<!-- (c) ammap.com | SVG map of United Kingdom - High -->
<svg xmlns="http://www.w3.org/2000/svg"
	viewBox="0 0 1000 1400"
	xmlns:amcharts="http://amcharts.com/ammap" xmlns:xlink="http://www.w3.org/1999/xlink" version="1.1">
	<defs>
		<style type="text/css">
			.land
			{
				fill: #ff0000;
				fill-opacity: 1;
				stroke:white;
				stroke-opacity: 1;
				stroke-width:0.5;
			}
		</style>

		<amcharts:ammap projection="mercator" leftLongitude="-10.476361" topLatitude="60.846142" rightLongitude="1.765083" bottomLatitude="49.162600"></amcharts:ammap>

		<!-- All areas are listed in the line below. You can use this list in your script. -->
		<!--{id:"GB-UKC"},{id:"GB-UKD"},{id:"GB-UKE"},{id:"GB-UKF"},{id:"GB-UKG"},{id:"GB-UKH"},{id:"GB-UKI"},{id:"GB-UKJ"},{id:"GB-UKK"},{id:"GB-UKL"},{id:"GB-UKM"},{id:"GB-UKN"},{id:"GG"},{id:"JE"},{id:"IM"},{id:"IE"}-->

	</defs>
	<g>
		<path id="GB-UKC" title="North East" class="land" d="M609.34,675.68l0.03,0.08h0.26l0.11,-0.18l0.11,-0.02l-0.06,0.3l0.29,0.13h0.11l0.17,-0.13h0.26l0.03,0.1l-0.2,0.33l-0.03,0.3l-0.06,0.08l0.03,0.65l0.14,0.2l-0.03,0.08l-0.4,-0.13h-0.29l-0.11,-0.1l-0.14,0.03l0.03,0.13l-0.37,0.03l-0.23,-0.25v-0.65l-0.17,-0.23l-0.2,-0.03l-0.29,-0.18h-0.6l-0.28,0.18l-0.11,0.28l-0.34,0.03l-0.34,-0.25l-0.14,-0.4l0.06,-0.3l0.11,-0.08h0.37l0.31,0.1l0.26,0.18l0.34,0.08l0.77,-0.05l0.14,-0.15v-0.08l0.23,-0.08l0.14,-0.15h0.2L609.34,675.68zM597.56,665.15l0.11,0.11h0.11l0.2,0.13l0.11,0.28l0.28,0.33l0.03,0.25l0.11,0.1l0.31,0.1v0.18l0.23,0.15v0.43l0.31,0.18v0.18l-0.09,0.05v0.13l0.2,0.15l-0.03,0.25l0.17,0.2v0.13l-0.08,0.1h-0.29l-0.2,0.13l-0.23,0.05v0.2l0.14,0.13h0.17l0.03,0.05h0.14l0.09,-0.08l0.08,0.03l0.17,0.53l0.43,0.5l0.23,0.18l0.08,0.18l0.09,0.03l0.11,0.35l0.4,0.28l0.09,0.4l0.17,0.23l0.46,0.35l0.14,0.25l0.6,0.63l0.68,0.45l0.14,0.18l0.08,0.28l0.26,0.25l0.4,0.13l0.4,0.35l0.26,0.1l0.08,0.13l0.11,0.05l0.37,0.63l0.11,0.1l0.26,0.5l0.11,0.78l0.4,0.68l0.03,0.38l0.11,0.38l0.14,0.25l0.23,0.23l0.31,0.13l0.31,0.58l-0.06,0.1l0.03,0.18l0.17,0.02l0.34,0.3l0.63,0.23l0.28,0.2h0.29l0.37,-0.35l0.4,-1.08l0.06,-0.1l0.09,-0.03l-0.03,-0.25l-0.17,-0.18l0.08,-0.15h0.11l0.11,0.13l0.31,1.36l0.91,1.33l-0.03,0.33l-0.29,0.5l-0.17,0.13l-0.51,-0.02l0.03,0.1l0.17,0.03l0.14,0.1l0.09,0.25l0.34,0.55l-0.03,0.13l0.29,-0.08l0.23,-0.3v-0.25l0.06,-0.08l0.11,-0.05l0.11,-0.15l0.2,-0.05l0.37,-0.38l0.28,-0.02l0.2,0.13l0.29,-0.02l0.11,0.08h0.17l0.06,0.05l0.03,0.33l1.02,0.38l0.23,0.17l0.14,0.03l0.23,0.4l0.43,0.4l0.03,0.15l0.26,0.15l0.43,0.75l0.17,0.1l0.6,0.15l0.08,0.1l0.17,0.03l0.03,0.1h0.11l0.23,0.18v0.13l-0.06,0.05l-0.06,0.05h-0.2l-0.06,0.1l0.06,0.4l0.17,0.38l0.31,0.3l0.14,0.38l0.23,0.2l0.06,0.25l0.17,0.07v0.1h0.29l-0.23,0.2h-0.14l-0.09,-0.15h-0.14l-0.11,0.08l-0.2,0.78l-0.14,0.18v0.18l0.17,0.03l0.06,0.2l0.09,0.08l0.03,0.23l0.17,0.17l0.06,0.15l0.31,0.2h0.17l-0.09,0.15l0.03,0.2l0.46,0.08l0.03,0.08l-0.4,0.58v0.2l0.11,0.17l0.09,0.03v0.28l-0.11,0.28v0.38l0.06,0.15l0.34,0.33h0.37l0.23,0.25h0.2l0.06,0.1v0.53l-0.09,0.05l0.11,0.48l-0.06,0.33l0.03,0.6l0.14,0.2l0.06,0.22v0.25l-0.11,0.05l-0.03,0.2l-0.06,0.05v0.23l0.2,0.23l0.08,0.35l-0.03,0.2l-0.17,0.23l-0.03,0.17l-0.08,0.1v0.18l0.26,0.45l0.17,0.05l0.28,0.22l0.03,0.4l-0.09,0.18l-0.03,0.35l-0.06,0.17l-0.06,0.03l0.03,0.38l0.14,0.35v0.33l-0.14,0.13l-0.23,-0.03l-0.23,0.15l-0.2,0.4l-0.14,0.55l-0.23,0.08l-0.26,0.23l-0.17,0.22l0.03,0.17l-0.09,0.08h-0.11l-0.11,-0.13l-0.28,-0.15l0.2,-0.1l-0.03,-0.08l-0.26,0.03l-0.03,0.13l0.14,0.15l0.14,0.05v0.17l0.17,0.38l0.08,-0.05l-0.03,-0.35l0.23,-0.02l0.06,0.6l0.06,0.08v0.2l0.06,0.05l0.03,0.45l0.17,0.55l0.03,0.3l0.14,0.2v0.15l0.34,0.72l0.51,0.8v0.05h-0.06l-0.4,-0.18l-0.17,0.1v0.05l0.23,0.15l0.31,0.08h0.26l0.14,-0.08l0.03,0.1h0.14l0.03,-0.08h0.09l0.06,0.2l0.31,0.5l0.37,0.25l0.26,0.38l0.03,0.97l-0.06,0.13l-0.17,0.07l-0.23,0.25v0.1l-0.29,0.65l-0.06,0.03l-0.26,0.67l-0.06,1.35l0.6,2.04l0.2,0.32l0.14,0.37l0.31,0.47l0.4,0.25l0.31,0.3l0.23,0.62l-0.03,0.65l0.11,0.32l0.54,0.97l0.29,0.35l0.03,0.12l0.4,0.2v0.15l-0.08,0.15v0.32l0.17,0.3l0.08,0.05l-0.06,0.15l-0.17,0.05l-0.06,-0.07h-0.14l-0.26,0.2l-0.08,0.17v0.37l0.08,0.17l-0.37,0.42l-0.26,0.67l-0.4,-0.05l-0.2,0.1l-0.23,0.03l-0.51,-0.22l-0.51,-0.03l-0.08,-0.05h-0.09v0.05l0.26,0.2l0.37,-0.05l0.06,0.05l0.31,0.03l0.17,0.1l0.6,-0.05l0.26,0.22l0.11,0.7l0.17,0.5l0.71,1.09l0.43,0.4l0.11,0.32l0.17,0.25l-0.03,0.15l-0.11,-0.25h-0.08v0.1l0.14,0.17l0.03,0.15l-0.14,0.1l-0.17,0.52l0.09,0.7l0.17,0.4l0.06,0.03l0.26,0.55l0.43,0.6l0.14,0.12l0.29,-0.02l0.11,0.07l-0.03,0.37l0.29,0.07l0.06,0.1v0.27l0.09,0.12l0.2,0.1l0.03,0.27h0.2l0.06,0.05v0.27l-0.17,0.12l0.17,0.87l0.51,0.92l0.37,0.3l0.23,0.1l-0.03,0.1l0.14,0.12l-0.14,0.45l0.08,0.3l0.11,0.17l0.03,0.35l0.14,0.1h0.17l0.03,0.4h0.23v0.07l0.11,0.1h0.17l0.31,0.15v0.05h-0.06l-0.28,-0.07l-0.09,-0.07h-0.17l-0.08,0.17l-0.09,0.05h-0.54l-0.11,0.37l0.06,0.15h0.23l0.06,0.27l0.14,0.15l0.11,0.02l0.06,-0.12l0.14,-0.05l0.23,-0.32l0.2,-0.05l-0.43,0.45l0.09,0.37l0.11,0.03l0.03,0.12l0.11,0.05l0.03,0.12l0.14,0.07v0.07l0.28,0.05l0.09,0.15l0.26,0.02l0.03,0.2l0.06,0.05h0.2v0.05l0.11,0.03l0.09,0.1l0.2,0.03l0.06,0.2l-0.06,0.05l-0.03,0.35l0.2,0.22h0.14l0.43,0.4l0.34,0.1v0.35l0.11,0.07l0.06,0.15h0.06l-0.06,0.05l0.03,0.32l0.14,0.45l-0.08,0.05l-0.09,0.22l-0.03,0.45l-0.11,0.05v0.1l-0.09,0.1l-0.2,0.12l-0.2,0.74l0.06,0.32l0.11,0.05l0.06,0.17l-0.17,0.1l0.09,0.27l-0.03,0.22h0.48l0.29,0.17l-0.09,0.02l-0.31,-0.15l-0.31,0.03l0.17,0.37l-0.03,0.1l0.06,-0.02l0.06,0.12v0.15l0.23,-0.07l0.14,-0.35l0.09,-0.02l-0.14,0.37l-0.2,0.17l0.06,0.17l0.14,0.07v0.17l0.14,0.15v0.12l-0.09,0.15l-0.17,0.1l-0.06,0.15l-0.14,0.1l-0.06,0.22l0.06,0.05l0.03,0.32l-0.06,0.02l0.03,0.07l-0.06,0.1l0.14,0.32l-0.03,0.07l0.11,0.1l-0.06,0.05v0.15l0.11,0.05l0.08,0.17l-0.08,0.25l0.06,0.12l-0.06,0.07l0.03,0.25l0.11,0.15l0.11,0.42l0.08,0.05l0.09,0.17l-0.03,0.39l0.03,0.12l0.06,0.02l0.03,0.3l0.09,0.2l0.14,0.1v0.12L632.8,751l0.23,0.12l-0.06,0.1l0.2,0.22v0.1l0.06,0.05l0.2,-0.02l0.09,0.12v0.15l0.08,0.05l-0.03,0.05l0.14,0.12v0.12l0.06,0.02l-0.06,0.32l0.08,0.15l0.03,0.34l0.08,0.1l0.11,0.02l0.03,0.07l-0.11,0.39l0.11,0.3l0.08,0.07v0.1l0.2,0.15v0.15l0.09,0.12l-0.03,0.32l0.06,0.07v0.34l-0.06,0.1l0.14,0.07v0.22l-0.06,0.02l-0.03,0.25v0.39l0.06,0.17l0.09,0.05v0.22l-0.09,0.22l0.14,0.15v0.25l0.08,0.07l0.06,0.25l0.08,0.07l0.03,0.2l0.11,0.12l-0.03,0.07l0.17,0.27v0.15l0.26,0.15l0.03,0.15l0.11,0.1v0.12l0.11,0.05l0.03,0.12l0.37,0.29l-0.03,0.07l0.09,0.05l0.11,0.25l0.34,0.2l0.06,0.2l0.08,0.05v0.15l0.14,0.12l0.03,0.2l0.37,0.37l-0.03,0.12l0.11,0.02l0.06,0.2l0.2,0.17l0.06,0.17l0.37,0.39l0.03,0.1l0.71,0.62l0.17,0.02l0.2,0.12l0.06,0.1l0.26,0.1l0.31,0.25l0.31,0.1l0.08,0.1h0.11l0.46,0.32l0.37,0.1l0.23,0.27l-0.03,0.2l-0.11,0.15l-0.34,0.02l-0.03,-0.07l0.06,-0.05l-0.23,-0.07l-0.06,-0.29l-0.11,-0.07l-0.08,0.03l-0.03,0.1l0.09,0.02l0.06,0.17l0.11,0.12l-0.06,0.03v0.12l-0.34,0.1l0.03,0.2l0.14,0.07l0.06,0.1l-0.14,0.17l-0.14,0.07v0.42l0.06,0.07v0.15l0.08,0.05v0.15l0.23,0.25l0.08,0.34l0.14,0.2l-0.03,0.2l0.14,0.25v0.17l0.17,0.05v0.29l0.11,0.05l0.08,0.15h0.09l0.2,0.32l0.14,0.07l0.14,-0.05l0.11,0.2l0.26,-0.05l-0.08,0.17l-0.31,-0.03l-0.08,0.05v0.74l0.37,0.07l0.03,0.12l-0.2,0.29l-0.11,-0.02v-0.1h0.09l0.06,-0.12l-0.17,-0.2h-0.08l-0.14,0.2l0.11,0.1v0.07l-0.68,-0.02l-0.26,0.05l-0.06,0.12l-0.14,0.1v0.1l-0.06,-0.02l-0.11,0.07l-0.06,0.22l-0.14,0.1l-0.03,0.2l-0.06,0.05l1.51,0.05l0.23,-0.37v-0.07l-0.08,-0.03v-0.27l0.23,0.07v0.37l-0.11,0.07v0.07l-0.17,0.1l0.11,0.12l0.28,-0.32l0.03,-0.12l0.37,0.02l0.06,-0.05l0.06,-0.25l0.43,0.07l0.17,-0.02l0.09,-0.07l0.08,0.1l0.09,-0.1l0.03,-0.2l-0.31,-0.22l-0.06,-0.39l0.06,-0.05v-0.59l0.06,-0.02l0.06,-0.17l0.11,-0.05l-0.11,0.44v0.34l0.63,0.44l0.2,0.37l0.88,0.52l0.68,0.22l0.11,0.1l0.6,0.02l0.26,-0.07l0.37,0.2l0.28,0.02l0.09,0.1l0.31,0.07l0.06,0.22l0.23,0.22l0.06,0.25l0.09,-0.02l0.08,0.1l0.09,-0.02l0.03,0.12l0.31,0.15l0.31,0.29l0.74,0.42l0.48,0.2l0.08,0.1h0.2l0.09,0.07l0.4,0.1l0.97,0.44l0.37,-0.02l0.2,0.1h0.14l0.08,-0.1l0.23,0.02l0.34,-0.22h0.23l0.31,0.17l0.37,0.02l0.09,0.1l0.23,0.1l0.14,0.17l0.03,0.15l0.83,0.64l0.06,0.17h0.23l0.03,0.05l0.23,-0.05l0.31,0.03l0.28,0.15h0.34l0.51,-0.17l0.6,0.05l0.88,0.39l0.2,0.15l0.2,0.27l0.23,0.02l0.4,0.2h0.11l0.06,0.07h0.31l0.11,-0.07l0.26,0.02l0.2,-0.07l0.08,0.12l0.37,-0.02l0.09,0.12l0.13,0.05l0,0l-0.49,0.89l-1.35,0.59l-0.75,0.93l-0.43,1.25l-0.03,2.52l-2.18,-0.76l-2.65,0.81l-1.17,-0.95l-1.07,-0.22l-2.72,0.66l-2.32,-1.1l-2.04,0.61l-4.48,-0.69l-1.71,0.54l-0.68,1.08l-4.01,2.22l-0.34,-0.46h-1.47l-0.4,-1.88l-0.63,-0.41l-0.31,0.41l-1.04,-0.32l-0.43,1.29l-0.78,-0.3l-0.21,-0.83l-0.55,-0.64l-0.64,-0.07l-0.38,0.39l0.01,1l1.05,2.79l-0.16,0.59l-0.57,0.24l-0.61,-0.56l-0.63,-2.03l-0.54,-0.68l-1.57,-0.17l0.27,1.25l-0.63,0.12l-2.32,-2.15l-0.67,-1.66l-0.48,-0.34l-1.52,0.37l-0.27,-0.12l-0.36,-1.2l-1.03,-0.66l-2.63,-0.08l-0.57,0.59l-1.24,-0.49l-0.93,0.17l-0.01,1.3l-0.63,1.13l-0.4,1.71l-1.91,-1.86l-0.58,-0.15l-0.71,0.98l-0.06,1.13l-4.08,2.27l-1.31,0.02l-2.61,-1.59l-1.98,-0.63L594,784.7l-2.01,1.61l-0.83,-0.53l0,0l0.25,-1.53l-0.4,-1.1l-0.26,-3.74l-1.47,-0.29l-0.91,-1.84l-3.89,-3.16l-0.57,-1.32l-0.47,-2.18l1,-1.1l0.36,-0.88l-0.07,-0.66l-0.53,-0.71l-2.76,-1.45l-0.47,-0.76l1.37,-3.25l1.14,-4.94l-2.92,-2.44l-1.95,-3.23l-0.63,0.15l-0.95,1.11l-3.54,2.47l-1.72,-0.25l-1.54,-1.38l-0.34,-1.13l0.36,-0.94l-0.16,-1.01l-1.08,-1.31l-0.31,-0.86l1.2,-0.91l0.56,-2.03l0.91,-0.64l-0.8,-1.29l-0.4,-1.81l-1.38,-0.79l1.24,-1.73l0.04,-1.76l0.36,-0.54l4,-2.43l-0.65,-1.69l0.17,-0.65l-0.23,-0.59l0.54,-0.67l-1.03,-0.67l-2.15,0.62l-0.4,-0.84l-1.67,-1.22l-0.53,-1.84l-2.21,-0.77l-0.68,-0.99l-1.2,-3.8l-0.42,-0.49l0,0l1.33,-2.24l1.72,-0.62l0.4,-1.87l-0.63,-0.97l0.11,-0.95l2.99,-2.39l0.91,-2.09l1.51,-0.9l1.34,-1.75l1.72,-1.45l1.81,-0.52l1.31,0.5l0.88,1.55l1.81,-1.85l0.41,-1.85l0.56,-1.17l1.88,-0.42l1.07,-1.5l2.93,-0.5l1.72,-3.03l-0.21,-0.95l-1.4,-1.88l-0.16,-1.73l-2.09,-3.61l-1.05,-3.64l-1.18,-1.36l0.1,-0.83l-1.35,-0.75l-0.26,-1.66l4.73,-1.03l0.95,-1.18l1.32,-2.59l0.87,-0.81l0.24,-1.51l1.75,-1.59l1.11,-0.48l1.44,-1.5l0.42,-3.27L597.56,665.15z"/>
		<path id="GB-UKD" title="North West" class="land" d="M534.79,812.32l0.17,0.07v0.17l-0.17,-0.02l-0.17,0.15l-0.26,0.07l-0.11,0.41l0.08,0.27l0.17,0.17v0.22l0.26,0.1l0.03,0.19l0.11,0.1l0.2,0.75l-0.03,0.61l0.09,0.24l0.14,0.14l-0.03,0.39l0.09,0.46l0.08,0.17v0.07l-0.11,0.1v0.24l-0.06,0.12l0.26,0.1l0.11,0.15l0.14,0.07l0.03,0.19l0.17,-0.1l0.17,0.07v0.19l-0.08,0.02l-0.03,0.12l0.23,0.02l0.17,0.29l-0.09,0.17l0.03,0.19l0.06,0.02l-0.06,0.22l0.03,0.17l0.28,0.46l0.2,-0.02l0.2,0.12l0.03,0.22l0.09,0.07l0.03,0.17v0.51l0.17,0.24l0.2,0.07l0.11,0.15h0.08l0.2,-0.05l0.77,-0.48l0.09,0.12l0.03,0.19l0.08,0.02l-0.03,-0.36l0.09,-0.02l0.14,0.07l0.03,0.15l-0.2,0.53h-0.34l-0.03,0.05l-0.08,-0.02l-0.14,0.07l-0.4,0.05l-0.08,0.07l-0.48,-0.05l-0.26,-0.15l-0.06,-0.15l-0.2,-0.22v-0.1l-0.17,-0.17l-0.14,-0.46l-0.11,-0.17l-0.14,-0.07l-0.4,-0.7l-0.14,-0.12l-0.23,-0.41l-0.2,-0.17l-0.8,-1.43l-0.06,-0.31l-0.14,-0.17l-0.06,-0.29l-0.26,-0.44l-0.06,-0.39l-0.14,-0.29l-0.03,-0.34l0.08,-0.02v-0.27l-0.08,-0.1l-0.03,-0.41l-0.11,-0.27l-0.03,-1.02l0.14,-0.17v-0.1l0.23,-0.22l0.37,-0.12l0.23,-0.19H534.79zM563.98,720.49l0.42,0.49l1.2,3.8l0.68,0.99l2.21,0.77l0.53,1.84l1.67,1.22l0.4,0.84l2.15,-0.62l1.03,0.67l-0.54,0.67l0.23,0.59l-0.17,0.65l0.65,1.69l-4,2.43l-0.36,0.54l-0.04,1.76l-1.24,1.73l1.38,0.79l0.4,1.81l0.8,1.29l-0.91,0.64l-0.56,2.03l-1.2,0.91l0.31,0.86l1.08,1.31l0.16,1.01l-0.36,0.94l0.34,1.13l1.54,1.38l1.72,0.25l3.54,-2.47l0.95,-1.11l0.63,-0.15l1.95,3.23l2.92,2.44l-1.14,4.94l-1.37,3.25l0.47,0.76l2.76,1.45l0.53,0.71l0.07,0.66l-0.36,0.88l-1,1.1l0.47,2.18l0.57,1.32l3.89,3.16l0.91,1.84l1.47,0.29l0.26,3.74l0.4,1.1l-0.25,1.53l0,0l-0.67,-0.35l-1.32,1.15l-2.59,-0.12l-1.64,1.1l-0.97,1.56l0.63,2.02v1.05l-3.27,2.85l0.31,0.71l1.45,1.07l0.34,0.83l0.03,2.46l-0.3,0.97l0.57,1.41l-0.18,1.46l-0.3,0.46l-1.01,0.22l-1.18,-0.85l-1.55,0.78l-0.91,1.17l-2.41,-0.17l-0.3,1.14l-2.99,3.98l-0.37,1l-1.47,0.8l-0.2,1.99l0.33,1.28l1.32,1.43l2.63,1.53l0.43,0.7l0.06,2.13l0.5,0.63l1.11,0.17l2.38,-0.65l1.32,0.27l0.3,0.48l0.16,1.31l0.46,0.34l-0.54,1.38l0.64,0.97l1.79,0.17l0.65,1.57l0.67,0.1l2.39,-1.11l-0.04,0.87l0.31,0.41l1.62,0.19l0.61,0.97l0.23,1.62l3.54,1.86l0.38,2.27l1.61,2.24l1.72,1.31l-0.24,1.24l-0.51,0.77l-2.48,1.2l-1.28,1.66l-0.04,3.12l-1.96,2.57l0.54,1.75l-0.11,0.75l0.47,0.21l0.54,1.03l0.7,-0.07l1,0.96l0.9,-0.24l0.83,-0.77l0.95,-0.05l0.14,0.89l0.63,0.91l-0.19,1.41l0.57,0.65l0.36,1.8l1.48,0.33l0.57,1.79l2.01,2.22l2.08,3.01l0,0l-0.66,1.08l-0.46,1.7l-0.7,0.43l-1.59,0.1l-0.73,2.22l-0.24,2.15l-1.37,1.41l-0.2,0.64l0.07,0.64l1.08,0.52l0.47,0.69l-1.18,2.07l-0.63,0.32l-0.1,0.61l0.9,1.38l0.16,0.97l0.13,6.13l0.26,1.92l1.01,0.97l-0.31,1.96l0,0l-1.47,1.82l-1.28,0.12l-1.94,1.61h-1.71l-1.2,-0.78l-0.67,0.1l-0.1,0.33l0.47,1.04l-0.2,0.64l-1.18,0.24l-4.54,5.42l-1.32,0.54l-1.71,-0.14l-1.54,1.96l-1.55,0.33l-0.41,1.28l0.38,3.14l-0.63,0.91l-2.3,0.93l-0.37,1.16l-0.95,0.68l-1.51,0.24l-1.74,-0.64l-0.74,1.23l-1.58,-0.78l-1.01,0.24l-0.6,-1.98l-1.71,-0.75l-5.38,1.63l0,0l-2.39,-1.56l-1.96,0.07l-0.75,-1.53l-1.69,-1.67l0.18,-0.9l-0.38,-1.79l-1.59,-4.01l0.08,-0.29l-2.28,-1.75l-1.07,-1.92l0.08,-0.38l2.48,-1.68l-0.17,-1.28l-2.19,-2.49l-2.36,-2.04l-1.84,-1l-2.25,0.05l-0.38,-0.81l-0.2,0.28l0,0l-0.06,-0.13l0.09,-0.05v-0.07l-0.17,0.12v-0.09l-0.14,-0.14l-0.03,-0.14h-0.09l-0.06,-0.09l-0.09,-0.62l0.46,-0.02l0.06,0.17h0.06l-0.06,-0.19l0.29,-0.02l0.03,-0.07l-0.26,0.02l-0.14,-0.12l-0.17,-0.02l-0.06,0.05l-0.09,-0.07l0.17,-0.09l0.08,-0.14l0.14,0.02l0.17,0.19h0.09l0.03,-0.1l-0.09,0.02v-0.09l0.09,-0.19l0.2,0.07l0.34,-0.05l0.03,-0.05l0.11,0.02v-0.07l-0.14,-0.02l-0.11,0.07l-0.57,-0.1l-0.2,0.1l0.06,-0.12l-0.06,-0.14l0.17,-0.07l0.08,0.02l0.03,-0.14l-0.31,0.05l-0.09,0.17l-0.17,0.02l-0.08,-0.09l-0.17,-0.05l-0.03,-0.12l0.14,0.02v-0.05h0.11l0.06,-0.14l-0.2,0.07l-0.11,-0.02l0.03,-0.14l-0.11,0.09l-0.17,-0.09l-0.03,-0.24l-0.08,-0.12v-0.19h0.46l0.06,-0.02v-0.09l-0.34,0.02l0.06,-0.17l-0.26,0.12l-0.23,-0.33l-0.06,-0.19v-0.12l0.09,-0.07l-0.29,-0.07v-0.26l-0.08,-0.07l-0.06,-0.21l0.03,-0.4l-0.09,-0.17l-0.14,-0.09l-0.03,-0.57l-0.17,-0.12l0.17,-0.02l0.09,0.12h0.08v-0.07l-0.28,-0.29l-0.09,-0.02l-0.71,-0.95l-0.17,-0.07l-0.26,-0.29l-0.17,-0.05l-0.43,-0.48l-0.14,-0.07l-0.14,-0.24l-0.11,-0.4l-0.14,-0.19l-0.29,-0.19l-0.06,-0.24l-0.2,-0.24l-0.03,-0.17l-0.06,-0.02v-0.17l-0.11,-0.07l-0.14,-0.6l-0.08,-0.09v-0.1l-0.14,-0.07l-0.03,-0.14l0.2,-0.05l0.4,-0.33l0.11,-0.24h0.06l0.31,-0.36v-0.1l0.31,-0.14l0.2,-0.19h0.09l0.26,-0.19h0.11l0.17,-0.17l0.34,-0.07l0.08,-0.1l0.29,-0.09l0.2,-0.14l0.23,-0.05l0.17,-0.17l0.37,-0.05l0.43,-0.26l0.2,-0.05l0.23,-0.19h0.2l0.23,-0.1l0.17,-0.21l0.14,-0.02l0.2,-0.26l0.26,-0.05l-0.06,-0.1l0.23,-0.09l0.28,-0.33l0.74,-0.14l0.11,-0.14l0.54,-0.17l0.17,-0.12h0.31l0.11,0.07l0.08,0.21l0.06,0.02v0.33l0.14,0.24l-0.03,0.17l0.14,0.12l0.06,0.45l0.11,0.17l-0.03,0.1l0.26,0.33v0.09l0.09,0.02l0.14,0.31l0.11,0.02v0.48l-0.4,0.17l-0.11,-0.07l-0.08,0.05l-0.34,-0.17l-0.57,0.09l-0.14,-0.09l-0.03,-0.12h-0.09l-0.06,-0.12h-0.06l0.08,0.24l0.29,0.17h0.26l0.11,-0.07l0.28,0.02l0.26,0.14l0.11,0.19h0.06v0.1l0.11,0.07h0.14v-0.07l0.14,-0.05h0.23v0.17l0.14,0.14l-0.06,0.45l0.17,0.67l0.14,0.12l0.09,0.33l0.2,-0.02l0.03,0.21l-0.09,0.05v0.33l0.06,0.14l0.17,0.07l0.09,0.26l0.2,0.14l-0.03,0.12l0.11,0.02l0.06,-0.17l0.08,0.16l0.14,0.08l-0.03,0.09l0.06,0.1l0.29,0.19l-0.03,0.09l0.14,0.14v0.12l0.26,0.38l-0.03,0.14l0.11,0.1v0.12l0.14,0.1v0.14l0.14,0.19l0.06,0.14l-0.03,0.17l0.11,0.17v0.12l0.14,0.14l0.14,0.33l0.37,0.29l0.14,0.4l0.08,0.02l0.03,0.19l0.23,0.17l0.06,0.19l0.17,0.09v0.07l0.17,0.09l0.06,0.1l0.2,0.12l0.26,-0.05l0.09,0.07l0.11,-0.05l0.14,0.05l0.09,0.14l0.11,0.05l0.03,0.12l0.2,0.02l0.48,0.43l0.17,0.29l0.11,0.02l0.29,0.24l0.11,-0.02l0.17,0.12l0.26,-0.02l0.23,-0.09l0.11,0.02l0.11,-0.09v-0.07l0.17,-0.05l0.23,0.07l0.09,-0.02v-0.07l0.03,0.05l0.28,-0.02l0.14,0.05l0.09,-0.14l0.2,-0.02l0.57,-0.36l0.2,-0.05l0.14,-0.19l0.46,-0.33l0.06,0.05l0.09,-0.07l0.11,0.05l0.17,-0.02l0.11,0.07l0.06,-0.07h0.31l0.06,-0.07l0.2,0.12l0.06,-0.07h0.46l0.11,0.05v0.17h0.06l0.17,-0.09l0.06,-0.1l0.14,-0.05l0.37,-0.36l0.37,-0.17l0.11,-0.14l0.08,0.09h0.2l0.17,0.36l0.14,0.09h0.4l-0.03,-0.07h-0.26l-0.17,-0.09l-0.23,-0.5l-0.34,-0.26l-0.26,-0.29l-0.08,-0.29l-0.09,-0.05v-0.12l-0.11,-0.12l-0.8,-0.02l-0.11,0.26l-0.11,0.02l-0.11,0.17l-0.48,0.05l-0.37,-0.38l-0.34,-0.17l-0.11,-0.14l-0.4,-0.19l-0.14,0.05v0.09l-0.4,0.02l-0.31,0.26h-0.09l-0.03,0.07l-1.02,-0.12l-0.77,-0.38l-0.29,0.05v-0.07l0.11,-0.09l-0.23,-0.05l-0.17,-0.19l-0.23,-0.07l-0.28,-0.29l-0.34,-0.14l-0.08,-0.17l-0.23,-0.14l-0.03,-0.12l-0.26,-0.19l-0.11,-0.26l-0.2,-0.12l-0.03,-0.1l-0.2,-0.12l-0.03,-0.09l-0.2,-0.17l-0.26,-0.09l-0.14,-0.17l-0.26,-0.12l-0.14,-0.17l-0.34,-0.12l-0.09,-0.14l-0.28,-0.1l-0.09,-0.14h-0.11l-0.4,-0.24l-0.17,-0.24l-0.43,-0.36h-0.08l-0.06,-0.26l-0.26,-0.29l-0.26,-0.81l-0.09,-0.07l-0.14,-0.45l-0.09,-0.02l-0.03,-0.36l-0.14,-0.19v-0.36l-0.08,-0.14l-0.14,-0.67v-0.64l-0.11,-0.64l-0.03,-0.1l-0.11,-0.02l-0.14,-0.41l0.06,-0.07h0.26v-0.05h-0.2l0.2,-0.12v-0.07l-0.17,0.1l-0.09,-0.02v-0.07h-0.11l0.06,0.12l-0.03,0.1L547,873.2l-0.14,-0.12v-0.09l0.2,-0.05l0.14,-0.12l0.03,-0.12l-0.2,0.1l0.14,-0.12v-0.09l-0.23,0.07l0.06,-0.1h-0.14l-0.14,0.07l0.06,0.21l-0.11,-0.14l-0.23,0.05l-0.06,-0.1l-0.14,-0.07l-0.31,-0.45l-0.06,-0.17h-0.06l-0.11,-0.17l0.2,-0.17l0.03,-0.21l-0.11,-0.36l-0.37,-0.57l-0.2,-0.48l-0.14,-0.12l-0.14,-0.67l-0.11,-0.17l-0.06,-0.29h-0.06l-0.11,-0.81l-0.06,-0.05v-0.38l0.08,-0.14l-0.08,-0.6l-0.08,-0.09l-0.46,-0.12l-0.34,-0.55l-0.2,-0.09l-0.11,-0.14l-0.23,-0.14l-0.08,-0.19l-0.17,-0.1l-0.17,-0.22l-0.14,-0.64l0.17,-1.34l0.06,-0.02v-0.14l0.14,-0.36l0.31,-0.5v-0.14l0.11,-0.1l0.08,-0.26l0.31,-0.43l0.03,-0.17l0.14,-0.12l0.26,-0.38l0.11,-0.31l0.08,-0.05l0.2,-0.31l0.06,-0.24h0.06l0.08,-0.12l0.09,-0.31l0.17,-0.22l0.03,-0.24l0.06,-0.02l0.11,-0.36l0.11,-0.1l0.17,-0.33l0.14,-0.41l0.08,-0.12l0.09,-0.02v-0.07l0.08,-0.05v-0.07l0.17,-0.22l0.2,-0.1l0.29,-0.6l0.08,-0.05l0.23,-0.36l0.14,-0.1l-0.17,-0.17l-0.17,-0.07v-0.07h0.17l0.2,0.22l0.09,-0.02l0.08,-0.14l0.17,-0.07l0.17,-0.17l0.31,-0.41l0.2,-0.05l0.14,-0.14l0.03,-0.17l0.08,-0.02l0.06,-0.65l-0.06,-0.12l-0.14,-0.1l0.4,-0.19l0.03,-0.24l0.14,-0.17l0.08,-0.26l0.11,-0.07l0.06,-0.17l0.14,-0.1l0.11,-0.24l0.31,-0.22l0.34,-0.05l0.14,-0.24l0.23,-0.05v-0.22l0.08,-0.22l0.11,-0.22l0.17,-0.1v-0.05l-0.34,-0.07l-0.06,-0.38l0.37,-0.19l0.11,-0.17l0.09,0.02l0.06,-0.19l0.14,-0.12v-0.22l-0.26,-0.58l0.03,-0.05l-0.2,-0.02l-0.06,-0.07l-0.08,0.07l-0.11,-0.02v-0.14h-0.14l-0.11,0.07l-1.11,-0.05l-0.71,-0.19l-0.2,-0.12h-0.11l-0.03,0.1l-0.08,0.02l-0.06,-0.07l-0.31,-0.02l-0.51,-0.22l-0.17,-0.17l-0.23,-0.1v-0.05l-0.34,-0.19l-0.26,-0.31l-0.14,-0.31l-0.31,-0.41l-0.2,-0.6l-0.2,-0.34l-0.23,-0.87v-1.42l0.08,-0.41l-0.06,-1.35l0.06,-0.43l-0.03,-0.91l0.09,-1.2l0.08,-0.19l0.23,-1.66l0.09,-0.17l-0.03,-0.58l0.06,-0.14l-0.03,-1.45l-0.09,-0.38l0.03,-0.34l-0.03,-0.29l-0.06,-0.07l0.03,-0.22l-0.06,-0.15l0.03,-0.36l-0.06,-0.05v-0.43l0.26,-0.34l0.46,-0.24l0.43,-0.12h0.17l0.03,0.07h0.23v-0.05l0.57,-0.29h0.11l0.08,0.07l0.14,0.46l-0.09,0.22l-0.28,0.17l0.14,0.07l0.03,0.1l-0.03,0.63l0.08,1.16l0.17,0.34l0.17,0.05v0.27l0.23,0.22l0.06,0.17l1,0.48l0.06,0.19l0.17,0.14l0.03,0.31l-0.17,0.1l-0.31,0.02v0.29l0.11,0.31l-0.03,0.17l0.26,0.14h0.2l0.06,-0.07l0.28,-0.05l0.2,0.17l0.17,0.46l0.23,0.26l0.2,0.1h0.26l0.14,-0.07l-0.03,-0.07l-0.34,0.05l-0.23,-0.14l-0.11,-0.17l-0.09,-0.36l-0.11,-0.19h-0.09l-0.11,-0.12h-0.2l-0.03,-0.07l-0.17,0.05l-0.26,-0.1l0.17,-0.43h0.09l0.14,-0.17l0.03,-0.24l-0.14,-0.41l-0.17,-0.12v-0.1l-0.29,-0.22v-0.07l-0.11,-0.02l-0.03,-0.1l-0.14,-0.07l-0.17,-0.22l-0.17,-0.39l-0.2,-0.17l-0.14,-0.8l0.06,-0.24l-0.11,-0.07l0.08,-0.46l-0.08,-0.02l-0.09,-0.24l0.06,-0.77l-0.09,-0.12l0.09,-0.14l0.51,-0.07l0.06,-0.07l0.46,-0.19l0.09,-0.1l0.2,-0.02l0.37,-0.26l0.26,-0.07l0.34,-0.19l0.4,-0.07l0.2,-0.12l0.43,-0.07l0.17,-0.22h0.17l0.06,-0.07l0.23,0.05l0.26,-0.05l0.37,0.36l0.17,0.07h0.4l0.08,-0.05v-0.19h0.37l0.23,-0.14l0.03,-0.1l0.14,-0.02l0.14,-0.12l0.09,-0.22l-0.06,-0.1l0.06,-0.27l0.26,-0.14l0.2,0.07l0.09,-0.14l0.14,-0.02l0.14,-0.31l0.11,-0.07l0.08,-0.24l0.11,-0.02l-0.03,-0.07l-0.4,-0.05l-0.66,-0.39l-0.14,-0.56l0.06,-0.14l-0.06,-0.02l-0.28,-1.06l-0.31,-0.29l-0.06,-0.19l-0.17,-0.24l-0.06,-0.41l-0.26,-0.29l-0.08,-0.27l-0.03,-0.43l-0.09,-0.1l-0.06,-0.29l-0.06,-0.1l-0.23,0.02l-0.11,-0.07l-0.06,-0.12l-0.14,-0.1l-0.09,-0.22l-0.28,-0.27l0.11,-0.02l0.09,-0.15l0.23,-0.05l0.03,-0.07l-0.11,-0.05l-0.26,0.02l-0.06,0.15l-0.06,-0.02v-0.1l0.31,-0.22l0.14,-0.02l0.08,-0.15l0.14,0.02l0.06,-0.07h0.17l0.03,-0.24l-0.09,-0.07v-0.12l0.11,-0.12l-0.11,-0.07l0.03,-0.15l0.26,0.02l0.17,-0.1l0.11,-0.46l0.14,-0.24v-0.27l0.23,-0.19l0.06,-0.29l0.17,0.02l0.34,-0.24l0.06,-0.36l-0.2,-0.27l0.09,-0.02l0.11,0.15l0.17,-0.05l0.31,0.1l0.34,-0.36l0.17,0.02l0.14,-0.05l0.14,0.07h0.17l0.43,-0.22l0.26,-0.31l0.03,-0.19l0.28,-0.05v-0.07l0.31,-0.07l0.23,-0.15l0.09,-0.63l0.17,-0.36l-0.03,-0.31l0.06,-0.05v-0.17l0.06,-0.07l0.17,-0.02l0.11,-0.19l0.11,-0.05l0.31,-0.58l0.14,-0.41l-0.03,-0.24l0.11,-0.17v-0.22l0.06,-0.02l0.03,-0.12h-0.09l-0.14,0.27l-0.2,0.02l-0.26,-0.17l-0.03,-0.19l-0.08,-0.1l-0.4,-0.07v-0.19l-0.14,-0.15l-0.03,-0.19l-0.08,-0.02v-0.34l-0.14,-0.34v-0.07l0.23,-0.17v-0.24l-0.06,-0.1l-0.46,0.1l-0.37,-0.61v-0.92l-0.06,-0.24l-0.14,-0.17l-0.2,-0.1l-0.09,-0.15l-0.2,-0.02l-0.06,-0.12l-0.54,-0.19l-0.11,0.02l-0.17,-0.12l0.03,-0.29l0.17,-0.17l-0.2,-0.27l0.26,-0.22h0.4l0.11,-0.24l0.37,-0.1l0.06,-0.19l0.37,-0.15l0.06,-0.15h0.6l0.03,-0.15l0.06,-0.02v-0.17l0.17,-0.07v-0.17l0.31,-0.22v-0.15l0.2,-0.29l0.03,-0.15l0.08,-0.05l0.17,0.02l0.28,-0.36l0.2,-0.05l0.08,-0.22l0.14,-0.05v-0.17l-0.11,-0.12l-0.09,-0.29l-0.17,-0.05l-0.03,-0.22l-0.08,-0.07l0.03,-0.22l-0.08,-0.17l0.03,-0.07l-0.11,-0.24l-0.34,-0.07h-0.37l0.03,0.2l0.11,0.15l0.11,0.05h0.08l0.03,-0.1l0.08,-0.05l0.26,0.05l-0.06,0.27l-0.17,0.27l-0.03,0.2l0.03,0.85l-0.4,0.07l-0.14,0.19l-0.94,0.36l-0.11,0.17l-0.14,0.51l-0.14,0.19v0.17l-0.08,0.07l-0.14,0.02l-0.51,-0.12l-0.26,0.19l-0.51,0.05l-0.43,0.17l-0.34,0.22l0.06,0.27l-0.08,0.15h-0.11v-0.22l-0.11,-0.02l-0.54,0.17l-0.03,0.1l-0.2,0.12v0.07l-0.17,0.12l-0.03,0.22h-0.08l0.03,0.22l-0.11,0.02l-0.08,0.1v0.41l-0.06,0.12l0.03,0.27l-0.2,0.39l0.08,0.34l-0.11,0.07l-0.11,-0.05h-0.11l-0.03,0.05l0.09,0.1l0.23,0.02l-0.06,0.1v0.36l-0.31,0.17l-0.23,0.27l-0.06,0.24l-0.17,-0.19l-0.06,-0.39l-0.17,-0.07l-0.34,0.15l-0.14,-0.02l-0.17,0.24l-0.23,0.1l-0.2,0.17h-0.17l-0.46,0.19l-0.46,0.02l-0.43,-0.29l-0.17,-0.05v-0.1l-0.11,-0.05l-0.03,-0.19l-0.26,-0.05l-0.06,-0.17l-0.08,-0.05l0.14,-0.17v-0.07l0.17,-0.07l0.03,-0.15l-0.09,-0.22l0.23,-0.34l-0.11,-0.36l-0.11,-0.02l-0.03,-0.12h-0.14l-0.08,-0.07l-0.37,-0.49l-0.26,-0.17l-0.14,-0.19l-0.23,-0.12l-0.23,-0.22l-0.03,-0.1l-0.34,-0.24l0.2,-0.12h0.06l0.03,0.07l0.2,-0.07l0.06,-0.27l0.09,-0.07l0.2,0.02l0.14,-0.07l0.09,-0.1v-0.17l-0.11,-0.34l-0.31,-0.22l-0.03,-0.46l-0.2,-0.17l-0.06,-0.46l-0.23,-0.36l-0.09,-0.07h-0.17l-0.17,0.19h-0.11l-0.26,-0.24l-0.03,-0.44l0.2,-0.05l-0.06,-0.19l0.37,-0.03l0.06,-0.05l0.2,-0.24l-0.03,-0.24h0.2v-0.12l-0.08,-0.02l-0.23,0.1l0.03,0.32l-0.06,0.05l-0.48,-0.07l-0.06,0.07l-0.06,0.39l-0.2,0.32v0.29l0.06,0.12l-0.09,0.17v0.29l-0.06,0.12l0.14,0.22l0.28,-0.07l0.37,0.15l0.17,0.17l0.03,0.27l-0.2,0.39l-0.06,0.41l-0.2,0.1l-0.11,0.19l-0.23,-0.02l-0.14,0.15l-0.06,0.29l-0.06,0.05v0.17l0.14,0.12v0.24l-0.23,0.15l-0.11,0.17l-0.03,0.17l-0.17,0.19l0.03,0.17l0.11,0.1v0.22l0.09,0.17v0.19l-0.09,0.1l0.03,0.41l-0.06,0.07v0.22l-0.26,0.05l-0.11,0.1l-0.03,0.1l-0.14,0.07l-0.03,0.17l-0.17,0.1l-0.14,0.22l-0.06,0.22l-0.17,0.19l-0.08,0.22l0.03,0.19l-0.2,0.46v0.17l-0.2,0.14l-0.2,0.32l-0.26,0.17l-0.03,0.19l-0.14,0.1v0.39l-0.08,0.17l-0.51,0.27l-0.23,0.19l-0.14,0.41h-0.08l-0.34,0.31l0.06,0.1l-0.06,0.12l-0.37,0.27v0.05l-0.14,0.05l-0.08,0.15l-0.46,0.34l-0.31,0.34l-0.06,0.19l-0.34,0.29l-0.11,0.27v0.19l0.03,0.1l0.11,0.07v0.12l0.26,0.12l0.08,0.17l0.26,0.19h0.17l0.17,0.27l-0.03,0.15l-0.08,0.02v0.05l-0.26,-0.05l-0.03,0.07h-0.09l-0.08,-0.56l-0.46,-0.36l-0.08,-0.15l0.03,-0.1l-0.14,-0.19l-0.11,0.02l0.06,0.14l-0.11,0.07l0.09,0.34l-0.14,0.02l-0.03,-0.1l-0.11,-0.05l0.06,-0.17l0.08,-0.05v-0.17l0.14,-0.24l-0.03,-0.19l-0.63,-0.51l-0.14,-0.24v-0.12l0.11,-0.05v-0.22l-0.08,-0.07v-0.17l-0.26,-0.32l-0.37,-0.05l0.06,-0.17l-0.2,-0.31h-0.11l-0.26,0.19l-0.2,0.07v0.07l0.14,0.15l0.03,0.31l-0.2,0.05l-0.23,0.19l-0.08,0.39l-0.4,-0.12l-0.28,-0.53l-0.17,-0.07l-0.11,-0.15l-0.14,-0.31l-0.03,-0.41l-0.06,-0.05l0.03,-0.19l-0.14,-0.19l-0.03,-0.15l0.06,-0.39l-0.06,-0.61l0.06,-0.46l0.2,-0.7v-0.24l0.06,-0.07l-0.03,-0.39l0.17,-0.27v-0.17l-0.23,-0.24l-0.23,0.05v0.15l-0.17,-0.02l-0.08,-0.29l-0.2,-0.15l-0.17,-0.32l0.03,-0.24l0.17,-0.22l0.48,-0.19l0.51,0.05l0.09,0.07h0.31l0.28,-0.22v-0.17l0.2,-0.1l0.14,-0.34l-0.03,-0.34l-0.09,-0.12h-0.08l-0.29,-0.19l0.11,-0.05l0.2,0.12l0.2,-0.05l0.06,-0.44l-0.06,-0.07l0.03,-0.15l0.2,-0.15l0.2,-0.27v-0.17l-0.11,-0.12l-0.03,-0.24l-0.14,-0.1l-0.06,-0.17l0.14,0.1h0.11l0.11,-0.12l0.11,-0.22l0.06,-0.73l0.09,-0.07l0.08,-0.24l0.11,-0.07l0.03,-0.15l0.14,-0.15l0.11,-0.29l0.23,-0.22l0.06,-0.22h-0.14l-0.08,0.22l-0.2,0.22l-0.06,0.22l-0.14,0.07l-0.09,-0.02l0.03,-0.15l-0.17,-0.87l-0.08,-0.15l-0.31,-0.27l-0.06,-0.17l-0.11,-0.05l-0.03,-0.19l0.11,-0.12l-0.09,-0.34l-0.54,-0.44l-0.2,-0.02v-0.15l0.11,-0.1l0.11,-0.27l-0.09,-0.27l-0.06,-0.05l-0.14,0.02l-0.08,0.29l-0.09,0.02l0.06,0.15l-0.11,0.07l-0.03,0.19l0.11,0.05v0.12l0.11,0.22l0.31,0.15l-0.09,0.51l-0.08,0.05v0.07l0.06,0.02l0.03,0.44l-0.14,0.24l-0.08,0.05l-0.03,0.22l-0.14,0.1v0.22l-0.17,0.22l-0.03,0.15l-0.06,0.07h-0.14l-0.06,0.12l-0.2,0.05l-0.14,0.46h-0.14l-0.08,0.07l-0.06,0.2l0.03,0.1l0.11,0.02l0.06,0.15h0.11v0.07l-0.11,0.07v0.19l0.29,0.12l0.06,0.24l0.14,0.15v0.12l-0.17,0.1l-0.14,0.53l-0.31,0.24l-0.06,0.12l-0.14,-0.05l-0.09,0.1l-0.31,0.1h-0.23l-0.28,-0.1l-0.26,-0.27l-0.06,-0.19l-0.11,-0.05l-0.14,0.07l-0.37,0.46l-0.46,0.29l-0.54,0.02l-0.34,-0.1l-0.2,-0.12l-0.08,-0.1v-0.1l-0.4,-0.46l-0.06,-0.17l-0.11,-0.12l-0.14,-0.44l-0.34,-0.49l-0.77,-1.41l-0.14,-0.12l-0.26,-0.58l-0.34,-0.37l-0.34,-0.53l-0.11,-0.07l-0.08,-0.19l-0.23,-0.27l-0.2,-0.12l-0.31,-0.58l-0.14,-0.15l-0.11,-0.22l0.03,-0.37l-0.14,-0.05l-0.08,-0.2l-0.08,-0.05l-0.34,-0.56l-0.2,-0.51l-0.03,-0.29l0.06,-0.17l-0.09,-0.05l-0.06,-0.15l-0.03,-0.32l0.08,-0.15l0.26,-0.2l0.03,-0.56l-0.06,-0.63l-0.06,-0.02V798l-0.17,-0.27l-0.06,-0.39l-0.11,-0.2l-0.03,-0.19v-0.37l0.09,-0.2l0.03,-0.49l0.23,-0.24l0.17,-0.1l0.23,-0.49l0.09,0.02l0.03,0.12l-0.09,0.19l0.03,0.37l-0.11,0.29l0.17,0.17l0.28,0.12h0.2l0.37,-0.41h0.09l0.23,-0.17l-0.03,-0.2l-0.11,-0.02l-0.06,0.1l0.03,0.07l-0.26,0.17h-0.09l-0.08,0.1l-0.26,0.05l-0.23,-0.05v-0.27l0.09,-0.1v-0.44l-0.09,-0.24l-0.23,-0.17l0.03,-0.32l0.14,-0.32l0.29,-0.05v-0.15l0.08,-0.15h-0.11l-0.08,0.17l-0.31,-0.02l-0.03,-0.07h-0.11v0.15l0.06,0.05l-0.03,0.22l0.06,0.02l-0.08,0.15h-0.11l-0.14,-0.17l-0.28,-0.02l-0.11,0.07l-0.08,0.22h-0.14l-0.37,-0.27v-0.17l0.11,-0.22v-0.12l-0.14,-0.17l-0.14,-0.02l-0.03,0.05l0.11,0.15v0.12l-0.09,0.05v0.32l0.31,0.29l0.4,0.15l0.34,0.24l-0.03,0.34l-0.37,0.27l-0.26,0.05l-0.11,-0.15l-0.17,-0.1l-0.2,-0.39l-0.4,-0.39l-0.2,-0.41l-0.31,-0.39l-0.17,-0.41l-0.23,-0.34l-0.23,-0.85l-0.08,-0.07l-0.11,-0.29l-0.23,-0.22l-0.37,-0.73l-0.17,-0.2h-0.06l-0.43,-0.56l-0.09,-0.02l-0.26,-0.39v-0.1l-0.34,-0.39l-0.06,-0.15h-0.06l0.09,-0.07l-0.09,0.02l-0.11,-0.1l-0.14,-0.32l-0.46,-0.51l-0.06,-0.15l-0.23,-0.2l-0.14,-0.29l-0.28,-0.29l-0.11,-0.24l-0.37,-0.29l-0.28,-0.34l-0.17,-0.07l-0.09,-0.34l-0.2,-0.22l-0.06,-0.24l-0.14,-0.07l-0.23,-0.29l-0.26,-0.71l-0.31,-0.32l-0.14,-0.37l-0.14,-0.15l-0.08,-0.2l-0.14,-0.07l-0.11,-0.2l-0.4,-0.34h-0.08l-0.08,0.12h-0.29l-0.2,-0.1l-0.09,-0.42l-0.23,-0.37l-0.11,-0.34l-0.2,-0.05l-0.17,-0.17l-0.23,-0.81l0.06,-0.2l0.08,-0.07l0.51,-0.17l0.28,-0.27l0.31,-0.15l0.09,0.05l0.14,-0.17l0.14,-0.34l0.11,-0.07l0.06,-0.29l0.06,-0.05l0.06,-0.66l0.26,-0.64v-0.15l0.08,0.02l0.09,0.12l0.2,-0.02l-0.03,-0.1l0.06,-0.05l0.06,-0.27l-0.06,-0.44l0.34,-0.39l0.03,-0.56l0.2,-0.12v-0.15l-0.11,-0.07l-0.03,-0.29l-0.06,-0.02l0.06,-0.76l-0.03,-0.39l0.17,-0.12l0.11,-0.22l0.14,-0.49l0.08,-0.05l0.03,-0.22l0.06,-0.02l-0.03,-0.27l0.09,-0.12l-0.06,-0.2l0.06,-0.29v-0.93l-0.14,-0.29l-0.14,-0.56v-0.88l-0.17,-0.71l0.14,0.07l0.09,0.17l0.26,0.1l0.11,-0.02l-0.03,-0.32l0.23,-0.27v-0.12l0.11,-0.12l-0.06,-0.32l0.34,-0.32l0.14,-0.2v-0.1l0.17,-0.1v-0.15l0.14,-0.12l0.09,-0.22l0.23,-0.1l0.11,-0.2l0.17,-0.02l0.2,-0.15v-0.15l0.11,-0.1l0.03,-0.12l0.26,-0.2l0.11,-0.37l0.2,-0.22v-0.17l0.09,-0.12v-0.29l0.14,-0.22l-0.03,-0.17l0.11,-0.07v-0.17l0.08,-0.12v-0.74l0.09,0.02l0.03,0.1h0.26l-0.03,-0.07l0.23,-0.22l0.2,-0.59l0.57,-0.34l0.43,-0.44v-0.07l0.46,-0.22l0.09,-0.12l0.17,-0.05l0.17,-0.3l0.23,-0.17l0.03,-0.1h0.06v-0.2l0.34,-0.27l0.03,-0.1l0.23,-0.22l0.23,-0.42l0.03,-0.25l0.08,-0.15l0.06,-0.96l0.06,-0.07l-0.03,-0.52l0.08,-0.15h-0.06v-0.17l-0.09,-0.02v-0.15l-0.06,-0.02l-0.08,-0.39l-0.09,-0.15l-0.06,-0.84l0.23,-0.32l0.23,-0.74l0.14,-0.2l0.06,-0.3l0.11,-0.17v-0.15l0.17,-0.32l-0.03,-0.05l0.11,-0.15l0.14,-0.47l0.28,-0.59l0.2,-1.06l0.09,-0.12v-0.37l0.11,-0.32l0.06,-0.02v-0.2l0.14,-0.32l0.23,-0.1l0.51,-0.57l0.06,-0.22l0.17,-0.2l0.14,-0.35l0.37,-0.42v-0.07l0.88,-0.54l0.11,-0.12l0.11,-0.02l0.03,-0.07l0.17,-0.05h0.4l0.14,0.1l-0.51,0.27l0.03,0.07l0.2,-0.02l0.11,0.27l0.71,0.27l0.26,-0.02l0.26,0.07l0.03,0.1H532l0.43,0.27l0.23,0.07l0.03,0.08l0.11,0.05l-0.03,0.1l0.14,0.22h0.31l0.11,0.07l0.09,0.12l-0.09,0.2l0.06,0.02v0.1l0.11,0.03l0.06,-0.15l-0.03,-0.25l0.06,-0.07l-0.14,-0.1l-0.09,-0.34l-0.08,-0.08l0.06,-0.42l-0.34,-0.1v-0.17l0.09,-0.07l0.28,-0.05l0.68,0.2l0.08,-0.1l0.29,-0.12l0.08,-0.15l0.11,-0.02l0.08,-0.22l0.34,-0.4l0.03,-0.64l0.06,-0.12h0.08v0.12l0.11,0.05h0.6l0.14,0.3l0.28,0.22l0.14,-0.03v-0.27l-0.43,-0.12l-0.17,-0.3l-0.57,-0.05l-0.11,-0.1v-0.1h-0.08l-0.03,0.07l-0.57,0.2l-0.17,0.35l-0.34,0.37h-0.71l-0.06,-0.05l0.06,-0.07l-0.03,-0.1l-0.2,-0.05l-0.2,0.05l-0.09,-0.15h-0.23l0.03,-0.25l-0.2,-0.17l-0.06,-0.2l0.17,-0.74l0.26,-0.52l0.31,-0.32l0.06,-0.15l0.48,-0.35l0.48,-0.25l0.29,-0.05l0.28,-0.22h0.11l0.06,-0.1l0.14,0.02l0.48,-0.15l0.14,-0.2l0.29,-0.2l0.11,-0.15l0.48,-0.05l0.28,-0.2h0.66l0.54,0.27l0.26,0.02l0.17,0.2l0.11,0.35l0.28,0.32l0.17,0.07l0.31,0.03l0.11,0.12l0.4,0.05l0.14,0.15l0.03,0.27h0.06l0.03,0.07l0.43,0.17l0.11,0.1h0.14l0.17,0.15l0.65,-0.07l0.46,0.15l0.23,-0.02l0.34,-0.15l0.23,-0.27l0.28,-0.12l0.46,-0.1l0.71,-0.35l0.23,-0.25l0.11,-0.4l0.43,0.03l0.03,-0.05h0.11l0.03,-0.07h0.11l0.11,0.1l0.28,0.03l0.03,0.05l0.66,0.05l0.26,-0.15l0.34,-0.1l-0.11,-0.05l-0.43,0.17l-0.43,-0.02l-0.23,0.05l-0.46,-0.47l-0.43,-0.05l-0.2,0.1l-0.14,-0.02l-0.31,-0.35l-0.17,0.1l-0.09,0.17l-0.11,0.02l-0.17,0.17l-0.2,0.05l-0.2,-0.1v-0.17l-0.08,-0.08v-0.37l0.17,-0.25l0.46,-0.27l0.11,-0.22l0.26,-0.22l0.26,-0.4l0.23,-0.77l0.28,-0.32l0.23,-0.02l0.11,0.07l0.09,0.59l0.14,0.15l0.17,0.07l-0.06,0.25l0.06,0.27l0.34,0.05l0.66,-0.27h-0.17l-0.54,0.22h-0.14l-0.11,-0.1l0.09,-0.5l-0.11,-0.17l-0.2,-0.05v-0.37l-0.06,-0.05v-0.12l-0.17,-0.12l0,0l0.21,-1.47l0.75,-1.34l-0.13,-3.2l0.94,0.02l1.91,1.09l1.4,-0.05l1.14,-2.75l2.26,-0.89l0.74,-1.89l1.75,-1.57l0.8,-1.76l2.51,-0.57l1.28,-0.72l2.39,-2.06L563.98,720.49z"/>
		...
```

显示图片的代码
Step1:使用系统方法解析文件，创建path集合（通过DocumentBuilder）
Step2：轮训path集合，获取数据存储。（使用PathParser将pathdata转换为Path实例）
Step3：轮训完毕，调用重新绘制方法，在onDraw中进行绘制

```
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

```
ProvinceItem.kt

```
data class ProvinceItem(
    val path: Path,
    val name: String,
    //模块颜色
    val drawColor: Int

) {
    //显示省份信息
    var clickPoint: PointF? = null

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
```