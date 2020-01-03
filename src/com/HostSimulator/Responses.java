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
		
		if(requestMTI.equals(Constants.authorisationRequestMTI)) {
			responsePacket = authorizationMessageResponse();
		}else if(requestMTI.equals(Constants.financialSalesRequestMTI)||requestMTI.equals(Constants.financialForceDraftRequestMTI)) {
			responsePacket = financialMessageResponse();
		}else if(requestMTI.equals(Constants.reversalRequestMTI)) {
			responsePacket = reversalMessageResponse();
		}else if(requestMTI.equals(Constants.reconsillationRequestMTI)) {
			responsePacket = reconciliationMessageResponse();
		}		
		
//		switch (requestMTI) {
//		case "1100":
//			responsePacket = authorizationMessageResponse();
//			break;
//		case "1200":
//		case "1220":
//			responsePacket = financialMessageResponse();
//			break;
//		case "1420":
//			responsePacket = reversalMessageResponse();
//			break;
//		case "1520":
//			responsePacket = reconciliationMessageResponse();
//			break;
//		}
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
		String approveTransaction = Constants.authorizationTransactionResponse;
		String responsePacket = "", bitmap, bitfield4 = "";
		TreeSet<Integer> elementsInTransaction = new TreeSet<>(Arrays.asList(Constants.elementsInAuthorisationTransaction));
		generateResponseBitfieldswithValue(elementsInTransaction);
		boolean isBalanceInquiry = false;
		if (Constants.balanceInquiryCodes.contains(requestBitfieldsWithValues.get(Constants.nameOfbitfield3))) {
			isBalanceInquiry = true;
			bitfield4 = Constants.valueOfBitfield4;
			// Partial approval is not applicable for balance inquiry.
			if (approveTransaction.equals("PartiallyApprove")) {
				approveTransaction = "Approve";
			}
		}
		switch (approveTransaction) {
		case "Approve":
			responseBitfieldswithValue.put(Constants.nameOfbitfield39, Constants.ValueOfBitfield39Approval);
			break;
		case "Decline":
			responseBitfieldswithValue.put(Constants.nameOfbitfield39, Constants.ValueOfBitfield39Decline);
			break;
		case "PartiallyApprove":
			bitfield4 = generateHalfAmountForPartialApproval(requestBitfieldsWithValues.get(Constants.nameOfbitfield4));
			responseBitfieldswithValue.put(Constants.nameOfbitfield39, Constants.ValueOfBitfield39Partial);
			break;
		}
		//Bitfields for which the values should be generated.		
		if (approveTransaction.equals("PartiallyApprove") || isBalanceInquiry) {
			responseBitfieldswithValue.put(Constants.nameOfbitfield4, bitfield4);
		}
		if (approveTransaction.contentEquals("Approve") || approveTransaction.contentEquals("PartiallyApprove")) {
			responseBitfieldswithValue.put(Constants.nameOfbitfield38, Constants.valueOfBitfield38);
			elementsInTransaction.add(38);
		}
		if (approveTransaction.equals("Decline")) {
			responseBitfieldswithValue.put(Constants.nameOfbitfield44, Constants.valueOfBitfield44);
			elementsInTransaction.add(44);
		}
		if (isBalanceInquiry && Main.fepName.equals("HPS")) {
			responseBitfieldswithValue.put(Constants.nameOfbitfield54, Constants.valueOfBitfield54);
			elementsInTransaction.add(54);
		}
		addFEPSpecificElements(Constants.authorisationRequestMTI);

		HexEncoder encoder = new HexEncoder(Constants.authorisationResponseMTI, eHeader);
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
		TreeSet<Integer> elementsInTransaction = new TreeSet<>(Arrays.asList(Constants.elementsInFinancialTransaction));
		String approveTransaction = "", responsePacket = "", bitmap, bitfield4 = "", responseMTI = "";
		generateResponseBitfieldswithValue(elementsInTransaction);
		// Setting the response MTI bases on the request MTI. Two conditions are checked
		// since financial transaction can have sales and force draft requests
		if (requestMTI.equals(Constants.financialSalesRequestMTI)) {
			responseMTI = Constants.financialSalesResponseMTI;
			approveTransaction = Constants.financialSalesTransactionResponse;
			// Activation and Recharges transaction should not have partial approval
			if (Constants.activationRechargeCodes.contains(requestBitfieldsWithValues.get(Constants.nameOfbitfield3))
					&& approveTransaction.equals("PartiallyApprove")) {
				approveTransaction = "Approve";
			}
		} else if (requestMTI.equals(Constants.financialForceDraftRequestMTI)) {
			responseMTI = Constants.financialForceDraftResponseMTI;
			approveTransaction = Constants.financialForceDraftTransactionResponse;
			if (approveTransaction.equals("PartiallyApprove")) {
				approveTransaction = "Approve";
			}
		}
		switch (approveTransaction) {
		case "Approve":
			responseBitfieldswithValue.put(Constants.nameOfbitfield39, Constants.ValueOfBitfield39Approval);
			break;
		case "Decline":
			responseBitfieldswithValue.put(Constants.nameOfbitfield39, Constants.ValueOfBitfield39Decline);
			break;
		case "PartiallyApprove":
			bitfield4 = generateHalfAmountForPartialApproval(requestBitfieldsWithValues.get(Constants.nameOfbitfield4));
			responseBitfieldswithValue.put(Constants.nameOfbitfield4,
					requestBitfieldsWithValues.get(Constants.nameOfbitfield4));
			responseBitfieldswithValue.put(Constants.nameOfbitfield39, Constants.ValueOfBitfield39Partial);
			break;
		}
		//Bitfields for which the values should be generated.
		
		if (approveTransaction.contentEquals("Approve") || approveTransaction.contentEquals("PartiallyApprove")) {
			responseBitfieldswithValue.put(Constants.nameOfbitfield38, Constants.valueOfBitfield38);
			elementsInTransaction.add(38);
		}
		if (approveTransaction.equals("Decline")) {
			responseBitfieldswithValue.put(Constants.nameOfbitfield44, Constants.valueOfBitfield44);
			elementsInTransaction.add(44);
		}
		// For SVS cards, DE 54 should be included. This is identified using
		// bitfield 3.
		if (Constants.activationRechargeCodes.contains(requestBitfieldsWithValues.get(Constants.nameOfbitfield3))) {
			if(Main.fepName.equals("HPS")) {
				responseBitfieldswithValue.put(Constants.nameOfbitfield54, Constants.valueOfBitfield54);
				elementsInTransaction.add(54);
			}			
		}
		addFEPSpecificElements(Constants.financialSalesRequestMTI);
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
		String approveTransaction = Constants.reversalTransactionResponse;
		String responsePacket = "", bitmap, bitfield39 = "";
		TreeSet<Integer> elementsInTransaction = new TreeSet<>(Arrays.asList(Constants.elementsInReversalTransaction));
		generateResponseBitfieldswithValue(elementsInTransaction);
		
		//Bitfields for which the values should be generated.
		
		if (approveTransaction.equals("Approve")) {			
			responseBitfieldswithValue.put(Constants.nameOfbitfield39, Constants.ValueOfBitfield39Reversal);
			responseBitfieldswithValue.put(Constants.nameOfbitfield38, Constants.valueOfBitfield38);
			elementsInTransaction.add(38);
		} else {
			responseBitfieldswithValue.put(Constants.nameOfbitfield39, Constants.ValueOfBitfield39Decline);
			responseBitfieldswithValue.put(Constants.nameOfbitfield44, Constants.valueOfBitfield44);
			elementsInTransaction.add(44);
		}
		addFEPSpecificElements(Constants.reversalRequestMTI);
		HexEncoder encoder = new HexEncoder(Constants.reversalResponseMTI, eHeader);
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
		String responsePacket = "", bitmap , approveTransaction = Constants.reversalTransactionResponse;
		TreeSet<Integer> elementsInTransaction = new TreeSet<>(Arrays.asList(Constants.elementsInReconsillationTransaction));
		generateResponseBitfieldswithValue(elementsInTransaction);
		//Bitfields for which the values should be generated.
		if(approveTransaction.equals("")) {
			responseBitfieldswithValue.put(Constants.nameOfbitfield39, Constants.ValueOfBitfield39Reconsillation);
			responseBitfieldswithValue.put(Constants.nameOfbitfield123,Constants.valueOfBitfield123);
		}else {
			responseBitfieldswithValue.put(Constants.nameOfbitfield39, Constants.ValueOfBitfield39Decline);
		}
		responseBitfieldswithValue.put(Constants.nameOfbitfield48, Constants.valueOfBitfield48);
		addFEPSpecificElements(Constants.reconsillationRequestMTI);
		HexEncoder encoder = new HexEncoder(Constants.reconsillationResponseMTI, eHeader);
		bitmap = encoder.tgenerateBinaryData(elementsInTransaction);
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
		BitFieldData bitfieldLength = new BitFieldData();
		if (bitfieldLength.bitfieldLength.get(bitfield) == -2) {
			updatedValue.delete(0, 2);
		} else if (bitfieldLength.bitfieldLength.get(bitfield) == -3) {
			updatedValue.delete(0, 3);
		}
		return updatedValue.toString();
	}

	public String generateBitfield2(Map<String, String> requestPacketBitfields) {
		int endPoint = 0;
		if (requestPacketBitfields.containsKey(Constants.nameOfbitfield2)) {
			return removeLLVAR(Constants.nameOfbitfield2, requestPacketBitfields.get(Constants.nameOfbitfield2));
		} else if (requestPacketBitfields.containsKey(Constants.nameOfbitfield35)) {
			endPoint = requestPacketBitfields.get(Constants.nameOfbitfield35).indexOf('=');
			return requestPacketBitfields.get(Constants.nameOfbitfield35).substring(2, endPoint);
		} else if (requestPacketBitfields.containsKey(Constants.nameOfbitfield45)) {
			endPoint = requestPacketBitfields.get(Constants.nameOfbitfield45).indexOf('^');
			return requestPacketBitfields.get(Constants.nameOfbitfield45).substring(3, endPoint);
		}
		return "";
	}
	//------------------------------------------------------------------------------------------------------------------
	/*
	 * This method is used to create a responsebitfield treemap. Treemap is used to
	 * make sure the bitfield values are sorted. Numbers of bitfields that are to be
	 * sent in response should be passed to the method.
	 */
	//------------------------------------------------------------------------------------------------------------------
	public void generateResponseBitfieldswithValue(TreeSet<Integer> elementsInTransaction) {
		for (Integer currentEntry : elementsInTransaction) {
			String key = "BITFIELD" + currentEntry;
			responseBitfieldswithValue.put(key, requestBitfieldsWithValues.get(key));
		}
	}
	//------------------------------------------------------------------------------------------------------------------
	/*
	 * This method is used to generate a dynamic amount for partial approval.
	 * This takes transaction amount as input and returns half of it.
	 */
	//------------------------------------------------------------------------------------------------------------------
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
	//------------------------------------------------------------------------------------------------------------------
	/*
	 * This method is used to add HPS specific data elements
	 * Transaction type i.e., request MTI should be passed as argument
	 */
	//------------------------------------------------------------------------------------------------------------------
	public void addFEPSpecificElements(String transactionType) {
		if(Main.fepName.equals("HPS")) {
			if(!transactionType.equals(Constants.reconsillationRequestMTI)) {
				responseBitfieldswithValue.put(Constants.nameOfbitfield2, generateBitfield2(requestBitfieldsWithValues));
			}
		}else if(Main.fepName.equals("INCOMM")) {
			responseBitfieldswithValue.put(Constants.nameOfbitfield5, requestBitfieldsWithValues.get(Constants.nameOfbitfield4));
		}
		
	}

}