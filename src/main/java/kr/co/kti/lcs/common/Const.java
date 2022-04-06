package kr.co.kti.lcs.common;

/**
 * 상수 
 * 
 * @author yeski
 *
 */
public class Const
{
	public final static String ENCODE_TYPE = "EUC-KR";
	
	public final static int FIRST_TRIP_COUNT = 1;
	
	/**
	 * DATA COMMAND 
	 */
	public final static String COMMAND_INFO = "INFO";
	public final static String COMMAND_DTSC = "DTSC";
	public final static String COMMAND_DTSS = "DTSS";
	public final static String COMMAND_EVSS = "EVSS";
	
	/**
	 * Consumer Address
	 */
	public final static String EB_AUTH_WORKER = "EB_AUTH_WORKER";
	public final static String EB_DAO_MANAGER_WORKER = "EB_DAO_MANAGER_WORKER";
	public final static String EB_DRIVING_DATA_PROC_WORKER = "EB_DRIVING_DATA_PROC_WORKER";
	public final static String EB_EVENT_DATA_PROC_WORKER = "EB_EVENT_DATA_PROC_WORKER";
	public final static String EB_FILE_CREATE_WORKER = "EB_FILE_CREATE_WORKER";
	public final static String EB_TRACE_LOG_WORKER = "EB_TRACE_LOG_WORKER";

	/**
	 * Define LOOP DTG Frame Protocol Length
	 */
	public final static int LEN_LOOP_SPK = 2;
	public final static int LEN_LOOP_DEVICE_ID = 2;
	public final static int LEN_LOOP_IDENTITY_NO = 4;
	public final static int LEN_LOOP_COMMAND = 4;
	public final static int LEN_LOOP_INDEX = 2;
	public final static int LEN_LOOP_COUNT = 2;
	public final static int LEN_LOOP_DATA_LENGTH = 4;
	// 데이터 길이는 LEN_DATA_LENGTH에저 지정한 값만큼 변동 됨
	public final static int LEN_LOOP_CRC = 2;
	public final static int LEN_LOOP_EPK = 2;

	/**
	 * Define LOOP DTG HEADER Packet Length
	 */
	public final static int LEN_LOOP_HD_RESERVED1 = 4; // BYNARY
	public final static int LEN_LOOP_HD_RESERVED2 = 7; // BYNARY
	public final static int LEN_LOOP_HD_DRIVER_NAME = 10; // ASCII
	public final static int LEN_LOOP_HD_DRIVER_CODE = 18; // ASCII
	public final static int LEN_LOOP_HD_CAR_TYPE = 1; // DECIMAL : 차량유형
	public final static int LEN_LOOP_HD_CAR_REG_NO = 11; // ASCII : 차량번호 함께 전달 (규격서 반듯이 확인 필요)
	public final static int LEN_LOOP_HD_CAR_ID_NO = 17; // ASCII
	public final static int LEN_LOOP_HD_OFFICE_NAME = 12; // ASCII
	public final static int LEN_LOOP_HD_BIZ_NO = 10; // ASCII
	public final static int LEN_LOOP_HD_TYPE_APPROVAL_NO = 10; // ASCII : 형식승인번호 ???
	public final static int LEN_LOOP_HD_SERIAL_NO = 14; // ASCII : 제조 일련번호 ???
	public final static int LEN_LOOP_HD_MODEL_NO = 20; // ASCII
	public final static int LEN_LOOP_HD_K_FACTOR = 2; // DECIMAL : ?????
	public final static int LEN_LOOP_HD_RPM_FACTOR = 2; // DECIMAL : ?????
	public final static int LEN_LOOP_HD_RESERVED3 = 1; // BINARY
	public final static int LEN_LOOP_HD_FIRMWARE_VER = 6; // BINARY
	public final static int LEN_LOOP_HD_RESERVED4 = 4; // BINARY
	public final static int LEN_LOOP_HD_RESERVED5 = 38; // BINARY
	public final static int LEN_LOOP_HD_RESERVED6 = 1; // BINARY
	public final static int LEN_LOOP_HD_RESERVED7 = 4; // BINARY
	public final static int LEN_LOOP_HD_RESERVED8 = 4; // BINARY
	public final static int LEN_LOOP_HD_RESERVED9 = 4; // BINARY
	public final static int LEN_LOOP_HD_RESERVED10 = 56; // BINARY

	public final static int LEN_LOOP_HD_TOTAL = LEN_LOOP_HD_RESERVED1 + LEN_LOOP_HD_RESERVED2 + LEN_LOOP_HD_DRIVER_NAME + LEN_LOOP_HD_DRIVER_CODE + LEN_LOOP_HD_CAR_TYPE + LEN_LOOP_HD_CAR_REG_NO + LEN_LOOP_HD_CAR_ID_NO + LEN_LOOP_HD_OFFICE_NAME + LEN_LOOP_HD_BIZ_NO + LEN_LOOP_HD_TYPE_APPROVAL_NO
			+ LEN_LOOP_HD_SERIAL_NO + LEN_LOOP_HD_MODEL_NO + LEN_LOOP_HD_K_FACTOR + LEN_LOOP_HD_RPM_FACTOR + LEN_LOOP_HD_RESERVED3 + LEN_LOOP_HD_FIRMWARE_VER + LEN_LOOP_HD_RESERVED4 + LEN_LOOP_HD_RESERVED5 + LEN_LOOP_HD_RESERVED6 + LEN_LOOP_HD_RESERVED7 + LEN_LOOP_HD_RESERVED8
			+ LEN_LOOP_HD_RESERVED9 + LEN_LOOP_HD_RESERVED10;
	
	/**
	 * Define LOOP DTG Driving Data Packet Length
	 */
	public final static int LEN_LOOP_TIME = 4;
	public final static int LEN_LOOP_SPEED = 1;
	public final static int LEN_LOOP_SPEED_FLOAT = 1;
	public final static int LEN_LOOP_RPM = 2;
	public final static int LEN_LOOP_SIGNAL = 2; // 차량 신호 : 브레이크, 키온, 이벤트, 사고 등 (규격서 참조)
	public final static int LEN_LOOP_STATUS = 2; // 차량 고장 코드 : GPS오류, 속도 오류, 브레이크 오류 등 ... (규격서 참조)
	public final static int LEN_LOOP_LATITUDE = 4; // 위도
	public final static int LEN_LOOP_LONGITUDE = 4; // 경도
	public final static int LEN_LOOP_AZIMUTH = 2; // 방위각
	public final static int LEN_LOOP_GPS_STATUS = 1; // GPS 수신 상태 : 약함, 좋음, 불량, 유효 (규격서 참조)
	public final static int LEN_LOOP_GPS_SPEED = 1;
	public final static int LEN_LOOP_ACC_X = 2;
	public final static int LEN_LOOP_ACC_Y = 2;
	public final static int LEN_LOOP_TRIP_COUNT = 1;
	public final static int LEN_LOOP_DRIVER_NO = 1;
	public final static int LEN_LOOP_RSSI = 1;
	public final static int LEN_LOOP_ISG_STATUS = 1;
	public final static int LEN_LOOP_DISTANCE = 4; // 주행거리
	public final static int LEN_LOOP_DAY_DISTANCE = 4;
	public final static int LEN_LOOP_TOT_DISTANCE = 4;
	public final static int LEN_LOOP_FUEL_CONSUMPTION = 4;
	public final static int LEN_LOOP_DAY_FUEL_CONSUMPTION = 4;
	public final static int LEN_LOOP_TOT_FUEL_CONSUMPTION = 4;
	public final static int LEN_LOOP_BATTERY_VOLT = 2;
	public final static int LEN_LOOP_AEBS = 1;
	public final static int LEN_LOOP_LDWS1 = 1;
	public final static int LEN_LOOP_TEMP1 = 2;
	public final static int LEN_LOOP_TEMP2 = 2;

	// 운행데이터 전체 길이 //
	public final static int LEN_LOOP_DB_TOTAL = LEN_LOOP_TIME + LEN_LOOP_SPEED + LEN_LOOP_SPEED_FLOAT + LEN_LOOP_RPM + LEN_LOOP_SIGNAL + LEN_LOOP_STATUS + LEN_LOOP_LATITUDE + LEN_LOOP_LONGITUDE + LEN_LOOP_AZIMUTH + LEN_LOOP_GPS_STATUS + LEN_LOOP_GPS_SPEED + LEN_LOOP_ACC_X + LEN_LOOP_ACC_Y
			+ LEN_LOOP_TRIP_COUNT + LEN_LOOP_DRIVER_NO + LEN_LOOP_RSSI + LEN_LOOP_ISG_STATUS + LEN_LOOP_DISTANCE + LEN_LOOP_DAY_DISTANCE + LEN_LOOP_TOT_DISTANCE + LEN_LOOP_FUEL_CONSUMPTION + LEN_LOOP_DAY_FUEL_CONSUMPTION + LEN_LOOP_TOT_FUEL_CONSUMPTION + LEN_LOOP_BATTERY_VOLT + LEN_LOOP_AEBS
			+ LEN_LOOP_LDWS1 + LEN_LOOP_TEMP1 + LEN_LOOP_TEMP2;
	
	// Trip Count 추출용 시작 주소 //
	public final static int LEN_LOOP_TRIP_START = LEN_LOOP_TIME + LEN_LOOP_SPEED + LEN_LOOP_SPEED_FLOAT + LEN_LOOP_RPM + LEN_LOOP_SIGNAL + LEN_LOOP_STATUS + LEN_LOOP_LATITUDE + LEN_LOOP_LONGITUDE + LEN_LOOP_AZIMUTH + LEN_LOOP_GPS_STATUS + LEN_LOOP_GPS_SPEED + LEN_LOOP_ACC_X + LEN_LOOP_ACC_Y;

	// ============================================================================================================================ //

	/**
	 * 자체 플랫폼 define 상수
	 */
	// define VDR header item length
	public final static int LEN_MODEL_NAME = 20; // 전송장치 모델 1
	public final static int LEN_VEHICLE_IDENTIFICATION_NUNBER = 17; // 차대번호 1
	public final static int LEN_VEHICLE_TYPE = 2; // 자동차 유형 1
	public final static int LEN_VEHICLE_REGISTRATION_NUMBER = 12; // 자동차 등록번호 1
	public final static int LEN_TRANSPORT_PROVIDER_NUMBER = 10; // 운송사업자 번호 1
	public final static int LEN_DRIVER_CODE = 18; // 운전자 코드 1
	public final static int LEN_TRIP_SEQUENCE = 14; // Trip sequence 정보발생 일
	public final static int LEN_DRIVING_INFO_CODE = 3; // 운행정보 종류 'S-' or 'SE' or '-E'
	// 마지막 바이트는 NULL(0x00)
	public final static int LEN_RECORD_TYPE = 1; // 레코드 종류 1 or 2
	public final static int LEN_ATTRIBUTE_TYPE = 1; // 레코드 속성 TYPE 길이
	public final static int LEN_ATTRIBUTE_RSVD = 1; // 레코드 속성 RSVD 길이
	public final static int LEN_ATTRIBUTE_LENGTH = 2; // 레코드 속성 LENGTH 길이

	public final static int LEN_MIN_VDR_HEAD_LEN = 101; // VDR 만 포함할 경우 최소 길
	public final static int LEN_MAX_VDR_HEAD_LEN = 105; // VDR + VER 을 포함한 경우 최대 길이
	public final static int LEN_E164 = 11; // E164 길이

	// 레코드 속성 TYPE
	public final static int VDR_TYPE = 0; // VDR attribute TYPE
	public final static int VER_TYPE = 1; // VER attribute TYPE

	public final static int LEN_VDR_HEADER_97BYTE = LEN_MODEL_NAME + LEN_VEHICLE_IDENTIFICATION_NUNBER + LEN_VEHICLE_TYPE + LEN_VEHICLE_REGISTRATION_NUMBER + LEN_TRANSPORT_PROVIDER_NUMBER + LEN_DRIVER_CODE + LEN_TRIP_SEQUENCE + LEN_DRIVING_INFO_CODE + LEN_RECORD_TYPE;

	// define VDR data item length
	public final static int LEN_VDR_OCCURRENCE_TIME = 17; // 정보발생일시
	public final static int LEN_GROSS_ACC_MILEAGE_K = 4; // 총누적 주행거리(km). KM 단위 소수점 아래는 버린다.
	public final static int LEN_GROSS_ACC_MILEAGE_M = 2; // 총누적 주행거리(m). 1000m 이하 m단위만 기술한다.
	public final static int LEN_DAILY_MILEAGE_K = 2; // 일일주행거리(km).
	public final static int LEN_SPEED = 2; // 차량속도(km/h)
	public final static int LEN_RPM = 2; // RPM 분당엔진회전
	public final static int LEN_BRAKE_SIGNAL = 1; // 브레이크 신호 0 or 1
	public final static int LEN_GPS_X = 4; // GPS X좌표
	public final static int LEN_GPS_Y = 4; // GPS Y좌표
	public final static int LEN_AZIMUTH = 2; // GPS 방위각
	public final static int LEN_ACC_X = 2; // 가속도 X m/s2
	public final static int LEN_ACC_Y = 2; // 가속도 Y m/s2
	public final static int LEN_STAT_CODE = 3; // 기기상태 코드
	public final static int LEN_DAILY_FUEL_CONSUMPTION_L = 2; // 일일연료소모량 (L)
	public final static int LEN_GROSS_ACC_FUEL_CONSUMPTION_L = 4; // 총누적연료소모량 (L). litter 단위 소수점 아래는 버린다.
	public final static int LEN_GROSS_ACC_FUEL_CONSUMPTION_ML = 2; // 총누적연료소모량 (ML). 1000ml 이하 ml 단위만 기록한다.
	public final static int LEN_MOMENT_FUEL_INJECTION_AMOUNT = 4; // 순간연료분사량 (ml/s)
	public final static int LEN_TPS = 2; // 가속페달(%). 0.1~100.0
	public final static int LEN_GEARSHIFT = 1; // 변속레버 S={P,R,D,N,M}
	public final static int LEN_GEAR_STEP = 1; // 변속단 0~20
	public final static int LEN_TORQUE = 4; // 토크 0~999.99
	public final static int LEN_COOLANT_TEMPERATURE = 2; // 냉각수 온도
	public final static int LEN_INTAKE_TEMPERATURE = 2; // 흡기온도
	public final static int LEN_ACC_MAF = 4; // 누적MAF
	public final static int LEN_MOMENT_MAF = 4; // 순간MAF
	public final static int LEN_AIR_PRESSURE = 2; // 대기압 0~255

	// define VER data item length
	public final static int LEN_VER_EVENT_CODE = 5; // VER 이벤트 코드 5바이트
	public final static int LEN_VER_OCCURRENCE_TIME = 15; // 정보발생일시 YYYYMMDDhhmmss

	// 외부 초단위 데이터 연계 방식에 따른 flag 값
	public final static int TFMSflag = 2; // TFMS 연계코드
	public final static int ANSANflag = 3; // 안산시 연계코드

	// user defined length
	public final static int LEN_VDR_RECORD = 81; // VDR data 한 로우의 길이
	public final static int MAX_VDR_ROW_COUNT = 300; // 최대 VDR data 로우 수. 5분간의 초단위 데이터
	public final static int LEN_VER_RECORD = 20; // VER data 한 로우의 길이

	public final static byte[] NULLByte = { 0x00 }; // NULL byte
	public final static String NULLString = new String(Const.NULLByte); // NULL String

	// server type
	public final static int COLLECT_SERVER_TYPE = 1;
	public final static int LOADBAL_SERVER_TYPE = 2;

	// 데이터를 만들어야 하는 타입 정리.
	public final static int EMPTY_TYPE = -1;
	public final static int ONLY_TRACE_TYPE = 0;
	public final static int ONLY_RAW_TYPE = 1;
	public final static int RAW_TFMS_TYPE = 2;
	public final static int RAW_ANSAN_TYPE = 3;
	public final static int RAW_SINYOUNG_TYPE = 4;
	public final static int ONLY_TFMS_TYPE = 22;
	public final static int ONLY_ANSAN_TYPE = 33;
	public final static int ONLY_SINYOUNG_TYPE = 44;

	public final static int TRACE_RAW_TYPE = 11;
	public final static int TRACE_RAW_TFMS_TYPE = 222;
	public final static int TRACE_RAW_ANSAN_TYPE = 333;
	public final static int TRACE_RAW_SINYOUNG_TYPE = 444;

	public final static String SVC_TRACE = "TRACE";
	public final static String SVC_RAW = "RAW";
	public final static String SVC_ANSAN = "ANSAN";
	public final static String SVC_TFMS = "TFMS";
	public final static String SVC_SHINYOUNG = "SHINYOUNG";
	// connection
	public final static String LOADBAL_NOWCONN_COUNT = "LOADBAL_NOWCONN_COUNT";
	public final static int TRACE_TABLE_FIELD_COUNT = 10;
	// oam
	public final static int LIMIT_DEAD_TIME = 60;
	// main timer
	public final static int CHECK_VERTICLE_TIMER = 5000;
	public final static int WAIT_VERTICLE_FOR_ALIVE = 60000;
	public final static int RT_VDE_INFO_FIELD_CNT = 20;

	// =============VDIS DTG HEADER META====================================
	public final static int LEN_VDIS_DTG_HEADER = 73;
	public final static int LEN_VDIS_DTG_DTGHEADER = 36;
	public final static int LEN_VDIS_DTG_DTGBODY_1ROW = 78;

	// =============VDIS DTG HEADER FIELD===================================
	public final static int LEN_VDIS_DTG_OPCODE = 2;
	public final static int LEN_VDIS_DTG_BODYLEN = 6;
	public final static int LEN_VDIS_DTG_SEQ = 6;
	public final static int LEN_VDIS_DTG_VINNUM = 17;
	public final static int LEN_VDIS_DTG_TYPE = 2;
	public final static int LEN_VDIS_DTG_CARNUM = 12;
	public final static int LEN_VDIS_DTG_BIZNUM = 10;
	public final static int LEN_VDIS_DTG_DRIVECODE = 18;

	// =============VDIS DTG HEADER FIELD===================================
	public final static int LEN_VDIS_DTG_MODELNAME = 20;
	public final static int LEN_VDIS_DTG_TRIPSEQ = 14;
	public final static int LEN_VDIS_DTG_DRIVEINFOCODE = 2;

	// =============VDIS DTG BODY FIELD=====================================
	public final static int LEN_VDIS_DTG_DAILY_MILEAGE_K = 4;
	public final static int LEN_VDIS_DTG_GROSS_ACC_MILEAGE_K = 7;
	public final static int LEN_VDIS_DTG_OCCURRENCE_TIME = 14;
	public final static int LEN_VDIS_DTG_SPEED = 3;
	public final static int LEN_VDIS_DTG_RPM = 4;
	public final static int LEN_VDIS_DTG_BRAKE = 1;
	public final static int LEN_VDIS_DTG_GPSX = 9;
	public final static int LEN_VDIS_DTG_GPSY = 9;
	public final static int LEN_VDIS_DTG_AZIMUTH = 3;
	public final static int LEN_VDIS_DTG_ACCX = 6;
	public final static int LEN_VDIS_DTG_ACCY = 6;
	public final static int LEN_VDIS_DTG_STATCODE = 2;
	public final static int LEN_VDIS_DTG_BATTERY_VOLTAGE = 5;
	public final static int LEN_VDIS_DTG_FUEL_CONSUMPTION = 5;

	// =============VDIS ISG HEADER META====================================
	public final static int LEN_VDIS_ISG_HEADER = 73;
	public final static int LEN_VDIS_ISG_DTGHEADER = 36;
	public final static int LEN_VDIS_ISG_DTGBODY_1ROW = 86;

	// =============VDIS ISG HEADER FIELD===================================
	public final static int LEN_VDIS_ISG_OPCODE = 2;
	public final static int LEN_VDIS_ISG_BODYLEN = 6;
	public final static int LEN_VDIS_ISG_SEQ = 6;
	public final static int LEN_VDIS_ISG_VINNUM = 17;
	public final static int LEN_VDIS_ISG_TYPE = 2;
	public final static int LEN_VDIS_ISG_CARNUM = 12;
	public final static int LEN_VDIS_ISG_BIZNUM = 10;
	public final static int LEN_VDIS_ISG_DRIVECODE = 18;

	// =============VDIS ISG DTG HEADER FIELD===================================
	public final static int LEN_VDIS_ISG_MODELNAME = 20;
	public final static int LEN_VDIS_ISG_TRIPSEQ = 14;
	public final static int LEN_VDIS_ISG_DRIVEINFOCODE = 2;

	// =============VDIS ISG DTG BODY FIELD=====================================
	public final static int LEN_VDIS_ISG_DAILY_MILEAGE_K = 4;
	public final static int LEN_VDIS_ISG_GROSS_ACC_MILEAGE_K = 7;
	public final static int LEN_VDIS_ISG_OCCURRENCE_TIME = 14;
	public final static int LEN_VDIS_ISG_SPEED = 3;
	public final static int LEN_VDIS_ISG_RPM = 4;
	public final static int LEN_VDIS_ISG_BRAKE = 1;
	public final static int LEN_VDIS_ISG_GPSX = 9;
	public final static int LEN_VDIS_ISG_GPSY = 9;
	public final static int LEN_VDIS_ISG_AZIMUTH = 3;
	public final static int LEN_VDIS_ISG_ACCX = 6;
	public final static int LEN_VDIS_ISG_ACCY = 6;
	public final static int LEN_VDIS_ISG_STATCODE = 2;
	public final static int LEN_VDIS_ISG_BATTERY_VOLTAGE = 5;
	public final static int LEN_VDIS_ISG_FUEL_CONSUMPTION = 5;
	public final static int LEN_VDIS_ISG_COOLING_WATER_TEMPERATURE = 3;
	public final static int LEN_VDIS_ISG_TRANSMISSION = 1;
	public final static int LEN_VDIS_ISG_ACC_PEDAL_ON_OFF = 1;
	public final static int LEN_VDIS_ISG_DRIVE_START_FLAG = 1;
	public final static int LEN_VDIS_ISG_ISG_STT_FLAG = 1;
	public final static int LEN_VDIS_ISG_ISG_ENGINE_FLAG = 1;

	// ==== file write key ==============================================
	public final static String TRACE_FILENAME = "TRACE";
	public final static String RAW_FILENAME = "RAW";
	public final static String ANSAN_FILENAME = "ANSAN";
	// VDIS-ISG
	public final static String VDIS_ISG_TRACE_FILENAME = "VDIS_ISG_TRACE";
	public final static String VDIS_ISG_FILENAME = "VDIS_ISG";
	// 2019-08-15 어린이 안심통합차량 사업 추가
	public final static String MOBILE_EVENT_FILENAME = "MOBILE_EVENT";
	public final static String MOBILE_TRACE_FILENAME = "MOBILE_TRACE";
	public final static String MOBILE_RAW_FILENAME = "MOBILE_RAW";

	// ==== EventBus name ================================================
	public final static String PHONE_ERR_TRACE_LOG_EVENTBUS = "PHONE_ERR_TRACE_LOG_EVENTBUS";
}
