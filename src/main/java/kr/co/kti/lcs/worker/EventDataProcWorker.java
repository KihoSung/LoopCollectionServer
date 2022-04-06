package kr.co.kti.lcs.worker;

import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import kr.co.kti.lcs.common.Const;

/**
 * EVSS Data 처리 Worker (현재는 사용하지 않음)
 * 
 * @author yeski
 *
 */
public class EventDataProcWorker extends AbstractVerticle
{
	/**
	 * Define Log Value
	 */
	private org.slf4j.Logger log = null;

	/**
	 * 이벤트 처리 Worker 시작
	 */
	@Override
	public void start(Future<Void> startFuture) throws Exception
	{
		// TODO Auto-generated method stub
		super.start(startFuture);
		
		log = LoggerFactory.getLogger("process.EventDataProcWorker");

		log.debug("EventDataProcWorker Start!!");
		
		vertx.eventBus().consumer(Const.EB_EVENT_DATA_PROC_WORKER, message -> {
			
		});
	}

	/**
	 * 이벤트 처리 Worker 종료
	 */
	@Override
	public void stop(Future<Void> stopFuture) throws Exception
	{
		// TODO Auto-generated method stub
		super.stop(stopFuture);
		
		log.debug("EventDataProcWorker {} Stop!!", this.deploymentID());
	}
	
}
