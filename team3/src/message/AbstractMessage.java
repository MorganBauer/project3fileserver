package team3.src.message;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.GregorianCalendar;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import team3.src.message.client.AbstractClientMessage;
import team3.src.message.server.AbstractServerMessage;


@Message(Message.Type.UNKNOWN)
public abstract class AbstractMessage implements IMessage {

	static {
		try{ factory = DatatypeFactory.newInstance(); }
		catch(DatatypeConfigurationException e){  throw new AssertionError("Couldnt instantiate DatatypeFactory"); }
	}
	private static DatatypeFactory factory;
	private GregorianCalendar now = new GregorianCalendar();
	@XmlAttribute(required=true)
	private XMLGregorianCalendar datetime;
	
	/**
	 * Returns the timestamp associated with this message
	 * @return the XMLGregorianCalendar timestamp
	 */
	public XMLGregorianCalendar getDateTime(){ return datetime; }
	
	public String marshal() throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(this.getClass());
		StringWriter writer = new StringWriter();
		Marshaller m = jc.createMarshaller();
		m.marshal(this, writer);
		return writer.toString();
	}
	/**
	 * Figure out what type of message this is...
	 * @return message type
	 */
	public Message.Type getMsgType(){
		return this.getClass().getAnnotation(Message.class).value();
	}
	
	protected AbstractMessage(){
		this.datetime = factory.newXMLGregorianCalendar(now);
	}
	
	/**
     * Converts xml message into a message object 
     * @param xml - Message received from client (or other servers)
     * @return IMessage object
     * @throws JAXBException  if unable to convert
     */
    public static AbstractMessage unmarshal(String xml) throws JAXBException{
        ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        JAXBContext jc = JAXBContext.newInstance(
                String.format("%s:%s:%s",AbstractMessage.class.getPackage().getName(), AbstractClientMessage.class.getPackage().getName(), AbstractServerMessage.class.getPackage().getName()));
        Unmarshaller u = jc.createUnmarshaller();
        return (AbstractMessage) u.unmarshal(inputStream); 
    }
}
