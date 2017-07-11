package sylvain.controller;

import static com.sun.nio.file.SensitivityWatchEventModifier.HIGH;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 *
 * @author Sylvain Mathiot
 *
 */
public class WatchServiceSingleton {
  private static final Logger logger =
      LogManager.getLogger(
          LoggerSingleton.getInstance().getLoggerName(WatchServiceSingleton.class));
  private static WatchServiceSingleton INSTANCE;
  private WatchService watchService;
  private static final Kind<?>[] kinds = new Kind[] {ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE};
  private Map<WatchKey, Path> cache;

  private WatchServiceSingleton() {
    File rootFolder = new File(PropertiesSingleton.getInstance().get("DATABASE.FOLDER"));
    cache = new HashMap<WatchKey, Path>();

    if (rootFolder == null || !rootFolder.exists() || !rootFolder.isDirectory()) {
      return;
    }

    try {
      watchService = FileSystems.getDefault().newWatchService();
      Files.walk(rootFolder.toPath(), 1)
          .filter(f -> f.toFile().exists() && Files.isDirectory(f))
          .forEach(f -> register(f));
    } catch (IOException e) {
      logger.error(e);
    }
  }

  /**
   * Get instance of singleton.
   * 
   * @return Instance of singleton
   */
  public static WatchServiceSingleton getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new WatchServiceSingleton();
    }
    return INSTANCE;
  }

  public WatchService getWatchService() {
    return watchService;
  }

  /**
   * Register a path in the watch service.
   * 
   * @param path The path to register
   */
  public void register(Path path) {
    if (path == null || !path.toFile().exists() || !Files.isDirectory(path)) {
      return;
    }

    try {
      logger.debug("Registering folder : " + path);
      cache.put(path.register(watchService, kinds, HIGH), path);
    } catch (IOException e) {
      logger.error("Error while registering folder " + path);
    }
  }

  /**
   * Resolve the absolute path.
   * 
   * @param watchKey The key to retrieve from cache
   * @param event The watch event from witch to get the relative path
   * @return The resolved absolute path
   */
  public Path resolve(WatchKey watchKey, WatchEvent<?> event) {
    if (cache.containsKey(watchKey)) {
      return cache.get(watchKey).resolve((Path) event.context());
    } else {
      return (Path) event.context();
    }
  }
}
