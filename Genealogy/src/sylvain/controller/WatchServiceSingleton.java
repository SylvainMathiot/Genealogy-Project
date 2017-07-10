package sylvain.controller;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.nio.file.SensitivityWatchEventModifier;

/**
 * 
 * @author Sylvain Mathiot
 *
 */
public class WatchServiceSingleton {
	private static final Logger logger = LogManager.getLogger(LoggerSingleton.getInstance().getLoggerName(WatchServiceSingleton.class));
	private static WatchServiceSingleton INSTANCE;
	private File rootFolder = new File(PropertiesSingleton.getInstance().get("DATABASE.FOLDER"));
	private WatchService watchService;
	private Map<WatchKey, Path> keys;
	private Consumer<Path> register;
	
	private WatchServiceSingleton(){
		try{
			watchService = FileSystems.getDefault().newWatchService();
			keys = new HashMap<>();
			
			register = p -> {
	            if(!p.toFile().exists() || !p.toFile().isDirectory()){
	                throw new RuntimeException("Folder " + p + " does not exist or is not a directory");
	            }
	            try{
	                Files.walkFileTree(p, new SimpleFileVisitor<Path>(){
	                    @Override
	                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    		logger.debug("Registering folder : " + dir);
                    		WatchKey watchKey = dir.register(watchService, new WatchEvent.Kind[]{ENTRY_CREATE,ENTRY_MODIFY,ENTRY_DELETE}, SensitivityWatchEventModifier.HIGH);
                    		keys.put(watchKey, dir);
	                        return FileVisitResult.CONTINUE;
	                    }
	                });
	            }catch (IOException e){
	                throw new RuntimeException("Error registering path " + p);
	            }
	        };

	        register.accept(rootFolder.toPath());
	
		}catch(IOException e){
			logger.error(e);
		}
	}
	
	public static WatchServiceSingleton getInstance(){
		if(INSTANCE == null){
			INSTANCE = new WatchServiceSingleton();
		}
		return INSTANCE;
	}
	
	public Consumer<Path> getRegister(){
		return register;
	}
	
	public Path getKey(WatchKey key){
		return keys.get(key);
	}
	
	public WatchService getWatchService(){
		return watchService;
	}
}
