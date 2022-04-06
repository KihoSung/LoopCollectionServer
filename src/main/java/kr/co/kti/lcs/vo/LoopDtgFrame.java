package kr.co.kti.lcs.vo;

import java.io.UnsupportedEncodingException;

import kr.co.kti.lcs.common.Const;
import kr.co.kti.lcs.common.Util;

/**
 * Loop DTG 통신 Frame Protocol 
 * @author yeski
 *
 */
public class LoopDtgFrame
{
	/**
	 * 패킷 시작
	 */
	private int SPK;
	
	/**
	 * 장치 ID
	 */
	private int DeviceID;
	
	/**
	 * 회선번호
	 */
	private int IdentityNo;
	
	/**
	 * Data Command
	 */
	private String Command;		// Little Endian
	
	/**
	 * Data 전송 시작 번지
	 */
	private int Index;
	
	/**
	 * Data 전송 종료 번지
	 */
	private int Count;
	
	/**
	 * 데이터 Count
	 */
	private long Length;
	// data 들어갈 곳

	/**
	 * 패킷 CRC
	 */
	private int CRC;
	
	/**
	 * 패킷 끝
	 */
	private int EPK;
	
	public int doParseStart(byte[] frameData) throws UnsupportedEncodingException
	{
		int intPos = 0;
		
		setSPK(Util.getShort(frameData, intPos, Const.LEN_LOOP_SPK, "L"));
		intPos += Const.LEN_LOOP_SPK;
		setDeviceID(Util.getShort(frameData, intPos, Const.LEN_LOOP_DEVICE_ID, "L"));
		intPos += Const.LEN_LOOP_DEVICE_ID;
		setIdentityNo(Util.getInteger(frameData, intPos, Const.LEN_LOOP_IDENTITY_NO, "L"));
		intPos += Const.LEN_LOOP_IDENTITY_NO;
		setCommand(Util.getString(frameData, intPos, Const.LEN_LOOP_COMMAND, "L"));
		intPos += Const.LEN_LOOP_COMMAND;
		setIndex(Util.getShort(frameData, intPos, Const.LEN_LOOP_INDEX, "L"));
		intPos += Const.LEN_LOOP_INDEX;
		setCount(Util.getShort(frameData, intPos, Const.LEN_LOOP_COUNT, "L"));
		intPos += Const.LEN_LOOP_COUNT;
		setLength(Util.getInteger(frameData, intPos, Const.LEN_LOOP_DATA_LENGTH, "L"));
		intPos += Const.LEN_LOOP_DATA_LENGTH;
		
		return intPos;
	}
	
	public int doParseEnd(byte[] frameData, int intPos)
	{
		setCRC(Util.getShort(frameData, intPos, Const.LEN_LOOP_CRC, "L"));
		intPos += Const.LEN_LOOP_CRC;
		setEPK(Util.getShort(frameData, intPos, Const.LEN_LOOP_EPK, "L"));
		intPos += Const.LEN_LOOP_EPK;
		
		return intPos;
	}

	public int getSPK()
	{
		return SPK;
	}

	public void setSPK(int sPK)
	{
		SPK = sPK;
	}

	public int getDeviceID()
	{
		return DeviceID;
	}

	public void setDeviceID(int deviceID)
	{
		DeviceID = deviceID;
	}

	public int getIdentityNo()
	{
		return IdentityNo;
	}

	public void setIdentityNo(int identityNo)
	{
		IdentityNo = identityNo;
	}

	public String getCommand()
	{
		return Command;
	}

	public void setCommand(String command)
	{
		Command = command;
	}

	public int getIndex()
	{
		return Index;
	}

	public void setIndex(int index)
	{
		Index = index;
	}

	public int getCount()
	{
		return Count;
	}

	public void setCount(int count)
	{
		Count = count;
	}

	public long getLength()
	{
		return Length;
	}

	public void setLength(long length)
	{
		Length = length;
	}

	public int getCRC()
	{
		return CRC;
	}

	public void setCRC(int cRC)
	{
		CRC = cRC;
	}

	public int getEPK()
	{
		return EPK;
	}

	public void setEPK(int ePK)
	{
		EPK = ePK;
	}
}
