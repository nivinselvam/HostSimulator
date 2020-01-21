package com.HostSimulator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;




public class Tester {

	
	public static void main(String[] args) {
		
		String request = "303230303238008108c08c203138393139313030303030303030323530303230323030313231543130353732345a3030303030303030333232363130353732342d3035303032303230303132313030313031323334353132333435303030303030303030333935313233343530303456657269666f6e6531323334352020383430303030303030303030303030303030303031323739393336363632393336383030344543484f";
		Responses response = new Responses(request);
		System.out.println(response.getResponsePacket());
		

		
	}
}