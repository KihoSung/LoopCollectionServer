package kr.co.kti.lcs.vo;

/**
 * DTSS Data (DTG to Server)
 * 
 * @author yeski
 *
 */
public class LoopDtgDTSSBody extends LoopDtgFrame
{
	/**
	 * 운행시각
	 */
	private int Time;
	
	/**
	 * 차속
	 */
	private int Speed;
	
	/**
	 * 차속 소숫점 이하
	 */
	private int SpeedFix;
	
	/**
	 * RPM
	 */
	private int RPM;
	
	/**
	 * 차량 신호 (KeyOn, Brake등)
	 */
	private int Signal;
	
	/**
	 * 차량/단말기 상태 (GPS Fault 등)
	 */
	private int Status;
	
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
	 * GPS 상태 (수신불량, 신호약함 등)
	 */
	private int GPSStatus;
	
	/**
	 * GPS 속도
	 */
	private int GPSSpeed;
	
	/**
	 * 가속도x
	 */
	private int AccVx;
	
	/**
	 * 가속도y
	 */
	private int AccVy;
	
	/**
	 * 트립카운트 (해당 운행데이터의 트립카운트)
	 */
	private int TripCount;
	
	/**
	 * 운전자번호
	 */
	private int DriverNo;
	
	/**
	 * RSSI
	 */
	private int RSSI;
	
	/**
	 * Isg 상태
	 */
	private int IsgStatus;
	
	/**
	 * 주행거리
	 */
	private float Distance;
	
	/**
	 * 일주행거리
	 */
	private float DayDistance;
	
	/**
	 * 총주행거리
	 */
	private float TotDistance;
	
	/**
	 * 연료소모량
	 */
	private float FuelConsumption;
	
	/**
	 * 일 연료소모량
	 */
	private float DayFuelConsumption;
	
	/**
	 * 총 연료소모량
	 */
	private float TotFuelConsumption;
	
	/**
	 * 배터리 전압
	 */
	private int BatteryVolt;
	
	/**
	 * AEBS
	 */
	private int AEBS;
	private int LDWS1;
	private int Temp1;
	private int Temp2;

	/**
	 * Brake 신호
	 */
	private int Break;
	
	/**
	 * 시동 On/Off 신호
	 */
	private int KeyOn;
	
	/**
	 * VDS 상태
	 */
	private String VDRStatus;
	
	private int ErrCount = 0;

	public int getSpeedFix()
	{
		return SpeedFix;
	}

	public void setSpeedFix(int speedFix)
	{
		SpeedFix = speedFix;
	}

	public int getErrCount()
	{
		return ErrCount;
	}

	public void setErrCount(int errCount)
	{
		ErrCount = errCount;
	}

	public String getVDRStatus()
	{
		return VDRStatus;
	}

	public void setVDRStatus(String vDRStatus)
	{
		VDRStatus = vDRStatus;
	}

	public int getBreak()
	{
		return Break;
	}

	public void setBreak(int break1)
	{
		Break = break1;
	}

	public int getKeyOn()
	{
		return KeyOn;
	}

	public void setKeyOn(int keyOn)
	{
		KeyOn = keyOn;
	}

	public int getTime()
	{
		return Time;
	}

	public void setTime(int time)
	{
		Time = time;
	}

	public int getSpeed()
	{
		return Speed;
	}

	public void setSpeed(int speed)
	{
		Speed = speed;
	}

	public int getRPM()
	{
		return RPM;
	}

	public void setRPM(int rPM)
	{
		RPM = rPM;
	}

	public int getSignal()
	{
		return Signal;
	}

	public void setSignal(int signal)
	{
		Signal = signal;
	}

	public int getStatus()
	{
		return Status;
	}

	public void setStatus(int status)
	{
		Status = status;
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

	public int getGPSStatus()
	{
		return GPSStatus;
	}

	public void setGPSStatus(int gPSStatus)
	{
		GPSStatus = gPSStatus;
	}

	public int getGPSSpeed()
	{
		return GPSSpeed;
	}

	public void setGPSSpeed(int gPSSpeed)
	{
		GPSSpeed = gPSSpeed;
	}

	public int getAccVx()
	{
		return AccVx;
	}

	public void setAccVx(int accVx)
	{
		AccVx = accVx;
	}

	public int getAccVy()
	{
		return AccVy;
	}

	public void setAccVy(int accVy)
	{
		AccVy = accVy;
	}

	public int getTripCount()
	{
		return TripCount;
	}

	public void setTripCount(int tripCount)
	{
		TripCount = tripCount;
	}

	public int getDriverNo()
	{
		return DriverNo;
	}

	public void setDriverNo(int driverNo)
	{
		DriverNo = driverNo;
	}

	public int getRSSI()
	{
		return RSSI;
	}

	public void setRSSI(int rSSI)
	{
		RSSI = rSSI;
	}

	public int getIsgStatus()
	{
		return IsgStatus;
	}

	public void setIsgStatus(int isgStatus)
	{
		IsgStatus = isgStatus;
	}

	public float getDistance()
	{
		return Distance;
	}

	public void setDistance(float distance)
	{
		Distance = distance;
	}

	public float getDayDistance()
	{
		return DayDistance;
	}

	public void setDayDistance(float dayDistance)
	{
		DayDistance = dayDistance;
	}

	public float getTotDistance()
	{
		return TotDistance;
	}

	public void setTotDistance(float totDistance)
	{
		TotDistance = totDistance;
	}

	public float getFuelConsumption()
	{
		return FuelConsumption;
	}

	public void setFuelConsumption(float fuelConsumption)
	{
		FuelConsumption = fuelConsumption;
	}

	public float getDayFuelConsumption()
	{
		return DayFuelConsumption;
	}

	public void setDayFuelConsumption(float dayFuelConsumption)
	{
		DayFuelConsumption = dayFuelConsumption;
	}

	public float getTotFuelConsumption()
	{
		return TotFuelConsumption;
	}

	public void setTotFuelConsumption(float totFuelConsumption)
	{
		TotFuelConsumption = totFuelConsumption;
	}

	public int getBatteryVolt()
	{
		return BatteryVolt;
	}

	public void setBatteryVolt(int batteryVolt)
	{
		BatteryVolt = batteryVolt;
	}

	public int getAEBS()
	{
		return AEBS;
	}

	public void setAEBS(int aEBS)
	{
		AEBS = aEBS;
	}

	public int getLDWS1()
	{
		return LDWS1;
	}

	public void setLDWS1(int lDWS1)
	{
		LDWS1 = lDWS1;
	}

	public int getTemp1()
	{
		return Temp1;
	}

	public void setTemp1(int temp1)
	{
		Temp1 = temp1;
	}

	public int getTemp2()
	{
		return Temp2;
	}

	public void setTemp2(int temp2)
	{
		Temp2 = temp2;
	}

}
