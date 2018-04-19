package com.luoji.spatialUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterators;

public class SpatialHelper {
	
	//获取多边形的角点坐标，minx,miny,maxx,maxy
	public double[] getCornerAxisByPolygon(double[] axis){
		double[] ds = new double[4];
		if (isOdd(axis.length)){
			new Exception("描述多边形的数字必须为偶数个").printStackTrace();
			return null;
		}
		int count = 0;
		ds[2] = Double.MIN_VALUE;
		ds[3] = Double.MIN_VALUE;
		ds[0] = Double.MAX_VALUE;
		ds[1] = Double.MAX_VALUE;
		for(double d:axis){
			if(isOdd(count)){
				ds[3] = Math.max(ds[3], d);
				ds[1] = Math.min(ds[1], d);
			}
			else{
				ds[2] = Math.max(ds[2], d);
				ds[0]= Math.min(ds[0], d);
			}
			count++;
		}
		return ds;
	}
	
	//获取多边形的中心点坐标
	public double[] getCenterAxisByPolygon(double[] axis){
		double[] ds = getCornerAxisByPolygon(axis);
		double[] dsc = new double[2];
		dsc[0] = (ds[0]+ds[2])/2;
		dsc[1] = (ds[1]+ds[3])/2;
		return dsc;
	}
	
	
	public double[] transferStringAxis2Array(String axis){
		Iterator<String> is = Splitter.on(",").split(axis).iterator();
		double[] ds=new double[Iterators.size(is)];
		 is = Splitter.on(",").split(axis).iterator();
		int count = 0;
		while(is.hasNext()){
			ds[count]=Double.parseDouble(is.next());
			count++;
		}
		return ds;
	}
	
	/*
	 * 估算的多边形面积，单位为度数的平方，适用于凹多边形，但不适用于有交叉边的多边形
	 */
	public double estimatePolygonArea(double[] axis){
		double area = 0;
		List<Double> xs = new ArrayList<Double>();
		List<Double> ys = new ArrayList<Double>();
		int count = 0;
		for(double d:axis){
			if(!isOdd(count)){
				xs.add(d);
			}else{
				ys.add(d);
			}
			count++;
		}
		int numPoints = count/2;
		int j = numPoints-1;
		for (int i=0; i<numPoints; i++)
		    { area = area +  (xs.get(j)+xs.get(i)) * (ys.get(j)-ys.get(i)); 
		      j = i;  //j is previous vertex to i
		    }
		return area/2;
	}
	
	/*
	 * 获取内接矩形，三个参数分别为圆心经纬度、半径，半径的单位为度。
	 * 这个圆是经纬度投影下的圆，真实情况应当不是圆，在北半球是上小下大。
	 * 但由于电子地图都是经纬度投影，所以在电子地图上画的圆，实际上都是参数中这样的圆
	 */
	public double[] getInscribeRectByCircle(double centerX, double centerY, double radius){
		double[] rect = new double[4];
		double sqrtRadius = radius/Math.sqrt(2);
		rect[0]=centerX-sqrtRadius;
		rect[1]=centerY-sqrtRadius;
		rect[2]=centerX+sqrtRadius;
		rect[3]=centerY+sqrtRadius;
		return rect;
		
	}
	/*
	 * 获取外切矩形
	 */
	public double[] getExteriorRectByCircle(double centerX, double centerY, double radius){
		double[] rect = new double[4];
		rect[0]=centerX-radius;
		rect[1]=centerY-radius;
		rect[2]=centerX+radius;
		rect[3]=centerY+radius;
		return rect;
		
	}
	
	private boolean isOdd(int i){//按位与判断奇偶
		return (i&1)!=0; 
	}
}
