package com.HostSimulator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Tester {
	public static void main(String[] args) {
		
		Properties p = new Properties();
		File file = new File("HPSConstants.properties");

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			p.load(fis);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(generateIntegerArrayFromString(p.getProperty("elementsInGenericTransaction")));	
		
	}	
	public static Integer[] generateIntegerArrayFromString(String elementsInTransaction) {
		elementsInTransaction = elementsInTransaction.replaceAll("\\s", "");
		Integer[] elementsInTransactionArrayIntegers = new Integer[elementsInTransaction.split(",").length];
		int i = 0;
		for(String currentString: elementsInTransaction.split(",")) {
			elementsInTransactionArrayIntegers[i] = Integer.parseInt(currentString);
			i++;
		}		
		return elementsInTransactionArrayIntegers;
	}
}