package com.HostSimulator;

import java.util.Map;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.HostSimulator.BitFieldData;

public class HexEncoder {
	final static Logger logger = Logger.getLogger(HexEncoder.class);
	private String encodedHexData, elementsInTransaction, bitmap, bitmapToHex, bitfieldValues, bitfieldValuesToHex, MTI,
			MTItoHex, eHeader, eHeaderToHex;

	private Map<String, String> responseBitFieldsWithValue;
	Converter converter = new Converter();

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
		PropertyConfigurator.configure("log4j.properties");
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
			if (Main.fepName.equals("HPS")) {
				encodeDataForHPS();
			} else if (Main.fepName.equals("FCB")) {
				encodeDataForFCB();
			}

		} catch (NullPointerException e) {
			logger.fatal("Unable to encode the given data");
		}

	}

	private void encodeDataForHPS() {
		logger.debug("Starting the encoding of response packet");
		MTItoHex = converter.asciitoHex(MTI);
		eHeaderToHex = converter.asciitoHex(eHeader);
		bitmapToHex = converter.binaryToHex(bitmap);
		bitfieldValues = generateBitFieldValues();
		bitfieldValuesToHex = converter.asciitoHex(bitfieldValues);
		String tempHexData = eHeaderToHex + " " + MTItoHex + " " + bitmapToHex + " " + bitfieldValuesToHex;
		encodedHexData = tempHexData.replaceAll("\\s", "");
	}

	private void encodeDataForFCB() {
		logger.debug("Starting the encoding of response packet");
		eHeaderToHex = converter.asciitoHex(eHeader);
		bitmapToHex = converter.binaryToHex(bitmap);
		bitfieldValues = generateBitFieldValues();
		String tempHexData = eHeaderToHex + " " + MTI + " " + bitmapToHex + " " + bitfieldValues;
		encodedHexData = tempHexData.replaceAll("\\s", "");
	}

	// --------------------------------------------------------------------------------------------------
	/*
	 * This function is used to create a bitmap after the elements invovled in the
	 * transaction are identified. This takes string array of bit field numbers as
	 * input and generates the bitmap based on it
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

	public static String tgenerateBinaryData(TreeSet<Integer> elementsInTransaction) {
		StringBuilder binaryData = new StringBuilder();
		int highestBitfield = elementsInTransaction.last();
		int bitmapLength;
		if (highestBitfield < 65) {
			bitmapLength = 64;
			binaryData.append("0");
		} else {
			bitmapLength = 128;
			binaryData.append("1");
		}
		int i = 2;
		for (Integer currentElement : elementsInTransaction) {
			boolean matchFound = false;
			do {
				if (i == currentElement) {
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
	 * This function finds the overall length of the hexData and generates the first
	 * two bytes of the hex encoding process
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
			logger.warn("Generated Hex Data is null");
		}

		finalHexData = converter.addSpacesToString(lengthConvertedToHex) + " " + hexData;
		return finalHexData;
	}

	// ---------------------------------------------------------------------------------------------------------------------
	/*
	 * This function is used to generate string of all the bitfield values involved
	 * in the transaction Certain bitfield are expected to have variable length. So
	 * bitfield value is picked from BitfieldValue hashmap in Bitfield data file and
	 * compared in the bitfieldlength hashmap. If the bitfield is a variable length
	 * one, then a prefix is added to denote the length
	 */
	// ---------------------------------------------------------------------------------------------------------------------
	public String generateBitFieldValues() {
		String finalBitfieldValues = "", currentBitfield, currentBitfieldLength;
		BitFieldData bitFieldLength = new BitFieldData();
		int currentBit;
		logger.debug("Trying to generate the bitfield values");
		for (Map.Entry<String, String> currentEntry : responseBitFieldsWithValue.entrySet()) {
			currentBitfield = currentEntry.getKey();
			logger.debug("Generating the value of " + currentBitfield);
			currentBit = Integer.parseInt(currentBitfield.substring(8));
			if (bitFieldLength.bitfieldLength.get(currentBitfield) > 0) {
				int numberOfSpacesRequired = bitFieldLength.bitfieldLength.get(currentBitfield)
						- currentEntry.getValue().length();
				String spaces = "";
				for (int i = 0; i < numberOfSpacesRequired; i++) {
					spaces = spaces + " ";
				}
				if (Main.fepName.equals("FCB") && Constants.elementsInHexFormatforFCBTransaction.contains(currentBit)) {
					finalBitfieldValues = finalBitfieldValues + converter.asciitoHex(currentEntry.getValue() + spaces);
				} else {
					finalBitfieldValues = finalBitfieldValues + currentEntry.getValue() + spaces;
				}
				logger.debug("Value of " + currentBitfield + ", " + currentEntry.getValue()
						+ " was added to the response string");
			} else if (bitFieldLength.bitfieldLength.get(currentBitfield) == -2) {
				currentBitfieldLength = currentEntry.getValue().substring(0, 2);
				if (Main.fepName.equals("FCB") && Constants.elementsInHexFormatforFCBTransaction.contains(currentBit)) {
					finalBitfieldValues = finalBitfieldValues
							+ converter.asciitoHex(currentBitfieldLength + currentEntry.getValue());

				} else {
					finalBitfieldValues = finalBitfieldValues + currentBitfieldLength
							+ currentEntry.getValue().substring(2);
				}
				logger.debug("Value of " + currentBitfield + " " + currentEntry.getValue()
						+ " was added to the response string");
			} else if (bitFieldLength.bitfieldLength.get(currentBitfield) == -3) {
				if (Main.fepName.equals("FCB")) {
					currentBitfieldLength = "0" + currentEntry.getValue().substring(0, 3);
				} else {
					currentBitfieldLength = currentEntry.getValue().substring(0, 3);
				}
				if (Main.fepName.equals("FCB") && Constants.elementsInHexFormatforFCBTransaction.contains(currentBit)) {
					finalBitfieldValues = finalBitfieldValues
							+ converter.asciitoHex(currentBitfieldLength + currentEntry.getValue());

				} else {
					finalBitfieldValues = finalBitfieldValues + currentBitfieldLength
							+ currentEntry.getValue().substring(3);
				}
				logger.debug("Value of " + currentBitfield + " " + currentEntry.getValue()
						+ " was added to the response string");
			}

		}

		return finalBitfieldValues;
	}

}