package sylvain.controller;

import org.apache.logging.log4j.ThreadContext;

/*
 *
 * @author Sylvain Mathiot
 *
 */
public class LoggerSingleton {
  private static LoggerSingleton INSTANCE;

  /*
   * Constructor
   */
  private LoggerSingleton() {}

  /**
   * Get instance of singleton.
   * 
   * @return Instance of singleton
   */
  public static synchronized LoggerSingleton getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new LoggerSingleton();
    }
    return INSTANCE;
  }

  /*
   * Returns the logger name of a given class
   *
   * @param classObj : a class
   *
   * @return the logger name
   */
  public String getLoggerName(Class<?> classObj) {
    ThreadContext.put("threadName", Thread.currentThread().getName());
    return classObj.getSimpleName();
  }
}
