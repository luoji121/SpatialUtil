package com.luoji.spatialUtil.test;

import static org.junit.Assert.*;

import java.util.TreeSet;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.luoji.spatialUtil.SpatialUtilPage;

public class TestSpatialUtilPage {
	int testLevel =2;
	long pageNumber = 37;
	SpatialUtilPage sup = SpatialUtilPage.getChinaSpatiaUtilPage(Constants.TEST_SPATIAL_LEVEL);
	@Test
	public void testGetCrossSquaresByRectangle() {
		TreeSet<String> ts = new TreeSet<String>();
		switch (testLevel) {
		case 1:
			ts = sup.getCrossSquaresByRectangleAndPage(113.8, 39.9, 114, 40.1,pageNumber);
			break;
		case 2:
			ts = sup.getCrossSquaresByRectangleAndPage(113, 39, 114, 40,pageNumber);
			break;
		default:
			break;
		}
		System.out.println("CrossRectangle:"+ts.size());
		System.out.println(ts);
	}
	
	@Test
	public void testGetInnerSquaresByRectangle() {
		TreeSet<String> ts = new TreeSet<String>();
		switch (testLevel) {
		case 1:
			ts = sup.getInnerSquaresByRectangleAndPage(113.8, 39.9, 114, 40.1,pageNumber);
			break;
		case 2:
			ts = sup.getInnerSquaresByRectangleAndPage(113, 39, 114, 40,pageNumber);
			break;
		default:
			break;
		}
		System.out.println("InnerRectangle:"+ts.size());
		System.out.println(ts);
	}
	
	@Test
	public void testGetCrossSquaresByCircle() {
		TreeSet<String> ts = new TreeSet<String>();
		switch (testLevel) {
		case 1:
			ts = sup.getCrossSquaresByCircleAndPage(113.9, 40, 0.1,pageNumber);
			break;
		case 2:
			ts = sup.getCrossSquaresByCircleAndPage(113.5, 39.5, 0.5,pageNumber);
			break;
		default:
			break;
		}
		System.out.println("CrossCircle:"+ts.size());
		System.out.println(ts);
	}
	
	@Test
	public void testGetInnerSquaresByCircle() {
		TreeSet<String> ts = new TreeSet<String>();
		switch (testLevel) {
		case 1:
			ts = sup.getInnerSquaresByCircleAndPage(113.9, 40, 0.1,pageNumber);
			break;
		case 2:
			ts = sup.getInnerSquaresByCircleAndPage(113.5, 39.5, 0.5,pageNumber);
			break;
		default:
			break;
		}
		System.out.println("InnerCircle:"+ts.size());
		System.out.println(ts);
	}

	@Test
	public void testGetCrossSquaresByPolygon() {
		TreeSet<String> ts = new TreeSet<String>();
		switch (testLevel) {
		case 1:
			ts = sup.getCrossSquaresByPolygonAndPage(
					"113.8,39.99,113.84,39.9,113.89,40.05,113.98,39.93,114,40.1",pageNumber);
			break;
		case 2:
			ts = sup.getCrossSquaresByPolygonAndPage(
					"113.8,39.99,113,40,114,39,113.98,39.93,114,39.5",pageNumber);
			break;
		default:
			break;
		}
		System.out.println("CrossPolygon:"+ts.size());
		System.out.println(ts);
	}

	@Test
	public void testGetTotalPageNumbers() {
		long count = 0;
		switch (testLevel) {
		case 1:
			count = sup.getTotalPageNumbers(113.8, 39.9, 114, 40.1);
			break;
		case 2:
			count = sup.getTotalPageNumbers(113, 39, 114, 40);
			break;
		default:
			break;
		}
		System.out.println("TotalNumber:"+count);
	}

}
