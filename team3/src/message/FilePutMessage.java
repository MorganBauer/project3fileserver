package team3.src.message;

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
	@XmlElement()
	private String data;
	@XmlElement()
	private int chunkNo;
	@XmlElement()
	private int chunkSize;
	@XmlElement()
	private boolean isLast;
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
		if(data == null) throw new AssertionError("You tried to call isLast on a message with no data!!!");
		return isLast;
	}
	
	public String read() {
		//Data will be null on request for file put.
		return (data== null)?filename:data;
	}
	
	public String toString(){
		return (data == null)?toStringRequest():toStringData();
	}
	
	private String toStringRequest(){
		return String.format("File Put from %s\nRequesting to upload: %s", getID(), filename);
	}
	private String toStringData(){
		return String.format("File Put from %s\nUploading chunk %d of %s\nSize of chunk is %d", getID(), chunkNo, filename, chunkSize);
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
	}
	
	private FilePutMessage(String clientID, String filename, int priority, String data, int chunkNo, int chunkSize, boolean isLast){
		super(clientID, priority);
		this.filename = filename;
		this.data = data;
		this.chunkNo = chunkNo;
		this.chunkSize = chunkSize;
		this.isLast = isLast;
	}
	private FilePutMessage(){}
	

}
