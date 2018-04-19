package com.luoji.spatialUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import com.google.common.math.DoubleMath;
/** 
 * Description: 
 * 四叉树网格剖分工具
 * @author LuoJi
 * @version 1.0 
 */
/* <br/>Copyright(C),2013-2033,Ji Luo
* <br/>This program is protected by copyright laws. 
* <br/>四叉树网格剖分工具
*/
public class SpatialUtil {

	private double basicX ;
	private double basicY;
	private double totalLongitude;//经度总长
	private double totalLatitude;//纬度总长
	private double spatialLevel;
	private double basicXDivideLength;
	private double basicYDivideLength;
	private TreeMergeTool tmt= new TreeMergeTool();
	private SpatialHelper sh = new SpatialHelper();
	
	//每次查询范围网格化简至约为该级别。意思是每次查询所包含的化简后的网格
	//不会明显超出4^QUERY_KEEP_SPATIAL_LEVEL
	private final static int QUERY_KEEP_SPATIAL_LEVEL = 4;
	
	private SpatialUtil(){
		
	}
	
	/**
	 * 实例化适用于中国的四叉树网格剖分工具
	 * @param spatiallevel 剖分级别，整体范围将被划分成4^spatiallevel个网格，可以不为整数，通常为12-18之间
	 * @return 适用于中国的四叉树网格剖分工具
	 */
	public static SpatialUtil getChinaSpatialUtil(double spatiallevel){//spatiallevel只要不大于原有数据导入时的spatiallevel即可？
		/*最东端 东经135度2分30秒 黑龙江和乌苏里江交汇处 按135.1算
		最西端 东经73度40分 帕米尔高原乌兹别里山口（乌恰县）按73.6算
		最南端 北纬3度52分 南沙群岛曾母暗沙  按3.8算
		最北端 北纬53度33分 漠河以北黑龙江主航道（漠河县）按53.6算*/
		SpatialUtil siu = new SpatialUtil();
		siu.basicX=73.6;
		siu.basicY=53.6;
		siu.totalLongitude=61.5;
		siu.totalLatitude=49.8;
		siu.spatialLevel=spatiallevel;
		siu.basicXDivideLength =61.5/ (Math.pow(2,spatiallevel));
		siu.basicYDivideLength = 49.8/ (Math.pow(2,spatiallevel));
		return siu;
		
	}
	
	/**
	 * 实例化适用于世界的四叉树网格剖分工具
	 * @param spatiallevel 剖分级别，整体范围将被划分成4^spatiallevel个网格，可以不为整数，通常为13-19之间
	 * @return 适用于世界的四叉树网格剖分工具
	 */
	public static SpatialUtil getWorldSpatialUtil(double spatiallevel){
		SpatialUtil siu = new SpatialUtil();
		siu.basicX=-180.0;
		siu.basicY=90;
		siu.totalLongitude=360;
		siu.totalLatitude=180;
		siu.spatialLevel=spatiallevel;
		siu.basicXDivideLength =360/ (Math.pow(2,spatiallevel));
		siu.basicYDivideLength = 180/ (Math.pow(2,spatiallevel));
		return siu;
	}
	
	/**
	 * 实例化适用于用户定义范围的四叉树网格剖分工具
	 * @param basicX 精度最小值，西经为负，东经为正，通常为范围的最西端经度
	 * @param basicY 纬度最大值，南纬为负，北纬为正，即为范围的最北端纬度
	 * @param totalLongitude 范围的经度跨度
	 * @param totalLatitude 范围的纬度跨度
	 * @param spatiallevel 剖分级别，整体范围将被划分成4^spatiallevel个网格，可以不为整数
	 * @return 适用于用户定义范围的四叉树网格剖分工具
	 */
	public static SpatialUtil getUserSpatialUtil(double basicX,double basicY,double totalLongitude,double totalLatitude,double spatiallevel){
		SpatialUtil siu = new SpatialUtil();
		siu.basicX=basicX;
		siu.basicY=basicY;
		siu.totalLongitude=totalLongitude;
		siu.totalLatitude=totalLatitude;
		siu.spatialLevel=spatiallevel;
		siu.basicXDivideLength =totalLongitude/ (Math.pow(2,spatiallevel));
		siu.basicYDivideLength = totalLatitude/ (Math.pow(2,spatiallevel));
		return siu;
	}
	
	/**
	 * 返回经纬度坐标点所在的网格的编码
	 * @param x 点的经度坐标
	 * @param y 点的纬度坐标
	 * @return 坐标点所在的网格的编码
	 */
	public String xy2spatialIndex(double x, double y){
		double offsetX = x-basicX;
		double offsetY = basicY-y;
		boolean isXodd;
		boolean isYodd;
		StringBuffer sb = new StringBuffer();
		for(int i=1;i<=spatialLevel;i++){
			double dividesXLength = basicXDivideLength*(Math.pow(2,spatialLevel-i));
			double dividesYLength = basicYDivideLength*(Math.pow(2,spatialLevel-i));
			isXodd = isOdd((int) (offsetX/dividesXLength) );
			isYodd = isOdd((int) (offsetY/dividesYLength) );
			if(isXodd && isYodd){
				sb.append(3);
			}
			else if(!isXodd && isYodd){
				sb.append(2);
			}
			else if(isXodd && !isYodd){
				sb.append(1);
			}
			else if(!isXodd && !isYodd){
				sb.append(0);
			}
		}
		return sb.toString();	
	}
	
	private boolean isOdd(int i){//按位与判断奇偶
		return (i&1)!=0; 
	}
	
	/**
	 * 查询落入指定矩形内部的网格编号列表
	 * @param minx 指定矩形最左侧经度坐标，即最小经度坐标
	 * @param miny 指定矩形最下方纬度坐标，即最小纬度坐标
	 * @param maxx 指定矩形最右侧经度坐标，即最大经度坐标
	 * @param maxy 指定矩形最上方纬度坐标，即最大纬度坐标
	 * @return 矩形内部的网格编号列表
	 */
	public TreeSet<String> getInnerSquaresByRectangle(double minx, double miny, double maxx, double maxy){
		int simplificationLevel =getSimplificationLevel(minx, miny, maxx, maxy);
		return getInnerSquares(minx, miny, maxx, maxy, simplificationLevel);
	}
	
	
	TreeSet<String> getInnerSquares(double minx, double miny, double maxx, double maxy,int simplificationLevel){
		//这个simplificationLevel，同一个范围请求在getInnerSquars和getCrossSquares中应当是一致的。此参数不开放给用户。
/*		String leftTopSquare = moveRight(moveDown(xy2spatialIndex(minx, maxy)));
		String rightTopSquare = moveLeft(moveDown(xy2spatialIndex(maxx, maxy)));
		String leftBottomSquare = moveRight(moveUp(xy2spatialIndex(minx, miny)));
		String rightBottomSquare = moveLeft(moveUp(xy2spatialIndex(maxx, miny)));
		//应该是将上下颠倒！？先合并后向内移动
		leftTopSquare = leftTopSquare.substring(0, leftTopSquare.length()-simplificationLevel);
		rightTopSquare = rightTopSquare.substring(0, rightTopSquare.length()-simplificationLevel);
		leftBottomSquare = leftBottomSquare.substring(0, leftBottomSquare.length()-simplificationLevel);
		rightBottomSquare = rightBottomSquare.substring(0, rightBottomSquare.length()-simplificationLevel);*/
		
		String leftTopSquare = xy2spatialIndex(minx, maxy);
		String rightTopSquare = xy2spatialIndex(maxx, maxy);
		String leftBottomSquare = xy2spatialIndex(minx, miny);
		String rightBottomSquare = xy2spatialIndex(maxx, miny);
		
		leftTopSquare = moveRight(moveDown(leftTopSquare.substring(0, leftTopSquare.length()-simplificationLevel)));
		rightTopSquare = moveLeft(moveDown(rightTopSquare.substring(0, rightTopSquare.length()-simplificationLevel)));
		leftBottomSquare = moveRight(moveUp(leftBottomSquare.substring(0, leftBottomSquare.length()-simplificationLevel)));
		rightBottomSquare = moveLeft(moveUp(rightBottomSquare.substring(0, rightBottomSquare.length()-simplificationLevel)));
		
		TreeSet<String> lsSquare = new TreeSet<String>();
		String curRowBeginSquare = leftTopSquare;
		String curRowEndSquare = rightTopSquare;
		String curSquare = leftTopSquare;
		
//		curRowBeginSquare = moveDown(curRowBeginSquare);
		while(curRowBeginSquare.compareTo(leftBottomSquare)<=0){
//			curSquare = moveRight(curRowBeginSquare);
			curSquare = curRowBeginSquare;
			while (curSquare.compareTo(curRowEndSquare)<=0) {
				lsSquare.add(curSquare);
				curSquare = moveRight(curSquare);
			}
			curRowBeginSquare = moveDown(curRowBeginSquare);
			curRowEndSquare = moveDown(curRowEndSquare);
		}
		return lsSquare;
		
	}

	

	
	/**
	 * 查询与指定矩形相交的网格编号列表
	 * @param minx 指定矩形最左侧经度坐标，即最小经度坐标
	 * @param miny 指定矩形最下方纬度坐标，即最小纬度坐标
	 * @param maxx 指定矩形最右侧经度坐标，即最大经度坐标
	 * @param maxy 指定矩形最上方纬度坐标，即最大纬度坐标
	 * @return 与指定矩形相交的网格编号列表
	 */
	public TreeSet<String> getCrossSquaresByRectangle(double minx, double miny, double maxx, double maxy){
		int simplificationLevel =getSimplificationLevel(minx, miny, maxx, maxy);
		return getCrossSquares(minx, miny, maxx, maxy, simplificationLevel);
	}
	
	TreeSet<String> getCrossSquares(double minx, double miny, double maxx, double maxy, int simplificationLevel){
		
		String leftTopSquare = xy2spatialIndex(minx, maxy);
		String rightTopSquare = xy2spatialIndex(maxx, maxy);
		String leftBottomSquare = xy2spatialIndex(minx, miny);
		String rightBottomSquare = xy2spatialIndex(maxx, miny);
		
		leftTopSquare = leftTopSquare.substring(0, leftTopSquare.length()-simplificationLevel);
		rightTopSquare = rightTopSquare.substring(0, rightTopSquare.length()-simplificationLevel);
		leftBottomSquare = leftBottomSquare.substring(0, leftBottomSquare.length()-simplificationLevel);
		rightBottomSquare = rightBottomSquare.substring(0, rightBottomSquare.length()-simplificationLevel);

		TreeSet<String> lsSquare = new TreeSet<String>();
		String curSquare = leftTopSquare;
		lsSquare.add(curSquare);
		while(curSquare.compareTo(rightTopSquare)<0){
			curSquare=moveRight(curSquare);
			lsSquare.add(curSquare);
		}
		while(curSquare.compareTo(rightBottomSquare)<0){
			curSquare=moveDown(curSquare);
			lsSquare.add(curSquare);
		}
		while(curSquare.compareTo(leftBottomSquare)>0){
			curSquare=moveLeft(curSquare);
			lsSquare.add(curSquare);
		}
		while(curSquare.compareTo(leftTopSquare)>0){
			curSquare=moveUp(curSquare);
			lsSquare.add(curSquare);
		}
		return lsSquare;
		
	}

	
	/**
	 * 查询与指定圆形相交的网格编号列表，不合并
	 * 此处利用外切矩形的相交网格∪外切矩形的包含网格-内接矩形的包含网格得到
	 * @param centerX 圆心经度
	 * @param centerY 圆心纬度
	 * @param radius 半径，单位为度
	 * @return 与指定圆形相交的网格编号列表
	 */
	public TreeSet<String> getCrossSquaresByCircle(double centerX, double centerY, double radius){
		double[] exRect = sh.getExteriorRectByCircle(centerX, centerY, radius);
		double[] inRect = sh.getInscribeRectByCircle(centerX, centerY, radius);
		//保障与getInnerSquaresByCircle，即对于外切和内接矩形用的是同一级别网格
		int simpLevel = getSimplificationLevel(inRect[0], inRect[1], inRect[2], inRect[3]);
		//外切矩形的相交网格
		TreeSet<String> exCrossSquares = getCrossSquares(exRect[0], exRect[1], exRect[2], exRect[3], simpLevel);
		//外切矩形的包含网格
		TreeSet<String> exInnerSquares = getInnerSquares(exRect[0], exRect[1], exRect[2], exRect[3], simpLevel);
		//内接矩形的包含网格，此处无法预先合并InnerSquares，否则无法做减法。
		TreeSet<String> inSquares = getInnerSquares(inRect[0], inRect[1], inRect[2], inRect[3],simpLevel);
		
		//圆的相交网格=外切矩形的相交网格+外切矩形的包含网格-内接矩形的包含网格
		exCrossSquares.addAll(exInnerSquares);
		exCrossSquares.removeAll(inSquares);
		return exCrossSquares;	
	}

	
	/**
	 * 查询落入指定圆形内的网格编号列表，未合并
	 * 此处即是其内接矩形的包含网格
	 * @param centerX 圆心经度
	 * @param centerY 圆心纬度
	 * @param radius 半径，单位为度
	 * @return 落入指定圆形内的网格编号列表
	 */
	public TreeSet<String> getInnerSquaresByCircle(double centerX, double centerY, double radius){
		double[] ds = sh.getInscribeRectByCircle(centerX, centerY, radius);
		return getInnerSquaresByRectangle(ds[0], ds[1], ds[2], ds[3]);
	}
	
	/**
	 * 查询与指定多边形相交网格∪被指定多边形包含网格，未合并
	 * @param axis 描述指定多边形的坐标点集合，按照点1经度、点1纬度、点2经度、点2纬度……的方式排列，该数组length必须为偶数。
	 * @return 与指定多边形相交网格∪被指定多边形包含网格
	 */
	public TreeSet<String> getCrossSquaresByPolygon(double[] axis){
//		if (isOdd(axis.length)){
//			new Exception("描述多边形的数字必须为偶数个").printStackTrace();
//			return null;
//		}
//		int count = 0;
//		double maxx = Double.MIN_VALUE;
//		double maxy = Double.MIN_VALUE;
//		double minx = Double.MAX_VALUE;
//		double miny = Double.MAX_VALUE;
//		for(double d:axis){
//			if(isOdd(count)){
//				maxy = Math.max(maxy, d);
//				miny = Math.min(miny, d);
//			}
//			else{
//				maxx = Math.max(maxx, d);
//				minx= Math.min(minx, d);
//			}
//			count++;
//		}
		double[] ds = sh.getCornerAxisByPolygon(axis);
		TreeSet<String> combinedResult = new TreeSet<String>();
		combinedResult.addAll(getCrossSquaresByRectangle(ds[0], ds[1], ds[2], ds[3]));
		combinedResult.addAll(getInnerSquaresByRectangle(ds[0], ds[1], ds[2], ds[3]));
		return combinedResult;
	}

	
	/**
	 * 查询与指定多边形相交网格∪被指定多边形包含网格，未合并
	 * @param axis 描述指定多边形的字符串，按照点1经度、点1纬度、点2经度、点2纬度……的方式排列，中间以英文逗号隔开。。
	 * @return 与指定多边形相交网格∪被指定多边形包含网格
	 */
	public TreeSet<String> getCrossSquaresByPolygon(String axis){
		double[] ds = sh.transferStringAxis2Array(axis);
		return getCrossSquaresByPolygon(ds);
		 
	}

	

	
	/*
	 * 获取单个分快的面积，单位为度数的平方
	 */
	public double getSingleSquareArea(){
		return basicXDivideLength*basicYDivideLength;
	}
	


	/**
	 * 根据查询范围获取网格简化等级，当查询范围过大时，会发生返回网格编号数目过大且计算缓慢的问题。
	 * 这个问题在合并网格前就会出现，所以合并网格不会解决此问题。
	 * 解决方法是提升网格的级别，即在查询出四个角点对应的网格编号之后，划去最后几位。
	 * 这个方法用于计算划掉几位合适。
	
	 * @param minx 指定矩形最左侧经度坐标，即最小经度坐标
	 * @param miny 指定矩形最下方纬度坐标，即最小纬度坐标
	 * @param maxx 指定矩形最上方纬度坐标，即最大纬度坐标
	 * @param maxy 指定矩形最右侧经度坐标，即最大经度坐标
	 * @return 获取网格简化的等级。
	 */
	public int getSimplificationLevel(double minx, double miny, double maxx, double maxy){
		// 此方法是查询范围(笛卡尔积)SpatialIndexUtil实例 与简化级别的映射，不是SpatialIndexUtil实例 与简化级别的映射
		//所以不能被固定
		double xSpan = (maxx-minx)/basicXDivideLength;
		double ySpan = (maxy-miny)/basicYDivideLength;
		double avgSquareSpan = 2*xSpan*ySpan/(xSpan+ySpan);
		//理解为X、Y的平均网格级别数
		double logSpan = DoubleMath.log2(avgSquareSpan);
		return Math.max((int) Math.floor(logSpan)-QUERY_KEEP_SPATIAL_LEVEL,0);
		
	}

	private String moveUp(String position){
		StringBuffer sb= new StringBuffer(position);
		int charIndex=sb.length()-1;
		while (true){
			if(sb.charAt(charIndex)=='0')
				sb.replace(charIndex, charIndex+1, "2");
			else if(sb.charAt(charIndex)=='1')
				sb.replace(charIndex, charIndex+1, "3");
			else if(sb.charAt(charIndex)=='2'){
				sb.replace(charIndex, charIndex+1, "0");
				break;
			}
			else if(sb.charAt(charIndex)=='3'){
				sb.replace(charIndex, charIndex+1, "1");
				break;
			}
				charIndex--;	
		}
		return sb.toString();
	}
	
private String moveDown(String position){
	StringBuffer sb= new StringBuffer(position);
	int charIndex=sb.length()-1;
	while (true){
		if(sb.charAt(charIndex)=='2')
			sb.replace(charIndex, charIndex+1, "0");
		else if(sb.charAt(charIndex)=='3')
			sb.replace(charIndex, charIndex+1, "1");
		else if(sb.charAt(charIndex)=='0'){
			sb.replace(charIndex, charIndex+1, "2");
			break;
		}
		else if(sb.charAt(charIndex)=='1'){
			sb.replace(charIndex, charIndex+1, "3");
			break;
		}
			charIndex--;	
	}
	return sb.toString();
	}
private String moveLeft(String position){
	StringBuffer sb= new StringBuffer(position);
	int charIndex=sb.length()-1;
	while (true){
		if(sb.charAt(charIndex)=='0')
			sb.replace(charIndex, charIndex+1, "1");
		else if(sb.charAt(charIndex)=='2')
			sb.replace(charIndex, charIndex+1, "3");
		else if(sb.charAt(charIndex)=='1'){
			sb.replace(charIndex, charIndex+1, "0");
			break;
		}
		else if(sb.charAt(charIndex)=='3'){
			sb.replace(charIndex, charIndex+1, "2");
			break;
		}
			charIndex--;	
	}
	return sb.toString();
}
private String moveRight(String position){
	StringBuffer sb= new StringBuffer(position);
	int charIndex=sb.length()-1;
	while (true){
		if(sb.charAt(charIndex)=='1')
			sb.replace(charIndex, charIndex+1, "0");
		else if(sb.charAt(charIndex)=='3')
			sb.replace(charIndex, charIndex+1, "2");
		else if(sb.charAt(charIndex)=='0'){
			sb.replace(charIndex, charIndex+1, "1");
			break;
		}
		else if(sb.charAt(charIndex)=='2'){
			sb.replace(charIndex, charIndex+1, "3");
			break;
		}
			charIndex--;	
	}
	return sb.toString();
}
	
/*public static void main(String[] args) {
	SpatialUtil siu = SpatialUtil.getChinaSpatialUtil(14);
	String s =siu.moveLeft(siu.moveDown("12110221323313"));
	System.out.println(s);
}*/
public static void main(String[] args) {
	SpatialUtil su = SpatialUtil.getChinaSpatialUtil(14);
	Set<String> ls = su.getInnerSquaresByRectangle(115.98, 39.98, 115.99, 39.99);
	System.out.println(ls);
}

}
