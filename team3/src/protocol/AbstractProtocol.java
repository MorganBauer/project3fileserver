/**
 * 
 */
package team3.src.protocol;

import java.io.File;

import team3.src.util.Data2MsgUtil;
import static java.lang.System.getProperty;


/**
 * @author Joir-dan Gumbs
 *
 */
public abstract class AbstractProtocol {
	protected String id;
	protected final int KILOBYTE = 1024;
	protected final int DECODED_SIZE = 768;
	private String[] backupDir; 

	/**
	 * Tool to convert binary data to base64
	 */
	protected Data2MsgUtil dataToMsgUtil;
	
	/**
	 * Backs up the directory
	 */
	public void backupDirectory(){
		File file = new File(getProperty("user.dir"));
		backupDir = file.list();
	}
	
	/**
	 * Gets the directory before a write transaction occured
	 * @return backup directory as String array
	 */
	public String[] getBackupDir(){ return backupDir; }
	
	protected AbstractProtocol(String id){
		this.id = id;
	}
}
