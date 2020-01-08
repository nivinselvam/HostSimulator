package com.HostSimulator;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.HostSimulator.BitFieldData;

public class HexDecoder {

	static final Logger log = Logger.getLogger("HexDecoder.class");
	public static Converter converter = new Converter();
	private int dataLength, currentPosition;
	private String eHeader, MTI, primaryBitMap, secondaryBitmap, consolidatedBitmap;
	Map<String, String> bitFieldwithValues = new LinkedHashMap<String, String>();

	public HexDecoder(String hexData) {
		try {
			decodedData(hexData);
		} catch (NumberFormatException e) {
			System.out.println("Request packet Format error. Please make sure the Hex data is correct");
		}

	}

	public int getDataLength() {
		return dataLength;
	}

	public String geteHeader() {
		return eHeader;
	}

	public String getMTI() {
		return MTI;
	}

	public String getPrimaryBitMap() {
		return primaryBitMap;
	}

	public String getSecondaryBitmap() {
		return secondaryBitmap;
	}

	public String getConsolidatedBitmap() {
		return consolidatedBitmap;
	}

	public Map<String, String> getBitFieldwithValues() {
		return bitFieldwithValues;
	}

	// -----------------------------------------------------------------------------------------------------------
	/*
	 * Takes the hex array as input, splits the data and convets into ascii values
	 */
	// -----------------------------------------------------------------------------------------------------------
	public void decodedData(String hexData) {
		hexData = converter.addSpacesToString(hexData);
		Boolean isSecondaryBitmapAvailable = false;
		String hexDataLengthValue, primaryBitmapValue, secondaryBitmapValue;
		try {
			eHeader = converter.hexToASCII(hexData.substring(Constants.eHeaderStartPoint, Constants.eHeaderEndPoint));
		} catch (NumberFormatException e) {
			eHeader = "";
		}

		if (hexData.length() > Constants.eHeaderEndPoint) {
			// Grep the MTI from hexData
			if (Main.fepName.equals("FCB")) {
				// FCB sends MTI as plain string unlike other feps
				MTI = hexData.substring(Constants.mtiStartPoint, Constants.mtiEndPoint).replace(" ", "");
			} else {
				MTI = converter.hexToASCII(hexData.substring(Constants.mtiStartPoint, Constants.mtiEndPoint));
			}

			// Grep the primary bitmap from hexData
			primaryBitmapValue = hexData.substring(Constants.primaryBitmapStartPoint, Constants.primaryBitmapEndPoint);
			primaryBitMap = converter.hexToBinary(primaryBitmapValue);
			// While creating a bitfieldAndValueMapping spaces in the hexdata will be
			// removed.
			// Hence current position should be calculated by removing spaces.
			currentPosition = Constants.primaryBitmapPosition;
			if (primaryBitMap.charAt(0) == '1') {
				isSecondaryBitmapAvailable = true;
			}

			// Grep the secondary bitmap from hex Data if available
			if (isSecondaryBitmapAvailable) {
				secondaryBitmapValue = hexData.substring(Constants.secondaryBitmapStartPoint,
						Constants.secondaryBitmapEndPoint);
				secondaryBitmap = converter.hexToBinary(secondaryBitmapValue);
				currentPosition = Constants.secondaryBitmapEndPosition;
			}

			// Bitmap consolidation
			if (isSecondaryBitmapAvailable) {
				consolidatedBitmap = primaryBitMap.replaceAll("\\s", "") + secondaryBitmap.replaceAll("\\s", "");
			} else {
				consolidatedBitmap = primaryBitMap.replaceAll("\\s", "");
			}

			// identify the bitfields involved in the transaction
			if (Main.fepName.equals("FCB")) {
				bitFieldwithValues = bitfieldAndValueMappingForFCB(consolidatedBitmap, hexData);
			} else {
				bitFieldwithValues = bitfieldAndValueMapping(consolidatedBitmap, hexData);
			}

		}
	}

	// --------------------------------------------------------------------------------------------------
	/*
	 * This function is used to identify the bitfields involved in the transaction
	 * Takes the consolidated bitmap as input and returns a string with integers
	 * representing bitfields
	 */
	// ---------------------------------------------------------------------------------------------------
	public String getElementsInTransaction(String bitmap) {
		String elementsInTransaction = "";
		for (int i = 0; i < bitmap.length(); i++) {
			if (bitmap.charAt(i) == '1') {
				elementsInTransaction = elementsInTransaction + (i + 1) + " ";
			}
		}
		return elementsInTransaction;
	}

	// -----------------------------------------------------------------------------------------------------
	/*
	 * This function is used to process the bitmap and identify the values
	 * associated with bitfields Takes consolidated bitmap involved in the
	 * transaction as input. Returns a linked hashmap which has bitfields as key and
	 * corresponding values as hashmap value
	 */
	// ------------------------------------------------------------------------------------------------------
	public Map<String, String> bitfieldAndValueMapping(String consolidatedBitmap, String hexData) {
		Map<String, String> bitfieldAndValueMap = new LinkedHashMap<String, String>();
		String tempString = getElementsInTransaction(consolidatedBitmap);
		String tempHexData = hexData.replaceAll("\\s", "");
		BitFieldData bitfieldLength = new BitFieldData();
		String[] elements = tempString.split(" ");
		for (String element : elements) {
			String currentBitField = "BITFIELD" + element, currentBitFieldValue;
			int currentBitfieldLength = (bitfieldLength.bitfieldLength.get(currentBitField)) * 2;
			if (currentBitfieldLength > 0 && (currentBitField.equals("BITFIELD1")) == false) {
				currentBitFieldValue = tempHexData.substring(currentPosition, currentPosition + currentBitfieldLength);
				currentBitFieldValue = converter.hexToASCII(converter.addSpacesToString(currentBitFieldValue));
				bitfieldAndValueMap.put(currentBitField, currentBitFieldValue);
				currentPosition = currentPosition + currentBitfieldLength;
			} else if (currentBitfieldLength == -4) {
				currentBitfieldLength = Integer.parseInt(converter.hexToASCII(
						converter.addSpacesToString(tempHexData.substring(currentPosition, currentPosition + 4))));
				currentPosition = currentPosition + 4;
				currentBitfieldLength = (currentBitfieldLength) * 2;
				currentBitFieldValue = tempHexData.substring(currentPosition, currentPosition + currentBitfieldLength);
				tempString = (Integer.toString(currentBitfieldLength / 2));
				if (tempString.length() < 2) {
					tempString = "0" + tempString;
				}
				currentBitFieldValue = tempString
						+ converter.hexToASCII(converter.addSpacesToString(currentBitFieldValue));
				bitfieldAndValueMap.put(currentBitField, currentBitFieldValue);
				currentPosition = currentPosition + currentBitfieldLength;
			} else if (currentBitfieldLength == -6) {

				currentBitfieldLength = Integer.parseInt(converter.hexToASCII(
						converter.addSpacesToString(tempHexData.substring(currentPosition, currentPosition + 6))));
				currentPosition = currentPosition + 6;
				currentBitfieldLength = (currentBitfieldLength) * 2;
				currentBitFieldValue = tempHexData.substring(currentPosition, currentPosition + currentBitfieldLength);
				tempString = (Integer.toString(currentBitfieldLength / 2));
				if (tempString.length() < 3) {
					if (tempString.length() < 2) {
						tempString = "00" + tempString;
					} else {
						tempString = "0" + tempString;
					}
				}
				currentBitFieldValue = tempString
						+ converter.hexToASCII(converter.addSpacesToString(currentBitFieldValue));
				bitfieldAndValueMap.put(currentBitField, currentBitFieldValue);
				currentPosition = currentPosition + currentBitfieldLength;
			}

		}
		return bitfieldAndValueMap;
	}

	// -----------------------------------------------------------------------------------------------------
	/*
	 * Below function should be used to create the bitfield with values mapping for
	 * FCB since the fep follows the format which is unique to itself
	 */
	// ------------------------------------------------------------------------------------------------------
	public Map<String, String> bitfieldAndValueMappingForFCB(String consolidatedBitmap, String hexData) {
		Map<String, String> bitfieldAndValueMap = new LinkedHashMap<String, String>();
		String tempString = getElementsInTransaction(consolidatedBitmap);
		String tempHexData = hexData.replaceAll("\\s", "");
		BitFieldData bitfieldLength = new BitFieldData();
		String[] elements = tempString.split(" ");
		for (String element : elements) {
			String currentBitField = "BITFIELD" + element, currentBitFieldValue;
			int currentBitfieldLength = (bitfieldLength.bitfieldLength.get(currentBitField));
			currentBitfieldLength = defineBitfieldLengthForFCBTransaction(element, currentBitfieldLength);
			if (currentBitfieldLength > 0 && (currentBitField.equals("BITFIELD1")) == false) {
				if(Constants.elementsInHexFormatforFCBTransaction.contains(Integer.parseInt(element))) {
					currentBitFieldValue = converter.hexToASCII(converter.addSpacesToString(tempHexData.substring(currentPosition, currentPosition + currentBitfieldLength)));
				}else {
					currentBitFieldValue = tempHexData.substring(currentPosition, currentPosition + currentBitfieldLength);
				}				
				bitfieldAndValueMap.put(currentBitField, currentBitFieldValue);
				currentPosition = currentPosition + currentBitfieldLength;
			} else if (currentBitfieldLength == -2 || currentBitfieldLength == -4) {
				fcbLLVARCalculationOFBitfield(bitfieldAndValueMap, tempHexData, element, currentBitField);

			} else if (currentBitfieldLength == -3 || currentBitfieldLength == -6) {
				fcbLLLVARCalculationOFBitfield(bitfieldAndValueMap, tempHexData, element, currentBitField);

			}
		}

		return bitfieldAndValueMap;
	}
	// -----------------------------------------------------------------------------------------------------------------------------------------
	/*
	 * This method is used to read the value of bitfield with variable length which is 3 digits i.e., LLLVAR
	 * 
	 */
	// -----------------------------------------------------------------------------------------------------------------------------------------

	private void fcbLLLVARCalculationOFBitfield(Map<String, String> bitfieldAndValueMap, String tempHexData,
			String element, String currentBitField) {
		String tempString;
		String currentBitFieldValue;
		int currentBitfieldLength;
		// If plain string LL will be the plain length of the bitfield value.
		// If hex string LL*2 will be the length of the bitfield value.
		if (Constants.elementsInHexFormatforFCBTransaction.contains(Integer.parseInt(element))) {
			currentBitfieldLength = Integer.parseInt(tempHexData.substring(currentPosition, currentPosition + 6));
			currentPosition = currentPosition + 6;
			currentBitfieldLength = (currentBitfieldLength) * 2;
			currentBitFieldValue = tempHexData.substring(currentPosition,
					currentPosition + currentBitfieldLength);
			tempString = (Integer.toString(currentBitfieldLength / 2));
			if (tempString.length() < 3) {
				if (tempString.length() < 2) {
					tempString = "00" + tempString;
				} else {
					tempString = "0" + tempString;
				}
			}
			currentBitFieldValue = tempString
					+ converter.hexToASCII(converter.addSpacesToString(currentBitFieldValue));
		} else {
			currentBitfieldLength = Integer
					.parseInt(tempHexData.substring(currentPosition, currentPosition + 4));
			currentPosition = currentPosition + 4;
			// Only for bitfield 55, value is read like in Hex format, but actual value is
			// plain string.
			if (currentBitField.equals(Constants.nameOfbitfield55)) {
				currentBitfieldLength = currentBitfieldLength * 2;
			}
			// if length of bitfield value is and odd number, one more digit to the left
			// should be read.
			if (currentBitfieldLength % 2 == 1) {
				currentBitFieldValue = tempHexData.substring(currentPosition,
						currentPosition + (currentBitfieldLength + 1));
				currentPosition = currentPosition + currentBitfieldLength + 1;
			} else {
				currentBitFieldValue = tempHexData.substring(currentPosition,
						currentPosition + currentBitfieldLength);
				currentPosition = currentPosition + currentBitfieldLength;
			}
			// Since the value is multipled by 2 previously, should be divided to get the
			// actual value
			if (currentBitField.equals(Constants.nameOfbitfield55)) {
				currentBitfieldLength = currentBitfieldLength / 2;
			}

		}
		bitfieldAndValueMap.put(currentBitField, currentBitfieldLength + currentBitFieldValue);
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------
	/*
	 * This method is used to read the value of bitfield with variable length which is 2 digits i.e., LLVAR
	 * 
	 */
	// -----------------------------------------------------------------------------------------------------------------------------------------

	private void fcbLLVARCalculationOFBitfield(Map<String, String> bitfieldAndValueMap, String tempHexData,
			String element, String currentBitField) {
		String tempString;
		String currentBitFieldValue;
		int currentBitfieldLength;
		// If plain string LL will be the plain length of the bitfield value.
		// If hex string LL*2 will be the length of the bitfield value.
		if (Constants.elementsInHexFormatforFCBTransaction.contains(Integer.parseInt(element))) {
			tempString = tempHexData.substring(currentPosition, currentPosition + 4);
			currentBitfieldLength = Integer.parseInt(tempString);
			currentPosition = currentPosition + 4;
			currentBitfieldLength = (currentBitfieldLength) * 2;
			currentBitFieldValue = tempHexData.substring(currentPosition,
					currentPosition + currentBitfieldLength);
			currentBitFieldValue = tempString
					+ currentBitFieldValue;
			currentPosition = currentPosition + currentBitfieldLength;
		} else {
			currentBitfieldLength = Integer
					.parseInt(tempHexData.substring(currentPosition, currentPosition + 2));
			currentPosition = currentPosition + 2;
			// if length of bitfield value is and odd number, one more digit to the left
			// should be read.
			if (currentBitfieldLength % 2 == 1) {
				currentBitFieldValue = currentBitfieldLength
						+ tempHexData.substring(currentPosition, currentPosition + (currentBitfieldLength + 1));
				currentPosition = currentPosition + currentBitfieldLength + 1;
			} else {
				currentBitFieldValue = currentBitfieldLength
						+ tempHexData.substring(currentPosition, currentPosition + currentBitfieldLength);
				currentPosition = currentPosition + currentBitfieldLength;
			}
		}
		bitfieldAndValueMap.put(currentBitField, currentBitFieldValue);
	}
	// -----------------------------------------------------------------------------------------------------------------------------------------
	/*
	 * Since few elements of FCB transaction should be read as plain text and few as hex data.
	 * Length should be set accordingly
	 */
	// -----------------------------------------------------------------------------------------------------------------------------------------
	private int defineBitfieldLengthForFCBTransaction(String element, int currentBitfieldLength) {
		if (Constants.elementsInHexFormatforFCBTransaction.contains(Integer.parseInt(element))) {
			currentBitfieldLength = currentBitfieldLength * 2;
		} else {
			if (currentBitfieldLength % 2 == 1) {
				currentBitfieldLength = currentBitfieldLength + 1;
			}
		}
		return currentBitfieldLength;
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------
	/*
	 * This function is used to retrive all the available data involved in the transaction
	 */
	// -----------------------------------------------------------------------------------------------------------------------------------------
	public void printEncodedData() {
		System.out.println("------------------------------------");
		try {
			System.out.println("eHeader: " + eHeader);
			System.out.println("MTI: " + MTI);
			for (Map.Entry<String, String> currentEntry : bitFieldwithValues.entrySet()) {
				System.out.println(currentEntry.getKey() + ": " + currentEntry.getValue());
			}
			System.out.println("------------------------------------");
		} catch (NullPointerException e) {
			System.out.println("Few data unavailable");
		}

	}
}