package kr.co.kti.lcs.verticle;

import java.io.UnsupportedEncodingException;

import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.NetSocket;
import kr.co.kti.lcs.common.Const;
import kr.co.kti.lcs.common.Util;

/**
 * DTG Socket Server Verticle 
 * 
 * @author yeski
 *
 */
public class DTGServerVerticle extends AbstractVerticle
{
	/**
	 * Define Log Value
	 */
	private org.slf4j.Logger log = null;

	/**
	 * Define Socket
	 */
	private NetSocket netSocket;

	/**
	 * Define 서버 IP
	 */
	private String strServerIp;
	
	/**
	 * Define 응답 IP
	 */
	private String strResIp;
	
	/**
	 * Define 서버 Port
	 */
	private int intServerPort;
	
	/**
	 * Define 서버 최대 Connection 수
	 */
	private int intMaxConn;

	/**
	 * 루프 Socket 서버 Verticle 시작
	 */
	@Override
	public void start(Future<Void> startFuture) throws Exception
	{
		// TODO Auto-generated method stub
		super.start(startFuture);

		log = LoggerFactory.getLogger("process.DTGServerVerticle");

		log.debug("DTGServerVerticle Start!!");

		JsonObject verticle_conf = config().getJsonObject(DTGServerVerticle.class.getName());
		JsonObject deploy_info = verticle_conf.getJsonObject("DEPLOY_INFO");

		this.strResIp = deploy_info.getString("RES_IP");
		this.strServerIp = deploy_info.getString("IP");
		this.intServerPort = deploy_info.getInteger("PORT");
		this.intMaxConn = deploy_info.getInteger("MAXCONN");

		/**
		 * Initial Loop DTG Server
		 */
		initServer();

		// startFuture.complete();
	}

	/**
	 * Define 수신 Buffer
	 */
	private Buffer revBuffer = Buffer.buffer();

	/**
	 * Instance Server
	 */
	private void initServer()
	{
		log.debug("Initial LOOP DTG Socket Server!!");

		log.debug("SERVER_IP : " + this.strServerIp);
		log.debug("SERVER_PORT : " + this.intServerPort);
		log.debug("MAX_CONN : " + this.intMaxConn);

		NetServerOptions netServerOption = new NetServerOptions();

		netServerOption.setAcceptBacklog(5);
		netServerOption.setReuseAddress(true);
		netServerOption.setTcpNoDelay(true);
		netServerOption.setTcpKeepAlive(false);
		netServerOption.setIdleTimeout(30);
		netServerOption.setReceiveBufferSize(8192);
		netServerOption.setHost(this.strServerIp);
		netServerOption.setPort(this.intServerPort);

		NetServer netServer = vertx.createNetServer(netServerOption);

		netServer.connectHandler(socket -> {
			this.netSocket = socket;

			this.netSocket.handler(recvBuf -> {
				log.debug("Recv data from client");

				if (recvBuf.length() > 2)
				{
					this.revBuffer.appendBuffer(recvBuf);
					
					Buffer bufTemp = recvBuf.getBuffer(recvBuf.length() - 2, recvBuf.length());
					String strTemp = Util.byteArrayToHex(bufTemp.getBytes());
					
					if (strTemp.equals("cfd0"))
					{
						this.procData(this.revBuffer);
						this.revBuffer = Buffer.buffer();
					}
				}				
			});

			this.netSocket.closeHandler(rst -> {
				log.debug("socket is closed");
			});
		});

		netServer.listen(rst -> {
			if (rst.succeeded())
			{
				log.debug("Succ listening..");
			}
		});
	}

	/**
	 * 수신된 Buffer Data 처리 
	 * 
	 * @param buffer
	 */
	private void procData(Buffer buffer)
	{
		// Command 에 따라서 데이터 분기 처리
		// INFO(RECV Command : DTSC), DTSS, EVSS

		String strCommand = this.getCommand(buffer);
		int intIdentityNo = this.getIdentityNo(buffer);
		String strIdentityNo = "0" + Long.toString(intIdentityNo);
		Buffer resBuffer = Buffer.buffer();

		log.debug("====================buffer==========>" + buffer.length());

		switch (strCommand)
		{
			case "INFO": // DTG Header 데이터 전달 응답 Command : DTSC //
				Util.writeTraceLog(vertx, "[" + DTGServerVerticle.class.getName() + "] Reqeust 'INFO' Command from Client [" + strIdentityNo + "]");

				DeliveryOptions infoDeliveryOptions = new DeliveryOptions();
				infoDeliveryOptions.addHeader("JOB_STATUS", "INFO");
				infoDeliveryOptions.addHeader("SERVER_IP", this.strResIp);
				infoDeliveryOptions.addHeader("IDENTITY_NO", strIdentityNo);

				vertx.eventBus().send(Const.EB_AUTH_WORKER, buffer, infoDeliveryOptions, message -> {

					byte[] byteResponse;

					Util.writeTraceLog(vertx, "[" + DTGServerVerticle.class.getName() + "] Response 'DTCS' Command to Client [" + strIdentityNo + "]");

					if (message.succeeded())
					{
						// 인증 성공 응답 처리
						byteResponse = (byte[]) message.result().body();
					}
					else
					{
						// Client에게 Not Ready 전달 (0x00)
						byteResponse = Util.makeResponse(Util.serverIpToByte(this.strResIp), Const.COMMAND_DTSC, "00");
					}

					log.debug("RES Buffer : " + Util.byteArrayToHex(byteResponse));

					resBuffer.appendBytes(byteResponse);

					this.socketWrite(resBuffer);
				});

				break;

			case "DTSS": // DTG 운행 파일 전달 //
				Util.writeTraceLog(vertx, "[" + DTGServerVerticle.class.getName() + "] Reqeust 'DTSS' Command from Client [" + strIdentityNo + "]");

				DeliveryOptions dtssDeliveryOptions = new DeliveryOptions();
				dtssDeliveryOptions.addHeader("JOB_STATUS", "DTSS");
				dtssDeliveryOptions.addHeader("SERVER_IP", this.strResIp);
				dtssDeliveryOptions.addHeader("IDENTITY_NO", strIdentityNo);

				vertx.eventBus().send(Const.EB_DRIVING_DATA_PROC_WORKER, buffer, dtssDeliveryOptions, message -> {

					byte[] byteResponse;

					Util.writeTraceLog(vertx, "[" + DTGServerVerticle.class.getName() + "] Response 'DTSS' Command from Client [" + strIdentityNo + "]");

					if (message.succeeded())
					{
						// 인증 성공 응답 처리
						byteResponse = (byte[]) message.result().body();
					}
					else
					{
						// Client에게 Not Ready 전달 (0x00)
						byteResponse = Util.makeResponse(Util.serverIpToByte(this.strResIp), Const.COMMAND_DTSC, "00");
					}

					resBuffer.appendBytes(byteResponse);

					this.socketWrite(resBuffer);
				});

				break;

			case "EVSS": // Event 실시간 데이터(운행 일부 데이터) 전달 //
				Util.writeTraceLog(vertx, "[" + DTGServerVerticle.class.getName() + "] Reqeust 'EVSS' Command from Client [" + strIdentityNo + "]");

				DeliveryOptions evssDeliveryOptions = new DeliveryOptions();
				evssDeliveryOptions.addHeader("JOB_STATUS", "EVSS");
				evssDeliveryOptions.addHeader("SERVER_IP", this.strResIp);
				evssDeliveryOptions.addHeader("IDENTITY_NO", strIdentityNo);

				vertx.eventBus().send(Const.EB_EVENT_DATA_PROC_WORKER, buffer, evssDeliveryOptions, message -> {

					byte[] byteResponse;

					if (message.succeeded())
					{
						// 인증 성공 응답 처리
						byteResponse = (byte[]) message.result().body();
					}
					else
					{
						// Client에게 Not Ready 전달 (0x00)
						byteResponse = Util.makeResponse(Util.serverIpToByte(this.strResIp), Const.COMMAND_DTSC, "00");
					}

					resBuffer.appendBytes(byteResponse);

					this.socketWrite(resBuffer);
				});

				break;

			default:
				break;
		}
	}

	/**
	 * 수신된 운행 Buffer에서 Command 추출하기 
	 * 
	 * @param buffer
	 * @return
	 */
	private String getCommand(Buffer buffer)
	{
		String strReturn = "";

		try
		{
			strReturn = Util.getCommand(buffer.getBytes());
			log.debug("strReturn:" + strReturn);
		}
		catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			log.error("[Get Command ERROR] " + e.getMessage());
		}

		return strReturn;
	}

	/**
	 * 수신된 운행 Buffer에서 회선번호 추출하기 
	 * 
	 * @param buffer
	 * @return
	 */
	private int getIdentityNo(Buffer buffer)
	{
		int intReturn = 0;

		intReturn = Util.getInteger(buffer.getBytes(), 4, 4, "L");

		return intReturn;
	}

	/**
	 * Write Socket Buffer
	 * 
	 * @param buffer
	 */
	private void socketWrite(Buffer buffer)
	{
		if (this.netSocket != null && buffer != null)
		{
			log.debug("Write Buffer : " + Util.byteArrayToHex(buffer.getBytes()));

			Util.writeTraceLog(vertx, Util.byteArrayToHex(buffer.getBytes()));

			this.netSocket.write(buffer);
		}
	}

	/**
	 * 루프 Socket 서버 Verticle 종료
	 */
	@Override
	public void stop(Future<Void> stopFuture) throws Exception
	{
		// TODO Auto-generated method stub
		super.stop(stopFuture);

		log.debug("DTGServerVerticle {} Stop!!", this.deploymentID());
	}

}
