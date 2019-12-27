package com.HostSimulator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public final class Constants {
	public static Properties p = new Properties();

	static {
		String fepFile = null;
		
		switch(Main.fepName) {
		case "HPS": fepFile = "HPSConstants.properties";
		break;
		}

		File file = new File(fepFile);

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
	}

	private Constants() {

	}

	// Transaction Status
	public static final String authorizationTransactionResponse = p.getProperty("authorizationTransactionResponse");
	public static final String financialSalesTransactionResponse = p.getProperty("financialSalesTransactionResponse");
	public static final String financialForceDraftTransactionResponse = p.getProperty("financialForceDraftTransactionResponse");
	public static final String reversalTransactionResponse = p.getProperty("reversalTransactionResponse");
	public static final String reconsillationTransactionResponse = p.getProperty("reconsillationTransactionResponse");
	// Transaction MTI:
	public static final String authorisationRequestMTI = p.getProperty("authorisationRequestMTI");
	public static final String authorisationResponseMTI = p.getProperty("authorisationResponseMTI");
	public static final String financialSalesRequestMTI = p.getProperty("financialSalesRequestMTI");
	public static final String financialSalesResponseMTI = p.getProperty("financialSalesResponseMTI");
	public static final String financialForceDraftRequestMTI = p.getProperty("financialForceDraftRequestMTI");
	public static final String financialForceDraftResponseMTI = p.getProperty("financialForceDraftResponseMTI");
	public static final String reversalRequestMTI = p.getProperty("reversalRequestMTI");
	public static final String reversalResponseMTI = p.getProperty("reversalResponseMTI");
	public static final String reconsillationRequestMTI = p.getProperty("reconsillationRequestMTI");
	public static final String reconsillationResponseMTI = p.getProperty("reconsillationResponseMTI");
	// BitFields involved in Transaction
	public static final Integer[] elementsInGenericTransaction = generateIntegerArrayFromString(p.getProperty("elementsInGenericTransaction"));
	public static final Integer[] elementsInReconsillationTransaction = generateIntegerArrayFromString(p.getProperty("elementsInReconsillationTransaction"));
	// Codes to be validated during transaction
	public static final List<String> balanceInquiryCodes = generateArrayListFromString(p.getProperty("balanceInquiryCodes"));
	public static final List<String> activationRechargeCodes = generateArrayListFromString(p.getProperty("activationRechargeCodes"));
	// BitField Names:
	public static final String nameOfbitfield2 = "BITFIELD2";
	public static final String nameOfbitfield3 = "BITFIELD3";
	public static final String nameOfbitfield4 = "BITFIELD4";
	public static final String nameOfbitfield11 = "BITFIELD11";
	public static final String nameOfbitfield12 = "BITFIELD12";
	public static final String nameOfbitfield35 = "BITFIELD35";
	public static final String nameOfbitfield38 = "BITFIELD38";
	public static final String nameOfbitfield39 = "BITFIELD39";
	public static final String nameOfbitfield41 = "BITFIELD41";
	public static final String nameOfbitfield42 = "BITFIELD42";
	public static final String nameOfbitfield44 = "BITFIELD44";
	public static final String nameOfbitfield45 = "BITFIELD45";
	public static final String nameOfbitfield48 = "BITFIELD48";
	public static final String nameOfbitfield49 = "BITFIELD49";
	public static final String nameOfbitfield54 = "BITFIELD54";
	public static final String nameOfbitfield123 = "BITFIELD123";
	// BitField Values:
	public static final String valueOfBitfield4 = p.getProperty("valueOfBitfield4");
	public static final String valueOfBitfield38 = p.getProperty("valueOfBitfield38");
	public static final String ValueOfBitfield39Approval = p.getProperty("ValueOfBitfield39Approval");
	public static final String ValueOfBitfield39Decline = p.getProperty("ValueOfBitfield39Decline");
	public static final String ValueOfBitfield39Partial = p.getProperty("ValueOfBitfield39Partial");
	public static final String ValueOfBitfield39Reversal = p.getProperty("ValueOfBitfield39Reversal");
	public static final String valueOfBitfield44 = p.getProperty("valueOfBitfield44");
	public static final String valueOfBitfield48 = p.getProperty("valueOfBitfield48");
	public static final String valueOfBitfield54 = p.getProperty("valueOfBitfield54");
	public static final String valueOfBitfield123 = p.getProperty("valueOfBitfield123");
	// Decoding details:
	public static final Integer eHeaderStartPoint = Integer.parseInt(p.getProperty("eHeaderStartPoint"));
	public static final Integer eHeaderEndPoint = Integer.parseInt(p.getProperty("eHeaderEndPoint"));
	public static final Integer mtiStartPoint = Integer.parseInt(p.getProperty("mtiStartPoint"));
	public static final Integer mtiEndPoint = Integer.parseInt(p.getProperty("mtiEndPoint"));
	public static final Integer primaryBitmapStartPoint = Integer.parseInt(p.getProperty("primaryBitmapStartPoint"));
	public static final Integer primaryBitmapEndPoint = Integer.parseInt(p.getProperty("primaryBitmapEndPoint"));
	public static final Integer primaryBitmapPosition = Integer.parseInt(p.getProperty("primaryBitmapPosition"));
	public static final Integer secondaryBitmapStartPoint = Integer.parseInt(p.getProperty("secondaryBitmapStartPoint"));
	public static final Integer secondaryBitmapEndPoint = Integer.parseInt(p.getProperty("secondaryBitmapEndPoint"));
	public static final Integer secondaryBitmapEndPosition = Integer.parseInt(p.getProperty("secondaryBitmapEndPosition"));
	
	
	public static Integer[] generateIntegerArrayFromString(String elementsInTransaction) {
		elementsInTransaction = elementsInTransaction.replaceAll(" ", "");
		Integer[] elementsInTransactionArrayIntegers = new Integer[elementsInTransaction.split(",").length];
		int i = 0;
		for(String currentString: elementsInTransaction.split(",")) {
			elementsInTransactionArrayIntegers[i] = Integer.parseInt(currentString);
			i++;
		}		
		return elementsInTransactionArrayIntegers;
	}
	
	public static ArrayList<String> generateArrayListFromString(String elementsInTransaction){
		elementsInTransaction = elementsInTransaction.replace(" ", "");
		ArrayList<String> elementsInTransactionList = new ArrayList<String>();
		for(String currentString: elementsInTransaction.split(",")) {
			elementsInTransactionList.add(currentString);
		}
		return elementsInTransactionList;
	}

}
