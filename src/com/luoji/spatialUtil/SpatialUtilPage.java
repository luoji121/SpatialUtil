package com.luoji.spatialUtil;

import java.util.Iterator;
import java.util.TreeSet;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterators;
/** 
 * Description: 
 * 四叉树网格剖分工具，分页
 * @author LuoJi
 * @version 1.0 
 */
/* <br/>Copyright(C),2013-2033,Ji Luo
* <br/>This program is protected by copyright laws. 
* <br/>四叉树网格剖分工具
*/
public class SpatialUtilPage {
	private SpatialUtil siu;
	private SpatialHelper sh = new SpatialHelper();
	/**
	 * 实例化适用于中国的四叉树网格剖分工具，带分页功能。
	 * @param spatiallevel 剖分级别，整体范围将被划分成4^spatiallevel个网格，可以不为整数，通常为12-18之间
	 * @return 适用于中国的四叉树网格剖分工具，带分页功能。
	 */
	public static SpatialUtilPage getChinaSpatiaUtilPage(double spatialLevel){
		SpatialUtilPage sup= new SpatialUtilPage();
		sup.setSiu(SpatialUtil.getChinaSpatialUtil(spatialLevel));
		return sup;	 
	}
	/**
	 * 实例化适用于世界的四叉树网格剖分工具，带分页功能。
	 * @param spatiallevel 剖分级别，整体范围将被划分成4^spatiallevel个网格，可以不为整数，通常为13-19之间
	 * @return 适用于世界的四叉树网格剖分工具，带分页功能。
	 */
	public static SpatialUtilPage getWorldSpatialUtilPage(double spatialLevel){
		SpatialUtilPage sup= new SpatialUtilPage();
		sup.setSiu(SpatialUtil.getWorldSpatialUtil(spatialLevel));
		return sup;	
	}
	/**
	 * 实例化适用于用户定义范围的四叉树网格剖分工具，带分页功能。
	 * @param basicX 精度最小值，西经为负，东经为正，通常为范围的最西端经度
	 * @param basicY 纬度最大值，南纬为负，北纬为正，即为范围的最北端纬度
	 * @param totalLongitude 范围的经度跨度
	 * @param totalLatitude 范围的纬度跨度
	 * @param spatiallevel 剖分级别，整体范围将被划分成4^spatiallevel个网格，可以不为整数
	 * @return 适用于用户定义范围的四叉树网格剖分工具，带分页功能。
	 */
	public static SpatialUtilPage getUserSpatialUtilPage(double basicX,double basicY,
			double totalLongitude,double totalLatitude,double spatiallevel){
		SpatialUtilPage sup= new SpatialUtilPage();
		sup.setSiu(SpatialUtil.getUserSpatialUtil(basicX, basicY, totalLongitude,totalLatitude,spatiallevel));
		return sup;	
	}
	
	private void setSiu(SpatialUtil siu) {
		this.siu = siu;
	}
	/**
	 * 查询与指定矩形相交的网格编号列表,带分页
	 * @param minx 指定矩形最左侧经度坐标，即最小经度坐标
	 * @param miny 指定矩形最下方纬度坐标，即最小纬度坐标
	 * @param maxx 指定矩形最右侧经度坐标，即最大经度坐标
	 * @param maxy 指定矩形最上方纬度坐标，即最大纬度坐标
	 * @param pageNumber 页号
	 * @return 指定分页下与矩形相交的网格编号列表，结果为最基本级别的矩形网格，没有简化或合并
	 */
	public TreeSet<String> getCrossSquaresByRectangleAndPage(double minx, double miny, double maxx, double maxy, long pageNumber){
		int simplificationLevel =siu.getSimplificationLevel(minx, miny, maxx, maxy);
		TreeSet<String> ts =  siu.getCrossSquares(minx, miny, maxx, maxy, simplificationLevel);
		return addSuffix(ts, pageNumber, simplificationLevel);
	}
	
	/**
	 * 查询落入指定矩形内部的网格编号列表,带分页
	 * @param minx 指定矩形最左侧经度坐标，即最小经度坐标
	 * @param miny 指定矩形最下方纬度坐标，即最小纬度坐标
	 * @param maxx 指定矩形最右侧经度坐标，即最大经度坐标
	 * @param maxy 指定矩形最上方纬度坐标，即最大纬度坐标
	 * @param pageNumber 页号
	 * @return 指定分页下矩形内部的网格编号列表，结果为最基本级别的矩形网格，没有简化或合并
	 */
	public TreeSet<String> getInnerSquaresByRectangleAndPage(double minx, double miny, double maxx, double maxy, long pageNumber){
		int simplificationLevel =siu.getSimplificationLevel(minx, miny, maxx, maxy);
		TreeSet<String> ts =  siu.getInnerSquares(minx, miny, maxx, maxy, simplificationLevel);
		return addSuffix(ts, pageNumber, simplificationLevel);
	}
	
	/**
	 * 查询与指定圆形相交的网格编号列表，带分页
	 * 此处利用外切矩形的相交网格∪外切矩形的包含网格-内接矩形的包含网格得到
	 * @param centerX 圆心经度
	 * @param centerY 圆心纬度
	 * @param radius 半径，单位为度
	 * @param pageNumber 页号
	 * @return 指定分页下与圆形相交的网格编号列表
	 */
	public TreeSet<String> getCrossSquaresByCircleAndPage(double centerX, double centerY, double radius, long pageNumber){
		double[] ds = sh.getInscribeRectByCircle(centerX, centerY, radius);
		//保障与getInnerSquaresByCircle，即对于外切和内接矩形用的是同一级别网格,不可改为Exterior的
		int simplificationLevel = siu.getSimplificationLevel(ds[0], ds[1], ds[2], ds[3]);
		TreeSet<String> ts =  siu.getCrossSquaresByCircle(centerX, centerY, radius);
		return addSuffix(ts, pageNumber, simplificationLevel);
	}
	
	/**
	 * 查询落入指定圆形内的网格编号列表，带分页
	 * 此处即是其内接矩形的包含网格
	 * @param centerX 圆心经度
	 * @param centerY 圆心纬度
	 * @param radius 半径，单位为度
	 * @param pageNumber 页号
	 * @return 指定分页下的圆形内的网格编号列表
	 */
	public TreeSet<String> getInnerSquaresByCircleAndPage(double centerX, double centerY, double radius, long pageNumber){
		double[] ds = sh.getInscribeRectByCircle(centerX, centerY, radius);
		//保障与getCrossSquaresByCircle，即对于外切和内接矩形用的是同一级别网格,不可改为Exterior的
		int simplificationLevel = siu.getSimplificationLevel(ds[0], ds[1], ds[2], ds[3]);
		TreeSet<String> ts =  siu.getInnerSquaresByCircle(centerX, centerY, radius);
		return addSuffix(ts, pageNumber, simplificationLevel);
	}
	
	/**
	 * 查询与指定分页下,多边形相交网格∪多边形包含网格，带分页
	 * @param axis 描述指定多边形的坐标点集合，按照点1经度、点1纬度、点2经度、点2纬度……的方式排列，该数组length必须为偶数。
	 * @param pageNumber 页号
	 * @return 指定分页下,多边形相交网格∪多边形包含网格
	 */
	public TreeSet<String> getCrossSquaresByPolygonAndPage(double[] axis, long pageNumber){
		if (isOdd(axis.length)){
			new Exception("描述多边形的数字必须为偶数个").printStackTrace();
			return null;
		}
		int count = 0;
		double maxx = Double.MIN_VALUE;
		double maxy = Double.MIN_VALUE;
		double minx = Double.MAX_VALUE;
		double miny = Double.MAX_VALUE;
		for(double d:axis){
			if(isOdd(count)){
				maxy = Math.max(maxy, d);
				miny = Math.min(miny, d);
			}
			else{
				maxx = Math.max(maxx, d);
				minx= Math.min(minx, d);
			}
			count++;
		}
		int simpLevel = siu.getSimplificationLevel(minx, miny, maxx, maxy);
		TreeSet<String> ts = siu.getCrossSquaresByPolygon(axis);		
		return addSuffix(ts, pageNumber, simpLevel);
	}
	
	/**
	 * 查询与指定多边形相交网格∪被指定多边形包含网格，带分页
	 * @param axis 描述指定多边形的字符串，按照点1经度、点1纬度、点2经度、点2纬度……的方式排列，中间以英文逗号隔开。
	 * @param pageNumber 页号
	 * @return 与指定多边形相交网格∪被指定多边形包含网格
	 */
	public TreeSet<String> getCrossSquaresByPolygonAndPage(String axis, long pageNumber){
		Iterator<String> is = Splitter.on(",").split(axis).iterator();
		double[] ds=new double[Iterators.size(is)];
		 is = Splitter.on(",").split(axis).iterator();
		int count = 0;
		while(is.hasNext()){
			ds[count]=Double.parseDouble(is.next());
			count++;
		}
		return getCrossSquaresByPolygonAndPage(ds, pageNumber);
		 
	}
	
	/**
	 * 根据查询范围获取总页数
	 * @param minx 指定矩形最左侧经度坐标，即最小经度坐标
	 * @param miny 指定矩形最下方纬度坐标，即最小纬度坐标
	 * @param maxx 指定矩形最上方纬度坐标，即最大纬度坐标
	 * @param maxy 指定矩形最右侧经度坐标，即最大经度坐标
	 * @return 总页数
	 */
	public long getTotalPageNumbers(double minx, double miny, double maxx, double maxy){
		return (long) Math.pow(4, siu.getSimplificationLevel(minx, miny, maxx, maxy));
	}
	
	//为简化后的网格代码，添加尾部的suffix，用于分页
	private TreeSet<String> addSuffix(TreeSet<String> squaresList, long pageNumber, int simplificationLevel){
		//pageNumber从0开始
		//simplificationLevel为1时，pageNumber0-3，为2时，0-15
		if(pageNumber>Math.pow(4,simplificationLevel)-1){
			//翻页结束，返回空值
			new Exception("翻页超出范围").printStackTrace();
			return new TreeSet<String>();
		}else if(simplificationLevel==0){//当没有简化时，直接返回。
			return squaresList;
		}
		else{
			TreeSet<String> tsReturn = new TreeSet<String>();
			String suffix = Strings.padStart(Long.toString(pageNumber, 4), simplificationLevel, '0');
			
			for(String s:squaresList){
				tsReturn.add(s.concat(suffix));
			}
			return tsReturn;
		}
	}
	
	private boolean isOdd(int i){//按位与判断奇偶
		return (i&1)!=0; 
	}
}
