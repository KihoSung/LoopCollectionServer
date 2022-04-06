package kr.co.kti.lcs.vo;

/**
 * DTSC 포함 Server에서 DTG 로 전달하는 Data 처리 VO
 * 
 * @author yeski
 *
 */
public class LoopServerToDtgBody extends LoopDtgFrame
{
	/**
	 * Server to DTG 전송 응답 데이타 (DTSC 포함)
	 * 
	 * 0x00 : Not Ready (값:0) 0x01 : Data 전송 요청 (값:1)
	 */
	private int Data;
	
	/**
	 * 서버 IP
	 */
	private String ServerIp;

	public int getData()
	{
		return Data;
	}

	public void setData(int data)
	{
		Data = data;
	}

	public String getServerIp()
	{
		return ServerIp;
	}

	public void setServerIp(String serverIp)
	{
		ServerIp = serverIp;
	}

}
