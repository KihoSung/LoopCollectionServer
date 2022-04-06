package kr.co.kti.lcs.vo;

import java.io.UnsupportedEncodingException;

import kr.co.kti.lcs.common.Const;
import kr.co.kti.lcs.common.Util;

/**
 * INFO Vo
 * 
 * @author yeski
 *
 */
public class LoopDtgINFOBody extends LoopDtgFrame
{
	/**
	 * INFO Data Bytes
	 */
	private byte[] infoData;

	private byte[] Reserved1;
	private byte[] Reserved2;
	
	/**
	 * 운전자명
	 */
	private String DriverName;
	
	/**
	 * 운전자코드
	 */
	private String DriverCode;

	/**
	 * 차량유형
	 */
	private int CarType;
	
	/**
	 * 차량 등록 번호 (13허1234)
	 */
	private String CarRegNo;
	
	/**
	 * 차량 등록 번호 Bytes
	 */
	private byte[] BCarRegNo;

	/**
	 * 차대번호
	 */
	private String CarIdNo;
	
	/**
	 * 회사명
	 */
	private String OfficeName;
	
	/**
	 * 사업자번호
	 */
	private String BizNo;
	
	/**
	 * 형식승인번호
	 */
	private String TypeApprovalNo;
	
	/**
	 * 시리얼 번호
	 */
	private String SerialNo;
	
	/**
	 * 모델 번호
	 */
	private String ModelNo;
	
	/**
	 * KFactor
	 */
	private int KFactor;
	
	/**
	 * RPMFactor
	 */
	private int RPMFactor;
	private byte[] Reserved3;
	
	/**
	 * Firmware 버전
	 */
	private byte[] FirmwareVer;
	private byte[] Reserved4;
	private byte[] Reserved5;
	private byte[] Reserved6;
	private byte[] Reserved7;
	private byte[] Reserved8;
	private byte[] Reserved9;
	private byte[] Reserved10;

	/**
	 * 트립시퀀스
	 */
	private int TripSequence;
	
	/**
	 * 운행데이터 운행 코드 (S,E,D)
	 */
	private String DrivingInfoCode;
	
	/**
	 * Packet Count
	 */
	private int PacketCount = 0;
	
	/**
	 * 트립 Count
	 */
	private int TripCount = 1;

	public int getTripCount()
	{
		return TripCount;
	}

	public void setTripCount(int tripCount)
	{
		TripCount = tripCount;
	}

	public String getSerialNo()
	{
		return SerialNo;
	}

	public void setSerialNo(String serialNo)
	{
		SerialNo = serialNo;
	}

	public int getPacketCount()
	{
		return PacketCount;
	}

	public void setPacketCount(int packetCount)
	{
		PacketCount = packetCount;
	}

	public byte[] getBCarRegNo()
	{
		return BCarRegNo;
	}

	public void setBCarRegNo(byte[] bCarRegNo)
	{
		BCarRegNo = bCarRegNo;
	}

	public String getDrivingInfoCode()
	{
		return DrivingInfoCode;
	}

	public void setDrivingInfoCode(String drivingInfoCode)
	{
		DrivingInfoCode = drivingInfoCode;
	}

	public int getTripSequence()
	{
		return TripSequence;
	}

	public void setTripSequence(int tripSequence)
	{
		TripSequence = tripSequence;
	}

	public LoopDtgINFOBody(byte[] iData)
	{
		this.infoData = iData;
	}
	
	public LoopDtgINFOBody()
	{
		
	}

	private String makeCarRegNo(byte[] bData)
	{
		StringBuilder sb = new StringBuilder();

		if ((int) bData[0] < 49) // 이게 문제 일반 승용은 안하려나... // 우선 검증
		{
			sb.append(Util.strArrayArea[(int) bData[0]]);
		}
		else
		{
			sb.append((int) bData[0]);
		}

		sb.append((int) bData[1]);
		sb.append((int) bData[2]);
		sb.append(Util.strArrayHangul[(int) bData[3]]);
		sb.append((int) bData[4]);
		sb.append((int) bData[5]);
		sb.append((int) bData[6]);
		sb.append((int) bData[7]);

		return sb.toString();
	}

	public void doParse() throws UnsupportedEncodingException
	{
		int intPos = 0;

		intPos = doParseStart(this.infoData);

		setReserved1(Util.getBytes(this.infoData, intPos, Const.LEN_LOOP_HD_RESERVED1));
		intPos += Const.LEN_LOOP_HD_RESERVED1;
		setReserved2(Util.getBytes(this.infoData, intPos, Const.LEN_LOOP_HD_RESERVED2));
		intPos += Const.LEN_LOOP_HD_RESERVED2;
		setDriverName(Util.getString(this.infoData, intPos, Const.LEN_LOOP_HD_DRIVER_NAME, "B").trim());
		intPos += Const.LEN_LOOP_HD_DRIVER_NAME;
		setDriverCode(Util.getString(this.infoData, intPos, Const.LEN_LOOP_HD_DRIVER_CODE, "B").trim());
		intPos += Const.LEN_LOOP_HD_DRIVER_CODE;
		setCarType((int) Util.getByte(this.infoData, intPos, Const.LEN_LOOP_HD_CAR_TYPE, "L"));
		intPos += Const.LEN_LOOP_HD_CAR_TYPE;
		String strCarRegNo = this.makeCarRegNo(Util.getBytes(this.infoData, intPos, Const.LEN_LOOP_HD_CAR_REG_NO));
		setCarRegNo(strCarRegNo);
		intPos += Const.LEN_LOOP_HD_CAR_REG_NO;
		setCarIdNo(Util.getString(this.infoData, intPos, Const.LEN_LOOP_HD_CAR_ID_NO, "B").trim());
		intPos += Const.LEN_LOOP_HD_CAR_ID_NO;
		setOfficeName(Util.getString(this.infoData, intPos, Const.LEN_LOOP_HD_OFFICE_NAME, "B").trim());
		intPos += Const.LEN_LOOP_HD_OFFICE_NAME;
		setBizNo(Util.getString(this.infoData, intPos, Const.LEN_LOOP_HD_BIZ_NO, "B").trim());
		intPos += Const.LEN_LOOP_HD_BIZ_NO;
		setTypeApprovalNo(Util.getString(this.infoData, intPos, Const.LEN_LOOP_HD_TYPE_APPROVAL_NO, "B").trim());
		intPos += Const.LEN_LOOP_HD_TYPE_APPROVAL_NO;
		setSerialNo(Util.getString(this.infoData, intPos, Const.LEN_LOOP_HD_SERIAL_NO, "B").trim());
		intPos += Const.LEN_LOOP_HD_SERIAL_NO;
		setModelNo(Util.getString(this.infoData, intPos, Const.LEN_LOOP_HD_MODEL_NO, "B").trim());
		intPos += Const.LEN_LOOP_HD_MODEL_NO;
		setKFactor(Util.getShort(this.infoData, intPos, Const.LEN_LOOP_HD_K_FACTOR, "L"));
		intPos += Const.LEN_LOOP_HD_K_FACTOR;
		setRPMFactor(Util.getShort(this.infoData, intPos, Const.LEN_LOOP_HD_RPM_FACTOR, "L"));
		intPos += Const.LEN_LOOP_HD_RPM_FACTOR;
		setReserved3(Util.getBytes(this.infoData, intPos, Const.LEN_LOOP_HD_RESERVED3));
		intPos += Const.LEN_LOOP_HD_RESERVED3;
		setFirmwareVer(Util.getBytes(this.infoData, intPos, Const.LEN_LOOP_HD_FIRMWARE_VER));
		intPos += Const.LEN_LOOP_HD_FIRMWARE_VER;
		setReserved4(Util.getBytes(this.infoData, intPos, Const.LEN_LOOP_HD_RESERVED4));
		intPos += Const.LEN_LOOP_HD_RESERVED4;
		setReserved5(Util.getBytes(this.infoData, intPos, Const.LEN_LOOP_HD_RESERVED5));
		intPos += Const.LEN_LOOP_HD_RESERVED5;
		setReserved6(Util.getBytes(this.infoData, intPos, Const.LEN_LOOP_HD_RESERVED6));
		intPos += Const.LEN_LOOP_HD_RESERVED6;
		setReserved7(Util.getBytes(this.infoData, intPos, Const.LEN_LOOP_HD_RESERVED7));
		intPos += Const.LEN_LOOP_HD_RESERVED7;
		setReserved8(Util.getBytes(this.infoData, intPos, Const.LEN_LOOP_HD_RESERVED8));
		intPos += Const.LEN_LOOP_HD_RESERVED8;
		setReserved9(Util.getBytes(this.infoData, intPos, Const.LEN_LOOP_HD_RESERVED9));
		intPos += Const.LEN_LOOP_HD_RESERVED9;
		setReserved10(Util.getBytes(this.infoData, intPos, Const.LEN_LOOP_HD_RESERVED10));
		intPos += Const.LEN_LOOP_HD_RESERVED10;

		intPos = doParseEnd(this.infoData, intPos);

	}

	public byte[] getReserved1()
	{
		return Reserved1;
	}

	public void setReserved1(byte[] reserved1)
	{
		Reserved1 = reserved1;
	}

	public byte[] getReserved2()
	{
		return Reserved2;
	}

	public void setReserved2(byte[] reserved2)
	{
		Reserved2 = reserved2;
	}

	public String getDriverName()
	{
		return DriverName;
	}

	public void setDriverName(String driverName)
	{
		DriverName = driverName;
	}

	public String getDriverCode()
	{
		return DriverCode;
	}

	public void setDriverCode(String driverCode)
	{
		DriverCode = driverCode;
	}

	public int getCarType()
	{
		return CarType;
	}

	public void setCarType(int carType)
	{
		CarType = carType;
	}

	public String getCarRegNo()
	{
		return CarRegNo;
	}

	public void setCarRegNo(String carRegNo)
	{
		CarRegNo = carRegNo;
	}

	public String getCarIdNo()
	{
		return CarIdNo;
	}

	public void setCarIdNo(String carIdNo)
	{
		CarIdNo = carIdNo;
	}

	public String getOfficeName()
	{
		return OfficeName;
	}

	public void setOfficeName(String officeName)
	{
		OfficeName = officeName;
	}

	public String getBizNo()
	{
		return BizNo;
	}

	public void setBizNo(String bizNo)
	{
		BizNo = bizNo;
	}

	public String getTypeApprovalNo()
	{
		return TypeApprovalNo;
	}

	public void setTypeApprovalNo(String typeApprovalNo)
	{
		TypeApprovalNo = typeApprovalNo;
	}

	public String getModelNo()
	{
		return ModelNo;
	}

	public void setModelNo(String modelNo)
	{
		ModelNo = modelNo;
	}

	public int getKFactor()
	{
		return KFactor;
	}

	public void setKFactor(int kFactor)
	{
		KFactor = kFactor;
	}

	public int getRPMFactor()
	{
		return RPMFactor;
	}

	public void setRPMFactor(int rPMFactor)
	{
		RPMFactor = rPMFactor;
	}

	public byte[] getReserved3()
	{
		return Reserved3;
	}

	public void setReserved3(byte[] reserved3)
	{
		Reserved3 = reserved3;
	}

	public byte[] getFirmwareVer()
	{
		return FirmwareVer;
	}

	public void setFirmwareVer(byte[] firmwareVer)
	{
		FirmwareVer = firmwareVer;
	}

	public byte[] getReserved4()
	{
		return Reserved4;
	}

	public void setReserved4(byte[] reserved4)
	{
		Reserved4 = reserved4;
	}

	public byte[] getReserved5()
	{
		return Reserved5;
	}

	public void setReserved5(byte[] reserved5)
	{
		Reserved5 = reserved5;
	}

	public byte[] getReserved6()
	{
		return Reserved6;
	}

	public void setReserved6(byte[] reserved6)
	{
		Reserved6 = reserved6;
	}

	public byte[] getReserved7()
	{
		return Reserved7;
	}

	public void setReserved7(byte[] reserved7)
	{
		Reserved7 = reserved7;
	}

	public byte[] getReserved8()
	{
		return Reserved8;
	}

	public void setReserved8(byte[] reserved8)
	{
		Reserved8 = reserved8;
	}

	public byte[] getReserved9()
	{
		return Reserved9;
	}

	public void setReserved9(byte[] reserved9)
	{
		Reserved9 = reserved9;
	}

	public byte[] getReserved10()
	{
		return Reserved10;
	}

	public void setReserved10(byte[] reserved10)
	{
		Reserved10 = reserved10;
	}

}
