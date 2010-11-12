package team3.src.message;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * @author Joir-dan Gumbs
 * Denotes the simple message
 */

@XmlRootElement(name="SimpleMessage")
@MsgType(MsgType.IS.ATOMIC)
public class SimpleMessage extends AbstractClientMessage {
	
	/** The message delivered*/
	@XmlAttribute(required=true)
	protected String message;
	@XmlElement
	private int start;
	@XmlElement
	private int numFiles;
	
	
	
	public String getMessage(){ return message; }
	public int getStart(){ return start; }
	public int getNumFiles(){ return numFiles; }
	
	public static SimpleMessage buildDirListMessage(String clientID, int priority, int start, int numFiles){
		return new SimpleMessage(clientID, "Dir", priority, start, numFiles);
	}
	
	public static SimpleMessage buildTerminateMessage(String clientID){
		return new SimpleMessage(clientID, "Terminate");
	}
	
	public static SimpleMessage buildHelloMessage(String clientID){
		return new SimpleMessage(clientID, "Hello");
	}
	
	public String read() { return message; }
	
	public String toString(){
		return message+" from "+this.getID()+" with Priority of "+getPriority();
	}
	
	private SimpleMessage(String clientID, String message) {
		this(clientID, message, 0);
	}
	
	protected SimpleMessage(String clientID, String message, int priority){
		super(clientID, priority);
		this.message = message;
	}
	
	private SimpleMessage(String clientID, String message, int priority, int start, int numFiles){
		super(clientID, priority);
		this.message = message;
		this.start = start;
		this.numFiles = numFiles;
	}
	
	protected SimpleMessage(){}

	public static void main(String args[]) throws JAXBException{
		SimpleMessage msg2;
		SimpleMessage message = SimpleMessage.buildTerminateMessage("890.002");
		msg2 =  (SimpleMessage) unmarshal(message.marshal());
		System.out.println(msg2.toString());
	}
	
	
}
