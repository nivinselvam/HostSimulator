//
/*
 * This file is used for generating the responses for the transaction requests.
 * Constructor of this class requires the request packet to be fed in form of string.
 * Identifies the MTI from the request packet and decides the response accordingly.
 */
//
package com.HostSimulator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Responses {
	final static Logger logger = Logger.getLogger(Responses.class);
	private String requestPacket, eHeader, requestMTI;
	Map<String, String> requestBitfieldsWithValues, responseBitfieldswithValue;
	TreeSet<Integer> elementsInTransaction;
	SimpleDateFormat sdf;
	Date date = new Date();
	HexDecoder decoder;

	public Responses(String requestPacket) {
		this.requestPacket = requestPacket;
		PropertyConfigurator.configure("log4j.properties");
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
		if (Main.fepName.equals("HPS")) {
			for (int i = 0; i < responsePacket.length(); i++) {
				if (i == 8 || i == 9) {
					responsePacket.setCharAt(i, '0');
				}
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
		logger.info("Request Packet");
		decoder.printEncodedData();
		responseBitfieldswithValue = new TreeMap<>(new BitfieldComparator());

		if (requestMTI.equals(Constants.authorisationRequestMTI)) {
			responsePacket = authorizationMessageResponse();
		} else if (requestMTI.equals(Constants.financialSalesRequestMTI)
				|| requestMTI.equals(Constants.financialForceDraftRequestMTI)) {
			responsePacket = financialMessageResponse();
		} else if (requestMTI.equals(Constants.reversalRequestMTI)) {
			responsePacket = reversalMessageResponse();
		} else if (requestMTI.equals(Constants.reconsillationRequestMTI)) {
			responsePacket = reconciliationMessageResponse();
		} else {
			logger.error("Provided MTI is invalid for creating the response packet.");
		}

		decoder = new HexDecoder(responsePacket);
		logger.info("Response Packet");
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
		String transactionResult = Main.window.getTransactionResult().replace(" ", "");
		String responsePacket = "", bitmap, bitfield4 = "";
		elementsInTransaction = new TreeSet<>(Arrays.asList(Constants.elementsInAuthorisationTransaction));
		generateResponseBitfieldswithValue(elementsInTransaction);
		boolean isBalanceInquiry = false;
		if (Constants.balanceInquiryCodes.contains(requestBitfieldsWithValues.get(Constants.nameOfbitfield3))) {
			isBalanceInquiry = true;
			// Partial approval is not applicable for balance inquiry.
			if (transactionResult.equals("PartiallyApprove")) {
				transactionResult = "Approve";
			}
			if(transactionResult.equals("Decline")) {
				bitfield4 = "000000000000";
			}else {
				bitfield4 = Constants.valueOfBitfield4;
			}
				
		}
//		switch (transactionResult) {
//		case "Approve":
//			responseBitfieldswithValue.put(Constants.nameOfbitfield39,
//					setBitfieldValue(Constants.nameOfbitfield39, Constants.ValueOfBitfield39Approval));
//			break;
//		case "Decline":
//			responseBitfieldswithValue.put(Constants.nameOfbitfield39,
//					setBitfieldValue(Constants.nameOfbitfield39, Constants.ValueOfBitfield39Decline));
//			break;
//		case "PartiallyApprove":
//			bitfield4 = generateHalfAmountForPartialApproval(requestBitfieldsWithValues.get(Constants.nameOfbitfield4));
//			responseBitfieldswithValue.put(Constants.nameOfbitfield39,
//					setBitfieldValue(Constants.nameOfbitfield39, Constants.ValueOfBitfield39Partial));
//			break;
//		}

		if (transactionResult.equals("PartiallyApprove")) {
			if (Main.window.getApprovalAmount().equals("half")) {
				bitfield4 = generateHalfAmountForPartialApproval(
						requestBitfieldsWithValues.get(Constants.nameOfbitfield4));
			} else {
				if (Double.parseDouble(Main.window.getApprovalAmount()) > Double
						.parseDouble(requestBitfieldsWithValues.get(Constants.nameOfbitfield4))) {
					bitfield4 = generateHalfAmountForPartialApproval(
							requestBitfieldsWithValues.get(Constants.nameOfbitfield4));
				} else {
					bitfield4 = Main.window.getApprovalAmount();
				}

			}
		}

		responseBitfieldswithValue.put(Constants.nameOfbitfield39,
				setBitfieldValue(Constants.nameOfbitfield39, Main.window.getResponseCode()));
		// Bitfields for which the values should be generated.
		if (transactionResult.equals("PartiallyApprove") || isBalanceInquiry) {
			responseBitfieldswithValue.put(Constants.nameOfbitfield4,
					setBitfieldValue(Constants.nameOfbitfield4, bitfield4));
		}
		if (transactionResult.contentEquals("Approve") || transactionResult.contentEquals("PartiallyApprove")) {
			responseBitfieldswithValue.put(Constants.nameOfbitfield38,
					setBitfieldValue(Constants.nameOfbitfield38, Constants.valueOfBitfield38));
			elementsInTransaction.add(38);
		}
		if (transactionResult.equals("Decline")) {
			responseBitfieldswithValue.put(Constants.nameOfbitfield44,
					setBitfieldValue(Constants.nameOfbitfield44, Constants.valueOfBitfield44));
			elementsInTransaction.add(44);
		}
		if (isBalanceInquiry && Main.fepName.equals("HPS")) {
			responseBitfieldswithValue.put(Constants.nameOfbitfield54,
					setBitfieldValue(Constants.nameOfbitfield54, Constants.valueOfBitfield54));
			elementsInTransaction.add(54);
		}
		if (requestBitfieldsWithValues.containsKey(Constants.nameOfbitfield55)) {
			responseBitfieldswithValue.put(Constants.nameOfbitfield55,
					requestBitfieldsWithValues.get(Constants.nameOfbitfield55));
			elementsInTransaction.add(55);
		}

		addFEPSpecificElements(Constants.authorisationRequestMTI, transactionResult);

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
		elementsInTransaction = new TreeSet<>(Arrays.asList(Constants.elementsInFinancialTransaction));
		String transactionResult = "", responsePacket = "", bitmap, bitfield4 = "", responseMTI = "";
		generateResponseBitfieldswithValue(elementsInTransaction);
		// Setting the response MTI bases on the request MTI. Two conditions are checked
		// since financial transaction can have sales and force draft requests
		if (requestMTI.equals(Constants.financialSalesRequestMTI)) {
			responseMTI = Constants.financialSalesResponseMTI;
			transactionResult = Main.window.getTransactionResult().replace(" ", "");
			;
			// Activation and Recharges transaction should not have partial approval
			if (Constants.activationRechargeCodes.contains(requestBitfieldsWithValues.get(Constants.nameOfbitfield3))
					&& transactionResult.equals("PartiallyApprove")) {
				transactionResult = "Approve";
			}
		} else if (requestMTI.equals(Constants.financialForceDraftRequestMTI)) {
			responseMTI = Constants.financialForceDraftResponseMTI;
			transactionResult = Main.window.getTransactionResult().replace(" ", "");
			if (transactionResult.equals("PartiallyApprove")) {
				transactionResult = "Approve";
			}
		}
//		switch (transactionResult) {
//		case "Approve":
//			responseBitfieldswithValue.put(Constants.nameOfbitfield39,
//					setBitfieldValue(Constants.nameOfbitfield39, Constants.ValueOfBitfield39Approval));
//			break;
//		case "Decline":
//			responseBitfieldswithValue.put(Constants.nameOfbitfield39,
//					setBitfieldValue(Constants.nameOfbitfield39, Constants.ValueOfBitfield39Decline));
//			break;
//		case "PartiallyApprove":
//			bitfield4 = generateHalfAmountForPartialApproval(requestBitfieldsWithValues.get(Constants.nameOfbitfield4));
//			responseBitfieldswithValue.put(Constants.nameOfbitfield4,
//					requestBitfieldsWithValues.get(Constants.nameOfbitfield4));
//			responseBitfieldswithValue.put(Constants.nameOfbitfield39,
//					setBitfieldValue(Constants.nameOfbitfield39, Constants.ValueOfBitfield39Partial));
//			break;
//		}

		if (transactionResult.equals("PartiallyApprove")) {
			if (Main.window.getApprovalAmount().equals("half")) {
				bitfield4 = generateHalfAmountForPartialApproval(
						requestBitfieldsWithValues.get(Constants.nameOfbitfield4));
			} else {
				if (Double.parseDouble(Main.window.getApprovalAmount()) > Double
						.parseDouble(requestBitfieldsWithValues.get(Constants.nameOfbitfield4))) {
					bitfield4 = generateHalfAmountForPartialApproval(
							requestBitfieldsWithValues.get(Constants.nameOfbitfield4));
				} else {
					bitfield4 = Main.window.getApprovalAmount();
				}

			}

			responseBitfieldswithValue.put(Constants.nameOfbitfield4,
					bitfield4);
		}

		responseBitfieldswithValue.put(Constants.nameOfbitfield39,
				setBitfieldValue(Constants.nameOfbitfield39, Main.window.getResponseCode()));

		// Bitfields for which the values should be generated.

		if (transactionResult.equals("Approve") || transactionResult.equals("PartiallyApprove")) {
			responseBitfieldswithValue.put(Constants.nameOfbitfield38,
					setBitfieldValue(Constants.nameOfbitfield38, Constants.valueOfBitfield38));
			elementsInTransaction.add(38);
		}
		if (transactionResult.equals("Decline")) {
			responseBitfieldswithValue.put(Constants.nameOfbitfield44,
					setBitfieldValue(Constants.nameOfbitfield44, Constants.valueOfBitfield44));
			elementsInTransaction.add(44);
		}

		addFEPSpecificElements(Constants.financialSalesRequestMTI, transactionResult);
		HexEncoder encoder = new HexEncoder(responseMTI, eHeader);
		bitmap = encoder.tgenerateBinaryData(elementsInTransaction);
		encoder.setBitmap(bitmap);
		encoder.setResponseBitFieldsWithValue(responseBitfieldswithValue);
		encoder.encodeddata();
		responsePacket = encoder.getEncodedHexData();
		return responsePacket;
	}

	private void isDE54RequiredForHPSTransaction(TreeSet<Integer> elementsInTransaction) {
		if (Constants.activationRechargeCodes.contains(requestBitfieldsWithValues.get(Constants.nameOfbitfield3))) {
			if (Main.fepName.equals("HPS")) {
				responseBitfieldswithValue.put(Constants.nameOfbitfield54,
						setBitfieldValue(Constants.nameOfbitfield54, Constants.valueOfBitfield54));
				elementsInTransaction.add(54);
			}
		}
	}

	// ------------------------------------------------------------------------------------------------------------------
	/*
	 * This method is used to generate the response for a reversal request(1420)
	 */
	// ------------------------------------------------------------------------------------------------------------------
	public String reversalMessageResponse() {
		String transactionResult = Main.window.getTransactionResult();
		if (transactionResult.equals("PartiallyApprove")) {
			transactionResult = "Approve";
		}
		String responsePacket = "", bitmap, bitfield39 = "";
		elementsInTransaction = new TreeSet<>(Arrays.asList(Constants.elementsInReversalTransaction));
		generateResponseBitfieldswithValue(elementsInTransaction);

		// Bitfields for which the values should be generated.

		if (transactionResult.equals("Approve")) {
			responseBitfieldswithValue.put(Constants.nameOfbitfield39,
					setBitfieldValue(Constants.nameOfbitfield39, Constants.ValueOfBitfield39Reversal));
			responseBitfieldswithValue.put(Constants.nameOfbitfield38,
					setBitfieldValue(Constants.nameOfbitfield38, Constants.valueOfBitfield38));
			elementsInTransaction.add(38);
		} else {
			responseBitfieldswithValue.put(Constants.nameOfbitfield39,
					setBitfieldValue(Constants.nameOfbitfield39, Main.window.getResponseCode()));
			responseBitfieldswithValue.put(Constants.nameOfbitfield44,
					setBitfieldValue(Constants.nameOfbitfield44, Constants.valueOfBitfield44));
			elementsInTransaction.add(44);
		}
		addFEPSpecificElements(Constants.reversalRequestMTI, transactionResult);
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
		String responsePacket = "", bitmap, transactionResult = Main.window.getTransactionResult();
		if (transactionResult.equals("PartiallyApprove")) {
			transactionResult = "Approve";
		}
		elementsInTransaction = new TreeSet<>(Arrays.asList(Constants.elementsInReconsillationTransaction));
		generateResponseBitfieldswithValue(elementsInTransaction);
		// Bitfields for which the values should be generated.
		if (transactionResult.equals("Approve")) {
			responseBitfieldswithValue.put(Constants.nameOfbitfield39,
					setBitfieldValue(Constants.nameOfbitfield39, Constants.ValueOfBitfield39Reconsillation));
		} else {
			responseBitfieldswithValue.put(Constants.nameOfbitfield39,
					setBitfieldValue(Constants.nameOfbitfield39, Main.window.getResponseCode()));
		}
		responseBitfieldswithValue.put(Constants.nameOfbitfield48,
				setBitfieldValue(Constants.nameOfbitfield48, Constants.valueOfBitfield48));
		addFEPSpecificElements(Constants.reconsillationRequestMTI, transactionResult);
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
		String bitfield2Value = "", bitfield2Length = "";
		if (requestPacketBitfields.containsKey(Constants.nameOfbitfield2)) {
			return requestPacketBitfields.get(Constants.nameOfbitfield2);
		} else if (requestPacketBitfields.containsKey(Constants.nameOfbitfield35)) {
			endPoint = requestPacketBitfields.get(Constants.nameOfbitfield35).indexOf('=');
			bitfield2Value = requestPacketBitfields.get(Constants.nameOfbitfield35).substring(2, endPoint);
			bitfield2Length = Integer.toString(bitfield2Value.length());
			if (bitfield2Length.length() < 2) {
				return "0" + bitfield2Value.length() + bitfield2Value;
			} else {
				return bitfield2Value.length() + bitfield2Value;
			}

		} else if (requestPacketBitfields.containsKey(Constants.nameOfbitfield45)) {
			endPoint = requestPacketBitfields.get(Constants.nameOfbitfield45).indexOf('^');
			bitfield2Value = requestPacketBitfields.get(Constants.nameOfbitfield45).substring(3, endPoint);
			bitfield2Length = Integer.toString(bitfield2Value.length());
			if (bitfield2Length.length() < 2) {
				return "0" + bitfield2Value.length() + bitfield2Value;
			} else {
				return bitfield2Value.length() + bitfield2Value;
			}
		}
		return "";
	}

	// ------------------------------------------------------------------------------------------------------------------
	/*
	 * This method is used to create a responsebitfield treemap. Treemap is used to
	 * make sure the bitfield values are sorted. Numbers of bitfields that are to be
	 * sent in response should be passed to the method.
	 */
	// ------------------------------------------------------------------------------------------------------------------
	public void generateResponseBitfieldswithValue(TreeSet<Integer> elementsInTransaction) {
		for (Integer currentEntry : elementsInTransaction) {
			String key = "BITFIELD" + currentEntry;
			responseBitfieldswithValue.put(key, requestBitfieldsWithValues.get(key));
			logger.debug(key + ":" + requestBitfieldsWithValues.get(key) + " added to the response bitfield map");
		}
	}

	// ------------------------------------------------------------------------------------------------------------------
	/*
	 * This method is used to generate a dynamic amount for partial approval. This
	 * takes transaction amount as input and returns half of it.
	 */
	// ------------------------------------------------------------------------------------------------------------------
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

	// ------------------------------------------------------------------------------------------------------------------
	/*
	 * This method is used to add HPS specific data elements Transaction type i.e.,
	 * request MTI should be passed as argument
	 */
	// ------------------------------------------------------------------------------------------------------------------
	public void addFEPSpecificElements(String transactionType, String transactionResult) {
		if (Main.fepName.equals("HPS")) {
			if (!transactionType.equals(Constants.reconsillationRequestMTI)) {
				responseBitfieldswithValue.put(Constants.nameOfbitfield2,
						generateBitfield2(requestBitfieldsWithValues));
				isDE54RequiredForHPSTransaction(elementsInTransaction);
			} else {
				if (transactionResult.equals("Approve")) {
					responseBitfieldswithValue.put(Constants.nameOfbitfield123,
							setBitfieldValue(Constants.nameOfbitfield123, Constants.valueOfBitfield123));
				}
			}
		} else if (Main.fepName.equals("FCB")) {
			sdf = new SimpleDateFormat("HHmmss");
			responseBitfieldswithValue.put(Constants.nameOfbitfield12, sdf.format(date));
			sdf = new SimpleDateFormat("MMdd");
			responseBitfieldswithValue.put(Constants.nameOfbitfield13, sdf.format(date));
			responseBitfieldswithValue.put(Constants.nameOfbitfield37,
					setBitfieldValue(Constants.nameOfbitfield37, Constants.valueOfBitfield37));

		} else if (Main.fepName.equals("INCOMM")) {
			responseBitfieldswithValue.put(Constants.nameOfbitfield5,
					requestBitfieldsWithValues.get(Constants.nameOfbitfield4));
		}

	}
	// ------------------------------------------------------------------------------------------------------------------
	/*
	 * This method is used to identify the bitfield and add length of bitfield if
	 * required.
	 */
	// ------------------------------------------------------------------------------------------------------------------

	public static String setBitfieldValue(String bitfieldName, String bitfieldValue) {
		int variableLengthValue;
		String bitfieldLength;
		BitFieldData bitfieldData = new BitFieldData();
		variableLengthValue = bitfieldData.bitfieldLength.get(bitfieldName);
		if (variableLengthValue > 0) {
			return bitfieldValue;
		} else if (variableLengthValue == -2) {
			bitfieldLength = Integer.toString(bitfieldValue.length());
			if (bitfieldLength.length() < 2) {
				bitfieldLength = "0" + bitfieldLength;
			}
			return bitfieldLength + bitfieldValue;
		} else if (variableLengthValue == -3) {
			bitfieldLength = Integer.toString(bitfieldValue.length());
			if (bitfieldLength.length() < 3) {
				if (bitfieldLength.length() < 2) {
					bitfieldLength = "00" + bitfieldLength;
				} else {
					bitfieldLength = "0" + bitfieldLength;
				}
			}
			return bitfieldLength + bitfieldValue;
		} else {
			return bitfieldValue;
		}

	}

}