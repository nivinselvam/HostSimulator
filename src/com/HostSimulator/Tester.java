package com.HostSimulator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class Tester {	

	public static void main(String[] args) throws IOException {
		File file = new File("HPSConstants.properties");
		
		FileInputStream fis = new FileInputStream(file);
		Properties p = new Properties();
		p.load(fis);
		
		System.out.println(p.getProperty("valueOfBitfield123"));
		
		
		
	}

}