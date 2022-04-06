package kr.co.kti.lcs.worker;

import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import kr.co.kti.lcs.common.Const;

/**
 * 작업 이력 로그 저장 Worker
 * 
 * @author yeski
 *
 */
public class TraceLogWorker extends AbstractVerticle
{
	private org.slf4j.Logger log = null;

	@Override
	public void start(Future<Void> startFuture)
	{
		log = LoggerFactory.getLogger("process.TraceLogWorker");
		log.debug("TraceLogWorker Start!!");

		vertx.eventBus().consumer(Const.EB_TRACE_LOG_WORKER, message -> {
			String strMessage = (String) message.body();

			log.debug(strMessage);
		});
	}

	@Override
	public void stop(Future<Void> startFuture)
	{
		log.debug("TraceLogWorker {} Stop!!", this.deploymentID());
	}
}
