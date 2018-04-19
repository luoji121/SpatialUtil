package com.luoji.spatialUtil;

import java.util.TreeSet;

import com.google.common.math.DoubleMath;
/** 
 * Description: 
 * 四叉树网格剖分工具，合并查询结果
 * @author LuoJi
 * @version 1.0 
 */
/* <br/>Copyright(C),2013-2033,Ji Luo
* <br/>This program is protected by copyright laws. 
* <br/>四叉树网格剖分工具
*/
public class SpatialUtilCombine {
	private SpatialUtil siu;
	private TreeMergeTool tmt = new TreeMergeTool();
	private SpatialHelper sh = new SpatialHelper();
	
	/**
	 * 实例化适用于中国的四叉树网格剖分工具，该工具会合并查询结果。
	 * @param spatiallevel 剖分级别，整体范围将被划分成4^spatiallevel个网格，可以不为整数，通常为12-18之间
	 * @return 适用于中国的四叉树网格剖分工具，该工具会合并查询结果。
	 */
	public static SpatialUtilCombine getChinaSpatiaUtilCombine(double spatialLevel){
		SpatialUtilCombine suc= new SpatialUtilCombine();
		suc.setSiu(SpatialUtil.getChinaSpatialUtil(spatialLevel));
		return suc;	
	}
	/**
	 * 实例化适用于世界的四叉树网格剖分工具，该工具会合并查询结果。
	 * @param spatiallevel 剖分级别，整体范围将被划分成4^spatiallevel个网格，可以不为整数，通常为13-19之间
	 * @return 适用于世界的四叉树网格剖分工具，该工具会合并查询结果。
	 */
	public static SpatialUtilCombine getWorldSpatialUtilCombine(double spatialLevel){
		SpatialUtilCombine suc= new SpatialUtilCombine();
		suc.setSiu(SpatialUtil.getWorldSpatialUtil(spatialLevel));
		return suc;	
	}
	/**
	 * 实例化适用于用户定义范围的四叉树网格剖分工具，该工具会合并查询结果。
	 * @param basicX 精度最小值，西经为负，东经为正，通常为范围的最西端经度
	 * @param basicY 纬度最大值，南纬为负，北纬为正，即为范围的最北端纬度
	 * @param totalLongitude 范围的经度跨度
	 * @param totalLatitude 范围的纬度跨度
	 * @param spatiallevel 剖分级别，整体范围将被划分成4^spatiallevel个网格，可以不为整数
	 * @return 适用于用户定义范围的四叉树网格剖分工具，该工具会合并查询结果。
	 */
	public static SpatialUtilCombine getUserSpatialUtilCombine(double basicX,double basicY,
			double totalLongitude,double totalLatitude,double spatiallevel){
		SpatialUtilCombine suc= new SpatialUtilCombine();
		suc.setSiu(SpatialUtil.getUserSpatialUtil(basicX, basicY, totalLongitude,totalLatitude,spatiallevel));
		return suc;	
	}
	
	private void setSiu(SpatialUtil siu) {
		this.siu = siu;
	}
	
	/**
	 * 查询与指定矩形相交的网格编号列表，按四叉树方式合并之后的结果
	 * 不推荐使用。因为根本不会合并任何结果，放在这里是为了接口的统一。
	 * 此处推荐使用SpatialUtil的getCrossSquaresByRectangle
	 * @param minx 指定矩形最左侧经度坐标，即最小经度坐标
	 * @param miny 指定矩形最下方纬度坐标，即最小纬度坐标
	 * @param maxx 指定矩形最右侧经度坐标，即最大经度坐标
	 * @param maxy 指定矩形最上方纬度坐标，即最大纬度坐标
	 * @return 矩形内部的网格编号列表，按四叉树方式合并之后的结果
	 */
	public TreeSet<String> getCrossCombinedSquaresByRectangle(double minx, double miny, double maxx, double maxy){
		
		TreeSet<String> ls = siu.getCrossSquaresByRectangle(minx, miny, maxx, maxy);
		return tmt.merge(ls);
	}
	
	/**
	 * 查询落入指定矩形内部的网格编号列表，按四叉树方式合并之后的结果
	 * @param minx 指定矩形最左侧经度坐标，即最小经度坐标
	 * @param miny 指定矩形最下方纬度坐标，即最小纬度坐标
	 * @param maxx 指定矩形最右侧经度坐标，即最大经度坐标
	 * @param maxy 指定矩形最上方纬度坐标，即最大纬度坐标
	 * @return 矩形内部的网格编号列表，按四叉树方式合并之后的结果
	 */
	public TreeSet<String> getInnerCombinedSquaresByRectangle(double minx, double miny, double maxx, double maxy){
		
		TreeSet<String> ls = siu.getInnerSquaresByRectangle(minx, miny, maxx, maxy);
		return tmt.merge(ls);
	}
	
	/**
	 * 查询与指定圆形相交的网格编号列表，合并四叉树后的结果
	 * 此处利用外切矩形的相交网格∪外切矩形的包含网格-内接矩形的包含网格得到
	 * @param centerX 圆心经度
	 * @param centerY 圆心纬度
	 * @param radius 半径，单位为度
	 * @return 与指定圆形相交的网格编号列表
	 */
	public TreeSet<String> getCrossCombinedSquaresByCircle(double centerX, double centerY, double radius){
		TreeSet<String> circleCrossSquares = siu.getCrossSquaresByCircle(centerX, centerY, radius);
		return tmt.merge(circleCrossSquares);	
	}
	/**
	 * 查询落入指定圆形内的网格编号列表，合并四叉树后的结果
	 * 此处即是其内接矩形的包含网格
	 * @param centerX 圆心经度
	 * @param centerY 圆心纬度
	 * @param radius 半径，单位为度
	 * @return 落入指定圆形内的网格编号列表
	 */
	public TreeSet<String> getInnerCombinedSquaresByCircle(double centerX, double centerY, double radius){
		double[] ds = sh.getInscribeRectByCircle(centerX, centerY, radius);
		return getInnerCombinedSquaresByRectangle(ds[0], ds[1], ds[2], ds[3]);
	}
	
	/**
	 * 查询与指定多边形相交网格∪被指定多边形包含网格，合并四叉树后的结果
	 * @param axis 描述指定多边形的坐标点集合，按照点1经度、点1纬度、点2经度、点2纬度……的方式排列，该数组length必须为偶数。
	 * @return 与指定多边形相交网格∪被指定多边形包含网格
	 */
	public TreeSet<String> getCrossCombinedSquaresByPolygon(double[] axis){
		TreeSet<String> combinedResult = siu.getCrossSquaresByPolygon(axis);
		return tmt.merge(combinedResult);
	}
	
	/**
	 * 查询与指定多边形相交网格∪被指定多边形包含网格，合并四叉树后的结果
	 * @param axis 描述指定多边形的字符串，按照点1经度、点1纬度、点2经度、点2纬度……的方式排列，中间以英文逗号隔开。。
	 * @return 与指定多边形相交网格∪被指定多边形包含网格
	 */
	public TreeSet<String> getCrossCombinedSquaresByPolygon(String axis){
		TreeSet<String> combinedResult = siu.getCrossSquaresByPolygon(axis);
		return tmt.merge(combinedResult);
	}
	
}
