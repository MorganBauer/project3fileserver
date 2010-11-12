package team3.src.message;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Joir-dan Gumbs
 * Abstraction of our Message Object. Defines clientID.
 */
@XmlRootElement(name="AbstractClientMessage")
@Message(Message.Type.UNKNOWN)
public abstract class AbstractClientMessage extends AbstractMessage  {

	
	@XmlAttribute(required=true)
	private String clientID;
	@XmlAttribute(required=true)
	private int priority;

	
	public String getID(){ return clientID; }
	public int getPriority(){ return priority; }
	

	protected AbstractClientMessage(String clientID, int priority){
		super();
		this.clientID = clientID;
		this.priority = (priority < 0)?0:priority;
		
	}
	protected AbstractClientMessage(){ }
}
