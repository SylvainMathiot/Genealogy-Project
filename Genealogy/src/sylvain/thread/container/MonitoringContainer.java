package sylvain.thread.container;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.Arrays;

import javax.swing.event.EventListenerList;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sylvain.controller.LoggerSingleton;
import sylvain.controller.PropertiesSingleton;
import sylvain.controller.WatchServiceSingleton;
import sylvain.model.DataModel;
import sylvain.model.Person;
import sylvain.thread.event.MonitoringEventInterface;
import sylvain.thread.event.PersonAddedEvent;
import sylvain.thread.event.PersonModifiedEvent;
import sylvain.thread.listener.MonitoringListener;
/**
 * 
 * @author Sylvain Mathiot
 *
 */
public class MonitoringContainer implements Serializable{
	private static final Logger logger = LogManager.getLogger(LoggerSingleton.getInstance().getLoggerName(MonitoringContainer.class));
	private static final long serialVersionUID = 4863785957098726655L;
	private transient EventListenerList listeners;
	
	public MonitoringContainer(){
		readResolve();
	}
	
	private MonitoringContainer readResolve(){
		listeners = new EventListenerList();
		return this; 
	}
	
	private MonitoringListener[] getMonitoringListeners(){
		return listeners.getListeners(MonitoringListener.class);
	}
	
	public void addMonitoringListener(MonitoringListener monitoringListener){
		listeners.add(MonitoringListener.class, monitoringListener);
	}
	
	protected void firePersonAddedEvent(MonitoringEventInterface event){
		Arrays.asList(getMonitoringListeners()).stream().filter(listener -> event instanceof PersonAddedEvent).forEach(listener -> listener.personAdded((PersonAddedEvent)event));
	}
	
	protected void firePersonModifiedEvent(MonitoringEventInterface event){
		Arrays.asList(getMonitoringListeners()).stream().filter(listener -> event instanceof PersonModifiedEvent).forEach(listener -> listener.personModified((PersonModifiedEvent)event));
	}
	
	public void monitor() throws IOException, InterruptedException{
	    WatchKey watchKey;
	    Path child;
	    boolean valid = true;
	    
	    do{
	    	watchKey = WatchServiceSingleton.getInstance().getWatchService().take();
	    	final Path dir = WatchServiceSingleton.getInstance().getKey(watchKey);
			if(dir == null){
				logger.error("WatchKey " + watchKey + " not recognized!");
				continue;
			}
			
			for(WatchEvent<?> event : watchKey.pollEvents()){
				Thread.sleep(2000);
				child = dir.resolve((Path)event.context());
				
				if(ENTRY_CREATE.equals(event.kind())){
					if(Files.isDirectory(child)){
						if (StringUtils.isNumeric(child.getName(child.getNameCount()-1).toFile().getName().subSequence(0, 4))){
							Person person = DataModel.getInstance().add(child.toFile());
							firePersonAddedEvent(new PersonAddedEvent(person));
						}
						WatchServiceSingleton.getInstance().getRegister().accept(child);
					}
					
					if(Files.isRegularFile(child) && child.getName(child.getNameCount()-1).toString().equalsIgnoreCase(PropertiesSingleton.getInstance().get("IDENTITY.FILENAME"))){
						Person person = DataModel.getInstance().add(child.getParent().toFile());
						firePersonAddedEvent(new PersonAddedEvent(person));
					}
				}
				
				if(ENTRY_MODIFY.equals(event.kind()) && Files.isRegularFile(child) && child.getName(child.getNameCount()-1).toString().equalsIgnoreCase(PropertiesSingleton.getInstance().get("IDENTITY.FILENAME"))){
					Person person = DataModel.getInstance().add(child.getParent().toFile());
					firePersonModifiedEvent(new PersonModifiedEvent(person));
				}
				
				if(ENTRY_DELETE.equals(event.kind())){
					
				}
			}
			
			valid = watchKey.reset();
		}while(valid);
	}
}
