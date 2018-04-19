package com.luoji.spatialUtil;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;


public class TreeMergeTool {

	int radix = 4;

	public TreeSet<String> merge(TreeSet<String> originalNumbers) {
		// 这个merge，只是一个增加效率的方式，即便有一些应当merge而没有merge的情况，也不会影响最终结果。
		boolean completeFlag = false;
		TreeSet<String> tempSet0 = new TreeSet<String>();
		TreeSet<String> tempSet1 = new TreeSet<String>();
		TreeSet<String> sameRectSet = new TreeSet<String>();
		//tempSet1 = originalNumbers;// 废话，但如果需要保留originalNumbers，考虑改用AddAll方法。
		tempSet1.addAll(originalNumbers);
		while (!completeFlag && originalNumbers.size() > 1) {// 每执行完一遍，如果有融合,completeFlag为false;没有，为true，结束循环
			tempSet0.clear();//tempSet0用于指针移动、tempSet1用于存储合并后的结果。
			tempSet0.addAll(tempSet1);
			Iterator<String> its = tempSet0.iterator();
			String lastHex = its.next();
			String curHex = its.next();
			completeFlag = true;
			//下面的循环 每执行完一遍，如果有融合，继续,completeFlag设为false
			while (its.hasNext()) {// 每执一遍相当于过一遍数据

				while (!belongsToSameRect(lastHex, curHex) && its.hasNext()) {// 每执行一遍，指针位移一位
					lastHex = curHex;
					curHex = its.next();
				}
				if (its.hasNext()) {// 当某遍内层循环不走此处时，证明融合已经结束，completeFlag保持为true，外层循环结束。					
					sameRectSet.clear();
					sameRectSet.add(lastHex);
//					sameRectSet.add(curHex);
					for (int i = 0; i < radix-1; i++) {
						if (its.hasNext()) {
							long lastHexL = Long.parseLong(lastHex, radix);
							long curHexL = Long.parseLong(curHex, radix);
							if (curHexL - lastHexL - i == 1) {
								sameRectSet.add(curHex);
								curHex = its.next();
								
							} else
								break;
						} else{
							long lastHexL = Long.parseLong(lastHex, radix);
							long curHexL = Long.parseLong(curHex, radix);
							if (curHexL - lastHexL - i == 1) {
								sameRectSet.add(curHex);
							}
							break;
						}
							
					}
					if (sameRectSet.size() == radix) {
						// 删除合并后低一级的所有Rect，加入更高一级的Rect。
						tempSet1.removeAll(sameRectSet);
						tempSet1.add(lastHex.substring(0, lastHex.length() - 1));
						// 每执行完一遍，如果有融合，继续,completeFlag设为false
						completeFlag = false;
					}
					lastHex = curHex;
					if (its.hasNext()) {
						curHex = its.next();
					} else
						break;
				}
				// else{//!its.hasNext()
				// break;
				// }
			}
		}
		return tempSet1;
	}

	private boolean belongsToSameRect(String lastHex, String curHex) {
		if (!lastHex.endsWith("0")) {
			return false;
		} else if (lastHex.length() != curHex.length()) {
			return false;
		} else
			return true;
	}

}
