package com.HostSimulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class HPSConstants {

	public HPSConstants() {

	}
	//Transaction Status:
	public static final String approveTransaction = "Approve";
	public static final String partiallyApproveTransaction = "PartiallyApprove";
	public static final String declineTransaction = "Decline";
	//Transaction MTI:
	public static final String authorisationRequestMTI = "1100";
	public static final String authorisationResponseMTI = "1110";
	public static final String financialSalesRequestMTI = "1200";
	public static final String financialSalesResponseMTI = "1210";
	public static final String financialForceDraftRequestMTI = "1220";
	public static final String financialForceDraftResponseMTI = "1230";
	public static final String reversalRequestMTI = "1420";
	public static final String reversalResponseMTI = "1430";
	public static final String reconsillationRequestMTI = "1520";
	public static final String reconsillationResponseMTI = "1530";
	//BitFields involved in Transaction
	public static final Integer[] elementsInGenericTransaction = {2, 3, 4, 11, 12, 39, 41, 42, 49};
	public static final Integer[] elementsInReconsillationTransaction = {11, 12, 39, 41, 42, 48, 123};
	//Codes to be validated during transaction
	public static final String[] balanceInquiryCodes = {"313000", "318000", "318100", "309700", "319700", "316000", "313900"};
	public static final List<String> activationRechargeCodes = new ArrayList<String>(Arrays.asList("900060", "930060", "210060"));	
	//BitField Names:
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
	//BitField Values:
	public static final String valueOfBitfield4 = "000000010000";
	public static final String valueOfBitfield38 = "123456";
	public static final String valueOfBitfield44 = "0705";
	public static final String valueOfBitfield48 = "       0000000099";
	public static final String valueOfBitfield54 = "6501840C000000010000";
	public static final String valueOfBitfield123 = "0010002   CT  0000000000\\\\000000000000\\\\002   DB  0000000000\\\\000000000000\\\\002   MC  0000000000\\\\000000000000\\\\002   OH  0000000000\\\\000000000000\\\\002   PL  0000000000\\\\000000000000\\\\002   VI  0000000000\\\\000000000000\\\\007   CT  0000000000\\\\000000000000\\\\007   DB  0000000000\\\\000000000000\\\\299   CT  0000000000\\\\000000000000\\\\299   DB  0000000000\\\\000000000000\\\\";
}
