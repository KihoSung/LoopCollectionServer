package kr.co.kti.lcs.common;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;

/**
 * Util 공통 함수
 * 
 * @author yeski
 *
 */
public class Util
{
	/**
	 * Locale 상수 (한국)
	 */
	public static final Locale currentLocale = new Locale("KOREAN", "KOREA");
	
	/**
	 * 시간차이 9시간 
	 */
	public static final long lUtc = 1000 * 60 * 60 * 9;	// UNIX 시간 변환 중 GMT기준 시간으로 적용시 시간 갭 9시 //

	/**
	 * CRC Check Value
	 */
	public static final int[] crc16tab = { 0x0000, 0x1021, 0x2042, 0x3063, 0x4084, 0x50a5, 0x60c6, 0x70e7, 0x8108, 0x9129, 0xa14a, 0xb16b, 0xc18c, 0xd1ad, 0xe1ce, 0xf1ef, 0x1231, 0x0210, 0x3273, 0x2252, 0x52b5, 0x4294, 0x72f7, 0x62d6, 0x9339, 0x8318, 0xb37b, 0xa35a, 0xd3bd, 0xc39c, 0xf3ff, 0xe3de,
			0x2462, 0x3443, 0x0420, 0x1401, 0x64e6, 0x74c7, 0x44a4, 0x5485, 0xa56a, 0xb54b, 0x8528, 0x9509, 0xe5ee, 0xf5cf, 0xc5ac, 0xd58d, 0x3653, 0x2672, 0x1611, 0x0630, 0x76d7, 0x66f6, 0x5695, 0x46b4, 0xb75b, 0xa77a, 0x9719, 0x8738, 0xf7df, 0xe7fe, 0xd79d, 0xc7bc, 0x48c4, 0x58e5, 0x6886, 0x78a7,
			0x0840, 0x1861, 0x2802, 0x3823, 0xc9cc, 0xd9ed, 0xe98e, 0xf9af, 0x8948, 0x9969, 0xa90a, 0xb92b, 0x5af5, 0x4ad4, 0x7ab7, 0x6a96, 0x1a71, 0x0a50, 0x3a33, 0x2a12, 0xdbfd, 0xcbdc, 0xfbbf, 0xeb9e, 0x9b79, 0x8b58, 0xbb3b, 0xab1a, 0x6ca6, 0x7c87, 0x4ce4, 0x5cc5, 0x2c22, 0x3c03, 0x0c60, 0x1c41,
			0xedae, 0xfd8f, 0xcdec, 0xddcd, 0xad2a, 0xbd0b, 0x8d68, 0x9d49, 0x7e97, 0x6eb6, 0x5ed5, 0x4ef4, 0x3e13, 0x2e32, 0x1e51, 0x0e70, 0xff9f, 0xefbe, 0xdfdd, 0xcffc, 0xbf1b, 0xaf3a, 0x9f59, 0x8f78, 0x9188, 0x81a9, 0xb1ca, 0xa1eb, 0xd10c, 0xc12d, 0xf14e, 0xe16f, 0x1080, 0x00a1, 0x30c2, 0x20e3,
			0x5004, 0x4025, 0x7046, 0x6067, 0x83b9, 0x9398, 0xa3fb, 0xb3da, 0xc33d, 0xd31c, 0xe37f, 0xf35e, 0x02b1, 0x1290, 0x22f3, 0x32d2, 0x4235, 0x5214, 0x6277, 0x7256, 0xb5ea, 0xa5cb, 0x95a8, 0x8589, 0xf56e, 0xe54f, 0xd52c, 0xc50d, 0x34e2, 0x24c3, 0x14a0, 0x0481, 0x7466, 0x6447, 0x5424, 0x4405,
			0xa7db, 0xb7fa, 0x8799, 0x97b8, 0xe75f, 0xf77e, 0xc71d, 0xd73c, 0x26d3, 0x36f2, 0x0691, 0x16b0, 0x6657, 0x7676, 0x4615, 0x5634, 0xd94c, 0xc96d, 0xf90e, 0xe92f, 0x99c8, 0x89e9, 0xb98a, 0xa9ab, 0x5844, 0x4865, 0x7806, 0x6827, 0x18c0, 0x08e1, 0x3882, 0x28a3, 0xcb7d, 0xdb5c, 0xeb3f, 0xfb1e,
			0x8bf9, 0x9bd8, 0xabbb, 0xbb9a, 0x4a75, 0x5a54, 0x6a37, 0x7a16, 0x0af1, 0x1ad0, 0x2ab3, 0x3a92, 0xfd2e, 0xed0f, 0xdd6c, 0xcd4d, 0xbdaa, 0xad8b, 0x9de8, 0x8dc9, 0x7c26, 0x6c07, 0x5c64, 0x4c45, 0x3ca2, 0x2c83, 0x1ce0, 0x0cc1, 0xef1f, 0xff3e, 0xcf5d, 0xdf7c, 0xaf9b, 0xbfba, 0x8fd9, 0x9ff8,
			0x6e17, 0x7e36, 0x4e55, 0x5e74, 0x2e93, 0x3eb2, 0x0ed1, 0x1ef0 };

	/**
	 * 지역 값
	 */
	public static final String[] strArrayArea = { "", "서울", "부산", "대구", "인천", "광주", "대전", "울산", "경기", "강원", "충북", "충남", "전북", "전남", "경북", "경남", "제주", "세종" };

	/**
	 * 차량번호 한글 값
	 */
	public static final String[] strArrayHangul = { "가", "나", "다", "라", "마", "거", "너", "더", "러", "머", "버", "서", "어", "저", "고", "노", "도", "로", "모", "보", "소", "오", "조", "구", "누", "두", "루", "무", "부", "수", "우", "주", "바", "사", "아", "자", "허", "외교", "영사", "준외", "준영", "국기", "협정", "대표", "육", "공", "해", "어",
			"배", "하", "호" };

	/**
	 * 오류코드 
	 */
	public static final String[] strArrayStatus = { "11", "12", "13", "14", "21", "22", "31", "32", "41", "99", "00", "00", "00", "00", "00", "00", "00", "00" };

	/**
	 * TraceLog 기록
	 * 
	 * @param vertx
	 * @param strMessage
	 */
	public static void writeTraceLog(Vertx vertx, String strMessage)
	{
		vertx.eventBus().send(Const.EB_TRACE_LOG_WORKER, strMessage);
	}

	/**
	 * 트립시퀀스에서 날짜 추출
	 * 
	 * @param intTripSeq
	 * @return
	 */
	public static String getTripSeqToDate(int intTripSeq)
	{
		String strDate = new SimpleDateFormat("yyyyMMdd", currentLocale).format(intTripSeq * 1000L - lUtc);

		return strDate;
	}

	/**
	 * 트립시퀀스에서 시간 추출
	 * 
	 * @param intTripSeq
	 * @return
	 */
	public static String getHourTime(int intTripSeq)
	{
		String strDate = new SimpleDateFormat("HHmmss", currentLocale).format(intTripSeq * 1000L - lUtc);

		return strDate;
	}

	/**
	 * INFO 정보 DB 저장 요청 
	 * 
	 * @param vertx
	 * @param strIdentityNo
	 * @param strDbCommand
	 * @param joValue
	 */
	public static void updateDBInfo(Vertx vertx, String strIdentityNo, String strDbCommand, JsonObject joValue)
	{
		DeliveryOptions deliveryOptions = new DeliveryOptions();
		deliveryOptions.addHeader("IDENTITY_NO", strIdentityNo);
		deliveryOptions.addHeader("COMMAND_ID", DBConst.COMMAND_SET_TRIPINFO);

		switch (strDbCommand)
		{
			case DBConst.COMMAND_SET_TRIPINFO:

				vertx.eventBus().send(Const.EB_DAO_MANAGER_WORKER, joValue, deliveryOptions);

				break;

			default:
				break;

		}
	}

	/**
	 * Byte Order 변경
	 * 
	 * @param value
	 * @param Order
	 *            1:유지, 2:변경
	 * @return
	 */
	public static byte[] changeByteOrder(byte[] value, int Order)
	{
		int idx = value.length;
		byte[] Temp = new byte[idx];

		if (Order == 1)
		{
			Temp = value;
		}
		else if (Order == 2)
		{
			for (int i = 0; i < idx; i++)
			{
				Temp[i] = value[idx - (i + 1)];
			}
		}

		return Temp;
	}

	/**
	 * ByteArray에서 지정한 부분 추출
	 * 
	 * @param buff
	 * @param intStart
	 * @param intEnd
	 * @return
	 */
	public static byte[] getBytes(byte[] buff, int intStart, int intLength)
	{
		byte[] retByte = new byte[intLength];

		System.arraycopy(buff, intStart, retByte, 0, retByte.length);

		return retByte;
	}

	/**
	 * 수신된 Buffer 중에서 Command 추출 하기 
	 * 
	 * @param buff
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String getCommand(byte[] buff) throws UnsupportedEncodingException
	{
		String strReturn = "";

		byte[] retByte = changeByteOrder(getBytes(buff, 8, 4), 2); // Little Endian To Big Endian

		strReturn = new String(retByte, Const.ENCODE_TYPE);

		return strReturn;
	}

	/**
	 * ByteArray에서 지정부분 문자열 변환 추출
	 * 
	 * @param buff
	 * @param intStart
	 * @param intLength
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String getString(byte[] buff, int intStart, int intLength, String strOrder) throws UnsupportedEncodingException
	{
		String strReturn = "";

		byte[] retByte = new byte[intLength];

		if (strOrder.equals("B"))
		{
			retByte = changeByteOrder(getBytes(buff, intStart, intLength), 1);
		}
		else if (strOrder.equals("L"))
		{
			retByte = changeByteOrder(getBytes(buff, intStart, intLength), 2);
		}

		strReturn = new String(retByte, Const.ENCODE_TYPE);

		return strReturn.trim();
	}

	/**
	 * 지정한 크기만큼 Bytes 추출 
	 * 
	 * @param buff
	 * @param intStart
	 * @param intLength
	 * @param strOrder
	 * @return
	 */
	public static byte getByte(byte[] buff, int intStart, int intLength, String strOrder)
	{
		byte intReturn = 0;

		byte[] retByte = getBytes(buff, intStart, intLength);

		intReturn = retByte[0];

		return intReturn;
	}

	/**
	 * 지정된 크기만큼 Short 추출
	 * 
	 * @param buff
	 * @param intStart
	 * @param intLength
	 * @param strOrder
	 * @return
	 */
	public static short getShort(byte[] buff, int intStart, int intLength, String strOrder)
	{
		short intReturn = 0;

		byte[] retByte = getBytes(buff, intStart, intLength);

		if (strOrder.equals("B"))
		{
			intReturn = ByteBuffer.wrap(retByte).order(ByteOrder.BIG_ENDIAN).getShort();
		}
		else if (strOrder.equals("L"))
		{
			intReturn = ByteBuffer.wrap(retByte).order(ByteOrder.LITTLE_ENDIAN).getShort();
		}

		return intReturn;
	}

	/**
	 * ByteArray에서 지정부분 정수(Integer) 변환 추출
	 * 
	 * @param buff
	 * @param intStart
	 * @param intLength
	 * @param strOrder
	 *            (L:Little Endian,B:Big Endian)
	 * @return
	 */
	public static int getInteger(byte[] buff, int intStart, int intLength, String strOrder)
	{
		int intReturn = 0;

		byte[] retByte = getBytes(buff, intStart, intLength);

		if (strOrder.equals("B"))
		{
			intReturn = ByteBuffer.wrap(retByte).order(ByteOrder.BIG_ENDIAN).getInt();
		}
		else if (strOrder.equals("L"))
		{
			intReturn = ByteBuffer.wrap(retByte).order(ByteOrder.LITTLE_ENDIAN).getInt();
		}

		return intReturn;
	}

	/**
	 * ByteArray에서 지정부분 정수(Long) 변환 추출
	 * 
	 * @param buff
	 * @param intStart
	 * @param intLength
	 * @param strOrder
	 * @return
	 */
	public static long getLong(byte[] buff, int intStart, int intLength, String strOrder)
	{
		long longReturn = 0;

		byte[] retByte = getBytes(buff, intStart, intLength);

		// System.out.println("retByte:" + byteArrayToHex(retByte));

		if (strOrder.equals("B"))
		{
			longReturn = ByteBuffer.wrap(retByte).order(ByteOrder.BIG_ENDIAN).getLong();
		}
		else if (strOrder.equals("L"))
		{
			longReturn = ByteBuffer.wrap(retByte).order(ByteOrder.LITTLE_ENDIAN).getLong();
		}

		return longReturn;
	}

	/**
	 * ByteArray에서 지정부분 실수 변환 추출
	 * 
	 * @param buff
	 * @param intStart
	 * @param intLength
	 * @param strOrder
	 * @return
	 */
	public static float getFloat(byte[] buff, int intStart, int intLength, String strOrder)
	{
		float floatReturn = 0f;

		byte[] retByte = getBytes(buff, intStart, intLength);

		if (strOrder.equals("B"))
		{
			floatReturn = ByteBuffer.wrap(retByte).order(ByteOrder.BIG_ENDIAN).getFloat();
		}
		else if (strOrder.equals("L"))
		{
			floatReturn = ByteBuffer.wrap(retByte).order(ByteOrder.LITTLE_ENDIAN).getFloat();
		}

		return floatReturn;
	}

	/**
	 * 16진수 문자열을 ByteArray로 변경
	 * 
	 * @param sHexStr
	 * @return
	 */
	public static byte[] hexToByteArray(String sHexStr)
	{
		if (sHexStr == null || sHexStr.length() == 0)
		{
			return null;
		}

		byte[] ba = new byte[sHexStr.length() / 2];
		for (int i = 0; i < ba.length; i++)
		{
			ba[i] = (byte) Integer.parseInt(sHexStr.substring(2 * i, 2 * i + 2), 16);
		}
		return ba;
	}

	/**
	 * byte[] to hex : unsigned byte(바이트) 배열을 16진수 문자열로 바꾼다.
	 *
	 * @param ba
	 *            byte[]
	 * @return
	 */
	public static String byteArrayToHex(byte[] ba)
	{
		if (ba == null || ba.length == 0)
		{
			return null;
		}

		StringBuffer sb = new StringBuffer(ba.length * 2);
		int ch;
		for (int x = 0; x < ba.length; x++)
		{
			ch = 0xff & ba[x];
			if (ch < 0x10) sb.append("0");

			sb.append(Integer.toString(ch, 16));
		}
		return sb.toString();
	}

	/**
	 * Timestamp 비교 이전이 같거나 크면 false, 현재가 크면 true
	 * 
	 * @param beforeTimestamp
	 * @param currentTimestamp
	 * @return
	 */
	public static boolean compareDate(long beforeTimestamp, long currentTimestamp)
	{
		boolean boolReturn = false;

		if (beforeTimestamp < currentTimestamp)
		{
			boolReturn = true;
		}

		return boolReturn;
	}

	/**
	 * Make Response ByteArray
	 * 
	 * @param byteServerIp
	 * @param strCommand
	 *            (ex:DTSC, DTSS, EVSS)
	 * @param strData
	 *            (00:Not Ready, 01:Ready, Next Data)
	 * @return
	 */
	public static byte[] makeResponse(byte[] byteServerIp, String strCommand, String strData)
	{
		byte[] byteSPK = hexToByteArray("CCCF");
		byte[] byteDeviceId = hexToByteArray("0300");
		byte[] byteIdentityNo = hexToByteArray("00000000");
		byte[] byteCommand = changeByteOrder(strCommand.getBytes(), 2);
		byte[] byteIndex = hexToByteArray("0000");
		byte[] byteCount = hexToByteArray("0100");
		byte[] byteLength = hexToByteArray("01000000");
		byte[] byteData = hexToByteArray(strData);

		// Make CRC
		int intTemp = 20 + byteData.length;
		byte[] byteMakeCRC = new byte[intTemp];
		int intPos = byteTobyte(byteMakeCRC, 0, byteSPK);
		intPos = byteTobyte(byteMakeCRC, intPos, byteDeviceId);
		intPos = byteTobyte(byteMakeCRC, intPos, byteIdentityNo);
		intPos = byteTobyte(byteMakeCRC, intPos, byteCommand);
		intPos = byteTobyte(byteMakeCRC, intPos, byteIndex);
		intPos = byteTobyte(byteMakeCRC, intPos, byteCount);
		intPos = byteTobyte(byteMakeCRC, intPos, byteLength);
		intPos = byteTobyte(byteMakeCRC, intPos, byteData);

		byte[] byteCRC = makeCRC(byteMakeCRC);

		byte[] byteEPK = hexToByteArray("CFD0");

		byte[] byteReturn = new byte[intTemp + byteCRC.length + byteEPK.length];

		intPos = byteTobyte(byteReturn, 0, byteMakeCRC);
		intPos = byteTobyte(byteReturn, intPos, byteCRC);
		intPos = byteTobyte(byteReturn, intPos, byteEPK);

		return byteReturn;
	}

	/**
	 * Byte append Byte
	 * 
	 * @param byteTarget
	 *            Target Byte
	 * @param intPos
	 *            Append Start Position
	 * @param byteSrc
	 *            Source Byte
	 * @return
	 */
	public static int byteTobyte(byte[] byteTarget, int intPos, byte[] byteSrc)
	{
		for (byte byteData : byteSrc)
		{
			byteTarget[intPos] = byteData;

			intPos++;
		}

		return intPos;
	}

	/**
	 * Make CRC
	 * 
	 * @param byteMakeData
	 * @return
	 */
	public static byte[] makeCRC(byte[] byteMakeData)
	{
		byte[] byteReturn = new byte[2];
		int stCrc = 0x0000;

		for (int i = 0; i < byteMakeData.length; i++)
		{
			stCrc = ((stCrc << 8) ^ crc16tab[((stCrc >> 8) ^ byteMakeData[i]) & 0x00FF]);
		}

		short stTest = (short) stCrc;

		BigInteger bigInt = BigInteger.valueOf(stTest);
		byteReturn = bigInt.toByteArray();

		return changeByteOrder(byteReturn, 2);
	}

	/**
	 * 단말기로 응답 할 서버 IP Bytes 만들기 
	 * 
	 * @param strServerIp
	 * @return
	 */
	public static byte[] serverIpToByte(String strServerIp)
	{
		byte[] byteReturn = new byte[4];

		String[] arrServerIp = strServerIp.split("\\.");

		for (int i = 0; i < 4; i++)
		{
			int intVal = Integer.parseInt(arrServerIp[i]);
			byteReturn[i] = (byte) intVal;
		}

		return byteReturn;
	}

	/**
	 * 응답코드 기준 Boolean 변환
	 * 
	 * @param strResult
	 * @return
	 */
	public static boolean responseStatus(String strResult)
	{
		boolean boolReturn = false;

		if (strResult.equals("S"))
		{
			boolReturn = true;
		}

		return boolReturn;
	}

	/**
	 * Convert CarType 
	 * 
	 * @param intCarType
	 * @return
	 */
	public static String convertCarType(int intCarType)
	{
		String strReturn = "";

		switch (intCarType)
		{
			case 0:
				strReturn = "11"; // 시내버스
				break;
			case 1:
				strReturn = "12"; // 농어촌버스
				break;
			case 2:
				strReturn = "13"; // 마을버스
				break;
			case 3:
				strReturn = "14"; // 시외버스
				break;
			case 4:
				strReturn = "15"; // 고속버스
				break;
			case 5:
				strReturn = "16"; // 전세버스
				break;
			case 6:
				strReturn = "17"; // 특수여객자동차
				break;
			case 7:
				strReturn = "21"; // 일반택시
				break;
			case 8:
				strReturn = "22"; // 개인택시
				break;
			case 9:
				strReturn = "31"; // 일반화물자동차
				break;
			case 10:
				strReturn = "32"; // 개별화물자동차
				break;
			case 11:
				strReturn = "41"; // 비사업용자동차
				break;
			case 12:
				strReturn = "51"; // 어린이통학버스 // 루프 규격 확인 //
				break;
			case 13:
				strReturn = "98"; // 기타1
				break;
			case 14:
				strReturn = "99"; // 기타2
				break;

			default:
				strReturn = "99"; // 기타2
				break;

		}

		return strReturn;
	}

	/**
	 * 타임스템프 기준 날짜, 트립 데이터 추출
	 * 
	 * @param longTime
	 * @param strStatus
	 * @return
	 */
	public static String convertTime(long longTime, String strStatus)
	{
		String strReturn = "";

		if (strStatus.equals("T"))
		{
			strReturn = new SimpleDateFormat("yyyyMMddHHmmss", currentLocale).format(longTime - lUtc);
		}
		else if (strStatus.equals("D"))
		{
			strReturn = new SimpleDateFormat("yyMMddHHmmssSS", currentLocale).format(longTime - lUtc);
			// strReturn = strReturn + "00";
		}

		return strReturn;
	}

	/**
	 * 분석 서버로 전달할 기본 파일명 생성 
	 * 
	 * @param filetypeKey
	 * @param carKey
	 * @param carnum
	 * @return
	 */
	public static String getFileName(String filetypeKey, String carKey, String carnum)
	{
		StringBuffer filename = new StringBuffer();

		filename.append(filetypeKey);
		filename.append("_");
		filename.append(carKey);
		filename.append("_");
		filename.append(carnum);
		filename.append("_");

		String dateStr = Util.getCurrentDateTime();
		int dateStrLen = dateStr.length();
		if (dateStrLen > 17)
		{
			filename.append(dateStr.substring(0, 16));
		}
		else if (dateStrLen < 17)
		{
			int moreLen = 17 - dateStrLen;
			filename.append(dateStr);
			for (int i = 0; i < moreLen; i++)
			{
				filename.append("0");
			}
		}
		else
		{
			filename.append(dateStr);
		}
		filename.append("_");
		return filename.toString();
	}

	/**
	 * 지정 패턴 기준 현재 시간 반환 
	 * 
	 * @return
	 */
	public static String getCurrentDateTime()
	{
		String pattern = "yyyyMMddHHmmssSS";
		return getCurrentDateTime(pattern);
	}

	/**
	 * 현재 시간 생성 반환
	 * 
	 * @param pattern
	 * @return
	 */
	public static String getCurrentDateTime(String pattern)
	{
		Date today = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(pattern, currentLocale);
		return formatter.format(today);
	}

	/**
	 * Integer to Binary 
	 * 
	 * @param intVal
	 * @param intLen
	 * @return
	 */
	public static String[] convertBinaryStringToArray(int intVal, int intLen)
	{
		String[] strArray = new String[intLen];

		String strBinary = Integer.toBinaryString(intVal);

		strBinary = String.format("%1$" + intLen + "s", strBinary).replace(" ", "0");

		for (int i = 0; i < intLen; i++)
		{
			strArray[i] = strBinary.substring(i, i + 1);
		}

		return strArray;
	}
	
	/**
	 * 배열 정렬 
	 * 
	 * @param arr
	 * @param start
	 * @param end
	 */
	public static void quick_Sort(int[] arr, int start, int end)
	{

		int left = start;
		int right = end;
		/* pivot을 중앙 값으로 셋팅 */
		int pivot = arr[(left + right) / 2];

		do
		{
			while (arr[left] < pivot)
			{
				left++;
			}
			while (arr[right] > pivot)
			{
				right--;
			}

			if (left <= right)
			{
				int temp = arr[left];
				arr[left] = arr[right];
				arr[right] = temp;
				left++;
				right--;
			}

		}
		while (left <= right);

		if (start < right)
		{
			quick_Sort(arr, start, right);

		}
		if (end > left)
		{
			quick_Sort(arr, left, end);
		}
	}
	
	/**
	 * 지정된 시간만큼 Sleep 
	 * 
	 * @param intSleep
	 */
	public static void sleepTime(int intSleep)
	{
		try
		{
			Thread.sleep(intSleep);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
