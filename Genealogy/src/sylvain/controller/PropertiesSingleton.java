package sylvain.controller;

import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;

/**
 * 
 * @author Sylvain Mathiot
 *
 */
public class PropertiesSingleton {
	private static final Logger logger = LogManager.getLogger(LoggerSingleton.getInstance().getLoggerName(PropertiesSingleton.class));
	private static PropertiesSingleton INSTANCE;
	private Properties prop = null;
	
	/**
	 * Constructor
	 * 
	 * @param filename
	 */
	private PropertiesSingleton(String filename){
		Assert.assertNotNull(filename);
		
		try{
			InputStream in = this.getClass().getClassLoader().getResourceAsStream(filename);
			
			prop = new Properties();
			prop.load(in);
		}catch (Exception e){
			logger.error(e);
		}
		
		Assert.assertNotNull(this.prop);
		Assert.assertFalse(this.prop.isEmpty());
	}
	
	/**
	 * Get instance of singleton
	 * 
	 * @param filename
	 * @return Instance of singleton
	 */
	public static synchronized PropertiesSingleton getInstance(String filename){
		if(INSTANCE == null){
			INSTANCE = new PropertiesSingleton(filename);
		}
		return INSTANCE;
	}
	
	/**
	 * Get instance of singleton
	 * 
	 * @return Instance of singleton
	 */
	public static synchronized PropertiesSingleton getInstance(){
		if(INSTANCE == null){
			INSTANCE = new PropertiesSingleton(null);
		}
		return INSTANCE;
	}
	
	/**
	 * Returns the value of the given property 
	 * 
	 * @param key
	 * @return value of key
	 */
	public String get(String key){
		return prop.getProperty(key, "");
	}
}