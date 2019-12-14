package com.HostSimulator;

import java.util.Map;

import com.HostSimulator.BitFieldData;

public class HexEncoder {
	private String encodedHexData, elementsInTransaction, bitmap, bitmapToHex, bitfieldValues, bitfieldValuesToHex, MTI,
			MTItoHex, eHeader, eHeaderToHex;

	private Map<String, String> responseBitFieldsWithValue;

	public void setResponseBitFieldsWithValue(Map<String, String> responseBitFieldsWithValue) {
		this.responseBitFieldsWithValue = responseBitFieldsWithValue;
	}

	public void setBitmap(String bitmap) {
		this.bitmap = bitmap;
	}

	public String getBitmapToHex() {
		return bitmapToHex;
	}

	public String getMTItoHex() {
		return MTItoHex;
	}

	public void seteHeaderToHex(String eHeaderToHex) {
		this.eHeaderToHex = eHeaderToHex;
	}

	public String geteHeaderToHex() {
		return eHeaderToHex;
	}

	public String getEncodedHexData() {
		return encodedHexData;
	}

	public String getBitfieldValuesToHex() {
		return bitfieldValuesToHex;
	}

	public String getBitFieldValues() {
		return bitfieldValues;
	}

	public HexEncoder(String MTI, String eHeader) {
		this.MTI = MTI;
		this.eHeader = eHeader;
		// encodeddata();
	}

	public String getHexData() {
		return bitmapToHex;
	}

	public String getBitmap() {
		return bitmap;
	}

	public String getElementsInTransaction() {
		return elementsInTransaction;
	}

	public void encodeddata() {
		try {
			Converter converter = new Converter();
			MTItoHex = converter.asciitoHex(MTI);
			eHeaderToHex = converter.asciitoHex(eHeader);
			//elementsInTransaction = pickElementsInTransaction();
			// bitmap = generateBinaryData(elementsInTransaction);
			bitmapToHex = converter.binaryToHex(bitmap);
			bitfieldValues = generateBitFieldValues();
			bitfieldValuesToHex = converter.asciitoHex(bitfieldValues);
			String tempHexData = eHeaderToHex + " " + MTItoHex + " " + bitmapToHex + " " + bitfieldValuesToHex;
			// encodedHexData = generateEncodedHexData(tempHexData);
			encodedHexData = tempHexData.replaceAll("\\s", "");
		} catch (NullPointerException e) {
			System.out.println("Unable to encode the data. Please check the data.");
		}

	}

	// --------------------------------------------------------------------------------------------------
	/*
	 * This function is used to identify the bitfields involved in the
	 * transaction. Takes the bitfieldwithvalues hashmap as input and returns a
	 * string with numbers representing the bitfields invovled in the
	 * transaction This takes boolean value as input. If set as true, returns
	 * elementsInTransaction If set as false, will return the bitfieldvalues
	 * involved in the transaction
	 */
	// ---------------------------------------------------------------------------------------------------
	public String pickElementsInTransaction() {
		BitFieldData bitFieldData = new BitFieldData(false);
		String elementsInTransaction = "", bitFieldValuesInTransaction = "";
		for (Map.Entry<String, String> currentEntry : bitFieldData.bitfieldValue.entrySet()) {
			if (currentEntry.getValue().equals("") == false) {
				elementsInTransaction = elementsInTransaction + currentEntry.getKey().replace("BITFIELD", "") + " ";
				// bitFieldValuesInTransaction = bitFieldValuesInTransaction +
				// currentEntry.getValue()+" ";
			}
		}
		return elementsInTransaction;

	}

	// --------------------------------------------------------------------------------------------------
	/*
	 * This function is used to create a bitmap after the elements invovled in
	 * the transaction are identified. This takes string array of bit field
	 * numbers as input and generates the bitmap based on it
	 */
	// ---------------------------------------------------------------------------------------------------
	public String generateBinaryData(String elementsInTransaction) {
		String[] elementArray = elementsInTransaction.split(" ");
		StringBuilder binaryData = new StringBuilder();
		int highestBitfield = Integer.parseInt(elementArray[elementArray.length - 1]);
		int bitmapLength;
		if (highestBitfield < 65) {
			bitmapLength = 64;
			binaryData.append("0");
		} else {
			bitmapLength = 128;
			binaryData.append("1");
		}
		int i = 2;
		for (String currentElement : elementArray) {
			boolean matchFound = false;
			do {
				if (i == Integer.parseInt(currentElement)) {
					binaryData.append("1");
					i++;
					// if the final element is reached, loop will break and the
					// remaining bits will not be set.
					// To avoid this we will not break the loop when it is last
					// element.
					if (i <= highestBitfield) {
						matchFound = true;
					}
				} else {
					binaryData.append("0");
					i++;
				}
				if ((i - 1) % 8 == 0) {
					binaryData.append(" ");
				}
				if (matchFound) {
					break;
				}

			} while (i <= bitmapLength);
		}
		return binaryData.toString();
	}

	// --------------------------------------------------------------------------------------------------------------------
	/*
	 * This function finds the overall length of the hexData and generates the
	 * first two bytes of the hex encoding process
	 */
	// --------------------------------------------------------------------------------------------------------------------
	public String generateEncodedHexData(String hexData) {
		Converter converter = new Converter();
		String finalHexData = "", lengthConvertedToHex;
		String[] tempArray = hexData.split(" ");
		int arrayLength, numberOfDigits;
		arrayLength = (tempArray.length) + 2;
		lengthConvertedToHex = Integer.toHexString(arrayLength);
		numberOfDigits = lengthConvertedToHex.length();
		switch (numberOfDigits) {
		case 1:
			lengthConvertedToHex = "000" + lengthConvertedToHex;
			break;
		case 2:
			lengthConvertedToHex = "00" + lengthConvertedToHex;
			break;
		case 3:
			lengthConvertedToHex = "0" + lengthConvertedToHex;
			break;
		default:
			System.out.println("Generated Hex Data is null");
		}

		finalHexData = converter.addSpacesToString(lengthConvertedToHex) + " " + hexData;
		return finalHexData;
	}

	// ---------------------------------------------------------------------------------------------------------------------
	/*
	 * This function is used to generate string of all the bitfield values
	 * involved in the transaction Certain bitfield are expected to have
	 * variable length. So bitfield value is picked from BitfieldValue hashmap
	 * in Bitfield data file and compared in the bitfieldlength hashmap. If the
	 * bitfield is a variable length one, then a prefix is added to denote the
	 * length
	 */
	// ---------------------------------------------------------------------------------------------------------------------
	public String generateBitFieldValues() {
		String finalBitfieldValues = "", currentBitfield, currentBitfieldLength;
		BitFieldData bitFieldLength = new BitFieldData(true);
		// BitFieldData bitFieldValues = new BitFieldData(false);
		// for(Map.Entry<String, String> currentEntry :
		// bitFieldValues.bitfieldValue.entrySet()){
		for (Map.Entry<String, String> currentEntry : responseBitFieldsWithValue.entrySet()) {
			if (currentEntry.getValue().equals("") == false) {
				currentBitfield = currentEntry.getKey();
				if (bitFieldLength.bitfieldLength.get(currentBitfield) > 0) {
					int numberOfSpacesRequired = bitFieldLength.bitfieldLength.get(currentBitfield)
							- currentEntry.getValue().length();
					String spaces = "";
					for (int i = 0; i < numberOfSpacesRequired; i++) {
						spaces = spaces + " ";
					}

					finalBitfieldValues = finalBitfieldValues + currentEntry.getValue() + spaces;
				} else if (bitFieldLength.bitfieldLength.get(currentBitfield) == -2) {
					currentBitfieldLength = Integer.toString(currentEntry.getValue().length());
					if (currentBitfieldLength.length() < 2) {
						currentBitfieldLength = "0" + currentBitfieldLength;
					}
					finalBitfieldValues = finalBitfieldValues + currentBitfieldLength + currentEntry.getValue();
				} else if (bitFieldLength.bitfieldLength.get(currentBitfield) == -3) {
					currentBitfieldLength = Integer.toString(currentEntry.getValue().length());
					if (currentBitfieldLength.length() < 3) {
						if (currentBitfieldLength.length() < 2) {
							currentBitfieldLength = "00" + currentBitfieldLength;
						} else {
							currentBitfieldLength = "0" + currentBitfieldLength;
						}
					}
					finalBitfieldValues = finalBitfieldValues + currentBitfieldLength + currentEntry.getValue();
				}
			}
		}

		return finalBitfieldValues;
	}
}