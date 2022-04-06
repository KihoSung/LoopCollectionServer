package kr.co.kti.lcs.worker;

import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.DeliveryOptions;
import kr.co.kti.lcs.common.Const;
import kr.co.kti.lcs.common.DBConst;
import kr.co.kti.lcs.common.Util;

/**
 * 단말기 인증 확인 Worker
 * 
 * @author yeski
 *
 */
public class AuthWorker extends AbstractVerticle
{
	/**
	 * Define Log Value
	 */
	private org.slf4j.Logger log = null;

	/**
	 * 서버 IP Bytes
	 */
	private byte[] byteServerIp = new byte[4];

	/**
	 * 단말기 요청 Command ID
	 */
	private final String REQ_COMMAND = Const.COMMAND_INFO;
	
	/**
	 * 서버 응답 Command ID
	 */
	private final String RES_COMMAND = Const.COMMAND_DTSC;

	/**
	 * 인증 Worker 시작
	 */
	@Override
	public void start(Future<Void> startFuture) throws Exception
	{
		// TODO Auto-generated method stub
		super.start(startFuture);

		log = LoggerFactory.getLogger("process.AuthWorker");

		log.debug("AuthWorker Start!!");

		vertx.eventBus().consumer(Const.EB_AUTH_WORKER, message -> {
			String strJobStatus = message.headers().get("JOB_STATUS");
			String strServerIp = message.headers().get("SERVER_IP");
			String strIdentityNo = message.headers().get("IDENTITY_NO");
			String strClassName = message.headers().get("CLASS_NAME");

			this.byteServerIp = Util.serverIpToByte(strServerIp);

			if (strJobStatus != null && strJobStatus.equals(REQ_COMMAND))
			{

				Buffer infoBuffer = (Buffer) message.body();

				// this.infoMap.put(strIdentityNo, loopDtgINFOBody);

				// DAO 해당 단말기 인증 요청 //
				DeliveryOptions daoDeliveryOptions = new DeliveryOptions();
				daoDeliveryOptions.addHeader("JOB_STATUS", strJobStatus);
				daoDeliveryOptions.addHeader("IDENTITY_NO", strIdentityNo);
				daoDeliveryOptions.addHeader("COMMAND_ID", DBConst.COMMAND_SET_INFO);

				Util.writeTraceLog(vertx, "[" + AuthWorker.class.getName() + "] Reqeust 'INFO' Command from Client [" + strIdentityNo + "] Request DAOService by COMMAND_SET_INFO");

				vertx.eventBus().send(Const.EB_DAO_MANAGER_WORKER, infoBuffer, daoDeliveryOptions, reply -> {

					byte[] byteResponse;

					String strResCode = "00";

					if (reply.succeeded())
					{						
						// DAO 에 차량 인증 데이터 확인
						if (Util.responseStatus((String) reply.result().body()))
						{
							strResCode = "01";
							byteResponse = Util.makeResponse(byteServerIp, RES_COMMAND, strResCode);
						}
						else
						{
							byteResponse = Util.makeResponse(byteServerIp, RES_COMMAND, strResCode);
						}
					}
					else
					{
						byteResponse = Util.makeResponse(byteServerIp, RES_COMMAND, strResCode);

						// DAO 조회 실패
						log.error("Failed in Rqeuset DAOManagerWorker|JOB_STATUS:" + strJobStatus + "|CLASS_NAME:" + strClassName + "|IDENTITY_NO:" + strIdentityNo);
						Util.writeTraceLog(vertx, "[" + AuthWorker.class.getName() + "] Reqeust 'INFO' Command from Client [" + strIdentityNo + "] Failed in Rqeuset DAOManagerWorker");
					}

					Util.writeTraceLog(vertx, "[" + AuthWorker.class.getName() + "] Reqeust 'INFO' Command from Client [" + strIdentityNo + "] Response Code : " + strResCode);

					message.reply(byteResponse);
				});

			}
			else
			{
				// 잘못된 Command 요청
				log.error("Wrong in Request Job Status|JOB_STATUS:" + strJobStatus + "|CLASS_NAME:" + strClassName + "|IDENTITY_NO:" + strIdentityNo);
				Util.writeTraceLog(vertx, "[" + AuthWorker.class.getName() + "]Reqeust 'INFO' Command from Client[" + strIdentityNo + "] Job Status ERROR : " + strJobStatus);
			}
		});

	}

	/**
	 * 인증 Worker 종료
	 */
	@Override
	public void stop(Future<Void> stopFuture) throws Exception
	{
		// TODO Auto-generated method stub
		super.stop(stopFuture);

		log.debug("AuthWorker {} Stop!!", this.deploymentID());
	}

}
