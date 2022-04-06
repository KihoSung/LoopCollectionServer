package kr.co.kti.lcs.worker;

import java.io.File;

import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import kr.co.kti.lcs.common.Const;
import kr.co.kti.lcs.common.Util;

/**
 * 파일 생성 처리 Worker 
 * 
 * @author yeski
 *
 */
public class FileCreateWorker extends AbstractVerticle
{
	private org.slf4j.Logger log = null;

	private String strTracePath;
	private String strMobileRawPath;
	private String strFileTemp;

	private boolean isDebug = true;

	@Override
	public void start(Future<Void> startFuture) throws Exception
	{
		// TODO Auto-generated method stub
		super.start(startFuture);

		log = LoggerFactory.getLogger("process.FileCreateWorker");

		log.debug("FileCreateWorker Start!!");

		this.strTracePath = config().getString("TRACEDATA_PATH");
		this.strMobileRawPath = config().getString("MOBILERAWDATA_PATH");
		this.strFileTemp = config().getString("TEMP_PATH");

		vertx.eventBus().consumer(Const.EB_FILE_CREATE_WORKER, message -> {

			String strJobStatus = message.headers().get("JOB_STATUS");
			String strIdentityNo = message.headers().get("IDENTITY_NO");
			String strFilePath = message.headers().get("FILE_NAME");

			if (strJobStatus != null && strJobStatus.equals("DTSS"))
			{
				Util.writeTraceLog(vertx, "[" + FileCreateWorker.class.getName() + "] Reqeust 'DTSS' Command from Client [" + strIdentityNo + "]");

				Buffer bufData = (Buffer) message.body();

				// 파일 생성 (경로 확인)
				String rstfile = this.strMobileRawPath + File.separator + strFilePath + ".raw";
				String file_path = this.strMobileRawPath + File.separator + "temp" + File.separator + strFilePath + ".raw";

				// debug 확인
				String temp_path = this.strFileTemp + File.separator + "temp" + File.separator + strFilePath + ".raw";

				// 파일 쓰기
				final String temp_filename = file_path;
				final String result_filename = rstfile;

				vertx.fileSystem().writeFile(temp_filename, bufData, reply -> {

					if (reply.succeeded())
					{
						Util.writeTraceLog(vertx, "[" + FileCreateWorker.class.getName() + "] Reqeust 'DTSS' Command from Client [" + strIdentityNo + "] Created Temp File : " + temp_filename);

						// log.debug(" File write Success : " + temp_filename);
						// log.debug(" Try File move : " + temp_filename + " -> "+result_filename);
						vertx.fileSystem().move(temp_filename, result_filename, handler -> {
							if (handler.succeeded())
							{
								log.debug(" File move Success : " + result_filename);

								Util.writeTraceLog(vertx, "[" + FileCreateWorker.class.getName() + "] Reqeust 'DTSS' Command from Client [" + strIdentityNo + "] Completed Moving in Create File : " + result_filename);

								message.reply("S");
							}
							else
							{
								log.error(" File write Failed : " + result_filename);

								Util.writeTraceLog(vertx, "[" + FileCreateWorker.class.getName() + "] Reqeust 'DTSS' Command from Client [" + strIdentityNo + "] Failed Moving in Create File : " + result_filename);

								message.reply("F");
							}
						});
					}
					else
					{
						log.error(" File write Failed : " + temp_filename + " : " + reply.cause().getMessage());

						Util.writeTraceLog(vertx, "[" + FileCreateWorker.class.getName() + "] Reqeust 'DTSS' Command from Client [" + strIdentityNo + "] Failed in Create Temp File : " + temp_filename);

						message.reply("F");
					}
				});

				if (isDebug)
				{
					// temp file 만들기 debug 용 나중에 지우세요 //
					vertx.fileSystem().writeFile(temp_path, bufData, tempReply -> {
						if (tempReply.succeeded())
						{
							Util.writeTraceLog(vertx, "[" + FileCreateWorker.class.getName() + "] Reqeust 'DTSS' Command from Client [" + strIdentityNo + "] Created Debug File : " + temp_path);
						}
						else
						{
							Util.writeTraceLog(vertx, "[" + FileCreateWorker.class.getName() + "] Reqeust 'DTSS' Command from Client [" + strIdentityNo + "] Failed in Create Debug File : " + temp_path);
						}
					});
				}
			}

		});
	}

	@Override
	public void stop(Future<Void> stopFuture) throws Exception
	{
		// TODO Auto-generated method stub
		super.stop(stopFuture);

		log.debug("FileCreateWorker {} Stop!!", this.deploymentID());
	}

}
