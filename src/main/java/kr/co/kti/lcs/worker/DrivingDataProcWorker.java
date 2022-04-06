package kr.co.kti.lcs.worker;

import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import kr.co.kti.lcs.common.Const;
import kr.co.kti.lcs.common.DBConst;
import kr.co.kti.lcs.common.Util;
import kr.co.kti.lcs.service.LoopDtgDTSSService;

/**
 * DTSS Data 처리 Worker
 * 
 * @author yeski
 *
 */
public class DrivingDataProcWorker extends AbstractVerticle
{
	private org.slf4j.Logger log = null;

	private byte[] byteServerIp = new byte[4];

	private final String REQ_COMMAND = Const.COMMAND_DTSS;
	private final String RES_COMMAND = Const.COMMAND_DTSS;

	@Override
	public void start(Future<Void> startFuture) throws Exception
	{
		// TODO Auto-generated method stub
		super.start(startFuture);

		log = LoggerFactory.getLogger("process.DrivingDataProcWorker");

		log.debug("DataProcWorker Start!!");

		vertx.eventBus().consumer(Const.EB_DRIVING_DATA_PROC_WORKER, message -> {
			String strJobStatus = message.headers().get("JOB_STATUS");
			String strServerIp = message.headers().get("SERVER_IP");
			String strIdentityNo = message.headers().get("IDENTITY_NO");
			String strClassName = message.headers().get("CLASS_NAME");

			Util.writeTraceLog(vertx, "[" + DrivingDataProcWorker.class.getName() + "] Reqeust 'DTSS' Command from Client [" + strIdentityNo + "]");

			this.byteServerIp = Util.serverIpToByte(strServerIp);

			if (strJobStatus != null && strJobStatus.equals(REQ_COMMAND))
			{

				// 데이터 Parse
				// 검증 성공
				Buffer dtssBuffer = (Buffer) message.body();

				log.debug("===============dtssBuffer===============>" + dtssBuffer.length());

				DeliveryOptions tripOption = new DeliveryOptions();
				tripOption.addHeader("IDENTITY_NO", strIdentityNo);
				tripOption.addHeader("JOB_STATUS", strJobStatus);
				tripOption.addHeader("COMMAND_ID", DBConst.COMMAND_GET_INFO);

				vertx.eventBus().send(Const.EB_DAO_MANAGER_WORKER, null, tripOption, rpy -> {
					if (rpy.succeeded())
					{
						//
						JsonObject joInfo = (JsonObject) rpy.result().body();

						if (joInfo.getString("DVC_ID") != null && joInfo.getString("DVC_ID").equals(strIdentityNo))
						{
							//
							// log.debug("Reqest Bytes:" + Util.byteArrayToHex(dtssBuffer.getBytes()));

							LoopDtgDTSSService loopDtgDTSSService = new LoopDtgDTSSService(vertx, joInfo);
							loopDtgDTSSService.setBuffer(dtssBuffer);

							if (loopDtgDTSSService.doParse())
							{
								Util.writeTraceLog(vertx, "[" + DrivingDataProcWorker.class.getName() + "] Reqeust 'DTSS' Command from Client [" + strIdentityNo + "] Processed Complete by DTSS from Buffer");

								// Success from Data Parsing
								// Send to FileCreateWorker
								DeliveryOptions fileCreateDeliveryOptions = new DeliveryOptions();
								fileCreateDeliveryOptions.addHeader("JOB_STATUS", strJobStatus);
								fileCreateDeliveryOptions.addHeader("IDENTITY_NO", strIdentityNo);
								fileCreateDeliveryOptions.addHeader("FILE_NAME", Util.getFileName(Const.MOBILE_RAW_FILENAME, strIdentityNo, loopDtgDTSSService.getCarRegNo()));

								vertx.eventBus().send(Const.EB_FILE_CREATE_WORKER, loopDtgDTSSService.getBufferForDTG(), fileCreateDeliveryOptions, reply -> {

									byte[] byteResponse;
									String strResCode = "00";

									if (reply.succeeded())
									{
										if (Util.responseStatus((String) reply.result().body()))
										{
											// 날짜변경(0시0분0초) 트립 나누기로 새로운 데이터가 있을 경우만 파일 추가 생성 //
											if (loopDtgDTSSService.getDataTwoSize() > 0)
											{
												Util.sleepTime(200);	// 없어도 될것 같지만 그래도 생성 시간의 차이를 만들기 위해서 Sleep 실행 //
												
												vertx.eventBus().send(Const.EB_FILE_CREATE_WORKER, loopDtgDTSSService.getBufferForDTGTwo(), fileCreateDeliveryOptions);
											}

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
										Util.writeTraceLog(vertx, "[" + DrivingDataProcWorker.class.getName() + "] Reqeust 'DTSS' Command from Client [" + strIdentityNo + "] Failed in Rqeuset FileCreateWorker");
									}

									Util.writeTraceLog(vertx, "[" + DrivingDataProcWorker.class.getName() + "] Reqeust 'DTSS' Command from Client [" + strIdentityNo + "] Response Code : " + strResCode);

									message.reply(byteResponse);
								});
							}
							else
							{
								Util.writeTraceLog(vertx, "[" + DrivingDataProcWorker.class.getName() + "] Reqeust 'DTSS' Command from Client [" + strIdentityNo + "] Failed in Process by DTSS from Buffer");

								// Error from Data Parsing
								message.reply(Util.makeResponse(byteServerIp, RES_COMMAND, "00"));
							}
						}
						else
						{
							Util.writeTraceLog(vertx, "[" + DrivingDataProcWorker.class.getName() + "] Reqeust 'DTSS' Command from Client [" + strIdentityNo + "] None LOOP_INFO in Process by DTSS from Buffer");

							message.reply(Util.makeResponse(byteServerIp, RES_COMMAND, "00"));
						}
					}
					else
					{
						Util.writeTraceLog(vertx, "[" + DrivingDataProcWorker.class.getName() + "] Reqeust 'DTSS' Command from Client [" + strIdentityNo + "] Failed DB in Process by DTSS from Buffer");
						//
						message.reply(Util.makeResponse(byteServerIp, RES_COMMAND, "00"));
					}
				});
			}
		});
	}

	@Override
	public void stop(Future<Void> stopFuture) throws Exception
	{
		// TODO Auto-generated method stub
		super.stop(stopFuture);

		log.debug("DataProcWorker {} Stop!!", this.deploymentID());
	}

}
