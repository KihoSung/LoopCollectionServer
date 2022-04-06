package kr.co.kti.lcs.service;

import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.LoggerFactory;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import kr.co.kti.lcs.common.Const;
import kr.co.kti.lcs.common.DBConst;
import kr.co.kti.lcs.common.Util;
import kr.co.kti.lcs.vo.LoopDtgDTSSBody;
import kr.co.kti.lcs.vo.LoopDtgINFOBody;

/**
 * DTSS 분석 Class
 * 
 * @author yeski
 *
 */
public class LoopDtgDTSSService
{
	/**
	 * Define Logging Value
	 */
	private org.slf4j.Logger log = null;

	/**
	 * Define Vertx
	 */
	private Vertx vertx;

	/**
	 * 전체 DTSS 데이터 버퍼
	 */
	private Buffer dtssBuffer;

	/**
	 * 전체 DTSS 데이터 Bytes
	 */
	private byte[] bytesDtss;
	
	/**
	 * 운행 데이터 Bytes
	 */
	private byte[] bytesDriveData;

	/**
	 * DTSS Data 시작번지
	 */
	private int intIndex;
	
	/**
	 * DTSS Data 종료 번지
	 */
	private int intCount;
	
	/**
	 * 데이터 Byte 수
	 */
	private int intLength;
	
	/**
	 * DTSS Data Set Count
	 */
	private int intDataCount; // 
	
	/**
	 * DTSS Data CRC
	 */
	private byte[] bytesCrc; // 
	
	/**
	 * DTSS Data Make CRC
	 */
	private byte[] bytesMakeCrc; // 

	/**
	 * 운행데이터 시작 번지
	 */
	private int intSBuff = Const.LEN_LOOP_SPK + Const.LEN_LOOP_DEVICE_ID + Const.LEN_LOOP_IDENTITY_NO + Const.LEN_LOOP_COMMAND + Const.LEN_LOOP_INDEX + Const.LEN_LOOP_COUNT;

	/**
	 * 운행데이터 Array<LoopDtgDTSSBody>
	 */
	private ArrayList<LoopDtgDTSSBody> alLoopDtgDTSSBody = new ArrayList<LoopDtgDTSSBody>();
	
	/**
	 * 00시00분00초 기준 트립 나누기 기준 두번째 운행데이터 Array<LoopDtgDTSSBody>
	 */
	private ArrayList<LoopDtgDTSSBody> alLoopDtgDTSSBodyTwo = new ArrayList<LoopDtgDTSSBody>();

	/**
	 * DTSS VO
	 */
	private LoopDtgINFOBody loopDtgINFOBody;
	
	/**
	 * 00시00분00초 기준 트립 나누기 기준 두번째 DTSS VO
	 */
	private LoopDtgINFOBody loopDtgINFOBodyTwo;

	/**
	 * 00시00분00초 기준 트립이 나눠지면서 발생하는 두번째 데이터 사이트 반환
	 * 
	 * @return
	 */
	public int getDataTwoSize()
	{
		return this.alLoopDtgDTSSBodyTwo.size();
	}

	/**
	 * 운행데이터 Array 반환 (현재 사용하지 않음)
	 * 
	 * @return
	 */
	public ArrayList<LoopDtgDTSSBody> getAlLoopDtgDTSSBody()
	{
		return alLoopDtgDTSSBody;
	}

	/**
	 * Instance DTSS Service 
	 * 
	 * @param vertx
	 * @param joInfo
	 */
	public LoopDtgDTSSService(Vertx vertx, JsonObject joInfo)
	{
		log = LoggerFactory.getLogger("process.DrivingDataProcWorker");

		this.vertx = vertx;
		this.loopDtgINFOBody = new LoopDtgINFOBody();

		this.setLoopDtgInfo(joInfo);

		Util.writeTraceLog(vertx, "[" + LoopDtgDTSSService.class.getName() + "] Create instance of LoopDtgDTSSService");
	}

	/**
	 * DB에서 불러온 JsonData 형식의 INFO Data 설정
	 * 
	 * @param joInfo
	 */
	private void setLoopDtgInfo(JsonObject joInfo)
	{
		String strIdentityNo = joInfo.getString("DVC_ID");
		int intIdentityNo = Integer.parseInt(strIdentityNo.substring(1));
		log.debug("====>" + intIdentityNo + "<=======");

		this.loopDtgINFOBody.setIdentityNo(intIdentityNo);
		this.loopDtgINFOBody.setDriverCode(joInfo.getString("DRIVER_CODE"));
		this.loopDtgINFOBody.setModelNo(joInfo.getString("MODEL_NO"));
		this.loopDtgINFOBody.setCarIdNo(joInfo.getString("CAR_ID_NO"));
		this.loopDtgINFOBody.setCarRegNo(joInfo.getString("CAR_REG_NO"));
		this.loopDtgINFOBody.setBizNo(joInfo.getString("BIZ_NO"));
		this.loopDtgINFOBody.setCarType(joInfo.getInteger("CAR_TYPE"));
		this.loopDtgINFOBody.setTripSequence(joInfo.getInteger("TRIP_SEQ"));
		this.loopDtgINFOBody.setDrivingInfoCode(joInfo.getString("DRIVING_INFO_CODE"));
		this.loopDtgINFOBody.setTripCount(joInfo.getInteger("TRIP_COUNT"));
	}

	/**
	 * 단말에서 Socket으로 받은 Buffer Data 설정 
	 * 
	 * @param buf
	 */
	public void setBuffer(Buffer buf)
	{
		this.dtssBuffer = buf;
		this.bytesDtss = this.dtssBuffer.getBytes();

		Util.writeTraceLog(this.vertx, "[" + LoopDtgDTSSService.class.getName() + "] Set DTSS Data from Buffer");
	}

	/**
	 * 차량번호 반환 
	 * 
	 * @return
	 */
	public String getCarRegNo()
	{
		return this.loopDtgINFOBody.getCarRegNo();
	}

	/**
	 * 운행데이터 분석
	 * 
	 * @return
	 */
	public boolean doParse()
	{
		Util.writeTraceLog(this.vertx, "[" + LoopDtgDTSSService.class.getName() + "] Parsing DTSS Data");

		boolean boolReturn = false;

		boolReturn = this.validateData();

		if (boolReturn)
		{
			this.setDataArray();

			this.validateDriveData(); // 운행데이터 검증 //
		}

		return boolReturn;
	}

	/**
	 * 운행데이터 Header 확인
	 * 
	 * @return
	 */
	private boolean validateData()
	{
		Util.writeTraceLog(this.vertx, "[" + LoopDtgDTSSService.class.getName() + "] Validate DTSS Data");

		log.debug("length:" + this.bytesDtss.length);
		int intDtssLength = this.bytesDtss.length;

		this.intLength = Util.getInteger(this.bytesDtss, this.intSBuff, Const.LEN_LOOP_DATA_LENGTH, "L"); // Driving Data 길이
		log.debug("this.intLength:" + this.intLength);
		if (this.intLength < Const.LEN_LOOP_DB_TOTAL)
		{
			return false;
		}

		// this.intCount = Util.getShort(this.bytesDtss, this.intSBuff - Const.LEN_LOOP_COUNT + 1, Const.LEN_LOOP_COUNT, "L");
		// if (this.intCount < 0)
		// {
		// return false;
		// }
		//
		// this.intIndex = Util.getShort(this.bytesDtss, (this.intSBuff - (Const.LEN_LOOP_INDEX + Const.LEN_LOOP_COUNT)), Const.LEN_LOOP_INDEX, "L");
		// if (this.intIndex < 0)
		// {
		// return false;
		// }

		this.intDataCount = this.intLength / Const.LEN_LOOP_DB_TOTAL;
		this.bytesDriveData = Util.getBytes(this.bytesDtss, (this.intSBuff + Const.LEN_LOOP_DATA_LENGTH), this.intLength); // Driving Data
		if (this.bytesDriveData.length < Const.LEN_LOOP_DB_TOTAL)
		{
			return false;
		}

		this.bytesCrc = Util.getBytes(this.bytesDtss, (this.intSBuff + Const.LEN_LOOP_DATA_LENGTH + this.intLength), Const.LEN_LOOP_CRC); // Crc
		this.bytesMakeCrc = Util.makeCRC(Util.getBytes(this.bytesDtss, 0, (this.bytesDtss.length - 4))); // Make Crc

		log.debug("==>bytesCrc:" + Util.byteArrayToHex(bytesCrc));
		log.debug("==>bytesMakeCrc:" + Util.byteArrayToHex(bytesMakeCrc));

		if (this.bytesCrc == null || !Arrays.equals(this.bytesCrc, this.bytesMakeCrc))
		{
			return false;
		}

		return true;
	}

	/**
	 * 운행데이터 Body 검증
	 * 
	 * @return
	 */
	private boolean validateDriveData()
	{
		Util.writeTraceLog(this.vertx, "[" + LoopDtgDTSSService.class.getName() + "] Validing DTSS Data");

		boolean boolReturn = false;

		this.validateTime(); // 동일 시간 및 역순 시간 확인 //
		this.validateFactor(); // Factor Data 확인 //

		this.setDrivingInfoCode(); // 운행데이터 'SDE' 설정 //

		// 두번째 운행데이터 가 존재 할 경우 두번째 운행데이터 'SDE' 설정 //
		if (this.alLoopDtgDTSSBodyTwo.size() > 0)
		{
			Util.sleepTime(100);

			// 이전 운행 해더가를 설정 //
			this.loopDtgINFOBodyTwo = this.loopDtgINFOBody;

			this.setDrivingInfoCodeTwo();
		}

		return boolReturn;
	}

	/**
	 * 운행데이터 기준 운행코드 설정
	 */
	private void setDrivingInfoCode()
	{
		Util.writeTraceLog(this.vertx, "[" + LoopDtgDTSSService.class.getName() + "] Driving Info Code(SED) Parsing in DTSS Data");

		LoopDtgDTSSBody firstLoopDtgDTSSBody = this.alLoopDtgDTSSBody.get(0);

		String strFirstDrivingCode = "";
		int intTripCount = firstLoopDtgDTSSBody.getTripCount();

		if (this.loopDtgINFOBody.getTripCount() != intTripCount)
		{
			// 현재 TripSeq를 설정하고, DB에 저장 한다
			this.loopDtgINFOBody.setTripCount(firstLoopDtgDTSSBody.getTripCount());
			this.loopDtgINFOBody.setTripSequence(firstLoopDtgDTSSBody.getTime());
			strFirstDrivingCode = "S";

			// DB 저장 호출 만들기
			JsonObject joTripInfo = new JsonObject();
			joTripInfo.put("TRIP_COUNT", this.loopDtgINFOBody.getTripCount());
			joTripInfo.put("TRIP_SEQ", this.loopDtgINFOBody.getTripSequence());
			joTripInfo.put("DRIVING_INFO_CODE", strFirstDrivingCode);

			Util.updateDBInfo(vertx, "0" + this.loopDtgINFOBody.getIdentityNo(), DBConst.COMMAND_SET_TRIPINFO, joTripInfo);
		}
		else
		{
			// Trip Count가 같을 경우
			// 운행데이터와 트립시퀀스 날짜가 다르면 새로운 트립시퀀스 업뎃
			String strInfoTripDate = Util.getTripSeqToDate(this.loopDtgINFOBody.getTripSequence());
			String strDTSSDate = Util.getTripSeqToDate(firstLoopDtgDTSSBody.getTime());

			if (!strInfoTripDate.equals(strDTSSDate))
			{
				this.loopDtgINFOBody.setTripSequence(firstLoopDtgDTSSBody.getTime());
				strFirstDrivingCode = "S";

				// DB 저장 호출 만들기
				JsonObject joTripInfo = new JsonObject();
				joTripInfo.put("TRIP_COUNT", this.loopDtgINFOBody.getTripCount());
				joTripInfo.put("TRIP_SEQ", this.loopDtgINFOBody.getTripSequence());
				joTripInfo.put("DRIVING_INFO_CODE", strFirstDrivingCode);

				Util.updateDBInfo(vertx, "0" + this.loopDtgINFOBody.getIdentityNo(), DBConst.COMMAND_SET_TRIPINFO, joTripInfo);
			}
			else
			{
				strFirstDrivingCode = "-";
			}

		}

		LoopDtgDTSSBody lastLoopDtgDTSSBody = this.alLoopDtgDTSSBody.get(this.alLoopDtgDTSSBody.size() - 1);

		int intKeyOn = lastLoopDtgDTSSBody.getKeyOn();
		String strLastDrivingCode = "";

		switch (intKeyOn)
		{
			case 0:
				strLastDrivingCode = "E";

				break;
			case 1:
			default:

				strLastDrivingCode = "-";

				break;
		}

		this.loopDtgINFOBody.setDrivingInfoCode(strFirstDrivingCode + strLastDrivingCode);
	}

	/**
	 * 00시00분00초 기준 트립이 나눠지며 발생한 두번째 운행데이터 기준 운행코드 설정
	 */
	private void setDrivingInfoCodeTwo()
	{
		Util.writeTraceLog(this.vertx, "[" + LoopDtgDTSSService.class.getName() + "] Driving Info Code(SED) Parsing in DTSS Data");

		LoopDtgDTSSBody firstLoopDtgDTSSBody = this.alLoopDtgDTSSBodyTwo.get(0);

		String strFirstDrivingCode = "";
		int intTripCount = firstLoopDtgDTSSBody.getTripCount();

		if (this.loopDtgINFOBodyTwo.getTripCount() != intTripCount)
		{
			// 현재 TripSeq를 설정하고, DB에 저장 한다
			this.loopDtgINFOBodyTwo.setTripCount(firstLoopDtgDTSSBody.getTripCount());
			this.loopDtgINFOBodyTwo.setTripSequence(firstLoopDtgDTSSBody.getTime());
			strFirstDrivingCode = "S";

			// DB 저장 호출 만들기
			JsonObject joTripInfo = new JsonObject();
			joTripInfo.put("TRIP_COUNT", this.loopDtgINFOBodyTwo.getTripCount());
			joTripInfo.put("TRIP_SEQ", this.loopDtgINFOBodyTwo.getTripSequence());
			joTripInfo.put("DRIVING_INFO_CODE", strFirstDrivingCode);

			Util.updateDBInfo(vertx, "0" + this.loopDtgINFOBodyTwo.getIdentityNo(), DBConst.COMMAND_SET_TRIPINFO, joTripInfo);
		}
		else
		{
			// Trip Count가 같을 경우
			// 운행데이터와 트립시퀀스 날짜가 다르면 새로운 트립시퀀스 업뎃
			String strInfoTripDate = Util.getTripSeqToDate(this.loopDtgINFOBodyTwo.getTripSequence());
			String strDTSSDate = Util.getTripSeqToDate(firstLoopDtgDTSSBody.getTime());

			if (!strInfoTripDate.equals(strDTSSDate))
			{
				this.loopDtgINFOBodyTwo.setTripSequence(firstLoopDtgDTSSBody.getTime());
				strFirstDrivingCode = "S";

				// DB 저장 호출 만들기
				JsonObject joTripInfo = new JsonObject();
				joTripInfo.put("TRIP_COUNT", this.loopDtgINFOBodyTwo.getTripCount());
				joTripInfo.put("TRIP_SEQ", this.loopDtgINFOBodyTwo.getTripSequence());
				joTripInfo.put("DRIVING_INFO_CODE", strFirstDrivingCode);

				Util.updateDBInfo(vertx, "0" + this.loopDtgINFOBodyTwo.getIdentityNo(), DBConst.COMMAND_SET_TRIPINFO, joTripInfo);
			}
			else
			{
				strFirstDrivingCode = "-";
			}

		}

		LoopDtgDTSSBody lastLoopDtgDTSSBody = this.alLoopDtgDTSSBodyTwo.get(this.alLoopDtgDTSSBodyTwo.size() - 1);

		int intKeyOn = lastLoopDtgDTSSBody.getKeyOn();
		String strLastDrivingCode = "";

		switch (intKeyOn)
		{
			case 0:
				strLastDrivingCode = "E";

				break;
			case 1:
			default:

				strLastDrivingCode = "-";

				break;
		}

		this.loopDtgINFOBodyTwo.setDrivingInfoCode(strFirstDrivingCode + strLastDrivingCode);
	}

	/**
	 * 시간 확인
	 * 
	 * @return
	 */
	private void validateTime()
	{
		Util.writeTraceLog(this.vertx, "[" + LoopDtgDTSSService.class.getName() + "] Validing Drive Time in DTSS Data");

		long longBeforeTime = 0L;

		for (LoopDtgDTSSBody loopDtgDTSSBody : this.alLoopDtgDTSSBody)
		{
			if (longBeforeTime >= loopDtgDTSSBody.getTime())
			{
				Util.writeTraceLog(vertx, "날짜 역순 발생 해당 데이터 Error 추가 [" + longBeforeTime + "][" + loopDtgDTSSBody.getTime() + "]");
				loopDtgDTSSBody.setErrCount(loopDtgDTSSBody.getErrCount() + 1);
			}

			longBeforeTime = loopDtgDTSSBody.getTime();
		}

		// 두번째 운행데이터가 존재 할 경우 시간 순서 확인 //
		if (this.alLoopDtgDTSSBodyTwo.size() > 0)
		{
			for (LoopDtgDTSSBody loopDtgDTSSBody : this.alLoopDtgDTSSBodyTwo)
			{
				if (longBeforeTime >= loopDtgDTSSBody.getTime())
				{
					Util.writeTraceLog(vertx, "날짜 역순 발생 해당 데이터 Error 추가 두번째 운행데이터 [" + longBeforeTime + "][" + loopDtgDTSSBody.getTime() + "]");
					loopDtgDTSSBody.setErrCount(loopDtgDTSSBody.getErrCount() + 1);
				}

				longBeforeTime = loopDtgDTSSBody.getTime();
			}
		}
	}

	// Fator Data Type 검중 로직 추가 //

	/**
	 * 전체 정합성 검증
	 * 
	 * @throws Exception
	 */
	public void validateFactor()
	{
		Util.writeTraceLog(this.vertx, "[" + LoopDtgDTSSService.class.getName() + "] Validing Factor in DTSS Data");

		for (LoopDtgDTSSBody loopDtgDTSSBody : this.alLoopDtgDTSSBody)
		{
			if (!checkDayDistance(loopDtgDTSSBody.getDayDistance())) // 일일 주행거리
			{
				Util.writeTraceLog(vertx, "일일주행거리 오류 [" + loopDtgDTSSBody.getDayDistance() + "]");
				loopDtgDTSSBody.setErrCount(loopDtgDTSSBody.getErrCount() + 1);
			}

			if (!checkTotDistance(loopDtgDTSSBody.getTotDistance())) // 총 주행거리
			{
				Util.writeTraceLog(vertx, "총 주행거리 오류 [" + loopDtgDTSSBody.getTotDistance() + "]");
				loopDtgDTSSBody.setErrCount(loopDtgDTSSBody.getErrCount() + 1);
			}

			if (!checkSpeed(loopDtgDTSSBody.getSpeed())) // 속도
			{
				Util.writeTraceLog(vertx, "속도 오류 [" + loopDtgDTSSBody.getSpeed() + "]");
				loopDtgDTSSBody.setErrCount(loopDtgDTSSBody.getErrCount() + 1);
			}

			if (!checkBrk(loopDtgDTSSBody.getBreak())) // 브레이크
			{
				Util.writeTraceLog(vertx, "브레이크 오류 [" + loopDtgDTSSBody.getBreak() + "]");
				loopDtgDTSSBody.setErrCount(loopDtgDTSSBody.getErrCount() + 1);
			}

			if (!checkRpm(loopDtgDTSSBody.getRPM())) // RPM
			{
				Util.writeTraceLog(vertx, "RPM 오류 [" + loopDtgDTSSBody.getRPM() + "]");
				loopDtgDTSSBody.setErrCount(loopDtgDTSSBody.getErrCount() + 1);
			}

			if (!checkLongitude(loopDtgDTSSBody.getLongitude())) // GPSX : 127.00
			{
				Util.writeTraceLog(vertx, "GPSX 오류 [" + loopDtgDTSSBody.getLongitude() + "]");
				loopDtgDTSSBody.setErrCount(loopDtgDTSSBody.getErrCount() + 1);
			}

			if (!checkLatitude(loopDtgDTSSBody.getLatitude())) // GPSY : 37.5
			{
				Util.writeTraceLog(vertx, "GPSY 오류 [" + loopDtgDTSSBody.getLatitude() + "]");
				loopDtgDTSSBody.setErrCount(loopDtgDTSSBody.getErrCount() + 1);
			}

			if (!checkAzimuth(loopDtgDTSSBody.getAzimuth())) // 방위각
			{
				Util.writeTraceLog(vertx, "방위각 오류 [" + loopDtgDTSSBody.getAzimuth() + "]");
				loopDtgDTSSBody.setErrCount(loopDtgDTSSBody.getErrCount() + 1);
			}
		}

		if (this.alLoopDtgDTSSBodyTwo.size() > 0)
		{
			for (LoopDtgDTSSBody loopDtgDTSSBody : this.alLoopDtgDTSSBodyTwo)
			{
				if (!checkDayDistance(loopDtgDTSSBody.getDayDistance())) // 일일 주행거리
				{
					Util.writeTraceLog(vertx, "두번째 운행데이터 일일주행거리 오류 [" + loopDtgDTSSBody.getDayDistance() + "]");
					loopDtgDTSSBody.setErrCount(loopDtgDTSSBody.getErrCount() + 1);
				}

				if (!checkTotDistance(loopDtgDTSSBody.getTotDistance())) // 총 주행거리
				{
					Util.writeTraceLog(vertx, "두번째 운행데이터 총 주행거리 오류 [" + loopDtgDTSSBody.getTotDistance() + "]");
					loopDtgDTSSBody.setErrCount(loopDtgDTSSBody.getErrCount() + 1);
				}

				if (!checkSpeed(loopDtgDTSSBody.getSpeed())) // 속도
				{
					Util.writeTraceLog(vertx, "두번째 운행데이터 속도 오류 [" + loopDtgDTSSBody.getSpeed() + "]");
					loopDtgDTSSBody.setErrCount(loopDtgDTSSBody.getErrCount() + 1);
				}

				if (!checkBrk(loopDtgDTSSBody.getBreak())) // 브레이크
				{
					Util.writeTraceLog(vertx, "두번째 운행데이터 브레이크 오류 [" + loopDtgDTSSBody.getBreak() + "]");
					loopDtgDTSSBody.setErrCount(loopDtgDTSSBody.getErrCount() + 1);
				}

				if (!checkRpm(loopDtgDTSSBody.getRPM())) // RPM
				{
					Util.writeTraceLog(vertx, "두번째 운행데이터 RPM 오류 [" + loopDtgDTSSBody.getRPM() + "]");
					loopDtgDTSSBody.setErrCount(loopDtgDTSSBody.getErrCount() + 1);
				}

				if (!checkLongitude(loopDtgDTSSBody.getLongitude())) // GPSX : 127.00
				{
					Util.writeTraceLog(vertx, "두번째 운행데이터 GPSX 오류 [" + loopDtgDTSSBody.getLongitude() + "]");
					loopDtgDTSSBody.setErrCount(loopDtgDTSSBody.getErrCount() + 1);
				}

				if (!checkLatitude(loopDtgDTSSBody.getLatitude())) // GPSY : 37.5
				{
					Util.writeTraceLog(vertx, "두번째 운행데이터 GPSY 오류 [" + loopDtgDTSSBody.getLatitude() + "]");
					loopDtgDTSSBody.setErrCount(loopDtgDTSSBody.getErrCount() + 1);
				}

				if (!checkAzimuth(loopDtgDTSSBody.getAzimuth())) // 방위각
				{
					Util.writeTraceLog(vertx, "두번째 운행데이터 방위각 오류 [" + loopDtgDTSSBody.getAzimuth() + "]");
					loopDtgDTSSBody.setErrCount(loopDtgDTSSBody.getErrCount() + 1);
				}
			}
		}
	}

	/**
	 * 일일주행거리 정합성 검증
	 */
	private boolean checkDayDistance(float dayDistance)
	{
		if (dayDistance < 0 || dayDistance > 9999)
		{
			return false;
		}

		return true;
	}

	/**
	 * 누적주행거리 정합성 검증
	 */
	private boolean checkTotDistance(float totDistance)
	{
		if (totDistance < 0 || totDistance > 9999999)
		{
			return false;
		}

		return true;
	}

	/**
	 * 속도 정합성 검증
	 * 
	 * @throws Exception
	 */
	private boolean checkSpeed(int intSpeed)
	{
		if (intSpeed < 0 || intSpeed > 255)
		{
			return false;
		}

		return true;
	}

	/**
	 * 브레이크신호 정합성 검증
	 * 
	 * @throws Exception
	 */
	private boolean checkBrk(int intBreak)
	{
		if (0 != intBreak && 1 != intBreak)
		{
			return false;
		}

		return true;
	}

	/**
	 * RPM 정합성 검증
	 */
	private boolean checkRpm(int intRpm)
	{
		if (intRpm < 0 || intRpm > 9999)
		{
			return false;
		}

		return true;
	}

	/**
	 * gps X 정합성 검증
	 * 
	 * @throws Exception
	 */
	private boolean checkLongitude(long longLongitude)
	{
		if (longLongitude < -180000000 || longLongitude > 180000000)
		{
			return false;
		}

		return true;
	}

	/**
	 * gps Y 정합성 검증
	 * 
	 * @throws Exception
	 */
	private boolean checkLatitude(long longLatitude)
	{
		if (longLatitude < -90000000 || longLatitude > 90000000)
		{
			return false;
		}

		return true;
	}

	/**
	 * 방위각 정합성 검증
	 * 
	 * @throws Exception
	 */
	private boolean checkAzimuth(int intAzimuth)
	{
		if (intAzimuth < 0 || intAzimuth > 360)
		{
			return false;
		}

		return true;
	}

	////////////////////////////////

	/**
	 * 수신된 운행데이터 버퍼기준 운행 Array(VO) 만들기
	 */
	private void setDataArray()
	{
		Util.writeTraceLog(this.vertx, "[" + LoopDtgDTSSService.class.getName() + "] Set VO from DTSS Data");

		int intStart = 0;
		int intFirstTripCount = (int) Util.getByte(this.bytesDriveData, Const.LEN_LOOP_TRIP_START, Const.LEN_LOOP_TRIP_COUNT, "L"); // 패킷 첫번째 시각 데이터의
																																	// 트립카운트 //
		String strFirstDate = Util.getTripSeqToDate(Util.getInteger(this.bytesDriveData, 0, Const.LEN_LOOP_TIME, "L")); // 패킷 첫뻔재 시각기준 날짜 //

		for (int i = 0; i < this.intDataCount; i++)
		{
			LoopDtgDTSSBody curLoopDtgDTSSBody = this.setDTSSBody(Util.getBytes(this.bytesDriveData, intStart, Const.LEN_LOOP_DB_TOTAL), i);
			String strCurDate = Util.getTripSeqToDate(curLoopDtgDTSSBody.getTime());

			// 첫번째 TripCount와 같으면서 날짜가 동일하면 첫번째 운행데이터 Array에 추가 함 //
			if (intFirstTripCount == curLoopDtgDTSSBody.getTripCount() && strFirstDate.equals(strCurDate))
			{
				this.alLoopDtgDTSSBody.add(curLoopDtgDTSSBody);
			}
			else // 이외의 경우 새로운 날짜가 변경된 것으로 두번째 운행데이터 Array에 추가 함 //
			{
				this.alLoopDtgDTSSBodyTwo.add(curLoopDtgDTSSBody);
			}

			intStart += Const.LEN_LOOP_DB_TOTAL;
		}
	}

	/**
	 * 시동 On/Off, 브레이크(Brake) 값 추출
	 * 
	 * @param body
	 */
	private void parseSignal(LoopDtgDTSSBody body)
	{
		// Signal 값 불러오기
		int intSignal = body.getSignal();

		String[] strArraySignal = Util.convertBinaryStringToArray(intSignal, 16);

		// break와 Keyon을 추출해 낸다
		int intBreak = Integer.parseInt(strArraySignal[15]);
		int intKeyOn = Integer.parseInt(strArraySignal[12]);

		body.setBreak(intBreak);
		body.setKeyOn(intKeyOn);
	}

	/**
	 * 단말기 상태값 추출
	 * 
	 * @param body
	 */
	private void parseStatus(LoopDtgDTSSBody body)
	{
		// Stautus 가져오기
		String[] strArrayStatus = Util.convertBinaryStringToArray(body.getStatus(), 16);
		String[] strArrayTemp = new String[strArrayStatus.length];

		for (int i = 0; i < strArrayStatus.length; i++)
		{
			strArrayTemp[i] = strArrayStatus[strArrayStatus.length - i - 1];
		}

		int intNo = 0;
		boolean boolSet = false;

		for (String strStatus : strArrayTemp)
		{
			if (strStatus.equals("1"))
			{
				boolSet = true;
				break;
			}

			intNo++;
		}

		if (!boolSet)
		{
			intNo++;
		}

		body.setVDRStatus(Util.strArrayStatus[intNo]);
	}

	/**
	 * Buffer to DTSSVO 
	 * 
	 * @param byteDataBuf
	 * @param intIndex
	 * @return
	 */
	private LoopDtgDTSSBody setDTSSBody(byte[] byteDataBuf, int intIndex)
	{
		LoopDtgDTSSBody body = new LoopDtgDTSSBody();

		int intStart = 0;

		body.setTime(Util.getInteger(byteDataBuf, intStart, Const.LEN_LOOP_TIME, "L"));
		intStart += Const.LEN_LOOP_TIME;

		// log.debug("Index["+intIndex+"] Time[" + Util.convertTime(body.getTime() * 1000L, "T") + "]");

		body.setSpeed((int) Util.getByte(byteDataBuf, intStart, Const.LEN_LOOP_SPEED, "L"));
		intStart += Const.LEN_LOOP_SPEED;
		body.setSpeedFix((int) Util.getByte(byteDataBuf, intStart, Const.LEN_LOOP_SPEED_FLOAT, "L"));
		intStart += Const.LEN_LOOP_SPEED_FLOAT;
		body.setRPM(Util.getShort(byteDataBuf, intStart, Const.LEN_LOOP_RPM, "L"));
		intStart += Const.LEN_LOOP_RPM;
		body.setSignal(Util.getShort(byteDataBuf, intStart, Const.LEN_LOOP_SIGNAL, "L"));
		intStart += Const.LEN_LOOP_SIGNAL;

		this.parseSignal(body); // Signal에서 Break, KeyOn 추출하기

		body.setStatus(Util.getShort(byteDataBuf, intStart, Const.LEN_LOOP_STATUS, "L"));
		intStart += Const.LEN_LOOP_STATUS;

		this.parseStatus(body); // 차량 상태 추출하기 발생한 가장 첫번째 상태만 추출함 //

		body.setLatitude(Util.getInteger(byteDataBuf, intStart, Const.LEN_LOOP_LATITUDE, "L"));
		intStart += Const.LEN_LOOP_LATITUDE;
		body.setLongitude(Util.getInteger(byteDataBuf, intStart, Const.LEN_LOOP_LONGITUDE, "L"));
		intStart += Const.LEN_LOOP_LONGITUDE;
		body.setAzimuth(Util.getShort(byteDataBuf, intStart, Const.LEN_LOOP_AZIMUTH, "L"));
		intStart += Const.LEN_LOOP_AZIMUTH;
		body.setGPSStatus((int) Util.getByte(byteDataBuf, intStart, Const.LEN_LOOP_GPS_STATUS, "L"));
		intStart += Const.LEN_LOOP_GPS_STATUS;
		body.setGPSSpeed((int) Util.getByte(byteDataBuf, intStart, Const.LEN_LOOP_GPS_SPEED, "L"));
		intStart += Const.LEN_LOOP_GPS_SPEED;
		body.setAccVx(Util.getShort(byteDataBuf, intStart, Const.LEN_LOOP_ACC_X, "L"));
		intStart += Const.LEN_LOOP_ACC_X;
		body.setAccVy(Util.getShort(byteDataBuf, intStart, Const.LEN_LOOP_ACC_Y, "L"));
		intStart += Const.LEN_LOOP_ACC_Y;
		body.setTripCount((int) Util.getByte(byteDataBuf, intStart, Const.LEN_LOOP_TRIP_COUNT, "L"));
		intStart += Const.LEN_LOOP_TRIP_COUNT;

		body.setDriverNo((int) Util.getByte(byteDataBuf, intStart, Const.LEN_LOOP_DRIVER_NO, "L"));
		intStart += Const.LEN_LOOP_DRIVER_NO;
		body.setRSSI((int) Util.getByte(byteDataBuf, intStart, Const.LEN_LOOP_RSSI, "L"));
		intStart += Const.LEN_LOOP_RSSI;
		body.setIsgStatus((int) Util.getByte(byteDataBuf, intStart, Const.LEN_LOOP_ISG_STATUS, "L"));
		intStart += Const.LEN_LOOP_ISG_STATUS;
		body.setDistance(Util.getFloat(byteDataBuf, intStart, Const.LEN_LOOP_DISTANCE, "L"));
		intStart += Const.LEN_LOOP_DISTANCE;
		body.setDayDistance(Util.getFloat(byteDataBuf, intStart, Const.LEN_LOOP_DAY_DISTANCE, "L"));
		intStart += Const.LEN_LOOP_DAY_DISTANCE;
		body.setTotDistance(Util.getFloat(byteDataBuf, intStart, Const.LEN_LOOP_TOT_DISTANCE, "L"));
		intStart += Const.LEN_LOOP_TOT_DISTANCE;
		body.setFuelConsumption(Util.getFloat(byteDataBuf, intStart, Const.LEN_LOOP_FUEL_CONSUMPTION, "L"));
		intStart += Const.LEN_LOOP_FUEL_CONSUMPTION;
		body.setDayFuelConsumption(Util.getFloat(byteDataBuf, intStart, Const.LEN_LOOP_DAY_FUEL_CONSUMPTION, "L"));
		intStart += Const.LEN_LOOP_DAY_FUEL_CONSUMPTION;
		body.setTotFuelConsumption(Util.getFloat(byteDataBuf, intStart, Const.LEN_LOOP_TOT_FUEL_CONSUMPTION, "L"));
		intStart += Const.LEN_LOOP_TOT_FUEL_CONSUMPTION;
		body.setBatteryVolt(Util.getShort(byteDataBuf, intStart, Const.LEN_LOOP_BATTERY_VOLT, "L"));
		intStart += Const.LEN_LOOP_BATTERY_VOLT;
		body.setAEBS((int) Util.getByte(byteDataBuf, intStart, Const.LEN_LOOP_AEBS, "L"));
		intStart += Const.LEN_LOOP_AEBS;
		body.setLDWS1((int) Util.getByte(byteDataBuf, intStart, Const.LEN_LOOP_LDWS1, "L"));
		intStart += Const.LEN_LOOP_LDWS1;
		body.setTemp1(Util.getShort(byteDataBuf, intStart, Const.LEN_LOOP_TEMP1, "L"));
		intStart += Const.LEN_LOOP_TEMP1;
		body.setTemp2(Util.getShort(byteDataBuf, intStart, Const.LEN_LOOP_TEMP2, "L"));
		intStart += Const.LEN_LOOP_TEMP2;

		return body;
	}

	/**
	 * 사용용도를 모르겠은 이전 플랫폼에 존재하는 로직으로 왜 만들었는지 모르는 Method 이나 우선 옮겨 놓았음
	 * 
	 * @return
	 */
	public Buffer getBufferTrace()
	{
		Util.writeTraceLog(this.vertx, "[" + LoopDtgDTSSService.class.getName() + "] Maiking Trace Data from DTSS Data");

		Buffer trace = Buffer.buffer();

		trace.appendString("---------------------------------------------\n");
		trace.appendString("Model : " + this.loopDtgINFOBody.getModelNo() + "\n");
		trace.appendString("Vinnum : " + this.loopDtgINFOBody.getCarIdNo() + "\n");
		trace.appendString("CarType : " + Util.convertCarType(this.loopDtgINFOBody.getCarType()) + "\n");
		trace.appendString("CarNum : " + this.loopDtgINFOBody.getCarRegNo() + "\n");
		trace.appendString("OperCode : " + "60" + "\n");
		trace.appendString("DriverCode : " + this.loopDtgINFOBody.getDriverCode() + "\n");
		trace.appendString("TripSeq : " + Util.convertTime(this.loopDtgINFOBody.getTripSequence(), "T") + "\n");
		trace.appendString("DriveInfoCode : " + this.loopDtgINFOBody.getDrivingInfoCode() + "\n");
		trace.appendString("---------------------------------------------\n");

		for (LoopDtgDTSSBody loopDtgDTSSBody : this.alLoopDtgDTSSBody)
		{
			trace.appendString(Util.convertTime(loopDtgDTSSBody.getTime(), "D"));
			trace.appendString(",");

			String[] alTotDistance = String.format("%.3f", loopDtgDTSSBody.getTotDistance()).split("\\.");
			trace.appendString(alTotDistance[0]);
			trace.appendString(",");
			trace.appendString(alTotDistance[1]);
			trace.appendString(",");

			String[] alDayDistance = String.format("%.3f", loopDtgDTSSBody.getTotDistance()).split("\\.");
			trace.appendString(alDayDistance[0]);
			trace.appendString(",");
			trace.appendString(Integer.toString(loopDtgDTSSBody.getSpeed()));
			trace.appendString(",");
			trace.appendString(Integer.toString(loopDtgDTSSBody.getRPM()));
			trace.appendString(",");
			trace.appendString(Integer.toString(loopDtgDTSSBody.getBreak()));
			trace.appendString(",");
			trace.appendString(Long.toString(loopDtgDTSSBody.getLongitude()));
			trace.appendString(",");
			trace.appendString(Long.toString(loopDtgDTSSBody.getLatitude()));
			trace.appendString(",");
			trace.appendString(Integer.toString(loopDtgDTSSBody.getAzimuth()));
			trace.appendString(",");
			trace.appendString(Integer.toString(loopDtgDTSSBody.getAccVx()));
			trace.appendString(",");
			trace.appendString(Integer.toString(loopDtgDTSSBody.getAccVy()));
			trace.appendString(",");
			trace.appendString(loopDtgDTSSBody.getVDRStatus());
			trace.appendString(",");
			trace.appendString(Integer.toString((int) loopDtgDTSSBody.getDayFuelConsumption()));
			trace.appendString(",");

			String[] alTotFuelConsumption = String.format("%.3f", loopDtgDTSSBody.getDayFuelConsumption()).split("\\.");
			trace.appendString(alTotFuelConsumption[0]);
			trace.appendString(",");
			trace.appendString(alTotFuelConsumption[1]);
			trace.appendString(",");
			trace.appendString(""); // 연료 분사량은 루프에 없음 //
			trace.appendString(",");
			trace.appendString(""); // 가속 페달 루프에 없음 //
			trace.appendString(",");
			trace.appendString(""); // 변속 레버 루프에 없음 //
			trace.appendString(",");
			trace.appendString(""); // 변속단 루프에 없음 //
			trace.appendString(",");
			trace.appendString(""); // 토크 루프에 없음 //
			trace.appendString(",");
			trace.appendString(""); // 냉각수 온도 루프에 없음 //
			trace.appendString(",");
			trace.appendString(""); // 흡기 온도 루프에 없음 //
			trace.appendString("\n");
		}

		return trace;
	}

	/**
	 * 운행파일에 기록할 데이터 만들기
	 * 
	 * @return
	 */
	public Buffer getBufferForDTG()
	{
		Util.writeTraceLog(this.vertx, "[" + LoopDtgDTSSService.class.getName() + "] Making RAW Data from DTSS Data");

		Buffer raw = Buffer.buffer();

		String tripseq_modi = Util.convertTime(this.loopDtgINFOBody.getTripSequence() * 1000L, "T");
		// tripseq_modi = tripseq_modi.substring(2)+"00";

		for (int i = 0; i < this.alLoopDtgDTSSBody.size(); i++)
		{
			LoopDtgDTSSBody loopDtgDTSSBody = this.alLoopDtgDTSSBody.get(i);

			if (loopDtgDTSSBody.getErrCount() > 0)
			{
				continue;
			}

			// [0] 정책
			raw.appendString("VDS-001");
			raw.appendString(",");
			// [0] ->[1] 단말기 모델명
			raw.appendString(this.loopDtgINFOBody.getModelNo());
			raw.appendString(",");
			// [1] ->[2] 차대번호
			raw.appendString(this.loopDtgINFOBody.getCarIdNo());
			raw.appendString(",");
			// [2] ->[3]차량번호
			raw.appendString(this.loopDtgINFOBody.getCarRegNo());
			raw.appendString(",");
			// [14]->[4]사업자번호
			raw.appendString(this.loopDtgINFOBody.getBizNo());
			raw.appendString(",");
			// [15]->[5]운전자코드
			raw.appendString(this.loopDtgINFOBody.getDriverCode().trim());
			raw.appendString(",");
			// [16]->[6]자동차유형
			raw.appendString(Util.convertCarType(this.loopDtgINFOBody.getCarType()));
			raw.appendString(",");
			// [17]->[7]운행코드
			String driveCode = this.loopDtgINFOBody.getDrivingInfoCode();
			String driveCodeval = "";
			if (driveCode.equals("S-"))
			{
				if (i == 0)
				{
					driveCodeval = "S";
				}
				else
				{
					driveCodeval = "D";
				}
			}
			else if (driveCode.equals("--"))
			{
				driveCodeval = "D";
			}
			else if (driveCode.equals("-E"))
			{
				if (i == this.alLoopDtgDTSSBody.size() - 1)
				{
					driveCodeval = "E";
				}
				else
				{
					driveCodeval = "D";
				}
			}
			else if (driveCode.equals("SE"))
			{
				if (i == 0)
				{
					driveCodeval = "S";
				}
				else if (i == this.alLoopDtgDTSSBody.size() - 1)
				{
					driveCodeval = "E";
				}
				else
				{
					driveCodeval = "D";
				}
			}
			else
			{
				driveCodeval = "D";
			}
			raw.appendString(driveCodeval);
			raw.appendString(",");
			// [18]->[8]트립시퀀스
			raw.appendString(tripseq_modi); ///////////////////////////////////////////////////////////////////////////////////
			raw.appendString(",");
			// [19]->[9]정보발생일시
			raw.appendString(Util.convertTime(loopDtgDTSSBody.getTime() * 1000L, "D")); //////////////////////////////////////////////
			raw.appendString(",");

			// [20]->[10]누적주행거리
			String[] alTotDistance = String.format("%.3f", loopDtgDTSSBody.getTotDistance()).split("\\.");
			raw.appendString(alTotDistance[0]);
			raw.appendString(",");

			// [21]->[11]일주행거리
			String[] alDayDistance = String.format("%.3f", loopDtgDTSSBody.getDayDistance()).split("\\.");
			raw.appendString(alDayDistance[0]);
			raw.appendString(",");
			// [22]->[12]차량속도(km/h)
			raw.appendString(Integer.toString(loopDtgDTSSBody.getSpeed()));
			raw.appendString(",");
			// [24]->[13]RPM 분당 엔진회전
			raw.appendString(Integer.toString(loopDtgDTSSBody.getRPM()));
			raw.appendString(",");
			// [25]->[14]브레이크 신호
			raw.appendString(Integer.toString(loopDtgDTSSBody.getBreak()));
			raw.appendString(",");
			// [26]->[15]GPX X좌표
			raw.appendString(Double.toString(loopDtgDTSSBody.getLongitude() / 1000000d));
			raw.appendString(",");
			// [27]->[16]GPX Y좌표
			raw.appendString(Double.toString(loopDtgDTSSBody.getLatitude() / 1000000d));
			raw.appendString(",");
			// [28]->[17]GPS 방위각
			raw.appendString(Integer.toString(loopDtgDTSSBody.getAzimuth()));
			raw.appendString(",");
			// [29]->[18]가속도 X
			raw.appendString(Integer.toString(loopDtgDTSSBody.getAccVx())); ///////////////////////// //////////////////////
			raw.appendString(",");
			// [30]->[19]가속도 Y
			raw.appendString(Integer.toString(loopDtgDTSSBody.getAccVy())); /////////////////////////////////////
			raw.appendString(",");
			// [31]->[20]기기상태코드
			raw.appendString(loopDtgDTSSBody.getVDRStatus());
			raw.appendString(",");
			// [32]->[21]회선번호
			raw.appendString("0" + this.loopDtgINFOBody.getIdentityNo());
			raw.appendString(",");
			// [33]->[22]배터리 전압
			raw.appendString(Double.toString(Math.round(loopDtgDTSSBody.getBatteryVolt()) / 10.0)); ////////////////////////////////////////
			raw.appendString("\n");
		}

		return raw;
	}

	/**
	 * 00시00분00초 트립 쪼개기 데이터 발생시 처리 운행파일에 기록할 데이터 생성
	 * 
	 * @return
	 */
	public Buffer getBufferForDTGTwo()
	{
		Util.writeTraceLog(this.vertx, "[" + LoopDtgDTSSService.class.getName() + "] Making RAW Data from DTSS Data");

		Buffer raw = Buffer.buffer();

		String tripseq_modi = Util.convertTime(this.loopDtgINFOBodyTwo.getTripSequence() * 1000L, "T");
		// tripseq_modi = tripseq_modi.substring(2)+"00";

		for (int i = 0; i < this.alLoopDtgDTSSBodyTwo.size(); i++)
		{
			LoopDtgDTSSBody loopDtgDTSSBody = this.alLoopDtgDTSSBodyTwo.get(i);

			if (loopDtgDTSSBody.getErrCount() > 0)
			{
				continue;
			}

			// [0] 정책
			raw.appendString("VDS-001");
			raw.appendString(",");
			// [0] ->[1] 단말기 모델명
			raw.appendString(this.loopDtgINFOBodyTwo.getModelNo());
			raw.appendString(",");
			// [1] ->[2] 차대번호
			raw.appendString(this.loopDtgINFOBodyTwo.getCarIdNo());
			raw.appendString(",");
			// [2] ->[3]차량번호
			raw.appendString(this.loopDtgINFOBodyTwo.getCarRegNo());
			raw.appendString(",");
			// [14]->[4]사업자번호
			raw.appendString(this.loopDtgINFOBodyTwo.getBizNo());
			raw.appendString(",");
			// [15]->[5]운전자코드
			raw.appendString(this.loopDtgINFOBodyTwo.getDriverCode().trim());
			raw.appendString(",");
			// [16]->[6]자동차유형
			raw.appendString(Util.convertCarType(this.loopDtgINFOBodyTwo.getCarType()));
			raw.appendString(",");
			// [17]->[7]운행코드
			String driveCode = this.loopDtgINFOBodyTwo.getDrivingInfoCode();
			String driveCodeval = "";
			if (driveCode.equals("S-"))
			{
				if (i == 0)
				{
					driveCodeval = "S";
				}
				else
				{
					driveCodeval = "D";
				}
			}
			else if (driveCode.equals("--"))
			{
				driveCodeval = "D";
			}
			else if (driveCode.equals("-E"))
			{
				if (i == this.alLoopDtgDTSSBodyTwo.size() - 1)
				{
					driveCodeval = "E";
				}
				else
				{
					driveCodeval = "D";
				}
			}
			else if (driveCode.equals("SE"))
			{
				if (i == 0)
				{
					driveCodeval = "S";
				}
				else if (i == this.alLoopDtgDTSSBodyTwo.size() - 1)
				{
					driveCodeval = "E";
				}
				else
				{
					driveCodeval = "D";
				}
			}
			else
			{
				driveCodeval = "D";
			}
			raw.appendString(driveCodeval);
			raw.appendString(",");
			// [18]->[8]트립시퀀스
			raw.appendString(tripseq_modi); ///////////////////////////////////////////////////////////////////////////////////
			raw.appendString(",");
			// [19]->[9]정보발생일시
			raw.appendString(Util.convertTime(loopDtgDTSSBody.getTime() * 1000L, "D")); //////////////////////////////////////////////
			raw.appendString(",");

			// [20]->[10]누적주행거리
			String[] alTotDistance = String.format("%.3f", loopDtgDTSSBody.getTotDistance()).split("\\.");
			raw.appendString(alTotDistance[0]);
			raw.appendString(",");

			// [21]->[11]일주행거리
			String[] alDayDistance = String.format("%.3f", loopDtgDTSSBody.getDayDistance()).split("\\.");
			raw.appendString(alDayDistance[0]);
			raw.appendString(",");
			// [22]->[12]차량속도(km/h)
			raw.appendString(Integer.toString(loopDtgDTSSBody.getSpeed()));
			raw.appendString(",");
			// [24]->[13]RPM 분당 엔진회전
			raw.appendString(Integer.toString(loopDtgDTSSBody.getRPM()));
			raw.appendString(",");
			// [25]->[14]브레이크 신호
			raw.appendString(Integer.toString(loopDtgDTSSBody.getBreak()));
			raw.appendString(",");
			// [26]->[15]GPX X좌표
			raw.appendString(Double.toString(loopDtgDTSSBody.getLongitude() / 1000000d));
			raw.appendString(",");
			// [27]->[16]GPX Y좌표
			raw.appendString(Double.toString(loopDtgDTSSBody.getLatitude() / 1000000d));
			raw.appendString(",");
			// [28]->[17]GPS 방위각
			raw.appendString(Integer.toString(loopDtgDTSSBody.getAzimuth()));
			raw.appendString(",");
			// [29]->[18]가속도 X
			raw.appendString(Integer.toString(loopDtgDTSSBody.getAccVx())); ///////////////////////// //////////////////////
			raw.appendString(",");
			// [30]->[19]가속도 Y
			raw.appendString(Integer.toString(loopDtgDTSSBody.getAccVy())); /////////////////////////////////////
			raw.appendString(",");
			// [31]->[20]기기상태코드
			raw.appendString(loopDtgDTSSBody.getVDRStatus());
			raw.appendString(",");
			// [32]->[21]회선번호
			raw.appendString("0" + this.loopDtgINFOBodyTwo.getIdentityNo());
			raw.appendString(",");
			// [33]->[22]배터리 전압
			raw.appendString(Double.toString(Math.round(loopDtgDTSSBody.getBatteryVolt()) / 10.0)); ////////////////////////////////////////
			raw.appendString("\n");
		}

		return raw;
	}

}
