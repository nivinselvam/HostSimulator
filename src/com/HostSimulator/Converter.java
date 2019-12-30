package com.HostSimulator;

import java.nio.charset.Charset;

public class Converter {

	public static String[] tempArray;

	// ---------------------------------------------------------------------------------------------------
	/*
	 * This function is used to added empty spaces after every two characters in
	 * the string Since all the converters are designed to handle data in byte
	 * format, String should be altered
	 */
	// ---------------------------------------------------------------------------------------------------
	public String addSpacesToString(String hex) {
		StringBuilder sb = new StringBuilder(hex);
		for (int i = 2; i < sb.length(); i += 3) {
			sb.insert(i, " ");
		}
		return sb.toString();
	}

	// --------------------------------------------------------------------------------------------------------
	/*
	 * Takes byte array as input and return the hex string Used for converting
	 * the bytes read from the Data input stream into hex
	 */
	// ----------------------------------------------------------------------------------------------------------
	public String byteToHex(byte[] requestPacket) {
		StringBuilder hexData = new StringBuilder();
		for (byte currByte : requestPacket) {
			hexData.append(String.format("%02x ", currByte));
		}
		return hexData.toString();
	}

	// -----------------------------------------------------------------------------------------------------------
	/*
	 * Takes Hex string as input and return the decimal value of it Hex string
	 * is converted into hex array to work on on byte at a time
	 */
	// ------------------------------------------------------------------------------------------------------------
	public String hexToDecimal(String hex) {
		String decimalValue = "";
		hex = hex.replaceAll("\\s", "");
		String digits = "0123456789ABCDEF";
		hex = hex.toUpperCase();
		int val = 0;
		for (int j = 0; j < hex.length(); j++) {
			char c = hex.charAt(j);
			int d = digits.indexOf(c);
			val = 16 * val + d;
		}
		decimalValue = decimalValue + Integer.toString(val) + " ";

		return decimalValue;
	}

	// -------------------------------------------------------------------------------------------------------------
	/*
	 * Takes hex string as input and returns the ASCII value of it. Hex string
	 * is converted into hex array to work on on byte at a time
	 */
	// ---------------------------------------------------------------------------------------------------------------
	public String hexToASCII(String hex) {
		StringBuilder asciiValue = new StringBuilder();
		tempArray = hex.split(" ");
		for (String currentByte : tempArray) {
			int n = Integer.valueOf(currentByte, 16);
			asciiValue.append((char) n);
		}
		tempArray = null;
		return asciiValue.toString();
	}

	// -------------------------------------------------------------------------------------------------------------
	/*
	 * Takes hex string as input and returns the binary value of it. Hex string
	 * is converted into hex array to work on one byte at a time
	 */
	// ---------------------------------------------------------------------------------------------------------------
	public String hexToBinary(String hex) {
		String[] hexArray = hex.split(" ");
		String finalBinaryValue = "";
		for (String currentByte : hexArray) {
			int i = Integer.parseInt(currentByte, 16);
			String bin = Integer.toBinaryString(i);
			int missingNumbers = 8 - bin.length();
			String temp = "";
			/*
			 * All the zeros infront gets removed after the toBinaryString
			 * operation and the bin will always start with 1 To overcome this,
			 * missing zero's are added in front
			 */
			for (int j = 0; j < missingNumbers; j++) {
				temp = temp + "0";
			}
			finalBinaryValue = finalBinaryValue + temp + bin + " ";
		}
		return finalBinaryValue;
	}

	// -------------------------------------------------------------------------------------------------------------
	/*
	 * Takes string as input and returns the hex value of it. Spaces are added
	 * after every two characters to convert into byte format
	 */
	// ---------------------------------------------------------------------------------------------------------------
	public String asciitoHex(String asciiValue) {
		char[] chars = asciiValue.toCharArray();
		StringBuffer hex = new StringBuffer();
		String tempString = "";
		for (int i = 0; i < chars.length; i++) {
			tempString = Integer.toHexString((int) chars[i]);
			if (tempString.length() == 2) {
				hex.append(tempString);
			} else {
				hex.append("0" + tempString);
			}

		}
		hex = new StringBuffer(addSpacesToString(hex.toString()));
		return hex.toString();
	}

	// -------------------------------------------------------------------------------------------------------------
	/*
	 * Takes bitmap String as input and returns the hex value of it. Bitmap
	 * input should be divided into 8 bits followed by space Eg.(10101010
	 * 00001111)
	 */
	// ---------------------------------------------------------------------------------------------------------------
	public String binaryToHex(String bitmap) {
		
		if(bitmap.contains(" ")==false){
			StringBuffer sb = new StringBuffer(bitmap);
			for (int i = 8; i < sb.length(); i += 9) {
				sb.insert(i, " ");
			}
			bitmap = sb.toString();
		}
		
		String hexData = "", currentElementHexValue = "";
		tempArray = bitmap.split(" ");
		for (String currentElement : tempArray) {
			currentElementHexValue = String.format("%X", Long.parseLong(currentElement, 2));
			if (currentElementHexValue.length() == 2) {
				hexData = hexData + currentElementHexValue + " ";
			} else {
				hexData = hexData + "0" + currentElementHexValue + " ";
			}
		}
		return hexData.trim();
	}
	
	
	
	
	public static String toHexString(String hex) {
        if(hex == null || hex.trim().length() == 0){
               System.out.println("Cannot convert null HexString to ByteString! ");
               return("");
        }
     int l = hex.length();
     if(l%2 != 0){
        hex = "0" + hex;
     }
     l = hex.length();
     byte[] data = new byte[l/2];
     for (int i = 0; i < l; i += 2) {
         data[i/2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                              + Character.digit(hex.charAt(i+1), 16));
     }
     return new String(data, Charset.forName("ISO-8859-1"));
 }

}