package com.luoji.spatialUtil.test;

import static org.junit.Assert.*;

import java.util.TreeSet;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.luoji.spatialUtil.SpatialUtil;
import com.luoji.spatialUtil.SpatialUtilCombine;
import com.luoji.spatialUtil.TreeMergeTool;

public class TestSpatialUtilCombine {

	SpatialUtilCombine suc = SpatialUtilCombine.getChinaSpatiaUtilCombine(Constants.TEST_SPATIAL_LEVEL);
	int testLevel = 2;
	TreeMergeTool tmt = new TreeMergeTool();
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetCrossSquaresByRectangle() {
		TreeSet<String> ts = new TreeSet<String>();
		switch (testLevel) {
		case 1:
			ts = suc.getCrossCombinedSquaresByRectangle(113.8, 39.9, 114, 40.1);
			break;
		case 2:
			ts = suc.getCrossCombinedSquaresByRectangle(113, 39, 114, 40);
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
			ts = suc.getInnerCombinedSquaresByRectangle(113.8, 39.9, 114, 40.1);
			break;
		case 2:
			ts = suc.getInnerCombinedSquaresByRectangle(113, 39, 114, 40);
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
			ts = suc.getCrossCombinedSquaresByCircle(113.9, 40, 0.1);
			break;
		case 2:
			ts = suc.getCrossCombinedSquaresByCircle(113.5, 39.5, 0.5);
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
			ts = suc.getInnerCombinedSquaresByCircle(113.9, 40, 0.1);
			break;
		case 2:
			ts = suc.getInnerCombinedSquaresByCircle(113.5, 39.5, 0.5);
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
			ts = suc.getCrossCombinedSquaresByPolygon(
					"113.8,39.99,113.84,39.9,113.89, 40.05,113.98,39.93,114,40.1");
			break;
		case 2:
			ts = suc.getCrossCombinedSquaresByPolygon(
					"113.8,39.99,113,40,114,39,113.98,39.93,114,39.5");
			break;
		default:
			break;
		}
		System.out.println("CrossPolygon"+ts.size());
		System.out.println(ts);
	}
	
	@Test
	public void testGetTotalSquaresByRectangle(){
		//用于验证正确性，结果应当与testGetCrossSquaresByPolygon、testGetTotalSquaresByCircle相同
		//circle可以不同！简化级别不同
		TreeSet<String> ts0 = new TreeSet<String>();
		TreeSet<String> ts1 = new TreeSet<String>();
		switch (testLevel) {
		case 1:
			ts0 = suc.getCrossCombinedSquaresByRectangle(113.8, 39.9, 114, 40.1);
			ts1 = suc.getInnerCombinedSquaresByRectangle(113.8, 39.9, 114, 40.1);
			break;
		case 2:
			ts0 = suc.getCrossCombinedSquaresByRectangle(113, 39, 114, 40);
			ts1 = suc.getInnerCombinedSquaresByRectangle(113, 39, 114, 40);
			break;
		default:
			break;
		}
		ts0.addAll(ts1);
		ts0=tmt.merge(ts0);
		System.out.println("TotalRectangle:"+ts0.size());
		System.out.println(ts0);
	}
	
	@Test
	public void testGetTotalSquaresByCircle(){
		//用于验证正确性，结果应当与testGetCrossSquaresByPolygon、testGetTotalSquaresByRectangle相同
		//circle可以不同！简化级别不同
		TreeSet<String> ts0 = new TreeSet<String>();
		TreeSet<String> ts1 = new TreeSet<String>();
		switch (testLevel) {
		case 1:
			ts0 = suc.getCrossCombinedSquaresByCircle(113.9, 40, 0.1);
			ts1 = suc.getInnerCombinedSquaresByCircle(113.9, 40, 0.1);
			break;
		case 2:
			ts0 = suc.getCrossCombinedSquaresByCircle(113.5, 39.5, 0.5);
			ts1 = suc.getInnerCombinedSquaresByCircle(113.5, 39.5, 0.5);
			break;
		default:
			break;
		}
		ts0.addAll(ts1);
		ts0=tmt.merge(ts0);
		System.out.println("TotalCircle:"+ts0.size());
		System.out.println(ts0);
	}
}
