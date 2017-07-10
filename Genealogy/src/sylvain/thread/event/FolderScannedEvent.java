package sylvain.thread.event;

import java.io.File;

/**
 * 
 * @author Sylvain Mathiot
 *
 */
public class FolderScannedEvent implements ScanningEventInterface{
	private File folder;
	
	public FolderScannedEvent(File folder){
		this.folder = folder;
	}
	
	public String getFolderName(){
		return folder.getName();
	}
}
