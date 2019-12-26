package com.HostSimulator;


import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class Tester {
	public static TreeMap<String, String> requestBitfieldsWithValues = new TreeMap<String, String>();
	
	public static void main(String[] args) {
		Map<String, String> responseBitfieldWithValues = new TreeMap<String, String>();
		requestMap();

		TreeSet<Integer> elementsInTransaction = new TreeSet<Integer>(Arrays.asList(HPSConstants.elementsInGenericTransaction));

		for(Integer currentEntry: elementsInTransaction) {
			String key = "BITFIELD" + currentEntry;
			responseBitfieldWithValues.put(key, requestBitfieldsWithValues.get(key));
		}
		
		for(Map.Entry<String, String> currentEntry : responseBitfieldWithValues.entrySet()) {
			System.out.println(currentEntry);
		}
	}

	public static TreeMap<String, String> requestMap() {
		requestBitfieldsWithValues.put("BITFIELD3", "1");
		requestBitfieldsWithValues.put("BITFIELD4", "2");
		requestBitfieldsWithValues.put("BITFIELD11", "3");
		requestBitfieldsWithValues.put("BITFIELD12", "4");
		requestBitfieldsWithValues.put("BITFIELD18", "5");
		requestBitfieldsWithValues.put("BITFIELD19", "6");
		requestBitfieldsWithValues.put("BITFIELD22", "7");
		requestBitfieldsWithValues.put("BITFIELD23", "8");
		requestBitfieldsWithValues.put("BITFIELD24", "9");
		requestBitfieldsWithValues.put("BITFIELD35", "0");
		requestBitfieldsWithValues.put("BITFIELD41", "11");
		requestBitfieldsWithValues.put("BITFIELD42", "12");
		requestBitfieldsWithValues.put("BITFIELD48", "13");
		requestBitfieldsWithValues.put("BITFIELD49", "14");
		requestBitfieldsWithValues.put("BITFIELD55", "15");
		requestBitfieldsWithValues.put("BITFIELD62", "16");
		return requestBitfieldsWithValues;
	}

}