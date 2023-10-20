package kr.co.kti.lcs.vo;

/**
 * EVSS Event Data 처리 
 * 
 * @author yeski
 *
 */
public class LoopDtgEVSSBody extends LoopDtgFrame
{
	private long Time;
	
	/**
	 * Beacon : 0x00(0), rFid : 0x01(1) 구분 해당 구분에 따라서 Adress 데이터 처리 변경
	 */
	private int Type;
	
	/**
	 * Mac Address
	 */
	private byte[] Address;
	private int RSSI;
	private int Count;
	private int IO;
	
	/**
	 * 위도
	 */
	private long Latitude;
	
	/**
	 * 경도
	 */
	private long Longitude;
	
	/**
	 * 방위각
	 */
	private int Azimuth;
	
	/**
	 * 차속
	 */
	private int Speed;
	private byte[] REserved; // String 으로 해야 할지 //

	public long getTime()
	{
		return Time;
	}

	public void setTime(long time)
	{
		Time = time;
	}

	public int getType()
	{
		return Type;
	}

	public void setType(int type)
	{
		Type = type;
	}

	public byte[] getAddress()
	{
		return Address;
	}

	public void setAddress(byte[] address)
	{
		Address = address;
	}

	public int getRSSI()
	{
		return RSSI;
	}

	public void setRSSI(int rSSI)
	{
		RSSI = rSSI;
	}

	public int getCount()
	{
		return Count;
	}

	public void setCount(int count)
	{
		Count = count;
	}

	public int getIO()
	{
		return IO;
	}

	public void setIO(int iO)
	{
		IO = iO;
	}

	public long getLatitude()
	{
		return Latitude;
	}

	public void setLatitude(long latitude)
	{
		Latitude = latitude;
	}

	public long getLongitude()
	{
		return Longitude;
	}

	public void setLongitude(long longitude)
	{
		Longitude = longitude;
	}

	public int getAzimuth()
	{
		return Azimuth;
	}

	public void setAzimuth(int azimuth)
	{
		Azimuth = azimuth;
	}

	public int getSpeed()
	{
		return Speed;
	}

	public void setSpeed(int speed)
	{
		Speed = speed;
	}

	public byte[] getREserved()
	{
		return REserved;
	}

	public void setREserved(byte[] rEserved)
	{
		REserved = rEserved;
	}

}
