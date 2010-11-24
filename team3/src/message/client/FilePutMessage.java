package team3.src.message.client;

import team3.src.message.Message;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Message object for requesting pushing data to the server and
 * actually sending the data.
 * @author Joir-dan Gumbs
 *
 */
@XmlRootElement(name="FilePutMessage")
@Message(Message.Type.WRITE)
public class FilePutMessage extends AbstractClientMessage {
	@XmlAttribute(required=true)
	private String filename;
	@XmlAttribute(required=true)
	private boolean isInit;
	@XmlAttribute(required=true)
    private boolean isLast;
	@XmlElement()
	private String data;
	@XmlElement()
	private int chunkNo;
	@XmlElement()
	private int chunkSize;
	
	
	/**
	 * Gets the filename associated with this FilePut
	 * @return a filename string
	 */
	public String getFilename(){
	    return filename;
	}
	
	/**
	 * Check to see if this is an init message
	 * @return true if it is, false otherwise
	 */
	public boolean isInitMsg(){
	    return isInit;
	}
	/*
	 * For the following methods, these should only be called
	 * iff (THAT MEANS IF AND ONLY IF) we have previously accepted
	 * a file put transfer on the server, and are adding the file to
	 * the directory. I have enforced this with AssertionErrors, that will
	 * crash the program if called otherwise.
	 */
	
	/**
	 * Returns the chunk number. This should only be called if data exists
	 * within this message
	 * @return chunk number if data exists in message
	 */
	public int getChunkNo(){ 
		if(data == null) throw new AssertionError("You tried to call getChunkNo on a message with no data!!!");
		return chunkNo;
	}
	/**
	 * Returns the size of data chunk. This should only be called if data exists 
	 * within this message.
	 * @return size of the chunk of data if data exists in message
	 */
	public int getChunkSize(){
		if(data == null) throw new AssertionError("You tried to call getChunkSize on a message with no data!!!");
		return chunkSize;
	}
	/**
	 * Checks if this is the last data chunk message. This should only be called
	 * if data exists within this message.
	 * @return true if last chunk, false otherwise
	 */
	public boolean isLast(){
		return isLast;
	}
	/**
	 * ONLY SHOULD BE CALLED IF DATA MESSAGE
	 */
	public String read() {
		if(data == null) throw new AssertionError("TRIED TO READ DATA FROM INIT MSG!!");
		return data;
	}
	
	public String toString(){
		return (data == null)?toStringRequest():toStringData();
	}
	
	private String toStringRequest(){
		return String.format("File Put from %s\nRequesting to upload: %s", getID(), filename);
	}
	private String toStringData(){
		return String.format("File Put from %s\nUploading chunk %d of %s\nSize of chunk is %d. IS LAST = %b", getID(), chunkNo, filename, data.length(), isLast);
	}
	
	public static FilePutMessage buildFilePutRequestMessage(String clientID, String filename, int priority){
		return new FilePutMessage(clientID, filename, priority);
	}
	public static FilePutMessage buildFilePutDataMessage(String clientID, String filename, int priority, String data, int chunkNo, int chunkSize, boolean isLast){
		return new FilePutMessage(clientID, filename, priority, data, chunkNo, chunkSize, isLast);
	}
	
	private FilePutMessage(String clientID, String filename, int priority){
		super(clientID, priority);
		this.filename = filename;
		this.isInit = true;
		this.isLast = false;
	}
	
	private FilePutMessage(String clientID, String filename, int priority, String data, int chunkNo, int chunkSize, boolean isLast){
		super(clientID, priority);
		this.filename = filename;
		this.data = data;
		this.chunkNo = chunkNo;
		this.chunkSize = chunkSize;
		this.isLast = isLast;
		this.isInit = false;
	}
	private FilePutMessage(){}
	

}
