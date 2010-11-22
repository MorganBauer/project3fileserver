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

	protected String infile;
	protected String outfile;
	
	public static final String UNKNOWN_MSG = "0x001";
	public static final String INVALID_PARAMS = "0x002";
	public static final String CORRUPTED_MSG = "0x003";
	public static final String FILE_NOT_FOUND = "0x004";
	public static final String IO_ERROR = "0x005";
	public static final String DEL_ERROR = "0x006";
	
	
	
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
	
    /**
     * Check to see if a file exists
     * @param filename name of file
     * @return true if exists, false otherwise
     */
    protected boolean exists(String filename){
        File file = new File(filename);
        System.out.println(file.exists());
        return file.exists();
    }
	
	protected AbstractProtocol(String id){
		this.id = id;
	}
}
