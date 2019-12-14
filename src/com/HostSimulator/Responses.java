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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class Responses {

	private String requestPacket, sTAN, transactionTimeStamp, eHeader, requestMTI;
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
	 * This method generates the response packet for echo request Takes
	 * StringBuffer as input and returns response packet as String.
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
		String responsePacket = "", bitmap;
		decoder = new HexDecoder(this.requestPacket);
		eHeader = decoder.geteHeader();
		requestMTI = decoder.getMTI();
		requestBitfieldsWithValues = decoder.getBitFieldwithValues();
		System.out.println("Request Packet");
		decoder.printEncodedData();
		responseBitfieldswithValue = new LinkedHashMap<>();
		switch (requestMTI) {
		case "1100":
			responsePacket = authorizationMessageResponse();
			break;
		case "1200":
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
	 * Authorization request could be of two types. 1) Preauth 2) Balance
	 * Inquiry In case of balance inquiry Bitfield3 would denote the transaction
	 * type.
	 */
	// ------------------------------------------------------------------------------------------------------------------
	public String authorizationMessageResponse() {
		String responsePacket = "", bitmap, bitfield4 = "", bitfield38 = "", bitfield39 = "", bitfield44 = "",
				bitfield54 = "6501840C000000010000", elementsInTransaction;
		// approveTransaction can have values Approve,
		// Decline,PartiallyApprove(not applicable for balance inquiry)
		String approveTransaction = "PartiallyApprove";
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
		if (approveTransaction.equals("Decline")) {
			responseBitfieldswithValue.put("BITFIELD39", bitfield39);
			responseBitfieldswithValue.put("BITFIELD44", bitfield44);
			elementsInTransaction = "2 3 4 11 12 39 41 42 44 49";
		} else {
			responseBitfieldswithValue.put("BITFIELD38", bitfield38);
			responseBitfieldswithValue.put("BITFIELD39", bitfield39);
			if (isBalanceInquiry) {
				elementsInTransaction = "2 3 4 11 12 38 39 41 42 49 54";
			} else {
				elementsInTransaction = "2 3 4 11 12 38 39 41 42 49";
			}
		}
		responseBitfieldswithValue.put("BITFIELD41", requestBitfieldsWithValues.get("BITFIELD41"));
		responseBitfieldswithValue.put("BITFIELD42", requestBitfieldsWithValues.get("BITFIELD42"));
		responseBitfieldswithValue.put("BITFIELD49", requestBitfieldsWithValues.get("BITFIELD49"));
		if (isBalanceInquiry) {
			responseBitfieldswithValue.put("BITFIELD54", bitfield54);
		}

		HexEncoder encoder = new HexEncoder("1110", eHeader);
		bitmap = encoder.generateBinaryData(elementsInTransaction);
		encoder.setBitmap(bitmap);
		encoder.setResponseBitFieldsWithValue(responseBitfieldswithValue);
		encoder.encodeddata();
		responsePacket = encoder.getEncodedHexData();

		return responsePacket;
	}

	// ------------------------------------------------------------------------------------------------------------------
	/*
	 * This method is used to generate the response for a financial
	 * request(1200), (1220)
	 */
	// ------------------------------------------------------------------------------------------------------------------
	public String financialMessageResponse() {
		// approveTransaction can have values Approve,Decline,PartiallyApprove
		String approveTransaction = "PartiallyApprove";
		TreeSet<Integer> elements = new TreeSet<>(Arrays.asList(2,3));
		String responsePacket = "", bitmap, bitfield4 = "", bitfield38 = "", bitfield39 = "", bitfield44 = "",
				bitfield54 = "6501840C000000010000", elementsInTransaction, responseMTI = "";
		if (requestMTI.equals("1200")) {
			responseMTI = "1210";
		} else if (requestMTI.equals("1220")) {
			responseMTI = "1230";
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
			// Bitfield4 has a fixed length of 12 digits and has to have 0's for
			// the digits missing.
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
		if (approveTransaction.equals("PartiallyApprove")) {
			responseBitfieldswithValue.put("BITFIELD4", bitfield4);
		} else {
			responseBitfieldswithValue.put("BITFIELD4", requestBitfieldsWithValues.get("BITFIELD4"));
		}
		responseBitfieldswithValue.put("BITFIELD11", requestBitfieldsWithValues.get("BITFIELD11"));
		responseBitfieldswithValue.put("BITFIELD12", requestBitfieldsWithValues.get("BITFIELD12"));
		if (approveTransaction.equals("Decline")) {
			responseBitfieldswithValue.put("BITFIELD39", bitfield39);
			responseBitfieldswithValue.put("BITFIELD44", bitfield44);
			elementsInTransaction = "2 3 4 11 12 39 41 42 44 49";
		} else {
			responseBitfieldswithValue.put("BITFIELD38", bitfield38);
			responseBitfieldswithValue.put("BITFIELD39", bitfield39);
			elementsInTransaction = "2 3 4 11 12 38 39 41 42 49";
		}
		responseBitfieldswithValue.put("BITFIELD41", requestBitfieldsWithValues.get("BITFIELD41"));
		responseBitfieldswithValue.put("BITFIELD42", requestBitfieldsWithValues.get("BITFIELD42"));
		responseBitfieldswithValue.put("BITFIELD49", requestBitfieldsWithValues.get("BITFIELD49"));
		// For SVS cards, DE 54 should be included. This is identified using
		// bitfield 3.
		if (requestBitfieldsWithValues.get("BITFIELD3").equals("900060")
				|| requestBitfieldsWithValues.get("BITFIELD3").equals("930060")) {
			responseBitfieldswithValue.put("BITFIELD54", bitfield54);
		}

		HexEncoder encoder = new HexEncoder(responseMTI, eHeader);
		bitmap = encoder.generateBinaryData(elementsInTransaction);
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
		String responsePacket = "", bitmap, MTI = "1430", actionCode = "400";
		responseBitfieldswithValue.put("BITFIELD2", generateBitfield2(requestBitfieldsWithValues));
		responseBitfieldswithValue.put("BITFIELD3", requestBitfieldsWithValues.get("BITFIELD3"));
		responseBitfieldswithValue.put("BITFIELD4", requestBitfieldsWithValues.get("BITFIELD4"));
		responseBitfieldswithValue.put("BITFIELD11", requestBitfieldsWithValues.get("BITFIELD11"));
		responseBitfieldswithValue.put("BITFIELD12", requestBitfieldsWithValues.get("BITFIELD12"));
		responseBitfieldswithValue.put("BITFIELD38", "123456");
		responseBitfieldswithValue.put("BITFIELD39", actionCode);
		responseBitfieldswithValue.put("BITFIELD41", requestBitfieldsWithValues.get("BITFIELD41"));
		responseBitfieldswithValue.put("BITFIELD42", requestBitfieldsWithValues.get("BITFIELD42"));
		responseBitfieldswithValue.put("BITFIELD49", requestBitfieldsWithValues.get("BITFIELD49"));
		HexEncoder encoder = new HexEncoder(MTI, eHeader);
		String elementsInTransaction = "2 3 4 11 12 38 39 41 42 49";
		bitmap = encoder.generateBinaryData(elementsInTransaction);
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
	 * This method is used to generate the response bitfield value. When we
	 * receive the request packet, bitfields will have the length prefixed. This
	 * has to be removed for the HexEncoder to generate the correct value.
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
		String bitfield2Value = "";
		int startPoint = 0, endPoint = 0;
		if (requestPacketBitfields.containsKey("BITFIELD2")) {
			bitfield2Value = removeLLVAR("BITFIELD2", requestPacketBitfields.get("BITFIELD2"));
		} else if (requestPacketBitfields.containsKey("BITFIELD35")) {
			startPoint = 2;
			endPoint = requestPacketBitfields.get("BITFIELD35").indexOf('=');
			bitfield2Value = requestPacketBitfields.get("BITFIELD35").substring(startPoint, endPoint);
		} else if (requestPacketBitfields.containsKey("BITFIELD45")) {
			startPoint = 3;
			endPoint = requestPacketBitfields.get("BITFIELD45").indexOf('^');
			bitfield2Value = requestPacketBitfields.get("BITFIELD45").substring(startPoint, endPoint);
		}
		return bitfield2Value;
	}

}