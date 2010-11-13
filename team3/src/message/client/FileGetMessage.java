package team3.src.message.client;

import team3.src.message.Message;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is an object representation of a file request message.
 * This message has an XML schema representation of: 
 * <br><br>
 * {@code <xs:complexType name="fileGetMessage" final="extension restriction">} <br>
 * &nbsp;{@code <xs:complexContent>} <br>
   &nbsp;&nbsp;{@code <xs:extension base="simpleMessage">}<br>
   &nbsp;&nbsp;&nbsp;{@code <xs:sequence>}<br>
   &nbsp;&nbsp;&nbsp;&nbsp;{@code <xs:element name="chunkNo" type="xs:int"/>}<br>
   &nbsp;&nbsp;&nbsp;&nbsp;{@code <xs:element name="chunkSize" type="xs:int"/>}<br>
   &nbsp;&nbsp;&nbsp;{@code </xs:sequence>}<br>
   &nbsp;&nbsp;&nbsp;{@code <xs:attribute name="isInitMessage" type="xs:boolean" use="required"/>}<br>
   &nbsp;&nbsp;{@code </xs:extension>}<br>
   &nbsp;{@code </xs:complexContent>}<br>
  {@code </xs:complexType>}
 * @author Joir-dan Gumbs
 * 
 */
@XmlRootElement(name = "FileGetMessage")
@Message(Message.Type.READ)
public final class FileGetMessage extends SimpleMessage {

	@XmlElement()
	private int chunkNo;
	@XmlElement()
	private int chunkSize;
	@XmlAttribute(required=true)
	private boolean isInitMessage;
	
	/**
	 * Check to see if this is an initialization message
	 * @return true if it is, false otherwise
	 */
	public boolean isInit(){
		return isInitMessage;
	}
	/**
	 * Gets the maximum chunk size desired by this client
	 * @return chunk size
	 */
	public int getChunkSize(){
		if(isInitMessage) throw new AssertionError("Init Message DOES NOT HAVE CHUNK SIZE");
		return chunkSize;
	}
	/**
	 * gets the current chunk number in the file the data string represents
	 * @return chunk number
	 */
	public int getChunkNo(){
		if(isInitMessage) throw new AssertionError("Init Message DOES NOT HAVE CHUNK NO");
		return chunkNo;
	}
	
	private FileGetMessage(String clientID, String filename, int priority, int chunkNo, int chunkSize, boolean isInitMessage) {
		super(clientID, filename, priority);
		this.chunkNo = chunkNo;
		this.chunkSize = chunkSize;
		this.isInitMessage = isInitMessage;
	}

	public String toString(){
		return (isInitMessage)? String.format("Request get from %s with Priority of %d for %s.", getID(), getPriority(), message):
					            String.format("Request data from %s with Priority of %d for %s: Chunk %d with size %d", getID(), getPriority(), message, chunkNo, chunkSize);
	}
	/**
	 * Create a new initialization message. This shouldn't be called anywhere but 
	 * through the message factory
	 * @param clientID who is sending this message
	 * @param filename the file being requested
	 * @param priority how important this transaction is
	 * @return a new {@link FileGetMessage initialization message}
	 */
	public static FileGetMessage buildInitMessage(String clientID, String filename, int priority){
		return new FileGetMessage(clientID, filename, priority, 0, 0, true);
	}
	/**
	 * Creates a pull request message. This shouldn't be called anywhere but
	 * through the message factory
	 * @param clientID who is sending this message
	 * @param filename the file being requested
	 * @param priority how important is this transaction
	 * @param chunkNo what chunk of the file we are requesting
	 * @param chunkSize the maximum chunk size this client is accepting
	 * @return a new {@link FileGetMessage pull request message}
	 */
	public static FileGetMessage buildPullMessage(String clientID, String filename, int priority, int chunkNo, int chunkSize){
		return new FileGetMessage(clientID, filename, priority, chunkNo, chunkSize, false);
	}
	
	private FileGetMessage(){ super(); }
}
