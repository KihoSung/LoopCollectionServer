package kr.co.kti.lcs.worker;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;

import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import kr.co.kti.lcs.common.Const;
import kr.co.kti.lcs.common.DBConst;
import kr.co.kti.lcs.common.Util;
import kr.co.kti.lcs.jdbc.MySQLConnectionPool;
import kr.co.kti.lcs.service.DAOService;
import kr.co.kti.lcs.vo.LoopDtgINFOBody;

/**
 * DB 처리 Worker
 * 
 * @author yeski
 *
 */
public class DAOManagerWorker extends AbstractVerticle
{
	/**
	 * Define Log Value
	 */
	private org.slf4j.Logger log = null;

	/**
	 * Define DB INFO 
	 */
	private JsonObject DB_INFO;

	/**
	 * Define DAO Service
	 */
	private DAOService daoService;

	/**
	 * Loacal DB Connection
	 */
	private Connection conn;

	/**
	 * 서버 URL
	 */
	private String strUrl;
	
	/**
	 * 서버 사용자 ID
	 */
	private String strUser;
	
	/**
	 * 서버 사용자 비번
	 */
	private String strPassword;
	
	/**
	 * DB 연결 Driver Class
	 */
	private String strDriverClass;
	
	/**
	 * 초기 DB PoolSize
	 */
	private int intInitialPoolSize;
	
	/**
	 * 최대 DB PoolSize
	 */
	private int intMaxPoolSize;

	/**
	 * DAOManser Worker 시작
	 */
	@Override
	public void start(Future<Void> startFuture) throws Exception
	{
		// TODO Auto-generated method stub
		super.start(startFuture);

		log = LoggerFactory.getLogger("process.DAOManagerWorker");

		log.debug("DAOManagerWorker Start!!");

		JsonObject verticle_conf = config().getJsonObject(DAOManagerWorker.class.getName());
		this.DB_INFO = verticle_conf.getJsonObject("DB_INFO");

		this.strUrl = this.DB_INFO.getString("url");
		this.strUser = this.DB_INFO.getString("user");
		this.strPassword = this.DB_INFO.getString("password");
		this.strDriverClass = this.DB_INFO.getString("driver_class");
		this.intInitialPoolSize = this.DB_INFO.getInteger("initial_pool_size");
		this.intMaxPoolSize = this.DB_INFO.getInteger("max_pool_size");
		
		MySQLConnectionPool pool = new MySQLConnectionPool(this.strUrl, this.strUser, this.strPassword, this.intMaxPoolSize);

		this.daoService = new DAOService(pool);

		vertx.eventBus().consumer(Const.EB_DAO_MANAGER_WORKER, message -> {
			String strJobStatus = message.headers().get("JOB_STATUS");
			String strIdentityNo = message.headers().get("IDENTITY_NO");
			String strCommandId = message.headers().get("COMMAND_ID");

			Util.writeTraceLog(vertx, "[" + DAOManagerWorker.class.getName() + "] Request DAOService by JOB_STATUS [" + strJobStatus + "][" + strIdentityNo + "]");

			if (strCommandId != null && !strCommandId.equals(""))
			{
				// DB 결과에 대한 응답 처리 //
				switch (strCommandId)
				{
					case DBConst.COMMAND_SET_INFO:
						
						int intCount = this.procInfo(strJobStatus, strIdentityNo, strCommandId, message.body());

						if (intCount > 0)
						{
							Util.writeTraceLog(vertx, "[" + DAOManagerWorker.class.getName() + "] Select Count[" + intCount + "] DAOService by JOB_STATUS [" + strJobStatus + "][" + strIdentityNo + "]");

							message.reply("S");
						}
						else
						{
							Util.writeTraceLog(vertx, "[" + DAOManagerWorker.class.getName() + "] Select Count[" + intCount + "] DAOService by JOB_STATUS [" + strJobStatus + "][" + strIdentityNo + "]");
							
							message.reply("N");
						}

						break;
						
					case DBConst.COMMAND_GET_INFO:
						
						JsonObject joInfo = this.getTripInfo(strIdentityNo);
						
						message.reply(joInfo);
						
						break;
						
					case DBConst.COMMAND_SET_TRIPINFO:
						
						JsonObject joTripInfo = (JsonObject) message.body();
						
						this.daoService.setTripInfo(strIdentityNo, joTripInfo);
						message.reply("S");
						
						break;

					default:
						
						// message.reply(buffer);

						break;
				}
			}
			else
			{
				log.error("Wrong in DB Commnad ID|JOB_STATUS:" + strJobStatus + "|IDENTITY_NO:" + strIdentityNo + "|COMMAND_ID:" + strCommandId);
				Util.writeTraceLog(vertx, "[" + DAOManagerWorker.class.getName() + "] Wrong in DB Commnad ID [" + strIdentityNo + "] Command ID ERROR : " + strCommandId);
				
				message.reply("F");
			}
		});
	}

	/**
	 * INFO Data 처리 
	 * 
	 * @param strJobStatus
	 * @param strIdentityNo
	 * @param strCommandId
	 * @param bodyObject
	 * @return
	 */
	private int procInfo(String strJobStatus, String strIdentityNo, String strCommandId, Object bodyObject)
	{
		Buffer buf = (Buffer) bodyObject;

		int intCount = this.daoService.authIdentityNo(strIdentityNo);
		
		if (intCount > 0)
		{
			Util.writeTraceLog(vertx, "[" + DAOManagerWorker.class.getName() + "][" + strIdentityNo + "] Complete Parsing INFO Data from Buffer");
			LoopDtgINFOBody loopDtgINFOBody = new LoopDtgINFOBody(buf.getBytes());

			try
			{
				loopDtgINFOBody.doParse();
			}
			catch (UnsupportedEncodingException e)
			{
				// TODO Auto-generated catch block
				log.error("[LOOP_DTG_INFO_BODY][PARSE][ERROR] " + e.getMessage());
			}
			
			this.daoService.setLoopDtgInfo(strIdentityNo, loopDtgINFOBody);
		}
		else
		{
			// 청약없어 LOOP_INFO 삭제 //
			this.daoService.deleteLoopInfo(strIdentityNo);
		}

		return intCount;
	}
	
	/**
	 * 트립정보 Update 
	 * 
	 * @param strIdentityNo
	 * @return
	 */
	private JsonObject getTripInfo(String strIdentityNo)
	{
		JsonObject jo = this.daoService.getTripInfo(strIdentityNo);

		return jo;
	}

	/**
	 * DAOManager Worker 종료
	 */
	@Override
	public void stop(Future<Void> stopFuture) throws Exception
	{
		// TODO Auto-generated method stub
		super.stop(stopFuture);

		this.conn.close();

		log.debug("DAOManagerWorker {} Stop!!", this.deploymentID());
	}

}
