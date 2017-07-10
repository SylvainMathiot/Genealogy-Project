package sylvain.thread.runnable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sylvain.controller.LoggerSingleton;
import sylvain.thread.container.MonitoringContainer;

/**
 * 
 * @author Sylvain Mathiot
 *
 */
public class MonitoringThread implements Runnable {
	private static final Logger logger = LogManager.getLogger(LoggerSingleton.getInstance().getLoggerName(MonitoringThread.class));
	private MonitoringContainer monitoringContainer;
	
	public MonitoringThread(MonitoringContainer monitoringContainer){
		super();
		this.monitoringContainer = monitoringContainer;
	}
	
	public void run(){
		try{
			Thread.currentThread().setName(this.getClass().getSimpleName());
			
			monitoringContainer.monitor();
		}catch(Exception e){
			logger.error(e);
		}
	}
}
