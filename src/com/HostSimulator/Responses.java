//
/*
 * This file is used for generating the responses for the transaction requests.
 * Constructor of this class requires the request packet to be fed in form of string.
 * Identifies the MTI from the request packet and decides the response accordingly.
 */
//
package com.HostSimulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class Responses {

	private String requestPacket, eHeader, requestMTI;
	Map<String, String> requestBitfieldsWithValues, responseBitfieldswithValue;
	HexDecoder decoder;

	public Responses(String requestPacket) {
		this.requestPacket = requestPacket;
	}

	public Map<String, String> getResponseBitfieldswithValue() {
		return responseBitfieldswithValue;
	}

	// ----------------------------------------------------------------------------------------------------------------
	/*
	 * This method generates the response packet for echo request Takes StringBuffer
	 * as input and returns response packet as String.
	 */
	// ----------------------------------------------------------------------------------------------------------------
	public String echoMessageResponse() {
		StringBuffer responsePacket = new StringBuffer(this.requestPacket);
		for (int i = 0; i < responsePacket.length(); i++) {
			if (i == 8 || i == 9) {
				responsePacket.setCharAt(i, '0');
			}
		}
		return responsePacket.toString();
	}

	// ------------------------------------------------------------------------------------------------------------------
	/*
	 * This method identifies the type of transaction and generates the response
	 * based on it
	 */
	// -------------------------------------------------------------------------------------------------------------------
	public String getResponsePacket() {
		String responsePacket = "";
		decoder = new HexDecoder(this.requestPacket);
		eHeader = decoder.geteHeader();
		requestMTI = decoder.getMTI();
		requestBitfieldsWithValues = decoder.getBitFieldwithValues();
		System.out.println("Request Packet");
		decoder.printEncodedData();
		responseBitfieldswithValue = new TreeMap<>(new BitfieldComparator());
		switch (requestMTI) {
		case "1100":
			responsePacket = authorizationMessageResponse();
			break;
		case HPSConstants.financialSalesRequestMTI:
		case "1220":
			responsePacket = financialMessageResponse();
			break;
		case "1420":
			responsePacket = reversalMessageResponse();
			break;
		case "1520":
			responsePacket = reconciliationMessageResponse();
			break;
		}
		decoder = new HexDecoder(responsePacket);
		System.out.println("Response Packet");
		decoder.printEncodedData();
		return responsePacket;
	}

	// ------------------------------------------------------------------------------------------------------------------
	/*
	 * This method is used to generate the response for a reversal request(1100)
	 * Authorization request could be of two types. 1) Preauth 2) Balance Inquiry In
	 * case of balance inquiry Bitfield3 would denote the transaction type.
	 */
	// ------------------------------------------------------------------------------------------------------------------
	public String authorizationMessageResponse() {
		// approveTransaction can have values Approve,
		// Decline,PartiallyApprove(not applicable for balance inquiry)
		String approveTransaction = "Approve";
		String responsePacket = "", responseMTI = "1110", bitmap, bitfield4 = "", bitfield38 = "", bitfield39 = "",
				bitfield44 = "", bitfield54 = "6501840C000000010000";
		TreeSet<Integer> elementsInTransaction = new TreeSet<>(Arrays.asList(2, 3, 4, 11, 12, 39, 41, 42, 49));
		String[] balanceInquiryCodes = { "313000", "318000", "318100", "309700", "319700", "316000", "313900" };
		List<String> balanceInquiryCodesList = new ArrayList<String>(Arrays.asList(balanceInquiryCodes));
		boolean isBalanceInquiry = false;
		if (balanceInquiryCodesList.contains(requestBitfieldsWithValues.get("BITFIELD3"))) {
			isBalanceInquiry = true;
			bitfield4 = "000000010000";
			bitfield54 = "6501840C000000010000";
			// Partial approval is not applicable for balance inquiry.
			if (approveTransaction.equals("PartiallyApprove")) {
				approveTransaction = "Approve";
			}
		}
		switch (approveTransaction) {
		case "Approve":
			bitfield38 = "123456";
			bitfield39 = "000";
			break;
		case "Decline":
			bitfield39 = "100";
			bitfield44 = "0705";
			break;
		case "PartiallyApprove":
			bitfield4 = Integer.toString(Integer.parseInt(requestBitfieldsWithValues.get("BITFIELD4")) / 2);
			// Bitfield4 has a fixed length of 12 digits and has to have 0's
			// for the digits missing.
			int length = bitfield4.length();
			String tempString = "";
			for (int i = 0; i < 12 - length; i++) {
				tempString = tempString + "0";
			}
			bitfield4 = tempString + bitfield4;
			bitfield38 = "123456";
			bitfield39 = "002";
			break;
		}

		responseBitfieldswithValue.put("BITFIELD2", generateBitfield2(requestBitfieldsWithValues));
		responseBitfieldswithValue.put("BITFIELD3", requestBitfieldsWithValues.get("BITFIELD3"));
		if (approveTransaction.equals("PartiallyApprove") || isBalanceInquiry) {
			responseBitfieldswithValue.put("BITFIELD4", bitfield4);
		} else {
			responseBitfieldswithValue.put("BITFIELD4", requestBitfieldsWithValues.get("BITFIELD4"));
		}
		responseBitfieldswithValue.put("BITFIELD11", requestBitfieldsWithValues.get("BITFIELD11"));
		responseBitfieldswithValue.put("BITFIELD12", requestBitfieldsWithValues.get("BITFIELD12"));
		if (approveTransaction.contentEquals("Approve") || approveTransaction.contentEquals("PartiallyApprove")) {
			responseBitfieldswithValue.put("BITFIELD38", bitfield38);
			elementsInTransaction.add(38);
		}
		responseBitfieldswithValue.put("BITFIELD39", bitfield39);
		if (approveTransaction.equals("Decline")) {
			responseBitfieldswithValue.put("BITFIELD44", bitfield44);
			elementsInTransaction.add(44);
		}
		responseBitfieldswithValue.put("BITFIELD41", requestBitfieldsWithValues.get("BITFIELD41"));
		responseBitfieldswithValue.put("BITFIELD42", requestBitfieldsWithValues.get("BITFIELD42"));
		responseBitfieldswithValue.put("BITFIELD49", requestBitfieldsWithValues.get("BITFIELD49"));
		if (isBalanceInquiry) {
			responseBitfieldswithValue.put("BITFIELD54", bitfield54);
		}

		HexEncoder encoder = new HexEncoder(responseMTI, eHeader);
		bitmap = encoder.tgenerateBinaryData(elementsInTransaction);
		encoder.setBitmap(bitmap);
		encoder.setResponseBitFieldsWithValue(responseBitfieldswithValue);
		encoder.encodeddata();
		responsePacket = encoder.getEncodedHexData();

		return responsePacket;
	}

	// ------------------------------------------------------------------------------------------------------------------
	/*
	 * This method is used to generate the response for a financial request(1200),
	 * (1220)
	 */
	// ------------------------------------------------------------------------------------------------------------------
	public String financialMessageResponse() {
		// approveTransaction can have values Approve,Decline,PartiallyApprove
		String approveTransaction = HPSConstants.partiallyApproveTransaction;
		TreeSet<Integer> elementsInTransaction = new TreeSet<>(
				Arrays.asList(HPSConstants.elementsInGenericTransaction));
		String responsePacket = "", bitmap, bitfield4 = "", bitfield39 = "", responseMTI = "";
		generateResponseBitfieldswithValue(elementsInTransaction);
		responseBitfieldswithValue.put(HPSConstants.nameOfbitfield2, generateBitfield2(requestBitfieldsWithValues));

		if (requestMTI.equals(HPSConstants.financialSalesRequestMTI)) {
			responseMTI = HPSConstants.financialSalesResponseMTI;
			// Activation and Recharges transaction should not have partial approval
			if (HPSConstants.activationRechargeCodes
					.contains(requestBitfieldsWithValues.get(HPSConstants.nameOfbitfield3))
					&& approveTransaction.equals("PartiallyApprove")) {
				approveTransaction = "Approve";
			}
		} else if (requestMTI.equals(HPSConstants.financialForceDraftRequestMTI)) {
			responseMTI = HPSConstants.financialForceDraftResponseMTI;
			if (approveTransaction.equals("PartiallyApprove")) {
				approveTransaction = "Approve";
			}
		}
		switch (approveTransaction) {
		case "Approve":
			bitfield39 = "000";
			break;
		case "Decline":
			bitfield39 = "100";
			break;
		case "PartiallyApprove":
			bitfield4 = generateHalfAmountForPartialApproval(
					requestBitfieldsWithValues.get(HPSConstants.nameOfbitfield4));
			responseBitfieldswithValue.put(HPSConstants.nameOfbitfield4, requestBitfieldsWithValues.get("BITFIELD4"));
			bitfield39 = "002";
			break;
		}
		if (approveTransaction.contentEquals("Approve") || approveTransaction.contentEquals("PartiallyApprove")) {
			responseBitfieldswithValue.put(HPSConstants.nameOfbitfield38, HPSConstants.valueOfBitfield38);
			elementsInTransaction.add(38);
		}
		responseBitfieldswithValue.put(HPSConstants.nameOfbitfield39, bitfield39);
		if (approveTransaction.equals("Decline")) {
			responseBitfieldswithValue.put(HPSConstants.nameOfbitfield44, HPSConstants.valueOfBitfield44);
			elementsInTransaction.add(44);
		}
		// For SVS cards, DE 54 should be included. This is identified using
		// bitfield 3.
		if (HPSConstants.activationRechargeCodes
				.contains(requestBitfieldsWithValues.get(HPSConstants.nameOfbitfield3))) {
			responseBitfieldswithValue.put(HPSConstants.nameOfbitfield54, HPSConstants.valueOfBitfield54);
			elementsInTransaction.add(54);
		}

		HexEncoder encoder = new HexEncoder(responseMTI, eHeader);
		bitmap = encoder.tgenerateBinaryData(elementsInTransaction);
		encoder.setBitmap(bitmap);
		encoder.setResponseBitFieldsWithValue(responseBitfieldswithValue);
		encoder.encodeddata();
		responsePacket = encoder.getEncodedHexData();
		return responsePacket;
	}

	// ------------------------------------------------------------------------------------------------------------------
	/*
	 * This method is used to generate the response for a reversal request(1420)
	 */
	// ------------------------------------------------------------------------------------------------------------------
	public String reversalMessageResponse() {
		// approveTransaction can have values Approve,Decline
		String approveTransaction = "Decline";
		String responsePacket = "", bitmap, responseMTI = "1430", bitfield38 = "", bitfield39 = "", bitfield44 = "";
		TreeSet<Integer> elementsInTransaction = new TreeSet<>(Arrays.asList(2, 3, 4, 11, 12, 39, 41, 42, 49));
		if (approveTransaction.equals("Approve")) {
			bitfield38 = "123456";
			bitfield39 = "400";
		} else {
			bitfield39 = "100";
			bitfield44 = "0705";
		}
		responseBitfieldswithValue.put("BITFIELD2", generateBitfield2(requestBitfieldsWithValues));
		responseBitfieldswithValue.put("BITFIELD3", requestBitfieldsWithValues.get("BITFIELD3"));
		responseBitfieldswithValue.put("BITFIELD4", requestBitfieldsWithValues.get("BITFIELD4"));
		responseBitfieldswithValue.put("BITFIELD11", requestBitfieldsWithValues.get("BITFIELD11"));
		responseBitfieldswithValue.put("BITFIELD12", requestBitfieldsWithValues.get("BITFIELD12"));
		if (approveTransaction.equals("Approve")) {
			responseBitfieldswithValue.put("BITFIELD38", bitfield38);
			elementsInTransaction.add(38);
		}
		responseBitfieldswithValue.put("BITFIELD39", bitfield39);
		responseBitfieldswithValue.put("BITFIELD41", requestBitfieldsWithValues.get("BITFIELD41"));
		responseBitfieldswithValue.put("BITFIELD42", requestBitfieldsWithValues.get("BITFIELD42"));
		if (approveTransaction.equals("Decline")) {
			responseBitfieldswithValue.put("BITFIELD44", bitfield44);
			elementsInTransaction.add(44);
		}
		responseBitfieldswithValue.put("BITFIELD49", requestBitfieldsWithValues.get("BITFIELD49"));
		HexEncoder encoder = new HexEncoder(responseMTI, eHeader);
		bitmap = encoder.tgenerateBinaryData(elementsInTransaction);
		encoder.setBitmap(bitmap);
		encoder.setResponseBitFieldsWithValue(responseBitfieldswithValue);
		encoder.encodeddata();
		responsePacket = encoder.getEncodedHexData();
		return responsePacket;
	}

	// ------------------------------------------------------------------------------------------------------------------
	/*
	 * This method is used to generate the response for a reconcillation request
	 */
	// ------------------------------------------------------------------------------------------------------------------
	public String reconciliationMessageResponse() {
		String responsePacket = "", bitmap;
		responseBitfieldswithValue.put("BITFIELD11", requestBitfieldsWithValues.get("BITFIELD11"));
		responseBitfieldswithValue.put("BITFIELD12", requestBitfieldsWithValues.get("BITFIELD12"));
		responseBitfieldswithValue.put("BITFIELD39", "940");
		responseBitfieldswithValue.put("BITFIELD41", requestBitfieldsWithValues.get("BITFIELD41"));
		responseBitfieldswithValue.put("BITFIELD42", requestBitfieldsWithValues.get("BITFIELD42"));
		responseBitfieldswithValue.put("BITFIELD48", "       0000000099");
		responseBitfieldswithValue.put("BITFIELD123",
				"0010002   CT  0000000000\\000000000000\\002   DB  0000000000\\000000000000\\002   MC  0000000000\\000000000000\\002   OH  0000000000\\000000000000\\002   PL  0000000000\\000000000000\\002   VI  0000000000\\000000000000\\007   CT  0000000000\\000000000000\\007   DB  0000000000\\000000000000\\299   CT  0000000000\\000000000000\\299   DB  0000000000\\000000000000\\");
		HexEncoder encoder = new HexEncoder("1530", eHeader);
		String elementsInTransaction = "11 12 39 41 42 48 123";
		bitmap = encoder.generateBinaryData(elementsInTransaction);
		encoder.setBitmap(bitmap);
		encoder.setResponseBitFieldsWithValue(responseBitfieldswithValue);
		encoder.encodeddata();
		responsePacket = encoder.getEncodedHexData();
		return responsePacket;
	}

	// ------------------------------------------------------------------------------------------------------------------
	/*
	 * This method is used to generate the response bitfield value. When we receive
	 * the request packet, bitfields will have the length prefixed. This has to be
	 * removed for the HexEncoder to generate the correct value.
	 */
	// ------------------------------------------------------------------------------------------------------------------
	public String removeLLVAR(String bitfield, String bitfieldValue) {
		StringBuffer updatedValue = new StringBuffer(bitfieldValue);
		BitFieldData bitfieldLength = new BitFieldData(true);
		if (bitfieldLength.bitfieldLength.get(bitfield) == -2) {
			updatedValue.delete(0, 2);
		} else if (bitfieldLength.bitfieldLength.get(bitfield) == -3) {
			updatedValue.delete(0, 3);
		}
		return updatedValue.toString();
	}

	public String generateBitfield2(Map<String, String> requestPacketBitfields) {
		int endPoint = 0;
		if (requestPacketBitfields.containsKey("BITFIELD2")) {
			return removeLLVAR("BITFIELD2", requestPacketBitfields.get("BITFIELD2"));
		} else if (requestPacketBitfields.containsKey("BITFIELD35")) {
			endPoint = requestPacketBitfields.get("BITFIELD35").indexOf('=');
			return requestPacketBitfields.get("BITFIELD35").substring(2, endPoint);
		} else if (requestPacketBitfields.containsKey("BITFIELD45")) {
			endPoint = requestPacketBitfields.get("BITFIELD45").indexOf('^');
			return requestPacketBitfields.get("BITFIELD45").substring(3, endPoint);
		}
		return "";
	}

	/*
	 * This method is used to create a responsebitfield treemap. 
	 * Treemap is used to make sure the bitfield values are sorted.
	 * Numbers of bitfields that are to be sent in response should be passed to the method.
	 */
	public void generateResponseBitfieldswithValue(TreeSet<Integer> elementsInTransaction) {
		for (Integer currentEntry : elementsInTransaction) {
			String key = "BITFIELD" + currentEntry;
			responseBitfieldswithValue.put(key, requestBitfieldsWithValues.get(key));
		}
	}

	public String generateHalfAmountForPartialApproval(String transactionAmount) {
		String bitfield4 = Integer.toString(Integer.parseInt(transactionAmount) / 2);
		// Bitfield4 has a fixed length of 12 digits and has to have 0's for
		// the digits missing.
		int length = bitfield4.length();
		String tempString = "";
		for (int i = 0; i < 12 - length; i++) {
			tempString = tempString + "0";
		}
		return tempString + bitfield4;
	}

}