package sylvain.thread.listener;

import java.util.EventListener;

import sylvain.thread.event.FolderScannedEvent;

/**
 * Scanning events listener.
 * 
 * @author Sylvain Mathiot
 */
public interface ScanningListener extends EventListener {
  void folderScanned(FolderScannedEvent e);
}
