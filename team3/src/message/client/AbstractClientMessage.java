package team3.src.message.client;

import java.io.ByteArrayInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import team3.src.message.AbstractMessage;
import team3.src.message.Message;

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
	
	/**
     * Converts xml message into a message object 
     * @param xml - Message received from client (or other servers)
     * @return IMessage object
     * @throws JAXBException  if unable to convert
     */
    public static AbstractClientMessage unmarshal(String xml) throws JAXBException{
        ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        JAXBContext jc = JAXBContext.newInstance(AbstractClientMessage.class.getPackage().getName());
        Unmarshaller u = jc.createUnmarshaller();
        return (AbstractClientMessage) u.unmarshal(inputStream); 
    }
}
