package kr.co.kti.lcs.verticle;

import java.util.List;

import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import kr.co.kti.lcs.common.Const;

/**
 * Loop 단말기 서버 Main Verticle (worker 구동)
 * 
 * @author yeski
 *
 */
public class MainVerticle extends AbstractVerticle
{
	/**
	 * Define Log Value
	 */
	private org.slf4j.Logger log = null;
	
	/**
	 * Define Worker Array
	 */
	private JsonArray verticle_array = null;

	/**
	 * 메인 Verticle 시작
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void start(Future<Void> startFuture) throws Exception
	{
		log = LoggerFactory.getLogger("process.MainVerticle");

		log.debug("MainVerticle Start!!");

		verticle_array = config().getJsonArray("DEPLOY_VERTICLES");

		if (null == verticle_array)
		{
			log.error("Verticle array 정보가 없습니다.[DEPLOY_VERTICLES]");
			startFuture.fail("Verticle array 정보가 없습니다.[DEPLOY_VERTICLES]");
			this.Exit(2);
		}

		List<String> verticle_name_list = verticle_array.copy().getList();	// 복사본을// 가져온다.// 

		log.debug("verticle list count : " + verticle_name_list.size());
		this.StartVerticles(verticle_name_list);

		// 5 sec cycle
		vertx.setPeriodic(Const.CHECK_VERTICLE_TIMER, checkHandler -> {
			// this.checkReloading();
			this.CheckSystemStop();
		});

		startFuture.complete();

	}

	/**
	 * Main Verticle 종료
	 */
	@Override
	public void stop(Future<Void> startFuture)
	{
		log.debug("MainVerticle {} Stop!!", this.deploymentID());

		startFuture.complete();
	}

	/**
	 * VertX 종료 처리
	 * 
	 * @param arg
	 */
	private void Exit(int arg)
	{
		vertx.close();
		System.exit(arg);
	}

	/**
	 * Run Worker 
	 * 
	 * @param verticle_name_list
	 */
	private void StartVerticles(List<String> verticle_name_list)
	{

		if (null == verticle_name_list || verticle_name_list.size() == 0)
		{
			return;
		}

		String verticle_name = verticle_name_list.remove(0);

		JsonObject verticle_conf = config().getJsonObject(verticle_name);
		if (null == verticle_conf)
		{// verticle_name 이름의 jsonobj 가 없다면 false
			// return
			log.error("Verticle conf 가 없습니다. : " + verticle_name);
			this.Exit(2);
		}

		JsonObject deploy_info = verticle_conf.getJsonObject("DEPLOY_INFO");// deployment
																			// 정보를
																			// 가져온다.
		if (null == deploy_info)
		{// deployment 정보가 없다면 false return
			log.error("Verticle deployment 정보가 없습니다.[DEPLOY_INFO] : " + verticle_name);
			this.Exit(2);
		}

		boolean isWorker = deploy_info.getBoolean("IS_WORKER", false); // worker
																		// 인가?,
																		// default
																		// false
		int instance_num = deploy_info.getInteger("INSTANCE_NUM", 1); // verticle
																		// instance
																		// 갯수,
																		// defatult
																		// 1

		DeploymentOptions options = new DeploymentOptions().setConfig(config()).setWorker(isWorker).setInstances(instance_num);

		vertx.deployVerticle(verticle_name, options, res -> {
			if (res.succeeded())
			{
				log.debug("Deploy Success : " + verticle_name + " : " + res.result());
				this.StartVerticles(verticle_name_list);
			}
			else
			{
				log.error("Deploy Fail : " + verticle_name + " " + res.cause().toString());
				this.Exit(2);
			}
		});
	}

	/**
	 * 서버 종료를 위한 파일 확인
	 */
	private void CheckSystemStop()
	{

		vertx.fileSystem().readDir("/data", rstList -> {
			if (rstList.succeeded())
			{
				for (int i = 0; i < rstList.result().size(); i++)
				{
					if (rstList.result().get(i).indexOf("LOOPSYSTEMSTOP.down") > 0)
					{
						log.debug("Processing System down ..   Find LOOPSYSTEMSTOP.down File..");
						vertx.fileSystem().deleteBlocking("/data/LOOPSYSTEMSTOP.down");
						this.Exit(0);
					}
				}
			}
			else
			{
				log.debug("No file found from /data");
			}
		});
	}
}
